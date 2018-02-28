package com.moinapp.wuliao.bean;

import java.io.Serializable;

/**
 * Created by liujiancheng on 16/7/20.
 * 魔豆类
 */
public class MoinBean implements Serializable {
    /**
     * 总的魔豆数量
     */
    private int totalBean;

    /**
     * 当前排名
     */
    private int rank;

    /**
     * 击败了百分之多少的用户
     */
    private String pkRank;

    /**
     * 当前任务名称, 即赢取魔豆的任务名称,比如分享 评论 发图发帖 点赞等
     */
    private String taskName;

    /**
     * 获得的魔豆数量
     */
    private int obtainBean;

    public int getTotalBean() {
        return totalBean;
    }

    public void setTotalBean(int totalBean) {
        this.totalBean = totalBean;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getPkRank() {
        return pkRank;
    }

    public void setPkRank(String pkRank) {
        this.pkRank = pkRank;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getObtainBean() {
        return obtainBean;
    }

    public void setObtainBean(int obtainBean) {
        this.obtainBean = obtainBean;
    }

    @Override
    public String toString() {
        return "MoinBean{" +
                "taskName='" + taskName + '\'' +
                ", totalBean='" + totalBean + '\'' +
                ", obtainBean=" + obtainBean +
                ", pkRank=" + pkRank +
                '}';
    }


}
