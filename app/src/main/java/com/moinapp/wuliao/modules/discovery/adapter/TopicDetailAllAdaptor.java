package com.moinapp.wuliao.modules.discovery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.AvatarView;
import com.moinapp.wuliao.widget.LikeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

/** 话题详情全部列表适配器[3.2.7版本以前]
 * Created by liujiancheng on 15/9/22.
 */
public class TopicDetailAllAdaptor extends ATopicDetailAdapter {
    private static final ILogger MyLog = LoggerFactory.getLogger(TopicDetailAllAdaptor.class.getSimpleName());
    private Context context;
    private int mPosition = 0;
    private boolean animShow = true;
    private static int IMG_WIDTH = (int) (TDevice.getScreenWidth() / 2 - TDevice.dpToPixel(8f));

    public TopicDetailAllAdaptor() {

    }

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
                    R.layout.list_cell_topic_detail_all, null);

            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final CosplayInfo item0 = mDatas.get(position * 2);
        final CosplayInfo item1 = (position * 2 + 1 < mDatas.size()) ? mDatas.get(position * 2 + 1) : null;

        if (item0 != null) {
            updateCosplayInfo(vh.item0, item0, context);
        }

        if (item1 != null) {
            updateCosplayInfo(vh.item1, item1, context);
            vh.item1.setVisibility(View.VISIBLE);
        } else {
            vh.item1.setVisibility(View.INVISIBLE);
        }

        return convertView;
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


        // 点赞
        LikeLayout like_view = (LikeLayout) view.findViewById(R.id.like_layout);
        like_view.setContent(cosplay);
    }

    static class ViewHolder {
        @InjectView(R.id.topic_detail_all_list_item0)
        View item0;

        @InjectView(R.id.topic_detail_all_list_item1)
        View item1;


        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}

