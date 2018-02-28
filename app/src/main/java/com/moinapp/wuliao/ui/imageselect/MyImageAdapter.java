package com.moinapp.wuliao.ui.imageselect;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.util.TDevice;

import java.util.List;

/** 选择图片适配器
 * Created by moying on 15/6/11.
 */
public class MyImageAdapter extends CommonAdapter<String> {
    /**
     * 文件夹路径
     */
    private String mDirPath;
    private Handler mHandler;
    private int lastClickItem = -1;
    private Context mContext;

    public MyImageAdapter(Context context, List<String> mDatas, int itemLayoutId,
                     String dirPath, Handler handler) {
        super(context, mDatas, itemLayoutId);
        this.mDirPath = dirPath;
        this.mHandler = handler;
        this.mContext = context;
    }

    private int clickTemp = -1;
    //标识选择的Item
    public void setSeclection(int position) {
        clickTemp = position;
    }

    @Override
    public void convert(final ViewHolder helper, final int position) {
        final String item = getItem(position);
        //设置图片
        helper.setImageByUrl(R.id.id_item_image, item);
        final ImageView mImageView = helper.getView(R.id.id_item_image);
        final FrameLayout frame = helper.getView(R.id.image_frame);
        RelativeLayout rootView = helper.getView(R.id.rl_rootview);
//        ViewGroup.LayoutParams param1s = mImageView.getLayoutParams();
//        para1ms.height = (int) (TDevice.getScreenWidth() / 4);

        mImageView.setColorFilter(null);

        if (lastClickItem == -1 && position == 0) {
            frame.setBackgroundColor(mContext.getResources().getColor(R.color.moin));
        } else if (lastClickItem == position) {
            frame.setBackgroundColor(mContext.getResources().getColor(R.color.moin));
        } else {
            frame.setBackgroundColor(Color.TRANSPARENT);
        }

        // 点击改变选中listItem的背景色
        if (clickTemp == position) {
            rootView.setBackgroundColor(mContext.getResources().getColor(R.color.moin));
        } else {
            rootView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }

        mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lastClickItem = position;
                notifyDataSetChanged();
                Message message = Message.obtain(mHandler, 0x119);
                Bundle b = new Bundle();
                b.putString("uri",item);
                message.setData(b);
                message.sendToTarget();

                setSeclection(position);
                notifyDataSetChanged();
            }
        });

    }
}
