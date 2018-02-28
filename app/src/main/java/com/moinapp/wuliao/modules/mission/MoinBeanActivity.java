package com.moinapp.wuliao.modules.mission;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.ui.WebViewActivity;

/**
 * Created by liujiancheng on 16/7/25
 * 我的魔豆页面
 */
public class MoinBeanActivity extends WebViewActivity {
    private ILogger MyLog = LoggerFactory.getLogger(MoinBeanActivity.class.getSimpleName());
    @Override
    protected String getUrl() {
//        return AppConfig.getBaseShareUrl() + "web/test/viewMoincoins";
        return "http://mp.moinapp.com/moinjc/mygold.html";
    }

    @Override
    protected void handleShare() {

    }

    @Override
    protected void initView() {
        super.initView();
//        titleBar.setTitleTxt("魔豆页");
        hideRightButton();
    }

    @Override
    protected boolean isNoCache() {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (AppContext.getInstance().isLogin()) {
//            refresh();
//        }
//        MyLog.i("onResume...reload url");
    }
}
