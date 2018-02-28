package com.moinapp.wuliao.modules.discovery.result;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.modules.discovery.model.NodeInfo;

import java.util.List;

/**
 * Created by liujiancheng on 15/10/9.
 */
public class GetCosplayFamilyResult extends BaseHttpResponse {
    /**
     * 原创节点
     */
    private NodeInfo root;

    /**
     * 前作节点
     */
    private NodeInfo previous;

    /**
     * 自己
     */
    private NodeInfo self;

    /**
     * 转发节点
     */
    private List<NodeInfo> children;

    /**
     * 所有节点  v34版本时间轴页面要显示从原图开始的所有节点, 不再下发root/previous/self/children
     * 以后放到cosplayList
     */
    private List<NodeInfo> cosplayList;

    public NodeInfo getRoot() {
        return root;
    }

    public void setRoot(NodeInfo root) {
        this.root = root;
    }

    public NodeInfo getPrevious() {
        return previous;
    }

    public void setPrevious(NodeInfo previous) {
        this.previous = previous;
    }

    public NodeInfo getSelf() {
        return self;
    }

    public void setSelf(NodeInfo self) {
        this.self = self;
    }

    public List<NodeInfo> getChildren() {
        return children;
    }

    public void setChildren(List<NodeInfo> children) {
        this.children = children;
    }

    public boolean hasRoot() {
        return root != null;
    }

    public boolean hasPrevious() {
        return previous != null;
    }

    public boolean hasSelf() {
        return self != null;
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    public List<NodeInfo> getCosplayList() {
        return cosplayList;
    }

    public void setCosplayList(List<NodeInfo> cosplayList) {
        this.cosplayList = cosplayList;
    }
}
