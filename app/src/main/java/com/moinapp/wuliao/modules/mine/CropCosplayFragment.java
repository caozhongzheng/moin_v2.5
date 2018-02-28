package com.moinapp.wuliao.modules.mine;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imagezoom.ImageViewTouch;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.SimpleBackActivity;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.IOUtil;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.ImageUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.UIHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 图片裁剪界面
 * 
 */
public class CropCosplayFragment extends BaseFragment {
    private static final ILogger MyLog = LoggerFactory.getLogger("ccf");

    @InjectView(R.id.title_layout)
    CommonTitleBar titleBar;
    @InjectView(R.id.crop_image)
    ImageViewTouch cropImage;

    private CosplayInfo mCosplayInfo;
    private String mUcid;
    private Bitmap oriBitmap;
    private int initWidth, initHeight;
    private View.OnClickListener goonListener;

    //剪切图片的变量
    private static final boolean IN_MEMORY_CROP = Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD_MR1;

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crop_cosplay, container,
                false);
        ButterKnife.inject(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        goonListener = v -> {
            ((SimpleBackActivity) getActivity()).showWaitDialog("图片处理中...");
            cropImage();
        };
        titleBar.setRightBtnOnclickListener(null);
        titleBar.setLeftBtnOnclickListener(v -> {
            getActivity().finish();
        });
        ViewGroup.LayoutParams params = cropImage.getLayoutParams();
        params.height = (int) TDevice.getScreenWidth();
        cropImage.setLayoutParams(params);
    }

    @Override
    public void initData() {
        Bundle args = getArguments();
        MyLog.i("args = " + args);
        if (args != null) {
            mCosplayInfo = (CosplayInfo) args.getSerializable(DiscoveryConstants.COSPLAY_INFO);
            mUcid = args.getString(DiscoveryConstants.UCID);
            MyLog.i("mCosplayInfo = " + mCosplayInfo);
            MyLog.i("mUcid = " + mUcid);
        }

        if(mCosplayInfo != null && mCosplayInfo.getPicture() != null
                && !StringUtil.isNullOrEmpty(mCosplayInfo.getPicture().getUri())) {
            ImageLoader.getInstance().displayImage(ImageLoaderUtils.defineUrlByImageSize(cropImage, mCosplayInfo.getPicture().getUri()),
                    new ImageViewAware(cropImage), null, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {
                            cropImage.setImageResource(R.drawable.pic_bg);
                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            MyLog.i("onSuccess = " + bitmap);
                            if (bitmap != null) {
                                oriBitmap = bitmap;

                                initWidth = oriBitmap.getWidth();
                                initHeight = oriBitmap.getHeight();

                                cropImage.setImageBitmap(oriBitmap, new Matrix(), 1, 10);

                                titleBar.setRightBtnOnclickListener(goonListener);
                            }
                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {

                        }
                    }, null);
        }
    }

    private void cropImage() {
        Bitmap croppedImage;
        if (IN_MEMORY_CROP) {
            croppedImage = inMemoryCrop(cropImage);
        } else {
            try {
                croppedImage = decodeRegionCrop(cropImage);
            } catch (IllegalArgumentException e) {
                croppedImage = inMemoryCrop(cropImage);
            }
        }
        saveImageToCache(croppedImage);
    }

    private void saveImageToCache(Bitmap croppedImage) {
        if (croppedImage != null) {
            try {
                String fileName = ImageUtils.saveToFile(BitmapUtil.getAvatarCropPath(), false, croppedImage);
                EventBus.getDefault().post(new CropCosplay(fileName));
            } catch (Exception e) {
                e.printStackTrace();
                AppContext.showToast("裁剪图片异常，请稍后重试");
            } finally {
                ((SimpleBackActivity) getActivity()).hideWaitDialog();
            }
        }
    }

    public static class CropCosplay {
        private String path;
        public CropCosplay(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    @TargetApi(10)
    private Bitmap decodeRegionCrop(ImageViewTouch cropImage) {
        int width = initWidth > initHeight ? initHeight : initWidth;
        int screenWidth = AppContext.getApp().getScreenWidth();
        float scale = cropImage.getScale() / getImageRadio();
        RectF rectf = cropImage.getBitmapRect();
        int left = -(int) (rectf.left * width / screenWidth / scale);
        int top = -(int) (rectf.top * width / screenWidth / scale);
        int right = left + (int) (width / scale);
        int bottom = top + (int) (width / scale);
        Rect rect = new Rect(left, top, right, bottom);
        InputStream is = null;
        System.gc();
        Bitmap croppedImage = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oriBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            is = new ByteArrayInputStream(baos.toByteArray());
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
            croppedImage = decoder.decodeRegion(rect, new BitmapFactory.Options());
        } catch (Throwable e) {

        } finally {
            IOUtil.closeStream(is);
        }
        return croppedImage;
    }

    private Bitmap inMemoryCrop(ImageViewTouch cropImage) {
        int width = initWidth > initHeight ? initHeight : initWidth;
        int screenWidth = AppContext.getApp().getScreenWidth();
        System.gc();
        Bitmap croppedImage = null;
        try {
            croppedImage = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565);

            Canvas canvas = new Canvas(croppedImage);
            float scale = cropImage.getScale();
            RectF srcRect = cropImage.getBitmapRect();
            Matrix matrix = new Matrix();

            matrix.postScale(scale / getImageRadio(), scale / getImageRadio());
            matrix.postTranslate(srcRect.left * width / screenWidth, srcRect.top * width
                    / screenWidth);
            //matrix.mapRect(srcRect);
            canvas.drawBitmap(oriBitmap, matrix, null);
        } catch (OutOfMemoryError e) {
            Log.e("OOM cropping image: " + e.getMessage(), e.toString());
            System.gc();
        }
        return croppedImage;
    }

    private float getImageRadio() {
        return Math.max((float) initWidth, (float) initHeight)
                / Math.min((float) initWidth, (float) initHeight);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
        case R.id.rl_notification_settings:
            UIHelper.showSettingNotification(getActivity());
            break;
        case R.id.rl_about:
            UIHelper.showAbout(getActivity());
            break;
        default:
            break;
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.CROP_COSPLAY_FRAGMENT); //统计页面，
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.CROP_COSPLAY_FRAGMENT);
    }
}
