package com.moinapp.wuliao.modules.discovery.result;

import com.moinapp.wuliao.bean.BaseFile;
import com.moinapp.wuliao.bean.BaseHttpResponse;

/**
 * Created by liujiancheng on 15/10/13.
 */
public class UpdateCosplayResult extends BaseHttpResponse {
    private BaseFile file;

    public BaseFile getFile() {
        return file;
    }

    public void setFile(BaseFile file) {
        this.file = file;
    }
}
