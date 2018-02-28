package com.keyboard.bean;

import java.util.ArrayList;

public class EmoticonSetBean {
    /**
     * 表情专辑ID
     * 大咖秀的id==uid，则在切换用户时应该将大咖秀的EmoticonSetBean的这条记录重新delete/insert一下。
     */
    private String id;
    /**
     * 表情专辑所属的uid，用户标示哪个用户下载的表情专辑或者制作的大咖秀
     * 预制的表情的uid就以固定的string【当前是“00000000”】，这也是为了预制给所有用户使用。
     */
    private String uid;
    /**
     * 贴纸类型,v2.0:0 v2.5:1-6
     */
    private int stickType;
    /**
     * 表情的排序序号 预制表情的order设为10，大咖秀预制时为1，以后下载的表情都以max（order）+1开始。
     * 这个目的是为了以后表情做排序用。 order<10的表情可以不参加排序，这个也是类似微信的表情组排序一样。
     */
    private int order;
    /**
     * 表情集名称(必须且唯一)
     */
    private String name;
    /**
     * 每页行数
     */
    private int line;
    /**
     * 每页列数
     */
    private int row;
    /**
     * 表情集图标路径
     */
    private String iconUri;
    /**
     * 表情集图标路径URL
     */
    private String iconUrl;
    /**
     * 表情集图标名称
     */
    private String iconName;
    /**
     * 是否在每页最后一项显示删除按钮
     */
    private boolean isShowDelBtn;
    /**
     * 表情内间距
     */
    private int itemPadding;
    /**
     * 表情列间距
     */
    private int horizontalSpacing;
    /**
     * 表情行间距
     */
    private int verticalSpacing;
    /**
     * 表情包的flag  0:正常, 1:待更新/更新中 -1:已经下架
     */
    private int flag;
    /**
     * 表情的下载时间
     */
    private long updateTime;
    /**
     * 贴纸上一次检查时间
     */
    private long lastCheckTime;
    /**
     * 表情集数据源
     */
    private ArrayList<EmoticonBean> emoticonList;

    public EmoticonSetBean(){
    }

    public EmoticonSetBean(String id , String name , int stickType , int line , int row){
        this.id = id;
        this.name = name;
        this.stickType = stickType;
        this.line = line;
        this.row = row;
    }

    public EmoticonSetBean(String id, int order, String name, int stickType, int line, int row, String iconUri, String iconUrl, String iconName, boolean isShowDelBtn,
                           int itemPadding, int horizontalSpacing, int verticalSpacing, int flag, long updateTime, long lastCheckTime, ArrayList<EmoticonBean> emoticonList){
        this.id = id;
        this.order = order;
        this.name = name;
        this.stickType = stickType;
        this.line = line;
        this.row = row;
        this.iconUri = iconUri;
        this.iconUrl = iconUrl;
        this.iconName = iconName;
        this.isShowDelBtn = isShowDelBtn;
        this.itemPadding = itemPadding;
        this.horizontalSpacing = horizontalSpacing;
        this.verticalSpacing = verticalSpacing;
        this.flag = flag;
        this.updateTime = updateTime;
        this.lastCheckTime = lastCheckTime;
        this.emoticonList = emoticonList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getStickType() {
        return stickType;
    }

    public void setStickType(int stickType) {
        this.stickType = stickType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getIconUri() {
        return iconUri;
    }

    public void setIconUri(String iconUri) {
        this.iconUri = iconUri;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public boolean isShowDelBtn() {
        return isShowDelBtn;
    }

    public void setShowDelBtn(boolean isShowDelBtn) {
        this.isShowDelBtn = isShowDelBtn;
    }

    public int getItemPadding() {
        return itemPadding;
    }

    public void setItemPadding(int itemPadding) {
        this.itemPadding = itemPadding;
    }

    public int getHorizontalSpacing() {
        return horizontalSpacing;
    }

    public void setHorizontalSpacing(int horizontalSpacing) { this.horizontalSpacing = horizontalSpacing; }

    public int getVerticalSpacing() {
        return verticalSpacing;
    }

    public void setVerticalSpacing(int verticalSpacing) {
        this.verticalSpacing = verticalSpacing;
    }

    public ArrayList<EmoticonBean> getEmoticonList() {
        return emoticonList;
    }

    public void setEmoticonList(ArrayList<EmoticonBean> emoticonList) { this.emoticonList = emoticonList; }

    public EmoticonBean getFirstEmoticon() {
        return emoticonList != null && !emoticonList.isEmpty() ? emoticonList.get(0) : null;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(long lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }

    @Override
    public String toString() {
        return "EmoticonSetBean{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                "stickType=" + stickType +
                ", line=" + line +
                ", row=" + row +
                ", iconUri='" + iconUri + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", iconName='" + iconName + '\'' +
                ", isShowDelBtn=" + isShowDelBtn +
                ", itemPadding=" + itemPadding +
                ", horizontalSpacing=" + horizontalSpacing +
                ", verticalSpacing=" + verticalSpacing +
                ", emoticonList=" + emoticonList +
                '}';
    }
}
