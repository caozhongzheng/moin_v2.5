package com.moinapp.wuliao.modules.discovery.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.Messages;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.CosplayMsg;
import com.moinapp.wuliao.modules.discovery.model.TagInfo;
import com.moinapp.wuliao.modules.discovery.ui.CosplayEvolutionActivity;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.KJAnimations;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.Tools;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.AvatarView;
import com.moinapp.wuliao.widget.LikeLayout;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 3.2.7版本以前用
 * 发现列表适配器
 */
public class DiscoveryAdapter extends ListBaseAdapter<CosplayInfo> {

    private static final ILogger MyLog = LoggerFactory.getLogger(DiscoveryAdapter.class.getSimpleName());
    private static final String FROM = "comment";
    private Activity mActivity;

    public DiscoveryAdapter(Activity activity) {
        mActivity = activity;
    }

    static class ViewHolder {
        // 大咖秀图片
        @InjectView(R.id.iv_cosplay_image)
        ImageView mIvCosplayImage;

        // 点赞
        @InjectView(R.id.like_layout)
        LikeLayout mLyLike;

        @InjectView(R.id.rl_image)
        RelativeLayout mRlImage;
        @InjectView(R.id.ll_contentinfo)
        LinearLayout mLlContentInfo;

        // 作者
        @InjectView(R.id.rl_user_info)
        RelativeLayout mUserInfo;
        @InjectView(R.id.iv_cosplay_face)
        AvatarView mFace;
        @InjectView(R.id.tv_author_name)
        TextView mTvAuthorName;
        @InjectView(R.id.tv_discovery_time)
        TextView mTvTime;
        @InjectView(R.id.tv_desc)
        TextView mTvContent;

        @InjectView(R.id.ll_content_item0)
        LinearLayout mLyContent0;
        @InjectView(R.id.ll_content_item1)
        LinearLayout mLyContent1;
        @InjectView(R.id.ll_content_item2)
        LinearLayout mLyContent2;

        // 浏览和浏览数
        @InjectView(R.id.ll_discovery_viewuser)
        LinearLayout mLyViewuser;
        @InjectView(R.id.iv_discovery_viewuser)
        ImageView mIvViewuser;
        @InjectView(R.id.iv_discovery_viewnum)
        TextView mTvViewnum;

        // 评论和评论数
        @InjectView(R.id.ll_discovery_comment)
        LinearLayout mLyComment;
        @InjectView(R.id.iv_discovery_comment)
        ImageView mIvComment;
        @InjectView(R.id.tv_discovery_commentnum)
        TextView mTvCommentnum;

        // 转发和转发数
        @InjectView(R.id.ll_discovery_forward)
        LinearLayout mLyForward;
        @InjectView(R.id.iv_discovery_forward)
        ImageView mIvForward;
        @InjectView(R.id.tv_discovery_forwardnum)
        TextView mTvForwardnum;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private Context context;
    private int mPosition = 0;
    private boolean animShow = true;
    private static int IMG_WIDTH = (int) TDevice.getScreenWidth();
    private static int LIKE_IMG_TOPMARGIN = IMG_WIDTH * 57 / 100;
    private static int LL_CONTENTINFO_TOPMARGIN = IMG_WIDTH * 70 / 100;//57

    final private AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
        }
    };

    //关注和发现页面在下拉时不显示正在加载的信息
    @Override
    public void setFooterViewLoading() {
        HideFooterViewLoading();
    }

    @Override
    protected View getRealView(final int position, View convertView,
                               final ViewGroup parent) {
        context = parent.getContext();
        if (mPosition < position) {
            mPosition = position;
            animShow = true;
        } else {
            animShow = false;
        }
        final ViewHolder vh;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_discovery_cosplay_item_new, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) vh.mRlImage.getLayoutParams();
        params.topMargin = 0;//-13

        params = (ViewGroup.MarginLayoutParams) vh.mLyLike.getLayoutParams();
        params.topMargin = LIKE_IMG_TOPMARGIN;

        params = (ViewGroup.MarginLayoutParams) vh.mLlContentInfo.getLayoutParams();
        params.topMargin = LL_CONTENTINFO_TOPMARGIN;

        final CosplayInfo item = mDatas.get(position);

        updateCosplayInfo(vh, item, parent.getContext(), position, animShow);

        return convertView;
    }

    private void updateCosplayInfo(ViewHolder vh, CosplayInfo cosplay, Context context, int position, boolean animShow) {
        if (cosplay == null) return;
        MyLog.i("CosplayInfo=" + cosplay.toString());
        // 头像和作者名
        try {
            if (cosplay.getAuthor() != null) {

                UserInfo author = cosplay.getAuthor();
                vh.mFace.setUserInfo(author.getUId(), author.getUsername());
                if (author.getAvatar() != null) {
                    vh.mFace.setAvatarUrl(author.getAvatar().getUri());
                } else {
                    vh.mFace.setAvatarUrl(null);
                }

                vh.mTvAuthorName.setText(author.getUsername());

                View.OnClickListener userCenterListener = v -> {
                    //TODO 进入用户中心w
                    if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(cosplay.getAuthor().getUId())){
                        // 登录用户点击自己头像
                        UIHelper.showMine(context,0);
                    }else {
                        UIHelper.showUserCenter(context, cosplay.getAuthor().getUId());
                    }
                };
                vh.mFace.setOnClickListener(userCenterListener);
                vh.mUserInfo.setOnClickListener(userCenterListener);
            }
        } catch (Exception e) {
            MyLog.e(e);
        }

        // 发布时间
        vh.mTvTime.setText(StringUtil.humanDate(cosplay.getCreatedAt(), StringUtil.TIME_PATTERN));

        // 发布的图,大咖秀
        try {
            ViewGroup.LayoutParams params = vh.mIvCosplayImage.getLayoutParams();
            params.width = IMG_WIDTH;
            params.height = IMG_WIDTH;
            vh.mIvCosplayImage.setLayoutParams(params);

            if (cosplay.getPicture() != null) {
                ImageLoaderUtils.displayHttpImage(cosplay.getPicture().getUri(), vh.mIvCosplayImage, null, animShow, null);
            }
            vh.mIvCosplayImage.setOnClickListener(v -> {
                // TODO 进入图片详情
                if (position >= 0 && position < 6) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(UmengConstants.ITEM_ID, cosplay.getUcid() + "");
                    map.put(UmengConstants.FROM,   "发现页");
                    MobclickAgent.onEvent(mActivity, UmengConstants.HOTPIC_CLICK, map);
                }
                MyLog.i("进入图片详情:" + cosplay.toString());
                UIHelper.showDiscoveryCosplayDetail(context, cosplay, cosplay.getUcid(), TimeUtils.getCurrentTimeInLong());
            });
        } catch (Exception e) {
            MyLog.e(e);
        }
        // 发布说明
        cosplay.setContentView(context, vh.mTvContent);
        // 点赞状态和点击事件,动画等
//        setLikeState(vh.mIvLike, vh.mTvLiked, cosplay.getIsLike() == 1, false);
//        vh.mLyLike.setOnClickListener(v -> {
//            handleLikeOrNot(context, cosplay, null, vh.mIvLike, vh.mTvLiked, null, null);
//        });
        vh.mLyLike.setContent(cosplay);

        updateContent(vh, cosplay, context);
        updateBottomInfo(vh, cosplay, context);
    }

    @Override
    public boolean isNeedLogin() {
        return false;
    }

    // 设置点赞状态
    private void setLikeState(ImageView view, TextView textView, boolean liked, boolean anim) {
        if (liked) {
            view.setImageResource(R.drawable.icon_cosplay_like_selected);
            textView.setVisibility(View.VISIBLE);
        } else {
            view.setImageResource(R.drawable.icon_cosplay_like_normal);
            textView.setVisibility(View.GONE);
        }
        if (anim) {
            view.setAnimation(KJAnimations.getScaleAnimation(1.5f, 300));
        }
    }


    /**
     * 点赞或者取消点赞
     *
     * @param mLlLike      底部点赞用户列表的parent布局
     * @param mIvLike      大咖秀图片右上角的点赞按钮
     * @param mTvLiked     大咖秀图片右上角的点赞+1文字
     * @param mTvLikeCount 底部点赞用户列表的总个数
     * @param mLikeUser    底部点赞用户列表的用户列表
     */
    public void handleLikeOrNot(Context context, CosplayInfo mCosplayInfo, LinearLayout mLlLike,
                                ImageView mIvLike, TextView mTvLiked, TextView mTvLikeCount, TextView mLikeUser) {
        if (Tools.isFastDoubleClick()) {
            return;
        }
        if (!isHasNetAndLogin(context)) {
            return;
        }
        final boolean isLiked = mCosplayInfo.getIsLike() == 1 ? true : false;
//        DiscoveryManager.getInstance().likeCosplay(mCosplayInfo.getUcid(), isLiked ? 0 : 1, new IListener2() {
//
//            @Override
//            public void onSuccess(Object obj) {
//                List<UserInfo> likeList = mCosplayInfo.getLikeUsers();
//                if (isLiked) {
//                    AppContext.showToast(R.string.del_like_success);
//                    MyLog.i(context.getString(R.string.del_like_success));
//                    int x = -1;
//                    if (likeList != null) {
//                        for (int i = 0; i < likeList.size(); i++) {
//                            UserInfo us = likeList.get(i);
//                            if (us.getUId().equals(ClientInfo.getUID())) {
//                                x = i;
//                                break;
//                            }
//                        }
//                    }
//
//                    if (x >= 0) {
//                        likeList.remove(x);
//                    }
//                    mCosplayInfo.setIsLike(0);
//                    mCosplayInfo.setLikeNum(mCosplayInfo.getLikeNum() - 1);
//                } else {
//                    AppContext.showToast(R.string.add_like_success);
//                    MyLog.i(context.getString(R.string.add_like_success));
//
//                    if (likeList == null) {
//                        likeList = new ArrayList<UserInfo>();
//                    }
//                    likeList.add(0, ClientInfo.getLoginUser());
//                    mCosplayInfo.setIsLike(1);
//                    mCosplayInfo.setLikeNum(mCosplayInfo.getLikeNum() + 1);
//                }
//                mCosplayInfo.setLikeUsers(likeList);
//
//                setLikeState(mIvLike, mTvLiked, !isLiked, true);
//                notifyDataSetChanged();
//                // 修改点赞列表
////                setLikeUser(context, mCosplayInfo, mLlLike, mTvLikeCount, mLikeUser);
//            }
//
//            @Override
//            public void onErr(Object obj) {
//                if (isLiked) {
//                    AppContext.showToast(R.string.del_like_faile);
//                    MyLog.i(context.getString(R.string.del_like_faile));
//                } else {
//                    AppContext.showToast(R.string.add_like_faile);
//                    MyLog.i(context.getString(R.string.add_like_faile));
//                }
//            }
//
//            @Override
//            public void onNoNetwork() {
//
//            }
//        });
    }

    private void updateContent(ViewHolder vh, CosplayInfo cosplay, Context context) {
        updateContentView(vh.mLyContent0, cosplay, context, 0);
        updateContentView(vh.mLyContent1, cosplay, context, 1);
        updateContentView(vh.mLyContent2, cosplay, context, 2);
    }

    private void updateContentView(LinearLayout ll, CosplayInfo cosplay, Context context, int i) {
        if (cosplay.getMsgList() == null
                || i > cosplay.getMsgList().size() - 1
                || cosplay.getMsgList().get(i) == null) {
            ll.setVisibility(View.GONE);
        } else {
            ll.setVisibility(View.VISIBLE);
            CosplayMsg msg = cosplay.getMsgList().get(i);

            ImageView icon = (ImageView) ll.findViewById(R.id.icon_action);
            TextView content = (TextView) ll.findViewById(R.id.content);
            setupIcon(icon, msg);
            setupContent(content, msg);
        }
    }

    private void setupIcon(ImageView imageView, CosplayMsg msg) {
        switch (msg.getAction()) {
            case Messages.ACTION_LIKE_COSPLAY:
                imageView.setImageResource(R.drawable.icon_like_content);
                break;
            case Messages.ACTION_COMMENT_COSPLAY:
            case Messages.ACTION_REPLY_COSPLAY:
            case Messages.ACTION_COMMENT_LIKE_COSPLAY:
            case Messages.ACTION_REPLY_COMMENT_COSPLAY:
                imageView.setImageResource(R.drawable.icon_comment_content);
                break;
            case Messages.ACTION_FORWARD_COSPLAY:
            case Messages.ACTION_MODIFY_COSPLAY:
                imageView.setImageResource(R.drawable.change_black_small);
                break;
            case Messages.ACTION_AT_COSPLAY:
            case Messages.ACTION_FOLLOW:
                imageView.setImageResource(R.drawable.small_follow_gray);
                break;
            case Messages.ACTION_FOLLOW_TAG:
            case Messages.ACTION_FROM_TAG:
                imageView.setImageResource(R.drawable.icon_tag_content);
                break;
        }
    }

    private void setupContent(TextView textView, CosplayMsg msg) {
        String fixContent1 = "";
        String fixContent2 = "";
        String more = "等";
        switch (msg.getAction()) {
            case Messages.ACTION_LIKE_COSPLAY:
                fixContent1 = "赞了";
                if (msg.getFromUsers() != null && msg.getFromUsers().size() > 1) {
                    fixContent1 = more + fixContent1;
                }
                if (msg.getTargetUsers() != null && msg.getTargetUsers().size() > 0) {
                    fixContent2 = "的图";
                } else {
                    fixContent2 = "这张图";
                }

                break;
            case Messages.ACTION_COMMENT_COSPLAY:
            case Messages.ACTION_REPLY_COSPLAY:
            case Messages.ACTION_COMMENT_LIKE_COSPLAY:
            case Messages.ACTION_REPLY_COMMENT_COSPLAY:
                fixContent1 = "评论了";
                if (msg.getFromUsers() != null && msg.getFromUsers().size() > 1) {
                    fixContent1 = more + fixContent1;
                }
                if (msg.getTargetUsers() != null && msg.getTargetUsers().size() > 0) {
                    fixContent2 = "的图";
                } else {
                    fixContent2 = "这张图";
                }
                break;
            case Messages.ACTION_FORWARD_COSPLAY:
            case Messages.ACTION_MODIFY_COSPLAY:
                fixContent1 = "转改了";
                if (msg.getFromUsers() != null && msg.getFromUsers().size() > 1) {
                    fixContent1 = more + fixContent1;
                }
                if (msg.getTargetUsers() != null && msg.getTargetUsers().size() > 0) {
                    fixContent2 = "的图";
                } else {
                    fixContent2 = "这张图";
                }
                break;
            case Messages.ACTION_FOLLOW:
                fixContent1 = "最近关注了";
                break;
            case Messages.ACTION_FOLLOW_TAG:
                fixContent1 = "最近关注了";
                break;
            case Messages.ACTION_FROM_TAG:
                fixContent1 = "来自";
                break;
        }

        try {
            SpannableStringBuilder wholeContent = buildContentString(fixContent1, fixContent2, msg);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setFocusable(false);
            textView.setLongClickable(false);
            textView.setText(wholeContent, TextView.BufferType.SPANNABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SpannableStringBuilder buildContentString(String fixContent1, String fixContent2, CosplayMsg msg) {
        int maxLength = 24;
        SpannableStringBuilder wholeContent = new SpannableStringBuilder();
        int start = 0;
        if (msg.getFromUsers() != null) {
            boolean lost = false;
            for (int i = 0; i < msg.getFromUsers().size(); i++) {
                UserInfo fromUser = msg.getFromUsers().get(i);
                if (fromUser == null || fromUser.getUsername() == null) continue;
                if (fromUser.getUsername().length() + wholeContent.length() + fixContent1.length()
                        + fixContent2.length() <= maxLength) {
                    wholeContent.append(fromUser.getUsername());
                    if (i < msg.getFromUsers().size() - 1) {
                        wholeContent.append(",");
                    }
                    wholeContent.setSpan(new ClickableSpan() {

                        @Override
                        public void onClick(View widget) {
                            if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(fromUser.getUId())){
                                // 登录用户点击自己头像
                                UIHelper.showMine(context,0);
                            }else {
                                UIHelper.showUserCenter(context, fromUser.getUId());
                            }
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            // 设置文本颜色
                            ds.setColor(context.getResources().getColor(R.color.gray));
                            // 去掉下划线
                            ds.setUnderlineText(false);
                        }

                    }, start, start + fromUser.getUsername().length(), 0);
                    start = wholeContent.length();
                } else {
                    lost = true;
                    break;
                }
            }
            if (lost && wholeContent.length() > 0) {
                wholeContent.delete(wholeContent.length() - 1, wholeContent.length());
            }
        }
        wholeContent.append(fixContent1);
        start = wholeContent.length();
        if (msg.getAction() == Messages.ACTION_FOLLOW_TAG
                || msg.getAction() == Messages.ACTION_FROM_TAG) {
            if (msg.getTargetTags() != null) {
                for (int i = 0; i < msg.getTargetTags().size(); i++) {
                    TagInfo tag = msg.getTargetTags().get(i);
                    if (tag.getName().length() + wholeContent.length()
                            <= maxLength) {
                        String name = null;
                        if (tag.getType().equalsIgnoreCase("IP")) {
                            name = "《" + tag.getName() + "》";
                        } else if (tag.getType().equalsIgnoreCase("OP")) {
                            name = "#" + tag.getName();
                        } else if (tag.getType().equalsIgnoreCase("TP")) {
                            name = tag.getName();
                        }

                        if (StringUtils.isEmpty(name)) {
                            continue;
                        }
                        wholeContent.append(name);
                        if (i < msg.getTargetTags().size() - 1) {
                            wholeContent.append(" ");
                        }
                        wholeContent.setSpan(new ClickableSpan() {

                            @Override
                            public void onClick(View widget) {
                                if ("TP".equalsIgnoreCase(tag.getType())) {
                                    UIHelper.showTopicDetail(context, tag.getName(), tag.getType(), tag.getTagId(), 0);
                                } else {
                                    UIHelper.showTagDetail(context, tag.getName(), tag.getType(), 0);
                                }
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                // 设置文本颜色
                                ds.setColor(context.getResources().getColor(R.color.gray));
                                // 去掉下划线
                                ds.setUnderlineText(false);
                            }

                        }, start, start + name.length(), 0);
                        start = wholeContent.length();
                    }
                }
            }
        } else {
            if (msg.getTargetUsers() != null) {
                boolean lost = false;
                for (int i = 0; i < msg.getTargetUsers().size(); i++) {
                    UserInfo targetUser = msg.getTargetUsers().get(i);
                    if (targetUser == null || targetUser.getUsername() == null) continue;
                    if (targetUser.getUsername().length() + wholeContent.length()
                            + fixContent2.length() <= maxLength) {
                        wholeContent.append(targetUser.getUsername());
                        if (i < msg.getTargetUsers().size() - 1) {
                            wholeContent.append(",");
                        }
                        wholeContent.setSpan(new ClickableSpan() {

                            @Override
                            public void onClick(View widget) {
                                if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(targetUser.getUId())){
                                    // 登录用户点击自己头像
                                    UIHelper.showMine(context,0);
                                }else {
                                    UIHelper.showUserCenter(context, targetUser.getUId());
                                }
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                // 设置文本颜色
                                ds.setColor(context.getResources().getColor(R.color.gray));
                                // 去掉下划线
                                ds.setUnderlineText(false);
                            }

                        }, start, start + targetUser.getUsername().length(), 0);
                        start = wholeContent.length();
                    } else {
                        lost = true;
                        break;
                    }
                }
                if (lost && wholeContent.length() > 0) {
                    wholeContent.delete(wholeContent.length() - 1, wholeContent.length());
                }
            }
        }
        wholeContent.append(fixContent2);
        return wholeContent;
    }


    private void updateBottomInfo(ViewHolder vh, CosplayInfo cosplay, Context context) {
        // 浏览和浏览数
        vh.mTvViewnum.setText(String.valueOf(cosplay.getReadNum()));
        OnClickListener viewClick = null;
        if (cosplay.getReadNum() > 0) {
            viewClick = v -> {
                UIHelper.showUserList(context, cosplay.getUcid(), 2);
            };
        }
        vh.mLyViewuser.setOnClickListener(viewClick);
        vh.mIvViewuser.setOnClickListener(viewClick);

        // 评论和评论数
        vh.mTvCommentnum.setText(String.valueOf(cosplay.getCommentNum()));
        OnClickListener commentClick = null;
        if (cosplay.getCommentNum() > 0) {
            commentClick = v -> {
                UIHelper.showCosplayLikeComment(context, cosplay.getUcid(), 1);
            };
        } else {
            commentClick = v -> {
                UIHelper.showDiscoveryCosplayDetail(mActivity, cosplay, cosplay.getUcid(), FROM, TimeUtils.getCurrentTimeInLong());
            };
        }

        vh.mLyComment.setOnClickListener(commentClick);
        vh.mIvComment.setOnClickListener(commentClick);

        // 转发和转发数
        vh.mIvForward.setSelected(cosplay.getIsWrite() == 1);
        vh.mTvForwardnum.setText(String.valueOf(cosplay.getChildrenNum()));
        OnClickListener forwardClick = null;
        if (cosplay.getChildrenNum() > 0) {
            forwardClick = v -> {
                MyLog.i("进入图片网格模式详情: " + cosplay.getUcid());
                CosplayEvolutionActivity.showCosplayEvolution(context, cosplay.getUcid());
            };
        } else {
            forwardClick = v -> {
                MyLog.i("进入图片详情: " + cosplay.getUcid());
                UIHelper.showDiscoveryCosplayDetail(context, cosplay, cosplay.getUcid(), TimeUtils.getCurrentTimeInLong());
            };
        }
        vh.mLyForward.setOnClickListener(forwardClick);
        vh.mIvForward.setOnClickListener(forwardClick);

    }

}
