package com.moinapp.wuliao.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.bean.FollowStatusChange;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.interf.FollowChangedCallback;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.mine.UserDefineConstants;
import com.moinapp.wuliao.modules.mine.model.FollowActionResult;
import com.moinapp.wuliao.modules.mine.model.FollowResultList;
import com.moinapp.wuliao.modules.stickercamera.base.util.DialogHelper;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.Tools;
import com.moinapp.wuliao.util.UIHelper;
import com.umeng.analytics.MobclickAgent;

/** 关注,取关,互相关注按钮
 * Created by moin on 16/4/12
 */
public class FollowView extends LinearLayout implements View.OnClickListener {
    private ILogger MyLog = LoggerFactory.getLogger(FollowView.class.getSimpleName());
    /**
     * 情况1: 关注后点击可以取消关注
     */
    private final static int CLICK_CANEL_FOLLOW = 1;

    /**
     * 情况2: 关注后点击后要消失
     */
    private final static int CLICK_GONE = 2;

    /**
     * 情况3: 关注后点击后无反应
     */
    private final static int CLICK_NONE = 3;

    private Context mContext;
    private int mRelation;
    private int mCase;
    private FollowChangedCallback mCallback;

    //用户UID
    private UserInfo mUser;

    private TextView mTvFollow;
    private ImageView mIvAddFollow;
    private View mBackground;


    public FollowView(Context context) {
        super(context);
        initUI(context, null);
    }

    public FollowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI(context, attrs);
    }

    public FollowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI(context, attrs);
    }

    private void initUI(Context context, AttributeSet attrs) {
        mContext = context;
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_follow_view, this, true);
        mTvFollow = (TextView) rootView.findViewById(R.id.tv_follow);
        mIvAddFollow = (ImageView) rootView.findViewById(R.id.iv_addfollow);
        mBackground = rootView.findViewById(R.id.ll_follow);

        mBackground.setOnClickListener(this);
    }

    public void setBackgroundImage(int resid) {
        mBackground.setBackgroundResource(resid);
    }

    public void setLikeImage(int resid) {
        if (resid == 0) {
            mIvAddFollow.setVisibility(GONE);
        } else {
            mIvAddFollow.setVisibility(VISIBLE);
            MarginLayoutParams params = (MarginLayoutParams) mIvAddFollow.getLayoutParams();
            params.rightMargin = BaseApplication.context().getResources().getDimensionPixelOffset(R.dimen.iv_addfollow_right_margin);
            mIvAddFollow.setBackgroundResource(resid);
        }
    }

    /**
     * 使用时必须初始化userinfo
     */
    public void init(UserInfo user, int relation, int from) {
        EventBus.getDefault().register(this);

        this.mUser = user;
        this.mRelation = relation;
        convertCase(from);
        updateViewByStatus();
    }

    public void setFollowChangedCallback(FollowChangedCallback callback) {
        this.mCallback = callback;
    }

    private void convertCase(int from) {
        switch (from) {
            case UserDefineConstants.FOLLOW_USER_CENTER:
            case UserDefineConstants.FOLLOW_MY_FOLLOWS_FANS:
                mCase = CLICK_CANEL_FOLLOW;
                break;
            case UserDefineConstants.FOLLOW_COMMENT_LIST:
            case UserDefineConstants.FOLLOW_LIKE_LIST:
            case UserDefineConstants.FOLLOW_OTHER_FOLLOWS_FANS:
                mCase = CLICK_GONE;
                break;
            case UserDefineConstants.FOLLOW_SEARCH:
            case UserDefineConstants.FOLLOW_MESSAGE:
            case UserDefineConstants.FOLLOW_COSPLAY:
            case UserDefineConstants.INVITE_LIST:
                mCase = CLICK_NONE;
                break;
        }
    }

    private void updateViewByStatus() {
        mUser.setRelation(mRelation);
        setVisibility(VISIBLE);
        try {
            MyLog.i("updateViewByStatus, user[" + mUser.getUsername() + ", alias=" + mUser.getAlias() + "] mRelation=" + mRelation);
        } catch (Exception e) {
            MyLog.e(e);
        }
        switch (mRelation) {
            case UserDefineConstants.FRIENDS_NOTHING: // 0
            case UserDefineConstants.FRIENDS_FANS: // 1
                // 加关注
                setFocusStatus(UserDefineConstants.FOLLOW);
                break;
            case UserDefineConstants.FRIENDS_FOLLOWERS: // 2
                if (mCase == CLICK_GONE) {
                    // 点击消失[不设置背景]
                    setFocusStatus(UserDefineConstants.HIDE);
                } else {
                    // 已关注
                    setFocusStatus(UserDefineConstants.FOLLOWED);
                }
                break;
            case UserDefineConstants.FRIENDS_FANS_FOLLOWERS: // 3
                if (mCase == CLICK_GONE) {
                    // 点击消失[不设置背景]
                    setFocusStatus(UserDefineConstants.HIDE);
                } else {
                    // 互相关注
                    setFocusStatus(UserDefineConstants.FOLLOW_EACH_OTHER);
                }
                break;
            case UserDefineConstants.FRIENDS_FOLLOW_ALREADY: // 100
                // 已关注
                setFocusStatus(UserDefineConstants.FOLLOWED);
                break;
            case UserDefineConstants.FRIENDS_FOLLOW_EACH_OTHER_ALREADY: // 101
                // 互相关注
                setFocusStatus(UserDefineConstants.FOLLOW_EACH_OTHER);
                break;
            case UserDefineConstants.FRIENDS_SELF:
                // 自己[不设置背景]
                setFocusStatus(UserDefineConstants.SELF);
                break;
            default:
                // 加关注
                setFocusStatus(UserDefineConstants.FOLLOW);
                break;
        }
    }

    /**设置关注状态*/
    private void setFocusStatus(int relation) {
        try {
            MyLog.i("setFocusStatus...user=" + mUser.getUsername() + ", alias=" + mUser.getAlias());
        } catch (Exception e) {
            MyLog.e(e);
        }
        setBackgroundImage(R.drawable.long_boreder_gray);
        mTvFollow.setTextColor(getResources().getColor(R.color.invited));
        if (relation == UserDefineConstants.FOLLOW) {
            setLikeImage(R.drawable.plus_pink);
        } else {
            setLikeImage(0);
        }
        switch (relation) {
            case UserDefineConstants.FOLLOW:
                mTvFollow.setText(R.string.follow);
                mTvFollow.setTextColor(getResources().getColor(R.color.moin));
                setBackgroundImage(R.drawable.long_border_pink);
                break;
            case UserDefineConstants.FOLLOWED:
                mTvFollow.setText(R.string.followed);
                break;
            case UserDefineConstants.FOLLOW_EACH_OTHER:
                mTvFollow.setText(R.string.followed_each_other);
                break;
            case UserDefineConstants.SELF:
                setVisibility(GONE);
                break;
            case UserDefineConstants.HIDE:
                mTvFollow.setText("");
                setBackgroundImage(0);
                break;
        }
    }

    private void processFollow(int action) {
        if (mUser == null) {
            return;
        }
        MineManager.getInstance().followUser(mUser.getUId(), action, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                FollowResultList actionResultList = (FollowResultList) obj;
                if (actionResultList == null || actionResultList.getFollow() == null) {
                    return;
                }
                MyLog.i(mUser.getUId() + ", followUser-->" + (action == 1 ? "关注" : "取消关注") + " succeed!");
                for (FollowActionResult result : actionResultList.getFollow()) {
                    if (result.getResult() == 0 || result.getUserid() == null) {
                        continue;
                    }
                    if (result.getUserid().equalsIgnoreCase(mUser.getUId())) {
                        int newRelation = result.getRelation();
                        MobclickAgent.onEvent(mContext, action == 1 ? UmengConstants.USER_FOLLOW : UmengConstants.USER_UNFOLLOW);

                        //如果不是自己的列表,设为FOLLOW_ALREADY
                        if (mCase == CLICK_GONE) {
                            if (newRelation == UserDefineConstants.FRIENDS_FOLLOWERS) { // 2
                                newRelation = UserDefineConstants.FRIENDS_FOLLOW_ALREADY; // 100
                            } else if (newRelation == UserDefineConstants.FRIENDS_FANS_FOLLOWERS) { // 3
                                newRelation = UserDefineConstants.FRIENDS_FOLLOW_EACH_OTHER_ALREADY; // 101
                            }
                        }
                        MyLog.i(mUser.getUId() + ", followUser-->post FollowStatusChange event newRelation=" + newRelation + " succeed!");
                        EventBus.getDefault().post(new FollowStatusChange(mUser.getUId(), newRelation));
                        break;
                    }
                }
            }

            @Override
            public void onErr(Object obj) {
                AppContext.toast(mContext, (action == 1 ? "关注" : "取消关注") + "失败~");
                MyLog.i(mUser.getUId() + ", followUser-->" + (action == 1 ? "关注" : "取消关注") + " failed!");
            }

            @Override
            public void onNoNetwork() {
                AppContext.toast(mContext, getResources().getString(R.string.no_network));
                MyLog.i(getResources().getString(R.string.no_network));
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == mBackground.getId()) {
            onFollowChange();
        }
    }

    public void onFollowChange() {
        Context context = getContext();
        // 未登陆提示登陆
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(context);
            return;
        }

        if (Tools.isFastDoubleClick()) {
            return;
        }


        setEnabled(true);
        switch (mRelation) {
            case UserDefineConstants.FRIENDS_NOTHING:
            case UserDefineConstants.FRIENDS_FANS:
                processFollow(1);
                break;
            case UserDefineConstants.FRIENDS_FOLLOWERS:
            case UserDefineConstants.FRIENDS_FANS_FOLLOWERS:
                if (mCase == CLICK_CANEL_FOLLOW) {
                    DialogHelper dialogHelper = new DialogHelper((Activity) context);
                    dialogHelper.alert4M(null, "真的要取消关注吗？",
                            "是", new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    processFollow(0);
                                    dialogHelper.dialogDismiss();
                                }
                            }, "否", new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogHelper.dialogDismiss();
                                }
                            }, false);
                }
                break;
            case UserDefineConstants.FRIENDS_FOLLOW_EACH_OTHER_ALREADY: // 101
                if (mCase == CLICK_GONE) {
                    mRelation = UserDefineConstants.FRIENDS_FANS_FOLLOWERS; // 3
                    updateViewByStatus();
                    EventBus.getDefault().post(new FollowStatusChange(mUser.getUId(), mRelation));
                }
                break;
            case UserDefineConstants.FRIENDS_FOLLOW_ALREADY:
                if (mCase == CLICK_GONE) {
                    mRelation = UserDefineConstants.FRIENDS_FOLLOWERS;
                    updateViewByStatus();
                    EventBus.getDefault().post(new FollowStatusChange(mUser.getUId(), mRelation));
                }
                break;
            default:
                break;
        }
    }

    public void onEvent(FollowStatusChange status) {
        if (status == null || StringUtil.isNullOrEmpty(status.getUid())
                || !mUser.getUId().equals(status.getUid())) {
            return;
        }
        MyLog.i("FollowView onEvent: status.getUID=" + status.getUid() + ", new Relation=" + status.getNewRelation());
        if (mUser.getUId().equalsIgnoreCase(status.getUid())) {
            mRelation = status.getNewRelation();
            MyLog.i("FollowView, update ui...new mRelation=" + mRelation);
            //注意要在ui线程去通知更新ui
            new Handler(BaseApplication.context().getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    updateViewByStatus();
                }
            });

        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregist(this);
    }

}
