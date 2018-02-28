package com.moinapp.wuliao.bean;


/**
 * Created by liujiancheng on 15/5/5.
 * 用户信息模型类，用于和服务器通讯接口的json交互
 */
public class UserInfo extends Entity{
    private String username;
    private String username_abc;//用户名对应的字母串,给用户名分组用
    private String phone;
    private String id;
    private String password;
    private String email;
    private String sex;//注意这是字符串，ui要控制，比如“male/femail/unknown” 或者“男／女／未知”
    private String nickname;//3.2.3版本开始允许用户名重复,所以以后修改昵称,其实就是修改username.nickname字段暂时没有用了.
    private String contact; //联系方式
    private String ages;//注意也是字符串 00后就是00 75后就是75
    private int stars = -1;//12个星座，存成1-12的整数
    private Location location;
    private String signature;
    private BaseImage avatar;
    private int cosplayNum;
    private int tagNum;
    private int idolNum;
    private int fansNum;
    private int likeNum;
    private int activityNum;//动态数量
    private long birthday;//生日,客户端自己转成日期格式
    private MoinBean moinBean;//魔豆信息

    /**
     * 0:用户与我没有关系 1:我的粉丝 2:我关注的人3:互相关注
     */
    private int relation;

    /**
     * 备注名
     */
    private String alias;

    /**
     * 注册时间
     */
    private long createdAt;

    /**
     * 第三方用户的名字
     */
    private String thirdName;

    /**
     * 第三方用户的uid
     */
    private String uid;

    public void setUsername(String name) { this.username = name;  }

    public String getUsername() { return this.username; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getPhone() { return this.phone; }

    public String getUId() {return this.id; }

    public void setUId(String id) {
        this.id = id;
    }

    public void setPassword(String password) { this.password = password; }

    public String getPassword() { return this.password; }

    public void setEmail(String email) { this.email = email; }

    public String getEmail() { return this.email; }

    public void setSex(String sex) { this.sex = sex; }

    public String getSex() { return this.sex; }

    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getNickname() { return this.nickname; }

    public void setAges(String ages) { this.ages = ages; }

    public String getAges() { return this.ages; }

    public void setStars(int stars) { this.stars = stars; }

    public int getStars() { return this.stars; }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public BaseImage getAvatar() {
        return avatar;
    }

    public void setAvatar(BaseImage avatar) {
        this.avatar = avatar;
    }

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    public int getCosplayNum() {
        return cosplayNum;
    }

    public void setCosplayNum(int cosplayNum) {
        this.cosplayNum = cosplayNum;
    }

    public int getTagNum() {
        return tagNum;
    }

    public void setTagNum(int tagNum) {
        this.tagNum = tagNum;
    }

    public int getIdolNum() {
        return idolNum;
    }

    public void setIdolNum(int idolNum) {
        this.idolNum = idolNum;
    }

    public int getFansNum() {
        return fansNum;
    }

    public void setFansNum(int fansNum) {
        this.fansNum = fansNum;
    }

    public int getActivityNum() {
        return activityNum;
    }

    public void setActivityNum(int activityNum) {
        this.activityNum = activityNum;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public String getUsername_abc() {
        return username_abc;
    }

    public void setUsername_abc(String username_abc) {
        this.username_abc = username_abc;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getThirdName() {
        return thirdName;
    }

    public void setThirdName(String thirdName) {
        this.thirdName = thirdName;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getThirdUid() {
        return uid;
    }

    public void setThirdUid(String uid) {
        this.uid = uid;
    }

    public String getAvatarUri() {
        return avatar == null ? "" : avatar.getUri();
    }

    public MoinBean getMoinBean() {
        return moinBean;
    }

    public void setMoinBean(MoinBean moinBean) {
        this.moinBean = moinBean;
    }

    public int getMoinbeanNum() {
        return moinBean == null ? 0 : moinBean.getTotalBean();
    }

    public String getMoinbeanPkNum() {
        return moinBean == null ? "0" : moinBean.getPkRank();
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", id='" + id + '\'' +
//                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", sex='" + sex + '\'' +
                ", nickname='" + nickname + '\'' +
                ", ages='" + ages + '\'' +
                ", contact='" + contact + '\'' +
                ", stars=" + stars +
                ", location=" + location +
                ", signature='" + signature + '\'' +
                ", avatar=" + avatar +
                ", cosplayNum=" + cosplayNum +
                ", tagNum=" + tagNum +
                ", idolNum=" + idolNum +
                ", fansNum=" + fansNum +
                ", relation=" + relation +
                ", alias=" + alias +
                ", thirdName=" + thirdName +
                '}';
    }
}
