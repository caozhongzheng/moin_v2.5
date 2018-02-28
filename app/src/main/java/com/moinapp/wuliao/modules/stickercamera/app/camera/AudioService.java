package com.moinapp.wuliao.modules.stickercamera.app.camera;


import android.util.Log;

import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.modules.sticker.model.StickerAudioInfo;
import com.moinapp.wuliao.util.BitmapUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AudioService {

    private static AudioService mInstance;
    private static List<StickerAudioInfo> mAudioList;

    public static AudioService getInst() {
        if (mInstance == null) {
            synchronized (AudioService.class) {
                if (mInstance == null)
                    mInstance = new AudioService();
            }
        }
        return mInstance;
    }

    private AudioService() {
    }

    public List<StickerAudioInfo> getLocalAudio() {
        if (mAudioList != null && mAudioList.size() != 0) {
            return mAudioList;
        }
        mAudioList = new ArrayList<StickerAudioInfo>();
        mAudioList.add(new StickerAudioInfo(5, "星爷狂笑", "audio/s1.mp3", ""));//
        mAudioList.add(new StickerAudioInfo(3, "魔女淫笑", "audio/s2.mp3", ""));//
        mAudioList.add(new StickerAudioInfo(3, "志玲想你", "audio/s3.mp3", ""));//
        mAudioList.add(new StickerAudioInfo(2, "哇不错哟", "audio/s4.mp3", ""));//
        mAudioList.add(new StickerAudioInfo(5, "海浪海鸥", "audio/s5.mp3", ""));//
        mAudioList.add(new StickerAudioInfo(3, "小新加油", "audio/s7.mp3", ""));//
        mAudioList.add(new StickerAudioInfo(4, "小新\n万人迷", "audio/s8.mp3", ""));//
        mAudioList.add(new StickerAudioInfo(3, "小丸子\n生气", "audio/s9.mp3", ""));//
        mAudioList.add(new StickerAudioInfo(9, "愤怒的\n小鸟1", "audio/s14.mp3", ""));//
        mAudioList.add(new StickerAudioInfo(10, "愤怒的\n小鸟2", "audio/s15.mp3", ""));//
        mAudioList.add(new StickerAudioInfo(12, "愤怒的\n小鸟3", "audio/s16.mp3", ""));//
        mAudioList.add(new StickerAudioInfo(4, "嗲声\n讨厌你", "audio/s17.mp3", ""));//
        mAudioList.add(new StickerAudioInfo(2, "志玲\n说加油", "audio/s18.mp3", ""));//
        mAudioList.add(new StickerAudioInfo(2, "感觉\n萌萌哒", "audio/s19.mp3", ""));//
        mAudioList.add(new StickerAudioInfo(4, "姐姐是\n大美女", "audio/s20.mp3", ""));//
        mAudioList.add(new StickerAudioInfo(2, "你太帅了", "audio/s21.mp3", ""));//
        mAudioList.add(new StickerAudioInfo(3, "好好吃哦", "audio/s22.mp3", ""));//
        mAudioList.add(new StickerAudioInfo(4, "美女吻别", "audio/s23.mp3", ""));//
        mAudioList.add(new StickerAudioInfo(5, "皇上驾到", "audio/s24.mp3", ""));//
        mAudioList.add(new StickerAudioInfo(3, "众人哇叫", "audio/s25.mp3", ""));//
        return mAudioList;
    }

    public void copyAudioToSD(List<StickerAudioInfo> audioInfos) {
        if (audioInfos == null || audioInfos.size() == 0)  return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(BitmapUtil.BITMAP_AUDIO);
                if (!file.exists())
                    file.mkdirs();
                for (StickerAudioInfo audio : audioInfos) {
                    String input = audio.getUri();
                    String output = BitmapUtil.BITMAP_AUDIO + input.substring(input.lastIndexOf("/") + 1);
                    Log.i("ljc","output file="+output);
                    try {
                        copyBigDataToSD(input, output);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void copyBigDataToSD(String assertFile, String strOutFileName) throws IOException
    {
        File file = new File(strOutFileName);
        if (file != null && file.exists()) {
            return;
        }
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(strOutFileName);
        myInput = BaseApplication.context().getAssets().open(assertFile);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while(length > 0)
        {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }

        myOutput.flush();
        myInput.close();
        myOutput.close();
    }
}
