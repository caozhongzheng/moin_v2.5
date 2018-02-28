package com.moinapp.wuliao.modules.discovery.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.CommentOnClickListener;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.ui.CosplayEvolutionActivity;
import com.moinapp.wuliao.modules.discovery.ui.FollowFragment;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.AvatarView;
import com.moinapp.wuliao.widget.LikeLayout;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 关注频道列表适配器
 *
 * @author moying
 * @date 2015年10月15日
 */
public class FollowAdapter extends ListBaseAdapter<CosplayInfo> {
    private static final ILogger MyLog = LoggerFactory.getLogger(FollowAdapter.class.getSimpleName());
    private static final String FROM = "comment";
    static class ViewHolder {
        @InjectView(R.id.iv_avatar)
        AvatarView mIvAvatar;
        @InjectView(R.id.tv_name)
        TextView mTvName;
        @InjectView(R.id.tv_time)
        TextView mTvTime;
        @InjectView(R.id.tv_desc)
        TextView mTvDes;

        @InjectView(R.id.ll_viewuser)
        LinearLayout mLlLook;
        @InjectView(R.id.tv_viewnum)
        TextView mTvLookCount;

        @InjectView(R.id.ll_comment)
        LinearLayout mLlComment;
        @InjectView(R.id.tv_commentnum)
        TextView mTvComment;

        @InjectView(R.id.ll_forward)
        LinearLayout mLlForward;
        @InjectView(R.id.tv_forwardnum)
        TextView mTvForwardNum;

        @InjectView(R.id.fl_image_area)
        FrameLayout mIvContainer;
        @InjectView(R.id.iv_image)
        ImageView mIvCosplay;
        @InjectView(R.id.rl_upload)
        LinearLayout mRlUpload;
        @InjectView(R.id.sticker_refresh_progress_bar)
        ProgressBar mProcessBar;
        @InjectView(R.id.iv_upload)
        ImageView mIvUpload;
        @InjectView(R.id.tv_upload)
        TextView mTvUpload;
        @InjectView(R.id.rl_user_info)
        RelativeLayout mRlUserInfo;
        @InjectView(R.id.like_layout)
        LikeLayout mLikeView;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private Context context;
    private Activity activity;
    private int mPosition = 0;
    private boolean animShow = true;

    public FollowAdapter(Activity activity) {
        this.activity = activity;
    }

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
        LayoutInflater layoutInflater = getLayoutInflater(parent.getContext());
        if (convertView == null || convertView.getTag() == null) {
            convertView = layoutInflater.inflate(
                    R.layout.list_cell_follow_channel, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final CosplayInfo mCosplayInfo = mDatas.get(position);
        // -------------作者头像,名称和发布时间
        if (mCosplayInfo.getAuthor() != null) {
            if (mCosplayInfo.getAuthor().getAvatar() != null) {
                vh.mIvAvatar.setAvatarUrl(mCosplayInfo.getAuthor().getAvatar().getUri());
            } else {
                vh.mIvAvatar.setAvatarUrl(null);
            }
            vh.mIvAvatar.setUserInfo(mCosplayInfo.getAuthor().getUId(), mCosplayInfo.getAuthor().getUsername());
            vh.mTvName.setText(mCosplayInfo.getAuthor().getUsername());
            View.OnClickListener userCenterListener = v -> {
                //TODO 进入用户中心
                if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(mCosplayInfo.getAuthor().getUId())) {
                    // 登录用户点击自己头像
                    UIHelper.showMine(context, 0);
                } else {
                    UIHelper.showUserCenter(context, mCosplayInfo.getAuthor().getUId());
                }
            };
            vh.mIvAvatar.setOnClickListener(userCenterListener);
            vh.mRlUserInfo.setOnClickListener(userCenterListener);
        }
        vh.mTvTime.setText(StringUtil.humanDate(mCosplayInfo.getCreatedAt(), StringUtil.TIME_PATTERN));

        // -------------发布的说明
        mCosplayInfo.setContentView(context, vh.mTvDes);
        // -------------发布的大咖秀
        FrameLayout.LayoutParams cosImageParams = (FrameLayout.LayoutParams) vh.mIvCosplay.getLayoutParams();
        ViewGroup.LayoutParams cosImageContainerParams = vh.mIvContainer.getLayoutParams();
        cosImageParams.height = cosImageParams.width =
                cosImageContainerParams.height = cosImageContainerParams.width =
                        (int) TDevice.getScreenWidth();
        int error = CameraManager.getInst().isFileUploadFailed(mCosplayInfo.getUcid());
        if (!CameraManager.getInst().isCosplayUploaded(mCosplayInfo, true) || error != -1) {
            MyLog.i("ljc: 图片未上传, 显示本地图片,ucid=" + mCosplayInfo.getUcid());
            File pic = new File(CameraManager.getInst().getCosplayElementPath(mCosplayInfo.getUcid(), 1));
            if (pic != null && pic.exists()) {
                ImageLoaderUtils.displayLocalImage(pic.getAbsolutePath(), vh.mIvCosplay, null);
            } else {
                if (mCosplayInfo.getPicture() != null) {
                    ImageLoaderUtils.displayHttpImage(mCosplayInfo.getPicture().getUri(), vh.mIvCosplay, null, animShow, null);
                }
            }

            //上传失败的状态
            if (error != -1) {
                MyLog.i("ljc: 图片未上传, 显示上传提示, ucid=" + mCosplayInfo.getUcid());
                vh.mRlUpload.setVisibility(View.VISIBLE);

                if (error == 0) {
                    vh.mProcessBar.setVisibility(View.VISIBLE);
                    vh.mIvUpload.setVisibility(View.GONE);
                    vh.mTvUpload.setText(context.getResources().getString(
                            R.string.cosplay_uploading));
                } else {
                    vh.mProcessBar.setVisibility(View.GONE);
                    vh.mIvUpload.setVisibility(View.VISIBLE);
                    vh.mIvUpload.setBackgroundResource(R.drawable.icon_upload_fail);
                    vh.mTvUpload.setText(context.getResources().getString(R.string.cosplay_upload_fail));
                    vh.mRlUpload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CameraManager.getInst().startUpload(activity, mCosplayInfo.getUcid(),
                                    new IListener() {
                                        @Override
                                        public void onSuccess(Object obj) {
                                            MyLog.i("ljc: 上传成功!");
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    vh.mProcessBar.setVisibility(View.GONE);
                                                    vh.mIvUpload.setVisibility(View.VISIBLE);
                                                    vh.mIvUpload.setBackgroundResource(R.drawable.icon_upload_succeed);
                                                    vh.mTvUpload.setText(context.getResources()
                                                            .getString(R.string.cosplay_upload_succeed_tip));
                                                }
                                            });
                                        }

                                        @Override
                                        public void onErr(Object obj) {
                                            MyLog.i("ljc: 上传失败!");
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    vh.mProcessBar.setVisibility(View.GONE);
                                                    vh.mIvUpload.setVisibility(View.VISIBLE);
                                                    vh.mIvUpload.setBackgroundResource(R.drawable.icon_upload_fail);
                                                    vh.mTvUpload.setText(context.getResources()
                                                            .getString(R.string.cosplay_upload_fail));
                                                }
                                            });
                                        }

                                        @Override
                                        public void onNoNetwork() {

                                        }
                                    });
                            vh.mProcessBar.setVisibility(View.VISIBLE);
                            vh.mIvUpload.setVisibility(View.GONE);
                            vh.mTvUpload.setText(context.getResources().getString(
                                    R.string.cosplay_uploading));
                        }
                    });
                }
            } else {
                vh.mRlUpload.setVisibility(View.GONE);
            }
        } else {
            MyLog.i("ljc: 图片已经上传, 显示http图片,ucid=" + mCosplayInfo.getUcid());
            vh.mRlUpload.setVisibility(View.GONE);
            if (mCosplayInfo.getPicture() != null) {
                ImageLoaderUtils.displayHttpImage(mCosplayInfo.getPicture().getUri(), vh.mIvCosplay, null, animShow, null);
            }
        }
        vh.mIvCosplay.setOnClickListener(v -> {
            boolean uploaded = CameraManager.getInst().isCosplayUploaded(mCosplayInfo, true);
            if (!uploaded && (CameraManager.getInst().isFileUploadFailed(mCosplayInfo.getUcid()) != -1)) {
                AppContext.showToast(R.string.cosplay_not_upload_faile);
                return;
            }

            MyLog.i("进入图片网格模式详情: " + mCosplayInfo.getUcid());
//            CosplayEvolutionActivity.showCosplayEvolution(context, mCosplayInfo.getUcid());
            CosplayInfo tmp = new CosplayInfo();
            tmp.setUcid(mCosplayInfo.getUcid());
            tmp.setPicture(mCosplayInfo.getPicture());
            UIHelper.showDiscoveryCosplayDetail(context, tmp, mCosplayInfo.getUcid(),
                    FollowAdapter.class.getSimpleName(), TimeUtils.getCurrentTimeInLong());
        });

        // 点赞
        vh.mLikeView.setContent(mCosplayInfo);

        // 浏览用户
        vh.mTvLookCount.setText(mCosplayInfo.getReadNum() + "");
        vh.mLlLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showUserList(context, mCosplayInfo.getUcid(), 2);
            }
        });

        //---------评论按钮
        View.OnClickListener cmtListener = v -> {
            UIHelper.showDiscoveryCosplayDetail(context, mCosplayInfo, mCosplayInfo.getUcid(), FROM, TimeUtils.getCurrentTimeInLong());
        };
        vh.mLlComment.setOnClickListener(cmtListener);
        vh.mTvComment.setText(mCosplayInfo.getCommentNum() + "");

        //---------改图转发按钮
        MyLog.i("当前用户是否允许修改:" + mCosplayInfo.getIsWrite());
        MyLog.i("改图转发权限:" + mCosplayInfo.getWriteAuth());
        vh.mTvForwardNum.setText(mCosplayInfo.getChildrenNum() + "");
        View.OnClickListener forwardListener = v -> {
            if (isHasNetAndLogin(context)) {
                // TODO 跳到改图转发界面(根据权限)
                boolean uploaded = CameraManager.getInst().isCosplayUploaded(mCosplayInfo, true);
                if (!uploaded && (CameraManager.getInst().isFileUploadFailed(mCosplayInfo.getUcid()) != -1)) {
                    AppContext.showToast(R.string.cosplay_not_upload_faile);
                    return;
                }

                int writeAuth = mCosplayInfo.getWriteAuth();
                MyLog.i("跳到改图转发界面(根据权限)=" + writeAuth);
                UIHelper.editCosplay(context, mCosplayInfo, TimeUtils.getCurrentTimeInLong());
            }
        };
        vh.mLlForward.setOnClickListener(forwardListener);

        return convertView;
    }

    private void prepareComment(int position, UserInfo replyUser) {
        if (isHasNetAndLogin(context)) {
            if (commentCallback != null) {
                commentCallback.onComment(position, replyUser);
                MyLog.i("评论第几个~:" + position);
            }
        }
    }

    public static Bundle buildBundle(String from) {
        return buildBundle(from, 0);
    }

    public static Bundle buildBundle(String from, int childIndex) {
        Bundle bundle = new Bundle();
        bundle.putString(DiscoveryConstants.FROM, from);
        bundle.putInt(CosplayEvolutionActivity.KEY_CHILDREN_INDEX, childIndex);
        return bundle;
    }

    private void setComments(Context context, int position, LinearLayout mLlComments,
                             RelativeLayout mRlComment1, RelativeLayout mRlComment2, Button mBtnShowAllComments) {
        final CosplayInfo mCosplayInfo = mDatas.get(position);
        if (mCosplayInfo == null || mCosplayInfo.getComments() == null || mCosplayInfo.getComments().isEmpty()) {
            mLlComments.setVisibility(View.GONE);
            mBtnShowAllComments.setVisibility(View.GONE);
            return;
        } else {
            mLlComments.setVisibility(View.VISIBLE);
            mBtnShowAllComments.setVisibility(View.VISIBLE);
        }

        int commentCount = mCosplayInfo.getComments().size();
        if (commentCount > 0) {
            mLlComments.setVisibility(View.VISIBLE);
            MyLog.i("评论内容1是[" + mCosplayInfo.getComments().get(0).getContent() + "]");
//            mTvComment1.setText(mCosplayInfo.getComments().get(0).getAuthor().getUsername() + ":"
//                    + mCosplayInfo.getComments().get(0).getContent());
            mCosplayInfo.getComments().get(0).setCommentUser(context, mCosplayInfo, mRlComment1, new CommentOnClickListener() {
                @Override
                public void onDeleteClick(Object object) {
                    mCosplayInfo.getComments().remove(0);
                    mCosplayInfo.setCommentNum(mCosplayInfo.getCommentNum() - 1);
                    setComments(context, position, mLlComments, mRlComment1, mRlComment2, mBtnShowAllComments);
                }

                @Override
                public void onReplyClick(Object object) {
                    prepareComment(position, (UserInfo) object);
                }

                @Override
                public void onCopyClick(Object object) {
                    StringUtil.copyToClipboard((String) object);
                }
            });
            if (commentCount > 1) {
                mRlComment2.setVisibility(View.VISIBLE);
                MyLog.i("评论内容2是[" + mCosplayInfo.getComments().get(1).getContent() + "]");
//                mTvComment2.setText(mCosplayInfo.getComments().get(1).getAuthor().getUsername() + ":"
//                        + mCosplayInfo.getComments().get(1).getContent());
                mCosplayInfo.getComments().get(1).setCommentUser(context, mCosplayInfo, mRlComment2, new CommentOnClickListener() {
                    @Override
                    public void onDeleteClick(Object object) {
                        mCosplayInfo.getComments().remove(1);
                        mCosplayInfo.setCommentNum(mCosplayInfo.getCommentNum() - 1);
                        setComments(context, position, mLlComments, mRlComment1, mRlComment2, mBtnShowAllComments);
                    }

                    @Override
                    public void onReplyClick(Object object) {
                        prepareComment(position, (UserInfo) object);
                    }

                    @Override
                    public void onCopyClick(Object object) {
                        StringUtil.copyToClipboard((String) object);
                    }
                });
            } else {
                mRlComment2.setVisibility(View.GONE);
            }

            mBtnShowAllComments.setVisibility(mCosplayInfo.getCommentNum() > 2 ? View.VISIBLE : View.GONE);
            mBtnShowAllComments.setText("查看全部评论 (" + mCosplayInfo.getCommentNum() + ")");
            mBtnShowAllComments.setOnClickListener(v -> {
                // TODO 跳到该图片的全部评论列表
                MyLog.i("跳到该图片的全部评论列表");
                UIHelper.showCosplayLikeComment(context, mCosplayInfo.getUcid(), 1);
            });
        }

    }
    @Override
    public boolean isNeedLogin() {
        return true;
    }

    @Override
    public String getShareTitle(CosplayInfo obj) {
        return null;
    }

    @Override
    public String getShareContent(CosplayInfo obj) {
        return null;
    }

    @Override
    public String getShareUrl(CosplayInfo obj) {
        return null;
    }

    private FollowFragment fragment;

    public FollowFragment getFragment() {
        return fragment;
    }

    public void setFragment(FollowFragment fragment) {
        this.fragment = fragment;
    }

    private CommentCallback commentCallback = null;

    public interface CommentCallback {
        void onComment(int position, UserInfo replyUser);
    }

    public void setCommentCallback(CommentCallback callback) {
        commentCallback = callback;
    }


    public void refreshUploadStatus(String ucid, boolean succeed) {
        for (CosplayInfo cosplayInfo : mDatas) {
            if (cosplayInfo.getUcid().equalsIgnoreCase(ucid)) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
                break;
            }
        }
    }

}
