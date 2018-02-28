package com.moinapp.wuliao.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

public class DownloadReceiver extends BroadcastReceiver {
	private static ILogger MyLog = LoggerFactory.getLogger("DownloadReceiver");

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		MyLog.i("DownloadReceiver receive action=" + action);
		if (action.equals(android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
			long reference = intent.getLongExtra(android.app.DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			MyLog.i("Download complete refer=" + reference);

            EventBus.getDefault().post(new DownloadCompleteEvent(reference));
        }
	}
}
