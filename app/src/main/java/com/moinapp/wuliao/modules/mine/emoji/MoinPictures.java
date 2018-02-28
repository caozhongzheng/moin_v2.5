package com.moinapp.wuliao.modules.mine.emoji;

import com.moinapp.wuliao.bean.BaseImage;

import java.io.Serializable;

/**
 * IP首页大图等信息[2.5]->贴纸包图片[3.*]
 * Created by liujiancheng on 15/5/12.
 */
public class MoinPictures implements Serializable {
    private BaseImage cover;//封面 热门推荐封面图，点击进入详情页面
    private BaseImage clip;//片花 热门推荐封面图，点击进入片花列表页面
    private BaseImage poster;//海报 热门推荐封面图，点击进入海报列表页面
    private BaseImage screen;//剧照 热门推荐封面图，点击进入剧照列表页面
    private BaseImage emoji;//表情 热门推荐封面图，点击进入表情详情页面
    private BaseImage banner;//预留缩略图 IP详情图片，对应详情中Banner部分的图片
    private BaseImage suggest;//相关推荐的图

    public BaseImage getCover() {
        return cover;
    }

    public void setCover(BaseImage cover) {
        this.cover = cover;
    }

    public BaseImage getBanner() {
        return banner;
    }

    public void setBanner(BaseImage thumb) {
        this.banner = thumb;
    }

    public BaseImage getPoster() {
        return poster;
    }

    public void setPoster(BaseImage poster) {
        this.poster = poster;
    }

    public BaseImage getEmoji() {
        return emoji;
    }

    public void setEmoji(BaseImage emoji) {
        this.emoji = emoji;
    }

    public BaseImage getClip() {
        return clip;
    }

    public void setClip(BaseImage clip) {
        this.clip = clip;
    }

    public BaseImage getScreen() {
        return screen;
    }

    public void setScreen(BaseImage screen) {
        this.screen = screen;
    }

    public BaseImage getSuggest() {
        return suggest;
    }

    public void setSuggest(BaseImage suggest) {
        this.suggest = suggest;
    }
}
