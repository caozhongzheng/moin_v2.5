package com.moinapp.wuliao.modules.discovery.result;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.modules.discovery.model.BannerInfo;

import java.util.List;

/**
 * Created by liujiancheng on 15/12/30.
 */
public class GetBannerResult extends BaseHttpResponse {
    private List<BannerInfo> banners;

    public List<BannerInfo> getBanners() {
        return banners;
    }

    public void setBanners(List<BannerInfo> banners) {
        this.banners = banners;
    }
}
