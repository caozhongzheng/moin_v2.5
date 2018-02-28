package com.keyboard.utils.imageloader;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

public class ImageLoader implements ImageBase {

    protected final Context context;

    private volatile static ImageLoader instance;

    public static ImageLoader getInstance(Context context) {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ImageLoader(context);
                }
            }
        }
        return instance;
    }

    public ImageLoader(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     *
     * @param imageUri
     * @return
     */
    public Drawable getDrawable(String imageUri){
        switch (Scheme.ofUri(imageUri)) {
            case HTTP:
            case HTTPS:
                return null;
            case FILE:
                return null;
            case CONTENT:
                return null;
            case ASSETS:
                String filePath = Scheme.ASSETS.crop(imageUri);
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(context.getAssets().open(filePath));
                    return new BitmapDrawable(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            case DRAWABLE:
                String drawableIdString = Scheme.DRAWABLE.crop(imageUri);
                int resID = context.getResources().getIdentifier(drawableIdString, "drawable", context.getPackageName());
                return context.getResources().getDrawable((int) resID);
            case UNKNOWN:
            default:
                return null;
        }
    }

    /**
     *
     * @param uriStr
     * @param imageView
     * @throws IOException
     */
    @Override
    public void displayImage(String uriStr, ImageView imageView) throws IOException {
        switch (Scheme.ofUri(uriStr)) {
            case HTTP:
            case HTTPS:
                displayImageFromNetwork(uriStr, imageView);
                return ;
            case FILE:
                displayImageFromFile(uriStr, imageView);
                return ;
            case CONTENT:
                displayImageFromContent(uriStr, imageView);
                return ;
            case ASSETS:
                displayImageFromAssets(uriStr, imageView);
                return ;
            case DRAWABLE:
                displayImageFromDrawable(uriStr, imageView);
                return ;
            case UNKNOWN:
            default:
                displayImageFromOtherSource(uriStr, imageView);
                return ;
        }
    }

    /**
     * From Net
     * @param imageUri
     * @param extra
     * @throws IOException
     */
    protected void displayImageFromNetwork(String imageUri, Object extra) throws IOException {
        return ;
    }

    /**
     * From File
     * @param imageUri
     * @param imageView
     * @throws IOException
     */
    protected void displayImageFromFile(String imageUri, ImageView imageView) throws IOException {
        String filePath = Scheme.FILE.crop(imageUri);
        Bitmap bitmap = null;
        try {
//            bitmap = BitmapFactory.decodeFile(filePath);
            ImageSize imageSize = getImageViewWidth(imageView);

            int reqWidth = imageSize.width;
            int reqHeight = imageSize.height;

            bitmap = decodeSampledBitmapFromResource(filePath, reqWidth,
                    reqHeight);
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);
        return ;
    }

    /**
     * 计算inSampleSize，用于压缩图片
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight) {
        // 源图片的宽度
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;

        if (width > reqWidth && height > reqHeight) {
            // 计算出实际宽度和目标宽度的比率
            int widthRatio = Math.round((float) width / (float) reqWidth);
            int heightRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.max(widthRatio, heightRatio);
        }
        return inSampleSize;
    }

    /**
     * 根据计算的inSampleSize，得到压缩后图片
     *
     * @param pathName
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public Bitmap decodeSampledBitmapFromResource(String pathName,
                                                  int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);

        return bitmap;
    }

    /**
     * From Content
     * @param imageUri
     * @param imageView
     * @throws IOException
     */
    protected void displayImageFromContent(String imageUri, ImageView imageView) throws FileNotFoundException {
        ContentResolver res = context.getContentResolver();
        Uri uri = Uri.parse(imageUri);
        InputStream inputStream = res.openInputStream(uri);
        return ;
    }

    /**
     * From Assets
     * @param imageUri
     * @param imageView
     * @throws IOException
     */
    protected void displayImageFromAssets(String imageUri, ImageView imageView) throws IOException {
        String filePath = Scheme.ASSETS.crop(imageUri);
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getAssets().open(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);
        return ;
    }

    /**
     * From Drawable
     * @param imageUri
     * @param imageView
     * @throws IOException
     */
    protected void displayImageFromDrawable(String imageUri, ImageView imageView) {
        String drawableIdString = Scheme.DRAWABLE.crop(imageUri);
        int resID = context.getResources().getIdentifier(drawableIdString, "drawable", context.getPackageName());

        if (imageView != null) {
            imageView.setImageResource(resID);
        }
        return ;
    }

    /**
     * From OtherSource
     * @param imageUri
     * @param imageView
     * @throws IOException
     */
    protected void displayImageFromOtherSource(String imageUri, ImageView imageView) throws IOException {
        return ;
    }

    private class ImageSize {
        int width;
        int height;
    }

    /**
     * 根据ImageView获得适当的压缩的宽和高
     *
     * @param imageView
     * @return
     */
    private ImageSize getImageViewWidth(ImageView imageView) {
        ImageSize imageSize = new ImageSize();
        final DisplayMetrics displayMetrics = imageView.getContext()
                .getResources().getDisplayMetrics();
        final ViewGroup.LayoutParams params = imageView.getLayoutParams();

        int width = params.width == ViewGroup.LayoutParams.WRAP_CONTENT ? 0 : imageView
                .getWidth(); // Get actual image width
        if (width <= 0)
            width = params.width; // Get layout width parameter
        if (width <= 0)
            width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check
        // maxWidth
        // parameter
        if (width <= 0)
            width = displayMetrics.widthPixels;
        int height = params.height == ViewGroup.LayoutParams.WRAP_CONTENT ? 0 : imageView
                .getHeight(); // Get actual image height
        if (height <= 0)
            height = params.height; // Get layout height parameter
        if (height <= 0)
            height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check
        // maxHeight
        // parameter
        if (height <= 0)
            height = displayMetrics.heightPixels;
        imageSize.width = width;
        imageSize.height = height;
        return imageSize;

    }

    /**
     * 反射获得ImageView设置的最大宽度和高度
     *
     * @param object
     * @param fieldName
     * @return
     */
    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = (Integer) field.get(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
        }
        return value;
    }
}
