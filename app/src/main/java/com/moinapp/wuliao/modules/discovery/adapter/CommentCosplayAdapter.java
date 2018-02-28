package com.moinapp.wuliao.modules.discovery.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.discovery.model.CommentInfo;
import com.moinapp.wuliao.modules.mine.UserDefineConstants;
import com.moinapp.wuliao.ui.CommentDialog;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.AvatarView;
import com.moinapp.wuliao.widget.FollowView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 *  大咖秀评论列表列表适配器
 * 
 * @author liujiancheng
 * 
 */
public class CommentCosplayAdapter extends ListBaseAdapter<CommentInfo> {
    private Activity mContext;
    private static final int FOLLOW_ALREADY = 100;//当在页面关注以后设置的临时标记
    private boolean mFromPost;

    public CommentCosplayAdapter(Activity activity) {
        mContext = activity;
    }

    public CommentCosplayAdapter(Activity activity, boolean isFromPost) {
        mContext = activity;
        mFromPost = isFromPost;
    }

    private FollowAdapter.CommentCallback commentCallback = null;

    public void setCommentCallback(FollowAdapter.CommentCallback callback) {
        commentCallback = callback;
    }

    @SuppressLint("InflateParams")
    @Override
    protected View getRealView(int position, View convertView,
            final ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_comment_cosplay, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final CommentInfo item = mDatas.get(position);

        vh.author.setMovementMethod(LinkMovementMethod.getInstance());
        vh.author.setFocusable(false);
        vh.author.setLongClickable(false);
        vh.author.setText(addClickablePart(mContext, item), TextView.BufferType.SPANNABLE);

        if (item.getPicture() != null && item.getPicture().getUri() != null) {
            vh.content.setVisibility(View.GONE);
            vh.image.setVisibility(View.VISIBLE);
            ImageLoaderUtils.displayHttpImage(item.getPicture().getUri(), vh.image, null);
        } else {
            vh.image.setVisibility(View.GONE);
            vh.content.setVisibility(View.VISIBLE);
            vh.content.setText(item.getContent());
        }

        vh.time.setText(StringUtil.humanDate(item.getCreatedAt(), StringUtil.COMMENT_DATE_PATTERN));

        // 帖子详情里面的头像要重新设置大小
        if (mFromPost) {
            ViewGroup.LayoutParams params = vh.avatar.getLayoutParams();
            params.width = (int)TDevice.dpToPixel(22);
            params.height = (int)TDevice.dpToPixel(22);
        }
        if (item.getAuthor() != null) {
            if (item.getAuthor().getAvatar() != null) {
                vh.avatar.setAvatarUrl(item.getAuthor().getAvatar().getUri());
            }

            if (mFromPost) {
                vh.handle.setVisibility(View.VISIBLE);
                vh.like.setVisibility(View.GONE);
            } else {
                int relation = item.getAuthor().getRelation();
                vh.like.init(item.getAuthor(), relation, UserDefineConstants.FOLLOW_COMMENT_LIST);
                if (relation == UserDefineConstants.FRIENDS_SELF) {
                    vh.like.setVisibility(View.GONE);
                } else {
                    vh.like.setVisibility(View.VISIBLE);
                }
            }

            View.OnClickListener userCenterListener = v -> {
                if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(item.getAuthor().getUId())){
                    // 登录用户点击自己头像
                    UIHelper.showMine(mContext,0);
                }else {
                    UIHelper.showUserCenter(mContext, item.getAuthor().getUId());
                }
            };
            vh.avatar.setOnClickListener(userCenterListener);

            View.OnClickListener deleteListener = v -> {
                if (!AppContext.getInstance().isLogin()) {
                    AppContext.showToast(R.string.comment_when_no_login);
                    UIHelper.showLoginActivity(mContext);
                    return;
                } else {
                    //自己的帖子点击删除
                    CommentDialog dialog = new CommentDialog(mContext);
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(true);
                    if (item.getAuthor().getUId().equalsIgnoreCase(ClientInfo.getUID())) {
                        dialog.hideReplyButton();
                    } else {
                        dialog.hideDeleteButton();
                    }
                    if (item.getPicture() != null && !TextUtils.isEmpty(item.getPicture().getUri())) {
                        dialog.hideCopyButton();
                    }
                    dialog.setDeleteCallback(new CommentDialog.DeleteCallBack() {
                        @Override
                        public void onDeleteClick() {
                            DiscoveryManager.getInstance().deleteComment(item.getCid(),
                                    new IListener() {
                                        @Override
                                        public void onSuccess(Object obj) {
                                            new Handler(BaseApplication.context().getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mDatas.remove(position);
                                                    notifyDataSetChanged();
                                                }
                                            });

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
                            //别人的帖子点击回复
                            if (commentCallback != null) {
                                commentCallback.onComment(position, item.getAuthor());
                            }
                        }
                    });
                    dialog.setCopyCallback(new CommentDialog.CopyCallBack() {
                        @Override
                        public void onCopyClick() {
                            StringUtil.copyToClipboard(item.getContent());
                        }
                    });
                    dialog.show();
                }
            };
            //点击删除自己的帖子
            vh.commentLayout.setOnClickListener(deleteListener);
            vh.author.setOnClickListener(deleteListener);
            vh.time.setOnClickListener(deleteListener);
            vh.content.setOnClickListener(deleteListener);
            vh.handle.setOnClickListener(deleteListener);

            View.OnClickListener viewListener = v -> {
                if (item.getPicture() != null && item.getPicture().getUri() != null) {
                    UIHelper.showImagePreview(v.getContext(), new String[]{item.getPicture().getUri()});
                }
            };
            vh.image.setOnClickListener(viewListener);
        }
        return convertView;
    }

    static class ViewHolder {

        @InjectView(R.id.comment_container)
        LinearLayout commentLayout;

        @InjectView(R.id.comment_author)
        TextView author;

        @InjectView(R.id.comment_time)
        TextView time;

        @InjectView(R.id.comment_content_text)
        TextView content;

        @InjectView(R.id.comment_content_image)
        ImageView image;

        @InjectView(R.id.btn_like)
        FollowView like;

        @InjectView(R.id.avatar)
        AvatarView avatar;

        @InjectView(R.id.iv_handle_comment)
        ImageView handle;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private SpannableStringBuilder addClickablePart(final Context context, CommentInfo commentInfo) {
        String text;
        if (commentInfo.getAuthor() != null) {
            if (commentInfo.isReply()) {
                text = commentInfo.getAuthor().getUsername() + "回复@" + commentInfo.getReply().getUsername()/* + ": " + commentInfo.getContent()*/;
            } else {
                text = commentInfo.getAuthor().getUsername()/* + ": " + commentInfo.getContent()*/;
            }
            SpannableString spanStr = new SpannableString(text);
            SpannableStringBuilder ssb = new SpannableStringBuilder(spanStr);
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                    if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(commentInfo.getAuthor().getUId())) {
                        // 登录用户点击自己头像
                        UIHelper.showMine(context, 0);
                    } else {
                        UIHelper.showUserCenter(context, commentInfo.getAuthor().getUId());
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

            }, 0, commentInfo.getAuthor().getUsername().length(), 0);

            if (commentInfo.isReply()) {
                int len = (commentInfo.getAuthor().getUsername() + "回复").length();
                ssb.setSpan(new ClickableSpan() {

                    @Override
                    public void onClick(View widget) {
                        // TODO 进入用户中心
                        if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(commentInfo.getAuthor().getUId())) {
                            // 登录用户点击自己头像
                            UIHelper.showMine(context, 0);
                        } else {
                            UIHelper.showUserCenter(context, commentInfo.getAuthor().getUId());
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

                }, len, commentInfo.getReply().getUsername().length() + len + 1, 0);

            }
            return ssb;
        } else {
            return null;
        }
    }
}
