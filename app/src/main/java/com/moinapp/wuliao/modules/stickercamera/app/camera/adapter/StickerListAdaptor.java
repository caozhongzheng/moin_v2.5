package com.moinapp.wuliao.modules.stickercamera.app.camera.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.Callback;
import com.moinapp.wuliao.modules.sticker.StickerManager;
import com.moinapp.wuliao.modules.sticker.model.StickerPackage;
import com.moinapp.wuliao.modules.stickercamera.app.camera.event.AddMySticker;
import com.moinapp.wuliao.modules.stickercamera.app.camera.event.DeleteMySticker;
import com.moinapp.wuliao.ui.NoScrollGridView;
import com.moinapp.wuliao.util.StringUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 贴纸商城贴纸包列表适配器
 * Created by liujiancheng on 15/9/22.
 */
public class StickerListAdaptor extends ListBaseAdapter<StickerPackage> {
    private static final ILogger MyLog = LoggerFactory.getLogger(StickerListAdaptor.class.getSimpleName());
    private int mPosition = 0;
    private boolean animShow = true;
    private Activity mActivity;

    public StickerListAdaptor(FragmentActivity activity) {
        mActivity = activity;
        EventBus.getDefault().register(this);
    }

    /**
     *  注意使用此adaptor的ui需要显式调用这个方法去反注册event bus
     */
    public void unregisterEventBus() {
        EventBus.getDefault().unregist(this);
    }

    @SuppressLint("InflateParams")
    @Override
    protected View getRealView(int position, View convertView,
                               final ViewGroup parent) {
        if (mPosition < position) {
            mPosition = position;
            animShow = true;
        } else {
            animShow = false;
        }
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(R.layout.list_cell_sticker, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final StickerPackage item = mDatas.get(position);

        if (item != null) {
            int flag = StickerManager.getInstance().getLocalFlag(item);

            vh.name.setText(item.getName());
            if (!StringUtil.isNullOrEmpty(item.getDesc())) {
                vh.desc.setVisibility(View.VISIBLE);
                vh.desc.setText(item.getDesc());
            } else {
                vh.desc.setVisibility(View.GONE);
            }

            StickerItemAdapter stickerItemAdapter = new StickerItemAdapter(mActivity, item, true, animShow);
            if (vh.stickers.getTag() == null || !vh.stickers.getTag().equals(item.getName())) {
                vh.stickers.setAdapter(stickerItemAdapter);
                vh.stickers.setTag(item.getName());
            }

            boolean newest = StickerManager.getInstance().isNewest(item);
            vh.newset.setVisibility(newest ? View.VISIBLE : View.INVISIBLE);
        }

        return convertView;
    }

    public void onEvent(DeleteMySticker sticker) {
        Log.i("ljc", "received DeleteMySticker message: id=" + sticker.getmId());
        for (StickerPackage stickerPackage : mDatas) {
            if (stickerPackage.getStickerPackageId().equalsIgnoreCase(sticker.getmId())) {
                //注意要在ui线程去通知更新ui
                new Handler(BaseApplication.context().getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("ljc", "find matched item: id=" + stickerPackage.getStickerPackageId());
                        stickerPackage.setIsDownload(0);
                        StickerListAdaptor.this.notifyDataSetChanged();
                    }
                });
                break;
            }
        }
    }

    public void onEvent(AddMySticker sticker) {
        for (StickerPackage stickerPackage : mDatas) {
            if (stickerPackage.getStickerPackageId().equalsIgnoreCase(sticker.getSticker().getStickerPackageId())) {
                //注意要在ui线程去通知更新ui
                new Handler(BaseApplication.context().getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("ljc", "find matched item: id=" + stickerPackage.getStickerPackageId());
                        stickerPackage.setIsDownload(1);
                        StickerListAdaptor.this.notifyDataSetChanged();
                    }
                });
                break;
            }
        }
    }

    public Callback getDownloadCallback() {
        return downloadCallback;
    }

    public void setDownloadCallback(Callback downloadCallback) {
        this.downloadCallback = downloadCallback;
    }

    private Callback downloadCallback = null;

    static class ViewHolder {
        @InjectView(R.id.sticker_newest)
        ImageView newset;

        @InjectView(R.id.sticker_name)
        TextView name;

        @InjectView(R.id.sticker_desc)
        TextView desc;

        @InjectView(R.id.stickers)
        NoScrollGridView stickers;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}

