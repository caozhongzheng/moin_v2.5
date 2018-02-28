package com.moinapp.wuliao.modules.discovery.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 大咖秀图片的转发路径
 * Created by liujiancheng on 15/10/9.
 */
public class CosplayPath implements Serializable {
    /**
     * 原创节点
     */
    private NodeInfo root;

    /**
     * 前作节点
     */
    @SerializedName("parent")
    private NodeInfo previous;

    /**
     * 当前节点
     */
    private NodeInfo self;

    /**
     * 转发节点
     */
    private List<NodeInfo> children;

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

}
