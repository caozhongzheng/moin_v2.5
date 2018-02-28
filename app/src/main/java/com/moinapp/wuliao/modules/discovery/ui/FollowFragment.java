package com.moinapp.wuliao.modules.discovery.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.base.BaseListFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.MultiClickEvent;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.interf.OnTabReselectListener;
import com.moinapp.wuliao.listener.IListener2;
import com.moinapp.wuliao.modules.discovery.DiscoveryApi;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.discovery.adapter.FollowAdapter;
import com.moinapp.wuliao.modules.discovery.model.CommentInfo;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.FollowCosplayList;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.modules.stickercamera.app.camera.ui.PhotoReleaseActivity;
import com.moinapp.wuliao.ui.CommentDialogFragment;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.MainActivity;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.InjectView;

/**
 * 关注列表
 */
public class FollowFragment extends BaseListFragment<CosplayInfo> implements OnTabReselectListener {
    private static final ILogger MyLog = LoggerFactory.getLogger(FollowFragment.class.getSimpleName());

    protected static final String TAG = FollowFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "followlist_";
    private static String RECEIVE_MSG = "";

    @InjectView(R.id.title_layout)
    protected CommonTitleBar title;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            RECEIVE_MSG = intent.getAction();
            if (RECEIVE_MSG == Constants.INTENT_ACTION_USER_CHANGE || RECEIVE_MSG == Constants.INTENT_ACTION_LOGOUT) {
                mErrorLayout.hideNoLogin();
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                requestData(true);
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.follow_fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mCatalog > 0) {
            IntentFilter filter = new IntentFilter(
                    Constants.INTENT_ACTION_USER_CHANGE);
            filter.addAction(Constants.INTENT_ACTION_LOGOUT);
            getActivity().registerReceiver(mReceiver, filter);
        }
    }

    @Override
    public void onDestroy() {
        if (mCatalog > 0 && mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
        EventBus.getDefault().unregist(this);
        super.onDestroy();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.FOLLOW_FRAGMENT); //统计页面
        if (!StringUtil.isNullOrEmpty(RECEIVE_MSG)) {
            RECEIVE_MSG = "";
            setupContent();
        }
    }

    private void setupContent() {
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        mCurrentPage = 0;
        onRefresh();
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.FOLLOW_FRAGMENT);
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        EventBus.getDefault().register(this);

        title.setTitleTxt("关注");
        title.hideLeftBtn();
        title.hideRightBtn();

        requestData(true);
//        if (AppContext.getInstance().isLogin()) {
//            mErrorLayout.hideNoLogin();
//            mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
//            requestData(true);
        mErrorLayout.setBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到关注
                UIHelper.gotoMain(getActivity(), MainActivity.KEY_TAB_FOLLOW, false);
            }
        });

        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestData(true);
            }
        });
//        } else {
//            mErrorLayout.setErrorType(EmptyLayout.NO_LOGIN);
//            //mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
//            mErrorLayout.setOnLayoutClickListener(v -> {
//                if (AppContext.getInstance().isLogin()) {
//                    mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
//                    requestData(true);
//                } else {
//                    UIHelper.showLoginActivity(getActivity(), 0);
//                }
//            });
////            return;
//        }

        initCommentWindow();
    }


    @Override
    protected void requestData(boolean refresh) {
        if (AppContext.getInstance().isLogin()) {
            mCatalog = AppContext.getInstance().getLoginUid();
        } else {
            mCatalog = 1;
        }
        sendRequestData();
    }

    @Override
    protected void sendRequestData() {
        if (!TDevice.hasInternet()) {
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            return;
        }
        int size = mAdapter.getData().size();
        MyLog.i("sendRequestData size=" + size);
        if (size > 0 && mCurrentPage != 0) {
            String lastUcid = mAdapter.getData().get(mAdapter.getData().size() - 1).getUcid();
            DiscoveryApi.getIdolCosplay(1, lastUcid, mHandler);
        } else {
            DiscoveryApi.getIdolCosplay(1, null, mHandler);
        }
    }

//    private CommentPopWindow commentPopWindow;
//
//    private void initCommentWindow() {
//        commentPopWindow = new CommentPopWindow(getActivity(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        commentPopWindow.setCallback(new CommentPopWindow.CommentCallback() {
//            @Override
//            public void onComment(String string) {
//                sendComment(string);
//            }
//
//            @Override
//            public void onFinish() {
//                ((MainActivity) getActivity()).showFloatingBtn();
//            }
//
//            @Override
//            public void onStart() {
//                ((MainActivity) getActivity()).hideFloatingBtn();
//            }
//        });
//    }

    private CommentDialogFragment mCommentDialog;

    private void initCommentWindow() {
        mCommentDialog = new CommentDialogFragment(getActivity());
        mCommentDialog.setCallback(new CommentDialogFragment.CommentCallback() {
            @Override
            public void onComment(String string) {
                sendComment(string);
            }

            @Override
            public void onFinish() {
                ((MainActivity) getActivity()).showFloatingBtn();
            }

            @Override
            public void onStart() {
                ((MainActivity) getActivity()).hideFloatingBtn();
            }
        });
    }

    private void sendComment(String str) {
        if (!isNetOKetc()) {
            return;
        }
        if (StringUtils.isBlank(str)) {
            AppContext.showToastShort(R.string.tip_comment_content_empty);
            return;
        }
        if (commentPosition < 0 || commentPosition >= mAdapter.getDataSize()) {
            AppContext.showToastShort(R.string.tip_comment_target_empty);
            return;
        }
        showWaitDialog(R.string.progress_submit);
        final CosplayInfo cosplayInfo = mAdapter.getItem(commentPosition);
        final String ucid = cosplayInfo.getUcid();
        String name = cosplayInfo.getAuthorName();
        String cosContent = StringUtil.nullToEmpty(cosplayInfo.getContent());
        if (cosContent.length() > 10) {
            cosContent = cosContent.substring(0, 10);
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(UmengConstants.ITEM_ID, ucid + "_" + name + "_" + cosContent);
//        map.put(UmengConstants.FROM, "关注页");
        MobclickAgent.onEvent(getActivity(), UmengConstants.COMMENT_COSPLAY, map);
        MobclickAgent.onEvent(getActivity(), UmengConstants.COMMENT, map);

        DiscoveryManager.getInstance().commentCosplay(ucid, null, str.toString(),
                mReplyUser == null ? null : mReplyUser.getUId(), null, new IListener2() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onSuccess(Object obj) {
                        hideWaitDialog();
                        AppContext.showToastShort(R.string.comment_publish_success);

                        List<CommentInfo> clist = mAdapter.getItem(commentPosition).getComments();
                        if (clist == null) {
                            clist = new ArrayList<CommentInfo>();
                        }
                        CommentInfo myComment = new CommentInfo();
                        myComment.setCid((String) obj);
                        myComment.setContent(str.toString());
                        myComment.setCreatedAt(System.currentTimeMillis());
                        myComment.setAuthor(ClientInfo.getLoginUser());
                        myComment.setReply(mReplyUser);
                        mReplyUser = null;
                        clist.add(0, myComment);
                        mAdapter.getItem(commentPosition).setComments(clist);
                        mAdapter.getItem(commentPosition).setCommentNum(mAdapter.getItem(commentPosition).getCommentNum() + 1);
                        mAdapter.notifyDataSetChanged();

//                        if (commentPopWindow == null) {
//                            initCommentWindow();
//                        }
//                        commentPopWindow.clean();

                        if (mCommentDialog == null) {
                            initCommentWindow();
                        }
                        mCommentDialog.clean();
                    }

                    @Override
                    public void onErr(Object obj) {
                        hideWaitDialog();
                        AppContext.showToastShort(R.string.comment_publish_faile);
                    }

                    @Override
                    public void onNoNetwork() {

                    }
                });
    }

    private boolean isNetOKetc() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_network_error);
            return false;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return false;
        }
        return true;
    }


    @Override
    protected int getPageSize() {
        // 关注列表每次下发两个
//        MyLog.i("getPageSize 关注列表每次下发两个 ~");
        return 2;
    }

    private int commentPosition = -1;
    private UserInfo mReplyUser;
    FollowAdapter adapter = null;

    @Override
    protected FollowAdapter getListAdapter() {
        if (adapter == null) {
            adapter = new FollowAdapter(getActivity());

            adapter.setFragment(this);
            MyLog.i("getListAdapter setCallback~");
            adapter.setCommentCallback(new FollowAdapter.CommentCallback() {
                @Override
                public void onComment(int position, UserInfo replyUser) {
                    commentPosition = position;
                    mReplyUser = replyUser;

//                    if (commentPopWindow == null) {
//                        initCommentWindow();
//                    }
//                    if (replyUser == null) {
//                        commentPopWindow.setHint(getString(R.string.comment_hint));
//                    } else {
//                        commentPopWindow.setHint(String.format(getString(R.string.reply_referral_nickname), replyUser.getUsername()));
//                    }
//                    commentPopWindow.showButtom();

                    if (mCommentDialog == null) {
                        initCommentWindow();
                    }
                    if (replyUser == null) {
                        mCommentDialog.setHint(getString(R.string.comment_hint));
                    } else {
                        mCommentDialog.setHint(String.format(getString(R.string.reply_referral_nickname), replyUser.getUsername()));
                    }
                    mCommentDialog.show(getActivity().getFragmentManager(), TAG);
                }
            });
        } else {
            MyLog.i("getListAdapter adapter != null ");
        }
        return adapter;
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX;
    }

    @Override
    protected FollowCosplayList parseList(InputStream is) throws Exception {
        FollowCosplayList list = XmlUtils.JsontoBean(FollowCosplayList.class, is);
        return list;
    }

    @Override
    protected FollowCosplayList readList(Serializable seri) {
        return ((FollowCosplayList) seri);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
//        CosplayInfo cosplayInfo = mAdapter.getItem(position);
//        if (cosplayInfo != null) {
//            UIHelper.showCosplayInfoDetail(view.getContext(), null, cosplayInfo.getId());
//        }
    }


    @Override
    protected void executeOnLoadFinish() {
        super.executeOnLoadFinish();
        if (mClickTime > 0) {
            long duration = TimeUtils.getCurrentTimeInLong() - mClickTime; //开发者需要自己计算时长
            mClickTime = 0;
            HashMap<String, String> forward = new HashMap<String, String>();
            forward.put("type", UmengConstants.T_COSPLAY_SHOW);
            onStatistics(BaseApplication.context(), UmengConstants.T_COSPLAY_SHOW, forward, duration);
        }
    }

    private void refreshData() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        });
    }

    @Override
    public void onTabReselect() {
        if (SCROLL_STATE==0){
            mListView.setSelection(0);
        }
    }

    @Override
    protected long getAutoRefreshTime() {
        // 最新关注10分钟刷新一次
        return 10 * 60;
    }

    //关注频道不取缓存数据
    @Override
    protected boolean isReadCacheData(boolean refresh) {
        return false;
    }

    private long mClickTime;

    public void onEvent(PhotoReleaseActivity.ReleaseOkEvent event) {
        mClickTime = event.getClickTime();
        refreshData();
    }

    public void onEvent(DiscoveryManager.CosplayDeleteEvent event) {
//        if (mAdapter.getDataSize() == 0) {
        refreshData();
//        }
        //删除当前页的缓存
//        deleteCacheData();
    }

    public void onEvent(CameraManager.CosplayUploadStatus event) {
        MyLog.i("ljc: received CosplayUploadStatus event....");
        adapter.refreshUploadStatus(event.getUcid(), event.isSuceed());
    }

    public void onEvent(MultiClickEvent event) {
        MyLog.i("multiClick oriNum=" + event.builder.getOriginNum() + ", clkNum=" + event.builder.getClickNum() + ", obj=" + event.builder.getObject());
    }


    private static void onStatistics(Context context, String id, HashMap<String, String> m, long value) {
        m.put("__ct__", String.valueOf(value));
        MobclickAgent.onEvent(context, id, m);
    }


    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                if (((CosplayInfo) enity).getUcid() == ((CosplayInfo) data.get(i))
                        .getUcid()) {
                    return true;
                }
            }
        }
        return false;
    }
}