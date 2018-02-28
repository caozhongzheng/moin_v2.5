package com.moinapp.wuliao.modules.post;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.bean.BaseImage;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.preference.CommonsPreference;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.TagInfo;
import com.moinapp.wuliao.modules.sticker.StickPreference;
import com.moinapp.wuliao.modules.sticker.StickerManager;
import com.moinapp.wuliao.modules.sticker.model.StickerAudioInfo;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.MyPopWindow;
import com.moinapp.wuliao.ui.imageselect.SelectMultiPhotoActivity;
import com.moinapp.wuliao.util.AppTools;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.FileUtil;
import com.moinapp.wuliao.util.MediaRecorderUtil;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.Tools;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.AudioPlayLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.InjectView;

/**
 * 发帖
 * Created by moying on 15/6/10.
 */
public class PostActivity extends BaseActivity {
    private ILogger MyLog = LoggerFactory.getLogger(PostActivity.class.getSimpleName());

    @InjectView(R.id.title_layout)
    CommonTitleBar mTitleBar;
    @InjectView(R.id.content)
    EditText mContent;
    @InjectView(R.id.tv_content_length)
    TextView mContentLength;
    @InjectView(R.id.add_layout)
    LinearLayout mAddLayout;
    @InjectView(R.id.addPhoto)
    ImageView mAddPic;
    @InjectView(R.id.addVoice)
    ImageView mAddVoice;

    @InjectView(R.id.gv_photo)
    GridView mGv_photo;
    private MyPicAdapter mAdapter;

    @InjectView(R.id.fl_voice)
    FrameLayout mVoiceLayout;
    @InjectView(R.id.audio_play)
    AudioPlayLayout mAudio;
    @InjectView(R.id.iv_voice_del)
    ImageView mDelVoice;

    TextView tv_camera;
    TextView tv_gallery;
    TextView tv_cancel;
    TextView tv_voice_title;
    TextView tv_voice_ing;
    ImageView iv_voice_record;

    private MyPopWindow selectphoto_popupWindow;
    private MyPopWindow record_popupWindow;

    public static final int NO_SDCARD = -1;
    public static final int CAMERA_PHOTO_WITH_DATA = 1;// 拍照
    public static final int GALLERY_PHOTO_WITH_DATA = 2;// 图库

    private ImageLoader imageLoader;

    private ArrayList<String> mPhotoList = new ArrayList<>();
    private HashMap<String, String> map = new HashMap<>();//图片上传结果用
    private HashMap<String, String> failed_map = new HashMap<>();//图片上传结果用
    private MediaRecorderUtil mMediaRecorderUtil = new MediaRecorderUtil();
    private StickerAudioInfo mAudioInfo;

    private String topicID;
    private String topicName;

    private final int NULL = 0, ERROR = -1, CORRECT = 1;
    private int title_state = NULL;
    private boolean IS_POSTING = false;

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.POST_ACTIVITY); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.POST_ACTIVITY);
        MobclickAgent.onPause(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_post_layout;
    }

    @Override
    public void initView() {

        mTitleBar.setLeftBtnOnclickListener(v -> finish());
        mTitleBar.setRightBtnOnclickListener(v -> rightBtnHandle());

        mContent.addTextChangedListener(mTextWatcher);

        initPhotoWindow();
        initVoiceWindow();

        addPicPlus();

        mAddVoice.setOnClickListener(addVoiceListener);
        mDelVoice.setOnClickListener(view -> {
            recordLength = INVALID;
            refreshVoiceView();
        });
        mAddPic.setOnClickListener(addPicListener);

        mAdapter = new MyPicAdapter(getApplicationContext(), mPhotoList, R.layout.post_photo_item);
        mAdapter.setPicCallback(new MyPicAdapter.PicCallback() {
            @Override
            public void onAdd() {
                selectphoto_popupWindow.showButtom();
            }

            @Override
            public void onClick(int position) {
                UIHelper.showImagePreview(PostActivity.this, position, getPhotos(false).toArray(new String[1]));
            }

            @Override
            public void onDelete(int position) {
                Message message = Message.obtain(handler, 0x800);
                message.obj = position;
                message.sendToTarget();
            }
        });

        mGv_photo.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
        imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited())
            imageLoader.init(BitmapUtil.getImageLoaderConfiguration());

        if (getIntent() != null) {
            topicID = getIntent().getStringExtra(Constants.BUNDLE_KEY_ID);
            topicName = getIntent().getStringExtra(Constants.BUNDLE_KEY_TAG);
            MyLog.i("topicID= " + topicID + ", topicName= " + topicName);
            if (StringUtil.isNullOrEmpty(topicID) && StringUtil.isNullOrEmpty(topicName)) {
//                topicID = "573d614e0cf26a98105061e4";
//                topicName = "我和小s同星座";
                finish();
            }
        }

        MyLog.i("topicID= " + topicID + ", topicName= " + topicName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case NO_SDCARD:
                AppContext.showToast(R.string.label_no_sdcard);
                break;

            case CAMERA_PHOTO_WITH_DATA:
                switch (resultCode) {
                    case Activity.RESULT_OK://照相完成点击确定
                        if (!AppTools.existsSDCARD()) {
                            MyLog.v("SD card is not avaiable/writeable right now.");
                            return;
                        }

                        String fileName = capturePath;
                        if (!StringUtil.isNullOrEmpty(fileName)) {
                            if (mPhotoList == null) {
                                mPhotoList = new ArrayList<>();
                            }
                            if (mPhotoList.size() > 0 && PostConstants.ADD_PLUS.equals(mPhotoList.get(mPhotoList.size() - 1))) {
                                mPhotoList.remove(mPhotoList.size() - 1);
                            }
                            mPhotoList.add(fileName);
                            addPicPlus();

                            for (int i = 0; i < mPhotoList.size(); i++) {
                                MyLog.i(i + " 拍照返回 : " + mPhotoList.get(i));
                            }
                            mAdapter.setDatas(getPhotos(true));
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
            case GALLERY_PHOTO_WITH_DATA:
                if (data != null) {
                    boolean isChange = data.getBooleanExtra(SelectMultiPhotoActivity.KEY_IS_CHANGED, false);
                    if (isChange) {
                        mPhotoList = data.getStringArrayListExtra(SelectMultiPhotoActivity.KEY_SELECTED);
                        addPicPlus();
                        for (int i = 0; i < mPhotoList.size(); i++) {
                            MyLog.i((i + 1) + " :选择照片 " + mPhotoList.get(i));
                        }
                        mAdapter.setDatas(getPhotos(true));
                    }
                }
                break;
        }
    }

    /**
     * 处理加号按钮,以及图片布局的显示
     */
    private void addPicPlus() {
        if (mPhotoList == null) {
            mPhotoList = new ArrayList<>();
        }
        if (mPhotoList.size() < PostConstants.POST_PIC_MAX_SIZE) {
            mPhotoList.add(PostConstants.ADD_PLUS);
        }

        if (getPhotos(false).isEmpty()) {
            mAddLayout.setVisibility(View.VISIBLE);
            mGv_photo.setVisibility(View.GONE);
        } else {
            mGv_photo.setVisibility(View.VISIBLE);
            mAddLayout.setVisibility(View.GONE);
        }
    }

    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() == 0) {
                title_state = NULL;
            } else if (StringUtil.isNullOrEmpty(s.toString().replaceAll(" ", " ").replaceAll(" ", "").replaceAll("\n", ""))) {
                title_state = ERROR;
            } else {
                title_state = CORRECT;
            }
            mContentLength.setText(s.toString().length() + "/" + 140);
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x119:
                    IS_POSTING = false;
                    AppContext.showToast(R.string.no_network);
                    break;
                case 0x120:
                    IS_POSTING = false;
                    AppContext.showToast(R.string.connection_failed);
                    break;
                // 选择删除图片
                case 0x800:
                    // TODO 会出现下标越界
                    int pos = (int) msg.obj;
                    int count = getPhotos(false).size();
                    if (pos >= 0 && pos < count) {
                        deleteTmpCaptures(mPhotoList.get(pos));
                        map.remove(mPhotoList.get(pos));
                        mPhotoList.remove(pos);

                        // 如果是删除了最后一张图，那么要将加号加回来
                        addPicPlus();
                        mAdapter.setDatas(getPhotos(true));
                    }

                    break;
                // 图片上传结果
                case 0x55:
                    if (msg.getData() != null) {
                        String url = msg.getData().getString("url");
                        String picid = msg.getData().getString("picid");
                        if (StringUtil.isNullOrEmpty(picid)) {
                            MyLog.w("图片上传失败啦啦啦 " + url);
                            failed_map.put(url, picid);
                        } else {
                            MyLog.i("图片上传成功 " + url + ", picid=" + picid);
                            map.put(url, picid);
                        }

                        int size = map.keySet().size() + failed_map.keySet().size();

                        MyLog.i("图片上传成功个数 " + map.keySet().size() + ", 图片上传失败个数=" + failed_map.keySet().size());
                        int picSize = getPhotos(false).size();
                        if (size < picSize) {
                            MyLog.i("还没到总的个数 " + picSize);
                            return;
                        }
                        if (failed_map.keySet().size() > 0) {
                            retry();
                        } else if (map.keySet().size() == picSize) {
                            MyLog.i("图片全部全部全部全部全部全部 都上传成功");
                            ArrayList<String> picIdList = new ArrayList<>();

                            for (int i = 0; i < picSize; i++) {
                                MyLog.i((i + 1) + " : " + mPhotoList.get(i) + " / " + map.get(mPhotoList.get(i)));
                                picIdList.add(map.get(mPhotoList.get(i)));
                            }

                            // TODO 发帖接口,含敏感字的处理
                            /*
                            onNewPostSucc(int woid, String postid) {
                                MyLog.i("发帖成功 postid= " + postid);
                                handler.sendEmptyMessage(0x66);
                            */
                        }
                    }
                    break;
                // 发帖成功
                case 0x66:
                    isRetry = false;
                    AppContext.showToast(R.string.post_success);

                    for (int i = 0; i < mPhotoList.size(); i++) {
                        deleteTmpCaptures(mPhotoList.get(i));
                    }

                    // TODO 还是应该跳到帖子详情内呢?
//                    MyLog.i("跳到帖子列表去 topicID=" + topicID);
//                    Bundle bundle = new Bundle();
//                    bundle.putInt(WoPostListActivity.WO_ID, topicID);
//                    bundle.putInt("column",0);
//                    AppTools.toIntent(PostActivity.this, bundle, WoPostListActivity.class);
                    break;

                default:
                    break;
            }

        }
    };

    boolean isRetry = false;

    private void retry() {
        MyLog.i("retry ");
        if (!TDevice.hasInternet()) {
            handler.sendEmptyMessage(0x119);
            return;
        }
        if (isRetry) {
            handler.sendEmptyMessage(0x120);
            return;
        }
        isRetry = true;
        int size = failed_map.keySet().size();
        MyLog.i("retry GOGOGO  size：" + size);
        ArrayList<String> list = new ArrayList<>(size);
        for (Map.Entry<String, String> entry : failed_map.entrySet()) {
            list.add(entry.getKey());
        }
        failed_map.clear();
        ExecutorService limitedTaskExecutor = Executors.newFixedThreadPool(list.size());
        for (int i = 0; i < size; i++) {
            MyLog.i("重试  上传图片任务 " + list.get(i));
            UploadImageTask task = new UploadImageTask();
            task.executeOnExecutor(limitedTaskExecutor, list.get(i));
        }

    }

    private void deleteTmpCaptures(String path) {
        if (StringUtil.isNullOrEmpty(path))
            return;

        if (path.contains(BitmapUtil.POST_CAPTURE_PREFIX)) {
            new File(path).delete();
            MyLog.i("删除临时图片 success =" + path);
        }
    }


    /**
     * 录音UI
     */
    public void initVoiceWindow() {
        View popupWindow_view = this.getLayoutInflater().inflate(R.layout.voice_record, null);
        popupWindow_view.setPadding(0, 0, 0, CommonsPreference.getInstance().getVirtualKeyboardHeight());
        tv_voice_title = (TextView) popupWindow_view.findViewById(R.id.tv_voice_record_title);
        tv_voice_ing = (TextView) popupWindow_view.findViewById(R.id.tv_voice_recording);
        iv_voice_record = (ImageView) popupWindow_view.findViewById(R.id.iv_voice_record);

        iv_voice_record.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    doRecord();
                    break;
                case MotionEvent.ACTION_UP:
                    stopRecordAudio();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    break;
            }
            return true;
        });
        record_popupWindow = new MyPopWindow(this, popupWindow_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private long recordStartTime;
    private long INVALID = -1L;
    private long recordLength = INVALID;

    /**
     * 录音动作
     */
    private boolean doRecord() {
        setVoiceViewPressed(true);
        tv_voice_title.setVisibility(View.GONE);
        tv_voice_ing.setVisibility(View.VISIBLE);
        tv_voice_ing.setText("0:00");

        Drawable drawable[] = tv_voice_ing.getCompoundDrawables();
        if (drawable != null && drawable.length > 3) {
            AnimationDrawable left = (AnimationDrawable) drawable[0];
            AnimationDrawable right = (AnimationDrawable) drawable[2];
            if (left != null) left.start();
            if (right != null) right.start();
        }

        recordStartTime = mMediaRecorderUtil.startRecord();
        updateAudioStatus();

        return true;
    }
    /**
     * 停止录音动作
     */
    private void stopRecordAudio() {
        setVoiceViewPressed(false);
        recordLength = mMediaRecorderUtil.stopRecord();
        updateAudioStatus();
        MyLog.i("stop record, length=" + recordLength);
        if (record_popupWindow != null) {
            record_popupWindow.dismiss();
        }
        refreshVoiceView();
    }

    private void setVoiceViewPressed(boolean pressed) {
        ViewGroup.LayoutParams params = iv_voice_record.getLayoutParams();
        if (pressed) {
            iv_voice_record.setImageResource(R.drawable.holdandtalk_selected);
            params.width = params.height = (int) TDevice.dpToPixel(103);
        } else {
            iv_voice_record.setImageResource(R.drawable.holdandtalk_normal);
            params.width = params.height = (int) TDevice.dpToPixel(99);
        }
    }

    /**
     * 添加/删除录音,刷新view
     */
    private void refreshVoiceView() {
        if (recordLength < 1000 && recordLength != INVALID) {
            recordLength = INVALID;
            AppContext.getInstance().showToast(R.string.voice_record_too_short);
        }

        if (recordLength == INVALID) {
            mAddLayout.setVisibility(View.VISIBLE);
            mVoiceLayout.setVisibility(View.GONE);
            stopPlayAudio();
        } else {
            mAddLayout.setVisibility(View.GONE);
            mVoiceLayout.setVisibility(View.VISIBLE);
            mAudioInfo = new StickerAudioInfo();
            mAudioInfo.setLength(recordLength / 1000);
            mAudioInfo.setUri(mMediaRecorderUtil.getRecordPath());
            mAudio.setAudioInfo(mAudioInfo);
        }

    }

    private void stopPlayAudio() {
        if (mAudio != null && mAudio.getVisibility() == View.VISIBLE) {
            mAudio.stopPlayAudio();
        }
    }

    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateAudioStatus();
        }
    };

    /**
     * 更新当前录音的音频长度
     */
    private void updateAudioStatus() {
        if (mMediaRecorderUtil.isRecording()) {
            long curr = System.currentTimeMillis() - recordStartTime;
            curr /= 1000;
            if (curr >= 60) {
                AppContext.getInstance().showToast(R.string.voice_record_too_long);
                stopRecordAudio();
            }
            tv_voice_ing.setText(getAudioLength());
            handler.postDelayed(mUpdateMicStatusTimer, 1000);
        } else {
            MyLog.i("you record length= " + (System.currentTimeMillis() - recordStartTime));
        }
    }

    /**
     * 获取音频长度
     */
    private String getAudioLength() {
        if (recordStartTime == 0) {
            return "0:00";
        }
        long curr = System.currentTimeMillis() - recordStartTime;
        curr /= 1000;
        if (curr >= 60) {
            int min = (int) (curr / 60);
            int sec = (int) (curr % 60);
            if (sec < 10) {
                return min + ":0" + sec;
            } else {
                return min + ":" + sec;
            }
        } else if (curr < 10) {
            return "0:0" + curr;
        }
        return "0:" + curr;
    }

    /**
     * 选择图片
     */
    public void initPhotoWindow() {
        View popupWindow_view = this.getLayoutInflater().inflate(R.layout.alter_avatar, null);
        popupWindow_view.setPadding(0, 0, 0, CommonsPreference.getInstance().getVirtualKeyboardHeight());
//        ((TextView) popupWindow_view.findViewById(R.id.title)).setText(R.string.select_photo);
//        popupWindow_view.findViewById(R.id.emoji_ly).setVisibility(View.GONE);
        tv_camera = (TextView) popupWindow_view.findViewById(R.id.alter_avatar_camera);
        tv_gallery = (TextView) popupWindow_view.findViewById(R.id.alter_avatar_album);
        tv_cancel = (TextView) popupWindow_view.findViewById(R.id.alter_avatar_cancel);
        tv_camera.setOnClickListener(this);
        tv_gallery.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        selectphoto_popupWindow = new MyPopWindow(this, popupWindow_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // photo
            case R.id.alter_avatar_camera:
                selectphoto_popupWindow.dismiss();
                doCamera();
                break;
            case R.id.alter_avatar_album:
                selectphoto_popupWindow.dismiss();
                selectPhotos();
                break;
            case R.id.alter_avatar_cancel:
                selectphoto_popupWindow.dismiss();
                break;
            // tag
            default:
                break;
        }
    }

    /**
     * 选择图片
     */
    private void selectPhotos() {
        Intent intent = new Intent(PostActivity.this, SelectMultiPhotoActivity.class);
        intent.putStringArrayListExtra(SelectMultiPhotoActivity.KEY_SELECTED, getPhotos(false));
        startActivityForResult(intent, GALLERY_PHOTO_WITH_DATA);
    }

    /**
     * 获取当前图片  传递给帖子详情activity用
     */
    private ArrayList<BaseImage> getPhotos() {
        if (mPhotoList == null) {
            return new ArrayList<>();
        }

        ArrayList<BaseImage> tmp = null;
        for (String s : mPhotoList) {
            if (PostConstants.ADD_PLUS.equals(s)) {
                continue;
            }
            BaseImage image = new  BaseImage();
            image.setUri(s);
            if (tmp == null) {
                tmp = new ArrayList<>();
            }
            tmp.add(image);
        }

        return tmp;
    }

    /**
     * 获取当前图片  传递给选择图片activity用
     */
    private ArrayList<String> getPhotos(boolean addPlus) {
        if (mPhotoList == null) {
            return new ArrayList<>();
        }

        ArrayList<String> tmp = new ArrayList<>();
        for (String s : mPhotoList) {
            if (PostConstants.ADD_PLUS.equals(s)) {
                continue;
            }
            tmp.add(s);
        }
        if (addPlus && tmp.size() < PostConstants.POST_PIC_MAX_SIZE) {
            tmp.add("");
        }

        return tmp;
    }

    /**
     * 拍照
     */
    String capturePath;

    private void doCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        capturePath = BitmapUtil.getPostCapturePath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(capturePath)));
        startActivityForResult(intent, CAMERA_PHOTO_WITH_DATA);
    }

    private View.OnClickListener addVoiceListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (tv_voice_title != null && tv_voice_ing != null) {
                tv_voice_title.setVisibility(View.VISIBLE);
                tv_voice_ing.setVisibility(View.GONE);
                tv_voice_ing.setText("0:00");
            }

            record_popupWindow.showButtom();
        }
    };

    private View.OnClickListener addPicListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            selectphoto_popupWindow.showButtom();
        }
    };

    /**
     * 点击发布按钮进行发布帖子
     */
    private void rightBtnHandle() {
        stopPlayAudio();

        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(this);
            return;
        }

        if (IS_POSTING) {
            MyLog.w(StringUtil.formatDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss S") + " 正在发帖ing");
            AppContext.showToast(R.string.posting_toast);
            return;
        }

        if (Tools.isFastDoubleSend()) {
            MyLog.w(StringUtil.formatDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss S") + getString(R.string.click_too_fast));
            AppContext.showToast(R.string.click_too_fast);
            return;
        } else if (!TDevice.hasInternet()) {
            AppContext.showToast(R.string.no_network);
            return;
        } else {
            int picSize = getPhotos(false).size();
            boolean noPic = picSize == 0;
            boolean noVoice = (mVoiceLayout.getVisibility() == View.GONE);
            if (noPic && noVoice) {
                if (title_state != CORRECT) {
                    AppContext.showToast(R.string.post_title_hint);
                    return;
                } else if (getTextContent().length() < 10) {
                    AppContext.showToast(R.string.post_title_too_short);
                    return;
                }
            }

            IS_POSTING = true;

            HashMap<String, String> map = new HashMap<String, String>();
            map.put(UmengConstants.ITEM_ID, topicName + "_"  + topicID);
            MobclickAgent.onEvent(getApplicationContext(), UmengConstants.POST_SUBMIT_CLICK, map);

            showWaitDialog();
            mTitleBar.setRightBtnClickAble(false);

            // TODO 上传图片时要压缩图片
            compressPhotos(picSize);

            String bucket = StickerManager.getInstance().getBestServerBucket();
            int cosType = noPic ? (noVoice ? CosplayInfo.TYPE_POST_TEXT : CosplayInfo.TYPE_POST_AUDIO) : CosplayInfo.TYPE_POST_PICTURE;
            //TODO, 发布图片时需要加上话题名称
            StickerManager.getInstance().submitStickerPic(cosType, bucket, null, null, null,
                    getPhotoString(bucket), getAudioKey(bucket),
                    null, null,
                    getTextContent(),
                    topicName,
                    4, 4, new IListener() {
                        @Override
                        public void onSuccess(Object obj) {
                            //dismiss弹框
                            hideWaitDialog();

                            if (obj == null) {
                                onErr(ErrorState.RELEASE_FAIL);
                            }
                            //后台上传图片和音频文件,只备份音频文件.
                            String ucid = (String) obj;
                            if (!noPic) {
                                uploadPhotos(ucid);
                            } else if (!noVoice) {
                                uploadAudio(ucid);
                            }

                            //toast发布成功
                            MyLog.e("发布成功 UCID=" + obj + " @ " + TimeUtils.getCurrentTimeInString());
                            AppContext.showToastShort(R.string.release_success);

                            //跳转到帖子详情页面
                            finish();// 不再跳回来了
                            UIHelper.showPostDetail(PostActivity.this, generateCosplayInfo(ucid, cosType));

                            StickPreference.getInstance().deleteJoinTopicInfo();
                        }

                        @Override
                        public void onErr(Object obj) {
                            MyLog.e("发布失败：" + obj + " @ " + TimeUtils.getCurrentTimeInString());
                            toast(ErrorState.RELEASE_FAIL);
                            StickPreference.getInstance().deleteJoinTopicInfo();
                        }

                        @Override
                        public void onNoNetwork() {
                            MyLog.e("发布失败 NO——NETWORK @ " + TimeUtils.getCurrentTimeInString());
                            toast(ErrorState.NO_NETWORK);
                        }
                    });
        }
    }

    /**
     * 对图片进行压缩到5MB以内
     */
    private void compressPhotos(int picSize) {
        if (picSize <= 0) {
            return;
        }

        StringBuffer sb = new StringBuffer();
        boolean has = false;
        for (int i = 0; i < picSize; i++) {
            File file = new File(mPhotoList.get(i));
            if (file.exists() && file.isFile() && file.length() >= PostConstants.POST_PIC_MAX_MB ||
                    FileUtil.getExtensionName(mPhotoList.get(i)).equals("webp")) {
                MyLog.i(mPhotoList.get(i) + " 's size(KB)=" + file.length() / 1024);
                if (!has) {
                    sb.append(i + 1);
                    has = true;
                } else {
                    sb.append("," + (i + 1));
                }
            }
        }

        if (has) {
            AppContext.showToast(String.format(getResources().getString(R.string.picture_too_big), sb.toString()));

            String[] bigger = sb.toString().split(",");
            if (bigger.length > 0) {
                // TODO 用最大宽度 1080*1920 这样在大小分辨率手机上都能保证一定的清晰度   720*1280
                // 80 是压缩质量
                int quality = 80;
                for (int i = 0; i < bigger.length; i++) {
                    int position = i;
                    try {
                        position = Integer.parseInt(bigger[i]) - 1;
                    } catch (NumberFormatException e) {
                        break;
                    }
                    String path = mPhotoList.get(position);
                    String path_new = BitmapUtil.getPostCompressPath(position);
                    Bitmap bitmap =
                            com.moinapp.wuliao.ui.imageselect.ImageLoader.getInstance()
                                    .decodeSampledBitmapFromResource(path, PostConstants.POST_PIC_MAX_WIDTH, PostConstants.POST_PIC_MAX_HEIGHT);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
                    MyLog.i("按宽度缩小后图片是" + baos.toByteArray().length / 1024 + " KB, 宽高：" + bitmap.getWidth() + "*" + bitmap.getHeight());
                    if (baos.toByteArray().length < PostConstants.POST_PIC_MAX_MB) {
                        boolean ret = BitmapUtil.saveBitmap2file(bitmap, path_new, Bitmap.CompressFormat.JPEG, quality);
                        if (ret) {
                            mPhotoList.set(position, path_new);
                            MyLog.i(" decode图片OK: " + path_new);
                        }
                    } else {
                        MyLog.i(" 压缩图片 ");
                        Bitmap compress = BitmapUtil.compressImage(bitmap, 90, PostConstants.POST_PIC_MAX_KB);
                        boolean ret = BitmapUtil.saveBitmap2file(compress, path_new, Bitmap.CompressFormat.JPEG, quality);
                        if (ret) {
                            mPhotoList.set(position, path_new);
                            MyLog.i(" 压缩图片OK: " + path_new);
                        }
                    }
                }
            }
        }
    }

    /** 获取标题内容 */
    private String getTextContent() {
//        return mContent.getText().toString().replaceAll("\n", "").trim();
        return mContent.getText().toString().trim();
    }

    /**
     * 新建一个CosplayInfo,跳转到帖子详情用
     */
    private CosplayInfo generateCosplayInfo(String ucid, int type) {
        CosplayInfo cosplayInfo = new CosplayInfo();
        cosplayInfo.setUcid(ucid);
        cosplayInfo.setType(type);
        cosplayInfo.setContent(getTextContent());

        UserInfo me = AppContext.getInstance().getUserInfo();
        me.setRelation(4);
        cosplayInfo.setAuthor(me);

        cosplayInfo.setCreatedAt(System.currentTimeMillis());
        cosplayInfo.setIsWrite(1);
        cosplayInfo.setWriteAuth(4);

        TagInfo tagInfo = new TagInfo();
        tagInfo.setName(topicName);
        tagInfo.setTagId(topicID);
        tagInfo.setType("TP");
        List<TagInfo> tagInfos = new ArrayList<>(1);
        tagInfos.add(tagInfo);
        cosplayInfo.setTags(tagInfos);

        if (mAudioInfo != null) {
            ArrayList<StickerAudioInfo> audioList = new ArrayList(1);
            audioList.add(mAudioInfo);
            cosplayInfo.setAudio(audioList);
        }

        cosplayInfo.setPictureList(getPhotos());

        return cosplayInfo;
    }


    /**
     * 上传图片
     */
    private void uploadPhotos(String ucid) {
        int size = getPhotos(false).size();
        for (int i = 0; i < size; i++) {
            String path = mPhotoList.get(i);
            MyLog.i("执行上传图片任务[" + i + "], " + path);
            if (!StringUtil.isNullOrEmpty(path)) {
                CameraManager.getInst().saveUnuploadFiles2DataBase(DiscoveryConstants.TYPE_PHOTO, ucid, path, 0, System.currentTimeMillis());
                CameraManager.getInst().uploadFiles(this, DiscoveryConstants.TYPE_PHOTO, ucid, path, 0, 0, null);
            } else {
                AppContext.showToast(R.string.no_sufficient_storage);
            }
        }
    }


    /**
     * 上传音频
     */
    private void uploadAudio(String ucid) {
        String backupAudioPath = backupAudio(ucid);

        MyLog.i("执行上传音频任务[" + backupAudioPath + "]");
        if (!StringUtil.isNullOrEmpty(backupAudioPath)) {
            CameraManager.getInst().saveUnuploadFiles2DataBase(DiscoveryConstants.TYPE_AUDIO, ucid, backupAudioPath, 0, System.currentTimeMillis());
            CameraManager.getInst().uploadFiles(this, DiscoveryConstants.TYPE_AUDIO, ucid, backupAudioPath, 0, 0, null);
        } else {
            AppContext.showToast(R.string.no_sufficient_storage);
        }
    }

    /**
     * 发帖接口用的音频对象(uri是音频key)
     */
    private List<StickerAudioInfo> getAudioKey(String bucket) {
        if (mAudioInfo == null) {
            return null;
        }
        ArrayList<StickerAudioInfo> audioList = new ArrayList(1);
        StickerAudioInfo audioInfo = new StickerAudioInfo();
        audioInfo.setLength(mAudioInfo.getLength());
        audioInfo.setUri(StringUtil.getUploadKey(DiscoveryConstants.TYPE_AUDIO, mMediaRecorderUtil.getRecordPath(), bucket));
        audioList.add(audioInfo);

        return audioList;
    }

    /**
     * 发帖接口用的图片串(图片key以逗号连接)
     */
    private String getPhotoString(String bucket) {
        if (mPhotoList == null || mPhotoList.isEmpty()) {
            return null;
        }
        StringBuffer photos = new StringBuffer();
        boolean first = true;
        for (String s : mPhotoList) {
            if (PostConstants.ADD_PLUS.equals(s)) {
                continue;
            }

            String picKey = StringUtil.getUploadKey(DiscoveryConstants.TYPE_PHOTO, s, bucket);

            if (first) {
                photos.append(picKey);
                first = false;
            } else {
                photos.append(",").append(picKey);
            }
        }
        return photos.toString();
    }

    private enum ErrorState {NO_NETWORK, RELEASE_FAIL}

    /**
     * @param type 1:noNetwork 2:release failed
     */
    private void toast(ErrorState type) {
        hideWaitDialog();
        IS_POSTING = false;
        mTitleBar.setRightBtnClickAble(true);
        if (type == ErrorState.NO_NETWORK) {
            AppContext.showToastShort(R.string.hasno_network);
        } else if (type == ErrorState.RELEASE_FAIL) {
            AppContext.showToastShort(R.string.release_fail);
        }
    }

    /**
     * 备份音频文件
     */
    private String backupAudio(String ucid) {
        String dest = CameraManager.getInst().getCosplayElementPath(ucid, 3);
        if (StringUtil.isNullOrEmpty(dest)) {
            return null;
        }
        FileUtil.getInst().copyFile(mAudioInfo.getUri(), dest);
        if ((new File(dest)).exists()) {
            return dest;
        }
        return null;
    }

    /**
     * 上传帖子图片任务
     */
    private class UploadImageTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(final String... strings) {
            try {
                if (!TDevice.hasInternet()) {
                    MyLog.i("doInBackground onNoNetwork" + ", " + strings[0]);
                    onUploadCallback(strings[0], -1, "");
                }
/*
                // 图片上传成功咯  picid是服务器返回的图片id
                onUploadCallback(strings[0], 1, picid);

                MyLog.i("doInBackground onErr " + object + ", " + strings[0]);
                onUploadCallback(strings[0], 0, "");
                */
                // TODO 上传图片接口
            } catch (Exception e) {
                MyLog.e(e);
                e.printStackTrace();
                onUploadCallback(strings[0], 0, "");
            } finally {
                return null;
            }

        }

        private void onUploadCallback(String url, int type, String picId) {
            Message msg = new Message();
            msg.what = 0x55;

            Bundle b = new Bundle();
            b.putString("url", url);
            b.putString("picid", picId);
            b.putInt("type", type);
            msg.setData(b);
            handler.sendMessage(msg);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlayAudio();
    }
}
