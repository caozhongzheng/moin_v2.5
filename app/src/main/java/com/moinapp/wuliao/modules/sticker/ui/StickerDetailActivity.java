package com.moinapp.wuliao.modules.sticker.ui;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.keyboard.view.RoundAngleImageView;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.sticker.StickerManager;
import com.moinapp.wuliao.modules.sticker.model.StickerInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerPackage;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.StickerUtils;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 贴纸包详情界面
 *
 * @author liujiancheng
 */
public class StickerDetailActivity extends BaseActivity {
    private static final ILogger MyLog = LoggerFactory.getLogger("StickerDetail");
    public static final String STICKER_ID = "sticker_id";
    public static final String STICKER_FROM_JOIN = "sticker_from_join";
    public static final String STICKER_FROM_MY = "sticker_from_my";
    protected static final String TAG = StickerDetailActivity.class.getSimpleName();
    private StickerPackage mStickerPackage;
    private String mStickId;

    @InjectView(R.id.title_layout)
    protected CommonTitleBar title;

    @InjectView(R.id.sticker_name)
    TextView name;

    @InjectView(R.id.sticker_desc)
    TextView desc;

    @InjectView(R.id.sticker_grid)
    GridView grid;

    @Override
    protected int getLayoutId() {
        return R.layout.sticker_package_detail;
    }

    @Override
    public void initView() {
        title.setLeftBtnOnclickListener(v -> {
            finish();
        });
    }

    @Override
    public void initData() {
        mStickId = getIntent().getStringExtra(STICKER_ID);
        if (TextUtils.isEmpty(mStickId)) {
            finish();
            return;
        }

        getStickerDetail();
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.stick_detail;
    }

    @Override
    public void onClick(View view) {}

    private void getStickerDetail() {
        getStickerDetailFromNet();
    }

    private void getStickerDetailFromNet() {
        MyLog.i("ljc: mFromMySticker == 0, 从服务器获取贴纸包详情");
        StickerManager.getInstance().getStickerDetail(mStickId, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                if (obj != null) {
                    mStickerPackage = (StickerPackage) obj;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateView(mStickerPackage);
                        }
                    });
//                    int count = StickerManager.getInstance().clearHasUpdateFlag(mStickerPackage);
                    // 3.2.6版本不需要显示贴纸包的更新标识了,所以不需刷新编辑区包的一级界面.
//                    if (count > 0) {
//                        MinePreference.getInstance().setNeedRefreshPhotoEdit(true);
//                    }
                }
            }

            @Override
            public void onErr(Object obj) {

            }

            @Override
            public void onNoNetwork() {

            }
        });
    }

    private void updateView(StickerPackage sticker) {
        name.setText(sticker.getName());
        desc.setText(sticker.getDesc());

        StickerDetialItemAdapter stickerItemAdapter = new StickerDetialItemAdapter(this, mStickerPackage);
        grid.setAdapter(stickerItemAdapter);
    }

    private class StickerDetialItemAdapter extends BaseAdapter {
        private List<StickerInfo> mDatas;

        private Activity mActivity;

        public StickerDetialItemAdapter(Activity activity, StickerPackage stickerPackage) {
            mActivity = activity;
            mDatas = stickerPackage.getStickers();
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
                convertView = View.inflate(mActivity, R.layout.item_sticker_mall_list, null);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            StickerInfo item = getItem(position);
            if (item != null) {
                if (item.getIcon() != null) {
                    ImageLoaderUtils.displayHttpImage(true, item.getIcon().getUri(), vh.sticker, ImageLoaderUtils.getImageLoaderOptionWithoutDefPic(), true, null);
                }

                boolean download = StickerUtils.isStickerDownload(item);
                if (download) {
                    vh.useNum.setVisibility(View.VISIBLE);
                    vh.useNum.setText("已使用 " + String.valueOf(item.getUseNum()));
                } else {
                    vh.useNum.setVisibility(View.GONE);
                    vh.download.setVisibility(View.VISIBLE);
                }

                vh.sticker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(UmengConstants.ITEM_ID, item.getStickerId() + "");
                        map.put(UmengConstants.FROM, "贴纸详情");
                        MobclickAgent.onEvent(mActivity, UmengConstants.STICKER_USE, map);
                        // 因为是从我的贴纸跳到详情的,所以不需要重新下载这个贴纸包了
                        StickerManager.getInstance().useSingleSticker(mActivity, item.getStickerId(), null);
                        mActivity.finish();
                    }
                });
            }
            return convertView;
        }
    }

    static class ViewHolder {

        @InjectView(R.id.iv_sticker_image)
        RoundAngleImageView sticker;

        @InjectView(R.id.tv_use_num)
        TextView useNum;

        @InjectView(R.id.iv_download)
        ImageView download;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.STICKER_DETAIL_ACTIVITY); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.STICKER_DETAIL_ACTIVITY); //
        MobclickAgent.onPause(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
