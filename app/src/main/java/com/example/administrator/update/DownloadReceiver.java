package com.example.administrator.update;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by LKZ on 2017/9/22.
 *  downloadManager 下载完成回调
 */

public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){
            long downloadID =intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            DownloadUtil.getInstance(context).installAPK(downloadID);
        }
    }
}