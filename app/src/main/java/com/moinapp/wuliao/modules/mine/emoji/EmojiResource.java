package com.moinapp.wuliao.modules.mine.emoji;

import com.moinapp.wuliao.bean.BaseImage;

import java.io.Serializable;
import java.util.List;

/**
 * 表情专辑的详细信息
 * Created by liujiancheng on 15/9/16.
 */
public class EmojiResource implements Serializable {
    private String id;//表情专辑的主键id
    private String ipid;//对应的ipResource的主键
    private String name;//表情专辑名称
    private MoinPictures pics;//表情专辑图片 cover：IP详情中的大图  banner: 表情专辑中的顶部大图 suggest: 相关推荐中的缩略图
    private BaseImage icon;//表情专题的icon，发帖时在键盘中能看到
    private String author;//作者信息
    private List<EmojiInfo> emojis;
    private int emojiNum;//包含的表情对象数量
    private int size;//表情包大小字节数
    private String shareUrl;//分享链接地址，key 是平台名字，android, ios,wp,firefox等 value 是对应的地址
    private int shareCount;//被分享的次数
    private int favoriteCount;//被收藏的次数
    private int likeCount;//被点赞的次数
    private String desc;//专辑的描述信息
    private boolean isDownload;//是否被当前用户下载
    private boolean isLike;//是否被当前用户点赞
    private long updatedAt;

    public String getEmojiId() {
        return id;
    }

    public void setEmojiId(String id) {
        this.id = id;
    }

    public String getIpid() {
        return ipid;
    }

    public void setIpid(String ipId) {
        this.ipid = ipId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MoinPictures getPics() {
        return pics;
    }

    public void setPics(MoinPictures pics) {
        this.pics = pics;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<EmojiInfo> getEmojis() {
        return emojis;
    }

    public void setEmojis(List<EmojiInfo> emojis) {
        this.emojis = emojis;
    }

    public int getEmojiNum() {
        return emojiNum;
    }

    public void setEmojiNum(int emojiNum) {
        this.emojiNum = emojiNum;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public BaseImage getIcon() {
        return icon;
    }

    public void setIcon(BaseImage icon) {
        this.icon = icon;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setIsDownload(boolean isDownload) {
        this.isDownload = isDownload;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
