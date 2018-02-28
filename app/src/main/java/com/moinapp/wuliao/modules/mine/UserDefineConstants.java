package com.moinapp.wuliao.modules.mine;

/**
 * 用户的级别／关／权限系定义
 * Created by liujiancheng on 15/9/10.
 */
public class UserDefineConstants {
    /* *********************任意用户和当前用户的关系定义 ************************/
    /**
     * 无任何关系
     */
    public final static int FRIENDS_NOTHING = 0;

    /**
     * 我的粉丝
     */
    public final static int FRIENDS_FANS = 1;

    /**
     * 我关注的人
     */
    public final static int FRIENDS_FOLLOWERS = 2;

    /**
     * 我的朋友（互粉，即是关注者又是粉丝）
     */
    public final static int FRIENDS_FANS_FOLLOWERS = 3;

    /**
     * 我自己
     */
    public final static int FRIENDS_SELF = 4;

    public final static int FOLLOW = 1;//加关注
    public final static int FOLLOWED = 2;//已关注
    public final static int FOLLOW_EACH_OTHER = 3;//互相关注
    public final static int SELF = 4;//自己
    public final static int HIDE = 5;//隐藏


    /**
     * 表示临时的已经关注状态,再次点击消失的一种临时状态
     */
    public final static int FRIENDS_FOLLOW_ALREADY = 100;
    /**
     * 表示临时的已经互相关注状态,再次点击消失的一种临时状态
     */
    public final static int FRIENDS_FOLLOW_EACH_OTHER_ALREADY = 101;
    /* *********************用户级别的定义 ************************/
    /**
     * 普通用户
     */
    public final static int NORMAL_USER = 1;

    /**
     *  MOIN 运营用户
     */
    public final static int OPERATION_USER = 2;

    /**
     *  MOIN 超级用户
     */
    public final static int MOIN_SUPER_USER = 3;

    /* *********************关注状态变化的来源页面 ************************/
    public final static int FOLLOW_USER_CENTER = 1;
    public final static int FOLLOW_MY_FOLLOWS_FANS = 2;

    public final static int FOLLOW_COMMENT_LIST = 3;
    public final static int FOLLOW_LIKE_LIST = 4;
    public final static int FOLLOW_OTHER_FOLLOWS_FANS = 5;

    public final static int FOLLOW_SEARCH = 6;
    public final static int FOLLOW_MESSAGE = 7;
    public final static int FOLLOW_COSPLAY = 8;
    public final static int INVITE_LIST = 9;

    public final static int ALIAS_UND = -1;//放弃备注
    public final static int ALIAS_DEL = 0;//删除备注
    public final static int ALIAS_ADD = 1;//添加备注
    public final static int ALIAS_CHG = 2;//修改备注
    public final static int ALIAS_LNG = 3;//备注过长
    public final static int ALIAS_INV = 4;//备注不合格
}
