package com.moinapp.wuliao.modules.mine;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.MoinBean;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.cache.CacheManager;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.modules.mine.model.ChatMessage;
import com.moinapp.wuliao.modules.mission.MoinBeanActivity;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.AppTools;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.AvatarView;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 用户空间
 * Created by guyunfei on 16/1/19.14:51.
 */
public class UserSpaceFragment extends Fragment implements View.OnClickListener {

    private static final ILogger MyLog = LoggerFactory.getLogger(UserSpaceFragment.class.getSimpleName());

    private static String RECEIVE_MSG = "";

    @InjectView(R.id.ll_info_user_space)
    protected LinearLayout userInfoView;

    @InjectView(R.id.avatar_user_space)
    protected AvatarView userAvatar;

    @InjectView(R.id.tv_name_user_space)
    protected TextView userName;

    @InjectView(R.id.tv_sign_user_space)
    protected TextView userSign;

    @InjectView(R.id.iv_user_gender)
    protected ImageView userGender;

    @InjectView(R.id.rl_moin_bean_user_space)
    protected RelativeLayout moinBeanView;

    @InjectView(R.id.tv_moin_bean_num_user_space)
    protected TextView moinBeanNum;

    @InjectView(R.id.rl_moin_pk_user_space)
    protected RelativeLayout moinBeanPKView;

    @InjectView(R.id.tv_moin_pk_num_user_space)
    protected TextView moinBeanPKNum;

    @InjectView(R.id.rl_message_user_space)
    protected RelativeLayout messageView;

    @InjectView(R.id.tv_message_user_space)
    protected TextView userMessage;

    @InjectView(R.id.rl_chat_user_space)
    protected RelativeLayout chatView;

    @InjectView(R.id.rl_tags_user_space)
    protected RelativeLayout tagView;

    @InjectView(R.id.tv_tag_num_user_space)
    protected TextView userTagsNum;

    @InjectView(R.id.rl_follows_user_space)
    protected RelativeLayout followView;
    @InjectView(R.id.tv_follow_num_user_space)
    protected TextView userFollowsNum;

    @InjectView(R.id.rl_fans_user_space)
    protected RelativeLayout fansView;
    @InjectView(R.id.tv_fan_num_user_space)
    protected TextView userFansNum;

    @InjectView(R.id.rl_pics_user_space)
    protected RelativeLayout picsView;
//    @InjectView(R.id.tv_pics_num_user_space)
//    protected TextView userPicsNum;

    @InjectView(R.id.rl_stickers_user_space)
    protected RelativeLayout stickersView;

    @InjectView(R.id.rl_comments_user_space)
    protected RelativeLayout commentView;

    @InjectView(R.id.rl_likes_user_space)
    protected RelativeLayout likeView;

    @InjectView(R.id.rl_setting_user_space)
    protected RelativeLayout settingView;

    @InjectView(R.id.iv_chat_red_point_user_space)
    protected ImageView chatRedPoint;

    @InjectView(R.id.error_layout)
    protected EmptyLayout mErrorLayout;
    String mUid;
    private UserInfo mUserInfo;

    private boolean registed = false;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            RECEIVE_MSG = intent.getAction();
            mUid = RECEIVE_MSG.equals(Constants.INTENT_ACTION_LOGOUT) ? "" : ClientInfo.getUID();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //由于用户注销后不会再执行此方法,所以可能引起mUid为空,故将下列代码移至onResume()方法中执行
//        Bundle args = getArguments();
//        if (args != null) {
//            mUid = args.getString(DiscoveryConstants.USERID);
//        }
//        if (StringUtil.isNullOrEmpty(mUid)) {
//            mUid = ClientInfo.getUID();
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_space, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        initView(view);
        initData(mUid);

        EventBus.getDefault().register(this);

        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_LOGOUT);
        filter.addAction(Constants.INTENT_ACTION_USER_CHANGE);
        getActivity().registerReceiver(mReceiver, filter);
        registed = true;
    }

    private void initView(View view) {
        userInfoView.setOnClickListener(this);
        userAvatar.setOnClickListener(this);
        moinBeanView.setOnClickListener(this);
        moinBeanPKView.setOnClickListener(this);
        messageView.setOnClickListener(this);
        tagView.setOnClickListener(this);
        followView.setOnClickListener(this);
        fansView.setOnClickListener(this);
        picsView.setOnClickListener(this);
        stickersView.setOnClickListener(this);
        commentView.setOnClickListener(this);
        likeView.setOnClickListener(this);
        settingView.setOnClickListener(this);
        chatView.setOnClickListener(this);
        if (AppContext.getInstance().isLogin()) {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        } else {
            mErrorLayout.setErrorType(EmptyLayout.NO_LOGIN);
        }
        mErrorLayout.setOnLayoutClickListener(v -> {
            UIHelper.showLoginActivity(getActivity());
        });
    }

    private void initData(String uid) {
        MineManager.getInstance().getUserInfo(uid, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                if (obj != null) {
                    mUserInfo = (UserInfo) obj;
                    // TODO 测试代码
                    CacheManager.saveObject(getActivity(), mUserInfo, mUserInfo.getUId());
                    updatePageData(mUserInfo);
                }
            }

            @Override
            public void onErr(Object obj) {

            }

            @Override
            public void onNoNetwork() {

            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updatePageData(UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }
        if (userInfo.getAvatar() == null || StringUtil.isNullOrEmpty(userInfo.getAvatar().getUri())) {
        } else {
            userAvatar.setAvatarUrl(userInfo.getAvatar().getUri());
        }
        userName.setText(userInfo.getUsername());
        if (StringUtil.isNullOrEmpty(userInfo.getSignature())) {
            userSign.setVisibility(View.GONE);
        } else {
            userSign.setVisibility(View.VISIBLE);
            userSign.setText(StringUtil.nullToEmpty(userInfo.getSignature()));
        }

        if (userInfo.getSex() != null) {
            userGender.setImageResource(userInfo.getSex().equals("male") ? R.drawable.boy_yellow
                    : R.drawable.girl_red);
        } else {
            userGender.setImageResource(R.drawable.boy_yellow);
        }

        if (userInfo.getMoinBean() != null) {
            moinBeanNum.setText(String.valueOf(userInfo.getMoinbeanNum()));//魔豆个数
            moinBeanPKNum.setText(String.valueOf(userInfo.getMoinbeanPkNum()) + "%");//魔豆PK
        }
        userTagsNum.setText(String.valueOf(userInfo.getTagNum()));//标签个数
        userFollowsNum.setText(String.valueOf(userInfo.getIdolNum()));//关注个数
        userFansNum.setText(String.valueOf(userInfo.getFansNum()));//粉丝个数
//        userPicsNum.setText(String.valueOf(userInfo.getCosplayNum()));//图片个数
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UserSpaceFragment.class.getSimpleName());
//        initData(mUid);
//        mUserInfo = AppContext.getInstance().getUserInfo();

        updatePageData(mUserInfo);
        MineManager.getInstance().getMoinBean(mUid, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                if (obj != null) {
                    MoinBean moinBean = (MoinBean) obj;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (moinBean != null) {
                                moinBeanNum.setText(moinBean.getTotalBean() + "");
                                if (moinBean.getPkRank() != null) {
                                    moinBeanPKNum.setText(moinBean.getPkRank() + "%");
                                } else {
                                    moinBeanPKNum.setText("0%");
                                }
                            } else {
                                moinBeanNum.setText("0");
                                moinBeanPKNum.setText("0%");
                            }
                        }
                    });
                }
            }

            @Override
            public void onErr(Object obj) {

            }

            @Override
            public void onNoNetwork() {

            }
        });
        //设置消息小红点
        setNewMessageRedPoint();

        setChatRedPoint();
        if (!StringUtil.isNullOrEmpty(RECEIVE_MSG)) {
            if (mErrorLayout != null) {
                if (Constants.INTENT_ACTION_USER_CHANGE.equals(RECEIVE_MSG)) {
                    mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    initData(mUid = ClientInfo.getUID());
                } else {
                    mErrorLayout.setErrorType(EmptyLayout.NO_LOGIN);
                    mUid = "";
                }
            }
            RECEIVE_MSG = "";
        }
        if (AppContext.getInstance().isLogin()) {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        } else {
            mErrorLayout.setErrorType(EmptyLayout.NO_LOGIN);
        }

        Bundle args = getArguments();
        if (args != null) {
            mUid = args.getString(DiscoveryConstants.USERID);
        }
        if (StringUtil.isNullOrEmpty(mUid)) {
            mUid = ClientInfo.getUID();
        }

    }

    private void setNewMessageRedPoint() {
        int unreadMsg = MineManager.getInstance().getUnreadMessages(MinePreference.getInstance().getLastReadTime());
        if (unreadMsg > 0) {
            Drawable drawable = getResources().getDrawable(R.drawable.icon_message_user_space_red_point);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            userMessage.setCompoundDrawables(null, null, drawable, null);
        } else {
            userMessage.setCompoundDrawables(null, null, null, null);
        }
    }

    private void setChatRedPoint() {
        List<ChatMessage> chatRecordList = MineManager.getInstance().getChatRecordList();
        if (chatRecordList != null) {
            for (ChatMessage message : chatRecordList) {
                int readStatus = message.getReadStatus();
                // 0未读 1已读
                if (readStatus == 0) {
                    chatRedPoint.setVisibility(View.VISIBLE);
                    break;
                } else {
                    chatRedPoint.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UserSpaceFragment.class.getSimpleName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avatar_user_space:
            case R.id.ll_info_user_space:
                AppTools.toIntent(getActivity(), PersonalInfoActivity.class);
                break;
            case R.id.rl_moin_bean_user_space:
                AppTools.toIntent(getActivity(), MoinBeanActivity.class);
                break;
            case R.id.rl_moin_pk_user_space:
                if (mUserInfo != null) {
                    UIHelper.showMoinBeanPk(getActivity(), mUserInfo);
                }
//                AppTools.toIntent(getActivity(), MoinBeanPKFragment.class);
                break;
            case R.id.rl_message_user_space:
                UIHelper.showMyMessage(getActivity());
                userMessage.setCompoundDrawables(null, null, null, null);
                MinePreference.getInstance().setLastReadTime(System.currentTimeMillis());
                break;
            // TODO 聊天列表
            case R.id.rl_chat_user_space:
                UIHelper.showChatList(getActivity());
                break;
            // TODO 话题列表
            case R.id.rl_tags_user_space:
                UIHelper.showMyTagDetail(getActivity(), mUid, mUserInfo != null ? mUserInfo.getUsername() : "", 0);
                break;
            // TODO 关注列表
            case R.id.rl_follows_user_space:
                UIHelper.showMyFollowers(getActivity(), mUid, mUserInfo != null ? mUserInfo.getUsername() : "", 0);
                break;
            // TODO 粉丝列表
            case R.id.rl_fans_user_space:
                UIHelper.showMyFans(getActivity(), mUid, mUserInfo != null ? mUserInfo.getUsername() : "", 0);
                break;
            // TODO 我的图片
            case R.id.rl_pics_user_space:
                UIHelper.showMyCosplay(getActivity(), mUid);
                break;
            // TODO 我的贴纸
            case R.id.rl_stickers_user_space:
                if (TDevice.hasInternet()) {
                    UIHelper.showStickerCenter(getActivity(), ClientInfo.getUID(), 0, 2);
                } else {
                    AppContext.showToastShort(R.string.tip_network_error);
                }
                break;
            case R.id.rl_comments_user_space:
                UIHelper.showMyComments(getActivity(), mUid);
                break;
            case R.id.rl_likes_user_space:
                UIHelper.showMyLike(getActivity(), mUid);
                break;
            case R.id.rl_setting_user_space:
                UIHelper.showSetting(getActivity());
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregist(this);
        if (mReceiver != null && registed) {
            getActivity().unregisterReceiver(mReceiver);
            registed = false;
        }
    }

    public void onEvent(MineManager.ReceivedMessage message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setNewMessageRedPoint();
            }
        });
    }

    public void onEvent(MineManager.NewChatMessageEvent message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setChatRedPoint();
            }
        });
    }

}
