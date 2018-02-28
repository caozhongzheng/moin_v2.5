package com.moinapp.wuliao.commons.init.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.BaseImage;

/**
 * Created by liujiancheng on 15/12/30.
 */
public class GetBootImageResult extends BaseHttpResponse {
    private BootPicture bootPicture;

    public BootPicture getBootPicture() {
        return bootPicture;
    }

    public void setBootPicture(BootPicture bootPicture) {
        this.bootPicture = bootPicture;
    }
}
