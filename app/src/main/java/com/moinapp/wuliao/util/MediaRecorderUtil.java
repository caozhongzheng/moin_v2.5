package com.moinapp.wuliao.util;

/**
 * Created by moying on 16/6/14.
 */

import android.media.MediaRecorder;
import android.os.Handler;

import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * amr音频处理
 *
 */
public class MediaRecorderUtil {
    private ILogger MyLog = LoggerFactory.getLogger(MediaRecorderUtil.class.getSimpleName());
    private MediaRecorder mMediaRecorder;
    public static final int MAX_LENGTH = 1000 * 60;// 最大录音时长60s;
    private static String filePath;

    public MediaRecorderUtil() {
        this.filePath = BitmapUtil.BITMAP_AUDIO+"/record_audio.amr";
    }

    public MediaRecorderUtil(File file) {
        this.filePath = file.getAbsolutePath();
    }

    private long startTime;
    private long endTime;

    /**
     * 开始录音 使用amr格式
     * <p>
     * 录音文件
     *
     * @return
     */
    public long startRecord() {
        // 开始录音
        /* ①Initial：实例化MediaRecorder对象 */
        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();
        try {
			/* ②setAudioSource/setVedioSource */
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
			/* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            			/*
			 * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
			 * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
			 */
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            MyLog.i("setOutputFile, filePath= " + filePath);
			/* ③准备 */
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.setMaxDuration(MAX_LENGTH);
            mMediaRecorder.prepare();
			/* ④开始 */
            mMediaRecorder.start();
            // AudioRecord audioRecord.
			/* 获取开始时间* */
            startTime = System.currentTimeMillis();
//            updateMicStatus();
            MyLog.i("ACTION_START, startTime= " + startTime);
        } catch (IllegalStateException e) {
            MyLog.i("call startAmr(File mRecAudioFile) failed!"
                            + e.getMessage());
        } catch (IOException e) {
            MyLog.i("call startAmr(File mRecAudioFile) failed!"
                            + e.getMessage());
        }
        return startTime;
    }

    /**
     * 停止录音
     */
    public long stopRecord() {
        if (mMediaRecorder == null)
            return 0L;
        endTime = System.currentTimeMillis();
        MyLog.i("ACTION_END, endTime" + endTime);
        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
        } catch (IllegalStateException e) {
            MyLog.e(e);
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
            mMediaRecorder = null;
        }
        MyLog.i("ACTION_LENGTH, Time" + (endTime - startTime));
        return endTime - startTime;
    }

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    /**
     * 更新话筒状态
     */
    private int BASE = 1;
    private int SPACE = 100;// 间隔取样时间

    private void updateMicStatus() {
        if (mMediaRecorder != null) {
            double ratio = (double) mMediaRecorder.getMaxAmplitude() / BASE;
            double db = 0;// 分贝
            if (ratio > 1)
                db = 20 * Math.log10(ratio);
            MyLog.i("分贝值：" + db);
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    public boolean isRecording() {
        return mMediaRecorder != null;
    }

    public static String getRecordPath() {
        return filePath;
    }
}