package com.moinapp.wuliao.modules.stickercamera.app.camera.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.keyboard.bean.EmoticonBean;
import com.keyboard.bean.EmoticonSetBean;
import com.keyboard.db.DBHelper;
import com.keyboard.utils.DefEmoticons;
import com.keyboard.utils.EmoticonsKeyboardBuilder;
import com.keyboard.view.EmotPackagesPageView;
import com.keyboard.view.EmoticonsIndicatorView;
import com.keyboard.view.EmoticonsPageView;
import com.keyboard.view.I.IView;
import com.keyboard.view.I.OnEmoticonsPageViewListener;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.bean.BaseImage;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.cache.CacheManager;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.preference.CommonsPreference;
import com.moinapp.wuliao.listener.Callback;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.listener.IListener2;
import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.modules.mine.MinePreference;
import com.moinapp.wuliao.modules.sticker.StickPreference;
import com.moinapp.wuliao.modules.sticker.StickerConstants;
import com.moinapp.wuliao.modules.sticker.StickerManager;
import com.moinapp.wuliao.modules.sticker.model.StickerAudioInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerColorTextInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerEditInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerPackage;
import com.moinapp.wuliao.modules.sticker.model.StickerProject;
import com.moinapp.wuliao.modules.stickercamera.app.camera.AudioService;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraBaseActivity;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.modules.stickercamera.app.camera.adapter.VoiceAdapter;
import com.moinapp.wuliao.modules.stickercamera.app.camera.colortext.ColorTextColor;
import com.moinapp.wuliao.modules.stickercamera.app.camera.colortext.ColorTextColorAdapter;
import com.moinapp.wuliao.modules.stickercamera.app.camera.colortext.ColorTextColors;
import com.moinapp.wuliao.modules.stickercamera.app.camera.colortext.ColorTextStyle;
import com.moinapp.wuliao.modules.stickercamera.app.camera.colortext.ColorTextStyleAdapter;
import com.moinapp.wuliao.modules.stickercamera.app.camera.colortext.ColorTextUtils;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.Point2D;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.StickerUtils;
import com.moinapp.wuliao.modules.stickercamera.app.ui.Sticker;
import com.moinapp.wuliao.modules.stickercamera.app.ui.StickerView;
import com.moinapp.wuliao.modules.stickercamera.base.util.DialogHelper;
import com.moinapp.wuliao.ui.MyPopWindow;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.EmoticonsUtils;
import com.moinapp.wuliao.util.FileUtil;
import com.moinapp.wuliao.util.HttpUtil;
import com.moinapp.wuliao.util.ImageUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.sephiroth.android.library.widget.HListView;

/**
 * 图片处理界面
 * Created by sky on 2015/7/8.
 * Weibo: http://weibo.com/2030683111
 * Email: 1132234509@qq.com
 */
public class PhotoProcessActivity extends CameraBaseActivity {

    private static final ILogger MyLog = LoggerFactory.getLogger(PhotoProcessActivity.class.getSimpleName());
    public static final String KEY_PARENT_UCID = "parent_ucid";
    public static final String KEY_PARENT_WRITE_AUTH = "parent_wauth";
    public static final String KEY_PARENT_STICKPROJECT = "sticker";
    public static final String KEY_PARENT_GLORIFY = "glorify";
    public static final String KEY_PARENT_CLICK_TIME = "click_time";
    private static final int COLOR_TEXT_MARGIN_TOP = -15;
    private static final int TOOBAR_BTN_POS_VOICE = 1;
    private static final int TOOBAR_BTN_POS_COLORTXT = 2;
    private static final int TOOBAR_BTN_POS_STICK_PACKAGE = 3;
    private static final int TOOBAR_BTN_POS_RECENT = 4;
    private static final int EMOJI_PAGE_HEIGHT =
            AppContext.getApp().getScreenHeight()
                    - AppContext.getApp().getScreenWidth()
//                    - getResources().getDimensionPixelSize(R.dimen.all_title_height)
//                    - getResources().getDimensionPixelSize(R.dimen.bar_big_height) * 2
                    - AppContext.getApp().dp2px(113f) - 1;//title 42 + innerTitle 20 + toolbar 78*51
    @InjectView(R.id.title_left_btn)
    ImageView title_left;
    @InjectView(R.id.title_op_btns)
    LinearLayout title_op_btns;
    @InjectView(R.id.title_right)
    TextView title_right;
    //图片翻转按钮
    @InjectView(R.id.iv_reverse_horizontal)
    ImageView reverseHorizontal;
    @InjectView(R.id.iv_reverse_vertical)
    ImageView reverseVertical;
    //图层操作按钮
    @InjectView(R.id.iv_upper)
    ImageView cosUpper;
    @InjectView(R.id.iv_downer)
    ImageView cosDowner;
    @InjectView(R.id.tv_color_text)
    TextView colorTextModel;
    //滤镜图片
    @InjectView(R.id.gpuimage)
    ImageView mGPUImageView;
    //贴纸图片
    @InjectView(R.id.stickview)
    StickerView mStickView;
    //绘图区域
    @InjectView(R.id.drawing_view_container)
    ViewGroup drawArea;

    //工具区---------------------------------------------start
    //声音
    @InjectView(R.id.list_voices)
    HListView voiceBar;

    //彩色字布局
    @InjectView(R.id.ly_color_text)
    LinearLayout mLyColorText;
    //彩色字颜色标题
    @InjectView(R.id.tv_color_text_title_c)
    TextView colorTextColorTitle;
    //彩色字颜色
    @InjectView(R.id.list_color_text_styles)
    HListView colorTextStyleBar;
    //彩色字样式
    @InjectView(R.id.list_color_text_colors)
    HListView colorTextColorBar;

    //最近贴纸布局
    @InjectView(R.id.ly_recent)
    LinearLayout mLyRecent;
    //最近贴纸个数
    @InjectView(R.id.tv_rencent_count)
    TextView recentCount;
    //最近贴纸内容
    @InjectView(R.id.xview_recent_epv)
    EmoticonsPageView mRecentEmoticonsPageView;
    //最近贴纸indicator
    @InjectView(R.id.xview_recent_eiv)
    EmoticonsIndicatorView mRecentEmoticonsIndicatorView;

    // 3.2.6贴纸区域
    // 贴纸界面
    @InjectView(R.id.ly_sticker)
    LinearLayout mLyStick;
    // 贴纸一级界面
    @InjectView(R.id.ly_sticker_package)
    LinearLayout mLyStickPackage;
    @InjectView(R.id.xview_epv_package)
    EmotPackagesPageView mEmotPackagesPageView;
    @InjectView(R.id.xview_eiv_package)
    EmoticonsIndicatorView mEmotPackagesIndicatorView;

    // 贴纸二级界面
    @InjectView(R.id.ly_sticker_item)
    LinearLayout mLyStickItem;
    // 贴纸二级标题
    @InjectView(R.id.sticker_name)
    TextView stickerTitle;
    // 二级返回按钮
    @InjectView(R.id.sticker_back)
    ImageView stickerBack;
    @InjectView(R.id.xview_epv_sticker)
    EmoticonsPageView mEmoticonsPageView;
    @InjectView(R.id.xview_eiv_sticker)
    EmoticonsIndicatorView mEmoticonsIndicatorView;

    //工具区---------------------------------------------end
    // 底部声音按钮area
    @InjectView(R.id.voice_btn)
    LinearLayout voiceBtnArea;
    @InjectView(R.id.iv_voice)
    ImageView voiceImageBar;
    @InjectView(R.id.tv_voice)
    TextView voiceTvBar;
    // 底部彩色字按钮area
    @InjectView(R.id.colortext_btn)
    LinearLayout colorTextBtnArea;
    @InjectView(R.id.iv_colortext)
    ImageView colorTextImageBar;
    @InjectView(R.id.tv_colortext)
    TextView colorTextTvBar;
    // 底部贴纸按钮area
    @InjectView(R.id.store_btn)
    LinearLayout storeBtnArea;
    @InjectView(R.id.iv_store)
    ImageView storeImageBar;
    @InjectView(R.id.tv_store)
    TextView storeTvBar;
    // 底部最近按钮area
    @InjectView(R.id.recent_btn)
    LinearLayout recentBtnArea;
    @InjectView(R.id.iv_recent)
    ImageView recentImageBar;
    @InjectView(R.id.tv_recent)
    TextView recentTvBar;

    //当前图片[加了滤镜效果的图]
    private Bitmap currentBitmap;

    //滤镜值
    private int ctStyleI = -1;
    private ColorTextStyle currentCTS = new ColorTextStyle(Typeface.DEFAULT, Typeface.NORMAL, Paint.ANTI_ALIAS_FLAG);
    private int ctColorI = 0;
    private String currentCtColor = AppContext.getInstance().getString(R.string.color_text_color_0);
    private TextPaint textPaintnew;
    private int SW = AppContext.getApp().getScreenWidth();;
    private Uri bgUri; // 原始底图
    private String glorifyBmpPath;// 调整之后的底图
    private StickerProject stickerProject;

    private String ucid;
    private int wauth;
    View.OnClickListener finishCB;
    private String from;
    private int lastPosition = -1; // 贴纸包栏第几组贴纸
    List<EmoticonSetBean> reloadStickerList;//重新加载贴纸包列表
    List<StickerPackage> StickerPackageList;//包含了服务器给的我的全部贴纸列表
    List<StickerPackage> StickerPackageDetailList = new ArrayList<StickerPackage>();
    EmoticonSetBean mRecentSet = null;
    long mClickTime;
    VoiceAdapter voiceAdapter;
    ArrayList<StickerAudioInfo> mAudioInfo = new ArrayList<StickerAudioInfo>();//使用的声音贴纸

    // 一级贴纸包集合
    List<EmoticonSetBean> majorStickerList;


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleReloadStickers();
            updateStickersArea(0);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_process);
        mClickTime = getIntent().getLongExtra(KEY_PARENT_CLICK_TIME, 0);
        ButterKnife.inject(this);
//        EffectUtil.clear();
        initRecent(false);
        initVoiceToolBar();
        initMajorStickPackagesArea();

        initView();
        initEvent();

        if (hasTopicEmojiSet()) {
            setToolBtnBg(TOOBAR_BTN_POS_STICK_PACKAGE, true);
            toggleStickPackItem(false);
        } else {
            setToolBtnBg(TOOBAR_BTN_POS_VOICE, true);
        }

        IntentFilter filter = new IntentFilter(
                Constants.INTENT_ACTION_USER_CHANGE);
        filter.addAction(Constants.INTENT_ACTION_LOGOUT);
        registerReceiver(mReceiver, filter);

        //设置照片编辑页面已经启动的标记
        StickPreference.getInstance().setPhotoProcessRunning(true);

        ucid = getIntent().getStringExtra(KEY_PARENT_UCID);
        MyLog.i("ucid =" + ucid);
        if (!StringUtil.isNullOrEmpty(ucid)) {
            //如果是转改图片的话,清空默认使用的贴纸包
            StickPreference.getInstance().setDefaultUseSticker(null);
        }

        wauth = getIntent().getIntExtra(KEY_PARENT_WRITE_AUTH, 4);
        MyLog.i("wauth =" + wauth);

        bgUri = getIntent().getData();
        MyLog.i("bgUri=" + bgUri);

        glorifyBmpPath = getIntent().getStringExtra(KEY_PARENT_GLORIFY);
        MyLog.i("glorifyBmpPath =" + glorifyBmpPath);
        initBg();

        stickerProject = (StickerProject) getIntent().getSerializableExtra(KEY_PARENT_STICKPROJECT);
        MyLog.i("stickerProject =" + stickerProject);
        initStickProject();

        // 彩色字的前回设置颜色和样式恢复
        ctColorI = StickPreference.getInstance().getLastColorTextColorPos();
        currentCtColor = ColorTextColors.getAllColors().get(ctColorI).getValue();
        textPaintnew = colorTextModel.getPaint();
        textPaintnew.setColor(Color.parseColor(currentCtColor));
        textPaintnew.setAntiAlias(true);
    }

    /** 处理加载全部我的下载的贴纸包------------------------------------start */
    /**
     * 判断是否需要重新加载贴纸包
     */
    private void handleReloadStickers() {
        if (AppContext.getInstance().isLogin()) {
            //如果已经弹出过提示,不再弹出了
//            if (StickPreference.getInstance().getReloadStickerFlag()) return;

            reloadStickerList = StickerManager.getInstance().getDownloadStickers(ClientInfo.getUID());
            int local = 0;
            if (reloadStickerList != null) {
                local = reloadStickerList.size();
            }
            final int dbCount = local;
            // 数据库为空,需要调用获取我的贴纸包接口获取已经下载的贴纸
            if (StickerPackageList == null) {
                StickerPackageList = new ArrayList<>();
            } else {
                StickerPackageList.clear();
            }

            requestHistoryStickerPackages(dbCount);
        }
    }

    String mLastId;//获取服务器上已下载的我的全部贴纸列表接口用

    /**
     * 获取服务器上已下载的我的全部贴纸列表
     *
     * @param dbCount saved stickerpackages in local DB
     */
    private void requestHistoryStickerPackages(int dbCount) {
        StickerManager.getInstance().getMyStickerList(null, null, null, mLastId, new IListener2() {
            final int NONE = 0, ERROR = -1, MORE = -2, NO_MORE = 1;
            int mState = NONE;

            @Override
            public void onSuccess(Object obj) {
                List<StickerPackage> list = (List<StickerPackage>) obj;
                MyLog.i("onSuccess 获取到了" + list.size() + "条");
                if (list != null && list.size() > 0) {
                    mState = MORE;

                    // 去除重复数据
                    if (StickerPackageList != null && StickerPackageList.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            if (compareTo(StickerPackageList, list.get(i))) {
                                list.remove(i);
                                i--;
                            }
                        }
                    }

                    if (!list.isEmpty()) {
                        StickerPackageList.addAll(list);
//                    mLastId = StickerPackageList.get(list.size() - 1).getStickerPackageId();
                        // TODO 如果这个时候list在比较之后为空,那么get就会出现indexOutOfBoundsException
                        mLastId = list.get(list.size() - 1).getStickerPackageId();
                        MyLog.i("onSuccess:StickerPackageList.size=" + StickerPackageList.size());
                    } else {
                        mState = NO_MORE;
                    }
                } else {
                    mState = NO_MORE;
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                MyLog.i("开始 获取");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (mState == ERROR) {
                    MyLog.i("获取结束 mState=ERROR");
                    doReloadStickerPackages(dbCount);
                }
                if (mState == NO_MORE) {
                    doReloadStickerPackages(dbCount);
                    MyLog.i("获取结束 mState=NO_MORE");
                } else if (mState == MORE) {
                    MyLog.w("还有更多哦~~~~ ");
                    requestHistoryStickerPackages(dbCount);
                }
            }

            @Override
            public void onErr(Object obj) {
                mState = ERROR;
            }

            @Override
            public void onNoNetwork() {
                mState = ERROR;
            }
        });
    }

    private boolean compareTo(List<StickerPackage> data, StickerPackage enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                if (enity.getStickerPackageId().equalsIgnoreCase(data.get(i).getStickerPackageId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断用户已下载的贴纸包中是否有需要重新下载的贴纸包
     *
     * @param dbCount 是本地数据库里贴纸包的数量
     */
    private void doReloadStickerPackages(int dbCount) {
        if (StickerPackageList != null && StickerPackageList.size() > 0) {
            if (dbCount != StickerPackageList.size()) {
                // 服务器给的数量大于本地DB贴纸包数量,需要重新下载
                reloadAllStickers(2);
            } else {
                // 如果数据库和服务器下发数量相同,检查是否有需要重新加载的包
                reloadStickerList = StickerManager.getInstance().
                        getNeedReloadStickers(ClientInfo.getUID());
                if (reloadStickerList != null && reloadStickerList.size() > 0) {
                    // 有贴纸需要重新下载
                    reloadAllStickers(2);
                }
            }
        }
    }

    int mReloadCount = 0;
    int mTotalCount = 0;

    /**
     * 重新下载贴纸包
     *
     * @param flag : 1, 用本地数据库的记录轮循下载 2, 用服务器下发的贴纸包list轮循下载
     */
    private void reloadAllStickers(int flag) {
        mTotalCount = flag == 1 ? reloadStickerList.size() : StickerPackageList.size();
        if (mTotalCount > 0) toggleStickerRefreshProgress(true);
        try {
            if (flag == 1) {
                for (EmoticonSetBean beanset : reloadStickerList) {
                    reloadSingleSticker(flag, beanset.getId(), beanset);
                }
            } else if (flag == 2) {
                for (StickerPackage sticker : StickerPackageList) {
                    reloadSingleSticker(flag, sticker.getStickerPackageId(), null);
                }
            }
        } catch (Exception e) {
            MyLog.e(e);
            toggleStickerRefreshProgress(false);
            MyLog.i("call updateStickersArea 刷新下面的贴纸栏view  from reloadAllStickers");
            updateStickersArea(0);
            refresh_sticker_package_popupWindow = null;
        }
    }

    /** 重新下载贴纸包 */
    private void reloadSingleSticker(int flag, String id, EmoticonSetBean beanset) {
        StickerManager.getInstance().getStickerDetail(id, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                StickerPackage detail = (StickerPackage) obj;
                if (detail != null) {
                    StickerPackageDetailList.add(detail);
                    if (flag == 1) {
                        //这种情况是2.6用户升级到2.7,本地数据库有数据,需要重新下载的情况
                        reloadStickerPackage(0, detail, beanset);
                    } else if (flag == 2) {
                        //这里是用户换手机或重装了应用,本地数据库无数据的情况,考虑到体验,这里只下载icon
                        //下载完后需要后台下载大图
                        reloadStickerPackageIcon(0, detail);
                    }
                } else {
                    dismissStickerRefreshProgress();
                }
            }

            @Override
            public void onErr(Object obj) {
                dismissStickerRefreshProgress();
            }

            @Override
            public void onNoNetwork() {
                dismissStickerRefreshProgress();
            }
        });
    }

    private void reloadStickerPackage(int position, StickerPackage sticker, EmoticonSetBean emoticonSetBean) {
        UpdateTask task = new UpdateTask(emoticonSetBean);
        task.setOnCallback(new OnCallback() {
            @Override
            public void onSuccess(Object object) {
                MyLog.i("贴纸包更新 OK:");
                handleAfterReload(position, sticker, emoticonSetBean);
            }

            @Override
            public void onFailed(Object object) {
                dismissStickerRefreshProgress();
            }
        });
        task.execute(sticker);
    }

    /** 重新下载贴纸包icon */
    private void reloadStickerPackageIcon(int position, StickerPackage sticker) {
        StickerManager.getInstance().downloadStickerIconToLocal(sticker, new Callback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish(int result) {
                dismissStickerRefreshProgress();
            }
        });

    }

    /** 贴纸包更新情况下重新下载完成后后刷新界面 */
    private void handleAfterReload(int position, StickerPackage sticker, EmoticonSetBean emoticonSetBean) {
        mEmoticonsPageView.getEmoticonSetBeanList().get(position).setFlag(StickerConstants.FLAG_NORMAL);
        DBHelper dbHelper = DBHelper.getInstance(BaseApplication.context());
        if (emoticonSetBean.getStickType() > 1) {
            dbHelper.updateEmoticonSetFlag(emoticonSetBean.getStickType(), StickerConstants.FLAG_NORMAL);
        } else {
            dbHelper.updateEmoticonSetFlag(emoticonSetBean.getId(), StickerConstants.FLAG_NORMAL);
        }

        dismissStickerRefreshProgress();
        MyLog.i("call updateStickersArea 刷新下面的贴纸栏view  from handlerAfterReload");
        updateStickersArea(position);
    }

    /** 重新加载完贴纸包后刷新一级和二级界面 */
    private void dismissStickerRefreshProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    mReloadCount++;
                }
                if (mReloadCount >= mTotalCount) {
                    toggleStickerRefreshProgress(false);
                    refresh_sticker_package_popupWindow = null;

                    MyLog.i("call updateStickersArea 刷新下面的贴纸栏view  from dismissStickerRefreshProgress");
                    initMajorStickPackagesArea();
                    scrollOutPageView(0);

                    // 同步完成后选中默认的贴纸包
                    selectDefaultUseSitcker();
                }
            }
        });
    }

    MyPopWindow refresh_sticker_package_popupWindow;

    /**
     * 显示和隐藏更新按钮,以及更新时的popwindow
     */
    private void toggleStickerRefreshProgress(boolean isProgress) {
        if (isProgress) {

            if (refresh_sticker_package_popupWindow == null) {
                initRefreshPopWindow();
            } else {
                if (!refresh_sticker_package_popupWindow.isShowing()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                refresh_sticker_package_popupWindow.show(Gravity.CENTER, false);
                            } catch (WindowManager.BadTokenException e) {
                                MyLog.e(e);
                            }
                        }
                    });
                }
            }
        } else {
            if (refresh_sticker_package_popupWindow != null
                    && refresh_sticker_package_popupWindow.isShowing())
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refresh_sticker_package_popupWindow.dismiss();
                    }
                });
        }
    }

    public void initRefreshPopWindow() {
        View popupWindow_view = getLayoutInflater().inflate(R.layout.update_sticker_package_dialog, null);
        popupWindow_view.setPadding(0, 0, 0, CommonsPreference.getInstance().getVirtualKeyboardHeight());
        refresh_sticker_package_popupWindow = new MyPopWindow(this, popupWindow_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        refresh_sticker_package_popupWindow.setOutsideTouchable(false);
    }

    /** 处理加载全部我的下载的贴纸包------------------------------------end */

    /** 处理更新下载的贴纸包------------------------------------start */

    UpdateTask mUpdateTask;

    private void cancelUpdateTask(UpdateTask updateTask) {
        if (updateTask != null) {
            updateTask.cancel(true);
            updateTask = null;
        }
    }

    /**
     * 点击贴纸包更新按钮, 手动更新贴纸包
     */
    private void updateStickerPackage(int position) {
        if (position < 0 || position >= mEmoticonsPageView.getEmoticonSetBeanList().size()) {
            return;
        }
        EmoticonSetBean emoticonSetBean = mEmoticonsPageView.getEmoticonSetBeanList().get(position);
        if (emoticonSetBean == null) {
            return;
        }
        toggleStickerRefreshProgress(true);

        MyLog.i("更新贴纸包 updateStickerPackage position=" + position);
        MyLog.i("更新贴纸包 emoticonSetBean=" + emoticonSetBean.toString());
        MyLog.i("更新贴纸包 emoticonSetBean stickerType=" + emoticonSetBean.getStickType());
        MyLog.i("更新贴纸包 emoticonSetBean updatetime=" + emoticonSetBean.getUpdateTime());
        MyLog.i("更新贴纸包 emoticonSetBean id=" + emoticonSetBean.getId());
        StickerManager stickerManager = StickerManager.getInstance();
        stickerManager.getStickerUpdate(emoticonSetBean.getStickType(),
                emoticonSetBean.getUpdateTime(), emoticonSetBean.getId(), new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        DBHelper dbHelper = DBHelper.getInstance(BaseApplication.context());
                        if (emoticonSetBean.getStickType() == StickerPackage.STICKER_INTIME) {
                            dbHelper.updateEmoticonSetFlag(emoticonSetBean.getStickType(),
                                    obj == null ? StickerConstants.FLAG_NORMAL : StickerConstants.FLAG_UNUPDATED);
                        } else if (!TextUtils.isEmpty(emoticonSetBean.getId())) {
                            dbHelper.updateEmoticonSetFlag(emoticonSetBean.getId(),
                                    obj == null ? StickerConstants.FLAG_NORMAL : StickerConstants.FLAG_UNUPDATED);
                        }

                        MyLog.i("更新贴纸包 1 更新贴纸包内的贴纸UI");
                        // 更新贴纸包内的贴纸UI
                        refreshStickerPackage(position, (StickerPackage) obj, emoticonSetBean);
                    }

                    @Override
                    public void onErr(Object obj) {
                        // TODO 更新失败或者无网络时需要添加个标志啦,然后在贴纸制作activity中检查是否有更新没做,继续做
                        MyLog.i("更新贴纸包 2 sticker=onErr");
                        toggleStickerRefreshProgress(false);
                    }

                    @Override
                    public void onNoNetwork() {
                        MyLog.i("更新贴纸包 3 sticker=onNoNetwork");
                        toggleStickerRefreshProgress(false);
                    }
                });

    }

    /**
     * 更新贴纸包内的贴纸UI 刷新下面的显示view
     */
    private void refreshStickerPackage(int position, StickerPackage sticker, EmoticonSetBean emoticonSetBean) {
        // 只有更新通知,实则无贴纸更新的情况
        if (sticker == null) {
            doUIUpdte(position);
            return;
        }
        MyLog.i("更新贴纸包内的贴纸UI refreshStickerPackage sticker=" + sticker.toString());
        cancelUpdateTask(mUpdateTask);
        mUpdateTask = new UpdateTask(emoticonSetBean);
        mUpdateTask.setOnCallback(new OnCallback() {
            @Override
            public void onSuccess(Object object) {
                MyLog.i("贴纸包更新 OK:");

                mEmoticonsPageView.getEmoticonSetBeanList().get(position).setFlag(StickerConstants.FLAG_NORMAL);
                DBHelper dbHelper = DBHelper.getInstance(BaseApplication.context());
                if (emoticonSetBean.getStickType() > 1) {
                    dbHelper.updateEmoticonSetFlag(emoticonSetBean.getStickType(), StickerConstants.FLAG_NORMAL);
                } else {
                    dbHelper.updateEmoticonSetFlag(emoticonSetBean.getId(), StickerConstants.FLAG_NORMAL);
                }

                doUIUpdte(position);
            }

            @Override
            public void onFailed(Object object) {
                MyLog.i("贴纸包更新 failed:");
                toggleStickerRefreshProgress(false);
            }
        });
        mUpdateTask.execute(sticker);
    }

    // 手动更新完贴纸包之后刷新UI
    private void doUIUpdte(int position) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toggleStickerRefreshProgress(false);
//                updateStickersArea(position);
            }
        });
        MyLog.i("贴纸包更新 doUIUpdte:" + position);
        EmoticonsKeyboardBuilder builder = buildMyStickers();
        mEmoticonsPageView.setBuilder(builder);

        mEmoticonsPageView.updateView();
        mEmoticonsPageView.postInvalidate();
        mEmoticonsPageView.setPageSelect(position);
        MyLog.i("call setNameAndCount from doUIUpdte:" + position);
        setNameAndCount(position);
    }

    /**
     * 下载更新贴纸包
     */
    private class UpdateTask extends AsyncTask<StickerPackage, Void, Bundle> {
        EmoticonSetBean emoticonSetBean;

        public UpdateTask(EmoticonSetBean emoticonSetBean) {
            this.emoticonSetBean = emoticonSetBean;
        }

        @Override
        protected Bundle doInBackground(StickerPackage... params) {
            Bundle result = null;
            try {
                StickerPackage sticker = params[0];
                MyLog.i("task 要更新的贴纸包信息:" + emoticonSetBean);

                StickerManager stickerManager = StickerManager.getInstance();
                stickerManager.downloadStickerList(sticker, emoticonSetBean);
                result = stickerManager.downloadStickerIcon(sticker, emoticonSetBean);
                if (result != null) {
                    stickerManager.saveStickers2DB(emoticonSetBean.getStickType(), sticker, result.getString(stickerManager.KEY_URL), result.getString(stickerManager.KEY_PATH));
                }
            } catch (Exception e) {
                MyLog.e(e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            super.onPostExecute(bundle);

            if (bundle == null) {
                if (callback != null) {
                    MyLog.i("更新的贴纸包 failed");
                    callback.onFailed(null);
                }
                return;
            } else {
                if (callback != null) {
                    MyLog.i("更新的贴纸包 成功");
                    callback.onSuccess(bundle);
                }
            }
        }

        OnCallback callback;

        public void setOnCallback(OnCallback callback) {
            this.callback = callback;
        }
    }

    /** 处理更新下载的贴纸包------------------------------------end */

    /** 导入底图 */
    private void initBg() {
        if (!StringUtil.isNullOrEmpty(glorifyBmpPath)) {
            // TODO 设置加了滤镜的底图
            ImageUtils.asyncLoadImage(this, Uri.fromFile(new File(glorifyBmpPath)), new ImageUtils.LoadImageCallback() {
                @Override
                public void callback(Bitmap result) {
                    currentBitmap = result;
                    mGPUImageView.setImageBitmap(currentBitmap);

//                    MyLog.i("call refreshStickArea from initBg [" + bgUri.getScheme() + "]");
//                    refreshStickArea();// 刷新底部工具区
//                    }
                }
            });

        }
    }

    /** 导入工程文件[转改时] */
    private void initStickProject() {
        if (stickerProject != null) {
            List<StickerEditInfo> stickerEditInfoList = stickerProject.getStickers();
            if (stickerEditInfoList != null && stickerEditInfoList.size() > 0) {
                for (int i = 0; i < stickerEditInfoList.size(); i++) {
                    StickerEditInfo sei = stickerEditInfoList.get(i);

                    MyLog.i("第" + i + "个贴纸信息~是：" + sei.toString());
                    if (StickerUtils.isVoice(sei.getType())) {
                        //处理声音贴纸
                        Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.voice_pink_backgound);
                        Sticker sticker = mStickView.setWaterMark(background, null, sei.isLock(), sei);
                        mStickView.reDraw(sei, sticker);

                    } else if (StickerUtils.isColorText(sei.getType())) {
                        //处理彩色文字
                        Bitmap ctBmp = generateColorText(sei.getCtInfo());
                        Sticker sticker = mStickView.setWaterMark(ctBmp, null, sei.isLock(), sei);
                        mStickView.reDraw(sei, sticker);
                        MyLog.i("重绘彩色文字 " + sei.toString());
                    } else {
                        String picturePath = StickerUtils.getStickerPicPath(sei);
                        if (!StringUtil.isNullOrEmpty(picturePath)) {
                            File pictureFile = new File(picturePath);

                            if (!pictureFile.exists()) {
                                if (sei == null || sei.getPicture() == null || StringUtil.isNullOrEmpty(sei.getPicture().getUri())) {
                                    toast(getString(R.string.cosplay_import_failed), 0);
                                } else {
                                    String newUrl = StickerUtils.getSingleStickerPictureUrl(sei.getPicture().getUri());
                                    downloadSticker(newUrl, picturePath, new IListener() {
                                        @Override
                                        public void onSuccess(Object obj) {
                                            if (obj == null) {
                                                onErr(null);
                                            }
                                            MyLog.i("onStickerDownloadSucc: download sticker file:" + (String) obj);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    putStickerOnScreen(pictureFile, sei);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onErr(Object obj) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    AppContext.showToast(R.string.cosplay_import_failed);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onNoNetwork() {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    AppContext.showToast(R.string.no_network);
                                                }
                                            });
                                        }
                                    });
                                }
                            } else {
                                putStickerOnScreen(pictureFile, sei);
                            }
                        }
                    }

                }
            }
        }

        // 转改图片加载时间
        if (!StringUtil.isNullOrEmpty(ucid)) {
            long duration = TimeUtils.getCurrentTimeInLong() - mClickTime; //开发者需要自己计算时长
            HashMap<String, String> forward = new HashMap<String, String>();
            forward.put("type", UmengConstants.T_COSPLAY_FORWARD);
            onStatistics(getApplicationContext(), UmengConstants.T_COSPLAY_FORWARD, forward, duration);
        }
    }

    private void selectDefaultUseSitcker() {
        String defaultUse = StickPreference.getInstance().getDefaultUseSticker();
        MyLog.i("defaultUse = " + defaultUse);
        if (!TextUtils.isEmpty(defaultUse)) {
            int position = getDefaultPosition(defaultUse);
            MyLog.i("defaultUse postion = " + position);
            setStickerPackageSelected(position);
        }
    }

    private void setStickerPackageSelected(int position) {
        mEmoticonsPageView.updateView(); // TODO 这句话能否删除
        mEmoticonsPageView.setPageSelect(position);
        mEmoticonsPageView.postInvalidate();
        MyLog.i("call  setNameAndCount from setStickerPackageSelected ");
        setNameAndCount(position);
    }

    /** 获取贴纸包在二级界面的贴纸包list内的position */
    private int getDefaultPosition(String id) {
        if (StringUtil.isNullOrEmpty(id)
                || mEmoticonsPageView.getEmoticonSetBeanList() == null
                || mEmoticonsPageView.getEmoticonSetBeanList().size() == 0) {
            MyLog.i("defaultUse setBeanList = empty ");
            return 0;
        } else {
            List<EmoticonSetBean> setBeanList = mEmoticonsPageView.getEmoticonSetBeanList();
            MyLog.i("defaultUse setBeanList.size = " + setBeanList.size());
            for (int i = 0; i < setBeanList.size(); i++) {
//                MyLog.i("defaultUse 二级["+i+"] = " + setBeanList.get(i).toString());
                if (id.equalsIgnoreCase(setBeanList.get(i).getId())) {
                    return i;
                }
            }
            return 0;
        }
    }

    /** 添加工程文件内的贴纸到舞台 */
    private void putStickerOnScreen(File pictureFile, StickerEditInfo stickerEditInfo) {
        ImageUtils.asyncLoadImage(PhotoProcessActivity.this, Uri.fromFile(pictureFile), new ImageUtils.LoadImageCallback() {
            @Override
            public void callback(Bitmap result) {
                fillBubbleText(stickerEditInfo);

                EmoticonBean bean = StickerUtils.convertStickerEditInfoToEmoticonBean(stickerEditInfo);
                addBeanToRecent(bean);

                Sticker sticker = mStickView.setWaterMark(result, null, stickerEditInfo.isLock(), stickerEditInfo);
                mStickView.reDraw(stickerEditInfo, sticker);
                MyLog.i("setWaterMark from local " + stickerEditInfo.toString());
            }
        });
    }

    /** 如果从贴纸模板过来,文字气泡内应该添加随机的文字 */
    private void fillBubbleText(StickerEditInfo stickerEditInfo) {
        if (!StringUtil.isNullOrEmpty(ucid)) {
            return;
        }
        if (stickerEditInfo == null || !StickerUtils.isTextBubble(stickerEditInfo)) {
            return;
        }

        if (stickerEditInfo.getInfo() != null && StringUtil.isNullOrEmpty(stickerEditInfo.getInfo().getText())) {
            stickerEditInfo.getInfo().setText(StickerUtils.getRandomBubbleText());
//            MyLog.i("如果从贴纸模板过来, 文字气泡内添加个随机文字: " + stickerEditInfo.getInfo().getText());
        }
    }

    private void initView() {
        try {
            ViewGroup.LayoutParams layout = mGPUImageView.getLayoutParams();
            layout.height = SW;

            layout = drawArea.getLayoutParams();
            layout.height = SW;

            layout = mStickView.getLayoutParams();
            layout.height = SW;
        } catch (Exception e) {
            MyLog.e(e);
        }
        // 声音底部按钮点击事件
        voiceBtnArea.setOnClickListener(v -> {
            if (voiceBar.getVisibility() == View.VISIBLE) {
                return;
            }
            setToolBtnBg(TOOBAR_BTN_POS_VOICE, true);

            initVoiceToolBar();
        });
        // 彩色字底部按钮点击事件
        colorTextBtnArea.setOnClickListener(v -> {
            stopPlayAudio();
            if (mLyColorText.getVisibility() == View.VISIBLE) {
                return;
            }
            setToolBtnBg(TOOBAR_BTN_POS_COLORTXT, true);

            initColorText();
        });
        // 最近底部按钮点击事件
        recentBtnArea.setOnClickListener(v -> {
            stopPlayAudio();
            if (mLyRecent.getVisibility() == View.VISIBLE) {
                return;
            }
            setToolBtnBg(TOOBAR_BTN_POS_RECENT, true);

            initRecent(true);
        });
        // 贴纸底部按钮点击事件
        storeBtnArea.setOnClickListener(v -> {
            stopPlayAudio();
            if (mLyStick.getVisibility() == View.VISIBLE && mLyStickPackage.getVisibility() == View.VISIBLE) {
                return;
            }
            setToolBtnBg(TOOBAR_BTN_POS_STICK_PACKAGE, true);
        });

    }

    private void initEvent() {
        if (getIntent() != null) {
            from = getIntent().getStringExtra(DiscoveryConstants.FROM);
            MyLog.i("initIntent from = " + from);
        }

        finishCB = v -> {
            finish();
        };

        title_left.setOnClickListener(v -> {
            DialogHelper dialogHelper = new DialogHelper(PhotoProcessActivity.this);
            dialogHelper.alert4M(null, getString(R.string.cosplay_cancel_alert), getString(R.string.ok), finishCB, getString(R.string.cancle), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogHelper.dialogDismiss();
                }
            }, true);
        });

        title_right.setOnClickListener(v -> {
            savePicture();
        });

        mStickView.setActivity(this);
        // 2.7版本去除[锁定,向上,向下]的功能操作区, 3.1版本再添加回来图层向上向下的功能
        mStickView.setmOnStickMoveClickListener(new StickerView.OnStickMoveClickListener() {
            @Override
            public void onStickMove(boolean isMoving) {
//                layerArea.setVisibility(isMoving ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onLayerEnable(boolean enable) {
                onUpwardEnable(enable);
                onDownwardEnable(enable);
                onStickSelected(enable);
            }

            @Override
            public void onLockEnable(boolean enable) {
            }

            @Override
            public void onUpwardEnable(boolean enable) {
                if (enable) {
                    cosUpper.setImageResource(R.drawable.top_edit);
                } else {
                    cosUpper.setImageResource(R.drawable.top_edit_gray);
                }
            }

            @Override
            public void onDownwardEnable(boolean enable) {
                if (enable) {
                    cosDowner.setImageResource(R.drawable.bottom_edit);
                } else {
                    cosDowner.setImageResource(R.drawable.bottom_edit_gray);
                }
            }

            @Override
            public void onDeleteAudio(boolean enable) {
                if (voiceAdapter != null) {
                    voiceAdapter.deleteSelection();
                }
            }
        });

        // 向上,向下,水平翻转,垂直翻转的功能操作
        cosUpper.setOnClickListener(v -> {
            mStickView.zOrder(true);
        });
        cosDowner.setOnClickListener(v -> {
            mStickView.zOrder(false);
        });
        reverseHorizontal.setOnClickListener(v -> {
            mStickView.doReversalHorizontal(true);
        });
        reverseVertical.setOnClickListener(v -> {
            mStickView.doReversalVertical(true);
        });


        initEmojiKeyboardEvent();
    }

    int outPageViewLastPosition;

    private void initEmojiKeyboardEvent() {
        // 贴纸一级界面的设置
        mEmotPackagesPageView.setOnIndicatorListener(new OnEmoticonsPageViewListener() {
            @Override
            public void emoticonsPageViewInitFinish(int count) {
                mEmotPackagesIndicatorView.init(count);
            }

            @Override
            public void emoticonsPageViewCountChanged(int count) {
                mEmotPackagesIndicatorView.setIndicatorCount(count);
            }

            @Override
            public void playTo(int position) {
                mEmotPackagesIndicatorView.playTo(position);
            }

            @Override
            public void playBy(int oldPosition, int newPosition) {
                try {
                    mEmotPackagesIndicatorView.playBy(oldPosition, newPosition);
                    outPageViewLastPosition = newPosition;
                } catch (Exception e) {
                    MyLog.e(e);
                }
            }
        });


        mEmotPackagesPageView.resetIViewListener(new IView() {
            @Override
            public void onItemClick(EmoticonBean bean) {
                // TODO 判断是点击了1相机按钮还是 2话题贴纸包 又或者3普通贴纸包
                if (bean == null) {
                    UIHelper.showStickerCenter(PhotoProcessActivity.this, ClientInfo.getUID(), 1, 0);
                } else {
                    setStickerPackageSelected(getDefaultPosition(bean == null ? null : bean.getParentId()));

                    toggleStickPackItem(false);
                }
            }

            @Override
            public boolean onItemLongClick(int position, View converView, EmoticonBean bean) {
                if (bean != null && bean.getEventType() == EmoticonBean.FACE_TYPE_DEL) {
                    return true;
                } else if (bean != null && bean.getEventType() == EmoticonBean.FACE_TYPE_USERDEF) {
                    return false;
                }
                return false;
            }

            @Override
            public void onItemDisplay(EmoticonBean bean) {
            }

            @Override
            public void onPageChangeTo(int position) {
            }
        });

        // 贴纸二级界面的设置
        mEmoticonsPageView.setOnIndicatorListener(new OnEmoticonsPageViewListener() {
            @Override
            public void emoticonsPageViewInitFinish(int count) {
                mEmoticonsIndicatorView.init(count);
            }

            @Override
            public void emoticonsPageViewCountChanged(int count) {
                mEmoticonsIndicatorView.setIndicatorCount(count);
            }

            @Override
            public void playTo(int position) {
                mEmoticonsIndicatorView.playTo(position);
            }

            @Override
            public void playBy(int oldPosition, int newPosition) {
                try {
                    mEmoticonsIndicatorView.playBy(oldPosition, newPosition);
                } catch (Exception e) {
                    MyLog.e(e);
                }
            }
        });

        mEmoticonsPageView.resetIViewListener(new IView() {
            @Override
            public void onItemClick(EmoticonBean bean) {
                if (bean != null) {
                    addStickToStickView(bean, bean.getParentStickType() == StickerConstants.STICKER_TYPE_TOPIC ? false : true);
                }
            }

            @Override
            public boolean onItemLongClick(int position, View converView, EmoticonBean bean) {
                if (bean != null && bean.getEventType() == EmoticonBean.FACE_TYPE_DEL) {
                    return true;
                } else if (bean != null && bean.getEventType() == EmoticonBean.FACE_TYPE_USERDEF) {
                    return false;
                }
                return false;
            }

            @Override
            public void onItemDisplay(EmoticonBean bean) {
            }

            @Override
            public void onPageChangeTo(int position) {
                scrollOutPageView(position);
            }
        });

        stickerBack.setOnClickListener(v -> {
            toggleStickPackItem(true);
        });
    }

    // 判断以及界面有没有滑动到别的page
    private void scrollOutPageView(int position) {
        int outPagePOS = position / 8;
        MyLog.i("二级 onPageChangeTo " + position + ", outPagePOS=" + outPagePOS + ", outPageViewLastPosition=" + outPageViewLastPosition);
        if (outPagePOS != outPageViewLastPosition) {
            mEmotPackagesPageView.setPageSelect(outPagePOS);
            mEmotPackagesIndicatorView.playBy(outPageViewLastPosition, outPagePOS);
            outPageViewLastPosition = outPagePOS;
        }

        setNameAndCount(position);
    }

    /** 切换贴纸包一级二级界面显示 */
    private void toggleStickPackItem(boolean showPackage) {
        if (showPackage) {
            mLyStickPackage.setVisibility(View.VISIBLE);
            mLyStickItem.setVisibility(View.GONE);
        } else {
            mLyStickItem.setVisibility(View.VISIBLE);
            mLyStickPackage.setVisibility(View.GONE);
        }
    }

    /**
     * 设置底部工具区布局以及按钮的背景色
     * @param pos 底部按钮的位置 滤镜按钮的位置是0; 声音按钮的位置是1; 彩色字按钮的位置是2; 最近按钮的位置是4;
     * */
    private void setToolBtnBg(int pos, boolean selected) {
        voiceBar.setVisibility(View.GONE);
        mLyColorText.setVisibility(View.GONE);
        mLyStick.setVisibility(View.GONE);
        mLyRecent.setVisibility(View.GONE);


        voiceImageBar.setImageResource(R.drawable.dktoolbar_voice);
        colorTextImageBar.setImageResource(R.drawable.dktoolbar_color_text);
        storeImageBar.setImageResource(R.drawable.dktoolbar_store);
        recentImageBar.setImageResource(R.drawable.dktoolbar_recently);

        voiceTvBar.setTextColor(getResources().getColor(R.color.gray));
        colorTextTvBar.setTextColor(getResources().getColor(R.color.gray));
        storeTvBar.setTextColor(getResources().getColor(R.color.gray));
        recentTvBar.setTextColor(getResources().getColor(R.color.gray));

        colorTextBtnArea.setBackgroundColor(getResources().getColor(com.keyboard.view.R.color.white));
        storeBtnArea.setBackgroundColor(getResources().getColor(com.keyboard.view.R.color.white));
        voiceBtnArea.setBackgroundColor(getResources().getColor(com.keyboard.view.R.color.white));
        recentBtnArea.setBackgroundColor(getResources().getColor(com.keyboard.view.R.color.white));

        switch (pos) {
            case TOOBAR_BTN_POS_VOICE:
                if (selected) {
                    voiceBar.setVisibility(View.VISIBLE);
                    voiceBtnArea.setBackgroundColor(getResources().getColor(com.keyboard.view.R.color.list_item_background_pressed));
                    voiceImageBar.setImageResource(R.drawable.voice_black_dktoolbar);
                    voiceTvBar.setTextColor(getResources().getColor(R.color.common_title_grey));
                }
                break;
            case TOOBAR_BTN_POS_COLORTXT:
                if (selected) {
                    mLyColorText.setVisibility(View.VISIBLE);
                    colorTextBtnArea.setBackgroundColor(getResources().getColor(com.keyboard.view.R.color.list_item_background_pressed));
                    colorTextImageBar.setImageResource(R.drawable.font_black_dktoolbar);
                    colorTextTvBar.setTextColor(getResources().getColor(R.color.common_title_grey));
                }
                break;
            case TOOBAR_BTN_POS_RECENT:
                if (selected) {
                    mLyRecent.setVisibility(View.VISIBLE);
                    recentBtnArea.setBackgroundColor(getResources().getColor(com.keyboard.view.R.color.list_item_background_pressed));
                    recentImageBar.setImageResource(R.drawable.lately_black_dktoolbar);
                    recentTvBar.setTextColor(getResources().getColor(R.color.common_title_grey));
                }
                break;
            case TOOBAR_BTN_POS_STICK_PACKAGE:
            default:
                if (selected) {
                    storeBtnArea.setBackgroundColor(getResources().getColor(com.keyboard.view.R.color.list_item_background_pressed));
                    storeImageBar.setImageResource(R.drawable.sticker_black_dktoolbar);
                    storeTvBar.setTextColor(getResources().getColor(R.color.common_title_grey));
                }
                mLyStick.setVisibility(View.VISIBLE);
                toggleStickPackItem(true);
                break;
        }
    }

    /**
     * 贴纸是否选中时操作按钮的显示
     *
     * @param selected true:选中贴纸
     */
    private void onStickSelected(boolean selected) {
        title_op_btns.setVisibility(selected ? View.VISIBLE : View.GONE);
        reverseHorizontal.setVisibility(View.VISIBLE);
        reverseVertical.setVisibility(View.VISIBLE);
        cosUpper.setVisibility(View.VISIBLE);
        cosDowner.setVisibility(View.VISIBLE);

        colorTextColorTitle.setTextColor(getResources().getColor(R.color.color_text_color_alpha1));

        if (selected) {
            int stickerType = mStickView.getStickers().get(mStickView.getFocusStickerPosition()).getStickerType();
            if (((ColorTextColorAdapter) colorTextColorBar.getAdapter()) != null) {
                ((ColorTextColorAdapter) colorTextColorBar.getAdapter()).setIsColorTextStickerSelected(StickerUtils.isColorText(stickerType));
            }
            if (StickerUtils.isColorText(stickerType)) {
                colorTextColorTitle.setTextColor(getResources().getColor(R.color.common_title_grey));

                // 彩色文字只显示图层操作按钮
                reverseHorizontal.setVisibility(View.GONE);
                reverseVertical.setVisibility(View.GONE);
            } else if (StickerUtils.isVoice(stickerType)) {
                // 音频只显示水平翻转操作按钮
                reverseVertical.setVisibility(View.GONE);
                cosUpper.setVisibility(View.GONE);
                cosDowner.setVisibility(View.GONE);
            }
        } else {
            if (((ColorTextColorAdapter) colorTextColorBar.getAdapter()) != null) {
                ((ColorTextColorAdapter) colorTextColorBar.getAdapter()).setIsColorTextStickerSelected(false);
            }
        }
    }

    /** 点击底部贴纸,添加到舞台
     * @param bean 贴纸
     * @param refresh 是否刷新当前界面
     *  */
    private void addStickToStickView(EmoticonBean bean, boolean refresh) {
        if (bean == null) {
            return;
        }
        if (bean.getEventType() == EmoticonBean.FACE_TYPE_USERDEF) {
            String picPath = null;
            boolean picExist = false;
            Bitmap picBmp = null;
            if (!StringUtil.isNullOrEmpty(bean.getGifUri())) {
                picPath = bean.getGifUri().substring("file://".length());
                picExist = new File(picPath).exists();
                if (picExist) {
                    picBmp = ImageLoader.getInstance().loadImageSync(bean.getGifUri(), BitmapUtil.getImageLoaderOption());
                }
            }

            if (picBmp == null) {
                MyLog.w("贴纸decode failed:" + bean.toString());
                String newUrl = StickerUtils.getSingleStickerPictureUrl(bean.getGifUrl());
                downloadSticker(newUrl, picPath, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        if (obj == null) {
                            onErr(null);
                        }
                        MyLog.i("onStickerDownloadSucc: download sticker succ:" + (String) obj);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 下载了这个贴纸后,刷新二级界面吧.
                                MyLog.i("下载了这个贴纸后,刷新二级界面吧." + bean.toString());
                                mEmoticonsPageView.refresh();
                                // TODO UMA0040:java.lang.OutOfMemoryError
                                mStickView.setWaterMark(BitmapFactory.decodeFile((String) obj), null, false, bean);
                            }
                        });
                    }

                    @Override
                    public void onErr(Object obj) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AppContext.showToast(R.string.download_fail);
                            }
                        });
                    }

                    @Override
                    public void onNoNetwork() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AppContext.showToast(R.string.no_network);
                            }
                        });
                    }

                });
            } else {
                mStickView.setWaterMark(picBmp, null, false, bean);
            }

            onClickTopicStick(bean);

            // TODO 如果是彩色文字就不用加入到最近中去了
            addBeanToRecent(bean);
        }
    }

    /** 如果是点击了话题里面的贴纸,需要判断是否将对应的商城内的贴纸包下载下来 */
    private void onClickTopicStick(EmoticonBean bean) {
        if (bean == null || bean.getParentStickType() != StickerConstants.STICKER_TYPE_TOPIC) return;
        // 下载贴纸所对应的贴纸包去咯--start
        showProgressDialog("加载中...");
        // 如果点击的话题内的贴纸,获取贴纸包时,不要传 parentID
        StickerManager.getInstance().downloadSticker(null, bean.getStickerId(), 1, new IListener() {

            @Override
            public void onSuccess(Object obj) {
                MyLog.i("onClickTopicStick downloadSticker onSuccess");
                if (obj != null) {
                    StickerPackage sticker = (StickerPackage) obj;
                    MyLog.i("onClickTopicStick " + sticker.toString());
                    if (sticker.getStickers() != null && !sticker.getStickers().isEmpty()) {
                        for (int i = 0; i < sticker.getStickers().size(); i++) {
                            MyLog.i(i + ", " + sticker.getStickers().get(i).toString());
                        }
                    }

                    // 判断这个贴纸包是否需要下载
                    if (bean.getParentId().equals(sticker.getStickerPackageId())) {
                        MyLog.i("bean 's parentID equals server stickerPackage ID no NEED to download~~");
                        dismissProgressDialog();
                    } else {
                        MyLog.i("is server stickerPackage " + sticker.getStickerPackageId() + " in DB ?? ");
                        // 判断本地有无此贴纸包
                        if (StickerManager.getInstance().isDownloaded(sticker.getStickerPackageId())) {
                            MyLog.i("bean 's server StickerPackage " + sticker.getStickerPackageId() + " is in DB");
                            dismissProgressDialog();
                        } else {
                            // 未下载时下载之
                            MyLog.i("bean 's server StickerPackage " + sticker.getStickerPackageId() + " is NOT in DB");
                            // 没有的时候,需要下载的
                            StickerManager.getInstance().downloadStickerIconToLocal(sticker, new Callback() {
                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onFinish(int result) {
                                    MyLog.i("bean 's server StickerPackage " + sticker.getStickerPackageId() + " download onFinish result=" + result);
                                    if (result == 1) {
                                        MyLog.i("bean 's server StickerPackage download SUCC " + (result == 1));
                                        // 下载成功后需要刷新贴纸一级和二级界面
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                initMajorStickPackagesArea();
                                                MyLog.i("bean 's server StickerPackage download SUCC initMajorStickPackagesArea 下载成功后需要刷新贴纸一级和二级界面");
                                                updateStickersArea(0);
                                                MyLog.i("bean 's server StickerPackage download SUCC refresh MajorStickPackagesArea");
                                                dismissProgressDialog();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                } else {
                    MyLog.i("onClickTopicStick downloadSticker onSuccess but obj is null");
                    dismissProgressDialog();
                }
            }

            @Override
            public void onErr(Object obj) {
                MyLog.i("onClickTopicStick downloadSticker onErr " + obj);
                dismissProgressDialog();
            }

            @Override
            public void onNoNetwork() {
                MyLog.i("onClickTopicStick downloadSticker onNoNetwork");
                dismissProgressDialog();
            }
        });
    }

    /**
     * 添加底部或工程文件的贴纸到最近贴纸
     */
    private void addBeanToRecent(EmoticonBean bean) {
        if (bean == null) {
            return;
        }
        checkIcon(bean);

        if (mRecentSet == null) {
//            MyLog.i("abcd 首次点击贴纸");
            mRecentSet = new EmoticonSetBean();
        }
        mRecentSet.setName(getString(R.string.recent));
        mRecentSet.setStickType(StickerConstants.STICKER_TYPE_RECENTLY);
        mRecentSet.setUid(DefEmoticons.DEFAULT_EMOJISET_UID);
        mRecentSet.setLine(2);
        mRecentSet.setRow(4);
        mRecentSet.setOrder(1000);
        mRecentSet.setItemPadding(15);
        mRecentSet.setVerticalSpacing(5);
        ArrayList<EmoticonBean> list = null;
        if (mRecentSet.getEmoticonList() == null || mRecentSet.getEmoticonList().isEmpty()) {
            mRecentSet.setEmoticonList(list = new ArrayList<EmoticonBean>());
//            MyLog.i("abcd 首次点击贴纸,new list");
        } else {
            // 将已有的存储到第一个
            list = mRecentSet.getEmoticonList();
            for (int i = 0; i < list.size(); i++) {
                EmoticonBean tmp = list.get(i);
                if (tmp == null || tmp.getStickerId() == null) continue;
//                MyLog.i(i+":最近贴纸:" + tmp);
                if (tmp.getStickerId().equals(bean.getStickerId())) {
                    list.remove(i);
//                    MyLog.i(i + ":将这个贴纸移到第一个去:" + tmp);
//                    将这个贴纸移到第一个去
                    break;
                }
            }
        }
        bean.setParentStickType(StickerConstants.STICKER_TYPE_RECENTLY);
        list.add(0, bean);
        // 超过 48 个就删除
        while (list.size() > StickerConstants.RECENTLY_STICKER_MAX_COUNT) {
            list.remove(StickerConstants.RECENTLY_STICKER_MAX_COUNT);
        }
        mRecentSet.setEmoticonList(list);
//        MyLog.i("abcd 最近贴纸有~~  " + list.size());
        // 更新最近贴纸的DB
        setRecentStick();
    }

    /**最近历史的贴纸如果没下载,则下载*/
    private void checkIcon(EmoticonBean bean) {
        if (bean == null || StringUtil.isNullOrEmpty(bean.getIconUri()) || StringUtil.isNullOrEmpty(bean.getIconUrl())) {
            return;
        }
        StickerInfo stickerInfo = new StickerInfo();
        BaseImage icon = new BaseImage();
        icon.setUri(bean.getIconUrl());
        stickerInfo.setIcon(icon);
        stickerInfo.setStickerId(bean.getStickerId());
        stickerInfo.setId(Integer.parseInt(bean.getId()));
        stickerInfo.setParentid(bean.getParentId());
        new Thread(new Runnable() {
            @Override
            public void run() {
                StickerManager.getInstance().downloadSingleStickerIcon(stickerInfo);
            }
        }).start();
    }

    /**
     * 添加server获取的历史最近贴纸
     */
    private void addBeanToRecent(EmoticonSetBean bean) {
        if (bean == null || bean.getEmoticonList() == null || bean.getEmoticonList().isEmpty()) {
            return;
        }
        if (mRecentSet == null || mRecentSet.getEmoticonList() == null || mRecentSet.getEmoticonList().isEmpty()) {
            mRecentSet = bean;
//            MyLog.i("abcd server 的最近贴纸有~~  " + mRecentSet.toString());
        } else {
            // 虽然这快走不到~
            ArrayList<EmoticonBean> current = mRecentSet.getEmoticonList();
            ArrayList<EmoticonBean> got = bean.getEmoticonList();
//            MyLog.i("abcd current 的最近贴纸1有~~  " + current.size());
//            MyLog.i("abcd server 的最近贴纸有~~  " + got.size());
            for (EmoticonBean c : current) {
                if (!got.isEmpty()) {
                    for (int i = got.size() - 1; i > -1; i--) {
                        EmoticonBean g = got.get(i);
                        if (g.getStickerId().equals(c.getStickerId())) {
                            got.remove(i);
                        }
                    }
                } else {
                    break;
                }
            }
            current.addAll(got);
//            MyLog.i("abcd current 的最近贴纸2有~~  " + current.size());
//            MyLog.i("abcd server 的最近贴纸2有~~  " + got.size());
            mRecentSet.setEmoticonList(current);
        }

        ArrayList<EmoticonBean> recentList = mRecentSet.getEmoticonList();
        for (EmoticonBean item : recentList) {
            checkIcon(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.PHOTO_PROCESS_ACTIVITY);
        MobclickAgent.onResume(this);

        // 提前准备好popWindow.防止show时activity未createOK或者已经退出.
        if (refresh_sticker_package_popupWindow == null) {
            initRefreshPopWindow();
        }

        // 添加单张贴纸逻辑
        addSingleSticker();

        // 贴纸包有增加或者删除,排序等
        handleSPChanged();

        // 如果外面选中了默认的贴纸包
        selectDefaultUseSitcker();

        // 判断是否需要重新加载贴纸包
        handleReloadStickers();
    }

    /** 贴纸包有增加[stickerMallPackageId] 或者删除,排序[isNeedRefresh]等UI的变化 */
    private void handleSPChanged() {

        String stickerMallPackageId = StickPreference.getInstance().getStickerMallPackageId();
        boolean isNeedRefresh = MinePreference.getInstance().isNeedRefreshPhotoEdit();
        MyLog.i("handleSPChanged ~~~~~~  stickerMallPackageId=" + stickerMallPackageId + ", isNeedRefresh is " + isNeedRefresh);
        if (isNeedRefresh || !StringUtil.isNullOrEmpty(stickerMallPackageId)) {
            StickPreference.getInstance().setStickerMallPackageId("");
            MinePreference.getInstance().setNeedRefreshPhotoEdit(false);

            initMajorStickPackagesArea();

            int pos = getDefaultPosition(stickerMallPackageId);
            // 如果使用的贴纸所在的包 真的在底部工具二级包内,才切换到二级界面,否则还是在一级界面吧
            if (mEmoticonsPageView.getEmoticonSetBeanList() != null && mEmoticonsPageView.getEmoticonSetBeanList().size() > pos) {
                String PID = mEmoticonsPageView.getEmoticonSetBeanList().get(pos) != null ? mEmoticonsPageView.getEmoticonSetBeanList().get(pos).getId() : null;
                if (stickerMallPackageId.equals(PID)) {
                    scrollOutPageView(pos);
                    toggleStickPackItem(false);
                } else {
                    toggleStickPackItem(true);
                }
            }
        }


    }

    /**
     * 添加单张贴纸逻辑
     */
    private void addSingleSticker() {
        if (!StickerManager.getInstance().hasUsingSticker()) {
            MyLog.i("single no single sticker");
            return;
        }

        StickerInfo singleInfo = StickerManager.getInstance().getUseSingleStickerInfo(PhotoProcessActivity.this);
        if (singleInfo == null) {
            MyLog.i("single no single stickerInfo");
            return;
        }
        MyLog.i("single single singleInfo = " + singleInfo.toString());
        EmoticonBean singleBean = StickerUtils.convertStickerInfoToEmoticonBean(100, null, singleInfo);
//        singleBean = new EmoticonBean("-1", "563b0f510cf2361f2e3dd1ef", "566aa3d80cf2f7356a27072e", 1, 1,
//                "file:///storage/emulated/0/MOIN/.StickRes//566aa3d80cf2f7356a27072e.i", "http://prdimg.mo-image.com/image/a8e/ce2/c498d6482fa8ee9f5749e8ce262ded15.png",
//                "file:///storage/emulated/0/MOIN/.StickRes//566aa3d80cf2f7356a27072e.s", "http://prdimg.mo-image.com/image/2f3/b9d/17a95163d62f3a14eb3043b9d903f5d3.png",
//                "", 0, 2, 0, "", "");
        singleBean.setParentStickType(1);
        addStickToStickView(singleBean, true);
        StickerManager.getInstance().clearUseSingleSticker();

        // 如有使用单张贴纸时,则不进入声音,进入贴纸tab
        setToolBtnBg(TOOBAR_BTN_POS_STICK_PACKAGE, true);
    }

    private void updateStickersArea(int position) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EmoticonsKeyboardBuilder builder = buildMyStickers();
                mEmoticonsPageView.setBuilder(builder);
                mEmoticonsPageView.updateSelf(EMOJI_PAGE_HEIGHT);
/*                mEmoticonsPageView.updateSelf(AppContext.getApp().getScreenHeight()
                                - SW
                                - getResources().getDimensionPixelSize(R.dimen.height_100px)
                                //- getResources().getDimensionPixelSize(R.dimen.bar_height) * 2
                                - getResources().getDimensionPixelSize(R.dimen.bar_big_height) * 2
                );
*/
                // TODO 在编辑文字的时候,应该把当时的position传过去.如果在气泡页滑动到别的页面时,应该也加上.那么此处应该不需要设置setPageSelect(0)

                setStickerPackageSelected(position);

//                mEmoticonsPageView.updateView();
//                mEmoticonsPageView.setPageSelect(position, playBy);
//                mEmoticonsToolBarView.setToolBtnSelect(position);

//                mEmoticonsToolBarView.onToolBtnSelect(position);

                MyLog.i("updateStickersArea 刷新下面的贴纸栏view " + position);
//                setNameAndCount(position);

            }
        });
    }

    /** 构造包括临时话题贴纸包在内的贴纸包 */
    private EmoticonsKeyboardBuilder buildMyStickers() {
        EmoticonsKeyboardBuilder builder = EmoticonsUtils.getBuilder(PhotoProcessActivity.this);
        MyLog.i("buildMyStickers db emojiset size1 = " + (builder.builder.getEmoticonSetBeanList() != null ? builder.builder.getEmoticonSetBeanList().size() : 0));

        // 有话题贴纸包,则添加进去
        EmoticonSetBean mTopicEmjSet = null;
        String topicName = StickPreference.getInstance().getJoinTopicName();
        MyLog.i("话题,发图的话题id="
                        + StickPreference.getInstance().getJoinTopicID()
                        + ", name=" + StickPreference.getInstance().getJoinTopicName()
        );

        // 只有登录的时候才能用,否则进了话题后,没完成发布什么的,退出. 再从普通方式进入的时候就会拿到错误的话题了
        if (!StringUtil.isNullOrEmpty(topicName) && AppContext.getInstance().isLogin()) {

            String topicStickerpackageId = StickPreference.getInstance().getJoinTopicStickerpackageId();
            MyLog.i("buildMyStickers topic emojiset " + topicName +", id=" + topicStickerpackageId);

            if (!StringUtil.isNullOrEmpty(topicStickerpackageId)) {

                StickerPackage mStickerPackage = (StickerPackage) CacheManager.readObject(PhotoProcessActivity.this,
                        StickerConstants.STICKER_DETAIL_CACHE_PREFIX + topicStickerpackageId);

                mTopicEmjSet = StickerManager.getInstance().convertStickerPackageToEmoticonSetBean(StickerConstants.STICKER_TYPE_TOPIC, mStickerPackage,
                        null, StickerUtils.getStickePackagerIconPath(mStickerPackage.getStickerPackageId()));

//                if (mStickerPackage != null) {
//                    MyLog.i("topic sp=" + mStickerPackage.toString());
//                    if (mStickerPackage.getStickers() != null && !mStickerPackage.getStickers().isEmpty()) {
//                        for (int i = 0; i < mStickerPackage.getStickers().size(); i++) {
//                            MyLog.i(i+", " + mStickerPackage.getStickers().get(i).toString());
//                        }
//                    }
//                }
//                MyLog.i("buildMyStickers topic emojiset prj is null ? " + (mStickerPackage == null) +", mTopicEmjSet is null=" + (mTopicEmjSet == null));

                if (mTopicEmjSet != null) {
                    mTopicEmjSet.setStickType(StickerConstants.STICKER_TYPE_TOPIC);
                    mTopicEmjSet.setName(topicName);
                }
            }
        }
        if (mTopicEmjSet != null) {
            ArrayList<EmoticonSetBean> list = builder.builder.getEmoticonSetBeanList();
            list.add(0, mTopicEmjSet);
            builder.builder.setEmoticonSetBeanList(list);
//            MyLog.i("buildMyStickers has topic emojiset " + mTopicEmjSet.getName() + ", type=" + mTopicEmjSet.getStickType());
            MyLog.i("buildMyStickers db emojiset size2 = " + (builder.builder.getEmoticonSetBeanList() != null ? builder.builder.getEmoticonSetBeanList().size() : 0));
//
//
//            MyLog.i("topic sp2=" + mTopicEmjSet.toString());
//            if (mTopicEmjSet.getEmoticonList() != null && !mTopicEmjSet.getEmoticonList().isEmpty()) {
//                for (int i = 0; i < mTopicEmjSet.getEmoticonList().size(); i++) {
//                    MyLog.i(i+", " + mTopicEmjSet.getEmoticonList().get(i).toString());
//                }
//            }
        } else {
            MyLog.i("buildMyStickers has NO topic emojiset ");
        }

        return builder;
    }

    /** 判断是否有话题临时贴纸包 */
    public boolean hasTopicEmojiSet() {
        if (mEmoticonsPageView == null || mEmoticonsPageView.getEmoticonSetBeanList() == null) {
            return false;
        }
        int size = 2 > mEmoticonsPageView.getEmoticonSetBeanList().size() ? mEmoticonsPageView.getEmoticonSetBeanList().size() : 2;
        for (int i = 0; i < size; i++) {
            EmoticonSetBean setBean = mEmoticonsPageView.getEmoticonSetBeanList().get(i);
            if (setBean != null && StickerConstants.STICKER_TYPE_TOPIC == setBean.getStickType()) {
                return true;
            }
        }
        return false;
    }

    private void setNameAndCount(int position) {
        MyLog.i("setNameAndCount   下面的贴纸栏pos = " + position);
        lastPosition = position;
        String name = "";
        try {
            if (position >= 0 && position < mEmoticonsPageView.getEmoticonSetBeanList().size()) {
                MyLog.i("setNameAndCount setListsize=" + mEmoticonsPageView.getEmoticonSetBeanList().size());
                name = mEmoticonsPageView.getEmoticonSetBeanList().get(position).getName();
                if (name.equals(getString(R.string.more_cosplay))) {
                    name = mEmoticonsPageView.getEmoticonSetBeanList().get(position + 1).getName();
                }
            }
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            if (name.equals(getString(R.string.more_cosplay))) {
                name = "";
            }
            stickerTitle.setText(name);
        }
    }

    /**
     * 保存图片
     */
    private void savePicture() {
        final Bitmap xgtBitmap = Bitmap.createBitmap(SW, SW,
                Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(xgtBitmap);
        RectF dst = new RectF(0, 0, SW, SW);
        if (currentBitmap != null) {
            // fix UMA0021:java.lang.NullPointerException
            cv.drawBitmap(currentBitmap, null, dst, null);
        }

        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();
/*

        // 如果有彩色文字,则将之融入到底图内
        if (mStickView.isColorTxtOrVoiceStickerExist()) {
            final Bitmap xgtPlusBitmap = Bitmap.createBitmap(xgtBitmap);
            Canvas cvPlus = new Canvas(xgtPlusBitmap);
            for (Sticker sticker : mStickView.getStickers()) {
                if (StickerUtils.isColorText(sticker)) {
                    cvPlus.drawBitmap(sticker.getBitmap(), sticker.getmMatrix(), null);
                }
            }
            cvPlus.save(Canvas.ALL_SAVE_FLAG);
            cvPlus.restore();

            bgPlus = FileUtil.getInst().getCacheDir() + "/bgUriPluscache";
            try {
                StickerImgUtils.writeFile(bgPlus, BitmapUtil.bitmapToBytes(xgtPlusBitmap));
            } catch (IOException e) {
                MyLog.e(e);
                bgPlus = bgUri.getPath();
            }
        }
*/

        //加贴纸水印
//        EffectUtil.applyOnSave(cv, mImageView);
        for (Sticker sticker : mStickView.getStickers()) {
            if (StickerUtils.isVoice(sticker)) {
                cv.drawBitmap(StickerUtils.getAudioBitmap(sticker.getBitmap(),sticker.getAudioLength(),
                        sticker.isMirrorH()),sticker.getmMatrix(), null);
            } else {
                cv.drawBitmap(sticker.getBitmap(), sticker.getmMatrix(), null);
            }
            if (StickerUtils.isTextBubble(sticker)) {
                StickerUtils.drawStickText(cv, sticker);
            }
        }
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();

        SavePicToFileTask saveTask = new SavePicToFileTask();
        saveTask.setOnCallback(new OnCallback() {
            @Override
            public void onSuccess(Object xgt) {
                MyLog.i("保存效果图成功 @ " + TimeUtils.getCurrentTimeInString());
                if (StringUtil.isFromPersonalInfo(from)) {
                    com.moinapp.wuliao.commons.eventbus.EventBus.getDefault().post(new AvatarPath((String) xgt));
                    CameraManager.getInst().close();
                } else {
                    encodeStickerProject((String) xgt);
//                    toast("保存滤镜成功:"+filterName, 0);
                }
            }

            @Override
            public void onFailed(Object object) {
                MyLog.i("保存效果图失败 @ " + TimeUtils.getCurrentTimeInString());
            }
        });

        saveTask.execute(xgtBitmap);
    }

    /** 个人信息界面制作大咖秀来作为头像用的类 */
    public static class AvatarPath {
        private String path;

        public AvatarPath(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    /** 工程文件加密,成功后进入发布界面 */
    private void encodeStickerProject(String xgt) {
        EncodeTask encodeTask = new EncodeTask();
        encodeTask.setOnCallback(new OnCallback() {
            @Override
            public void onSuccess(Object gcwj) {
                MyLog.i("加密工程文件成功 @ " + TimeUtils.getCurrentTimeInString());
                Intent newIntent = new Intent(PhotoProcessActivity.this, PhotoReleaseActivity.class);
                newIntent.putExtra(PhotoReleaseActivity.KEY_XGT, xgt);
                newIntent.putExtra(PhotoReleaseActivity.KEY_GCWJ, (String) gcwj);
                newIntent.putStringArrayListExtra(PhotoReleaseActivity.KEY_STICKER_IDS, getStickIDlist());
                newIntent.putExtra(PhotoReleaseActivity.KEY_STICKER_ID_STRING, getStickIDsForRecommendWord());
                newIntent.putExtra(PhotoReleaseActivity.KEY_PARENT_WRITE_AUTH, wauth);
                if (!StringUtil.isNullOrEmpty(ucid)) {
                    newIntent.putExtra(PhotoReleaseActivity.KEY_PARENT_UCID, ucid);
                }
                if (mAudioInfo != null && mAudioInfo.size() > 0) {
                    newIntent.putExtra(PhotoReleaseActivity.KEY_AUDIO_LIST, mAudioInfo);
                }
                startActivity(newIntent);
            }

            @Override
            public void onFailed(Object object) {
                MyLog.e("加密工程文件失败 @ " + TimeUtils.getCurrentTimeInString());
            }
        });

        encodeTask.execute();
    }

    /** 构造工程文件中贴纸信息 从最底层开始写入 */
    private List<StickerEditInfo> getStickEditInfo() {
        List<StickerEditInfo> stickerlist = new ArrayList<>();
        List<Sticker> stickers = mStickView.getStickers();
        if (stickers == null || stickers.isEmpty())
            return stickerlist;
        int i = 1;
        for (Sticker sticker : stickers) {
            /*
            // 彩色贴纸,不进入工程文件.但是音频需要[转改用]
            if (sticker == null || StickerUtils.isColorText(sticker.getStickerType()))
                continue;
            */

            MyLog.i("第" + i + "个sticker贴纸信息是：" + sticker.toString());
            StickerEditInfo editInfo = convertStickerToEditInfo(sticker);
            if (editInfo != null) {
                stickerlist.add(editInfo);
                MyLog.i("第" + i + "个贴纸信息是：" + editInfo.toString());
                i++;
            }
        }
        return stickerlist;
    }

    /** 构造工程文件中贴纸信息 贴纸信息转换为工程文件用信息 */
    private StickerEditInfo convertStickerToEditInfo(Sticker sticker) {
        if (sticker == null)
            return null;
        StickerEditInfo stickerEditInfo = new StickerEditInfo();
        if (StickerUtils.isVoice(sticker.getStickerType())) {
            StickerAudioInfo audioInfo = new StickerAudioInfo();
            audioInfo.setAudioId(sticker.getStickerId());
            audioInfo.setLength(sticker.getAudioLength());
            audioInfo.setUri(sticker.getAudioUri());
            audioInfo.setX(getWanfenbi(sticker.getMapPointsDst()[8]));
            audioInfo.setY(getWanfenbi(sticker.getMapPointsDst()[9]));
            audioInfo.setWidth(getWanfenbi(sticker.getBitmap().getWidth() * sticker.getScaleSize()));
            audioInfo.setHeight(getWanfenbi(sticker.getBitmap().getHeight() * sticker.getScaleSize()));
            audioInfo.setMirrorH(sticker.isMirrorH());
            stickerEditInfo.setAudio(audioInfo);

            mAudioInfo.add(audioInfo);
        } else {
            stickerEditInfo.setId(Integer.parseInt(StringUtil.isNullOrEmpty(sticker.getId()) ? "0" : sticker.getId()));
            stickerEditInfo.setParentid(sticker.getParentId());
            stickerEditInfo.setStickerId(sticker.getStickerId());
            BaseImage picture = new BaseImage();
            picture.setUri(sticker.getPicUrl());
            stickerEditInfo.setPicture(picture);
        }
        stickerEditInfo.setX(getWanfenbi(sticker.getMapPointsDst()[8]));
        stickerEditInfo.setY(getWanfenbi(sticker.getMapPointsDst()[9]));
//        MyLog.i("贴纸宽高和中心坐标是：" + Point2D.distance(sticker.getMapPointsDst()[0], sticker.getMapPointsDst()[1], sticker.getMapPointsDst()[2], sticker.getMapPointsDst()[3])
//                + " ,中心= " + sticker.getMapPointsDst()[8] + "*" + sticker.getMapPointsDst()[9]);
//
//        MyLog.i("贴纸bmp宽高是:" + sticker.getBitmap().getWidth() + "*" + sticker.getBitmap().getHeight());
//        MyLog.i("贴纸缩放比例是:" + sticker.getScaleSize());
        double widthBmp = sticker.getBitmap().getWidth() * sticker.getScaleSize();
        double widthPoint = Point2D.distance(sticker.getMapPointsDst()[0], sticker.getMapPointsDst()[1], sticker.getMapPointsDst()[2], sticker.getMapPointsDst()[3]);
        double heightBmp = sticker.getBitmap().getHeight() * sticker.getScaleSize();
        double heightPoint = Point2D.distance(sticker.getMapPointsDst()[4], sticker.getMapPointsDst()[5], sticker.getMapPointsDst()[2], sticker.getMapPointsDst()[3]);

        stickerEditInfo.setWidth(getWanfenbi(Math.max(widthBmp, widthPoint)));
        stickerEditInfo.setHeight(getWanfenbi(Math.max(heightBmp, heightPoint)));
//        stickerEditInfo.setWidth(getWanfenbi(Point2D.distance(sticker.getMapPointsDst()[0], sticker.getMapPointsDst()[1], sticker.getMapPointsDst()[2], sticker.getMapPointsDst()[3])));
//        stickerEditInfo.setHeight(getWanfenbi(Point2D.distance(sticker.getMapPointsDst()[4], sticker.getMapPointsDst()[5], sticker.getMapPointsDst()[2], sticker.getMapPointsDst()[3])));
//        stickerEditInfo.setRotaion((int) Point2D.angleBetweenPoints(sticker.getMapPointsDst()[0], sticker.getMapPointsDst()[1], sticker.getMapPointsDst()[2], sticker.getMapPointsDst()[3], 0.0F));
        stickerEditInfo.setRotaion((int) sticker.getRotation());
        stickerEditInfo.setMirrorH(sticker.isMirrorH());
        stickerEditInfo.setMirrorV(sticker.isMirrorV());
//        stickerEditInfo.setIsLock(sticker.isLockedByParent() || sticker.isLocked());
        // 2.7版本开始,锁定只是在编辑时不能操作此图,发布后不保存锁定状态. [老的锁定除外]
        stickerEditInfo.setIsLock(sticker.isLockedByParent());
        stickerEditInfo.setType(sticker.getStickerType());
        // 文本框的问题
        if (StickerUtils.isTextBubble(sticker)) {
            stickerEditInfo.setInfo(sticker.getStickerTextInfo());
        } else if (StickerUtils.isColorText(sticker)) {
            // 彩色文字的信息
            stickerEditInfo.setCtInfo(sticker.getStickerColorTextInfo());
        }
        stickerEditInfo.setZoom(sticker.getZoom());
        return stickerEditInfo;
    }

    private int getWanfenbi(float length) {
        return (int) (10000 * length / SW);
    }

    private int getWanfenbi(double length) {
        return (int) (10000 * length / SW);
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

                boolean isNeedCompress = isNeedCompress();
                if (StringUtil.isFromPersonalInfo(from)) {
                    fileName = ImageUtils.saveToFile(BitmapUtil.getAvatarCropPath(), false, bitmap, isNeedCompress);
                } else {
                    String picName = TimeUtils.dtFormat(new Date(), "yyyyMMddHHmmss");//无扩展名
//                    picName += filterName;
                    fileName = ImageUtils.saveToFile(FileUtil.getInst().getPhotoSavedPath() + "/" + picName + ".jpg", false, bitmap, isNeedCompress);
                }
            } catch (Exception e) {
                e.printStackTrace();
                toast("图片处理错误，请退出相机并重试", Toast.LENGTH_LONG);
            } finally {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
            return fileName;
        }

        @Override
        protected void onPostExecute(String fileName) {
            super.onPostExecute(fileName);
            dismissProgressDialog();
            if (StringUtils.isEmpty(fileName)) {
                if (callback != null)
                    callback.onFailed(null);
                return;
            }

            if (callback != null)
                callback.onSuccess(fileName);

        }

        OnCallback callback;

        public void setOnCallback(OnCallback callback) {
            this.callback = callback;
        }
    }

    private class EncodeTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("工程文件加密处理中...");
        }

        @Override
        protected String doInBackground(Void... params) {
            String fileName = null;
            try {
                // 此处不需要设置滤镜和其他饱和度的值(一百倍的值哦)
                stickerProject.setStickers(getStickEditInfo());

                // 保存工程文件
//                MyLog.i("bgUri.getScheme()="+bgUri.getScheme());
//                MyLog.i("bgUri.="+bgUri);
                MyLog.i("bgUri.getPath()=" + bgUri.getPath());

                // 如果有彩色文字,使用融入底图内的底图bgPlus
//                if (mStickView.isColorTxtOrVoiceStickerExist()) {
//                    fileName = CameraManager.getInst().encodeToProjectFile(stickerProject, bgPlus);
//                } else
                if (bgUri.getScheme().startsWith("file")) {
//                    MyLog.i("bgUri.startsWithFile() true=");

                    fileName = CameraManager.getInst().encodeToProjectFile(stickerProject, bgUri.getPath().replaceFirst("file://", ""));
                } else {
//                    MyLog.i("bgUri.startsWithFile() false");

                }
            } catch (Exception e) {
                MyLog.e(e);
                toast("加密处理错误，请重试", Toast.LENGTH_LONG);
            }
            return fileName;
        }

        @Override
        protected void onPostExecute(String fileName) {
            super.onPostExecute(fileName);
            dismissProgressDialog();
            if (StringUtils.isEmpty(fileName)) {
                if (callback != null)
                    callback.onFailed(null);
                return;
            }

            if (callback != null)
                callback.onSuccess(fileName);
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

    /** 发布大咖秀接口用本地的贴纸idList */
    private ArrayList<String> getStickIDlist() {
        ArrayList<String> stickerIds = new ArrayList<>();
        List<Sticker> stickers = mStickView.getStickers();
        if (stickers == null || stickers.isEmpty())
            return stickerIds;
        for (Sticker sticker : stickers) {
            String mID = sticker.getStickerId() + "_" + (StringUtils.isBlank(sticker.getParentId()) ? null : sticker.getParentId());
            stickerIds.add(mID);
        }
        return stickerIds;
    }

    /** 推荐文字接口用本地的贴纸ids */
    private String getStickIDsForRecommendWord() {
        StringBuffer sb = new StringBuffer();
        List<Sticker> stickers = mStickView.getStickers();
        if (stickers == null || stickers.isEmpty())
            return null;
        for (Sticker sticker : stickers) {
            String mID = sticker.getStickerId() + ",";
            sb.append(mID);
        }
        return sb.toString();
    }

    /**
     * 是否需要压缩效果图
     */
    public boolean isNeedCompress() {
        return true;
    }


    /**
     * 初始化声音选择的页面
     */
    private void initVoiceToolBar() {
        final List<StickerAudioInfo> audioList = AudioService.getInst().getLocalAudio();
        voiceAdapter = new VoiceAdapter(PhotoProcessActivity.this, audioList);
        voiceBar.setAdapter(voiceAdapter);
        voiceAdapter.setCallBack(new VoiceAdapter.VoiceSlectCallback() {
            @Override
            public void onVoiceSelect(StickerAudioInfo audio) {
                MyLog.i("VoiceAdapter.onItemClick....");
                if (!mStickView.containAudio()) {
                    putOnStickerView(audio);
                } else {
                    AppContext.showToastShort(R.string.add_audio_sticker_error);
                }
            }
        });

        //copy预置音频文件到sd卡
        AudioService.getInst().copyAudioToSD(audioList);
    }

    /** 停止音频播放 */
    private void stopPlayAudio() {
        if (voiceAdapter != null) {
            voiceAdapter.stopPlayAudio();
        }
    }

    /** 选择声音后放入编辑舞台 */
    private void putOnStickerView(StickerAudioInfo audioInfo) {
        Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.voice_pink_backgound);

        EmoticonBean voiceBean = new EmoticonBean();
        voiceBean.setStickType(StickerConstants.STICKER_AUDIO);
        voiceBean.setAudioLength(audioInfo.getLength());
        voiceBean.setContent(audioInfo.getUri());
        voiceBean.setStickerId(audioInfo.getAudioId());
        mStickView.setWaterMark(background, null, false, voiceBean);
    }

    /**
     * 初始化彩色字
     */
    private void initColorText() {
        final List<ColorTextStyle> colorTextStyleList = ColorTextUtils.getInst().getLocalColorTextStyle();
        final ColorTextStyleAdapter adapter = new ColorTextStyleAdapter(PhotoProcessActivity.this, colorTextStyleList, ctStyleI, currentCtColor);
        // 样式
        colorTextStyleBar.setAdapter(adapter);
        colorTextStyleBar.setOnItemClickListener(new it.sephiroth.android.library.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (adapter.getLastClickPos() != arg2) {
                    adapter.setLastClickPos(arg2);
                    currentCTS = colorTextStyleList.get(arg2);
                    ctStyleI = arg2;
                }

                // TODO 刷新界面上的彩色字或添加彩色字
                refreshColotTxt(true);
            }
        });

        // 设置所记忆的彩色字颜色
        adapter.setSelectColor(currentCtColor);

        // 颜色
        final List<ColorTextColor> colorTextColorList = ColorTextColors.getAllColors();
        final ColorTextColorAdapter adapterC = new ColorTextColorAdapter(PhotoProcessActivity.this, colorTextColorList, ctColorI, mStickView.isColorTxtStickerSelected());
        colorTextColorBar.setAdapter(adapterC);
        colorTextColorBar.setOnItemClickListener(new it.sephiroth.android.library.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (adapterC.getLastClickPos() != arg2) {
                    // TODO 只有当前选中了彩色字才可以刷新界面上的彩色字,以及样式.
                    // ps 允许颜色在没有字的时候也能切换.
//                    if (mStickView.isColorTxtStickerSelected()) {
                    adapterC.setLastClickPos(arg2);
                    ctColorI = arg2;
                    StickPreference.getInstance().setLastColorTextColorPos(arg2);
                    currentCtColor = colorTextColorList.get(arg2).getValue();

                    adapter.setSelectColor(colorTextColorList.get(arg2).getValue());
//                    }
                }
                refreshColotTxt(false);
            }
        });
    }

    /** 生成一个彩色文字 */
    private Bitmap generateColorText(StickerColorTextInfo scti) {
        if (scti == null) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            setFakeColorTxtModel(scti);
            colorTextModel.setText(scti.getText());

            /**---------------------------------获取文字的宽高start---------------------------------*/

            textPaintnew.setFlags(currentCTS.getFlag());

            int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            colorTextModel.measure(spec, spec);
            int measuredWidth = colorTextModel.getMeasuredWidth();
            int measuredHeight = colorTextModel.getMeasuredHeight() + COLOR_TEXT_MARGIN_TOP;
//            MyLog.i(colorTextModel.getText() + ", measuredWidth,Height= " + measuredWidth + ", " + measuredHeight
//                    + ", color=" + currentCtColor
//                    + ", tf=" + currentCTS.getTf()
//                    + ", style=" + currentCTS.getStyle()
//                    + ", tp_color=" + textPaintnew.getColor()
//                    + ", tp_tf=" + textPaintnew.getTypeface()
//                    + ", tp_style=" + textPaintnew.getStyle()
//                    + ", flags=" + textPaintnew.getFlags());

            /**---------------------------------获取文字的宽高end---------------------------------*/

            /**---------------------------------draw start---------------------------------*/

            bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            /**
             * input:要绘制的字符串
             * textPaint(TextPaint 类型)设置了字符串格式及属性的画笔
             * measuredWidth设置画多宽后换行
             * 后面的参数是对齐方式...
             */

            StaticLayout layout = new StaticLayout(colorTextModel.getText(), textPaintnew, measuredWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
            canvas.translate(0, COLOR_TEXT_MARGIN_TOP);
            layout.draw(canvas);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();

            /**---------------------------------draw end---------------------------------*/
            bitmap = BitmapUtil.zoomBitmap(bitmap, 0.6f);
        } catch (Exception e) {
            MyLog.e(e);
        }

        return bitmap;
    }


    /** 设置这个txt的显示样式和颜色 */
    private void setFakeColorTxtModel(StickerColorTextInfo scti) {
        colorTextModel.setTextColor(Color.parseColor(scti.getColor()));
        ColorTextStyle tmpCTS = ColorTextUtils.getInst().getLocalColorTextStyle().get(scti.getTypeface());
        colorTextModel.setTypeface(tmpCTS.getTf(), tmpCTS.getStyle());
        textPaintnew.setColor(Color.parseColor(scti.getColor())); // 这个才是真正让彩色字变色的代码
    }

    /** 设置这个txt的显示样式和颜色 */
    private void setFakeColorTxtModel() {
        colorTextModel.setTextColor(Color.parseColor(currentCtColor));
        colorTextModel.setTypeface(currentCTS.getTf(), currentCTS.getStyle());
        textPaintnew.setColor(Color.parseColor(currentCtColor)); // 这个才是真正让彩色字变色的代码
    }

    /**刷新彩色字,或者添加彩色字
     * @param style true:点击了样式; false:点击了颜色
     * */
    private void refreshColotTxt(boolean style) {
        setFakeColorTxtModel();
        // 如果舞台上没彩色字
        if (!mStickView.isColorTxtStickerSelected()) {
            if (style) {
                // 点击了样式,则添加一个新彩色字
                String randomText = StickerUtils.getRandomBubbleText();
                StickerColorTextInfo scti = new StickerColorTextInfo();
                scti.setText(randomText);
                scti.setColor(currentCtColor);
                scti.setTypeface(ctStyleI);
                Bitmap colorTxtBmp = generateColorText(scti);
                EmoticonBean colorBean = new EmoticonBean();
//                colorBean.setStickerId(ClientInfo.getUID() + "_" + System.currentTimeMillis());
                // 将彩色文字的默认文字赋值到贴纸的bubbleText字段, 然后可以在贴纸编辑的时候使用(作为参数送到文字编辑Activity)
                Gson mGson = new Gson();
                colorBean.setBubbleText(mGson.toJson(scti));
                colorBean.setStickType(StickerConstants.STICKER_COLOR_TEXT);
                mStickView.setWaterMark(colorTxtBmp, null, false, colorBean);
                return;
            } else {
                // 点击了颜色,则不用操作舞台
                return;
            }
        } else {
            // 刷新彩色字
            try {
                StickerColorTextInfo scti = mStickView.getStickers().get(mStickView.getFocusStickerPosition()).getStickerColorTextInfo();
                if (!StringUtil.isNullOrEmpty(ucid)) {
                    // 转改时
                    if (style) {
                        scti.setTypeface(ctStyleI);
                    } else {
                        scti.setColor(currentCtColor);
                    }
                } else {
                    scti.setColor(currentCtColor);
                    scti.setTypeface(ctStyleI);
                }
                Bitmap bitmap = generateColorText(scti);
                mStickView.refreshColorText(bitmap);
            } catch (Exception e) {
                MyLog.e(e);
            }
        }
    }

    /**
     * 初始化最近贴纸
     *
     * @param needRefresh 是否需要刷新view
     */
    private void initRecent(boolean needRefresh) {
        if (!hasRecentStick()) {
            if (AppContext.getInstance().isLogin()) {
                /**如果没有最近贴纸的时候,取登录用户的前48条贴纸去更新数据.
                 而这48条数据就存储在 mRecentSet 中
                 获取成功后刷新view
                 * */
                MyLog.i("" +
                        "如果没有最近贴纸的时候,取登录用户的前48条贴纸去更新数据.\n" +
                        "//            而这48条数据就存储在 mRecentSet 中\n" +
                        "//            获取成功后刷新view\n" +
                        "//            而且需要将数据检查并异步下载");

                StickerManager.getInstance().getHistoryStickers(StickerConstants.RECENTLY_STICKER_MAX_COUNT, null, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        if (obj == null) {
                            return;
                        }
                        if (mRecentSet == null) {
                            mRecentSet = new EmoticonSetBean();
                        }
                        StickerPackage sticker = new StickerPackage();
                        sticker.setName(getString(R.string.recent));
                        sticker.setStickers((ArrayList<StickerInfo>) obj);
                        MyLog.i("xxx 有" + sticker.getStickers().size() + "个最近贴纸");
                        addBeanToRecent(StickerManager.getInstance().saveStickers2DB(StickerConstants.STICKER_TYPE_RECENTLY, sticker, null, null));
                    }

                    @Override
                    public void onErr(Object obj) {
                        MyLog.i("xxx onErr 有48个最近贴纸");
                    }

                    @Override
                    public void onNoNetwork() {
                        MyLog.i("xxx onNoNetwork 有48个最近贴纸");
                    }
                });
            }
        } else {
            mRecentSet = getRecentStick().get(0);
//            lastPosition = 1;
            MyLog.i("xxx getRecentCount=" + mRecentSet.getEmoticonList().size());
        }
        if (mRecentSet == null) {
            mRecentSet = new EmoticonSetBean();
        }
        final ArrayList<EmoticonBean> recentBeanList = mRecentSet.getEmoticonList();

        int count = 0;
        if (recentBeanList != null) {
            count = recentBeanList.size();
            recentCount.setText(count + "张");
            recentCount.setVisibility(View.VISIBLE);
            if (count % 8 == 0) {
                count /= 8;
            } else {
                count = count / 8 + 1;
            }
        } else {
            recentCount.setVisibility(View.GONE);
        }
        mRecentEmoticonsIndicatorView.init(count);

        if (!needRefresh) {
            return;
        }

        EmoticonsKeyboardBuilder builder = EmoticonsUtils.getBuilder(PhotoProcessActivity.this, StickerConstants.STICKER_TYPE_RECENTLY);
        mRecentEmoticonsPageView.setBuilder(builder);
        mRecentEmoticonsPageView.updateSelf(EMOJI_PAGE_HEIGHT);
        mRecentEmoticonsPageView.updateView();

        mRecentEmoticonsPageView.setOnIndicatorListener(new OnEmoticonsPageViewListener() {
            @Override
            public void emoticonsPageViewInitFinish(int count) {
                mRecentEmoticonsIndicatorView.init(count);
            }

            @Override
            public void emoticonsPageViewCountChanged(int count) {
                mRecentEmoticonsIndicatorView.setIndicatorCount(count);
            }

            @Override
            public void playTo(int position) {
                mRecentEmoticonsIndicatorView.playTo(position);
            }

            @Override
            public void playBy(int oldPosition, int newPosition) {
                try {
                    mRecentEmoticonsIndicatorView.playBy(oldPosition, newPosition);
                } catch (Exception e) {
                    MyLog.e(e);
                }
            }
        });

        mRecentEmoticonsPageView.resetIViewListener(new IView() {
            @Override
            public void onItemClick(EmoticonBean bean) {
                addStickToStickView(bean, false);
            }

            @Override
            public boolean onItemLongClick(int position, View converView, EmoticonBean bean) {
                return true;
            }

            @Override
            public void onItemDisplay(EmoticonBean bean) {
            }

            @Override
            public void onPageChangeTo(int position) {
            }
        });
    }

    /** 初始化一级,二级贴纸包界面 */
    private void initMajorStickPackagesArea() {
        EmoticonsKeyboardBuilder builder = buildMyStickers();

        mEmoticonsPageView.setBuilder(builder);
        mEmoticonsPageView.updateSelf(EMOJI_PAGE_HEIGHT);
        mEmoticonsPageView.updateView();


        majorStickerList = builder.builder.getEmoticonSetBeanList();
        if (majorStickerList == null) {
            majorStickerList = new ArrayList<>();
        }
//        MyLog.i("initMajorStickPackagesArea->majorStickerList.size = " + majorStickerList.size());

        // 添加加号,进入贴纸商城用
        EmoticonSetBean mStoreEmjSet = new EmoticonSetBean();
        mStoreEmjSet.setName(getString(R.string.more_cosplay));
        majorStickerList.add(0, mStoreEmjSet);

        mEmotPackagesPageView.setBuilder(builder);
        mEmotPackagesPageView.updateSelf(EMOJI_PAGE_HEIGHT);
        mEmotPackagesPageView.updateView();
        int count = majorStickerList.size() / 8;
        mEmotPackagesIndicatorView.init(majorStickerList.size() % 8 == 0 ? count : (count + 1));

        // 设置二级界面的title为第一个贴纸包名字
        setNameAndCount(1);
    }


    /** 重置最近贴纸DB */
    private long setRecentStick() {
        final DBHelper dbHelper = DBHelper.getInstance(PhotoProcessActivity.this);

//        mRecentSet.setIconUri("file://" + BitmapUtil.BITMAP_STICKRES + mRecentSet.getId() + "/" + mRecentSet.getIconUri());
        int drc = dbHelper.deleteDefaultStickerSet(StickerConstants.STICKER_RECENT);
        // 清空以前的旧文件
//        FileUtil.delAllFilesInFolder(EmojiUtils.getEmjSetFolder(mRecentSet.getId()));
        mRecentSet.setUpdateTime(System.currentTimeMillis());
        long lrc = dbHelper.insertEmoticonSet(mRecentSet);
        MyLog.i(drc + "/" + lrc + " 更新最近贴纸DB OK: " + mRecentSet.toString());
        return drc * lrc;
    }

    private ArrayList<EmoticonSetBean> getRecentStick() {
        EmoticonsKeyboardBuilder builder = EmoticonsUtils.getBuilder(PhotoProcessActivity.this, StickerConstants.STICKER_TYPE_RECENTLY);
        return builder.builder.getEmoticonSetBeanList();
    }

    private boolean hasRecentStick() {
        ArrayList<EmoticonSetBean> list = getRecentStick();
        return list != null && !list.isEmpty()
                && list.get(0).getEmoticonList() != null && !list.get(0).getEmoticonList().isEmpty();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Constants.ACTION_EDIT_BUBBLETEXT == requestCode && data != null) {
            mStickView.refreshTextPop(data);
        } else if (Constants.ACTION_EDIT_COLOR_TEXT == requestCode && data != null) {
            if (mStickView.isColorTextNeedRefresh(data)) {
                // 还好此处不需要考虑bmp的缩放,因为缩放倍数是在sticker matrix内了
                StickerColorTextInfo scti = mStickView.getStickers().get(mStickView.getFocusStickerPosition()).getStickerColorTextInfo();
                scti.setText(data.getStringExtra(Constants.PARAM_EDIT_TEXT));
                Bitmap bitmap = generateColorText(scti);
                mStickView.refreshColorText(bitmap);
            }
        }
    }

    /** 下载贴纸的大图etc */
    private void downloadSticker(final String url, final String path, final IListener listener) {
        if (!TDevice.hasInternet()) {
            if (listener != null) {
                listener.onNoNetwork();
            }
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                //download thumb first
                if (HttpUtil.download(url, path, true)) {
                    if (listener != null) {
                        listener.onSuccess(path);
                    }
                } else {
                    if (listener != null) {
                        listener.onErr(null);
                    }
                }

            }
        }).start();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPlayAudio();

        MobclickAgent.onPageEnd(UmengConstants.PHOTO_PROCESS_ACTIVITY);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mRecentSet == null) {
            if (hasRecentStick()) {
                mRecentSet = getRecentStick().get(0);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出时清除标记
        StickPreference.getInstance().setPhotoProcessRunning(false);
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        if (currentBitmap != null) {
            currentBitmap.recycle();
        }
    }

    private static void onStatistics(Context context, String id, HashMap<String, String> m, long value) {
        m.put("__ct__", String.valueOf(value));
        MobclickAgent.onEvent(context, id, m);
    }

}
