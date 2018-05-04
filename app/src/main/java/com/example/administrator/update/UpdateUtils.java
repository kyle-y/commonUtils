package com.example.administrator.update;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;

import com.example.administrator.common.commonUtils.Utils;

/**
 * 用于更新的操作类
 */

public class UpdateUtils implements DownloadUtil.OnDownloadCallBack {
    public static final int REQUEST_PERMISSION = 100;
    private SharedPreferences sharedPreferences;
    private static UpdateUtils instance;
    private Activity activity;
    private DownloadUtil downloadUtil;
    private boolean isForce;
    private DownloadListener listener;

    public static synchronized UpdateUtils getInstance(Activity activity) {
        if (instance == null) {
            instance = new UpdateUtils();
        }
        return instance.init(activity);
    }

    private UpdateUtils init(Activity activity) {
        this.activity = activity;
        downloadUtil = DownloadUtil.getInstance(activity);
        downloadUtil.setOnDownloadCallBack(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return this;
    }

    public void checkDownload(UpdateInfo version, DownloadListener listener) {
        this.listener = listener;
        if (version == null) return;
        if (version.getVersionCode() <= getVersionCode()) return;
        String name = "newVersion_" + version.getVersionName() + ".apk";
        this.isForce = version.isForce();
        //需要运行时权限
        if (Build.VERSION.SDK_INT >= 23 && activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            if (downloadUtil.isExistApp()) {//本地已经存在
                downloadUtil.setName(name);
                downloadUtil.installAPK(sharedPreferences.getLong(DownloadManager.EXTRA_DOWNLOAD_ID, -1L));
            } else {
                startDownload(version, name);
            }
        }
    }

    private void startDownload(UpdateInfo version, String name) {
        if (!isForce) { //强制更新，显示进度，不需要通知
            downloadUtil.startDownload(version.getUrl(), name);
        } else {  //非强制更新，弹框消失，显示通知
            downloadUtil.startDownload(version.getUrl(), "应用更新", version.getTitle(), name);
        }
    }


    @Override
    public void onDownload(long[] download) {
        if (download == null) return;
        double percentage = (double) download[1] / download[2] * 100;
        if (percentage <= 0) {
            percentage = 0;
        }
        if (listener != null) {
            listener.onProgress((int) percentage);
        }

        if (DownloadManager.STATUS_SUCCESSFUL == download[0]) {
            if (listener != null) {
                listener.onComplete();
            }
        } else if (DownloadManager.STATUS_FAILED == download[0]) {
            if (listener != null) {
                listener.onFailure();
            }
        }

    }

    /**
     * @return 版本号
     */
    public static int getVersionCode() {
        try {
            PackageManager manager = Utils.getApp().getPackageManager();
            PackageInfo info = manager.getPackageInfo(Utils.getApp().getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public interface DownloadListener {
        void onProgress(int progress);

        void onComplete();

        void onFailure();
    }
}
