package com.moinapp.wuliao.modules.login;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.fragment.FriendsFragment;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.login.model.HotUser;
import com.moinapp.wuliao.modules.login.model.ThirdInfo;
import com.moinapp.wuliao.modules.mine.adapter.HotUserAdapter;
import com.moinapp.wuliao.ui.InviteFriendDialog;
import com.moinapp.wuliao.widget.AvatarView;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 注册成功后的热门用户页面
 * @author liujiancheng
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class HotUserFragment extends BaseFragment {
    private static ILogger MyLog = LoggerFactory.getLogger("HotUserFragment");

    protected static final String TAG = FriendsFragment.class.getSimpleName();

    private String mUid;
    private String mUserName;
    private String mAvtarUrl;
    private String mSex;
    private int mJump;
    private HotUserAdapter mAdapter;
    private List<HotUser> mUserList;
    private ThirdInfo mToken;

//    @InjectView(R.id.title_layout)
//    protected CommonTitleBar title;
    @InjectView(R.id.listview)
    protected ListView mListView;

    private View mInviteButton;
    @Override
    public void initView(View view) {
        super.initView(view);

        View headerView = View.inflate(getActivity(), R.layout.layout_header_hot_user, null);
        TextView enter = (TextView) headerView.findViewById(R.id.enter);
        TextView username = (TextView) headerView.findViewById(R.id.user_name);
        AvatarView avatar = (AvatarView) headerView.findViewById(R.id.iv_avatar);
        ImageView gender = (ImageView) headerView.findViewById(R.id.iv_sex);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.gotoDiscovery();
            }
        });

        mListView.addHeaderView(headerView);
        mListView.setAdapter(mAdapter);

        mInviteButton= LayoutInflater.from(getActivity()).inflate(
                R.layout.view_invite_button, null);
//        mListView.addFooterView(mInviteButton);
        mInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleInvite();
            }
        });
        sendRequestData();

        // 显示用户名 头像和性别
        username.setText(mUserName);
        setAvatar(avatar);
        if (isMale()) {
            gender.setImageResource(R.drawable.male_yellow);
        } else {
            gender.setImageResource(R.drawable.female_red);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        initView(view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mUid = args.getString(Constants.BUNDLE_KEY_UID);
            mSex = args.getString(Constants.BUNDLE_KEY_SEX);
            mUserName = args.getString(Constants.BUNDLE_KEY_USERNAME);
            mAvtarUrl = args.getString(Constants.BUNDLE_KEY_AVATAR);
            mJump = args.getInt(Constants.BUNDLE_KEY_JUMP, 0);
            mToken = (ThirdInfo)args.getSerializable(Constants.BUNDLE_KEY_TOKEN);
        }

        mAdapter = new HotUserAdapter(getActivity(), mJump);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_hot_user_list;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.HOT_USER_FRAGMENT); //统计页面
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.HOT_USER_FRAGMENT);
    }

    protected void sendRequestData() {
        LoginManager.getInstance().getHotUser(mUid, mSex, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                if (obj != null) {
                    addInviteButton();
                    mUserList = (List<HotUser>) obj;
                    mAdapter.addData(mUserList);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onErr(Object obj) {
                addInviteButton();
            }

            @Override
            public void onNoNetwork() {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean onBackPressed() {
        MyLog.i("back key pressed!!!");
        mAdapter.gotoDiscovery();
        return false;
    }

    private void handleInvite() {
        final InviteFriendDialog dialog = new InviteFriendDialog(getActivity());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setSinaToken(mToken);
        dialog.show();
    }

    private void addInviteButton() {
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isAdded() && mListView != null) {
                        mListView.addFooterView(mInviteButton);
                    }
                }
            });
        }
    }

    private void setAvatar(AvatarView avatar) {
        if (avatar != null) {
            if (TextUtils.isEmpty(mAvtarUrl)) {
                if (isMale()) {
                    avatar.setImageResource(R.drawable.head_male);
                } else {
                    avatar.setImageResource(R.drawable.head_female);
                }
            } else {
                avatar.setAvatarUrl(mAvtarUrl);
            }
        }
    }


    private boolean isMale() {
        return "male".equalsIgnoreCase(mSex) || "男".equalsIgnoreCase(mSex);
    }
}
