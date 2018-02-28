package com.moinapp.wuliao.modules.post;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.ui.imageselect.CommonAdapter;
import com.moinapp.wuliao.ui.imageselect.ViewHolder;
import com.moinapp.wuliao.util.TDevice;

import java.util.List;

/**
 * 多个图片适配器(含加号)
 * Created by moying on 15/6/14.
 */
public class MyPicAdapter extends CommonAdapter<String> {

    public MyPicAdapter(Context context, List<String> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(final ViewHolder helper, final int position) {

        final LinearLayout mImageContainer = helper.getView(R.id.iv_container);
        final ImageView mImageView = helper.getView(R.id.iv_photo);
        final ImageView mDelete = helper.getView(R.id.iv_del);

        String item = getItem(position);
        //设置图片
        if (!PostConstants.ADD_PLUS.equals(item)) {
            helper.setImageByUrl(R.id.iv_photo, item);
            mDelete.setVisibility(View.VISIBLE);

            mImageView.setOnClickListener(view -> {
                if (piccallback != null) {
                    piccallback.onClick(position);
                }
            });
            mDelete.setOnClickListener(view -> {
                if (piccallback != null) {
                    piccallback.onDelete(position);
                }
            });
        } else {
            mImageView.setTag(PostConstants.ADD_PLUS);
            helper.setImageResource(R.id.iv_photo, R.drawable.add_picture);
            mDelete.setVisibility(View.GONE);
            mImageView.setOnClickListener(view -> {
                if (piccallback != null) {
                    piccallback.onAdd();
                }
            });
        }

        ViewGroup.LayoutParams params = mImageContainer.getLayoutParams();
        params.width = params.height = (int) ((int) (TDevice.getScreenWidth() / 4) - TDevice.dpToPixel(11f));
    }

    public interface PicCallback {
        void onAdd();//点击添加按钮

        void onClick(int position);//点击图片

        void onDelete(int position);//点击删除按钮
    }

    private PicCallback piccallback = null;

    public void setPicCallback(PicCallback listener) {
        piccallback = listener;
    }

}
