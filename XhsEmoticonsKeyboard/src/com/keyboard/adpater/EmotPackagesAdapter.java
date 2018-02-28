package com.keyboard.adpater;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keyboard.bean.EmoticonSetBean;
import com.keyboard.utils.imageloader.ImageBase;
import com.keyboard.utils.imageloader.ImageLoader;
import com.keyboard.view.I.IView;
import com.keyboard.view.R;

import java.io.IOException;
import java.util.List;

public class EmotPackagesAdapter extends BaseAdapter{

    static final String TAG = EmotPackagesAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private Context mContext;

    private List<EmoticonSetBean> data;
    private int mItemHeight = 0;
    private int mImgHeight = 0;

    public EmotPackagesAdapter(Context context, List<EmoticonSetBean> list) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.data = list;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_emotpackage, null);
            convertView.setLayoutParams(new AbsListView.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mItemHeight));
            viewHolder.iv_face = (ImageView) convertView.findViewById(R.id.ic_sticker_package_pic);
            viewHolder.iv_type = (ImageView) convertView.findViewById(R.id.iv_sticker_package_category);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_sticker_package_name);
            viewHolder.fl_content = (FrameLayout) convertView.findViewById(R.id.fl_sticker_package);
            viewHolder.rl_parent = (RelativeLayout) convertView.findViewById(R.id.rl_parent);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mImgHeight, mImgHeight);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            viewHolder.fl_content.setLayoutParams(params);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final EmoticonSetBean emoticonBean = data.get(position);
//        Log.i(TAG, "显示第"+(position+1)+"个:");
        if (emoticonBean != null) {
            Log.i(TAG, "1显示第" + (position + 1) + "个的表情：" + emoticonBean.toString());
//            viewHolder.rl_parent.setBackgroundResource(R.drawable.iv_face);
            viewHolder.iv_face.setPadding(0, 0, 0, 0);
            if(ImageBase.Scheme.ofUri(emoticonBean.getIconUri()) == ImageBase.Scheme.UNKNOWN){
                if (mOnItemListener != null) {
                    mOnItemListener.onItemDisplay(emoticonBean.getFirstEmoticon());
                }

                if (emoticonBean.getStickType() == 0) {
                    viewHolder.iv_face.setImageResource(R.drawable.plus_gray);
                    int paddingLeft = mContext.getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
                    int paddingTop = mContext.getResources().getDimensionPixelOffset(R.dimen.bar_tv_size);
                    viewHolder.iv_face.setPadding(paddingLeft, paddingTop, paddingLeft, 0);
                } else {

                    viewHolder.rl_parent.setVisibility(View.GONE);
                }
            }
            else{
                try {
//                        android.util.Log.i(TAG, "2显示第"+(position+1)+"个的表情uri："+emoticonBean.getIconUri());
                    ImageLoader.getInstance(mContext).displayImage(emoticonBean.getIconUri(), viewHolder.iv_face);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            viewHolder.tv_name.setText(emoticonBean.getName());
            /** 200表示话题贴纸包的类型*/
            if(emoticonBean.getStickType()==200) {
//                        Log.i(TAG, "3显示第"+(position+1)+"个的表情的名字："+emoticonBean.getContent());
                        viewHolder.iv_type.setVisibility(View.VISIBLE);
            } else {
//                        Log.i(TAG, "4 隐藏第"+(position+1)+"个的表情的名字：");
                        viewHolder.iv_type.setVisibility(View.GONE);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemListener != null) {
                        mOnItemListener.onItemClick(emoticonBean.getFirstEmoticon());
                    }
                }
            });

            final View finalConvertView = convertView;
            final int finalPosition = position;
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnItemListener != null) {
                        return mOnItemListener.onItemLongClick(finalPosition, finalConvertView, emoticonBean.getFirstEmoticon());
                    }
                    return false;
                }
            });
        } else {
//            Log.i(TAG, "9 隐藏第"+(position+1)+"个的表情：");
            viewHolder.rl_parent.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        public ImageView iv_face;
        public ImageView iv_type;
        public TextView tv_name;
        public FrameLayout fl_content;
        public RelativeLayout rl_parent;
    }

    public void setHeight(int height, int padding) {
        mItemHeight = height;
        mImgHeight = mItemHeight - padding;
        notifyDataSetChanged();
    }

    IView mOnItemListener;
    public void setOnItemListener(IView listener) {
        this.mOnItemListener = listener;
    }
}