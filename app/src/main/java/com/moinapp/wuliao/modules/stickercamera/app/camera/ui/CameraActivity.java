package com.moinapp.wuliao.modules.stickercamera.app.camera.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.FloatMath;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.imagezoom.ImageViewTouch;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.modules.mission.MissionPreference;
import com.moinapp.wuliao.modules.sticker.StickPreference;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraBaseActivity;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.modules.stickercamera.app.camera.adapter.PhotoHLAdapter;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.CameraHelper;
import com.moinapp.wuliao.modules.stickercamera.app.model.PhotoItem;
import com.moinapp.wuliao.ui.CameraGrid;
import com.moinapp.wuliao.ui.imageselect.SelectPhotoActivity;
import com.moinapp.wuliao.util.DistanceUtil;
import com.moinapp.wuliao.util.FileUtil;
import com.moinapp.wuliao.util.IOUtil;
import com.moinapp.wuliao.util.ImageUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.sephiroth.android.library.widget.HListView;

/**
 * 相机和选择图片界面
 * Created by sky on 15/7/6.
 */
public class CameraActivity extends CameraBaseActivity {

    private static final ILogger MyLog = LoggerFactory.getLogger("ca");
    private CameraHelper mCameraHelper;
    private Camera.Parameters parameters = null;
    private Camera cameraInst = null;
    private Bundle bundle = null;//存放拍照数据
    private int photoWidth = DistanceUtil.getCameraPhotoWidth();
    private int photoMargin = AppContext.getApp().dp2px(6);
    private float pointX, pointY;
    static final int FOCUS = 1;            // 聚焦
    static final int ZOOM = 2;            // 缩放
    private int mode;                      //0是聚焦 1是放大
    private float dist;
    private int PHOTO_SIZE = 2000;
    private int mCurrentCameraId = 0;  //1是前置 0是后置
    private Handler handler = new Handler();

    //剪切图片的变量
    private static final boolean IN_MEMORY_CROP = Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD_MR1;
    private Uri fileUri;
    private Bitmap oriBitmap;
    private int initWidth, initHeight;
    private static final int MAX_WRAP_SIZE = 2048;
    private String from;
    private List<PhotoItem> mPhotoList;
    private PhotoHLAdapter photoHLAdapter;

    @InjectView(R.id.masking)
    CameraGrid cameraGrid;

    @InjectView(R.id.photo_area_ly)
    LinearLayout photoAreaLy;

    @InjectView(R.id.photo_area)
    HListView photoArea;

    //    @InjectView(R.id.panel_photo)
//    View optionPhotoPanel;
    @InjectView(R.id.panel_take_photo)
    View takePhotoPanel;
    @InjectView(R.id.takepicture)
    Button takePicture;

    //进入下一步的按钮
    @InjectView(R.id.panel_crop_photo)
    View cropPhotoPanel;
    @InjectView(R.id.cancel)
    ImageView cancel;
    @InjectView(R.id.picked)
    TextView process;

    //进入下一步的按钮
    @InjectView(R.id.panel_simple_take)
    View simpleTakePanel;
    @InjectView(R.id.btn_cancel)
    Button btn_cancel;
    @InjectView(R.id.btn_complete)
    Button btn_complete;

    @InjectView(R.id.panel_simple_pick)
    View simplePickPanel;
    @InjectView(R.id.return_to)
    ImageView returnTo;
    @InjectView(R.id.btn_ok)
    Button btnOk;

    @InjectView(R.id.flashBtn)
    ImageView flashBtn;
    @InjectView(R.id.change)
    ImageView changeBtn;
    @InjectView(R.id.back)
    ImageView backBtn;
    @InjectView(R.id.next)
    LinearLayout galleryBtn;
    @InjectView(R.id.focus_index)
    View focusIndex;
    @InjectView(R.id.surfaceView)
    SurfaceView surfaceView;

    // 剪切图片显示区域
    @InjectView(R.id.crop_image)
    ImageViewTouch cropImage;
    @InjectView(R.id.draw_area)
    ViewGroup drawArea;
    @InjectView(R.id.wrap_image)
    View wrapImage;
    @InjectView(R.id.btn_crop_type)
    View btnCropType;
    @InjectView(R.id.image_center)
    ImageView imageCenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mCameraHelper = new CameraHelper(this);
        ButterKnife.inject(this);
        initView();
        initEvent();

        if (!StringUtil.isFromRegister(from) && !StringUtil.isFromPersonalInfo(from) &&
                (photoHLAdapter != null && photoHLAdapter.getCount() > 0)) {
            processPhotoItem((PhotoItem) photoHLAdapter.getItem(0), false);
            photoHLAdapter.setLastClickItem(0);
        }

        //如果是从任务跳转而来且不是第一次进入,弹出引导图
        if (StringUtil.isFromMission(from)
                && MissionPreference.getInstance().getCosplayGuide() == 0) {
            showGuide();
        }
    }

    private void initView() {
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.setKeepScreenOn(true);
        surfaceView.setFocusable(true);
        surfaceView.setBackgroundColor(TRIM_MEMORY_BACKGROUND);
        surfaceView.getHolder().addCallback(new SurfaceCallback());//为SurfaceView的句柄添加一个回调函数，里面直接打开相机

        //设置相机界面,照片列表,以及拍照布局的高度(保证相机预览为正方形)
        ViewGroup.LayoutParams layout = cameraGrid.getLayoutParams();
        layout.height = AppContext.getApp().getScreenWidth();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) photoAreaLy.getLayoutParams();
        layoutParams.height = DistanceUtil.getCameraPhotoAreaHeight();
        MyLog.i("photoAreaLy.height=" + layoutParams.height);
        layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.camera_top_h)
                + AppContext.getApp().getScreenWidth();
        MyLog.i("photoAreaLy.topMargin=" + layoutParams.topMargin);

        /*
        layout = optionPhotoPanel.getLayoutParams();
        MyLog.i("optionPhotoPanel.height=" + layout.height);
        layout = takePhotoPanel.getLayoutParams();
        */
        int height =
//                layout.height =
                AppContext.getApp().getScreenHeight()
                        - AppContext.getApp().getScreenWidth()
                        - getResources().getDimensionPixelSize(R.dimen.camera_top_h)
                        - DistanceUtil.getCameraPhotoAreaHeight();
        MyLog.i("takePhotoPanel.height=" + layout.height);
        layout = cropPhotoPanel.getLayoutParams();
        layout.height = height;
        MyLog.i("cropPhotoPanel.height=" + layout.height);

        layout = simpleTakePanel.getLayoutParams();
        layout.height = height;
        MyLog.i("simpleTakePanel.height=" + layout.height);

        layout = simplePickPanel.getLayoutParams();
        layout.height = height;
        MyLog.i("simplePickPanel.height=" + layout.height);

        // 取消因15张图片滑动导致的边界回弹效果,降低OOM风险
        photoArea.setOverScrollMode(View.OVER_SCROLL_NEVER);
        //添加系统相册内的图片
        ArrayList<PhotoItem> sysPhotos = FileUtil.getInst().findPicsInDir(
                FileUtil.getInst().getSystemPhotoPath());
        int showNumber = sysPhotos.size() > Constants.PHOTO_NUM ? Constants.PHOTO_NUM
                : sysPhotos.size();
        mPhotoList = sysPhotos.subList(0, showNumber);
        photoHLAdapter = new PhotoHLAdapter(CameraActivity.this, CameraActivity.this, mPhotoList);
        photoArea.setAdapter(photoHLAdapter);
        photoHLAdapter.setOnPhotoItemSelectedListener(new PhotoHLAdapter.OnPhotoItemSelectedListener() {
            @Override
            public void onPhotoItemSelected(Activity activity, PhotoItem photo) {
                // 处理推荐的15个DCIM/Camera中的图片
                boolean value = (StringUtil.isFromRegister(from) || StringUtil.isFromPersonalInfo(from)) ?
                        true : false;
                processPhotoItem(photo, value);
            }
        });
//        photoArea.setOnItemClickListener(new it.sephiroth.android.library.widget.AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> adapterView, View v, int i, long l) {
//                if (v instanceof ImageView && v.getTag() instanceof String) {
//                    // 处理推荐的4个DCIM/Camera中的图片
//                    CameraManager.getInst().processPhotoItem(CameraActivity.this,
//                            new PhotoItem(sysPhotos.get(i).getImageUri(), System.currentTimeMillis()));
//                }
//            }
//        });
        setGalleryParams();

        drawArea.getLayoutParams().height = AppContext.getApp().getScreenWidth();
    }

    private void setGalleryParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                photoWidth-6, photoWidth);
//        params.leftMargin = photoMargin;
//        params.rightMargin = photoMargin;
        //params.gravity = Gravity.CENTER;
        //galleryBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
        galleryBtn.setLayoutParams(params);
    }

    private void initEvent() {
        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                from = getIntent().getExtras().getString(DiscoveryConstants.FROM);
                MyLog.i("from = " + from);
            }
        }
        //拍照
        takePicture.setOnClickListener(v -> {
            try {
                cameraInst.takePicture(null, null, new MyPictureCallback());
            } catch (Throwable t) {
                t.printStackTrace();
                toast("拍照失败，请重试！", Toast.LENGTH_LONG);
                try {
                    cameraInst.startPreview();
                } catch (Throwable e) {

                }
            }

        });

        //闪光灯
        flashBtn.setOnClickListener(v -> turnLight(cameraInst));
        //前后置摄像头切换
        boolean canSwitch = false;
        try {
            canSwitch = mCameraHelper.hasFrontCamera() && mCameraHelper.hasBackCamera();
        } catch (Exception e) {
            //获取相机信息失败
        }
        if (!canSwitch) {
            changeBtn.setVisibility(View.GONE);
        } else {
//            if(StringUtil.isFromPersonalInfo(from) || StringUtil.isFromRegister(from)) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(5);
//                            switchCamera();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
//            }
            changeBtn.setOnClickListener(v -> switchCamera());
        }
        //跳转相册
//        galleryBtn.setOnClickListener(v -> startActivity(new Intent(CameraActivity.this, SelectPhotoActivity.class)));
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraActivity.this, SelectPhotoActivity.class);
                intent.putExtra(DiscoveryConstants.FROM, from);
                MyLog.i("SelectPhotoActivity from = " + from);
                CameraActivity.this.startActivity(intent);
            }
        });

        //返回按钮
        backBtn.setOnClickListener(v -> onCancel());

        /**
         *
         MotionEvent.ACTION_DOWN：在第一个点被按下时触发
         MotionEvent.ACTION_UP:当屏幕上唯一的点被放开时触发
         MotionEvent.ACTION_POINTER_DOWN:当屏幕上已经有一个点被按住，此时再按下其他点时触发。
         MotionEvent.ACTION_POINTER_UP:当屏幕上有多个点被按住，松开其中一个点时触发（即非最后一个点被放开时）。
         MotionEvent.ACTION_MOVE：当有点在屏幕上移动时触发。值得注意的是，由于它的灵敏度很高，而我们的手指又不可能完全静止（即使我们感觉不到移动，但其实我们的手指也在不停地抖动），所以实际的情况是，基本上只要有点在屏幕上，此事件就会一直不停地被触发。
         */
        // 应该先判断是否支持缩放cameraInst.getParameters().isZoomSupported()
        surfaceView.setOnTouchListener((v, event) -> {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                // 主点按下
                case MotionEvent.ACTION_DOWN:
                    pointX = event.getX();
                    pointY = event.getY();
                    mode = FOCUS;
                    break;
                // 副点按下
                case MotionEvent.ACTION_POINTER_DOWN:
                    dist = spacing(event);
                    // 如果连续两点距离大于10，则判定为多点模式
                    if (dist > 10f) {
                        mode = ZOOM;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    mode = FOCUS;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == FOCUS) {
                        //pointFocus((int) event.getRawX(), (int) event.getRawY());
                    } else if (mode == ZOOM) {
                        float newDist = spacing(event);
                        if (newDist > 10f) {
                            float tScale = (newDist - dist) / dist;
                            if (tScale < 0) {
                                tScale = tScale * 10;
                            }
                            addZoomIn((int) tScale);
                        }
                    }
                    break;
            }
            return false;
        });

        // 对焦点的上下边界
        int focusTopPoint = getResources().getDimensionPixelSize(R.dimen.camera_top_h);
        int focusBottomPoint = focusTopPoint + AppContext.getApp().getScreenWidth();

        //重新定点对焦
        surfaceView.setOnClickListener(v -> {
            try {
                pointFocus((int) pointX, (int) pointY);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (pointY < focusBottomPoint && pointY > focusTopPoint) {
                RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(focusIndex.getLayoutParams());
                layout.setMargins((int) pointX - 60, (int) pointY - 60, 0, 0);
                focusIndex.setLayoutParams(layout);
                focusIndex.setVisibility(View.VISIBLE);
                ScaleAnimation sa = new ScaleAnimation(2f, 1f, 2f, 1f,
                        ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
                sa.setDuration(800);
                focusIndex.startAnimation(sa);
                handler.postDelayed(() -> focusIndex.setVisibility(View.INVISIBLE), 800);
            }
        });

        takePhotoPanel.setOnClickListener(v -> {
            //doNothing 防止聚焦框出现在拍照区域
        });

        btnCropType.setOnClickListener(v -> {
            if (cropImage.getVisibility() == View.VISIBLE) {
                btnCropType.setSelected(true);
                cropImage.setVisibility(View.GONE);
                wrapImage.setVisibility(View.VISIBLE);
            } else {
                btnCropType.setSelected(false);
                cropImage.setVisibility(View.VISIBLE);
                wrapImage.setVisibility(View.GONE);
            }
        });
        imageCenter.setOnClickListener(v -> wrapImage.setSelected(!wrapImage.isSelected()));
        cancel.setOnClickListener(v -> {
            //返回拍摄页面时,清除上一次选择照片的红色边框
            photoHLAdapter.setLastClickItem(-1);
            photoHLAdapter.notifyDataSetChanged();
            startCrop(false, false);
        });
        btn_cancel.setOnClickListener(v -> startCrop(false, false));
        returnTo.setOnClickListener(v -> startCrop(false, false));
        process.setOnClickListener(v -> {
            processImage();
        });

        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processImage();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processImage();
            }
        });
    }

    private void onCancel() {
        StickPreference.getInstance().deleteJoinTopicInfo();
        finish();
    }
    private void processImage() {
        showProgressDialog("图片处理中...");
        new Thread() {
            public void run() {
                if (btnCropType.isSelected()) {
                    wrapImage();
                } else {
                    cropImage();
                }
                dismissProgressDialog();
            }
        }.start();
    }

    /**
     * 返回拍照或选取好的图片
     */
    private void returnPicture() {
        EventBus.getDefault().post(new String(fileUri.getPath()));
        CameraManager.getInst().close();
    }

    /**
     * @param crop        true:选择图片完成进行裁剪;
     *                    false:重新回到拍摄页
     * @param fromTakePic fromTakePic:是否来自拍摄
     */
    private void startCrop(boolean crop, boolean fromTakePic) {

        if (!crop) {
            takePhotoPanel.setVisibility(View.VISIBLE);
            cropPhotoPanel.setVisibility(View.GONE);
            cameraGrid.setVisibility(View.GONE);//View.VISIBLE
            surfaceView.setVisibility(View.VISIBLE);
            drawArea.setVisibility(View.GONE);
            flashBtn.setVisibility(View.VISIBLE);
            changeBtn.setVisibility(View.VISIBLE);
            if (cameraInst != null) {
                cameraInst.startPreview();
            }

            simplePickPanel.setVisibility(View.GONE);
            simpleTakePanel.setVisibility(View.GONE);
        } else {
            if (StringUtil.isFromRegister(from) || StringUtil.isFromPersonalInfo(from)) {
                takePhotoPanel.setVisibility(View.GONE);
                cropPhotoPanel.setVisibility(View.GONE);
                cameraGrid.setVisibility(View.GONE);
                surfaceView.setVisibility(View.GONE);
                drawArea.setVisibility(View.VISIBLE);
                flashBtn.setVisibility(View.GONE);
                changeBtn.setVisibility(View.GONE);
//
//                if (fromTakePic) {
//                    // 从注册或者换头像时
//                    simplePickPanel.setVisibility(View.VISIBLE);
//                    simpleTakePanel.setVisibility(View.GONE);
//                } else {
//                    // 普通拍照选图片
//                }
                simplePickPanel.setVisibility(View.VISIBLE);
                simpleTakePanel.setVisibility(View.GONE);
            } else {
                takePhotoPanel.setVisibility(View.GONE);
                cropPhotoPanel.setVisibility(View.VISIBLE);
                cameraGrid.setVisibility(View.GONE);
                surfaceView.setVisibility(View.GONE);
                drawArea.setVisibility(View.VISIBLE);
                flashBtn.setVisibility(View.GONE);
                changeBtn.setVisibility(View.GONE);

                simplePickPanel.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent result) {
        if (requestCode == Constants.REQUEST_PICK && resultCode == RESULT_OK) {
            // 处理图库选择的图片
            CameraManager.getInst().processPhotoItem(
                    CameraActivity.this,
                    new PhotoItem(result.getData().getPath(), System
                            .currentTimeMillis()), from);
        } else if (requestCode == Constants.REQUEST_CROP && resultCode == RESULT_OK) {
            Intent newIntent = new Intent(this, PhotoProcessActivity.class);
            newIntent.setData(result.getData());
            startActivity(newIntent);
        }
    }

    /**
     * 选择了推荐的15张图之一
     */
    public void processPhotoItem(PhotoItem photo, boolean fromTakePic) {
        Uri uri = photo.getImageUri().startsWith("file:") ? Uri.parse(photo
                .getImageUri()) : Uri.parse("file://" + photo.getImageUri());
        fileUri = uri;

        startCrop(true, fromTakePic);

        InputStream inputStream = null;
        try {
            //得到图片宽高比
//            double rate = ImageUtils.getImageRadio(getContentResolver(), fileUri);
//            oriBitmap = ImageUtils.decodeBitmapWithOrientationMax(fileUri.getPath(), AppContext.getApp().getScreenWidth(), AppContext.getApp().getScreenHeight());
            //长图片时会按照长边缩放比缩放,会失真严重
            oriBitmap = ImageUtils.decodeBitmapWithOrientation(fileUri.getPath(), AppContext.getApp().getScreenWidth(), AppContext.getApp().getScreenHeight());

            initWidth = oriBitmap.getWidth();
            initHeight = oriBitmap.getHeight();

            cropImage.setImageBitmap(oriBitmap, new Matrix(), (float) 1, 10);
            imageCenter.setImageBitmap(oriBitmap);
        } catch (Exception e) {
            toast(getString(R.string.image_not_exist), Toast.LENGTH_LONG);
            e.printStackTrace();
        } finally {
            IOUtil.closeStream(inputStream);
        }

    }


    protected void wrapImage() {
        int width = initWidth > initHeight ? initWidth : initHeight;
        int imageSize = width < MAX_WRAP_SIZE ? width : MAX_WRAP_SIZE;

        int move = (int) ((initHeight - initWidth) / 2 / (float) width * (float) imageSize);
        int moveX = initWidth < initHeight ? move : 0;
        int moveY = initHeight < initWidth ? -move : 0;
        Bitmap croppedImage = null;
        try {
            croppedImage = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(croppedImage);
            Paint p = new Paint();
            p.setColor(wrapImage.isSelected() ? Color.BLACK : Color.WHITE);
            canvas.drawRect(0, 0, imageSize, imageSize, p);
            Matrix matrix = new Matrix();
            matrix.postScale((float) imageSize / (float) width, (float) imageSize / (float) width);
            matrix.postTranslate(moveX, moveY);
            canvas.drawBitmap(oriBitmap, matrix, null);
        } catch (OutOfMemoryError e) {
            Log.e("OOM cropping image: " + e.getMessage(), e.toString());
            System.gc();
        }
        saveImageToCache(croppedImage);
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
                String path = FileUtil.getInst().getCacheDir() + "/croppedcache.jpg";
                ImageUtils.saveToFile(path,
                        false, croppedImage, false);
                fileUri = Uri.parse("file://" + path);
                dismissProgressDialog();

                if (StringUtil.isFromPersonalInfo(from)) {//如果是从修改个人信息来,跳转个人信息修改页
                    returnPicture();
                } else if (StringUtil.isFromRegister(from)) {//从注册页面来
                    returnPicture();
                } else {
                    Intent newIntent = new Intent(this, PhotoGlorifyActivity.class);
                    newIntent.putExtra(DiscoveryConstants.FROM, StringUtil.nullToEmpty(from));
                    newIntent.setData(fileUri);
                    startActivity(newIntent);
                }
            } catch (Exception e) {
                e.printStackTrace();
                toast("裁剪图片异常，请稍后重试", Toast.LENGTH_LONG);
            }
            finally {
                if (croppedImage != null && !croppedImage.isRecycled()) {
                    croppedImage.recycle();
                }
            }
        } else {
            toast(getString(R.string.image_not_exist), Toast.LENGTH_LONG);
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

        // 如果图不是正方形,且图片缩放后未填满舞台的正方形, 则只截取原图的有效[有图片内容]部分.这样就不会"切"[decodeRegion(new Rect)]出来一个黑底了(in Rect).
        // 本身的黑底可以用canvas来填充想要的颜色
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
                // 用canvas来填充底部想要的颜色
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(croppedImage, leftMargin, topMargin, null);
            }
        } catch (Throwable e) {

        } finally {
            IOUtil.closeStream(is);
        }
        return bitmap;
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

    /**
     * 两点的距离
     */
    private float spacing(MotionEvent event) {
        if (event == null) {
            return 0;
        }
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }


    int curZoomValue = 0;

    /**
     * 放大缩小
     */
    private void addZoomIn(int delta) {

        try {
            Camera.Parameters params = cameraInst.getParameters();
            Log.d("Camera", "Is support Zoom " + params.isZoomSupported());
            if (!params.isZoomSupported()) {
                return;
            }
            curZoomValue += delta;
            if (curZoomValue < 0) {
                curZoomValue = 0;
            } else if (curZoomValue > params.getMaxZoom()) {
                curZoomValue = params.getMaxZoom();
            }

            if (!params.isSmoothZoomSupported()) {
                params.setZoom(curZoomValue);
                cameraInst.setParameters(params);
                return;
            } else {
                cameraInst.startSmoothZoom(curZoomValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 定点对焦的代码,重新对焦
     */
    private void pointFocus(int x, int y) {
        cameraInst.cancelAutoFocus();
        parameters = cameraInst.getParameters();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            showPoint(x, y);
        }
        cameraInst.setParameters(parameters);
        autoFocus();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void showPoint(int x, int y) {
        if (parameters.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> areas = new ArrayList<Camera.Area>();
            //xy变换了
            int rectY = -x * 2000 / AppContext.getApp().getScreenWidth() + 1000;
            int rectX = y * 2000 / AppContext.getApp().getScreenHeight() - 1000;

            int left = rectX < -900 ? -1000 : rectX - 100;
            int top = rectY < -900 ? -1000 : rectY - 100;
            int right = rectX > 900 ? 1000 : rectX + 100;
            int bottom = rectY > 900 ? 1000 : rectY + 100;
            Rect area1 = new Rect(left, top, right, bottom);
            areas.add(new Camera.Area(area1, 800));
            parameters.setMeteringAreas(areas);
        }

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
    }

    private final class MyPictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            bundle = new Bundle();
            bundle.putByteArray("bytes", data); //将图片字节数据保存在bundle当中，实现数据交换
            new SavePicTask(data).execute();
//            camera.startPreview(); // 拍完照后，重新开始预览
        }
    }

    private class SavePicTask extends AsyncTask<Void, Void, String> {
        private byte[] data;

        protected void onPreExecute() {
            showProgressDialog("处理中");
        }

        ;

        SavePicTask(byte[] data) {
            this.data = data;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return saveToSDCard(data);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (StringUtils.isNotEmpty(result)) {
                dismissProgressDialog();

                // 处理拍好的照片
//                    CameraManager.getInst().processPhotoItem(CameraActivity.this,
//                            new PhotoItem(result, System.currentTimeMillis()), from);
                PhotoItem item = new PhotoItem(result, System.currentTimeMillis());
                mPhotoList.add(0, item);
                if (mPhotoList.size() > Constants.PHOTO_NUM) {
                    mPhotoList.remove(mPhotoList.size() - 1);
                }
                photoHLAdapter.setLastClickItem(0);
                photoHLAdapter.notifyDataSetChanged();
                //如果是注册进来的,不跳转,显示取消确认按钮
                if (StringUtil.isFromRegister(from) || StringUtil.isFromPersonalInfo(from)) {
                    processPhotoItem(item, true);
                } else {
                    processPhotoItem(item, false);
                }
            } else {
                toast("拍照失败，请稍后重试！", Toast.LENGTH_LONG);
            }
        }
    }

    /**
     * SurfaceCallback
     */
    private final class SurfaceCallback implements SurfaceHolder.Callback {

        public void surfaceDestroyed(SurfaceHolder holder) {
            try {
                if (cameraInst != null) {
                    cameraInst.stopPreview();
                    cameraInst.release();
                    cameraInst = null;
                }
            } catch (Exception e) {
                //相机已经关了
            }

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (null == cameraInst) {
                try {
                    cameraInst = Camera.open();
                    cameraInst.setPreviewDisplay(holder);
                    initCamera();

                    //如果是注册或者换头像默认切换前置
                    if (StringUtil.isFromPersonalInfo(from) || StringUtil.isFromRegister(from)) {
                        switchCamera();
                    }

                    cameraInst.startPreview();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            autoFocus();
        }
    }

    /**
     * 实现自动对焦
     */
    private void autoFocus() {
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (cameraInst == null) {
                    return;
                }
                cameraInst.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            initCamera();
                        }
                    }
                });
            }
        };
    }

    private Camera.Size adapterSize = null;
    private Camera.Size previewSize = null;

    /**
     * 实现相机的参数初始化
     */
    private void initCamera() {
        parameters = cameraInst.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);
        //if (adapterSize == null) {
        setUpPicSize(parameters);
        setUpPreviewSize(parameters);
        //}
        if (adapterSize != null) {
            parameters.setPictureSize(adapterSize.width, adapterSize.height);
        }
        if (previewSize != null) {
            parameters.setPreviewSize(previewSize.width, previewSize.height);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
        } else {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        setDispaly(parameters, cameraInst);
        try {
            cameraInst.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cameraInst.startPreview();
        cameraInst.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上

        try {
            // 默认闪光灯自动
            List<String> supportedModes = cameraInst.getParameters().getSupportedFlashModes();
            if (supportedModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                cameraInst.setParameters(parameters);
                flashBtn.setImageResource(R.drawable.led_black);
            }
        } catch (Exception e) {
            MyLog.e(e);
        }
    }

    private void setUpPicSize(Camera.Parameters parameters) {

        if (adapterSize != null) {
            return;
        } else {
            adapterSize = findBestPictureResolution();
            return;
        }
    }

    private void setUpPreviewSize(Camera.Parameters parameters) {

        if (previewSize != null) {
            return;
        } else {
            previewSize = findBestPreviewResolution();
        }
    }

    /**
     * 最小预览界面的分辨率
     */
    private static final int MIN_PREVIEW_PIXELS = 480 * 320;
    /**
     * 最大宽高比差
     */
    private static final double MAX_ASPECT_DISTORTION = 0.15;
    private static final String TAG = "Camera";

    /**
     * 找出最适合的预览界面分辨率
     *
     * @return
     */
    private Camera.Size findBestPreviewResolution() {
        Camera.Parameters cameraParameters = cameraInst.getParameters();
        Camera.Size defaultPreviewResolution = cameraParameters.getPreviewSize();

        List<Camera.Size> rawSupportedSizes = cameraParameters.getSupportedPreviewSizes();
        if (rawSupportedSizes == null) {
            return defaultPreviewResolution;
        }

        // 按照分辨率从大到小排序
        List<Camera.Size> supportedPreviewResolutions = new ArrayList<Camera.Size>(rawSupportedSizes);
        Collections.sort(supportedPreviewResolutions, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (bPixels < aPixels) {
                    return -1;
                }
                if (bPixels > aPixels) {
                    return 1;
                }
                return 0;
            }
        });

        StringBuilder previewResolutionSb = new StringBuilder();
        for (Camera.Size supportedPreviewResolution : supportedPreviewResolutions) {
            previewResolutionSb.append(supportedPreviewResolution.width).append('x').append(supportedPreviewResolution.height)
                    .append(' ');
        }
        Log.v(TAG, "Supported preview resolutions: " + previewResolutionSb);


        // 移除不符合条件的分辨率
        double screenAspectRatio = (double) AppContext.getApp().getScreenWidth()
                / (double) AppContext.getApp().getScreenHeight();
        Iterator<Camera.Size> it = supportedPreviewResolutions.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewResolution = it.next();
            int width = supportedPreviewResolution.width;
            int height = supportedPreviewResolution.height;

            // 移除低于下限的分辨率，尽可能取高分辨率
            if (width * height < MIN_PREVIEW_PIXELS) {
                it.remove();
                continue;
            }

            // 在camera分辨率与屏幕分辨率宽高比不相等的情况下，找出差距最小的一组分辨率
            // 由于camera的分辨率是width>height，我们设置的portrait模式中，width<height
            // 因此这里要先交换然preview宽高比后在比较
            boolean isCandidatePortrait = width > height;
            int maybeFlippedWidth = isCandidatePortrait ? height : width;
            int maybeFlippedHeight = isCandidatePortrait ? width : height;
            double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove();
                continue;
            }

            // 找到与屏幕分辨率完全匹配的预览界面分辨率直接返回
            if (maybeFlippedWidth == AppContext.getApp().getScreenWidth()
                    && maybeFlippedHeight == AppContext.getApp().getScreenHeight()) {
                return supportedPreviewResolution;
            }
        }

        // 如果没有找到合适的，并且还有候选的像素，则设置其中最大比例的，对于配置比较低的机器不太合适
        if (!supportedPreviewResolutions.isEmpty()) {
            Camera.Size largestPreview = supportedPreviewResolutions.get(0);
            return largestPreview;
        }

        // 没有找到合适的，就返回默认的

        return defaultPreviewResolution;
    }

    /**
     * 找出最适合的牌照图片分辨率
     *
     * @return
     */
    private Camera.Size findBestPictureResolution() {
        Camera.Parameters cameraParameters = cameraInst.getParameters();
        List<Camera.Size> supportedPicResolutions = cameraParameters.getSupportedPictureSizes(); // 至少会返回一个值

        StringBuilder picResolutionSb = new StringBuilder();
        for (Camera.Size supportedPicResolution : supportedPicResolutions) {
            picResolutionSb.append(supportedPicResolution.width).append('x')
                    .append(supportedPicResolution.height).append(" ");
        }
        Log.d(TAG, "Supported picture resolutions: " + picResolutionSb);

        Camera.Size defaultPictureResolution = cameraParameters.getPictureSize();
        Log.d(TAG, "default picture resolution " + defaultPictureResolution.width + "x"
                + defaultPictureResolution.height);

        // 排序
        List<Camera.Size> sortedSupportedPicResolutions = new ArrayList<Camera.Size>(
                supportedPicResolutions);
        Collections.sort(sortedSupportedPicResolutions, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (bPixels < aPixels) {
                    return -1;
                }
                if (bPixels > aPixels) {
                    return 1;
                }
                return 0;
            }
        });

        // 移除不符合条件的分辨率
        double screenAspectRatio = (double) AppContext.getApp().getScreenWidth()
                / (double) AppContext.getApp().getScreenHeight();
        Iterator<Camera.Size> it = sortedSupportedPicResolutions.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewResolution = it.next();
            int width = supportedPreviewResolution.width;
            int height = supportedPreviewResolution.height;

            // 在camera分辨率与屏幕分辨率宽高比不相等的情况下，找出差距最小的一组分辨率
            // 由于camera的分辨率是width>height，我们设置的portrait模式中，width<height
            // 因此这里要先交换然后在比较宽高比
            boolean isCandidatePortrait = width > height;
            int maybeFlippedWidth = isCandidatePortrait ? height : width;
            int maybeFlippedHeight = isCandidatePortrait ? width : height;
            double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove();
                continue;
            }
        }

        // 如果没有找到合适的，并且还有候选的像素，对于照片，则取其中最大比例的，而不是选择与屏幕分辨率相同的
        if (!sortedSupportedPicResolutions.isEmpty()) {
            return sortedSupportedPicResolutions.get(0);
        }

        // 没有找到合适的，就返回默认的
        return defaultPictureResolution;
    }


    /**
     * 控制图像的正确显示方向
     */
    private void setDispaly(Camera.Parameters parameters, Camera camera) {
        if (Build.VERSION.SDK_INT >= 8) {
            setDisplayOrientation(camera, 90);
        } else {
            parameters.setRotation(90);
        }
    }

    /**
     * 实现的图像的正确显示
     */
    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation",
                    new Class[]{int.class});
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, new Object[]{i});
            }
        } catch (Exception e) {
            Log.e("Came_e", "图像出错");
        }
    }


    /**
     * 将拍下来的照片存放在SD卡中
     *
     * @param data
     * @throws IOException
     */
    public String saveToSDCard(byte[] data) throws IOException {
        Bitmap croppedImage;

        //获得图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        PHOTO_SIZE = options.outHeight > options.outWidth ? options.outWidth : options.outHeight;
        int height = options.outHeight > options.outWidth ? options.outHeight : options.outWidth;
        options.inJustDecodeBounds = false;
        Rect r;
        if (mCurrentCameraId == 1) {//前置摄像头
            r = new Rect(height - PHOTO_SIZE, 0, height, PHOTO_SIZE);
        } else {//后置摄像头
            r = new Rect(0, 0, PHOTO_SIZE, PHOTO_SIZE);
        }
        try {
            croppedImage = decodeRegionCrop(data, r);
        } catch (Exception e) {
            return null;
        }
        // 图的路径是放在DCIM里
        String imagePath = null;
        imagePath = ImageUtils.saveToFile(FileUtil.getInst().getSystemPhotoPath() + "/Camera", true,
                croppedImage);

//        } else {
//            imagePath = ImageUtils.saveToFile(FileUtil.getInst().getPhotoTempPath(), true,
//                    croppedImage);
//        }
        croppedImage.recycle();
        return imagePath;
    }

    /**
     * 截取屏幕宽度的正方形图
     */
    private Bitmap decodeRegionCrop(byte[] data, Rect rect) {

        InputStream is = null;
        System.gc();
        Bitmap croppedImage = null;
        try {
            is = new ByteArrayInputStream(data);
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);

            try {
                croppedImage = decoder.decodeRegion(rect, new BitmapFactory.Options());
            } catch (IllegalArgumentException e) {
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeStream(is);
        }
        Matrix m = new Matrix();
        m.setRotate(90, PHOTO_SIZE / 2, PHOTO_SIZE / 2);
        if (mCurrentCameraId == 1) {
            m.postScale(1, -1);
        }
        Bitmap rotatedImage = Bitmap.createBitmap(croppedImage, 0, 0, PHOTO_SIZE, PHOTO_SIZE, m, true);
        if (rotatedImage != croppedImage)
            croppedImage.recycle();
        return rotatedImage;
    }

    /**
     * 闪光灯开关   开->自动->关
     *
     * @param mCamera
     */
    private void turnLight(Camera mCamera) {
        if (mCamera == null || mCamera.getParameters() == null
                || mCamera.getParameters().getSupportedFlashModes() == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        String flashMode = mCamera.getParameters().getFlashMode();
        List<String> supportedModes = mCamera.getParameters().getSupportedFlashModes();
        if (Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)
                && supportedModes.contains(Camera.Parameters.FLASH_MODE_ON)) {//关闭状态
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            mCamera.setParameters(parameters);
            flashBtn.setImageResource(R.drawable.flash_on_black);
        } else if (Camera.Parameters.FLASH_MODE_ON.equals(flashMode)) {//开启状态
            if (supportedModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                flashBtn.setImageResource(R.drawable.led_black);
                mCamera.setParameters(parameters);
            } else if (supportedModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                flashBtn.setImageResource(R.drawable.flash_off_black);
                mCamera.setParameters(parameters);
            }
        } else if (Camera.Parameters.FLASH_MODE_AUTO.equals(flashMode)
                && supportedModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
            flashBtn.setImageResource(R.drawable.flash_off_black);
        }
    }


    /**
     * 切换前后置摄像头
     */
    private void switchCamera() {
        mCurrentCameraId = (mCurrentCameraId + 1) % mCameraHelper.getNumberOfCameras();
        releaseCamera();
        Log.d("DDDD", "DDDD----mCurrentCameraId" + mCurrentCameraId);
        setUpCamera(mCurrentCameraId);
    }

    /**
     * 关闭相机
     */
    private void releaseCamera() {
        if (cameraInst != null) {
            cameraInst.setPreviewCallback(null);
            cameraInst.release();
            cameraInst = null;
        }
        adapterSize = null;
        previewSize = null;
    }

    /**
     * 打开相机
     *
     * @param mCurrentCameraId2
     */
    private void setUpCamera(int mCurrentCameraId2) {
        cameraInst = getCameraInstance(mCurrentCameraId2);
        if (cameraInst != null) {
            try {
                cameraInst.setPreviewDisplay(surfaceView.getHolder());
                initCamera();
                cameraInst.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            toast("切换失败，请重试！", Toast.LENGTH_LONG);
        }
    }

    private Camera getCameraInstance(final int id) {
        Camera c = null;
        try {
            c = mCameraHelper.openCamera(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }


    Dialog mDialog;
    /**
     * 首次完成任务进入发图页面时需要弹出引导图
     */
    private void showGuide() {
        if (mDialog == null) {
            mDialog = new Dialog(this, R.style.Dialog_FullScreen);
        }
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_obtain_moin_bean, null);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = 0.6f;
        lp.width = getWindowManager().getDefaultDisplay().getWidth();
        lp.height = getWindowManager().getDefaultDisplay().getHeight();
        window.setAttributes(lp);
        view.setOnClickListener(v -> {
            mDialog.dismiss();
        });
        if (!mDialog.isShowing()) {
            mDialog.show();
        }

        MissionPreference.getInstance().setCosplayuide(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.CAMERA_ACTIVITY);
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.CAMERA_ACTIVITY);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (oriBitmap != null && !oriBitmap.isRecycled()) {
            oriBitmap.recycle();
        }
    }

    /**
     * 监听返回--是否退出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 退出大咖秀编辑
            StickPreference.getInstance().deleteJoinTopicInfo();

            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
