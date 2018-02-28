package com.moinapp.wuliao.modules.stickercamera.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 修改权限界面
 */
public class WriteAuthFragment extends BaseFragment {
    private static final ILogger MyLog = LoggerFactory.getLogger("waf");
    @InjectView(R.id.title_bar)
    CommonTitleBar commonTitleBar;
    @InjectView(R.id.auth_group)
    RadioGroup mRadioGroup;
    @InjectView(R.id.auth_all)
    RadioButton mRbAuthAll;
    @InjectView(R.id.auth_none)
    RadioButton mRbAuthNone;
    @InjectView(R.id.rl_auth_all)
    RelativeLayout mRlAuthAll;
    @InjectView(R.id.rl_auth_none)
    RelativeLayout mRlAuthNone;

    public static final String KEY_WRITE_AUTH = "KEY_WRITE_AUTH";
    private int mAuth = 4;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write_auth, container,
                false);
        ButterKnife.inject(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {

        try {
            MyLog.i("getArguments writeauth before = " + getArguments().getInt(KEY_WRITE_AUTH));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getArguments() != null) {
            mAuth = getArguments().getInt(KEY_WRITE_AUTH, 4);
        }
        MyLog.i("mAuth=" + mAuth);
        if (mAuth == 4) {
            mRadioGroup.check(mRbAuthAll.getId());
        } else if (mAuth == 1) {
            mRadioGroup.check(mRbAuthNone.getId());
        }
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == mRbAuthAll.getId()) {
                    authOK(4);
                } else if (checkedId == mRbAuthNone.getId()) {
                    authOK(1);
                }
            }
        });
        mRbAuthAll.setOnClickListener(v -> {
            auth(4);
        });
        mRbAuthNone.setOnClickListener(v -> {
            auth(1);
        });
        mRlAuthAll.setOnClickListener(v -> {
            auth(4);
        });
        mRlAuthNone.setOnClickListener(v -> {
            auth(1);
        });
    }

    @Override
    public void initData() {
        commonTitleBar.setLeftBtnOnclickListener(v -> {
            authOK(mAuth);
        });
    }

    private void auth(int auth) {
        MyLog.i("writeauth chg after = " + auth);
        mRadioGroup.clearCheck();
        mRadioGroup.check(auth == 4 ? mRbAuthAll.getId() : mRbAuthNone.getId());
    }

    private void authOK(int auth) {
        mAuth = auth;
        Intent intent = new Intent();
        Bundle b = new Bundle();
        b.putInt(KEY_WRITE_AUTH, mAuth);
        intent.putExtras(b);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.WRITE_AUTH_FRAGMENT); //统计页面，
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.WRITE_AUTH_FRAGMENT);
    }

}
