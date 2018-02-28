package com.moinapp.wuliao.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.modules.sticker.model.StickerAudioInfo;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.HttpUtil;
import com.moinapp.wuliao.util.MyAudioPlayer;
import com.moinapp.wuliao.util.TDevice;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by liujiancheng on 16/6/15.
 * 帖子中播放声音的控件
 */
public class AudioPlayLayout extends RelativeLayout implements SensorEventListener {

    /**
     * 最大最小的控件长度,对应最长最短的声音时长,单位dp和秒
     */
    private static final int mMaxBackgoundLength = 169;
    private static final int mMinBackgoundLength = 85;
    private static final int mMaxAudioLength = 60;
    private static final int mMinAudioLength = 1;

    private float mUnitLength = TDevice.dpToPixel(mMaxBackgoundLength - mMinBackgoundLength)/(mMaxAudioLength - mMinAudioLength);
    private Activity mContext;
    private TextView mTvAudioLength;
    private ImageView mIvAudioPlay;

    private AnimationDrawable animationDrawable;
    private StickerAudioInfo mAudioInfo;
    private boolean mPlayingAudio = false;
    private boolean mEnablePlay = true;

    private Sensor mSensor;
    private SensorManager mSensorManager;
    private AudioManager mAudioManager;

    public AudioPlayLayout(Context context) {
        super(context);
        initUI(context);
    }

    public AudioPlayLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI(context);
    }

    public AudioPlayLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI(context);
    }

    private void initUI(Context context) {
        mContext = (Activity)context;

        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_play_audio, this, true);
        mTvAudioLength = (TextView) rootView.findViewById(R.id.tv_audio_length);
        mIvAudioPlay = (ImageView) rootView.findViewById(R.id.iv_audio_play);
        mIvAudioPlay.setImageResource(R.drawable.voice_play_w3);

        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        //TYPE_PROXIMITY是距离传感器类型，当然你还可以换成其他的，比如光线传感器
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        //暂时不处理切换模式
//        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onDetachedFromWindow() {
        mSensorManager.unregisterListener(this);
        stopPlayAudio();
        super.onDetachedFromWindow();
    }

    /**
     * 设置播放的声音对象
     */
    public void setAudioInfo(StickerAudioInfo audioInfo) {
        mAudioInfo = audioInfo;
        if (mAudioInfo != null) {
            mTvAudioLength.setText(mAudioInfo.getLength() + "”");
            getLayoutParams().width = calculateViewWidth();
        }
    }

    /**
     * 设置播放控件是否可以播放
     * @param enable
     */
    public void setEnablePlay(boolean enable) {
        mEnablePlay = enable;
    }

    // 拦截子控件的触摸事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                Log.i("ljc", "AudioPlayLayout onTouchEvent......");
                setAudioPlayStatus();
                break;
        }
        return super.onTouchEvent(event);
    }

    private int calculateViewWidth() {
        int length = (int)TDevice.dpToPixel(mMaxBackgoundLength);
        if (mAudioInfo != null) {
            length = (int)(TDevice.dpToPixel(mMinBackgoundLength) + mUnitLength*(mAudioInfo.getLength() - mMinAudioLength));
            Log.i("ljc", "calculateViewWidth = " + length);
        }
        return length;
    }

    //设置播放的状态
    private void setAudioPlayStatus() {
        if (!mPlayingAudio && mEnablePlay) {
            mPlayingAudio = true;
            mIvAudioPlay.setImageResource(R.drawable.post_audio_play);
            animationDrawable = (AnimationDrawable) mIvAudioPlay.getDrawable();
            if (mAudioInfo != null) {
                playAudio(mAudioInfo);
                if (animationDrawable != null) {
                    animationDrawable.start();
                }
            }
        } else {
            stopPlayAudio();
        }
    }

    private void playAudio(StickerAudioInfo audioInfo) {
        if (audioInfo == null) return;
        if (!TextUtils.isEmpty(audioInfo.getUri())) {
            String input = audioInfo.getUri();
            String audio = BitmapUtil.BITMAP_AUDIO + input.substring(input.lastIndexOf("/") + 1);
            File file = new File(audio);
            if (!file.exists()) {
                //本地找不到就先下载
                downloadAudio(input, audio, new OnCallback() {
                    @Override
                    public void onSuccess(Object object) {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                playAudio(audio);
                            }
                        });
                    }

                    @Override
                    public void onFailed(Object object) {

                    }
                });
            } else {
                playAudio(audio);
            }
        }
    }

    private void downloadAudio(String url, String local, OnCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean result = HttpUtil.download(url, local);
                if (callback != null && result) {
                    callback.onSuccess(null);
                }
            }
        }).start();
    }

    private void playAudio(String path) {
        MyAudioPlayer.getInstance().playAudio(path, new MyAudioPlayer.PlayEndCallback() {
            @Override
            public void onCompleted() {
                if (animationDrawable != null) {
                    animationDrawable.stop();
                }
                mPlayingAudio = false;
                setStopPalyDrawable(mAudioInfo);
            }
        });
    }

    public void stopPlayAudio() {
        if (mAudioInfo == null) return;
        mPlayingAudio = false;
        MyAudioPlayer.getInstance().stopPlay();
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
        setStopPalyDrawable(mAudioInfo);
    }

    private void setStopPalyDrawable(StickerAudioInfo audioInfo) {
        if (audioInfo == null) return;
        mIvAudioPlay.setImageResource(R.drawable.voice_play_w3);
    }

    private void setInCallBySdk() {
        if (mAudioManager == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mAudioManager.getMode() != AudioManager.MODE_IN_COMMUNICATION) {
                mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            }
            try {
                Class clazz = Class.forName("android.media.AudioSystem");
                Method m = clazz.getMethod("setForceUse", new Class[]{int.class, int.class});
                m.invoke(null, 1, 1);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            if (mAudioManager.getMode() != AudioManager.MODE_IN_CALL) {
                mAudioManager.setMode(AudioManager.MODE_IN_CALL);
            }
        }
//        if (mAudioManager.isSpeakerphoneOn()) {
//            mAudioManager.setSpeakerphoneOn(false);
//            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
//                    AudioManager.STREAM_MUSIC);
//        }
    }

    private void setModeNormal() {
        if (mAudioManager == null) {
            return;
        }
        mAudioManager.setSpeakerphoneOn(true);
        mAudioManager.setMode(AudioManager.MODE_NORMAL);

//        if (!mAudioManager.isSpeakerphoneOn()) {
//            mAudioManager.setSpeakerphoneOn(true);
//
//            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
//                    mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
//                    AudioManager.STREAM_MUSIC);
//        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float range = event.values[0];
        Log.i("ljc", "onSensorChanged: range = " + range + ", mSensor.getMaximumRange()=" + mSensor.getMaximumRange());
        if (range > mSensor.getMaximumRange()) {
            setModeNormal();
        } else {
            setInCallBySdk();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    interface OnCallback {
        void onSuccess(Object object);
        void onFailed(Object object);
    }
}
