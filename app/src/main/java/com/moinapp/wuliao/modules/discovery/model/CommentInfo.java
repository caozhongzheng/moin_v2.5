package com.moinapp.wuliao.modules.discovery.model;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.BaseImage;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.listener.CommentOnClickListener;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.StyledText;
import com.moinapp.wuliao.ui.CommentDialog;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.AvatarView;

import java.util.List;

/**
 * 用户对大咖秀图片发表的评论信息
 * Created by liujiancheng on 15/10/9.
 */
public class CommentInfo extends Entity {
    /**
     * 评论的id
     */
    private String id;

    /**
     * 作者信息
     */
    private UserInfo author;

    /**
     * 被回复作者信息
     */
    private UserInfo reply;

    /**
     * 摘要
     */
    private String meta;

    /**
     * 正文
     */
    private String content;

    /**
     * 评论内容是图片的话用picture来存放url
     */
    private BaseImage picture;

    /**
     * 发表时间
     */
    private long createdAt;

    public UserInfo getAuthor() {
        return author;
    }

    public void setAuthor(UserInfo author) {
        this.author = author;
    }

    public UserInfo getReply() {
        return reply;
    }

    public void setReply(UserInfo reply) {
        this.reply = reply;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String text) {
        this.content = text;
    }

    public BaseImage getPicture() {
        return picture;
    }

    public void setPicture(BaseImage picture) {
        this.picture = picture;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getCid() {
        return id;
    }

    public void setCid(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CommentInfo{" +
                "id='" + id + '\'' +
                ", author=" + author +
                ", reply=" + reply +
                ", meta='" + meta + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    public void setCommentUser(Context context, CosplayInfo mCosplayInfo, RelativeLayout mRlComment, CommentOnClickListener callback) {
        AvatarView avatar = (AvatarView) mRlComment.findViewById(R.id.avatar);
        TextView authorInfo = (TextView) mRlComment.findViewById(R.id.comment_author_info);
        RelativeLayout rlAuthor = (RelativeLayout) mRlComment.findViewById(R.id.rl_author);
        TextView createAt = (TextView) mRlComment.findViewById(R.id.comment_timemills);
        ImageView handleReply = (ImageView) mRlComment.findViewById(R.id.iv_handle_comment);
        LinearLayout llContent = (LinearLayout) mRlComment.findViewById(R.id.ll_content);
        TextView content = (TextView) mRlComment.findViewById(R.id.comment_content);
        ImageView contentImage = (ImageView) mRlComment.findViewById(R.id.comment_content_image);

        if (getAuthor() != null && getAuthor().getAvatar() != null) {
            avatar.setAvatarUrl(getAuthor().getAvatar().getUri());
            avatar.setOnClickListener(v -> {
                if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(getAuthor().getUId())) {
                    // 登录用户点击自己头像
                    UIHelper.showMine(context, 0);
                } else {
                    UIHelper.showUserCenter(context, getAuthor().getUId());
                }
            });
        }
        createAt.setText(StringUtil.humanDate(getCreatedAt(), StringUtil.COMMENT_DATE_PATTERN));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getInstance().isLogin()) {
                    CommentDialog dialog = new CommentDialog(context);
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(true);
                    if (getAuthor() == null) {
                        dialog.hideDeleteButton();
                    } else if (ClientInfo.getUID().equalsIgnoreCase(getAuthor().getUId())) {
                        dialog.hideReplyButton();
                    } else {
                        dialog.hideDeleteButton();
                    }
                    if (getPicture() != null && !TextUtils.isEmpty(getPicture().getUri())) {
                        dialog.hideCopyButton();
                    }
                    //自己的帖子点击删除
                    dialog.setDeleteCallback(new CommentDialog.DeleteCallBack() {
                        @Override
                        public void onDeleteClick() {
                            DiscoveryManager.getInstance().deleteComment(getCid(),
                                    new IListener() {
                                        @Override
                                        public void onSuccess(Object obj) {
                                            callback.onDeleteClick(null);
                                        }

                                        @Override
                                        public void onErr(Object obj) {

                                        }

                                        @Override
                                        public void onNoNetwork() {

                                        }
                                    });
                        }
                    });
                    dialog.setReplyCallback(new CommentDialog.ReplyCallBack() {
                        @Override
                        public void onReplyClick() {
                            callback.onReplyClick(getAuthor());
                        }
                    });
                    dialog.setCopyCallback(new CommentDialog.CopyCallBack() {
                        @Override
                        public void onCopyClick() {
                            callback.onCopyClick(getContent());
                        }
                    });
                    dialog.show();
                } else {
                    AppContext.showToast(R.string.comment_when_no_login);
                    UIHelper.showLoginActivity(context);
                }
            }
        };

        authorInfo.setMovementMethod(LinkMovementMethod.getInstance());
        authorInfo.setFocusable(false);
        authorInfo.setLongClickable(false);
        authorInfo.setText(addClickableAuthor(context, mCosplayInfo),
                TextView.BufferType.SPANNABLE);

        if (getPicture() != null && getPicture().getUri() != null) {
            contentImage.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
            ImageLoaderUtils.displayHttpImage(getPicture().getUri(), contentImage, null);
            contentImage.setOnClickListener(v -> {
                UIHelper.showImagePreview(v.getContext(), new String[]{getPicture().getUri()});
            });
        } else {
            contentImage.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
            content.setMovementMethod(LinkMovementMethod.getInstance());
            content.setFocusable(false);
            content.setLongClickable(false);
            content.setText(addClickableContent(context, mCosplayInfo),
                    TextView.BufferType.SPANNABLE);
            content.setOnClickListener(listener);
        }
        rlAuthor.setOnClickListener(listener);
        createAt.setOnClickListener(listener);
        handleReply.setOnClickListener(listener);
        llContent.setOnClickListener(listener);
    }

    public boolean isReply() {
        return reply != null && !StringUtil.isNullOrEmpty(reply.getUId());
    }

    private boolean hasAt() {
        int start = getContent().lastIndexOf("@");
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

    private SpannableStringBuilder addClickableAuthor(final Context context, CosplayInfo mCosplayInfo) {
        String text;
        if (getAuthor() == null) return null;

        if(isReply()) {
            text = getAuthor().getUsername() + "回复@" + getReply().getUsername();
        } else {
            text = getAuthor().getUsername();
        }
        SpannableString spanStr = new SpannableString(text);
        SpannableStringBuilder ssb = new SpannableStringBuilder(spanStr);
        ssb.setSpan(new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(getAuthor().getUId())) {
                    // 登录用户点击自己头像
                    UIHelper.showMine(context, 0);
                } else {
                    UIHelper.showUserCenter(context, getAuthor().getUId());
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

        }, 0, getAuthor().getUsername().length(), 0);

        if (isReply()) {
            int len = (getAuthor().getUsername() + "回复").length();
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                    // TODO 进入用户中心
                    if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(getReply().getUId())) {
                        // 登录用户点击自己头像
                        UIHelper.showMine(context, 0);
                    } else {
                        UIHelper.showUserCenter(context, getReply().getUId());
                    }
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    // 设置文本颜色
//                    ds.setColor(context.getResources().getColor(R.color.main_blue));
                    ds.setColor(Color.BLACK);
                    // 去掉下划线
                    ds.setUnderlineText(false);
                }

            }, len, getReply().getUsername().length() + len + 1, 0);

        }
        return ssb;
    }

    private SpannableStringBuilder addClickableContent(final Context context, CosplayInfo mCosplayInfo) {
        String text = getContent();
        SpannableString spanStr = new SpannableString(text);
        SpannableStringBuilder ssb = new SpannableStringBuilder(spanStr);

        if (hasAt()) {
            List<StyledText> styledTextList = StringUtil.getStyledTextList(text);
            if (styledTextList == null) {
                return ssb;
            }
            for (StyledText st : styledTextList) {
                ssb.setSpan(new ClickableSpan() {

                    @Override
                    public void onClick(View widget) {
                        // TODO 进入用户中心,但是需要增加一个根据用户名查找用户id等的接口
                        android.util.Log.i(CommentInfo.class.getSimpleName(), "进入用户中心:[" + st.getUsername() + "]");
                        if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(getUidByName(mCosplayInfo, st.getUsername()))) {
                            // 登录用户点击自己头像
                            UIHelper.showMine(context, 0);
                        } else {
                            UIHelper.showUserCenter(context, getUidByName(mCosplayInfo, st.getUsername()));
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

                }, st.getStart(), st.getEnd(), 0);

            }
        }
        return ssb;
    }

    private String getUidByName(CosplayInfo cosplayInfo, String username) {
        if(cosplayInfo == null || cosplayInfo.getFriends() == null || cosplayInfo.getFriends().isEmpty()) {
            return null;
        }
        for (UserInfo userInfo : cosplayInfo.getFriends()) {
            if(username.equals(userInfo.getUsername())) {
                return userInfo.getUId();
            }
        }
        return null;
    }
}
