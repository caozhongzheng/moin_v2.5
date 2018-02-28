package com.moinapp.wuliao.modules.discovery.model;

import com.moinapp.wuliao.bean.BaseImage;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerPackage;

import java.util.List;

/**
 * 话题对象
 * Created by liujiancheng on 15/12/30.
 */
public class TagPop extends Entity {
    /**
     * 话题id
     */
    private String id;

    /**
     * 名字
     */
    private String name;

    /**
     * english名字
     */
    private String enName;

    /**
     * 标签类型: 比如tp
     */
    private String type;

    /**
     * 标签近似色 形如：0x123456
     */
    private String categoryColor;

    /**
     * 参与的users
     */
    private List<UserInfo> users;

    /**
     * 包含的标签
     */
    private List<TagInfo> tags;

    /**
     * 包含的大咖秀图片数量
     */
    private int cosplayNum;

    /**
     * 点赞数量
     */
    private int likeNum;

    /**
     * 浏览数
     */
    private int readNum;

    /**
     * 订阅数
     */
    private int followNum;

    /**
     * 评论数量
     */
    private int commentNum;

    /**
     * 参与人数量
     */
    private int userNum;

    /**
     * 描述内容
     */
    private String desc;

    /**
     * 贴纸包对象
     */
    private StickerPackage sticker;

    /**
     * 封面图
     */
    private BaseImage icon;

    /**
     * 热门话题的封面图,进入更多话题列表后封面图用icon字段
     */
    private BaseImage hotIcon;

    /**
     * 是否已经订阅
     */
    private int isIdol;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类图标
     */
    private BaseImage categoryIcon;

    /**
     * 话题里面包含的置顶帖子.其中帖子的标签信息如「置顶」「精华」「达人」使用tags字段
     */
    private List<CosplayInfo> hotList;

    public String getTagPopId() {
        return id;
    }

    public void setTagPopId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(String categoryColor) {
        this.categoryColor = categoryColor;
    }

    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> members) {
        this.users = members;
    }

    public List<TagInfo> getTags() {
        return tags;
    }

    public void setTags(List<TagInfo> tags) {
        this.tags = tags;
    }

    public int getCosplayNum() {
        return cosplayNum;
    }

    public void setCosplayNum(int cosplayNum) {
        this.cosplayNum = cosplayNum;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public StickerPackage getSticker() {
        return sticker;
    }

    public void setSticker(StickerPackage sticker) {
        this.sticker = sticker;
    }

    public BaseImage getIcon() {
        return icon;
    }

    public void setIcon(BaseImage icon) {
        this.icon = icon;
    }

    public int getUserNum() {
        return userNum;
    }

    public void setUserNum(int userNum) {
        this.userNum = userNum;
    }

    public int getIsIdol() {
        return isIdol;
    }

    public void setIsIdol(int isIdol) {
        this.isIdol = isIdol;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BaseImage getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(BaseImage categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public int getReadNum() {
        return readNum;
    }

    public void setReadNum(int readNum) {
        this.readNum = readNum;
    }

    public int getFollowNum() {
        return followNum;
    }

    public void setFollowNum(int followNum) {
        this.followNum = followNum;
    }

    public BaseImage getHotIcon() {
        return hotIcon;
    }

    public void setHotIcon(BaseImage hotIcon) {
        this.hotIcon = hotIcon;
    }

    public List<CosplayInfo> getHotList() {
        return hotList;
    }

    public void setHotList(List<CosplayInfo> hotList) {
        this.hotList = hotList;
    }

    @Override
    public String toString() {
        return "TagPop{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", users=" + users +
                ", tags=" + tags +
                ", cosplayNum=" + cosplayNum +
                ", likeNum=" + likeNum +
                ", commentNum=" + commentNum +
                ", desc='" + desc + '\'' +
                ", sticker=" + sticker +
                ", icon=" + icon +
                '}';
    }
}
