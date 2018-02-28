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

/** 搜索结果集列表适配器
 * Created by liujiancheng on 15/9/22.
 */
public class CosplaySearchResultAdaptor extends ATopicDetailAdapter {
    private static final ILogger MyLog = LoggerFactory.getLogger(CosplaySearchResultAdaptor.class.getSimpleName());
    private Context context;
    private int mPosition = 0;
    private boolean animShow = true;
    private static int IMG_WIDTH = (int) (TDevice.getScreenWidth() / 2 - TDevice.dpToPixel(8f));

    public CosplaySearchResultAdaptor() {}

    @Override
    protected boolean hasFooterView() {
        return false;
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
                    R.layout.list_cell_cosplay_search_result_item, null);

            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final CosplayInfo cosplayInfo = mDatas.get(position);

        if (cosplayInfo != null) {
            updateCosplayInfo(vh, cosplayInfo, context);
        }

        return convertView;
    }

    private void updateCosplayInfo(ViewHolder vh, CosplayInfo cosplay, Context context) {
        MyLog.i("CosplayInfo=" + cosplay.toString());
        // 头像和作者名
        try {
            if (cosplay.getAuthor() != null) {
                UserInfo author = cosplay.getAuthor();
                vh.face.setUserInfo(author.getUId(), author.getUsername());
                if (author.getAvatar() != null) {
                    vh.face.setAvatarUrl(author.getAvatar().getUri());
                } else {
                    vh.face.setAvatarUrl(null);
                }

                vh.author_name.setText(author.getUsername());

                View.OnClickListener userCenterListener = v -> {
                    if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(cosplay.getAuthor().getUId())) {
                        // 登录用户点击自己头像
                        UIHelper.showMine(context, 0);
                    } else {
                        UIHelper.showUserCenter(context, cosplay.getAuthor().getUId());
                    }
                };
                vh.face.setOnClickListener(userCenterListener);
                vh.author_name.setOnClickListener(userCenterListener);
            }
        } catch (Exception e) {
            MyLog.e(e);
        }


        // 发布说明
        if (!StringUtil.isNullOrEmpty(cosplay.getContent())) {
            vh.content.setText(cosplay.getContent());
            vh.contentLy.setVisibility(View.VISIBLE);
        } else {
            vh.contentLy.setVisibility(View.GONE);
        }

        // 发布时间
        vh.time.setText(StringUtil.humanDate(cosplay.getCreatedAt(), StringUtil.TIME_PATTERN));

        // 发布的图,大咖秀
        try {
            ViewGroup.LayoutParams params = vh.cosplay_image.getLayoutParams();
            params.width = IMG_WIDTH;
            params.height = IMG_WIDTH;
            vh.cosplay_image.setLayoutParams(params);

            if (cosplay.getPicture() != null) {
                ImageLoaderUtils.displayHttpImage(cosplay.getPicture().getUri(), vh.cosplay_image, null, animShow, null);
            }
            vh.cosplay_image.setOnClickListener(v -> {
                MyLog.i("进入图片详情:" + cosplay.toString());
                UIHelper.showDiscoveryCosplayDetail(context, cosplay, cosplay.getUcid(), TimeUtils.getCurrentTimeInLong());
            });
        } catch (Exception e) {
            MyLog.e(e);
        }

        // 点赞
        vh.like_view.setContent(cosplay);
    }

    static class ViewHolder {
        @InjectView(R.id.iv_cosplay_image)
        ImageView cosplay_image;
        @InjectView(R.id.like_layout)
        LikeLayout like_view;
        @InjectView(R.id.ly_cosplay_content)
        LinearLayout contentLy;
        @InjectView(R.id.tv_cosplay_content)
        TextView content;
        @InjectView(R.id.iv_cosplay_face)
        AvatarView face;
        @InjectView(R.id.tv_author_name)
        TextView author_name;
        @InjectView(R.id.tv_discovery_time)
        TextView time;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}

