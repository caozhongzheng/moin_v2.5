package com.moinapp.wuliao;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.init.InitManager;
import com.moinapp.wuliao.commons.init.InitPreference;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.mine.MinePreference;
import com.moinapp.wuliao.modules.sticker.StickPreference;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.HttpUtil;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import org.kymjs.kjframe.http.KJAsyncTask;
import org.kymjs.kjframe.utils.FileUtils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 应用启动界面
 *
 * @created 2014年12月22日 上午11:51:56
 */
public class AppStart extends Activity {
    private static final boolean SHOW_GIF = false;
    private View.OnClickListener jump;

    private ILogger MyLog = LoggerFactory.getLogger("ast");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //添加友盟统计的渠道
        AnalyticsConfig.setChannel(ClientInfo.getChannelId());
//        // 防止第三方跳转时出现双实例
//        Activity aty = AppManager.getActivity(MainActivity.class);
//        //禁止默认的页面统计方式，这样将不会再自动统计Activity
//        MobclickAgent.openActivityDurationTrack(false);
//
//        if (aty != null && !aty.isFinishing()) {
//            finish();
//        }
        // SystemTool.gc(this); //针对性能好的手机使用，加快应用相应速度

        final View view = View.inflate(this, R.layout.app_start, null);
        setContentView(view);
        final ImageView imageView = (ImageView) view.findViewById(R.id.welcome_iv);
        imageView.setBackgroundResource(R.drawable.welcome_moin);
        /**如果前回有未分享完或成功的同步分享任务,不再继续分享*/
        MinePreference.getInstance().setSharePlatform("");

        jump = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectTo();
                stopCountDown();
            }
        };

        timer.schedule(task, 3000);
        imageView.setOnClickListener(jump);

        boolean hasNewBootImage = InitManager.getInstance().hasNewBootImage();
        if (hasNewBootImage) {
            showBootImage(imageView, true);
            InitManager.getInstance().getBootImage(imageView, new IListener() {
                @Override
                public void onSuccess(Object obj) {
                    String url = ImageLoaderUtils.defineUrlByImageSize(imageView, InitPreference.getInstance().getBootImageUrl());
                    if (StringUtil.isNullOrEmpty(url)) {
//                            MyLog.i("启动图URL不存在, 返回");
                        return;
                    } else {
//                            MyLog.i("启动图URL存在, 下载: " + url);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean result = HttpUtil.download(url, BitmapUtil.getBootImagePath(), true);
                                if (result) {
                                    InitPreference.getInstance().setHasBootImageMsg(false);
                                    InitManager.getInstance().backupBootImage();
                                }
                            }
                        }).start();
                    }
                }

                @Override
                public void onErr(Object obj) {

                }

                @Override
                public void onNoNetwork() {

                }
            });
        } else {
            showBootImage(imageView, false);
        }

        TextView jump_tv = (TextView) view.findViewById(R.id.jump_tv);
        jump_tv.setOnClickListener(jump);

        // Push: 以apikey的方式登录，一般放在主Activity的onCreate中。
        // 这里把apikey存放于manifest文件中，只是一种存放方式，
        // 您可以用自定义常量等其它方式实现，来替换参数中的Utils.getMetaValue(PushDemoActivity.this,
        // "api_key")
        //请将AndroidManifest.xml 128 api_key 字段值修改为自己的 api_key 方可使用 ！！
        //ATTENTION：You need to modify the value of api_key to your own at row 128 in AndroidManifest.xml to use this Demo !!
        PushManager.startWork(BaseApplication.context(), PushConstants.LOGIN_TYPE_API_KEY,
                AppConfig.getBaiduPushKey());

        //清除大咖秀编辑页面启动的标记
        StickPreference.getInstance().setPhotoProcessRunning(false);
    }

    private void showBootImage(ImageView imageView, boolean showLast) {
        String bootImgPath = showLast ? BitmapUtil.getLastBootImagePath() : BitmapUtil.getBootImagePath();
        File bootFile = new File(bootImgPath);
        if (bootFile.exists() && bootFile.isFile()) {
//                    MyLog.i("启动图存在,显示 " + InitPreference.getInstance().getBootImageUrl());
            setBootImage(imageView, bootImgPath);
        } else {
            bootImgPath = !showLast ? BitmapUtil.getLastBootImagePath() : BitmapUtil.getBootImagePath();
            bootFile = new File(bootImgPath);
            if (bootFile.exists() && bootFile.isFile()) {
//                    MyLog.i("启动图存在,显示 " + InitPreference.getInstance().getBootImageUrl());
                setBootImage(imageView, bootImgPath);
            } else {
//                    MyLog.i("启动图不存在,显示默认的图");
                imageView.setImageResource(R.drawable.welcome_moin);
            }
        }
    }

    private void setBootImage(ImageView imageView, String bootImgPath) {
        try {
            Drawable bootDrawable = Drawable.createFromPath(bootImgPath);
            imageView.setImageDrawable(bootDrawable);
        } catch (Exception e) {
            MyLog.e(e);
        }
    }

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            redirectTo();
        }
    };

    private void stopCountDown() {
        timer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.APP_START);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.APP_START);
        MobclickAgent.onPause(this);
    }

    /**
     * 监听返回
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void cleanImageCache() {
        final File folder = FileUtils.getSaveFolder(BitmapUtil.IMAGELOAD_CACHE);
        KJAsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for (File file : folder.listFiles()) {
                    file.delete();
                }
            }
        });
    }

    /**
     * 跳转到...
     */
    private void redirectTo() {
        if (MinePreference.getInstance().isFirstEnter()) {
            Intent intent = new Intent(AppStart.this, GuideActivity.class);
            startActivity(intent);
        } else {
            //启动的时候根据是否登录分别跳转至关注和发现
//            int tabIndex = AppContext.getInstance().isLogin() ? 0: 1;
            //无论用户是否登录,当启动应用的时候都跳到发现界面
//            int tabIndex = 1;
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.putExtra(MainActivity.BUNDLE_KEY_TABINDEX, tabIndex);
//            startActivity(intent);
        }

        finish();
    }
}
