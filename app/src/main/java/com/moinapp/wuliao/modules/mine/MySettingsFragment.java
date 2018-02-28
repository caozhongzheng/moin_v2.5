package com.moinapp.wuliao.modules.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.moinapp.wuliao.AppConfig;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.AppManager;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.login.LoginManager;
import com.moinapp.wuliao.modules.login.model.GetUserTypeResult;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.MainActivity;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.togglebutton.ToggleButton;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 设置界面
 * 
 */
public class MySettingsFragment extends BaseFragment {

    @InjectView(R.id.ll_security)
    LinearLayout mLlSecurity;
    @InjectView(R.id.title_bar)
    CommonTitleBar mTitleBar;
    @InjectView(R.id.watermark_switch)
    ToggleButton mToggleWatermark;
    @InjectView(R.id.notification_switch)
    ToggleButton mToggleNewMsg;

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_settings, container,
                false);
        ButterKnife.inject(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        setToggleChanged(mToggleWatermark, MinePreference.KEY_SAVE_WATERMARK);
        setToggle(mToggleWatermark, MinePreference.getInstance().isSaveWatermark());
        mToggleWatermark.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                toggleMark();
            }
        });

        setToggleChanged(mToggleNewMsg, MinePreference.KEY_NOTIFY_NEW_MSG);
        setToggle(mToggleNewMsg, MinePreference.getInstance().isNotifyNewMsg());
        mToggleNewMsg.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                toggleNewMsg();
            }
        });

        view.findViewById(R.id.rl_security).setOnClickListener(this);
        view.findViewById(R.id.rl_notification_settings).setOnClickListener(
                this);
        view.findViewById(R.id.rl_sticker).setOnClickListener(this);
        view.findViewById(R.id.rl_feedback).setOnClickListener(this);
        view.findViewById(R.id.rl_watermark).setOnClickListener(this);
        view.findViewById(R.id.rl_message_notification).setOnClickListener(this);
        view.findViewById(R.id.rl_about).setOnClickListener(this);
        view.findViewById(R.id.rl_exit).setOnClickListener(this);
    }

    @Override
    public void initData() {
        // TODO 三方登录的情况下不显示账户安全
        LoginManager.getInstance().getUserLoginType(new IListener() {
            @Override
            public void onSuccess(Object obj) {
                if (((GetUserTypeResult) obj).getType() != 1) {
                    mLlSecurity.setVisibility(View.GONE);
                } else {
                    mLlSecurity.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onErr(Object obj) {

            }

            @Override
            public void onNoNetwork() {

            }
        });
        mTitleBar.setLeftBtnOnclickListener(v -> {
            getActivity().finish();
        });
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
        case R.id.rl_security:
            UIHelper.showSettingSecurity(getActivity());
            break;
        case R.id.rl_notification_settings:
            UIHelper.showSettingNotification(getActivity());
            break;
        case R.id.rl_sticker:
//            onClickCleanCache();
            break;
        case R.id.rl_feedback:
            UIHelper.showSettingFeedback(getActivity());
            break;
        case R.id.rl_watermark:
            toggleMark();
            break;
        case R.id.rl_message_notification:
            toggleNewMsg();
//            UIHelper.showNewMsgNotifyStyle(getActivity());
            break;
        case R.id.rl_about:
            if (AppConfig.isDebug()) {
                UIHelper.showDebugFragment(getActivity());
            } else {
                UIHelper.showAbout(getActivity());
            }
            break;
        case R.id.rl_exit:
            onClickExit();
            break;
        default:
            break;
        }

    }

    /**修改水印开关状态*/
    private void toggleMark() {
        boolean b = !MinePreference.getInstance().isSaveWatermark();
        MinePreference.getInstance().setIsSaveWatermark(b);
        setToggle(mToggleWatermark, b);
        MineManager.getInstance().updateSettings(MinePreference.getInstance().isNotifyNewMsg() ? 1 : 0, b ? 1 : 0, null);
    }


    /**修改消息提醒开关状态*/
    private void toggleNewMsg() {
        boolean b = !MinePreference.getInstance().isNotifyNewMsg();
        MinePreference.getInstance().setIsNotifyNewMsg(b);
        setToggle(mToggleNewMsg, b);
        MineManager.getInstance().updateSettings(b ? 1 : 0, MinePreference.getInstance().isSaveWatermark() ? 1 : 0, null);
    }

    /**设置开关状态*/
    private void setToggle(ToggleButton tb, boolean value) {
        if (value) {
            tb.setToggleOn();
        } else {
            tb.setToggleOff();
        }
    }

    /**设置监听*/
    private void setToggleChanged(ToggleButton tb, final String key) {
        tb.setOnToggleChanged(new ToggleButton.OnToggleChanged() {

            @Override
            public void onToggle(boolean on) {
                MinePreference.getInstance().setIsSaveWatermark(on);
            }
        });
    }

    private void onClickExit() {
        requestLogout();
        AppContext
                .set(AppConfig.KEY_NOTIFICATION_DISABLE_WHEN_EXIT,
                        false);
        AppContext.getInstance().Logout();

//        AppManager.getAppManager().AppExit(getActivity());
        AppManager.getAppManager().finishActivity();

        //跳转到发现
        UIHelper.gotoMain(getActivity(), MainActivity.KEY_TAB_DISCOVERY, true);
    }

    private void requestLogout() {
        LoginManager.getInstance().userLogout(new IListener() {
            @Override
            public void onSuccess(Object obj) {

            }

            @Override
            public void onErr(Object obj) {

            }

            @Override
            public void onNoNetwork() {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.MY_SETTINGS_FRAGMENT); //统计页面
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.MY_SETTINGS_FRAGMENT);
    }
}