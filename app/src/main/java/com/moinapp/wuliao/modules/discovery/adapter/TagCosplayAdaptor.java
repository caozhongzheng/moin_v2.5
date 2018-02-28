package com.moinapp.wuliao.modules.discovery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.ui.CosplayEvolutionActivity;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.Tools;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.AvatarView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 话题详情界面列表适配器(3.2.5版本以前用)
 * Created by liujiancheng on 15/9/22.
 */
public class TagCosplayAdaptor extends ListBaseAdapter<CosplayInfo> {
    private static final ILogger MyLog = LoggerFactory.getLogger(TagCosplayAdaptor.class.getSimpleName());
    private Context context;
    private int mPosition = 0;
    private boolean animShow = true;
    private static int IMG_WIDTH = (int) (TDevice.getScreenWidth() / 2 - TDevice.dpToPixel(18f));
    public TagCosplayAdaptor() {}

    @Override
    public int getDataSize() {
        return (mDatas.size() + 1) / 2;
    }

    @SuppressLint("InflateParams")
    @Override
    protected View getRealView(int position, View convertView,
                               final ViewGroup parent) {
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
                    R.layout.list_cell_tag_cosplay, null);

            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final CosplayInfo item0 = mDatas.get(position * 2);
        final CosplayInfo item1 = (position * 2 + 1 < mDatas.size()) ? mDatas.get(position * 2 + 1) : null;

        if (item0 != null ) {
            setView(item0, vh.item0);
        }

        if (item1 != null) {
            setView(item1, vh.item1);
            vh.item1.setVisibility(View.VISIBLE);
        } else {
            vh.item1.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private void setView(CosplayInfo item, View view) {
//        AvatarView cover = (AvatarView)view.findViewById(R.id.iv_cosplay_face);
//        TextView name = (TextView)view.findViewById(R.id.tv_author_name);
//        TextView update = (TextView)view.findViewById(R.id.tv_discovery_time);
//        ImageView image = (ImageView)view.findViewById(R.id.iv_cosplay_image);
//        TextView likecount = (TextView)view.findViewById(R.id.iv_discovery_likenum);
//        TextView forwardcount = (TextView)view.findViewById(R.id.iv_discovery_forwardnum);
//
//        mImageLoader.displayImage(item.getPicture().getUri(), image, BitmapUtil.getImageLoaderOption());
//        if (item.getIsLike() == 1) {
//            image.setImageResource(R.drawable.umeng_socialize_action_like);
//        } else {
//            image.setImageResource(R.drawable.umeng_socialize_action_unlike);
//        }
//        if (item.getAuthor().getAvatar()!= null)
//            cover.setAvatarUrl(item.getAuthor().getAvatar().getUri());
//        name.setText(item.getAuthor().getUsername());
//        likecount.setText(String.valueOf(item.getLikeNum()));
//        forwardcount.setText(String.valueOf(item.getChildrenNum()));
//        update.setText(StringUtil.humanDate(item.getCreatedAt(), StringUtil.TIME_PATTERN));
//        image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UIHelper.showDiscoveryCosplayDetail(view.getContext(), item, item.getUcid());
//            }
//        });
//        cover.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UIHelper.showFriends(view.getContext(), item.getAuthor().getUId(), 0);
//            }
//        });
        updateCosplayInfo(view, item, context);
    }

    private void updateCosplayInfo(View view, CosplayInfo cosplay, Context context) {
        MyLog.i("CosplayInfo=" + cosplay.toString());
        // 头像和作者名
        try {
            if (cosplay.getAuthor() != null) {
                AvatarView face = (AvatarView) view.findViewById(R.id.iv_cosplay_face);

                UserInfo author = cosplay.getAuthor();
                face.setUserInfo(author.getUId(), author.getUsername());
                if (author.getAvatar() != null) {
                    face.setAvatarUrl(author.getAvatar().getUri());
                } else {
                    face.setAvatarUrl(null);
                }

                TextView author_name = (TextView) view.findViewById(R.id.tv_author_name);
                author_name.setText(author.getUsername());

                View.OnClickListener userCenterListener = v -> {
                    //TODO 进入用户中心
                    if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(cosplay.getAuthor().getUId())) {
                        // 登录用户点击自己头像
                        UIHelper.showMine(context, 0);
                    } else {
                        UIHelper.showUserCenter(context, cosplay.getAuthor().getUId());
                    }
                };
                face.setOnClickListener(userCenterListener);
                author_name.setOnClickListener(userCenterListener);
            }
        } catch (Exception e) {
            MyLog.e(e);
        }

        // 发布时间
        TextView time = (TextView) view.findViewById(R.id.tv_discovery_time);
        time.setText(StringUtil.humanDate(cosplay.getCreatedAt(), StringUtil.TIME_PATTERN));

        // 发布的图,大咖秀
        try {
            ImageView cosplay_image = (ImageView) view.findViewById(R.id.iv_cosplay_image);
            ViewGroup.LayoutParams params = cosplay_image.getLayoutParams();
            params.width = IMG_WIDTH;
            params.height = IMG_WIDTH;
            cosplay_image.setLayoutParams(params);

            if (cosplay.getPicture() != null) {
                ImageLoaderUtils.displayHttpImage(cosplay.getPicture().getUri(), cosplay_image, null, animShow, null);
            }
            cosplay_image.setOnClickListener(v -> {
                // TODO 进入图片详情
                MyLog.i("进入图片详情:" + cosplay.toString());
                UIHelper.showDiscoveryCosplayDetail(context, cosplay, cosplay.getUcid(), TimeUtils.getCurrentTimeInLong());
            });
        } catch (Exception e) {
            MyLog.e(e);
        }

        // 点赞 和赞数
        LinearLayout likeuser_ll = (LinearLayout) view.findViewById(R.id.ll_discovery_likeuser);
        ImageView likeuser = (ImageView) view.findViewById(R.id.iv_discovery_likeuser);
        setLikeState(likeuser, cosplay.getIsLike() == 1);
        TextView likenum = (TextView) view.findViewById(R.id.iv_discovery_likenum);
        likenum.setText(String.valueOf(cosplay.getLikeNum()));
        View.OnClickListener likeClick = v -> {
            if (AppContext.getInstance().isLogin()) {
                updateLikeState(likeuser, likenum, cosplay);
            } else {
                AppContext.showToast("先登录再赞~");
                UIHelper.showLoginActivity(context);
            }
        };
        likeuser.setOnClickListener(likeClick);
        likeuser_ll.setOnClickListener(likeClick);

        // 评论和评论数
        LinearLayout comment_ll = (LinearLayout) view.findViewById(R.id.ll_discovery_comment);
        if (cosplay.getCommentNum() > 0) {
            comment_ll.setVisibility(View.VISIBLE);

            ImageView comment = (ImageView) view.findViewById(R.id.iv_discovery_comment);
            comment.setSelected(cosplay.getIsWrite() == 1);
            TextView commentNum = (TextView) view.findViewById(R.id.tv_discovery_commentnum);
            commentNum.setText(String.valueOf(cosplay.getCommentNum()));
            View.OnClickListener commentClick = v -> {
                if (AppContext.getInstance().isLogin()) {
                    MyLog.i("进入评论列表: " + cosplay.getUcid());
                    UIHelper.showCosplayLikeComment(context, cosplay.getUcid(), 1);
                } else {
                    AppContext.showToast("请先登录再进行评论~");
                    UIHelper.showLoginActivity(context);
                }
            };
            comment.setOnClickListener(commentClick);
            comment_ll.setOnClickListener(commentClick);
        } else {
            comment_ll.setVisibility(View.GONE);
        }

        // 转发和转发数
        LinearLayout forward_ll = (LinearLayout) view.findViewById(R.id.ll_discovery_forward);
        if (cosplay.getChildrenNum() > 0) { // 允许改图转发
            forward_ll.setVisibility(View.VISIBLE);

            ImageView forward = (ImageView) view.findViewById(R.id.iv_discovery_forward);
            forward.setSelected(cosplay.getIsWrite() == 1);
            TextView forwardnum = (TextView) view.findViewById(R.id.iv_discovery_forwardnum);
            forwardnum.setText(String.valueOf(cosplay.getChildrenNum()));
            View.OnClickListener forwardClick = v -> {
                if (AppContext.getInstance().isLogin()) {
                    MyLog.i("进入图片网格模式详情: " + cosplay.getUcid());
                    CosplayEvolutionActivity.showCosplayEvolution(context, cosplay.getUcid());
                } else {
                    AppContext.showToast("先登录再进网格模式~");
                    UIHelper.showLoginActivity(context);
                }
            };
            forward.setOnClickListener(forwardClick);
            forward_ll.setOnClickListener(forwardClick);
        } else {
            forward_ll.setVisibility(View.GONE);
        }

    }

    // 点赞/取消赞
    private void updateLikeState(ImageView likeUsers, TextView likeNum, CosplayInfo mCosplayInfo) {
        if(Tools.isFastDoubleClick()) {
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
//                    // TODO 取消点赞
//                    AppContext.showToast(R.string.del_like_success);
//                    MyLog.i(context.getString(R.string.del_like_success));
//                    if (likeList != null && !likeList.isEmpty()) {
//                        int x = -1;
//                        for (int i = 0; i < likeList.size(); i++) {
//                            UserInfo us = likeList.get(i);
//                            if (us.getUId().equals(ClientInfo.getUID())) {
//                                x = i;
//                                break;
//                            }
//                        }
//
//                        if (x >= 0) {
//                            likeList.remove(x);
//                            mCosplayInfo.setIsLike(0);
//                            mCosplayInfo.setLikeNum(mCosplayInfo.getLikeNum() - 1);
//                            likeNum.setText(mCosplayInfo.getLikeNum() + "");
//                            //likeNum.setTextColor(AppContext.getInstance().getResources().getColor(R.color.gray));
//                        }
//                    }
//                } else {
//                    // TODO 点赞
//                    AppContext.showToast(R.string.add_like_success);
//                    MyLog.i(context.getString(R.string.add_like_success));
//
//                    if (likeList == null) {
//                        likeList = new ArrayList<UserInfo>();
//                    }
//                    likeList.add(0, ClientInfo.getLoginUser());
//                    mCosplayInfo.setIsLike(1);
//                    mCosplayInfo.setLikeNum(mCosplayInfo.getLikeNum() + 1);
//                    likeNum.setText(mCosplayInfo.getLikeNum() + "");
//                    //likeNum.setTextColor(AppContext.getInstance().getResources().getColor(R.color.day_colorPrimary));
//
//                    likeUsers.setAnimation(KJAnimations.getScaleAnimation(1.5f, 300));
//                }
//                mCosplayInfo.setLikeUsers(likeList);
//
//                setLikeState(likeUsers, !isLiked);
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

    private void setLikeState(ImageView view, boolean liked) {
        if (liked) {
            view.setImageResource(R.drawable.icon_like_on_new_small);
        } else {
            view.setImageResource(R.drawable.icon_like_new_small);
        }
    }
    static class ViewHolder {
        @InjectView(R.id.cosplay_list_item0)
        View item0;

        @InjectView(R.id.cosplay_list_item1)
        View item1;


        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}

