package com.moinapp.wuliao.modules.discovery.ui;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.moinapp.wuliao.AppConfig;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseListFragment;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.BaseImage;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.emoji.Emojicon;
import com.moinapp.wuliao.emoji.MoinEmojiFragment;
import com.moinapp.wuliao.emoji.OnEmojiClickListener;
import com.moinapp.wuliao.emoji.OnSendClickListener;
import com.moinapp.wuliao.fragment.FriendsFragment;
import com.moinapp.wuliao.interf.OnTabReselectListener;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.listener.IListener2;
import com.moinapp.wuliao.modules.discovery.DiscoveryApi;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.discovery.adapter.CommentCosplayAdapter;
import com.moinapp.wuliao.modules.discovery.adapter.FollowAdapter;
import com.moinapp.wuliao.modules.discovery.model.CommentInfo;
import com.moinapp.wuliao.modules.discovery.model.CommentList;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.umeng.analytics.MobclickAgent;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 大咖秀图片的评论列表
 * @author liujiancheng
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CommentListFragment extends BaseListFragment<CommentInfo>
        implements OnTabReselectListener {
    private static ILogger MyLog = LoggerFactory.getLogger(CommentListFragment.class.getSimpleName());

    protected static final String TAG = FriendsFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "comment_cosplay_list";

    private String mUcid;
    public MoinEmojiFragment emojiFragment = new MoinEmojiFragment();
    private Handler mCallbackHandler = new Handler();

    @Override
    protected void addListHeader() {
        if (mListView.getHeaderViewsCount() == 0) {
            View headerView = View.inflate(getActivity(), R.layout.layout_title_down_grey_area, null);
            mListView.addHeaderView(headerView);
        }
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        mErrorLayout.hideTitleDownArea();

        int mode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        getActivity().getWindow().setSoftInputMode(mode);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.emoji_keyboard, emojiFragment).commit();

        if (emojiFragment.getEmjlistener() == null) {
            emojiFragment.setOnEmojiClickListener(new OnEmojiClickListener() {
                @Override
                public void onDeleteButtonClick(View v) {
                }

                @Override
                public void onEmojiClick(Emojicon v) {
                    sendComment(3, v.getRemote());
                }

            });
        }
        if (emojiFragment.getListener() == null) {
            emojiFragment.setListener(new OnSendClickListener() {
                @Override
                public void onClickSendButton(String str) {
                    sendComment(1, str);
                }

                @Override
                public void onClickFlagButton() {

                }
            });
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_comment_list;
    }

    @Override
    public void onTabReselect() {
        scrollToTop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mUcid = args.getString(Constants.BUNDLE_KEY_ID);
        }

        IntentFilter filter = new IntentFilter(
                Constants.INTENT_ACTION_USER_CHANGE);
        getActivity().registerReceiver(mReceiver, filter);

        //event bus registry
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        DiscoveryManager.getInstance().setCommentFlag(DiscoveryManager.COMMENT_FROM_COMMENT_LIST);
    }

    private UserInfo mReplyUser;
    CommentCosplayAdapter adapter;

    @Override
    protected CommentCosplayAdapter getListAdapter() {
        if (adapter == null) {
            adapter = new CommentCosplayAdapter(getActivity());
            adapter.setCommentCallback(new FollowAdapter.CommentCallback() {
                @Override
                public void onComment(int position, UserInfo replyUser) {
                    if(replyUser == null) {
                        return;
                    }
                    mReplyUser = replyUser;

                    mCallbackHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            emojiFragment.setHint("回复" + replyUser.getUsername());
                            emojiFragment.showSoftKeyboard();
                        }
                    }, 200);
                }
            });
        }
        return adapter;
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + mUcid;
    }

    @Override
    protected CommentList parseList(InputStream is) throws Exception {
        CommentList result = XmlUtils.JsontoBean(CommentList.class, is);
        if (result == null) {
            return null;
        }
        return result;
    }

    @Override
    protected CommentList readList(Serializable seri) {
        return ((CommentList) seri);
    }

    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null && ((CommentInfo) enity).getCid() != null) {
            for (int i = 0; i < s; i++) {
                if (((CommentInfo) enity).getCid().equalsIgnoreCase(((CommentInfo) data.get(i)).getCid())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void requestData(boolean refresh) {
        if (refresh) {
            DiscoveryApi.getCommentList(mUcid, null, mHandler);
        } else {
            sendRequestData();
        }
    }

    @Override
    protected void sendRequestData() {
        String lastid = null;
        if (mCurrentPage != 0 && mAdapter.getCount() > 0) {
            lastid = mAdapter.getItem(mAdapter.getData().size() - 1) != null ? mAdapter.getItem(mAdapter.getData().size() - 1).getCid() : null;
        }
        DiscoveryApi.getCommentList(mUcid, lastid, mHandler);
    }

    @Override
    protected void onRefreshNetworkSuccess() {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
        EventBus.getDefault().unregist(this);
    }

    @Override
    public boolean onBackPressed() {
        try {
            if (emojiFragment.isShowEmojiKeyBoard()) {
                emojiFragment.hideAllKeyBoard();
                return true;
            }
            if (emojiFragment.getEditText().getTag() != null) {
                emojiFragment.getEditText().setTag(null);
                emojiFragment.getEditText().setHint(getString(R.string.comment_hint));
                return true;
            }
        } catch (NullPointerException e) {
        }
        return super.onBackPressed();
    }

    /**
     * 发布评论
     *
     * @param type 发送类型 1:文本 2:图片 3:预置图片
     */
    private void sendComment(int type, final String content) {
        MyLog.i("MoinEmoji 你要发送的是:[" + content + "]");
        if (StringUtils.isBlank(content)) {
            if (type == 1) {
                AppContext.showToastShort(R.string.tip_comment_content_empty);
            }
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
        if (!isNetOKetc()) {
            return;
        }

        // 发送文本
        if (type == 1) {
            sendContent(content, null);
        } else if (type == 3) {
            // 发送预制图片
            sendContent(null, content);
        } else if (type == 2) {
            // 发送图片
            MineManager.getInstance().uploadChatImage2Oss(content, new IListener() {
                @Override
                public void onSuccess(Object url) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendContent(null, (String) url);
                        }
                    });
                }

                @Override
                public void onErr(Object obj) {
//                    AppContext.showToastShort(getString(R.string.tip_message_public_faile) + "[" + obj + "]");
                }

                @Override
                public void onNoNetwork() {
                    AppContext.showToastShort(R.string.no_network);
                }
            });

        }
    }

    /**
     * 真正的发布评论
     *
     * @param content type=1:文本
     * @param url     type=2:url
     */
    private void sendContent(String content, String url) {
        MyLog.i("sendContent 真正的发送content:" + content + ", url=" + url);
        UserInfo userInfo = AppContext.getInstance().getUserInfo();

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(UmengConstants.ITEM_ID, mUcid + "_评论列表_" + userInfo.getUsername());
//        map.put(UmengConstants.FROM, "图片评论列表页");
        MobclickAgent.onEvent(getActivity(), UmengConstants.COMMENT_COSPLAY, map);
        MobclickAgent.onEvent(getActivity(), UmengConstants.COMMENT, map);

        if (!StringUtil.isNullOrEmpty(content)) {
            emojiFragment.clean();
            emojiFragment.setHint(getString(R.string.comment_hint));
        }
        // 此处url是相对路径[/image/moon-image/***.jpg]
        DiscoveryManager.getInstance().commentCosplay(mUcid, null, content,
                mReplyUser == null ? null : mReplyUser.getUId(), url, new IListener2() {
                    @Override
                    public void onSuccess(Object obj) {
                        if (mAdapter.getData().isEmpty()) {
                            onRefresh();
                            return;
                        }
                        final String cid = (String)obj;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CommentInfo comment = new CommentInfo();

                                userInfo.setRelation(4);
                                comment.setAuthor(userInfo);
                                comment.setCid(cid);
                                comment.setContent(content);
                                BaseImage picture = null;
                                if (!TextUtils.isEmpty(url)) {
                                    picture = new BaseImage();
                                    String newUrl = url;
                                    if (!url.startsWith("http") && url.startsWith("/")) {
                                        newUrl = AppConfig.getBaseImageUrl() + url;
                                    }
                                    picture.setUri(newUrl);
                                    comment.setPicture(picture);
                                }
                                comment.setCreatedAt(System.currentTimeMillis());
                                comment.setReply(mReplyUser);
                                mAdapter.addItem(0, comment);
                                mAdapter.notifyDataSetInvalidated();
                                mReplyUser = null;
                            }
                        });
                    }

                    @Override
                    public void onErr(Object obj) {
                        AppContext.showToast(R.string.comment_publish_faile);
                        mReplyUser = null;
                    }

                    @Override
                    public void onNoNetwork() {
                        mReplyUser = null;
                    }
                });
    }

    private boolean isNetOKetc() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_network_error);
            return false;
        }
        if (!AppContext.getInstance().isLogin()) {
            AppContext.showToast("先登录再评论~");
            UIHelper.showLoginActivity(getActivity());
            return false;
        }
        return true;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.INTENT_ACTION_USER_CHANGE)) {
                requestData(true);
            }
        }
    };

    @Override
    protected void executeOnLoadDataSuccess(List<CommentInfo> data, boolean isLast) {
        super.executeOnLoadDataSuccess(data, isLast);
        if (mAdapter.getCount() == 1) {

            if (needShowEmptyNoData()) {
                mErrorLayout.setErrorType(EmptyLayout.NODATA);
            } else {
                mAdapter.setState(ListBaseAdapter.STATE_EMPTY_ITEM);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private int mEmojiFragmentHeight;
    private boolean mEmojiVisible = true;
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // 当滑动时如果有键盘,收起来
//        TDevice.hideSoftKeyboard(view);
        emojiFragment.hideAllKeyBoard();
        boolean visible = scrollState == SCROLL_STATE_IDLE;
        if (mEmojiVisible != visible) {
            mEmojiVisible = visible;

            if (!visible && mEmojiFragmentHeight == 0) {
                mEmojiFragmentHeight = getResources().getDimensionPixelSize(R.dimen.pic_comment_fg_height) + 3;
            }

            ViewPropertyAnimator.animate(emojiFragment.getRootView()).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(200L).translationY(visible ? 0 : (float) mEmojiFragmentHeight);
        }
    }

    public void onEvent(MineManager.SelectPhotoEvent event) {
        if (event == null || StringUtil.isNullOrEmpty(event.getImagePath())) return;
        if (DiscoveryManager.getInstance().getCommentFlag()
                != DiscoveryManager.COMMENT_FROM_COMMENT_LIST) return;
        String path = event.getImagePath();
        MyLog.i("收到选择照片事件.....path=" + path);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sendComment(2, path);
            }
        });
    }
}
