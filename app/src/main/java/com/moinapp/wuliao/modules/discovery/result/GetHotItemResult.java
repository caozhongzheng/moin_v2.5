package com.moinapp.wuliao.modules.discovery.result;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.HotItem;

import java.util.List;

/**
 * Created by liujiancheng on 15/10/10.
 */
public class GetHotItemResult extends BaseHttpResponse {
    private List<HotItem> list;

    public List<HotItem> getList() {
        return list;
    }

    public void setList(List<HotItem> list) {
        this.list = list;
    }
}
