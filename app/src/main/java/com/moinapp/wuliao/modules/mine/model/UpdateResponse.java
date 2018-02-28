package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.modules.update.model.App;

/**
 * 获取新版本的结果
 * Created by liujiancheng on 15/9/10.
 */
public class UpdateResponse extends BaseHttpResponse {
    private App app;

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }
}
