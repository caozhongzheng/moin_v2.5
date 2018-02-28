package com.moinapp.wuliao.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.adapter.RecyclingPagerAdapter;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.emoji.DisplayRules;
import com.moinapp.wuliao.listener.Callback;
import com.moinapp.wuliao.ui.dialog.ImageMenuDialog;
import com.moinapp.wuliao.ui.dialog.ImageMenuDialog.OnMenuClickListener;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.ImageUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.widget.HackyViewPager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;

/**
 * 图片预览界面
 *
 * @author kymjs
 */
public class ImagePreviewActivity extends BaseActivity implements
        OnPageChangeListener {

    private HackyViewPager mViewPager;
    private SamplePagerAdapter mAdapter;
    private TextView mTvImgIndex;
    private ImageView mIvMore;
    private int mCurrentPostion = 0;
    private String[] mImageUrls;

    private boolean mIsFullCreen;

//    private KJBitmap kjb;

    public static void showImagePrivew(Context context, int index,
                                       String[] images) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);

        intent.putExtra(Constants.BUNDLE_KEY_IMAGES, images);
        intent.putExtra(Constants.BUNDLE_KEY_INDEX, index);
        context.startActivity(intent);
    }

    public static void showImagePrivew(Context context, int index,
                                       String[] images, boolean isFullScreen) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);

        intent.putExtra(Constants.BUNDLE_KEY_IMAGES, images);
        intent.putExtra(Constants.BUNDLE_KEY_INDEX, index);
        intent.putExtra(Constants.BUNDLE_KEY_FULLSCREEN, isFullScreen);
        context.startActivity(intent);
    }

    @Override
    protected boolean hasActionBar() {
        getSupportActionBar().hide();
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_image_preview;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
//        kjb = new KJBitmap();
        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);

        mImageUrls = getIntent().getStringArrayExtra(Constants.BUNDLE_KEY_IMAGES);
        int index = getIntent().getIntExtra(Constants.BUNDLE_KEY_INDEX, 0);
        mIsFullCreen = getIntent().getBooleanExtra(Constants.BUNDLE_KEY_FULLSCREEN, false);

        mAdapter = new SamplePagerAdapter(mImageUrls);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setCurrentItem(index);

        mTvImgIndex = (TextView) findViewById(R.id.tv_img_index);
        mIvMore = (ImageView) findViewById(R.id.iv_more);
        mIvMore.setOnClickListener(this);

        onPageSelected(index);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_more:
                showOptionMenu();
                break;
            default:
                break;
        }
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
    }

    private void showOptionMenu() {
        final ImageMenuDialog dialog = new ImageMenuDialog(this);
        dialog.show();
        dialog.setCancelable(true);
        dialog.setOnMenuClickListener(new OnMenuClickListener() {
            @Override
            public void onClick(TextView menuItem) {
                if (menuItem.getId() == R.id.menu1) {
                    saveImg();
                } else if (menuItem.getId() == R.id.menu2) {
                    //sendTweet();
                } else if (menuItem.getId() == R.id.menu3) {
                    copyUrl();
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * 复制链接
     */
    private void copyUrl() {
        String content = null;
        if (mAdapter != null && mAdapter.getCount() > 0) {
            content = mAdapter.getItem(mCurrentPostion);
            TDevice.copyTextToBoard(content);
            AppContext.showToastShort("已复制到剪贴板");
        }
    }

    /**
     * 保存图片
     */
    private void saveImg() {
        if (mAdapter != null && mAdapter.getCount() > 0) {
            final String imgUrl = mAdapter.getItem(mCurrentPostion);
            final String filePath =
                    BitmapUtil.BITMAP_DOWNLOAD
//                    AppConfig.DEFAULT_SAVE_IMAGE_PATH
                            + getFileName(imgUrl);
//            kjb.saveImage(this, imgUrl, filePath);
            ImageLoader.getInstance().loadImage(imgUrl, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    try {
                        ImageUtils.saveToFile(filePath, false, bitmap);
                        AppContext.showToastShort(getString(R.string.tip_save_image_suc,
                                filePath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });

        } else {
            AppContext.showToastShort(R.string.tip_save_image_faile);
        }
    }

    private String getFileName(String imgUrl) {
        int index = imgUrl.lastIndexOf('/') + 1;
        if (index == -1) {
            return System.currentTimeMillis() + ".jpeg";
        }
        return imgUrl.substring(index);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int idx) {
        mCurrentPostion = idx;
        if (mImageUrls != null && mImageUrls.length > 1) {
            if (mTvImgIndex != null) {
                mTvImgIndex.setText((mCurrentPostion + 1) + "/"
                        + mImageUrls.length);
            }
        }
    }

    class SamplePagerAdapter extends RecyclingPagerAdapter {

        private String[] images = new String[]{};

        SamplePagerAdapter(String[] images) {
            if (images != null) {
                this.images = images;
            }
        }

        public String getItem(int position) {
            if (images != null && images.length > 0) {
                return images[position];
            } else {
                return null;
            }
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        @SuppressLint("InflateParams")
        public View getView(int position, View convertView, ViewGroup container) {
            ViewHolder vh = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(container.getContext())
                        .inflate(R.layout.image_preview_item, null);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) vh.image.getLayoutParams();
            params.height = params.width = (int) TDevice.getScreenWidth();
            vh.image.enable();
            if (mIsFullCreen) {
                vh.image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            if (!StringUtil.isNullOrEmpty(images[position])) {
                final ProgressBar bar = vh.progress;
                int emjResID = 0;
                try {
                    Integer obj = DisplayRules.getMapAll().get(images[position]);
                    if (obj != null) {
                        emjResID = obj;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (emjResID != 0) {
                    //加载预置图片
                    vh.image.setImageResource(emjResID);
                } else if (images[position].startsWith("http:")) {
                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .showImageForEmptyUri(R.drawable.default_icon)
                            .cacheInMemory(true)// 是否緩存都內存中
                            .cacheOnDisk(true)// 是否緩存到sd卡上
                            .build();//设置图片Uri为空或是错误的时候显示的图片
                    ImageLoaderUtils.displayHttpImage(true, images[position], vh.image, options, true, new Callback() {
                        @Override
                        public void onStart() {
                            bar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onFinish(int result) {
                            bar.setVisibility(View.GONE);
                        }
                    });
                } else if (images[position].startsWith("file:") || images[position].startsWith("/storage/")
                        // 华为手机的/system/media/Pre-loaded/Pictures/Picture_12_Leopard.jpg 预览图问题
                        || images[position].startsWith("/system/")) {
                    ImageLoaderUtils.displayLocalImage(images[position], vh.image, null);
                    bar.setVisibility(View.GONE);
                }
            }
            vh.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImagePreviewActivity.this.finish();
                }
            });

//
//            final ProgressBar bar = vh.progress;
//            KJBitmap kjbitmap = new KJBitmap();
//            kjbitmap.displayWithDefWH(vh.image, images[position],
//                    new ColorDrawable(0x000000), new ColorDrawable(0x000000),
//                    new BitmapCallBack() {
//                        @Override
//                        public void onPreLoad() {
//                            super.onPreLoad();
//                            bar.setVisibility(View.VISIBLE);
//                        }
//
//                        @Override
//                        public void onFinish() {
//                            super.onFinish();
//                            bar.setVisibility(View.GONE);
//                        }
//
//                        @Override
//                        public void onFailure(Exception arg0) {
//                            AppContext.showToast(R.string.tip_load_image_faile);
//                        }
//                    });
//            vh.attacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
//                @Override
//                public void onPhotoTap(View view, float v, float v1) {
//                    ImagePreviewActivity.this.finish();
//                    overridePendingTransition(0, R.anim.zoomout);
//                }
//            });
            return convertView;
        }
    }

    static class ViewHolder {
        PhotoView image;
        ProgressBar progress;

        ViewHolder(View view) {
            image = (PhotoView) view.findViewById(R.id.photoview);
            progress = (ProgressBar) view.findViewById(R.id.progress);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.IMAGE_PREVIEW_ACTIVITY); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.IMAGE_PREVIEW_ACTIVITY); //
        MobclickAgent.onPause(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.zoomout);
    }
}
