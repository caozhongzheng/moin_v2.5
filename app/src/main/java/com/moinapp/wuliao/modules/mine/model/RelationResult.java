package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;

/**
 * 获取用户关系结果
 * Created by liujiancheng on 15/9/10.
 */
public class RelationResult extends BaseHttpResponse {
    private int relation;

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }
}
