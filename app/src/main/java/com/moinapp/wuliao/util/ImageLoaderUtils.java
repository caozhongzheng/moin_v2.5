package com.moinapp.wuliao.util;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.listener.Callback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;

/**
 * UIL 工具类
 * Created by sky on 15/7/26.
 */
public class ImageLoaderUtils {

    /**
     * display local image
     * @param uri
     * @param imageView
     * @param options
     */
    public static void displayLocalImage(String uri, ImageView imageView, DisplayImageOptions options) {
        ImageLoader.getInstance().displayImage("file://" + uri, new ImageViewAware(imageView), options, null, null);
    }

    /**
     * display Drawable image
     * @param uri
     * @param imageView
     * @param options
     */
    public static void displayDrawableImage(String uri, ImageView imageView, DisplayImageOptions options) {
        ImageLoader.getInstance().displayImage("drawable://" + uri, new ImageViewAware(imageView), options, null, null);
    }

    public static Bitmap getImageFromCache(String url) {
        if (TextUtils.isEmpty(url)) return null;
        return ImageLoader.getInstance().loadImageSync(url, getImageLoaderOption());
    }

    /**
     * display image
     * @param url
     * @param imageView
     * @param options
     */
    public static void displayHttpImage(String url, ImageView imageView, DisplayImageOptions options) {
        displayHttpImage(false, url, imageView, options);
    }

    /**
     * display image
     * @param origin 显示原尺寸
     * @param url
     * @param imageView
     * @param options
     */
    public static void displayHttpImage(boolean origin, String url, ImageView imageView, DisplayImageOptions options) {
        if (imageView == null) {
            return;
        }
        if (options == null) {
            options = getImageLoaderOptionWithDefaultIcon();
        }
        if (imageView.getTag() == null || !imageView.getTag().equals(url)) {
            ImageLoader.getInstance().displayImage(origin ? url : defineUrlByImageSize(imageView, url), new ImageViewAware(imageView), options, null, null);
            imageView.setTag(url);
        }
    }

    public static void displayHttpImage(String url, ImageView imageView, DisplayImageOptions options, boolean animShow, Callback callback) {
        displayHttpImage(false, url, imageView, options, animShow, callback);
    }

    public static void displayFixedHttpImage(String url, ImageView imageView, DisplayImageOptions options) {
        String newUrl = buildNewUrl(url, getFixedWidthImageSize());
        ImageLoader.getInstance().displayImage(newUrl, new ImageViewAware(imageView), options);
    }

    public static void displayScreenWidthImage(String url, ImageView imageView, DisplayImageOptions options, LoadImageCallback callback) {
        String newUrl = buildNewUrl(url, new ImageSize(AppContext.getInstance().getScreenWidth(), 0));
        Log.i("ljc","displayScreenWidthImage: new url = " + newUrl);
        if (options == null) {
            options = getImageLoaderOptionWithDefaultIcon();
        }
        ImageLoader.getInstance().displayImage(newUrl, new ImageViewAware(imageView), options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (callback != null) {
                    callback.onLoadCompleted(loadedImage);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
            }
        }, null);
    }

    /**
     * display image
     * @param origin 显示原尺寸
     * @param url
     * @param imageView
     * @param options
     * @param animShow 显示渐入动画
     * @param callback 显示完成的回调
     */
    public static void displayHttpImage(boolean origin, String url, ImageView imageView, DisplayImageOptions options, boolean animShow, Callback callback) {
        if (imageView == null) {
            return;
        }
        if (options == null) {
            options = getImageLoaderOptionWithDefaultIcon();
        }
        if (imageView.getTag() == null || !imageView.getTag().equals(url)) {
            ImageLoader.getInstance().displayImage(origin ? url : defineUrlByImageSize(imageView, url), new ImageViewAware(imageView), options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    if (callback != null) {
                        callback.onStart();
                    }
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    if (callback != null) {
                        callback.onFinish(0);
                    }
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (callback != null) {
                        callback.onFinish(1);
                    }
                    if (animShow) {
                        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
                        alphaAnimation.setDuration(1000);
                        alphaAnimation.setFillAfter(true);
                        imageView.startAnimation(alphaAnimation);
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    if (callback != null) {
                        callback.onFinish(-1);
                    }
                }
            }, null);
            imageView.setTag(url);
        }
    }

    /**
     * 带回调的display image
     */
    public static void displayHttpImage(String url, DisplayImageOptions options, ImageLoadingListener listener) {
        ImageLoader.getInstance().loadImage(url, options, listener);
    }

    public static DisplayImageOptions getImageLoaderOptionWithDefaultIcon() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_img) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.default_icon)
                .cacheInMemory(false)// 是否緩存都內存中
                .cacheOnDisk(true)// 是否緩存到sd卡上
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT) //设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565) //设置图片的解码类型//
                .resetViewBeforeLoading(false)
                .build();//设置图片Uri为空或是错误的时候显示的图片
        return options;
    }

    public static DisplayImageOptions getImageLoaderOption() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(false)// 是否緩存都內存中
                .cacheOnDisk(true)// 是否緩存到sd卡上
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT) //设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565) //设置图片的解码类型//
                .resetViewBeforeLoading(false)
                .build();//设置图片Uri为空或是错误的时候显示的图片
        return getImageLoaderOptionWithDefaultIcon();
    }

    public static DisplayImageOptions getImageLoaderOptionWithoutDefPic() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(false)// 是否緩存都內存中
                .cacheOnDisk(true)// 是否緩存到sd卡上
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT) //设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565) //设置图片的解码类型//
                .resetViewBeforeLoading(false)
                .build();//设置图片Uri为空或是错误的时候显示的图片
        return options;
    }

    public static String defineUrlByImageSize(ImageView imageView, String url) {
        if(StringUtil.isNullOrEmpty(url)) {
            return "";
        }
        android.util.Log.i("du", "url=" + url);
        ImageViewAware imageAware = new ImageViewAware(imageView);
        ImageSize targetSize = ImageSizeUtils.defineTargetSizeForView(imageAware, getMaxImageSize());
        String newUrl = buildNewUrl(url, targetSize);

        android.util.Log.i("du","newUrl="+newUrl);
        return newUrl;
    }
    public static ImageSize getMaxImageSize() {
        return new ImageSize(AppContext.getInstance().getScreenWidth(), AppContext.getInstance().getScreenHeight());
    }

    public static ImageSize getFixedWidthImageSize() {
        return new ImageSize(AppContext.getInstance().getScreenWidth(), AppContext.getInstance().getScreenWidth());
    }

    public static String buildNewUrl(String imageUri, ImageSize targetSize) {
        if(StringUtil.isNullOrEmpty(imageUri)) {
            return "";
        }
        String header = imageUri;
        String tailer = "";
        int lastIndex = imageUri.lastIndexOf(".");
        if(lastIndex != -1) {
            header = imageUri.substring(0, lastIndex);
            tailer = imageUri.substring(lastIndex);
        }
        return header + "_" + targetSize.getWidth() + "_" + targetSize.getHeight() + tailer;
    }

    public interface LoadImageCallback {
        public void onLoadCompleted(Bitmap bitmap);
    }
}
