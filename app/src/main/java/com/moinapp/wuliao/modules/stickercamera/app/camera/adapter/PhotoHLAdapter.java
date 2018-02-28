package com.moinapp.wuliao.modules.stickercamera.app.camera.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.modules.stickercamera.app.model.PhotoItem;
import com.moinapp.wuliao.util.DistanceUtil;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtils;

import java.io.File;
import java.util.List;

/**
 * 大咖秀编辑第一步.图片选择水平HorizontalListView适配器
 */
public class PhotoHLAdapter extends BaseAdapter {

    List<PhotoItem> photoItems;
    Context            mContext;
    Activity mActivity;
    private int photoWidth = DistanceUtil.getCameraPhotoWidth();
    private int photoMargin = AppContext.getApp().dp2px(3);
    private int lastClickItem = -1;

    public PhotoHLAdapter(Context context, Activity activity, List<PhotoItem> effects) {
        photoItems = effects;
        mContext = context;
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return photoItems.size();
    }

    @Override
    public Object getItem(int position) {
        return photoItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PhotoHolder holder = null;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_photo_hl, null);
            holder = new PhotoHolder();
            holder.photo = (RoundedImageView) convertView.findViewById(R.id.photo_item);
            convertView.setTag(holder);
        } else {
            holder = (PhotoHolder) convertView.getTag();
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                photoWidth, photoWidth);
        params.leftMargin = photoMargin;
        params.rightMargin = photoMargin;
        params.topMargin = AppContext.getApp().dp2px(5);
//        params.bottomMargin = photoMargin;
        params.gravity = Gravity.CENTER;
        holder.photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.photo.setLayoutParams(params);

        final PhotoItem photoItem = (PhotoItem) getItem(position);

        if (StringUtils.isNotBlank(photoItem.getImageUri()) && (new File(photoItem.getImageUri()).exists())) {
            ImageLoaderUtils.displayLocalImage(photoItem.getImageUri(), holder.photo, null);
        } else {
            holder.photo.setImageResource(R.drawable.default_img);
        }

        if (lastClickItem != position) {
            holder.photo.setBorderWidth(0f);
        } else {
            holder.photo.setBorderWidth(3f);
            holder.photo.setBorderColor(mContext.getResources().getColor(R.color.moin));
        }
        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastClickItem = position;

                if(mOnPhotoItemSelectedListener != null) {
                    mOnPhotoItemSelectedListener.onPhotoItemSelected(mActivity,
                            new PhotoItem(photoItem.getImageUri(), System.currentTimeMillis()));
                }
                notifyDataSetChanged();
            }
        });


        return convertView;
    }

    public void setLastClickItem(int position) {
        lastClickItem = position;
    }

    class PhotoHolder {
        RoundedImageView photo;
    }


    public static interface OnPhotoItemSelectedListener
    {
        /**
         * 选择图片 回调函数
         * @param activity
         * @param photo
         */
        public abstract void onPhotoItemSelected(Activity activity, PhotoItem photo);
    }
    private OnPhotoItemSelectedListener mOnPhotoItemSelectedListener = null;
    /**
     * 为图片设置点击监听函数
     * @param onPhotoItemSelectedListener 参见 {@link OnPhotoItemSelectedListener}
     */
    public void setOnPhotoItemSelectedListener(OnPhotoItemSelectedListener onPhotoItemSelectedListener)
    {
        mOnPhotoItemSelectedListener = onPhotoItemSelectedListener;
    }
}
