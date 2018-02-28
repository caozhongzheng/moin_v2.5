package com.moinapp.wuliao.modules.mission;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.MoinBean;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.AvatarView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by liujiancheng on 16/7/25
 * 魔豆pk榜
 */
public class MoinBeanPKFragment extends Fragment {

    private UserInfo mUserInfo;

    @InjectView(R.id.title_layout)
    protected CommonTitleBar titleBar;
    @InjectView(R.id.avatar)
    protected AvatarView avatar;
    @InjectView(R.id.moin_bean_num)
    protected TextView moinBeanNum;
    @InjectView(R.id.beat_num)
    protected TextView moinBeanBeatNum;
    @InjectView(R.id.earn_bean)
    protected Button earnBtn;
    @InjectView(R.id.moin_bean_pk_layout)
    protected LinearLayout pk_layout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arg = getArguments();
        if (arg != null) {
            mUserInfo = (UserInfo) arg.getSerializable(Constants.BUNDLE_KEY_USERINFO);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moin_bean_pk, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        initView();
        initData();
    }

    private void initData() {
        MineManager.getInstance().getMoinBean(mUserInfo.getUId(), new IListener() {
            @Override
            public void onSuccess(Object obj) {
                if (obj != null) {
                    MoinBean moinBean = (MoinBean) obj;
                    if (moinBean != null) {
                        moinBeanNum.setText(moinBean.getTotalBean() + "");
                        pk_layout.setVisibility(View.VISIBLE);
                        if (moinBean.getPkRank() != null) {
                            moinBeanBeatNum.setText(moinBean.getPkRank() + "%");
                        } else {
                            moinBeanBeatNum.setText("0%");
                        }
                    } else {
                        moinBeanNum.setText("0");
                        moinBeanBeatNum.setText("0%");
                    }
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

    private void initView() {
        if (mUserInfo == null) {
            return;
        }
        if (mUserInfo.getAvatar() == null || StringUtil.isNullOrEmpty(mUserInfo.getAvatar().getUri())) {
        } else {
            avatar.setAvatarUrl(mUserInfo.getAvatar().getUri());
        }

        earnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showMissionActivity(getActivity());
            }
        });

        titleBar.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }
}