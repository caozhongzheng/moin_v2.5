package com.moinapp.wuliao.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

import java.io.IOException;

/**
 * 媒体播放类
 * 现在播放音频的处理是每次都生成一个新的mediaplayer对象, 原因是如果使用一个对象时只有第一个播放正常
 * 第二次以后报状态错误,可能时java本地对象和native层对象状态不一致造成的,暂时没有找到解决办法,所以采用
 * 每次播放时new一个新的对象来处理
 */
public class MyAudioPlayer {
	private static ILogger MyLog = LoggerFactory.getLogger(MyAudioPlayer.class.getSimpleName());
	private MediaPlayer mPlayer;
	private static MyAudioPlayer mInstance;

	public MyAudioPlayer() {
		mPlayer = new MediaPlayer();
	}

	public static synchronized MyAudioPlayer getInstance() {
		if (mInstance == null) {
			mInstance = new MyAudioPlayer();
		}

		return mInstance;
	}
	public void playAudio(String filePath,  PlayEndCallback callback) {
		try {
			stopPlay();
			mPlayer = new MediaPlayer();
			mPlayer.setDataSource(filePath);
			mPlayer.prepare();
			mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					MyLog.i("amr play completed!");
					clearPlayer();
					callback.onCompleted();
				}
			});

			mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					MyLog.i("amr play onError!");
					return false;
				}
			});
			mPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void playAssertsAudio(Context context, String assertsPath, PlayEndCallback callback) {
		try {
			stopPlay();
			mPlayer = new MediaPlayer();
			AssetFileDescriptor fileDescriptor = context.getAssets().openFd(assertsPath);
			mPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
					fileDescriptor.getStartOffset(),
					fileDescriptor.getLength());
			mPlayer.prepare();
			mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					MyLog.i("amr play completed!");
					clearPlayer();
					callback.onCompleted();
				}
			});

			mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					MyLog.i("amr play onError!");
					return false;
				}
			});
			mPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopPlay() {
		if (mPlayer != null && mPlayer.isPlaying()) {
			clearPlayer();
		}
	}

	public boolean isPlalying() {
		if (mPlayer == null) return false;
		return mPlayer.isPlaying();
	}

	public interface PlayEndCallback {
		public void onCompleted();
	}
	private void clearPlayer() {
		mPlayer.stop();
		mPlayer.release();
		mPlayer = null;
	}
}
