package com.moinapp.wuliao.modules.mine.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.Messages;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.mine.model.UserActivity;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 我的动态界面,发表的动态适配器
 */
public class MyUserActivityAdapter extends AUserActivityAdapter {
    private static final ILogger MyLog = LoggerFactory.getLogger(MyUserActivityAdapter.class.getSimpleName());

    private Context context;
    String mUid;
    private int mPosition = 0;
    private boolean animShow = true;
    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        context = parent.getContext();
        if (mPosition < position) {
            mPosition = position;
            animShow = true;
        } else {
            animShow = false;
        }
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_myuser_activity, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        if (position == 0) {
            vh.ll_item_background.setBackgroundResource(R.drawable.ac_pop_no_top);
        } else {
            vh.ll_item_background.setBackgroundResource(R.drawable.ac_pop);
        }

        UserActivity data = mDatas.get(position);
        if (data != null) {
            vh.ly_item_cosplay.setVisibility(View.GONE);
            vh.ly_item_comment.setVisibility(View.GONE);
            vh.ly_item_follow.setVisibility(View.GONE);
            vh.ly_item_like.setVisibility(View.GONE);
            vh.ly_item_systemmsg.setVisibility(View.GONE);
            vh.tv_activity_time.setText(StringUtil.humanDate(data.getCreatedAt(), StringUtil.UESRACTIVITY_PATTERN));
            switch (data.getAction()) {
                case Messages.ACTION_FOLLOW:
                    vh.ly_item_follow.setVisibility(View.VISIBLE);
                    vh.follow_txt.setText(addClickablePart(context, data),
                            TextView.BufferType.SPANNABLE);
                    // TODO 如果是头像的话,应该设计成圆形的图吧?? 如果是圆形的话就用AvatarView,否则要考虑用圆角图片了
                    ImageLoaderUtils.displayHttpImage(data.getPicture(), vh.follow_img, null, animShow, null);
                    if (!StringUtil.isNullOrEmpty(data.getResource())) {
                        vh.ly_item_follow.setOnClickListener(v -> {
                            MyLog.i("res=" + data.getResource() + ", getUid=" + getUid());
                            if (data.getResource().equals(getUid())) {
                                AppContext.toast(context, "您正在看" + data.getTargetName() + "的动态了哦~");
                            } else {
                                if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(data.getResource())) {
                                    // 登录用户点击自己头像
                                    UIHelper.showMine(context, 0);
                                } else {
                                    UIHelper.showUserCenter(context, data.getResource());
                                }
                            }
                        });
                    }
                    break;
                case Messages.ACTION_LIKE_COSPLAY:
                    vh.ly_item_like.setVisibility(View.VISIBLE);
                    vh.like_txt.setText(addClickablePart(context, data),
                            TextView.BufferType.SPANNABLE);
                    ImageLoaderUtils.displayHttpImage(data.getPicture(), vh.like_img, null, animShow, null);
                    vh.ly_item_like.setOnClickListener(v -> onCosplayClick(context, data));
                    break;
                case Messages.ACTION_COMMENT_COSPLAY:
                case Messages.ACTION_REPLY_COSPLAY:
                case Messages.ACTION_COMMENT_LIKE_COSPLAY:
                case Messages.ACTION_REPLY_COMMENT_COSPLAY:
                case Messages.ACTION_AT_COSPLAY:
                    vh.ly_item_comment.setVisibility(View.VISIBLE);
                    vh.comment_txt.setText(addClickablePart(context, data),
                            TextView.BufferType.SPANNABLE);
                    ImageLoaderUtils.displayHttpImage(data.getPicture(), vh.comment_img, null, animShow, null);
                    vh.ly_item_comment.setOnClickListener(v -> onCosplayClick(context, data));
                    break;
                case Messages.ACTION_REGISTER:
                    vh.ly_item_systemmsg.setVisibility(View.VISIBLE);
                    String text = "我在moin完成了注册!";
                    boolean isLoginUser = ClientInfo.isLoginUser(getUid());
                    if (!isLoginUser) {
                        text = data.getUserName() + "在moin完成了注册!";
                    }
                    SpannableString spanStr = new SpannableString(text);
                    SpannableStringBuilder ssb = new SpannableStringBuilder(spanStr);
                    ssb.setSpan(new ClickableSpan() {

                        @Override
                        public void onClick(View widget) {
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            // 设置文本颜色
                            ds.setColor(context.getResources().getColor(R.color.common_title_grey));
                            // 去掉下划线
                            ds.setUnderlineText(false);
                        }

                    }, 0, isLoginUser ? 1 : data.getUserName().length(), 0);

                    vh.systemmsg_txt.setText(ssb, TextView.BufferType.SPANNABLE);
                    ImageLoaderUtils.displayHttpImage(data.getPicture(), vh.systemmsg_img, null, animShow, null);
                    // 注册动态无点击事件
                    break;
                case Messages.ACTION_SUBMIT_COSPLAY:
                case Messages.ACTION_FORWARD_COSPLAY:
                case Messages.ACTION_MODIFY_COSPLAY:
                    if (data.getType() == Messages.TYPE_IMAGE) {
                        vh.ly_item_cosplay.setVisibility(View.VISIBLE);
                        int img_width = context.getResources().getDimensionPixelSize(R.dimen.activity_cosplay_width);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.height = params.width = img_width;
//                    MyLog.i("作品的wh=" + params.width + "*" + params.height + ", img_width=" + img_width);
                        vh.cos_image.setLayoutParams(params);

                        LinearLayout.LayoutParams paramsBtn = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        paramsBtn.width = img_width / 4;
                        ImageLoaderUtils.displayHttpImage(data.getPicture(), vh.cos_image, null, animShow, null);
                        if (data.getCosplay() != null) {
                            if (data.getCosplay().getLikeNum() > 0) {
                                vh.cos_ly_like.setLayoutParams(paramsBtn);
                                vh.cos_ly_like.setVisibility(View.VISIBLE);
                                vh.cos_like_num.setText(data.getCosplay().getLikeNum() + "");
                            } else {
                                vh.cos_ly_like.setVisibility(View.GONE);
                            }
                            if (data.getCosplay().getCommentNum() > 0) {
                                vh.cos_ly_comment.setLayoutParams(paramsBtn);
                                vh.cos_ly_comment.setVisibility(View.VISIBLE);
                                vh.cos_comment_num.setText(data.getCosplay().getCommentNum() + "");
                            } else {
                                vh.cos_ly_comment.setVisibility(View.GONE);
                            }
                            if (data.getCosplay().getChildrenNum() > 0) {
                                vh.cos_ly_forward.setLayoutParams(paramsBtn);
                                vh.cos_ly_forward.setVisibility(View.VISIBLE);
                                vh.cos_forward_num.setText(data.getCosplay().getChildrenNum() + "");
                            } else {
                                vh.cos_ly_forward.setVisibility(View.GONE);
                            }
                            // 浏览量
                            if (data.getCosplay().getReadNum() > 0) {
                                vh.cos_ly_browse.setLayoutParams(paramsBtn);
                                vh.cos_ly_browse.setVisibility(View.VISIBLE);
                                vh.cos_browse_num.setText(data.getCosplay().getReadNum() + "");
                            } else {
                                vh.cos_ly_browse.setVisibility(View.GONE);
                            }

                        }
                        vh.ly_item_cosplay.setOnClickListener(v -> onCosplayClick(context, data));
                    } else {
                        // TODO 暂时作为帖子类型处理
                        vh.ly_item_like.setVisibility(View.VISIBLE);

                        String text2 = "我发布了一个帖子!";
                        boolean isLoginUser2 = ClientInfo.isLoginUser(getUid());
                        if (!isLoginUser2) {
                            text2 = data.getUserName() + "发布了一个帖子!";
                        }
                        SpannableString spanStr2 = new SpannableString(text2);
                        SpannableStringBuilder ssb2 = new SpannableStringBuilder(spanStr2);
                        ssb2.setSpan(new ClickableSpan() {

                            @Override
                            public void onClick(View widget) {
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                // 设置文本颜色
                                ds.setColor(context.getResources().getColor(R.color.common_title_grey));
                                // 去掉下划线
                                ds.setUnderlineText(false);
                            }

                        }, 0, isLoginUser2 ? 1 : data.getUserName().length(), 0);

                        vh.like_txt.setText(ssb2, TextView.BufferType.SPANNABLE);

                        ImageLoaderUtils.displayHttpImage(data.getPicture(), vh.like_img, null, animShow, null);
                        vh.ly_item_like.setOnClickListener(v -> onCosplayClick(context, data));
                    }

                    break;
            }
        }

        return convertView;
    }

    private void onCosplayClick(Context mContext, UserActivity userActivity) {
        if (userActivity == null) return;
        MyLog.i("你点击了 动态 " + userActivity.toString());
        if (userActivity.getType() == Messages.TYPE_IMAGE) {
            UIHelper.showDiscoveryCosplayDetail(mContext, null, userActivity.getResource(), TimeUtils.getCurrentTimeInLong());
        } else {
            // TODO 非大咖秀的全部默认是帖子先.如果以后还有别的类型,需要增加判断.此处是防止服务器下发类型错误.
            UIHelper.showPostDetail(mContext, userActivity.getResource(), TimeUtils.getCurrentTimeInLong());
        }
    }

    @Override
    protected boolean needShowFootWhenEmpty() {
        return false;
    }

    /**
     * 设置黑体文字的显示风格以及跳转
     */
    private SpannableStringBuilder addClickablePart(Context context, UserActivity data) {
        if (data == null) {
            return null;
        }
        data.setContent(StringUtil.nullToEmpty(data.getContent()));

        boolean isLoginUser = ClientInfo.isLoginUser(getUid());
        boolean isSelf = StringUtils.equals(data.getUserName(), data.getTargetName());
        boolean isMe = false;
        boolean isComment = false;
        int commentLen = 0;
        if (data.getTargetName() == null) data.setTargetName("");
        String text = null;
        int startLen = 0;
        switch (data.getAction()) {
            case Messages.ACTION_FOLLOW:
                if (isLoginUser) {
                    isMe = true;
                    text = "我关注了" + data.getTargetName();
                    startLen = "我关注了".length();
                } else {
                    text = data.getUserName() + "关注了" + data.getTargetName();
                    startLen = (data.getUserName() + "关注了").length();
                }
                break;
            case Messages.ACTION_LIKE_COSPLAY:
                // 自己赞自己: [我开心得赞了自己][targetName开心得赞了自己]
                if (isLoginUser) {
                    isMe = true;
                    if (isSelf) {
                        text = "我开心地赞了自己";
                    } else {
                        text = "我赞了" + data.getTargetName()/* + "的图片"*/;
                        startLen = "我赞了".length();
                    }
                } else {
                    if (isSelf) {
                        text = data.getUserName() + "开心地赞了自己";
                    } else {
                        text = data.getUserName() + "赞了" + data.getTargetName()/* + "的图片"*/;
                        startLen = (data.getUserName() + "赞了").length();
                    }
                }
                break;
            case Messages.ACTION_COMMENT_COSPLAY:
            case Messages.ACTION_COMMENT_LIKE_COSPLAY:
                isComment = true;
                if (isLoginUser) {
                    isMe = true;
                    // 自己评论自己: [我调皮地评论了自己][targetName评论了自己]
                    if (isSelf) {
                        text = "我调皮地评论了自己\n" + data.getContent();
                        commentLen = "我调皮地评论了自己\n".length();
                    } else {
                        text = "我评论了" + data.getTargetName() + "\n" + data.getContent();
                        startLen = "我评论了".length();
                        commentLen = ("我评论了" + data.getTargetName() + "\n").length();
                    }

                } else {
                    if (isSelf) {
                        text = data.getUserName() + "调皮地评论了自己\n" + data.getContent();
                        commentLen = (data.getUserName() + "调皮地评论了自己\n").length();
                    } else {
                        text = data.getUserName() + "评论了" + data.getTargetName() + "\n" + data.getContent();
                        startLen = (data.getUserName() + "评论了").length();
                        commentLen = (data.getUserName() + "评论了" + data.getTargetName() + "\n").length();
                    }

                }
                break;
            case Messages.ACTION_AT_COSPLAY:
                if (isLoginUser) {
                    isMe = true;
                    // 自己@自己: [我@了自己][targetName@了自己]
                    if (isSelf) {
                        text = "我@了自己";
                    } else {
                        text = "我@了" + data.getTargetName();
                        startLen = "我@了".length();
                    }

                } else {
                    if (isSelf) {
                        text = data.getUserName() + "@了自己";
                    } else {
                        text = data.getUserName() + "@了" + data.getTargetName();
                        startLen = (data.getUserName() + "@了").length();
                    }

                }
                break;
            case Messages.ACTION_REPLY_COSPLAY:
            case Messages.ACTION_REPLY_COMMENT_COSPLAY:
                isComment = true;
                if (isLoginUser) {
                    isMe = true;
                    text = "我回复了" + data.getTargetName() + "\n" + data.getContent();
                    startLen = "我回复了".length();
                    commentLen = ("我回复了" + data.getTargetName() + "\n").length();
                } else {
                    text = data.getUserName() + "回复了" + data.getTargetName() + "\n" + data.getContent();
                    startLen = (data.getUserName() + "回复了").length();
                    commentLen = (data.getUserName() + "回复了" + data.getTargetName() + "\n").length();
                }
                break;
            default:
                break;
        }

        if (StringUtil.isNullOrEmpty(text)) {
            return null;
        }

        SpannableString spanStr = new SpannableString(text);
        SpannableStringBuilder ssb = new SpannableStringBuilder(spanStr);
        if (!isSelf) {
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    // 设置文本颜色
                    ds.setColor(context.getResources().getColor(R.color.common_title_grey));
                    // 去掉下划线
                    ds.setUnderlineText(false);
                }

            }, startLen, startLen + data.getTargetName().length(), 0);

        }

        // 其他用户的名字加粗
        if (!isLoginUser) {
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    // 设置文本颜色
                    ds.setColor(context.getResources().getColor(R.color.common_title_grey));
                    // 去掉下划线
                    ds.setUnderlineText(false);
                }

            }, 0, data.getUserName().length(), 0);
        }

        // "评论内容"加粗
        if (isComment) {
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    // 设置文本颜色
                    ds.setColor(context.getResources().getColor(R.color.common_title_grey));
                    // 去掉下划线
                    ds.setUnderlineText(false);
                }

            }, commentLen, commentLen + data.getContent().length(), 0);
        }
        // "我"加粗
        /*if (isMe) {
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    // 设置文本颜色
                    ds.setColor(context.getResources().getColor(R.color.common_title_grey));
                    // 去掉下划线
                    ds.setUnderlineText(false);
                }

            }, 0, 1, 0);
        }*/

        return ssb;
    }

    @Override
    public boolean isNeedLogin() {
        return true;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String mUid) {
        this.mUid = mUid;
        MyLog.i("setUid=" + mUid + ", " + this.mUid);
    }

    @Override
    public void setFinishText(int id) {
        super.setFinishText(R.string.no_more_data);
    }

    static class ViewHolder {

        @InjectView(R.id.ll_item_background)
        LinearLayout ll_item_background;

        @InjectView(R.id.tv_activity_time)
        TextView tv_activity_time;
        // 发布
        @InjectView(R.id.ly_item_cosplay)
        LinearLayout ly_item_cosplay;
        @InjectView(R.id.cos_image)
        ImageView cos_image;
        @InjectView(R.id.cos_ly_browse)
        LinearLayout cos_ly_browse;
        @InjectView(R.id.cos_browse_num)
        TextView cos_browse_num;
        @InjectView(R.id.cos_ly_like)
        LinearLayout cos_ly_like;
        @InjectView(R.id.cos_like_num)
        TextView cos_like_num;
        @InjectView(R.id.cos_ly_comment)
        LinearLayout cos_ly_comment;
        @InjectView(R.id.cos_comment_num)
        TextView cos_comment_num;
        @InjectView(R.id.cos_ly_forward)
        LinearLayout cos_ly_forward;
        @InjectView(R.id.cos_forward_num)
        TextView cos_forward_num;

        // 评论
        @InjectView(R.id.ly_item_comment)
        LinearLayout ly_item_comment;
        @InjectView(R.id.comment_txt)
        TextView comment_txt;
        @InjectView(R.id.comment_img)
        ImageView comment_img;

        // 赞
        @InjectView(R.id.ly_item_like)
        LinearLayout ly_item_like;
        @InjectView(R.id.like_txt)
        TextView like_txt;
        @InjectView(R.id.like_img)
        ImageView like_img;

        // 关注
        @InjectView(R.id.ly_item_follow)
        LinearLayout ly_item_follow;
        @InjectView(R.id.follow_txt)
        TextView follow_txt;
        @InjectView(R.id.follow_img)
        ImageView follow_img;

        // 系统消息[注册成功]
        @InjectView(R.id.ly_item_systemmsg)
        LinearLayout ly_item_systemmsg;
        @InjectView(R.id.systemmsg_txt)
        TextView systemmsg_txt;
        @InjectView(R.id.systemmsg_img)
        ImageView systemmsg_img;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
