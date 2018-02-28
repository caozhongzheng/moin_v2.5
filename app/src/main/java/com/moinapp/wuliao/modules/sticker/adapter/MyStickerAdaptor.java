package com.moinapp.wuliao.modules.sticker.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keyboard.view.RoundAngleImageView;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.mine.chat.ChatLayoutManager;
import com.moinapp.wuliao.modules.mine.chat.ChatListItemLayout;
import com.moinapp.wuliao.modules.sticker.model.StickerInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerPackage;
import com.moinapp.wuliao.modules.sticker.ui.MyStickerFragment;
import com.moinapp.wuliao.modules.sticker.ui.StickerDetailActivity;
import com.moinapp.wuliao.modules.stickercamera.app.camera.event.AddMySticker;
import com.moinapp.wuliao.util.ImageLoaderUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 我的贴纸适配器
 * Created by liujiancheng on 16/2/2
 */
public class MyStickerAdaptor extends ListBaseAdapter<StickerPackage> {
    private static final ILogger MyLog = LoggerFactory.getLogger("msa");
    protected ImageView[] stickerList = new ImageView[5];
    private RemoveCallback mRemoveCallback;

    public MyStickerAdaptor(Activity activity) {
        EventBus.getDefault().register(this);
    }

    /**
     *  注意使用此adaptor的ui需要显式调用这个方法去反注册event bus
     */
    public void unregisterEventBus() {
        EventBus.getDefault().unregist(this);
    }

    //移除指定位置的item
    public void remove(int position) {
        mDatas.remove(position);
        this.notifyDataSetChanged();
    }

    //在指定位置插入item
    public void insert(StickerPackage item, int position) {
        mDatas.add(position, item);
        this.notifyDataSetChanged();
    }

    @SuppressLint("InflateParams")
    @Override
    protected View getRealView(int position, View convertView,
                               final ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.item_my_sticker_list, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        stickerList[0] = vh.stickerOne;
        stickerList[1] = vh.stickerTwo;
        stickerList[2] = vh.stickerThree;
        stickerList[3] = vh.stickerFour;

        final StickerPackage item = mDatas.get(position);

        if (item != null) {
            vh.name.setText(item.getName());

            List<StickerInfo> stickers = item.getStickers();
            if (stickers != null)
            for (int i = 0; i < 4; i++) {
                if (i < stickers.size()) {
                    StickerInfo stickerInfo = stickers.get(i);
                    if (stickerInfo != null) {
                        stickerList[i].setVisibility(View.VISIBLE);
                        ImageLoaderUtils.displayHttpImage(true, stickerInfo.getIcon().getUri(), stickerList[i], null);
                    } else {
                        stickerList[i].setVisibility(View.GONE);
                    }
                } else {
                    stickerList[i].setVisibility(View.GONE);
                }
            }

            if (mMode == MyStickerFragment.MODE_NORMAL) {
                vh.layout_my_sticker.setStickerSort(false);
                vh.sort.setVisibility(View.GONE);
            } else {
                vh.layout_my_sticker.setStickerSort(true);
                ChatLayoutManager.getInstance().clearCurrentLayout();
                vh.sort.setVisibility(View.VISIBLE);
                //因为第四个贴纸会占住排序手柄的位置,所以排序状态时隐藏它
                vh.stickerFour.setVisibility(View.GONE);
            }

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BaseApplication.context(), StickerDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(StickerDetailActivity.STICKER_ID, item.getStickerPackageId());
                    intent.putExtra(StickerDetailActivity.STICKER_FROM_MY, 1);
                    BaseApplication.context().startActivity(intent);
                }
            };
            vh.content.setOnClickListener(listener);
        }

        vh.delete.setOnClickListener(v -> {
            MyLog.i("vh.delete.setOnClickListener .....");
            if (mRemoveCallback != null) {
                MyLog.i("mRemoveCallback != null.....");
                mRemoveCallback.onRemove(position);
            }
        });
        return convertView;
    }

    private int mMode;
    public void setMode(int mode) {
        this.mMode = mode;
    }

    static class ViewHolder {
        @InjectView(R.id.layout_my_sticker)
        ChatListItemLayout layout_my_sticker;
        @InjectView(R.id.tv_delete)
        TextView delete;

        @InjectView(R.id.sticker_name)
        TextView name;

        @InjectView(R.id.drag_handle)
        ImageView sort;

        @InjectView(R.id.iv_sticker_1)
        RoundAngleImageView stickerOne;
        @InjectView(R.id.iv_sticker_2)
        RoundAngleImageView stickerTwo;
        @InjectView(R.id.iv_sticker_3)
        RoundAngleImageView stickerThree;
        @InjectView(R.id.iv_sticker_4)
        RoundAngleImageView stickerFour;

        @InjectView(R.id.ly_content)
        LinearLayout content;
        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public void onEvent(AddMySticker sticker) {
        new Handler(BaseApplication.context().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (StickerPackage stickerPackage:mDatas) {
                    if (stickerPackage.getStickerPackageId().equalsIgnoreCase(sticker.getSticker().getStickerPackageId())) {
                        return;
                    }
                }
                mDatas.add(0, sticker.getSticker());
                notifyDataSetInvalidated();
            }
        });
    }

    public void setRemoveCallback(RemoveCallback callback) {
        mRemoveCallback = callback;
    }

    public interface RemoveCallback {
        public void onRemove(int position);
    }
}

