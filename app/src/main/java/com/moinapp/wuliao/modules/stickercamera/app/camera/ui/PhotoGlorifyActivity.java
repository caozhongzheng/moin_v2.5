package com.moinapp.wuliao.modules.stickercamera.app.camera.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.BaseFile;
import com.moinapp.wuliao.bean.ErrorCode;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener2;
import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.sticker.model.StickerProject;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraBaseActivity;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.modules.stickercamera.app.camera.EffectService;
import com.moinapp.wuliao.modules.stickercamera.app.camera.adapter.FilterAdapter;
import com.moinapp.wuliao.modules.stickercamera.app.camera.effect.FilterEffect;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.GPUImageFilterTools;
import com.moinapp.wuliao.modules.stickercamera.app.model.StickerDecode;
import com.moinapp.wuliao.modules.stickercamera.base.util.DialogHelper;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.FileUtil;
import com.moinapp.wuliao.util.HttpUtil;
import com.moinapp.wuliao.util.ImageUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.TimeUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.sephiroth.android.library.widget.HListView;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * 图片美化界面, 目前主要是滤镜处理
 */
public class PhotoGlorifyActivity extends CameraBaseActivity {

    private static final ILogger MyLog = LoggerFactory.getLogger(PhotoGlorifyActivity.class.getSimpleName());
    //滤镜图片
    @InjectView(R.id.title_layout)
    CommonTitleBar mTitleBar;
    @InjectView(R.id.gpuimage)
    GPUImageView mGPUImageView;

    //滤镜区域
    @InjectView(R.id.list_filters)
    HListView filterBar;

    //调整区域
    @InjectView(R.id.filter_adjust)
    LinearLayout adjustBar;

    //亮度调整seekBar
    @InjectView(R.id.seek_brightness)
    SeekBar brSeekBar;
    @InjectView(R.id.textBrLayout)
    TextMoveLayout textBrLayout;

    //对比度调整seekBar
    @InjectView(R.id.seek_contrast)
    SeekBar ctSeekBar;
    @InjectView(R.id.textCtLayout)
    TextMoveLayout textCtLayout;

    //饱和度调整seekBar
    @InjectView(R.id.seek_saturation)
    SeekBar saSeekBar;
    @InjectView(R.id.textSaLayout)
    TextMoveLayout textSaLayout;

    @InjectView(R.id.ll_bottom_filter)
    // 底部滤镜按钮area
    LinearLayout filterBtnArea;
    @InjectView(R.id.iv_bottom_filter)
    ImageView filterImageView;
    @InjectView(R.id.tv_bottom_filter)
    TextView filterTextView;

    @InjectView(R.id.ll_bottom_adjust)
    // 底部调整按钮area
    LinearLayout adjustBtnArea;
    @InjectView(R.id.iv_bottom_adjust)
    ImageView adjustImageView;
    @InjectView(R.id.tv_bottom_adjust)
    TextView adjustTextView;

    //当前图片
    private Bitmap currentBitmap;
    //用于预览的小图片 300*300
    private Bitmap smallImageBackgroud;

    //滤镜值
    private int filterI = -1;
    private int SW = -1;
    private Uri bgUri;
    private StickerProject stickerProject = new StickerProject();
    private int writeAuth;
    private String ucid;
    View.OnClickListener finishCB, tryAgainCB;
    private String from;

    //当前对比度值
    private float currentContrast = 1.0f;
    //当前亮度值
    private float currentBrightness = 0.0f;
    //当前饱和度值
    private float currentSaturation = 1.0f;
    private TextView textBr, textCt, textSa;

    private GPUImageFilter mFilter;
    final List<FilterEffect> filters = EffectService.getInst().getLocalFilters();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_process);
        ButterKnife.inject(this);
        initView();
        initEvent();

        bgUri = getIntent().getData();
        MyLog.i("bgUri=" + bgUri);

        ucid = getIntent().getStringExtra(PhotoProcessActivity.KEY_PARENT_UCID);
        writeAuth = getIntent().getIntExtra(PhotoProcessActivity.KEY_PARENT_WRITE_AUTH, 4);
        MyLog.i("ucid =" + ucid);
        if (!StringUtil.isNullOrEmpty(ucid)) {
            getOriginalStickProject(ucid);
        } else if (bgUri != null) {
            initBg();
            initStickProject();
        }
    }

    private void initBg() {
        if (bgUri != null) {
            ImageUtils.asyncLoadImage(this, bgUri, new ImageUtils.LoadImageCallback() {
                @Override
                public void callback(Bitmap result) {
                    smallImageBackgroud = currentBitmap = result;
                    mGPUImageView.setImage(currentBitmap);
                    MyLog.i("call refreshStickArea from initBg [" + bgUri.getScheme() + "]");
                    onClickFilterBar();
                    initStickProject();
                }
            });
        }
    }

    private void initView() {
        SW = AppContext.getApp().getScreenWidth();

        try {
            //初始化滤镜图片
            ViewGroup.LayoutParams layout = mGPUImageView.getLayoutParams();
            layout.height = SW;
        } catch (Exception e) {
            MyLog.e(e);
        }
        initFilterAdjust();
    }

    private void initEvent() {
        if (getIntent() != null) {
            from = getIntent().getStringExtra(DiscoveryConstants.FROM);
            MyLog.i("initIntent from = " + from);
        }

        tryAgainCB = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOriginalStickProject(ucid);
            }
        };

        mTitleBar.setLeftBtnOnclickListener(v -> {
            finish();
        });

        mTitleBar.setRightBtnOnclickListener(v -> {
            savePicture();
        });

        filterBtnArea.setOnClickListener(v -> {
            // 点击滤镜按钮
            onClickFilterBar();
        });
        adjustBtnArea.setOnClickListener(v -> {
            // 点击调整按钮
            onClickdjustrBar();
        });

        // 滑动控件的实际刻度为0-100,代码中setProgress用的是实际刻度
        // 只是显示的刻度为ui要求显示-50到+50,所以textview显示进度为实际progress-50
        brSeekBar.setOnSeekBarChangeListener(new OnBrSeekBarChangeListenerImp());
        brSeekBar.setProgress(50);
        ctSeekBar.setOnSeekBarChangeListener(new OnCtSeekBarChangeListenerImp());
        ctSeekBar.setProgress(50);
        saSeekBar.setOnSeekBarChangeListener(new OnSaSeekBarChangeListenerImp());
        saSeekBar.setProgress(50);
    }

    //初始化调整区域内的显示滑动刻度的控件
    private void initFilterAdjust(){
        textBr = new TextView(this);
        textCt = new TextView(this);
        textSa = new TextView(this);
        List<TextView> listTextView = new ArrayList<TextView>();
        listTextView.add(textBr);
        listTextView.add(textCt);
        listTextView.add(textSa);

        for (TextView text : listTextView) {
            text.setTextColor(Color.rgb(102, 102, 102));
            text.setTextSize(13);
            text.setGravity(Gravity.CENTER_VERTICAL);
            text.layout(20, 0, SW, 60);
        }
        ViewGroup.LayoutParams layoutParam = new ViewGroup.LayoutParams(SW, 60);
        textBrLayout.addView(textBr, layoutParam);
        textCtLayout.addView(textCt, layoutParam);
        textSaLayout.addView(textSa, layoutParam);
    }

    private void updateAdjust(TextView textView, int progress) {
        //显示进度为实际progress-50
        String s = String.valueOf(progress - 50);
        //滑块大小为16dp, seekbar在xml中leftPadding设置为了8dp, seekbar总共长度273dp, 可滑动的线长度为
        // 273-16-8
        int paddingLeft = (int)(progress * (TDevice.dpToPixel(273f - 16 - 8)) / 100);
        if (s.length() != 1) {
            textView.layout(paddingLeft, 0, SW, 60);
        } else {
            textView.layout(paddingLeft + (int) TDevice.dpToPixel(3f), 0, SW, 60);
        }
        textView.setText(s);
    }

    private void onClickFilterBar() {
        MyLog.i("点击滤镜按钮");
        filterBar.setVisibility(View.VISIBLE);
        adjustBar.setVisibility(View.GONE);
        setToolBtnBg(0, true);
        initFilterToolBar();
    }

    private void onClickdjustrBar() {
        MyLog.i("点击调整按钮");
        filterBar.setVisibility(View.GONE);
        adjustBar.setVisibility(View.VISIBLE);
        setToolBtnBg(1, true);
        initFilterToolBar();
    }

    /**
     * 设置底部按钮的背景色
     * */
    private void setToolBtnBg(int pos, boolean selected) {
        filterBtnArea.setBackgroundColor(getResources().getColor(com.keyboard.view.R.color.white));
        filterImageView.setImageResource(R.drawable.filter_gray);
        filterTextView.setTextColor(getResources().getColor(R.color.gray));
        adjustBtnArea.setBackgroundColor(getResources().getColor(com.keyboard.view.R.color.white));
        adjustImageView.setImageResource(R.drawable.unselect_adjustment_gray);
        adjustTextView.setTextColor(getResources().getColor(R.color.gray));
        switch (pos) {
            case 0:
                if (selected) {
                    filterBtnArea.setBackgroundColor(getResources().getColor(com.keyboard.view.R.color.list_item_background_pressed));
                    filterImageView.setImageResource(R.drawable.filter_black);
                    filterTextView.setTextColor(getResources().getColor(R.color.common_title_grey));
                }
                break;
            case 1:
                if (selected) {
                    adjustBtnArea.setBackgroundColor(getResources().getColor(com.keyboard.view.R.color.list_item_background_pressed));
                    adjustImageView.setImageResource(R.drawable.be_selected_adjustment_black);
                    adjustTextView.setTextColor(getResources().getColor(R.color.common_title_grey));
                }
                break;
        }
    }

    /**
     * 保存图片
     */
    private void savePicture() {
        SavePicToFileTask saveTask = new SavePicToFileTask();
        try {
            saveTask.execute(mGPUImageView.capture());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private FilterAdapter filterAdapter;
    /**
     * 初始化滤镜
     */
    private void initFilterToolBar() {
        if (filterAdapter == null) {
            filterAdapter = new FilterAdapter(PhotoGlorifyActivity.this, filters, filterI, smallImageBackgroud);
            filterBar.setAdapter(filterAdapter);
            filterBar.setOnItemClickListener(new it.sephiroth.android.library.widget.AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    if (filterAdapter.getSelectFilter() != arg2) {
                        filterAdapter.setSelectFilter(arg2);
                        mFilter = GPUImageFilterTools.createFilterForType(
                                PhotoGlorifyActivity.this, filters.get(arg2).getType());
                        filterI = arg2;
                        updateFilters();
                    }
                }
            });
        }
    }

    // 刷新滤镜重新渲染
    private void updateFilters() {
        List<GPUImageFilter> filters = new LinkedList<GPUImageFilter>();
        if (mFilter != null) {
            filters.add(mFilter);
        }
        filters.add(new GPUImageContrastFilter(currentContrast));
        filters.add(new GPUImageBrightnessFilter(currentBrightness));
        filters.add(new GPUImageSaturationFilter(currentSaturation));
        mGPUImageView.setFilter(new GPUImageFilterGroup(filters));
    }

    // 亮度进度条拖动的listener
    private class OnBrSeekBarChangeListenerImp implements
            SeekBar.OnSeekBarChangeListener {

        // 触发操作，拖动
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            updateAdjust(textBr, progress);
            currentBrightness = GPUImageFilterTools.convertBrightness(progress);
            updateFilters();
        }

        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    // 对比度进度条拖动的listener
    private class OnCtSeekBarChangeListenerImp implements
            SeekBar.OnSeekBarChangeListener {

        // 触发操作，拖动
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            updateAdjust(textCt, progress);
            currentContrast = GPUImageFilterTools.convertContrast(progress);
            updateFilters();
        }

        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    // 饱和度进度条拖动的listener
    private class OnSaSeekBarChangeListenerImp implements
            SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            updateAdjust(textSa, progress);
            currentSaturation = GPUImageFilterTools.convertSaturation(progress);
            updateFilters();
        }

        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    //转改时下载工程文件
    private void getOriginalStickProject(String ucid) {
        MyLog.i("getOriginalStickProject ucid=" + ucid);
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.hasno_network);
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgressDialog(getString(R.string.cosplay_loading));
            }
        });
        DiscoveryManager.getInstance().updateCosplay(ucid, new IListener2() {

            @Override
            public void onSuccess(Object obj) {
                if (obj != null) {
                    BaseFile file = (BaseFile) obj;
                    MyLog.i("获取工程文件 OK:" + file.toString());
                    if (!StringUtil.isNullOrEmpty(file.getUrl())) {
                        downloadOriginalStickProject(file.getUrl());
                    }
                }
            }

            @Override
            public void onErr(Object obj) {
                MyLog.i("获取工程文件 NG" + obj);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgressDialog();
                        int error = (int) obj;
                        if (error == ErrorCode.ERROR_COSPLAY_DELETED) {
                            //-20表示转改时图已经被删除
                            AppContext.showToast(getString(R.string.cosplay_delete_alreay));
                            finish();
                        } else {
                            DialogHelper dialogHelper = new DialogHelper(PhotoGlorifyActivity.this);
                            dialogHelper.alert4M(null, getString(R.string.cosplay_download_failed),
                                    getString(R.string.try_again), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            getOriginalStickProject(ucid);
                                            dialogHelper.dialogDismiss();
                                        }
                                    }, getString(R.string.cancle), finishCB, true);
                        }
                    }
                });
            }

            @Override
            public void onNoNetwork() {

            }
        });
    }

    private void initStickProject() {
        if (stickerProject != null) {
            filterI = stickerProject.getFilter();
            //如果工程文件里带有滤镜参数,转化未进度并显示
            currentBrightness = ((float)stickerProject.getBrightness())/(100f);
            brSeekBar.setProgress(GPUImageFilterTools.convertBrightnessProgress(currentBrightness));
            if (stickerProject.getContrast() >= 0) {
                currentContrast = ((float)stickerProject.getContrast())/(100f);
                ctSeekBar.setProgress(GPUImageFilterTools.convertContrastProgress(currentContrast));
            }
            if (stickerProject.getSaturation() >= 0) {
                currentSaturation = ((float)stickerProject.getSaturation())/(100f);
                saSeekBar.setProgress(GPUImageFilterTools.convertSaturationProgress(currentSaturation));
            }
            MyLog.i("下载的工程文件中 stickerProject from Intent filter==" + filterI
                    + ", brightness=" + currentBrightness
                    + ", contrast = " + currentContrast
                    + ", saturation = " + currentSaturation);
            final List<FilterEffect> filters = EffectService.getInst().getLocalFilters();
            if (filterI >= 0 && filterI < filters.size()) {
                if (filterAdapter != null) {
                    MyLog.i("转改找到滤镜: filter=" + filterI + ",setSelection..");
                    filterAdapter.setSelectFilter(filterI);
                    filterBar.setSelection(filterI);
                }
                mFilter = GPUImageFilterTools.createFilterForType(
                        PhotoGlorifyActivity.this, filters.get(filterI).getType());
                updateFilters();
            }
        }
    }

    DownloadTask mDownloadTask;
    private void cancelDownloadTask() {
        if (mDownloadTask != null) {
            mDownloadTask.cancel(true);
            mDownloadTask = null;
        }
    }

    private void downloadOriginalStickProject(String url) {
        cancelDownloadTask();
        mDownloadTask = new DownloadTask();
        mDownloadTask.setOnCallback(new OnCallback() {
            @Override
            public void onSuccess(Object object) {
                MyLog.i("下载工程文件 OK:" + object.toString());
                StickerDecode stickerDecode = (StickerDecode) object;

                bgUri = Uri.fromFile(new File(stickerDecode.getImageFile()));
                stickerProject = stickerDecode.getSticker();
                initBg();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgressDialog();
                    }
                });

            }

            @Override
            public void onFailed(Object object) {
                AppContext.showToast(R.string.load_error);
                finish();
            }
        });
        mDownloadTask.execute(url);
    }

    private class DownloadTask extends AsyncTask<String, Void, StickerDecode> {
        String url;

        @Override
        protected StickerDecode doInBackground(String... params) {
            StickerDecode decode = null;
            try {
                url = params[0];
                MyLog.i("下载工程文件 url:" + url);

                boolean result = HttpUtil.download(url, BitmapUtil.getOriginalCosplayFile(url));
                if (result == true) {
                    decode = CameraManager.getInst().decodeProjectFile(BitmapUtil.getOriginalCosplayFile(url));
                    MyLog.i("下载工程文件 result true: decode = " + decode);

                } else
                    MyLog.i("下载工程文件 result false:");

            } catch (Exception e) {
                MyLog.e(e);
                toast(getString(R.string.cosplay_try), Toast.LENGTH_LONG);
            }
            return decode;
        }

        @Override
        protected void onPostExecute(StickerDecode decode) {
            super.onPostExecute(decode);
            if (decode == null) {
                MyLog.i("下载工程文件 decode==null:");
                if (callback != null) {
                    callback.onFailed(null);
                }
                return;
            } else {
                MyLog.i("下载工程文件 decode != null:");
                if (callback != null) {
                    callback.onSuccess(decode);
                }
            }
        }

        OnCallback callback;

        public void setOnCallback(OnCallback callback) {
            this.callback = callback;
        }
    }

    interface OnCallback {
        void onSuccess(Object object);

        void onFailed(Object object);
    }

    private class SavePicToFileTask extends AsyncTask<Bitmap, Void, String> {
        Bitmap bitmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("图片处理中...");
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            String fileName = null;
            try {
                bitmap = params[0];
                String picName = TimeUtils.dtFormat(new Date(), "yyyyMMddHHmmss");//无扩展名
                fileName = ImageUtils.saveToFile(FileUtil.getInst().getPhotoSavedPath() + "/" + picName + ".jpg", false, bitmap, false);
            } catch (Exception e) {
                e.printStackTrace();
                toast("图片处理错误，请退出相机并重试", Toast.LENGTH_LONG);
            }
            return fileName;
        }

        @Override
        protected void onPostExecute(String fileName) {
            super.onPostExecute(fileName);
            dismissProgressDialog();
            //跳转大咖秀编辑页面
            Intent intent = new Intent(PhotoGlorifyActivity.this, PhotoProcessActivity.class);
            intent.putExtra(DiscoveryConstants.FROM, StringUtil.nullToEmpty(from));
            intent.setData(bgUri);
            MyLog.i("设置bguri " + bgUri);
            if (stickerProject == null) {
                stickerProject = new StickerProject();
            }
            stickerProject.setFilter(filterI);
            stickerProject.setBrightness((int)(currentBrightness*100));
            stickerProject.setContrast((int)(currentContrast*100));
            stickerProject.setSaturation((int)(currentSaturation*100));
            MyLog.i("设置 filterI=" + filterI
                            + ", currentBrightness=" + currentBrightness
                            + ", currentContrast=" + currentContrast
                            + ", currentSaturation=" + currentSaturation
            );

            intent.putExtra(PhotoProcessActivity.KEY_PARENT_GLORIFY, fileName);
            intent.putExtra(PhotoProcessActivity.KEY_PARENT_STICKPROJECT, stickerProject);
            intent.putExtra(PhotoProcessActivity.KEY_PARENT_UCID, ucid);
            intent.putExtra(PhotoProcessActivity.KEY_PARENT_WRITE_AUTH, writeAuth);

            startActivity(intent);
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.PHOTO_GLORIFY_ACTIVITY); //统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.PHOTO_GLORIFY_ACTIVITY);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (currentBitmap != null) {
            currentBitmap.recycle();
        }
    }
}
