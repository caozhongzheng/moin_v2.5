package com.moinapp.wuliao.modules.mine;

import android.view.View;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.bean.UmengConstants;
import com.umeng.analytics.MobclickAgent;

/**
 * 服务协议
 * Created by moying on 15/5/6.
 */
public class AgreementActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_agreement;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.AGREEMENT_ACTIVITY); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.AGREEMENT_ACTIVITY); //
        MobclickAgent.onPause(this);
    }
}
