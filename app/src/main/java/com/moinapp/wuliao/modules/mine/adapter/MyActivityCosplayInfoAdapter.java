package com.moinapp.wuliao.modules.mine.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.Messages;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.mine.model.UserActivity;
import com.keyboard.view.RoundAngleImageView;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 我的动态界面,发表的图片适配器
 */
public class MyActivityCosplayInfoAdapter extends AUserActivityAdapter {
    private static final ILogger MyLog = LoggerFactory.getLogger(MyActivityCosplayInfoAdapter.class.getSimpleName());

    private Context context;
    final static int COLUMN = 3;
    private static int IMG_WIDTH = (int) ((TDevice.getScreenWidth() - TDevice.dpToPixel(16f)) / COLUMN);
    private int mPosition = 0;
    private boolean animShow = true;

    @Override
    public int getDataSize() {
        return (mDatas.size() + COLUMN - 1) / COLUMN;
    }

    @Override
    protected boolean needShowFootWhenEmpty() {
        return false;
    }

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
                    R.layout.list_cell_myuser_activity_grid, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        for (int i = 0; i < COLUMN; i++) {
            UserActivity userActivity = (position * COLUMN + i < mDatas.size()) ? mDatas.get(position * COLUMN + i) : null;
            setCosImage(userActivity, i, vh);
        }

        return convertView;
    }

    private void setCosImage(UserActivity userActivity, int i, ViewHolder vh) {
        RelativeLayout item = i == 0 ? vh.left_item : i == 1 ? vh.middle_item : vh.right_item;
        ImageView imageView = i == 0 ? vh.left_image : i == 1 ? vh.middle_image : vh.right_image;
        ImageView imageType = i == 0 ? vh.left_img_type : i == 1 ? vh.middle_img_type : vh.right_img_type;

        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = IMG_WIDTH;
        params.height = IMG_WIDTH;
        imageView.setLayoutParams(params);
        if (userActivity == null || userActivity.getPicture() == null || StringUtil.isNullOrEmpty(userActivity.getPicture())) {
            item.setVisibility(View.INVISIBLE);
        } else {
            item.setVisibility(View.VISIBLE);

            MyLog.i("显示第" + (i) + "个的图片:" + userActivity.getPicture());
            ImageLoaderUtils.displayHttpImage(userActivity.getPicture(), imageView, null, animShow, null);
            imageView.setOnClickListener(v -> {
                UIHelper.showDiscoveryCosplayDetail(context, null, userActivity.getResource(), TimeUtils.getCurrentTimeInLong());
            });

            switch (userActivity.getAction()) {
                case Messages.ACTION_LIKE_COSPLAY:
                    imageType.setImageResource(R.drawable.cosplay_type_like);
                    break;
                case Messages.ACTION_COMMENT_COSPLAY:
                case Messages.ACTION_AT_COSPLAY:
                case Messages.ACTION_COMMENT_LIKE_COSPLAY:
                case Messages.ACTION_REPLY_COSPLAY:
                case Messages.ACTION_REPLY_COMMENT_COSPLAY:
                    imageType.setImageResource(R.drawable.cosplay_type_comment);
                    break;
                case Messages.ACTION_SUBMIT_COSPLAY:
                case Messages.ACTION_FORWARD_COSPLAY:
                case Messages.ACTION_MODIFY_COSPLAY:
                    imageType.setImageResource(R.drawable.cosplay_type_pic);
                    break;

            }
        }
    }

    @Override
    public boolean isNeedLogin() {
        return true;
    }

    @Override
    public void setFinishText(int id) {
        super.setFinishText(R.string.no_more_data);
    }

    private int from;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    static class ViewHolder {

        @InjectView(R.id.left_item)
        RelativeLayout left_item;
        @InjectView(R.id.left_image)
        RoundAngleImageView left_image;
        @InjectView(R.id.left_img_type)
        ImageView left_img_type;

        @InjectView(R.id.middle_item)
        RelativeLayout middle_item;
        @InjectView(R.id.middle_image)
        RoundAngleImageView middle_image;
        @InjectView(R.id.middle_img_type)
        ImageView middle_img_type;

        @InjectView(R.id.right_item)
        RelativeLayout right_item;
        @InjectView(R.id.right_image)
        RoundAngleImageView right_image;
        @InjectView(R.id.right_img_type)
        ImageView right_img_type;


        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
