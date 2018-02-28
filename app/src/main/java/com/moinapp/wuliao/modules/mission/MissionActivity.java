package com.moinapp.wuliao.modules.mission;

import com.moinapp.wuliao.AppConfig;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.ui.WebViewActivity;

/**
 * Created by guyunfei on 16/7/25.14:13.
 * 每日任务页面
 */
public class MissionActivity extends WebViewActivity {
    private ILogger MyLog = LoggerFactory.getLogger(MissionActivity.class.getSimpleName());

    @Override
    protected String getUrl() {
//        return AppConfig.getBaseShareUrl() + "web/test/viewdaytask";
        return "http://mp.moinapp.com/moinjc/daily_quests.html";
    }

    @Override
    protected void handleShare() {

    }

    @Override
    protected void initView() {
        super.initView();
//        titleBar.setTitleTxt("每日任务");
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
