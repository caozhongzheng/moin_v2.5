package com.moinapp.wuliao.modules.discovery.adapter;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keyboard.view.RoundAngleImageView;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.BaseImage;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.TagInfo;
import com.moinapp.wuliao.modules.discovery.ui.TopicDetailNewFragment;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.AudioPlayLayout;
import com.moinapp.wuliao.widget.AvatarView;
import com.moinapp.wuliao.widget.LikeLayout;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 话题详情: 列表适配器(3.2.7)
 * Created by guyunfei on 16/6/12.17:44.
 */
public class TopicDetailMultipleAdapter extends ListBaseAdapter<CosplayInfo> {

    private static final ILogger MyLog = LoggerFactory.getLogger(TopicDetailMultipleAdapter.class.getSimpleName());

    private static int IMAGE_MAX_NUM = 3;

    private int mPosition = 0;
    private boolean animShow = true;

    boolean scrolled = false;
    boolean rollback = false;

    private RoundAngleImageView[] images;

    private Activity mActivity;
    private Handler mHandler;

    public TopicDetailMultipleAdapter(Activity activity, Handler mHandler) {
        mActivity = activity;
        this.mHandler = mHandler;
    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (mPosition < position) {
            mPosition = position;
            animShow = true;
        } else {
            animShow = false;
        }
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.item_topic_detail, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        images = new RoundAngleImageView[3];
        images[0] = vh.image0;
        images[1] = vh.image1;
        images[2] = vh.image2;

        CosplayInfo cosplayInfo = mDatas.get(position);
        if (cosplayInfo != null) {
            if (cosplayInfo.getAuthor() != null) {

                if (cosplayInfo.getAuthor().getAvatar() != null) {
                    ImageLoaderUtils.displayHttpImage(cosplayInfo.getAuthor().getAvatar().getUri(),
                            vh.avatarView, null, animShow, null);
                }

                if (cosplayInfo.getAuthor().getUsername() != null) {
                    vh.userName.setText(cosplayInfo.getAuthor().getUsername());
                }
            }

            if (cosplayInfo.getCreatedAt() > 0) {
                vh.time.setText(StringUtil.humanDate(cosplayInfo.getCreatedAt()));
            }

            setContentView(cosplayInfo, vh);

            setClick(cosplayInfo, vh);

            // 回调滚动到下面完成
            if (mHandler != null && position == (getDataSize() > 4 ? 4 : getDataSize() - 1) && !scrolled) {
                mHandler.sendEmptyMessage(TopicDetailNewFragment.SCROLL_DOWN);
                scrolled = true;
            }

            // 回调触发滚回到第一条位置
            if (mHandler != null && position == 0 && !rollback) {
                int[] xy = new int[2];
                vh.mLikeView.getLocationOnScreen(xy);

                if (xy[0] != 0) {
                    mHandler.sendEmptyMessage(TopicDetailNewFragment.ROLLBACK);
                    rollback = true;
                }
            }
        }

        return convertView;
    }

    /**
     * 设置中心区域内容
     */
    private void setContentView(CosplayInfo cosplayInfo, ViewHolder vh) {
        String bbsName = null;
        if (cosplayInfo.getTags() != null) {
            for (TagInfo tag : cosplayInfo.getTags()) {
                if (tag == null || StringUtil.isNullOrEmpty(tag.getBbsName())) {
                    continue;
                }
                bbsName = tag.getBbsName();
            }
        }

        vh.category.setText(bbsName);
        if (!StringUtil.isNullOrEmpty(cosplayInfo.getContent())) {
            vh.content.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(bbsName)) {
                vh.category.setVisibility(View.GONE);
                vh.content.setText(cosplayInfo.getContent());
            } else {
                vh.category.setVisibility(View.VISIBLE);
                vh.content.setText("          " + cosplayInfo.getContent());
            }
        } else {
            vh.content.setVisibility(View.GONE);
            if (TextUtils.isEmpty(bbsName)) {
                vh.category.setVisibility(View.GONE);
            } else {
                vh.category.setVisibility(View.VISIBLE);
            }
        }

        vh.lookNum.setText(cosplayInfo.getReadNum() + "");
        vh.commentNum.setText(cosplayInfo.getCommentNum() + "");

        vh.mLikeView.setBackgroundImage(0);
        vh.mLikeView.setLikeImage(R.drawable.big_like_black);
        vh.mLikeView.setContent(cosplayInfo);

        // 类型,1 大咖秀图 2 图片帖 3 文字帖 4 音频帖
        int type = cosplayInfo.getType();
        switch (type) {
            case 1:
                vh.audio.setVisibility(View.GONE);
                vh.lyPic.setVisibility(View.VISIBLE);
                vh.imageNum.setVisibility(View.INVISIBLE);
                setCosplayImage(cosplayInfo, 1);
                break;
            case 2:
                vh.audio.setVisibility(View.GONE);
                vh.lyPic.setVisibility(View.VISIBLE);
                if (cosplayInfo.getPictureList() != null) {
                    if (cosplayInfo.getPictureList().size() > IMAGE_MAX_NUM) {
                        vh.imageNum.setVisibility(View.VISIBLE);
                        vh.imageNum.setText(cosplayInfo.getPictureList().size() + "");
                    } else {
                        vh.imageNum.setVisibility(View.INVISIBLE);
                    }
                    setPictureImage(cosplayInfo, cosplayInfo.getPictureList().size());
                }
                break;
            case 3:
                vh.audio.setVisibility(View.GONE);
                vh.lyPic.setVisibility(View.GONE);
                break;
            case 4:
                vh.audio.setVisibility(View.VISIBLE);
                vh.lyPic.setVisibility(View.GONE);
                if (cosplayInfo.getAudio() != null) {
                    vh.audio.setAudioInfo(cosplayInfo.getAudio().get(0));
                    //设置列表页面的音频不可播放
                    vh.audio.setEnablePlay(false);
                }
                break;
        }

    }

    private void setCosplayImage(CosplayInfo cosplayInfo, int imageMaxNum) {
        for (int i = 0; i < IMAGE_MAX_NUM; i++) {
            if (i < imageMaxNum) {
                images[i].setVisibility(View.VISIBLE);
                if (cosplayInfo.getPicture() != null) {
                    if (cosplayInfo.getPicture().getUri() != null) {
                        ImageLoaderUtils.displayHttpImage(false, cosplayInfo.getPicture().getUri(), images[i], null);
                    }
                }
            } else {
                images[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setPictureImage(CosplayInfo cosplayInfo, int imageMaxNum) {
        List<BaseImage> pictureList = cosplayInfo.getPictureList();
        for (int i = 0; i < IMAGE_MAX_NUM; i++) {
            if (i < imageMaxNum) {
                images[i].setVisibility(View.VISIBLE);
                if (pictureList != null) {
                    BaseImage baseImage = pictureList.get(i);
                    if (baseImage != null) {
                        if (baseImage.getUri() != null) {
                            ImageLoaderUtils.displayHttpImage(true, baseImage.getUri(), images[i], null);
                        }
                    }
                }
            } else {
                images[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setClick(CosplayInfo cosplayInfo, ViewHolder vh) {

        vh.userinfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(cosplayInfo.getAuthor().getUId())) {
                    // 登录用户点击自己头像
                    UIHelper.showMine(mActivity, 0);
                } else {
                    UIHelper.showUserCenter(mActivity, cosplayInfo.getAuthor().getUId());
                }
            }
        });

        vh.avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(cosplayInfo.getAuthor().getUId())) {
                    // 登录用户点击自己头像
                    UIHelper.showMine(mActivity, 0);
                } else {
                    UIHelper.showUserCenter(mActivity, cosplayInfo.getAuthor().getUId());
                }
            }
        });

        vh.contentLayout.setOnClickListener(onCosplayClick(cosplayInfo));

        vh.lookNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 跳转到浏览用户列表
                UIHelper.showUserList(mActivity, cosplayInfo.getUcid(), 2);
            }
        });

        vh.commentNum.setOnClickListener(onCosplayClick(cosplayInfo));
        vh.audio.setOnClickListener(onCosplayClick(cosplayInfo));
    }

    private View.OnClickListener onCosplayClick(CosplayInfo cosplayInfo) {
        if (cosplayInfo == null) return null;
        View.OnClickListener listener = v -> {
            if (cosplayInfo.getType() == 1) {
                // TODO 跳转到图片详情
                UIHelper.showDiscoveryCosplayDetail(mActivity, cosplayInfo, cosplayInfo.getUcid(), TimeUtils.getCurrentTimeInLong());
            } else if (cosplayInfo.getType() > 1 && cosplayInfo.getType() < 5) {
                // TODO 跳转到帖子详情
                UIHelper.showPostDetail(mActivity, cosplayInfo.getUcid(), TimeUtils.getCurrentTimeInLong());
            }
        };
        return listener;
    }

    static class ViewHolder {

        @InjectView(R.id.ly_userinfo)
        LinearLayout userinfoLayout;
        @InjectView(R.id.avatarView)
        AvatarView avatarView;
        @InjectView(R.id.userName)
        TextView userName;
        @InjectView(R.id.time)
        TextView time;

        @InjectView(R.id.ly_content)
        LinearLayout contentLayout;
        @InjectView(R.id.content)
        TextView content;
        @InjectView(R.id.audio)
        AudioPlayLayout audio;
        @InjectView(R.id.layout_image)
        RelativeLayout lyPic;
        @InjectView(R.id.image0)
        RoundAngleImageView image0;
        @InjectView(R.id.image1)
        RoundAngleImageView image1;
        @InjectView(R.id.image2)
        RoundAngleImageView image2;
        @InjectView(R.id.image_num)
        TextView imageNum;

        @InjectView(R.id.look_num)
        TextView lookNum;
        @InjectView(R.id.comment_num)
        TextView commentNum;
        @InjectView(R.id.like_layout)
        LikeLayout mLikeView;
        @InjectView(R.id.category)
        TextView category;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
