package com.moinapp.wuliao.modules.stickercamera.app.camera.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.keyboard.view.RoundAngleImageView;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.sticker.model.StickerInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerPackage;
import com.moinapp.wuliao.modules.stickercamera.base.util.DialogHelper;
import com.moinapp.wuliao.util.ImageLoaderUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/** 贴纸商城每个贴纸包的grid adapter[包括推荐页的12张贴纸]
 * Created by guyunfei on 16/5/25.11:35.
 */
public class StickerItemAdapter extends BaseAdapter {
    private static final ILogger MyLog = LoggerFactory.getLogger(StickerItemAdapter.class.getSimpleName());

    private boolean mAnimShow = true;
    private boolean mIsUseNumVis = true;

    private List<StickerInfo> mDatas;
    private StickerPackage mStickerPackage;

    private Activity mActivity;

    public StickerItemAdapter(Activity activity, StickerPackage stickerPackage, boolean isUseNumVis, boolean animShow) {
        mActivity = activity;
        mStickerPackage = stickerPackage;
        mDatas = stickerPackage.getStickers();
        mIsUseNumVis = isUseNumVis;
        mAnimShow = animShow;
    }


    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public StickerInfo getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = View.inflate(mActivity,R.layout.item_sticker_mall_list,null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        StickerInfo item = getItem(position);

        ImageLoaderUtils.displayHttpImage(true, item.getIcon().getUri(), vh.sticker, ImageLoaderUtils.getImageLoaderOptionWithoutDefPic(), mAnimShow, null);

        if (mIsUseNumVis) {
            vh.useNum.setVisibility(View.VISIBLE);
            vh.useNum.setText("已使用 " + String.valueOf(item.getUseNum()));
        }else {
            vh.useNum.setVisibility(View.GONE);
        }
        vh.sticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper dialogHelper = new DialogHelper(mActivity);
                dialogHelper.showStickerDetail(item, mStickerPackage);
            }
        });

        return convertView;
    }

    static class ViewHolder {

        @InjectView(R.id.iv_sticker_image)
        RoundAngleImageView sticker;

        @InjectView(R.id.tv_use_num)
        TextView useNum;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
