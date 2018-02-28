package com.moinapp.wuliao.ui.imageselect;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.imagezoom.ImageViewTouch;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.sticker.StickPreference;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraBaseActivity;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.modules.stickercamera.app.camera.ui.PhotoGlorifyActivity;
import com.moinapp.wuliao.util.FileUtil;
import com.moinapp.wuliao.util.IOUtil;
import com.moinapp.wuliao.util.ImageUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/** 发布大咖秀时用选择图片
 * Created by moying on 15/6/11.
 */
public class SelectPhotoActivity extends CameraBaseActivity implements ListImageDirPopupWindow.OnImageDirSelected {

    private ILogger MyLog = LoggerFactory.getLogger(SelectPhotoActivity.class.getSimpleName());

    private ProgressDialog mProgressDialog;

    /**
     * 存储文件夹中的图片数量
     */
    private int mPicsSize;

    /**
     * 图片数量最多的文件夹
     */
    private File mImgDir;
    /**
     * 所有的图片
     */
    private List<String> mImgs;
    private List<File> mImgFiles;//排序用

    /**
     * 已选择的图片
     */
    private ArrayList<String> mSelectImgs;

    private GridView mGirdView;
    private GridView mFullGirdView;
    private MyImageAdapter mAdapter;
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mDirPaths = new HashSet<String>();

    /**
     * 扫描拿到所有的图片文件夹
     */
    private List<ImageFolder> mImageFloders = new ArrayList<ImageFolder>();

    private RelativeLayout mBottomLy;
    private RelativeLayout mFullBottomLy;
    private RelativeLayout mHeadLy;
    private RelativeLayout mRightLy;
    private RelativeLayout mAlbumAdjust;
    private RelativeLayout mFullPhotoLy;
    private TextView mTitle;
    private TextView mRightText;
    int totalCount = 0;

    private int mScreenHeight;

    private ListImageDirPopupWindow mListImageDirPopupWindow;

    private static final boolean IN_MEMORY_CROP = Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD_MR1;
    private Uri fileUri;
    private Bitmap oriBitmap;
    private int initWidth, initHeight;
    private static final int MAX_WRAP_SIZE = 2048;
    ImageViewTouch cropImage;
    ViewGroup drawArea;

    private String from;
    private String lastPhotoDir = FileUtil.getInst().getLastPhotoPath();
    private String systemPhotoDir = FileUtil.getInst().getSystemPhotoPath();
    private String systemPhotoDir1 = systemPhotoDir + CAMERA;
    private File cameraFile = new File(systemPhotoDir1);
    private static final String CAMERA = "/Camera";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);

        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;
        initView();
        getImages();
        initEvent();
    }

    /**
     * 初始化View
     */
    public void initView() {
        mTitle = (TextView) findViewById(R.id.ly_header).findViewById(R.id.tv_title);
        mGirdView = (GridView) findViewById(R.id.id_gridView);
        mFullGirdView = (GridView) findViewById(R.id.full_gridView);
        mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
        mFullBottomLy = (RelativeLayout) findViewById(R.id.full_bottom_ly);
        mHeadLy = (RelativeLayout) findViewById(R.id.tv_middle);
        mRightLy = (RelativeLayout) findViewById(R.id.tv_right);
        mRightText = (TextView) findViewById(R.id.tv_right_txt);
        mAlbumAdjust = (RelativeLayout) findViewById(R.id.album_adjust);
        mFullPhotoLy = (RelativeLayout) findViewById(R.id.full_album);

        cropImage = (ImageViewTouch) findViewById(R.id.crop_image);
        drawArea = (ViewGroup) findViewById(R.id.draw_area);
    }

    private void initEvent() {
        from = getIntent().getStringExtra(DiscoveryConstants.FROM);
        MyLog.i("from = " + from);

        if (StringUtil.isFromChat(from)) {
            mRightText.setText(getString(R.string.chat_send));
        }

        /**
         * 为底部的布局设置点击事件，弹出popupWindow
         */
        mHeadLy.setOnClickListener(chooseDirListener);

        mRightLy.setOnClickListener(v -> {
            if (mImgDir == null) {
                AppContext.showToast(R.string.no_picture_can_select);
                finish();
                return;
            }
            if (StringUtil.isFromChat(from)) {
                String path = fileUri.getPath().replaceFirst("file://", "");
                EventBus.getDefault().post(new MineManager.SelectPhotoEvent(path));
                finish();
                return;
            }
            showProgressDialog("图片处理中...");
            new Thread() {
                public void run() {
                    cropImage();
                    dismissProgressDialog();
                }

            }.start();
        });

        findViewById(R.id.adjust_image_down).setOnClickListener(v -> {
            mFullPhotoLy.setVisibility(View.GONE);
        });

        findViewById(R.id.adjust_image_up).setOnClickListener(v -> {
            mFullPhotoLy.setVisibility(View.VISIBLE);
        });

        findViewById(R.id.tv_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setImage() {
        cropImage.setBackgroundColor(getResources().getColor(R.color.white));
        drawArea.getLayoutParams().height = AppContext.getApp().getScreenWidth();
        InputStream inputStream = null;
        try {
            //得到图片宽高比
            double rate = ImageUtils.getImageRadio(getContentResolver(), fileUri);
//            oriBitmap = ImageUtils.decodeBitmapWithOrientationMax(fileUri.getPath(), AppContext.getApp().getScreenWidth(), AppContext.getApp().getScreenHeight());
            //长图片时会按照长边缩放比缩放,会失真严重
            oriBitmap = ImageUtils.decodeBitmapWithOrientation(fileUri.getPath(), AppContext.getApp().getScreenWidth(), AppContext.getApp().getScreenHeight());

            initWidth = oriBitmap.getWidth();
            initHeight = oriBitmap.getHeight();
            cropImage.setImageBitmap(oriBitmap, new Matrix(), 1f, 10f);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeStream(inputStream);
        }
    }

    private void cropImage() {
        Bitmap croppedImage;
        if (IN_MEMORY_CROP) {
            MyLog.i("ljc:MEMROY_CROP........");
            croppedImage = inMemoryCrop(cropImage);
        } else {
            try {
                MyLog.i("ljc:decodeRegionCrop........");
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
                String path = FileUtil.getInst().getCacheDir() + "/croppedcache.jpg";
                ImageUtils.saveToFile(path,
                        false, croppedImage, false);
                Intent i = new Intent();
                i.setData(Uri.parse("file://" + path));
                setResult(RESULT_OK, i);
                dismissProgressDialog();
                finish();

                fileUri = Uri.parse("file://" + path);

                if (StringUtil.isFromRegister(from) || StringUtil.isFromPersonalInfo(from)) {
                    EventBus.getDefault().post(new String(path));
                    CameraManager.getInst().close();
                    return;
                }
                Intent newIntent = new Intent(this, PhotoGlorifyActivity.class);
                newIntent.putExtra(DiscoveryConstants.FROM, StringUtil.nullToEmpty(from));
                newIntent.setData(fileUri);
                startActivity(newIntent);
                croppedImage.recycle();
            } catch (Exception e) {
                e.printStackTrace();
                toast("裁剪图片异常，请稍后重试", Toast.LENGTH_LONG);
            }
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

        if (left < 0) left = 0;
        if (right > initWidth) right = initWidth;
        if (top < 0) top = 0;
        if (bottom > initHeight) bottom = initHeight;
        Rect rect = new Rect(left, top, right, bottom);
        InputStream is = null;
        System.gc();
        Bitmap croppedImage = null;
        Bitmap bitmap = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oriBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            is = new ByteArrayInputStream(baos.toByteArray());
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
            croppedImage = decoder.decodeRegion(rect, new BitmapFactory.Options());

            if (croppedImage != null) {
                int w, leftMargin, topMargin;
                if (croppedImage.getHeight() > croppedImage.getWidth()) {
                    w = croppedImage.getHeight();
                    leftMargin = (w - croppedImage.getWidth()) / 2;
                    topMargin = 0;
                } else {
                    w = croppedImage.getWidth();
                    leftMargin = 0;
                    topMargin = (w - croppedImage.getHeight()) / 2;
                }

                bitmap = Bitmap.createBitmap(w, w, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(croppedImage, leftMargin, topMargin, null);
            }
        } catch (Throwable e) {

        } finally {
            IOUtil.closeStream(is);
        }
        return bitmap;
    }

    private float getImageRadio() {
        return Math.max((float) initWidth, (float) initHeight)
                / Math.min((float) initWidth, (float) initHeight);
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

//===========================original select photo================

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        // 显示进度条
        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

        new Thread(new Runnable() {
            @Override
            public void run() {

                String firstImage = null;

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = SelectPhotoActivity.this
                        .getContentResolver();

                // 只查询jpeg,webp,gif和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/*", "image/jpeg", "image/png", "image/webp", "image/gif"},
                        MediaStore.Images.Media.DATE_MODIFIED + " desc");

                if (mCursor != null) {
//                Log.e("TAG", mCursor.getCount() + "");
//                MyLog.i("lastPhotoDir= "+ lastPhotoDir);
                    while (mCursor.moveToNext()) {
                        // 获取图片的路径
                        String path = mCursor.getString(mCursor
                                .getColumnIndex(MediaStore.Images.Media.DATA));
                        // 获取该图片的父路径名
                        File parentFile = new File(path).getParentFile();
                        if (parentFile == null) {
                            continue;
                        }

                        // 默认第一个选中的目录是相册目录, 因为由于android版本的差异
                        // 暂时无法精确得知系统相册的目录(有的是/DCIM/ 大部分是/DCIM/Camera/)
                        // 这里统一把/DCIM/及子目录/Camera下面的图片都当作/DCIM 目录的来处理
                        // todo 终极方案是能得到系统相册的准确路径
                        if (path.contains(lastPhotoDir)
                                && firstImage == null) {
                            firstImage = path;
                            MyLog.i("system DCIM =" + firstImage);
                            fileUri = Uri.fromFile(new File(firstImage));
//                        mImgDir = new File(firstImage).getParentFile();
                            mImgDir = new File(lastPhotoDir);
                        }

                        // 处理/DCIM目录和/Camera子目录
                        String dirPath = parentFile.getAbsolutePath();
                        if (dirPath.equalsIgnoreCase(systemPhotoDir) || dirPath.equalsIgnoreCase(systemPhotoDir1)) {
                            dirPath = systemPhotoDir;
                        }

                        ImageFolder imageFloder = null;
                        // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                        if (mDirPaths.contains(dirPath)) {
                            continue;
                        } else {
                            mDirPaths.add(dirPath);
                            // 初始化imageFloder
                            imageFloder = new ImageFolder();
                            imageFloder.setDir(dirPath);
                            imageFloder.setFirstImagePath(path);
                        }

                        String[] filelist = listFiles(new File(dirPath));
                        int picSize;
                        if (filelist != null) {
                            picSize = filelist.length;
                            totalCount += picSize;
                        } else {
                            picSize = 0;
                        }

                        //处理/DCIM子目录目录
                        if (dirPath.equalsIgnoreCase(systemPhotoDir)) {
                            String[] cameralist = listFiles(cameraFile);
                            if (cameralist != null) {
                                picSize += cameralist.length;
                            }
                        }

                        imageFloder.setCount(picSize);
                        MyLog.i("扫描完了:" + imageFloder.toString());
                        if (picSize > 0) {
                            mImageFloders.add(imageFloder);
                        }

                        if (picSize > mPicsSize) {
//                        mPicsSize = picSize;
//                        mImgDir = parentFile;
                        }
                    }

                    // 如果上次选择的文件夹中无文件时,确定一个新的文件夹[优先用系统文件夹]
                    if (firstImage == null) {
                        if (mDirPaths.contains(systemPhotoDir)) {
                            for (ImageFolder imageFolder : mImageFloders) {
                                if (systemPhotoDir.equals(imageFolder.getDir())) {
                                    fileUri = Uri.fromFile(new File(imageFolder.getFirstImagePath()));
                                    mImgDir = new File(systemPhotoDir);
                                }
                            }

                        } else {
                            if (mImageFloders.size() > 0) {
                                fileUri = Uri.fromFile(new File(mImageFloders.get(0).getFirstImagePath()));
                                mImgDir = new File(mImageFloders.get(0).getDir());
                            }
                        }
                    }

                    mCursor.close();
                }
                // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths = null;

                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0x110);

            }
        }).start();

    }

    private String[] listFiles(File dir) {
        if (dir == null || !dir.exists()) return null;
        return dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.toLowerCase().endsWith(".jpg")
                        || filename.endsWith(".jpeg")
                        || filename.endsWith(".png")
                        || filename.endsWith(".gif")
                        || filename.endsWith(".webp");
            }
        });
    }

    private File[] getFileList(File dir) {
        if (dir == null || !dir.exists()) return null;
        return dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                String tmp = filename.toLowerCase();
                return tmp.endsWith(".jpg") || tmp.endsWith(".png") || tmp.endsWith(".gif")
                        || tmp.endsWith(".jpeg") || tmp.endsWith(".webp");
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            mFullPhotoLy.setVisibility(View.GONE);
            switch (msg.what) {
                case 0x119:
                    String uri = msg.getData().getString("uri");
                    MyLog.i("handleMessage 119 uri= " + uri);
                    fileUri = Uri.fromFile(new File(uri));
                    setImage();
                    break;

                case 0x110:
                    mProgressDialog.dismiss();
                    MyLog.i("handleMessage 110      first time setImage fileUri = " + fileUri);
                    setImage();
                    // 为View绑定数据
                    data2View();
                    // 初始化展示文件夹的popupWindw
                    initListDirPopupWindw();
                    break;
                default:
                    break;
            }
        }
    };

    private OnClickListener chooseDirListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mListImageDirPopupWindow
                    .setAnimationStyle(R.style.popwin_anim_style);
            mListImageDirPopupWindow.showAsDropDown(mFullPhotoLy.getVisibility() == View.VISIBLE ?
                    mFullBottomLy : mBottomLy, 0, 0);

            // 设置背景颜色变暗
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = .3f;
            getWindow().setAttributes(lp);
        }
    };

    /**
     * 为View绑定数据
     */
    private void data2View() {
        if (mImgDir == null) {
            AppContext.showToast(R.string.no_photo);
            return;
        }
        mTitle.setText(mImgDir.getName());
        List<File> fileList = null;
        File[] files = getFileList(mImgDir);
        if (files != null) {
            fileList = Arrays.asList(files);
        }

        // 处理/DCIM/Camera子目录
        if (mImgDir.getAbsolutePath().equalsIgnoreCase(systemPhotoDir)
                || mImgDir.getAbsolutePath().equalsIgnoreCase(systemPhotoDir)) {
            List<File> cameraFileList = Arrays.asList(getFileList(cameraFile));
            if (cameraFileList != null && cameraFileList.size() > 0) {
                mImgFiles = new ArrayList<File>();
                if (fileList != null) {
                    for (File file : fileList) {
                        mImgFiles.add(file);
                    }
                }
                for (File file1 : cameraFileList) {
                    if (!isFileInList(mImgFiles, file1))
                        mImgFiles.add(file1);
                }
            } else {
                mImgFiles = fileList;
            }
        } else {
            mImgFiles = fileList;
        }
        getPathsFromFiles();
        setAdapter();
    }


    /**
     * 初始化展示文件夹的popupWindw
     */
    private void initListDirPopupWindw() {
        View popUpView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.imgloader_list_dir, null);
        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                LayoutParams.MATCH_PARENT, (int) (mScreenHeight),
                mImageFloders, popUpView);

        mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(this);
        if (popUpView != null) {
            TextView textView = (TextView) popUpView.findViewById(R.id.ly_header).findViewById(R.id.tv_title);
            if (textView != null && mImgDir != null) {
                textView.setText(mImgDir.getName());
            }
            // 隐藏继续的问题
            popUpView.findViewById(R.id.tv_right).setVisibility(View.GONE);
            popUpView.findViewById(R.id.tv_left).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListImageDirPopupWindow.dismiss();
                }
            });
        }
    }


    @Override
    public void selected(ImageFolder folder) {
        MyLog.i("selected mImgDir = " + folder.toString());
        mImgDir = new File(folder.getDir());
        // 记住上次选择的相册文件夹
        StickPreference.getInstance().setLastImageFolder(folder.getDir());

        mTitle.setText(mImgDir.getName());
        ((TextView) mListImageDirPopupWindow.findViewById(R.id.ly_header).findViewById(R.id.tv_title)).setText(mImgDir.getName());

        List<File> fileList = null;
        File[] files = getFileList(mImgDir);
        if (files != null) {
            fileList = Arrays.asList(files);
        }
        // 处理/DCIM/Camera子目录
        if (mImgDir.getAbsolutePath().equalsIgnoreCase(systemPhotoDir)
                || mImgDir.getAbsolutePath().equalsIgnoreCase(systemPhotoDir)) {
            files = getFileList(cameraFile);
            if (files != null) {
                List<File> cameraFileList = Arrays.asList(files);
                if (cameraFileList != null && cameraFileList.size() > 0) {
                    mImgFiles = new ArrayList<File>();
                    for (File file : fileList) {
                        mImgFiles.add(file);
                    }
                    for (File file1 : cameraFileList) {
                        if (!isFileInList(mImgFiles, file1))
                            mImgFiles.add(file1);
                    }
                } else {
                    mImgFiles = fileList;
                }
            } else {
                mImgFiles = fileList;
            }
        } else {
            mImgFiles = fileList;
        }
        getPathsFromFiles();
        setAdapter();

        Message message = Message.obtain(mHandler, 0x119);
        Bundle b = new Bundle();
//        b.putString("uri", mImgDir + "/" + mImgFiles.get(0).getName());
        b.putString("uri", mImgFiles.get(0).getAbsolutePath());
        message.setData(b);
        message.sendToTarget();

        mFullPhotoLy.setVisibility(View.GONE);
        mListImageDirPopupWindow.dismiss();
    }

    private boolean isFileInList(List<File> list, File file) {
        if(list == null || list.size() == 0 || file == null || !file.exists()) {
            return false;
        }
        for (File f : list) {
            if (f == null) continue;
            if (f.getAbsolutePath().equalsIgnoreCase(file.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    private void getPathsFromFiles() {
        Collections.sort(mImgFiles, new FileComparator());
        if (mImgs == null) {
            mImgs = new ArrayList<>();
        } else {
            mImgs.clear();
        }
        for (File photo : mImgFiles) {
            mImgs.add(photo.getAbsolutePath());
        }
    }

    private void setAdapter() {
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new MyImageAdapter(getApplicationContext(), mImgs,
                R.layout.imgloader_grid_item, mImgDir.getAbsolutePath(), mHandler);
        mGirdView.setAdapter(mAdapter);
//
//        mGirdView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mAdapter.setSeclection(position);
//                mAdapter.notifyDataSetChanged();
//            }
//        });
        mFullGirdView.setAdapter(mAdapter);
//        mFullGirdView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mAdapter.setSeclection(position);
//                mAdapter.notifyDataSetChanged();
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (oriBitmap != null) {
            oriBitmap.recycle();
        }
    }

    //最后修改的照片在前
    private class FileComparator implements Comparator<File> {

        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.lastModified() == rhs.lastModified()) {
                return 0;
            }
            if (lhs.lastModified() < rhs.lastModified()) {
                return 1;
            } else {
                return -1;
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.SELECT_PHOTO_ACTIVITY); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.SELECT_PHOTO_ACTIVITY); //
        MobclickAgent.onPause(this);
    }
}
