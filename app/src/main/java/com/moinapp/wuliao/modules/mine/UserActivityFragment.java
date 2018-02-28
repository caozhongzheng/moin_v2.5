package com.moinapp.wuliao.modules.mine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BeseMultiHaveHeaderListFragment;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.FollowStatusChange;
import com.moinapp.wuliao.bean.Location;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.cache.CacheManager;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.interf.OnTabReselectListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.modules.mine.adapter.AUserActivityAdapter;
import com.moinapp.wuliao.modules.mine.adapter.MyActivityCosplayInfoAdapter;
import com.moinapp.wuliao.modules.mine.adapter.MyUserActivityAdapter;
import com.moinapp.wuliao.modules.mine.model.GetUserInfoResult;
import com.moinapp.wuliao.modules.mine.model.UserActivity;
import com.moinapp.wuliao.modules.mine.model.UserActivityList;
import com.moinapp.wuliao.modules.sticker.StickPreference;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.ui.RemarkReportDialog;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.AppTools;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;
import com.moinapp.wuliao.widget.AvatarView;
import com.moinapp.wuliao.widget.FollowView;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * 用户的动态界面
 *
 * @author caozz
 */
public class UserActivityFragment extends
        BeseMultiHaveHeaderListFragment<UserActivity, UserInfo> implements OnTabReselectListener {
    private static final ILogger MyLog = LoggerFactory.getLogger(UserActivityFragment.class.getSimpleName());
    private static String RECEIVE_MSG = "";

    AvatarView mIvAvatar;
    ImageView mIvGender;
    TextView mTvRemake;
    TextView mTvName;
    TextView mTvSignature;
    FollowView mBtnLikeUser;
    TextView mTvScore;
    TextView mTvFavorite;
    TextView mTvFollowing;
    TextView mTvFans;
    ImageView mIvSettings;
    ImageView mIvBack;
    View mUserContainer;
    View mUserUnLogin;
    LinearLayout mUserInfo;
    TextView mTvGender;
    TextView mTvStar;
    TextView mTvCity;
    LinearLayout mBtnChat;

    private boolean mIsWatingLogin;

    private String mUid;
    private LinearLayout mLlCity;
    private LinearLayout mLlStar;
    private View mHeaderLine;
    private RemarkReportDialog dialog;
    private View headerView;
    private int signature_max;
    AUserActivityAdapter mCurrentAdapter;
    @Override
    protected void sendRequestData() {
        if (StringUtil.isNullOrEmpty(mUid)) {
            return;
        }
        String lastid = null;
        int action;
        int type;
        if (mCurrentIndex == 0) {
            // 获取所有的消息列表 (3.2.7版本改为3, 之前是1)
            type = 3;
            action = 0;
        } else {
            // 获取所有的[九宫格]图片列表
            type = 2;
            action = 0;
        }
        if (mCurrentPage[mCurrentIndex] != 0 && mAdapter[mCurrentIndex].getData().size() > 0) {
            lastid = mAdapter[mCurrentIndex].getItem(
                    mAdapter[mCurrentIndex].getData().size() - 1) != null ?
                    mAdapter[mCurrentIndex].getItem(mAdapter[mCurrentIndex].getData().size() - 1).getActivityId()
                    : null;
        }
        MyLog.i("sendRequestData uid=" + mUid + ", type=" + type + ", action=" + action + ", lastid=" + lastid + ", mCurrentPage=" + mCurrentPage[mCurrentIndex]);

        MineApi.getMyActivity(mUid, type, action, lastid, mHandler);

        //fix有时候重新登陆的话,新的uid没有set到适配器里面,造成一些适配器的判断用户逻辑出错
        if (mCurrentAdapter != null && (mCurrentAdapter instanceof MyUserActivityAdapter)) {
            ((MyUserActivityAdapter) mCurrentAdapter).setUid(mUid);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signature_max = getResources().getInteger(R.integer.signature_max_len);
        // 默认进入九宫格模式
        setMode(1);
        Bundle args = getArguments();
        if (args != null) {
            mUid = args.getString(DiscoveryConstants.USERID);
//            mUid = "55ade9be0bdcb6025e2f0f61";
//            MyLog.i("args 小飞哥 uid=" + mUid);
        }

        if (StringUtil.isNullOrEmpty(mUid)) {
            mUid = ClientInfo.getUID();
            // 如果是当前用户,则进入列表模式
            setMode(0);
        } else if (ClientInfo.isLoginUser(mUid)) {
            // 如果是当前用户,则进入列表模式
            setMode(0);
        }
    }

    private void setMode(int mode) {
        mCurrentIndex = mode;

    }

    private boolean registed = false;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            RECEIVE_MSG = intent.getAction();
            if (mUid == null) {
                mUid = RECEIVE_MSG.equals(Constants.INTENT_ACTION_LOGOUT) ? "" : ClientInfo.getUID();
            }
        }
    };

    public void onEvent(FollowStatusChange status) {
        MyLog.e(status.toString());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateHeader(detailBean, status.getNewRelation());
            }
        });
    }

    // 删除大咖秀作品后,不影响此页面的内容
    /*
    public void onEvent(DiscoveryManager.CosplayDeleteEvent event) {
        Log.i("ljc", UserActivityFragment.class.getSimpleName()+"-->onEvent CosplayDeleteEvent ok!!");
//        requestDetailData(true);
        String ucid = event.getUcid();
        if (!TextUtils.isEmpty(ucid)) {
            for (UserActivity cosplayInfo : mAdapter[mCurrentIndex].getData()) {
                if (cosplayInfo.getCosplay() != null && ucid.equalsIgnoreCase(cosplayInfo.getCosplay().getUcid())) {
                    Log.i("ljc", UserActivityFragment.class.getSimpleName()+"-->onEvent:find delete items!");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter[mCurrentIndex].removeItem(cosplayInfo);
                            mAdapter[mCurrentIndex].notifyDataSetChanged();
                        }
                    });
                    break;
                }
            }
        }
    }
    */

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        for (int i = 0; i < LIST_COUNT; i++) {
            if (i == mCurrentIndex) {
                mLayout[i].setVisibility(View.VISIBLE);
            } else {
                mLayout[i].setVisibility(View.GONE);
            }

//            RelativeLayout.MarginLayoutParams params = (RelativeLayout.MarginLayoutParams) mErrorLayout[i].getLayoutParams();
//            params.topMargin = mHeaderView.getLayoutParams().height;
//            MyLog.i("这个empty的marginTop=" + params.topMargin);
//            mErrorLayout[i].setLayoutParams(params);
            final int finalI = i;
            view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    int headerHeight = headerView.getMeasuredHeight();
                    mErrorLayout[finalI].layout(0, headerHeight - 100, (int) TDevice.getScreenWidth(),
                            mErrorLayout[finalI].getMeasuredHeight() + headerHeight);
                }
            });

            mErrorLayout[i].setEmptyImage(R.drawable.activity_empty_image);
            mErrorLayout[i].setBackgroundColor(0);
            if (i == 1) {
                if (ClientInfo.isLoginUser(mUid)) {
                    mErrorLayout[i].setNoDataContent(getString(R.string.ac_grid_no_data_self));
                    mErrorLayout[i].setBtnText(getString(R.string.go_to_discovery_self));
                    mErrorLayout[i].setBtnVisibility(View.VISIBLE);
                    mErrorLayout[i].setBtnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // TODO 进入拍照界面
                            CameraManager.getInst().openCamera(getActivity() == null ? AppContext.context() : getActivity(), null);
                            StickPreference.getInstance().setDefaultUseSticker(null);
                        }
                    });
                } else {
                    mErrorLayout[i].setOnLayoutClickListener(null);
                    mErrorLayout[i].setNoDataContent(String.format(getString(R.string.ac_grid_no_data_somebody), mTvName.getText()));
                    mErrorLayout[i].setBtnVisibility(View.GONE);
                }
            }
        }

        switch (mCurrentIndex) {
            // 时间轴模式
            case 0:
                mExchangeBt.setImageResource(R.drawable.ac_gridmode);
                break;
            // 九宫格模式
            case 1:
            default:
                mExchangeBt.setImageResource(R.drawable.ac_linemode);
                break;
        }

        setupUser();
        mOutsideErrorLayout.setOnLayoutClickListener(v -> {
            UIHelper.showLoginActivity(getActivity());
        });

        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_USER_CHANGE);
        filter.addAction(Constants.INTENT_ACTION_LOGOUT);
        filter.addAction(Constants.INTENT_ACTION_USER_CHANGE);

        getActivity().registerReceiver(mReceiver, filter);
        registed = true;

        EventBus.getDefault().register(this);
    }

    @Override
    protected void requestDetailData(boolean isRefresh) {
        if (!AppContext.getInstance().isLogin()) {
            mIsWatingLogin = true;
        } else {
            mIsWatingLogin = false;
        }
        setupUser();
        // 获取我的信息详情
        MineApi.getUserInfo(mUid, mDetailHandler);
    }

    @Override
    protected void onGetDetailSuccess() {
        super.onGetDetailSuccess();
        mIsWatingLogin = false;
        if (detailBean != null && detailBean.getRelation() == 4) {
            mUid = detailBean.getUId();
            if (mAdapter[0] != null) {
                ((MyUserActivityAdapter) mAdapter[0]).setUid(mUid);
            }
        }
        setupUser();
    }

    @Override
    protected boolean onGetDetailFailed(byte[] arg2) {
        super.onGetDetailFailed(arg2);

        /*
        if (arg2 != null) {
            GetUserInfoResult result = XmlUtils.JsontoBean(GetUserInfoResult.class, arg2);
            if (result != null) {
                MyLog.i("onGetDetailFailed GetUserInfoResult result=" + result.getResult() + ", err=" + result.getError());
                if (result.getUser() != null) {
                    MyLog.i("onGetDetailFailed GetUserInfoResult userinfo=" + result.getUser().toString());
                }
                // 用户不存在时显示最大的占位图
                if (result.getResult()==0 && result.getError() == -1) {
//                    mOutsideErrorLayout.setVisibility(View.VISIBLE);
                }
            }
        }
        */
        mIsWatingLogin = true;
        setupUser();
        return true;
    }

    /**
     * 设置用户信息详情UI
     */
    private void setupUser() {
        // 未登录时进入我的,应该是提示登录界面
        if (!AppContext.getInstance().isLogin() && StringUtil.isNullOrEmpty(mUid)) {
            mOutsideErrorLayout.setErrorType(EmptyLayout.NO_LOGIN);
        } else {
            // 登录用户看自己
            if (ClientInfo.isLoginUser(mUid)) {
//            MyLog.i("is login user and setupUser mIsWatingLogin=" + mIsWatingLogin);
//            if (mIsWatingLogin && StringUtil.isNullOrEmpty(AppContext.getInstance().getUserInfo().getUId())) {
//                mUserContainer.setVisibility(View.GONE);
//                mUserUnLogin.setVisibility(View.VISIBLE);
//            } else {
//            }
                mUserContainer.setVisibility(View.VISIBLE);
                mUserUnLogin.setVisibility(View.GONE);
                mIvBack.setVisibility(View.GONE);
//                mHeaderLine.setVisibility(View.VISIBLE);
                mIvGender.setVisibility(View.VISIBLE);
                mUserInfo.setVisibility(View.GONE);
                mBtnLikeUser.setVisibility(View.GONE);
                mBtnChat.setVisibility(View.GONE);
                // TODO 新版动态中没有此设置按钮了
                mIvSettings.setVisibility(View.GONE);
            } else {
                // 登录用户看别的用户
//            MyLog.i("is NOT login user and setupUser");
                mUserContainer.setVisibility(View.VISIBLE);
                mUserUnLogin.setVisibility(View.GONE);
                mIvBack.setVisibility(View.VISIBLE);
                mUserInfo.setVisibility(View.VISIBLE);
                mBtnLikeUser.setVisibility(View.VISIBLE);
                mBtnChat.setVisibility(View.VISIBLE);
                mIvSettings.setVisibility(View.VISIBLE);
                mIvGender.setVisibility(View.GONE);
//                mHeaderLine.setVisibility(View.GONE);
                if (detailBean != null && isAdded()) {
                    mErrorLayout[mCurrentIndex].setNoDataContent(
                            String.format(getString(R.string.ac_grid_no_data_somebody), StringUtil.nullToEmpty(detailBean.getUsername())));
                }
            }
        }
        if (isAdded()) {
            mErrorLayout[mCurrentIndex].setBackgroundColor(getResources().getColor(R.color.user_activity_light_grey));
        }
    }

    @Override
    protected View initHeaderView() {
        headerView = LayoutInflater.from(getActivity()).inflate(
                R.layout.fragment_my_info_head, null);

        mIvAvatar = (AvatarView) headerView.findViewById(R.id.iv_avatar);
        mIvGender = (ImageView) headerView.findViewById(R.id.iv_gender);
        mTvRemake = (TextView) headerView.findViewById(R.id.tv_remark);
        mTvName = (TextView) headerView.findViewById(R.id.tv_name);
        mTvSignature = (TextView) headerView.findViewById(R.id.tv_signature);
        mTvGender = (TextView) headerView.findViewById(R.id.tv_gender);
        mTvStar = (TextView) headerView.findViewById(R.id.tv_star);
        mTvCity = (TextView) headerView.findViewById(R.id.tv_city);
        mBtnLikeUser = (FollowView) headerView.findViewById(R.id.btn_like_user);
        mBtnChat = (LinearLayout) headerView.findViewById(R.id.btn_chat);
        mTvScore = (TextView) headerView.findViewById(R.id.tv_score);
        mTvFavorite = (TextView) headerView.findViewById(R.id.tv_favorite);
        mTvFollowing = (TextView) headerView.findViewById(R.id.tv_following);
        mTvFans = (TextView) headerView.findViewById(R.id.tv_follower);
        mIvBack = (ImageView) headerView.findViewById(R.id.iv_back);
        mIvSettings = (ImageView) headerView.findViewById(R.id.iv_settings);
        mUserContainer = headerView.findViewById(R.id.ll_user_container);
        mUserUnLogin = headerView.findViewById(R.id.rl_user_unlogin);
        mUserInfo = (LinearLayout) headerView.findViewById(R.id.ll_user_info);
        mLlStar = (LinearLayout) headerView.findViewById(R.id.ll_star);
        mLlCity = (LinearLayout) headerView.findViewById(R.id.ll_city);
//        mHeaderLine = (View) headerView.findViewById(R.id.view_big_line);

        if (!ClientInfo.isLoginUser(mUid)) {
            mIvBack.setVisibility(View.VISIBLE);
            mUserInfo.setVisibility(View.VISIBLE);
            mIvSettings.setVisibility(View.VISIBLE);
            mIvGender.setVisibility(View.GONE);
//            mHeaderLine.setVisibility(View.GONE);
        } else {
            mIvBack.setVisibility(View.GONE);
            mUserInfo.setVisibility(View.GONE);
            mIvSettings.setVisibility(View.GONE);
            mIvGender.setVisibility(View.VISIBLE);
//            mHeaderLine.setVisibility(View.VISIBLE);
        }
        mIvAvatar.setOnClickListener(this);
        headerView.findViewById(R.id.ly_favorite).setOnClickListener(this);
        headerView.findViewById(R.id.ly_following).setOnClickListener(this);
        headerView.findViewById(R.id.ly_follower).setOnClickListener(this);
        headerView.findViewById(R.id.btn_chat).setOnClickListener(this);

        mUserUnLogin.setOnClickListener(v -> {
            UIHelper.showLoginActivity(getActivity());
        });
        mIvBack.setOnClickListener(this);
        mIvSettings.setOnClickListener(this);

        return headerView;
    }

    @Override
    protected boolean needShowEmptyNoData() {
        return true;
    }

    @Override
    public void initData() {
        super.initData();

        for (int i = 0; i < LIST_COUNT; i++) {
            mErrorLayout[i].setErrorType(EmptyLayout.HIDE_LAYOUT);
            mErrorLayout[i].setOnLayoutClickListener(v -> {
                if (AppContext.getInstance().isLogin()) {
                    requestData(true);
                } else {
                    if (!ClientInfo.isLoginUser(mUid)) {
                        UIHelper.showLoginActivity(getActivity());
                    } else {
                        requestData(true);
                    }
                }
            });
        }
    }

    /**
     * 切换个人中心的九宫格和时间轴列表格式
     */
    @Override
    protected void doExchange() {
        super.doExchange();
        MobclickAgent.onEvent(getActivity(), UmengConstants.ACTIVITY_SWITCH);
    }

    @Override
    public void onClick(View v) {
//        if (mIsWatingLogin) {
//            AppContext.showToast(R.string.unlogin);
//            UIHelper.showLoginActivity(getActivity());
//            return;
//        }
        String userName;
        if (ClientInfo.isLoginUser(mUid) && AppContext.getInstance().isLogin()) {
            userName = detailBean != null ? detailBean.getUsername() : ClientInfo.getUserName();
        } else {
            userName = detailBean != null ? detailBean.getUsername() : "";
        }
        final int id = v.getId();
        switch (id) {
            case R.id.iv_avatar:
                if (ClientInfo.isLoginUser(mUid) && AppContext.getInstance().isLogin()) {
//            UIHelper.showSimpleBack(getActivity(),
//                    SimpleBackPage.MY_INFORMATION_DETAIL);
                    AppTools.toIntent(getActivity(), PersonalInfoActivity.class);
                } else {
                    UIHelper.showUserAvatar(getActivity(), mIvAvatar.getUri());
                    getActivity().overridePendingTransition(R.anim.zoomin, 0);
                }
                break;
            case R.id.iv_qr_code:
                // 显示二维码
//                showMyQrCode();
                break;
            case R.id.iv_back:
                getActivity().finish();
                break;
            case R.id.iv_settings:
//                UIHelper.showSetting(getActivity());
                showEditDialog();
                break;
            case R.id.ly_following:
                // TODO 进入关注列表
                UIHelper.showMyFollowers(getActivity(), mUid, userName, 0);
                break;
            case R.id.ly_follower:
                // TODO 进入粉丝列表
                UIHelper.showMyFans(getActivity(), mUid, userName, 0);
                break;
            case R.id.ly_favorite:
                // TODO 进入标签列表
                UIHelper.showMyTagDetail(getActivity(), mUid, userName, 0);
                break;
            case R.id.rl_user_center:
                /*// TODO 查看用户详情
                UIHelper.showUserCenter(getActivity(), AppContext.getInstance()
                        .getLoginUid(), AppContext.getInstance().getLoginUser()
                        .getName());*/
                break;
            case R.id.btn_chat:
                // 跳转到与当前用户聊天界面
//                AppContext.showToastShort("跳转聊天界面[" + mUid + "/" + detailBean.getUsername() + "]");

                if (!AppContext.getInstance().isLogin()){
                    UIHelper.showLoginActivity(getActivity());
                } else if (detailBean != null) {
                    CacheManager.saveObject(getActivity(), detailBean, detailBean.getUId());
                    UIHelper.showChat(getActivity(), mUid, StringUtil.getUserName(detailBean));
                }
//                UIHelper.showMessageDetail(getActivity(), mUid.hashCode(), detailBean.getUsername());
                break;
            default:
                break;
        }
    }

    private void showEditDialog() {
        if (detailBean != null) {
            dialog.setUserInfo(detailBean);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.USER_ACTIVITY_FRAGMENT);
        // 如果登录成功,可以刷新登录用户信息
        if (!mIsWatingLogin) {
            requestDetailData(true);
        }

        if (!StringUtil.isNullOrEmpty(RECEIVE_MSG)) {
            if (Constants.INTENT_ACTION_USER_CHANGE.equals(RECEIVE_MSG)) {
                mUserContainer.setVisibility(View.VISIBLE);
                mUserUnLogin.setVisibility(View.GONE);
                mOutsideErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
//                mOutsideErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
//                if (mUid == null) {
//                    mUid = ClientInfo.getUID();
//                }
                RECEIVE_MSG = "";
                // 如果看的是登录用户自己的动态界面,那么退出,进入首页个人动态.
                if (ClientInfo.isLoginUser(mUid)) {
                    UIHelper.showMine(getActivity(), 0);
                    getActivity().finish();
                } else {
                    requestDetailData(true);
                }
            } else if (Constants.INTENT_ACTION_LOGOUT.equals(RECEIVE_MSG)) {
                mUid = "";
                mUserContainer.setVisibility(View.GONE);
                mUserUnLogin.setVisibility(View.VISIBLE);
                mOutsideErrorLayout.setErrorType(EmptyLayout.NO_LOGIN);
                RECEIVE_MSG = "";
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.USER_ACTIVITY_FRAGMENT);
    }

    // 更新个人信息详情header
    @Override
    protected void executeOnLoadDetailSuccess(UserInfo detailBean) {
        if (detailBean == null)
            return;
        // 填充我的个人信息详情
//        if (AppContext.getInstance().isLogin()) {
//            mIvAvatar.setAvatarUrl(BitmapUtil.getAvatarImagePath());
//        }
//        MyLog.i("executeOnLoadDetailSuccess " + detailBean!=null?detailBean.toString():"null");
        final int relation = detailBean.getRelation();
        mBtnLikeUser.init(detailBean, relation, UserDefineConstants.FOLLOW_USER_CENTER);

        updateHeader(detailBean, relation);

        // TODO 新版动态中是动态的个数
        mTvScore.setText(String.valueOf(detailBean.getActivityNum()));//图片个数 3.0版是动态的个数
        mTvFavorite.setText(String.valueOf(detailBean.getTagNum()));//标签个数
        mTvFollowing.setText(String.valueOf(detailBean.getIdolNum()));//关注个数
        mTvFans.setText(String.valueOf(detailBean.getFansNum()));//粉丝个数
    }

    private void updateHeader(UserInfo detailBean, int relation) {
        if (detailBean == null)
            return;
        detailBean.setRelation(relation);
        MyLog.i("updateHeader " + detailBean.toString());
        dialog = new RemarkReportDialog(getActivity());
        dialog.setIcon(relation);
        dialog.setOnChangeRemarkNameListener(new RemarkReportDialog.OnChangeRemarkNameListener() {
            @Override
            public void onChangeSucess() {
                updateHeader(detailBean, relation);
                CacheManager.saveObject(getActivity(), detailBean, detailBean.getUId());
            }

            @Override
            public void onChangeFailed() {

            }
        });

//        mUid = detailBean.getUId();
        if (detailBean.getAvatar() == null || StringUtil.isNullOrEmpty(detailBean.getAvatar().getUri())) {
//            MyLog.i("getAvatar()=null" + detailBean.toString());
        } else {
            mIvAvatar.setAvatarUrl(detailBean.getAvatar().getUri());
        }

        // 未关注的情况,不显示备注
        if (relation < 2 || StringUtil.isNullOrEmpty(detailBean.getAlias())) {
            mTvRemake.setText(detailBean.getUsername());
            mTvName.setVisibility(View.GONE);
        } else if (relation >= 2 && !StringUtil.isNullOrEmpty(detailBean.getAlias())) {
            mTvRemake.setText(detailBean.getAlias());
            mTvName.setVisibility(View.VISIBLE);
            mTvName.setText(detailBean.getUsername());
        }

        if (StringUtil.isNullOrEmpty(detailBean.getSignature())) {
            mTvSignature.setVisibility(View.VISIBLE);
            mTvSignature.setText("注册时间: "+StringUtil.formatDate(detailBean.getCreatedAt(), "yyyy.MM.dd"));
        } else {
            mTvSignature.setVisibility(View.VISIBLE);
            String signature = StringUtil.nullToEmpty(detailBean.getSignature());
            mTvSignature.setText(signature.length() > signature_max ? signature.substring(0, signature_max - 1) + "…" : signature);
        }

        if (detailBean.getSex() != null) {
            mIvGender.setImageResource("male".equals(detailBean.getSex()) ? R.drawable.boy_yellow
                    : R.drawable.girl_red);
            mTvGender.setText("male".equals(detailBean.getSex()) ? R.string.male
                    : R.string.female);
            mIvGender.setImageResource("male".equals(detailBean.getSex()) ? R.drawable.boy_yellow
                    : R.drawable.girl_red);
        } else {
            mIvGender.setImageResource(R.drawable.boy_yellow);
            mTvGender.setText(R.string.male);
        }

        if (detailBean.getStars() >= 0) {
            mLlStar.setVisibility(View.VISIBLE);
            mTvStar.setText(AlterZodiacActivity.zodiac_name_arr[detailBean.getStars() - 1]);
        } else {
            mLlStar.setVisibility(View.GONE);
        }

        if (detailBean.getLocation() != null ) {
            mLlCity.setVisibility(View.VISIBLE);
            mTvCity.setText(getLocation(detailBean.getLocation()));
        } else {
            mLlCity.setVisibility(View.GONE);
        }
    }

    private String getLocation(Location location) {
        if (location == null) return "";
        StringBuilder sb = new StringBuilder();
        if (!StringUtil.isNullOrEmpty(location.getProvince())) {
            if (!StringUtil.isNullOrEmpty(location.getCity())) {
                sb.append(location.getProvince()).append("·").append(location.getCity());
            }else {
                sb.append(location.getProvince());
            }
        }else {
            if (!StringUtil.isNullOrEmpty(location.getCity())) {
                sb.append(location.getCity()).append(" ");
            }
        }

        return sb.toString();
    }

    @Override
    protected String getDetailCacheKey() {
        return "my_info_detail_" + mUid;
    }

    @Override
    protected UserInfo getDetailBean(ByteArrayInputStream is) {
//        UserInfo us = XmlUtils.JsontoBean(GetUserInfoResult.class, is).getUser();
//        MyLog.i("get detail=" + us.toString());
//        return us;
        return XmlUtils.JsontoBean(GetUserInfoResult.class, is).getUser();
    }

    @Override
    protected ListBaseAdapter<UserActivity> getListAdapter() {
        AUserActivityAdapter adapter = null;
        if ( mCurrentIndex== 0) {
            adapter = new MyUserActivityAdapter();
            ((MyUserActivityAdapter) adapter).setUid(mUid);
        } else if (mCurrentIndex == 1) {
            adapter = new MyActivityCosplayInfoAdapter();
        }
        mCurrentAdapter = adapter;
        return adapter;
    }

    @Override
    protected UserActivityList parseList(InputStream is) throws Exception {

        UserActivityList list = XmlUtils.JsontoBean(UserActivityList.class, is);
        if (list != null && list.getList() != null && !list.getList().isEmpty()) {
//            MyLog.i("get index =" + mCurrentIndex);
//            MyLog.i("get list size=" + list.getList().size());
//            for (int i = 0; i < list.getList().size(); i++) {
//                UserActivity inf = list.getList().get(i);
//                if (inf != null && inf.getFollow() != null) {
//                    MyLog.i((i + 1) + "个大咖秀的follow " + inf.getFollow().toString());
//                }
//                if (inf != null && inf.getCosplay() != null) {
//                    MyLog.i((i + 1) + "个大咖秀的cosplay " + inf.getCosplay().toString());
//                }
//                MyLog.i((i+1) + "个大咖秀 " + inf.getAction()
//                                + ", aid=[" + inf.getActivityId()
//                                + ", time=" + StringUtil.humanDate(inf.getCreatedAt(), "MM/dd")
//                                + ", 发起者" + inf.getUserName()
//                                + ", 接收者" + inf.getTargetName()
//                                + ", content" + inf.getContent()
//                                + ", pic= " + inf.getPicture()
//                                + ", res= " + inf.getResource()
//                                + ", cos= " + inf.getCosplay()
//                );
//            }
        }
        return list;

//        return XmlUtils.JsontoBean(UserActivityList.class, is);
    }

    @Override
    protected UserActivityList readList(Serializable seri) {
        return (UserActivityList) seri;
    }

    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                if (((UserActivity) enity).getActivityId().equals(((UserActivity) data.get(i)).getActivityId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected String getCacheKeyPrefix(int mCurrentIndex) {
        return "my_activity_index_" + mCurrentIndex + "_" + mUid;
    }

    @Override
    protected long getAutoRefreshTime() {
        return super.getAutoRefreshTime();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregist(this);
        if (mReceiver != null && registed) {
            try {
                getActivity().unregisterReceiver(mReceiver);
                registed = false;
            } catch (Exception e) {
                MyLog.e(e);
            }
        }
    }

    @Override
    public void onTabReselect() {
        scrollToTop();
    }

    @Override
    protected void onScrolling(int scrollState) {
    }
}
