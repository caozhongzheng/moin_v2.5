package com.moinapp.wuliao.bean;

/**
 * Created by liujiancheng on 15/12/22.
 */
public class FollowStatusChange {
    private String mUid;
    private int mNewRelation;

    public FollowStatusChange() {
    }

    public FollowStatusChange(String uid, int relation) {
        this.mUid = uid;
        this.mNewRelation = relation;
    }

    public String getUid() {
        return mUid;
    }

    public int getNewRelation() {
        return mNewRelation;
    }

    @Override
    public String toString() {
        return "FollowStatusChange{" +
                "mUid='" + mUid + '\'' +
                ", mNewRelation=" + mNewRelation +
                '}';
    }
}
