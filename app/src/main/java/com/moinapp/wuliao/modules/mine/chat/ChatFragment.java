package com.moinapp.wuliao.modules.mine.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.api.OperationResponseHandler;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.base.BaseListFragment;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.CommentList;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.ErrorCode;
import com.moinapp.wuliao.bean.Result;
import com.moinapp.wuliao.bean.ResultBean;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.cache.CacheManager;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.emoji.Emojicon;
import com.moinapp.wuliao.emoji.MoinEmojiFragment;
import com.moinapp.wuliao.emoji.OnEmojiClickListener;
import com.moinapp.wuliao.emoji.OnSendClickListener;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.mine.model.ChatMessage;
import com.moinapp.wuliao.modules.mine.model.SendChatMessageResult;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.DialogHelp;
import com.moinapp.wuliao.util.HTMLUtil;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.InjectView;

/**
 * 与某人的聊天记录界面 ChatFragment
 *
 * @author caozz 16/4/13
 */
public class ChatFragment extends BaseListFragment<ChatMessage> implements
        OnItemLongClickListener, OnSendClickListener, OnEmojiClickListener, ChatMessageDetailAdapter.OnRetrySendMessageListener {
    private static final ILogger MyLog = LoggerFactory.getLogger(ChatFragment.class.getSimpleName());
    private static final String CACHE_KEY_PREFIX = "chat_detail_list";

    //时间间隔（要求：聊天时间显示，时间间隔为五分钟以上才显示出来）
    private final static long TIME_INTERVAL = DateUtils.MINUTE_IN_MILLIS * 5;

    @InjectView(R.id.title_layout)
    protected CommonTitleBar title;

    private String mUid;
    private String mFName;
    private int mMsgTag;
    private int mPageCount;
    private long mLastShowDate; //最后显示出来的时间
    // 变量名 emojiFragment 不要变,否则就提供一个getEmojiFragment方法
    public MoinEmojiFragment emojiFragment = new MoinEmojiFragment();

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mErrorLayout != null) {
                mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                //mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mUid = args.getString(Constants.BUNDLE_KEY_UID);
            mFName = args.getString(Constants.BUNDLE_KEY_USERNAME);
            mCatalog = CommentList.CATALOG_MESSAGE;
        }
        MyLog.i("chat with [" + mUid + "/" + mFName + "]");
        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_LOGOUT);
        getActivity().registerReceiver(mReceiver, filter);

        //event bus registry
        EventBus.getDefault().register(this);
        MineManager.getInstance().close();
        MineManager.getInstance().addActivity(getActivity());

        ((BaseActivity) getActivity()).setActionBarTitle(mFName);

        int mode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        getActivity().getWindow().setSoftInputMode(mode);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.emoji_container, emojiFragment).commit();

        if (emojiFragment.getEmjlistener() == null) {
            emojiFragment.setOnEmojiClickListener(new OnEmojiClickListener() {
                @Override
                public void onDeleteButtonClick(View v) {
                    MyLog.i("MoinEmoji 你点击了删除按钮");
//                    AppContext.getInstance().toast(getActivity(), "你点击了删除按钮");
                }

                @Override
                public void onEmojiClick(Emojicon v) {
                    MyLog.i("MoinEmoji 你点击了表情按钮:" + v.toString());
//                    AppContext.getInstance().toast(getActivity(), "你点击了表情按钮:" + v.toString());
                    prepareSendMessage(3, v.getRemote());
                }
            });
        }
        if (emojiFragment.getListener() == null) {
            emojiFragment.setListener(new OnSendClickListener() {
                @Override
                public void onClickSendButton(String str) {
                    prepareSendMessage(1, str.toString());
                }

                @Override
                public void onClickFlagButton() {

                }
            });
        }
    }

    /**
     * 准备发送单条聊天记录
     *
     * @param type 发送类型 1:文本 2:图片 3:预置图片
     */
    private void prepareSendMessage(int type, String msg) {
        // 收起键盘
//        emojiFragment.hideAllKeyBoard();

        // TODO 发消息接口
        MyLog.i("MoinEmoji 你要发送的是:[" + msg + "]");
        if (StringUtils.isEmpty(msg)) {
            if (type == 1) {
                AppContext.showToastShort(R.string.tip_content_empty);
            }
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }

        ChatMessage newMsg = new ChatMessage();
        newMsg.setChatUser((UserInfo) CacheManager.readObject(getActivity(), mUid));
        MyLog.i(type + "你要给谁发消息:" + ((UserInfo) CacheManager.readObject(getActivity(), mUid)).toString());
        newMsg.setContent(msg);
        newMsg.setContentType(type);
        newMsg.setLocalTime(System.currentTimeMillis());
        newMsg.setServerTime(System.currentTimeMillis());
        newMsg.setLoginUid(ClientInfo.getUID());
        newMsg.setMessageId(System.currentTimeMillis() + "");
        newMsg.setReadStatus(1);
        newMsg.setSendStatus(ChatMessage.SendStatus.SENDING);
        //如果此次发表的时间距离上次的时间达到了 TIME_INTERVAL 的间隔要求，则显示时间
        if (isNeedShowDate(System.currentTimeMillis(), mLastShowDate)) {
            newMsg.setShowDate(true);
            mLastShowDate = System.currentTimeMillis();
        }
        newMsg.setType(1);

        updateListView(newMsg);

        MineManager.getInstance().save2ChatTable(newMsg);

        readySend(newMsg);
    }

    private void readySend(final ChatMessage newMsg) {
        if (newMsg == null) {
            return;
        }
        // 发送文本
        if (newMsg.getContentType() == 1) {
            sendContent(newMsg, newMsg.getContent(), null);
        } else if (newMsg.getContentType() == 3) {
            // 发送预制图片
            sendContent(newMsg, null, newMsg.getContent());
        } else if (newMsg.getContentType() == 2) {
            // 发送图片
            MineManager.getInstance().uploadChatImage2Oss(newMsg.getContent(), new IListener() {
                @Override
                public void onSuccess(Object url) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendContent(newMsg, null, (String) url);
                        }
                    });
                }

                @Override
                public void onErr(Object obj) {
//                    AppContext.showToastShort(getString(R.string.tip_message_public_faile) + "[" + obj + "]");
                    onSendFinish(false, newMsg, null);
                }

                @Override
                public void onNoNetwork() {
                    AppContext.showToastShort(R.string.no_network);
                    onSendFinish(false, newMsg, null);
                }
            });

        }
    }

    /**
     * 真正的发送content
     *
     * @param newMsg
     * @param msg    type=1:文本
     * @param url    type=2:url
     */
    private void sendContent(ChatMessage newMsg, String msg, String url) {
        // TODO 此处是发给mUid的
        MyLog.i("sendContent 真正的发送content:" + newMsg.toString());
//        showWaitDialog("提交中...");

        if (newMsg.getContentType() == 1) {
            emojiFragment.clean();
        }

        MineManager.getInstance().sendChatMessage(mUid, msg, url, newMsg.getLocalTime(), new IListener() {

            @Override
            public void onSuccess(Object obj) {
                hideWaitDialog();
                try {
//                    AppContext.showToastShort(R.string.tip_message_public_success);
                    onSendFinish(true, newMsg, obj);
                    MyLog.i("sendContent SUCC :" + newMsg.toString());

                } catch (Exception e) {
                    MyLog.e(e);
                    onErr(null);
                }
            }

            @Override
            public void onErr(Object obj) {
                MyLog.i("sendContent onErr :" + newMsg.toString());
                MyLog.i("sendContent onErr :" + obj);
                hideWaitDialog();
//                AppContext.showToastShort(getString(R.string.tip_message_public_faile) + "[" + obj + "]");
                onSendFinish(false, newMsg, null);
                if (obj != null) {
                    if ((int) obj == ErrorCode.ERROR_LOW_VERSION) {
                        AppContext.showToastShort(R.string.chat_low_version);
                    }
                }
            }

            @Override
            public void onNoNetwork() {
                MyLog.i("sendContent onNoNetwork :" + newMsg.toString());
                hideWaitDialog();
                AppContext.showToastShort(R.string.no_network);
                onSendFinish(false, newMsg, null);
            }
        });
    }

    /**
     * 发送完成处理
     */
    private void onSendFinish(boolean succ, ChatMessage newMsg, Object obj) {
        if (newMsg == null) {
            return;
        }
        SendChatMessageResult result = null;
        if (obj != null) {
            result = (SendChatMessageResult) obj;
        }

        String oldMsgId = newMsg.getMessageId();
        newMsg.setSendStatus(succ ? ChatMessage.SendStatus.NORMAL : ChatMessage.SendStatus.ERROR);
        if (succ && result != null && !StringUtil.isNullOrEmpty(result.getMid())) {
            newMsg.setMessageId(result.getMid());
        }
        newMsg.setServerTime(result != null ? result.getCreatedAt() : System.currentTimeMillis());
        MineManager.getInstance().updateSentMessage(oldMsgId, newMsg);


        MyLog.i("onSendFinish1 :" + newMsg.toString());
        for (int i = mAdapter.getDataSize() - 1; i >= 0; i--) {
            ChatMessage listItem = mAdapter.getItem(i);
            MyLog.i(i + ", adapter :" + listItem);
            if (listItem != null && oldMsgId.equals(listItem.getMessageId())) {
                MyLog.i("onSendFinish2 :" + newMsg.toString());
                listItem.setSendStatus(newMsg.getSendStatus());
                listItem.setMessageId(newMsg.getMessageId());
                listItem.setServerTime(newMsg.getServerTime());
                break;
            }
        }
        MyLog.i("onSendFinish9 :" + newMsg.toString());
        updateListView(null);
    }

    /**
     * 刷新聊天详情内容界面
     */
    private void updateListView(ChatMessage chatMessage) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (chatMessage == null) {
                    // 本地发送出的消息有回调/删除消息后的刷新
                    mAdapter.notifyDataSetChanged();
                } else {
                    // 本地发送或收到的消息 scrollToBottom
                    if (mAdapter.getDataSize() == 0) {
                        // 如果是首次发消息,那么将空页面隐藏
                        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    }
                    mAdapter.addItem(0, chatMessage);
                }
                mAdapter.notifyDataSetChanged();
                scrollToBottom();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        EventBus.getDefault().unregist(this);
        MineManager.getInstance().removeActivity(getActivity());
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed() {
        if (emojiFragment.isShowEmojiKeyBoard()) {
            emojiFragment.hideAllKeyBoard();
            return true;
        } else {
            return super.onBackPressed();
        }
    }

    @Override
    protected ChatMessageDetailAdapter getListAdapter() {
        ChatMessageDetailAdapter adapter = new ChatMessageDetailAdapter();
        adapter.setOnRetrySendMessageListener(this);
        return adapter;
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + mUid;
    }

    @Override
    public void initView(View view) {
        super.initView(view);

        title.setTitleTxt(StringUtil.isNullOrEmpty(mFName) ? "聊天" : mFName);
        title.setRightBtnIcon(R.drawable.dynamics_chat);
        title.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        title.setRightBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 进入用户动态
                if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(mUid)) {
                    // 登录用户点击自己头像
                    UIHelper.showMine(getActivity(), 0);
                } else {
                    UIHelper.showUserCenter(getActivity(), mUid);
                }
//                getActivity().finish();
            }
        });
        title.setTitleOnclickListener(v -> {
            scrollToTop();
        });


        mListView.setDivider(null);
        mListView.setDividerHeight(0);
        mListView.setOnItemLongClickListener(this);
        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getInstance().isLogin()) {
                    requestData(false);
                } else {
                    UIHelper.showLoginActivity(getActivity());
                }
            }
        });
    }

    @Override
    protected void requestData(boolean refresh) {
        mErrorLayout.setErrorMessage("");
        if (AppContext.getInstance().isLogin()) {
            super.requestData(refresh);
        } else {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            //mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
        }
    }

    @Override
    protected void sendRequestData() {
        // TODO 获取聊天内容
        long lastChatTimestamp = System.currentTimeMillis();
        if (mAdapter != null && mAdapter.getDataSize() > 0) {
            // 获取最久的一条聊天记录的服务器时间戳
            mCurrentPage++;
            lastChatTimestamp = mAdapter.getItem(0).getServerTime();
        }
        List<ChatMessage> data = MineManager.getInstance().getChatMessages(mUid, lastChatTimestamp, getPageSize());
        MyLog.i(lastChatTimestamp + ", data from DB size = " + (data == null ? "null" : data.size()));
        boolean isLast = data == null ? true : data.size() < getPageSize();
        executeOnLoadDataSuccess(data, isLast);
        executeOnLoadFinish();
    }

    // 下拉刷新数据,其实这里是分页加载
    @Override
    public void onRefresh() {
        if (mState == STATE_REFRESH) {
            return;
        }
        // 设置顶部正在刷新
        mListView.setSelection(0);
        setSwipeRefreshLoadingState();
        mState = STATE_REFRESH;
        requestData(false);
    }

    @Override
    protected boolean isReadCacheData(boolean refresh) {
        return false;
    }

    /**
     * TODO 获取聊天信息成功
     */
    @Override
    protected void executeOnLoadDataSuccess(List<ChatMessage> data, boolean isLast) {
        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        if (data == null) {
            data = new ArrayList<>();
        } else {
            handleShowDate(data);
        }
        if (mAdapter != null) {
            if (mCurrentPage == 0) {
                mAdapter.clear();
            }

            // 去除重复数据
            for (int i = 0; i < data.size(); i++) {
                if (compareTo(mAdapter.getData(), data.get(i))) {
                    data.remove(i);
                    i--;
                }
            }

            int adapterState = ListBaseAdapter.STATE_EMPTY_ITEM;
            if (isLast) {
                if ((mAdapter.getDataSize() + data.size()) == 0) {
                    adapterState = ListBaseAdapter.STATE_EMPTY_ITEM;
                } else {
                    adapterState = ListBaseAdapter.STATE_NO_MORE;
                    mAdapter.notifyDataSetChanged();
                }
            } else {
                adapterState = ListBaseAdapter.STATE_LOAD_MORE;
            }
            mAdapter.setState(adapterState);
            mAdapter.addData(data);
            // 判断等于是因为最后有一项是listview的状态
            if (mAdapter.getDataSize() == 0) {

                if (needShowEmptyNoData()) {
                    mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                } else {
                    mAdapter.setState(ListBaseAdapter.STATE_EMPTY_ITEM);
                    mAdapter.notifyDataSetChanged();
                }
            }
            mListView.setSelection(mListView.getBottom());
        }
    }

    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null && ((ChatMessage) enity).getMessageId() != null) {
            for (int i = 0; i < s; i++) {
                if (((ChatMessage) enity).getMessageId().equalsIgnoreCase((((ChatMessage) data.get(i)).getMessageId()))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 处理时间显示，设置哪些需要显示时间，哪些不需要显示时间
     *
     * @param list
     */
    private void handleShowDate(List<ChatMessage> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        ChatMessage msg = null;
        long lastGroupTime = 0l;
        //因为获得的列表是按时间降序的，所以需要倒着遍历
        for (int i = list.size() - 1; i >= 0; i--) {
            msg = list.get(i);
            Date date = StringUtils.toDate(msg.getServerTime());
            if (date != null && isNeedShowDate(date.getTime(), lastGroupTime)) {
                lastGroupTime = date.getTime();
                msg.setShowDate(true);
            }
        }
        //只设置最新的时间
        if (lastGroupTime > mLastShowDate) {
            mLastShowDate = lastGroupTime;
        }
    }

    private boolean isNeedShowDate(long currentTime, long lastTime) {
        return currentTime - lastTime > TIME_INTERVAL;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        final ChatMessage message = mAdapter.getItem(mAdapter.getDataSize() - position - 1);
        DialogHelp.getSelectDialog(getActivity(),
                getResources().getStringArray(R.array.message_list_options), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                // TODO 复制聊天内容
                                TDevice.copyTextToBoard(HTMLUtil.delHTMLTag(message
                                        .getContent()));
                                break;
                            case 1:
                                // TODO 删除单条聊天记录
                                handleDeleteMessage(message);
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.CHAT_FRAGMENT); //统计页面，
        DiscoveryManager.getInstance().setCommentFlag(DiscoveryManager.COMMENT_FROM_CHAT);
        emojiFragment.hideFlagButton();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.CHAT_FRAGMENT);
    }


    // TODO 删除单条聊天记录
    private void handleDeleteMessage(final ChatMessage message) {
//        String authorId = null;
//        if (message.getType() == 1) {
//            authorId = message.getLoginUid();
//        } else if (message.getType() == 2 && message.getChatUser() != null) {
//            authorId = message.getChatUser().getUId();
//        }
//        final String authorID = authorId;
        DialogHelp.getConfirmDialog(getActivity(), "是否删除该消息?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                showWaitDialog(R.string.progress_submit);
                MyLog.i("删除 1");
                mAdapter.removeItem(message);
                MyLog.i("删除 2");
                updateListView(null);
                MyLog.i("删除 3");
                MineManager.getInstance().deleteChatMessage(mUid, message.getMessageId());
//                OSChinaApi.deleteComment(mUid.hashCode(),
//                        CommentList.CATALOG_MESSAGE, message.getId(),
//                        authorID.hashCode(),
//                        new DeleteMessageOperationHandler(message));
            }
        }).show();
    }

    @Override
    public void onRetrySendMessage(String msgId) {
        // 根据这个msgId.从DB去获取这条消息,然后重新发送
        ChatMessage retryMsg = MineManager.getInstance().getChatMessage(mUid, msgId);
        MyLog.i("准备重发 " + msgId + ", " + retryMsg);
        readySend(retryMsg);
    }

    class DeleteMessageOperationHandler extends OperationResponseHandler {

        public DeleteMessageOperationHandler(Object... args) {
            super(args);
        }

        @Override
        public void onSuccess(int code, ByteArrayInputStream is, Object[] args)
                throws Exception {
            Result res = XmlUtils.toBean(ResultBean.class, is).getResult();
            if (res.OK()) {
                ChatMessage msg = (ChatMessage) args[0];
                mAdapter.removeItem(msg);
                mAdapter.notifyDataSetChanged();
                hideWaitDialog();
                AppContext.showToastShort(R.string.tip_delete_success);
            } else {
                AppContext.showToastShort(res.getErrorMessage());
                hideWaitDialog();
            }
        }

        @Override
        public void onFailure(int code, String errorMessage, Object[] args) {
            AppContext.showToastShort(R.string.tip_delete_faile);
            hideWaitDialog();
        }
    }

    @Override
    public void onClickSendButton(String str) {
        prepareSendMessage(1, str.toString());
    }

    @Override
    public void onClickFlagButton() {
    }

    @Override
    public void onDeleteButtonClick(View v) {
    }

    @Override
    public void onEmojiClick(Emojicon v) {
        android.util.Log.i(ChatFragment.class.getSimpleName(),
                "ChatFragment.onEmojiClick  MoinEmoji listener.onEmojiClick " + v.toString());
    }

    @Override
    public void scrollToBottom() {
//        mListView.smoothScrollToPosition(mAdapter.getDataSize());
        mListView.setSelection(mAdapter.getDataSize());
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // 当滑动时如果有键盘,收起来
//        TDevice.hideSoftKeyboard(view);
        emojiFragment.hideAllKeyBoard();
    }

    // 完成刷新
    @Override
    protected void executeOnLoadFinish() {
        setSwipeRefreshLoadedState();
        mState = STATE_NONE;
        if (mCurrentPage > 0) {
            scrollToTop();
        } else {
            scrollToBottom();
        }
    }

    // 新的聊天消息来了
    public void onEvent(MineManager.NewChatMessageEvent newChatMessage) {
        MyLog.i("收到新消息1.....," + newChatMessage);
        if (newChatMessage == null) return;
        MyLog.i("收到新消息2.....," + newChatMessage.getChatMessage());
        ChatMessage chatMessage = newChatMessage.getChatMessage();
        // 不是本人的消息 TODO 如果message中添加了targetUID,则此处需要判断
//        if (!ClientInfo.getUID().equals(chatMessage.getLoginUid())) {
//            return;
//        }
        // 是否是当前聊天对象的消息
        if (chatMessage.getChatUser() != null && !mUid.equals(chatMessage.getChatUser().getUId())) {
            return;
        }

        // 此处不做servertime排序,否则会给用户困扰,乱了. 下次进页面才会排序
        updateListView(chatMessage);

        MyLog.i(mUid + ", 收到新消息.....end add to position0 ");
    }

    //
    public void onEvent(MineManager.SelectPhotoEvent event) {
        if (event == null || StringUtil.isNullOrEmpty(event.getImagePath())) return;
        if (DiscoveryManager.getInstance().getCommentFlag()
                != DiscoveryManager.COMMENT_FROM_CHAT) return;
        String path = event.getImagePath();
        MyLog.i("收到选择照片事件.....path=" + path);
        //todo 上传图片
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                prepareSendMessage(2, path);
            }
        });
    }
}
