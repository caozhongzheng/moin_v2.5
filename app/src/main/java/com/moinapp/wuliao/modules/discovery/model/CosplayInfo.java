package com.moinapp.wuliao.modules.discovery.model;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.BaseImage;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerAudioInfo;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.StyledText;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;

import java.util.Iterator;
import java.util.List;

/**
 * 3.2.7以后加入的帖子, 帖子和大咖秀图片共用cosplayinfo对象, 用type区分
 * 大咖秀图片后者帖子的详细信息
 * Created by liujiancheng on 15/10/9.
 */
public class CosplayInfo extends Entity {
    /**
     * 大咖秀图片
     */
    public static final int TYPE_COSPLAY = 1;

    /**
     * 图片帖
     */
    public static final int TYPE_POST_PICTURE = 2;

    /**
     *  文字帖
     */
    public static final int TYPE_POST_TEXT = 3;

    /**
     *  音频帖
     */
    public static final int TYPE_POST_AUDIO = 4;

    /**
     * 节点对应的图片ID
     */
    private String id;

    /**
     * 类型,1 大咖秀图 2 图片帖 3 文字帖 4 音频帖
     */
    private int type;

    /**
     * 图片的缩略图
     */
    private BaseImage icon;

    /**
     * 发布的图片
     */
    private BaseImage picture;

    /**
     * 正文
     */
    private String content;

    /**
     * IP名称标签
     */
    private List<String> ipNames;

    /**
     * 运营标签
     */
    private List<String> opNames;

    /**
     * 转发路径
     */
    private CosplayPath path;

    /**
     * 查看浏览的数量
     */
    private int readNum;

    /**
     * 收到的赞的数量
     */
    private int likeNum;

    /**
     * 点赞的用户列表，userInfo的数组集合。服务端仅当likeNum小于某个特定数字（10个）后才会下发该字段
     */
    private List<UserInfo> likeUsers;

    /**
     * 浏览的用户数量
     */
    private int viewNum;

    /**
     * 参与人数量
     */
    private int userNum;

    /**
     * 收到的评论的总数量
     */
    private int commentNum;

    /**
     * 转改的数量
     */
    private int childrenNum;

    /**
     * 收到的评论信息集合，取最新的前2条评论，是commentInfo的集合
     */
    private List<CommentInfo> comments;

    /**
     * 作者信息
     */
    private UserInfo author;

    /**
     * 发布时间
     */
    private long createdAt;

    /**
     * 是否点赞
     */
    private int isLike;

    /**
     * 当前用户是否允许修改 0 否 1 是，客户端显示转发或者改图转发
     */
    private int isWrite;

    /**
     * 改图转发的权限
     */
    private int writeAuth;

    /**
     * @好友的列表
     */
    private List<UserInfo> friends;

    /**
     * 前作作者
     */
    private UserInfo parentAuthor;

    /**
     * 前作的创作时间
     */
    private long parentCreatedAt;

    /**
     * 大咖秀图片或者帖子带的标签属性,
     */
    private List<TagInfo> tags;

    /**
     * 图片相关消息的集合
     */
    private List<CosplayMsg> msgList;

    /**
     * 大咖秀图片或者帖子所带音频的集合
     */
    private List<StickerAudioInfo> audio;

    /**
     * 大咖秀图片或者帖子所带图片的集合
     */
    private List<BaseImage> pictureList;

    public String getUcid() {
        return id;
    }

    public void setUcid(String ucid) {
        this.id = ucid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BaseImage getIcon() {
        return icon;
    }

    public void setIcon(BaseImage icon) {
        this.icon = icon;
    }

    public BaseImage getPicture() {
        return picture;
    }

    public void setPicture(BaseImage picture) {
        this.picture = picture;
    }

    public String getContent() {
//        if (getComments() != null && !getComments().isEmpty()) {
//            return getComments().get(0).getContent();
//        }
        return content;
    }

    public void setContent(String text) {
        this.content = text;
    }

    public List<String> getIpNames() {
        return ipNames;
    }

    public void setIpNames(List<String> ipNames) {
        this.ipNames = ipNames;
    }

    public List<String> getOpNames() {
        return opNames;
    }

    public void setOpNames(List<String> opNames) {
        this.opNames = opNames;
    }

    public CosplayPath getPath() {
        return path;
    }

    public void setPath(CosplayPath path) {
        this.path = path;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public List<UserInfo> getLikeUsers() {
        return likeUsers;
    }

    public void setLikeUsers(List<UserInfo> likeUsers) {
        this.likeUsers = likeUsers;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getChildrenNum() {
        return childrenNum;
    }

    public void setChildrenNum(int childrenNum) {
        this.childrenNum = childrenNum;
    }

    public List<CommentInfo> getComments() {
        return comments;
    }

    public void setComments(List<CommentInfo> comments) {
        this.comments = comments;
    }

    public String getAuthorName() {
        return author != null ? author.getUsername() : "";
    }

    public UserInfo getAuthor() {
        return author;
    }

    public void setAuthor(UserInfo author) {
        this.author = author;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public int getIsWrite() {
        return isWrite;
    }

    public void setIsWrite(int isWrite) {
        this.isWrite = isWrite;
    }

    public int getWriteAuth() {
        return writeAuth;
    }

    public void setWriteAuth(int writeAuth) {
        this.writeAuth = writeAuth;
    }

    public List<UserInfo> getFriends() {
        return friends;
    }

    public void setFriends(List<UserInfo> friends) {
        this.friends = friends;
    }

    public UserInfo getParentAuthor() {
        return parentAuthor;
    }

    public void setParentAuthor(UserInfo parentAuthor) {
        this.parentAuthor = parentAuthor;
    }

    public long getParentCreatedAt() {
        return parentCreatedAt;
    }

    public void setParentCreatedAt(long parentCreatedAt) {
        this.parentCreatedAt = parentCreatedAt;
    }

    public List<TagInfo> getTags() {
        return tags;
    }

    public void setTags(List<TagInfo> tags) {
        this.tags = tags;
    }

    public int getReadNum() {
        return readNum;
    }

    public void setReadNum(int readNum) {
        this.readNum = readNum;
    }

    public int getViewNum() {
        return viewNum;
    }

    public void setViewNum(int viewNum) {
        this.viewNum = viewNum;
    }

    public List<CosplayMsg> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<CosplayMsg> msgList) {
        this.msgList = msgList;
    }

    public int getUserNum() {
        return userNum;
    }

    public void setUserNum(int userNum) {
        this.userNum = userNum;
    }

    public List<StickerAudioInfo> getAudio() {
        return audio;
    }

    public void setAudio(List<StickerAudioInfo> audio) {
        this.audio = audio;
    }

    public List<BaseImage> getPictureList() {
        return pictureList;
    }

    public void setPictureList(List<BaseImage> pictureList) {
        this.pictureList = pictureList;
    }

    @Override
    public String toString() {
        return "CosplayInfo{" +
                "ucid='" + id + '\'' +
                ", writeAuth='" + writeAuth + '\'' +
                ", icon=" + icon +
                ", picture=" + picture +
                ", content=" + content +
                ", ipNames=" + ipNames +
                ", opNames=" + opNames +
                ", path=" + path +
                ", readNum=" + readNum +
                ", likeNum=" + likeNum +
                ", likeUsers=" + likeUsers +
                ", commentNum=" + commentNum +
                ", childrenNum=" + childrenNum +
                ", comments=" + comments +
                ", author=" + author +
                ", createdAt=" + createdAt +
                ", isLike=" + isLike +
                ", isWrite=" + isWrite +
                '}';
    }

    public void setContentView(Context context, TextView content) {
        setContentView(context, content, false, true);
    }
    public void setContentView(Context context, TextView content, boolean isVisibleWhenEmpty, boolean isJump) {
        if (content == null) return;
        if (StringUtil.isNullOrEmpty(getContent())) {
            if (isVisibleWhenEmpty) {
                content.setVisibility(View.VISIBLE);
                content.setText("");
            } else {
                content.setVisibility(View.GONE);
            }
        } else {
            if (isJump) {
                content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UIHelper.showDiscoveryCosplayDetail(context == null ? AppContext.context() : context,
                                CosplayInfo.this, getUcid(), TimeUtils.getCurrentTimeInLong());
                    }
                });
            }
            if (hasAt()) {
                SpannableString spanStr = new SpannableString(getContent());
                SpannableStringBuilder ssb = new SpannableStringBuilder(spanStr);

                List<StyledText> styledTextList = StringUtil.getStyledTextList(getContent());
                if (styledTextList != null) {
                    for (int i = 0; i < styledTextList.size(); i++) {
                        StyledText st = styledTextList.get(i);
                        if (st == null) {
                            continue;
                        }
                        String friendUid = null;
                        if (getFriends() != null && getFriends().size() > i && getFriends().get(i) != null) {
                            friendUid = getFriends().get(i).getUId();
                        }
                        final String uid = friendUid;
                        ssb.setSpan(new ClickableSpan() {

                            @Override
                            public void onClick(View widget) {
                                // TODO 进入用户中心,但是需要增加一个根据用户名查找用户id等的接口
                                android.util.Log.i(CosplayInfo.class.getSimpleName(), "进入用户中心:[" + st.getUsername() + "]");
//                                UIHelper.showUserCenter(context, getUidByName(CosplayInfo.this, finalI));
                                if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(uid)) {
                                    // 登录用户点击自己头像
                                    UIHelper.showMine(context, 0);
                                } else {
                                    UIHelper.showUserCenter(context, uid);
                                }
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                // 设置文本颜色
//                                ds.setColor(context.getResources().getColor(R.color.main_blue));
                                ds.setColor(Color.BLACK);
                                // 去掉下划线
                                ds.setUnderlineText(false);
                            }

                        }, st.getStart(), st.getEnd(), 0);

                    }
                }
                content.setMovementMethod(LinkMovementMethod.getInstance());
                content.setFocusable(true);
                content.setLongClickable(true);
                content.setText(ssb, TextView.BufferType.SPANNABLE);
            } else {
                content.setText(getContent());
            }
            content.setVisibility(View.VISIBLE);
            content.setTextIsSelectable(true);
        }
    }

    private boolean hasAt() {
        int start = StringUtil.nullToEmpty(getContent()).lastIndexOf("@");
        if(start == -1) {
            return false;
        } else {
            int end = getContent().indexOf(" ", start);
            if(end == -1) {
                StringBuffer sb = new StringBuffer();
                sb.append(getContent()).append(" ");
                setContent(sb.toString());
            }
        }
        return true;
    }

    private String getUidByName(CosplayInfo cosplayInfo, int position) {
        if (cosplayInfo == null || cosplayInfo.getFriends() == null
                || cosplayInfo.getFriends().isEmpty()
                || position > cosplayInfo.getFriends().size() - 1) {
            return null;
        }
        return cosplayInfo.getFriends().get(position).getUId();
    }

    public void setLikeUsersString(Context contet, TextView likeUser, boolean limit) {
        // 构造多个超链接的html, 通过选中的位置来获取用户名
        if (getLikeUsers() != null
                && !getLikeUsers().isEmpty()) {
            likeUser.setVisibility(View.VISIBLE);
            likeUser.setMovementMethod(LinkMovementMethod.getInstance());
            likeUser.setFocusable(false);
            likeUser.setLongClickable(false);
            likeUser.setText(addClickablePart(contet, limit),
                    TextView.BufferType.SPANNABLE);
        } else {
            likeUser.setVisibility(View.GONE);
            likeUser.setText("");
        }
    }

    private SpannableStringBuilder addClickablePart(final Context context,
                                                    boolean limit) {

        // 最多显示5个赞的用户名
        int showCunt = 5;

        SpannableString spanStr = new SpannableString("");
        SpannableStringBuilder ssb = new SpannableStringBuilder(spanStr);

        if(getLikeNum() > showCunt){
            String more = getLikeNum() + " 人赞";
            ssb.append(more);
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                    UIHelper.showCosplayLikeComment(context, getUcid(), 0);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    // 设置文本颜色
                    ds.setColor(context.getResources().getColor(R.color.common_title_grey));
                    // 去掉下划线
                    ds.setUnderlineText(false);
                }

            }, 0, more.length(), 0);
        }else{
            StringBuilder builder = new StringBuilder();
            final Iterator<UserInfo> it = getLikeUsers().iterator();
            String split = ", ";
            while(it.hasNext()){
                builder.append(it.next().getUsername() + split);
            }
            String likeStr = builder.substring(0, builder.length()- split.length());
            ssb.append(likeStr);

            final Iterator<UserInfo> it2 = getLikeUsers().iterator();
            int start = 0;
            while(it2.hasNext()){
                UserInfo user = it2.next();
                String name = user.getUsername();
                ssb.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(user.getUId())) {
                            // 登录用户点击自己头像
                            UIHelper.showMine(context, 0);
                        } else {
                            UIHelper.showUserCenter(context, user.getUId());
                        }
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        // 设置文本颜色
                        ds.setColor(context.getResources().getColor(R.color.common_title_grey));
                        // 去掉下划线
                        ds.setUnderlineText(false);
                    }

                }, start, start + name.length(), 0);
                start += name.length() + split.length();
            }
        }
        return ssb.append("");
    }
}
