package com.moinapp.wuliao.modules.sticker.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.sticker.StickerConstants;
import com.moinapp.wuliao.modules.sticker.StickerManager;
import com.moinapp.wuliao.modules.sticker.model.StickerInfo;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.UIHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 我的历史贴纸适配器
 *
 */
public class MyHistoryStickerAdapter extends ListBaseAdapter<StickerInfo> {
    private static final ILogger MyLog = LoggerFactory.getLogger(MyHistoryStickerAdapter.class.getSimpleName());

    final static int COLUMN = 4;
    private static int IMG_WIDTH = (int) ((TDevice.getScreenWidth() - TDevice.dpToPixel(24f)) / COLUMN);
    private Activity mActivity;
    private String mFrom;
    private boolean fromMyHistory;
    private int mPosition = 0;
    private boolean animShow = true;
    @Override
    public int getDataSize() {
        return (mDatas.size() + COLUMN - 1) / COLUMN;
    }

    public MyHistoryStickerAdapter(Activity activity, String from) {
        mActivity = activity;
        mFrom = from;
        fromMyHistory = mFrom.equals(activity.getResources().getString(R.string.my_history));
    }
//    @Override
//    protected boolean needShowFootWhenEmpty() {
//        return false;
//    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (mPosition < position) {
            mPosition = position;
            animShow = true;
        } else {
            animShow = false;
        }
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_history_sticker_grid, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        for (int i = 0; i < COLUMN; i++) {
            StickerInfo sticker = (position * COLUMN + i < mDatas.size()) ? mDatas.get(position * COLUMN + i) : null;
            setSticker(sticker, i, vh);
        }

        return convertView;
    }

    private void setSticker(StickerInfo stickerInfo, int i, ViewHolder vh) {
        RelativeLayout item = null;
        switch (i) {
            case 0:
                item = vh.item0;
                break;
            case 1:
                item = vh.item1;
                break;
            case 2:
                item = vh.item2;
                break;
            case 3:
                item = vh.item3;
                break;
        }
        if (item == null) return;
        if (stickerInfo == null || stickerInfo.getIcon() == null) {
            item.setVisibility(View.INVISIBLE);
            return;
        } else {
            item.setVisibility(View.VISIBLE);
        }

        ImageView imageView = (ImageView)item.findViewById(R.id.iv_sticker_image);
        if (imageView != null) {
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            params.width = IMG_WIDTH;
            params.height = IMG_WIDTH;
            imageView.setLayoutParams(params);

            MyLog.i(mFrom + ", 显示第" + (i) + "个的图片:" + stickerInfo.toString());
            ImageLoaderUtils.displayHttpImage(true, stickerInfo.getIcon().getUri(), imageView, null, animShow, null);
            imageView.setOnClickListener(v -> {
                if (!AppContext.getInstance().isLogin()){
                    UIHelper.showLoginActivity(mActivity, 0);
                    return;
                }
                //点击使用贴纸
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(UmengConstants.ITEM_ID, stickerInfo.getStickerId() + "");
                map.put(UmengConstants.FROM,  mFrom);
                MobclickAgent.onEvent(mActivity, UmengConstants.STICKER_USE, map);
                StickerManager.getInstance().useSingleSticker(mActivity, stickerInfo.getStickerId(), fromMyHistory ? null : StickerConstants.INTIME_STICKER_ID);
            });
        }

        TextView textView = (TextView)item.findViewById(R.id.tv_use_num);
        if (textView != null) {
            textView.setText("已使用  "+String.valueOf(stickerInfo.getUseNum()));
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
        @InjectView(R.id.history_item0)
        RelativeLayout item0;
        @InjectView(R.id.history_item1)
        RelativeLayout item1;
        @InjectView(R.id.history_item2)
        RelativeLayout item2;
        @InjectView(R.id.history_item3)
        RelativeLayout item3;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
