package com.example.administrator.update;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;

/**
 * 用于下载的工具类
 * 重要：一定要注册下载监听器，并申请FileProvider
 */

public class DownloadUtil {
    private static DownloadUtil instance;
    private SharedPreferences sharedPreferences;
    private DownloadManager mDownloadManager;
    private Context mContext;
    private String name;
    private long id;
    private boolean flag = true;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                long[] downloadInfo = (long[]) msg.obj;
                if (onDownloadCallBack != null) {
                    onDownloadCallBack.onDownload(downloadInfo);
                }
                if (DownloadManager.STATUS_SUCCESSFUL == downloadInfo[0]) {
                    flag = false;
                } else if (DownloadManager.STATUS_FAILED == downloadInfo[0]) {
                    flag = false;
                    Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private DownloadUtil(Context context) {
        mContext = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public static DownloadUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (DownloadUtil.class) {
                if (instance == null) {
                    instance = new DownloadUtil(context);
                }
            }
        }
        return instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 开始下载, 显示Notification，主要用于非强制更新
     *
     * @param url
     * @param title
     * @param description
     * @param name
     */
    public void startDownload(String url, String title, String description, String name) {
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(mContext, "下载地址为空", Toast.LENGTH_SHORT).show();
            return;
        }
        this.name = name;
        //先获取本地downloadID 查看下载状态
        long downloadID = sharedPreferences.getLong(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
        if (downloadID == -1L) {
            //本地不存在直接下载
            download(url, title, description, name);
        } else {
            if (DownloadManager.STATUS_SUCCESSFUL == getDownloadInfo(downloadID)[0]) {
                Uri uri = getDownloadUri(downloadID);
                if (uri == null) {
                    download(url, title, description, name);
                } else {
                    if (compareDownloadInfo(uri)) {
                        // 直接安装
                        installAPK(downloadID);
                    } else {
                        //移除存在的downloadID
                        mDownloadManager.remove(downloadID);
                        //重新下载
                        download(url, title, description, name);
                    }
                }
            } else {
                download(url, title, description, name);
            }
        }
    }

    /**
     * 开始下载, 不显示Notification， 主要用于强制更新
     *
     * @param url
     * @param name
     */
    public void startDownload(String url, String name) {
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(mContext, "下载地址为空", Toast.LENGTH_SHORT).show();
            return;
        }
        this.name = name;
        //先获取本地downloadID 查看下载状态
        long downloadID = sharedPreferences.getLong(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
        if (downloadID == -1L) {
            //本地不存在直接下载
            download(url, name);
        } else {
            if (DownloadManager.STATUS_SUCCESSFUL == getDownloadInfo(downloadID)[0]) {
                Uri uri = getDownloadUri(downloadID);
                if (uri == null) {
                    download(url, name);
                } else {
                    if (compareDownloadInfo(uri)) {
                        // 直接安装
                        installAPK(downloadID);
                    } else {
                        //移除存在的downloadID
                        mDownloadManager.remove(downloadID);
                        //重新下载
                        download(url, name);
                    }
                }
            } else {
                download(url, name);
            }
        }
    }

    /**
     * 判断本地是否存在安装包
     *
     * @return
     */
    public boolean isExistApp() {
        long downloadID = sharedPreferences.getLong(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
        if (downloadID != -1L && DownloadManager.STATUS_SUCCESSFUL == getDownloadInfo(downloadID)[0] &&
                getDownloadUri(downloadID) != null && compareDownloadInfo(getDownloadUri(downloadID))) {
            return true;
        }
        return false;
    }


    /**
     * 下载
     *
     * @param url
     * @param title
     * @param description
     * @param name
     */
    private void download(String url, String title, String description, String name) {
        if (!checkPermission())
            return;
        final String packageName = "com.android.providers.downloads";
        int state = mContext.getPackageManager().getApplicationEnabledSetting(packageName);
        //检测下载管理器是否被禁用
        if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext).setTitle("温馨提示").setMessage
                    ("系统下载管理器被禁止，需手动打开").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    try {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + packageName));
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        mContext.startActivity(intent);
                    }
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        } else {
            DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));
            req.setAllowedOverRoaming(false);
            req.setVisibleInDownloadsUi(true);
            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
            req.setTitle(title);
            req.setDescription(description);
            id = mDownloadManager.enqueue(req);
            sharedPreferences.edit().putLong(DownloadManager.EXTRA_DOWNLOAD_ID, id).commit();
        }
    }

    /**
     * 下载
     *
     * @param url
     * @param name
     */
    private void download(String url, String name) {
        if (!checkPermission())
            return;
        final String packageName = "com.android.providers.downloads";
        int state = mContext.getPackageManager().getApplicationEnabledSetting(packageName);
        //检测下载管理器是否被禁用
        if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext).setTitle("温馨提示").setMessage
                    ("系统下载管理器被禁止，需手动打开").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    try {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + packageName));
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        mContext.startActivity(intent);
                    }
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        } else {
            DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));
            req.setAllowedOverRoaming(false);
            req.setVisibleInDownloadsUi(true);
            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
            id = mDownloadManager.enqueue(req);
            queryDownloadSize();
            sharedPreferences.edit().putLong(DownloadManager.EXTRA_DOWNLOAD_ID, id).commit();
        }
    }

    /**
     * 权限检查
     *
     * @return
     */
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (mContext.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                Toast.makeText(mContext, "需要运行时权限", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }


    /**
     * 查询文件下载情况
     */
    private void queryDownloadSize() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag) {
                    long[] downloadInfo = getDownloadInfo(id);
                    Message message = Message.obtain();
                    message.what = 0;
                    message.obj = downloadInfo;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    /**
     * 获取下载信息
     *
     * @param downloadID
     * @return
     */
    private long[] getDownloadInfo(long downloadID) {
        long[] downloadInfo = new long[]{-1, 0, 0};
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadID);
        Cursor cursor = mDownloadManager.query(query);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    //下载状态
                    downloadInfo[0] = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    //已经下载文件大小
                    downloadInfo[1] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    //下载文件的总大小
                    downloadInfo[2] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                }
            } finally {
                cursor.close();
            }
        }
        return downloadInfo;
    }

    /**
     * 通过downloadID获取文件下载的地址
     *
     * @param downloadID
     * @return
     */
    private Uri getDownloadUri(long downloadID) {
        return mDownloadManager.getUriForDownloadedFile(downloadID);
    }


    /**
     * 比较本地已经存在的文件 和 要下载的文件
     *
     * @param exitUri
     * @return 返回true直接用本地的文件, 返回false重新下载.
     */
    private boolean compareDownloadInfo(Uri exitUri) {
        PackageManager pm = mContext.getPackageManager();
        String dataColumn = getDataColumn(exitUri);
        if (TextUtils.isEmpty(dataColumn)) {
            return false;
        }
        PackageInfo existPackageInfo = pm.getPackageArchiveInfo(dataColumn, PackageManager.GET_ACTIVITIES);

        if (existPackageInfo == null) {
            return false;
        }
        String localPackageName = mContext.getPackageName();
        //本地存在的文件包名 和 要下载的包名不相等直接返回false 重新下载
        if (existPackageInfo.packageName.equals(localPackageName)) {
            try {
                PackageInfo localPackageInfo = mContext.getPackageManager().getPackageInfo(localPackageName, 0);
                //若本地存在的文件版本号大于 要下载的版本号，返回true，直接安装本地的
                if (existPackageInfo.versionCode > localPackageInfo.versionCode) {
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    private String getDataColumn(Uri uri) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * 安装APK
     */
    public void installAPK(long downloadID) {
        //8.0安装需要运行时权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean b = mContext.getPackageManager().canRequestPackageInstalls();
            if (b) {
                apk(downloadID);
            } else {
                //跳转到未知权限页面
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                mContext.startActivity(intent);
            }
        } else {
            apk(downloadID);
        }
    }


    /**
     * 安装apk
     */
    private void apk(long downloadID) {
        long exitDownloadID = sharedPreferences.getLong(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
        if (exitDownloadID == downloadID) {
            File apkFile =
                    new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), name);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", apkFile);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            }
            mContext.startActivity(intent);
        } else {
            Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
        }
    }

    private OnDownloadCallBack onDownloadCallBack;

    public void setOnDownloadCallBack(OnDownloadCallBack onDownloadCallBack) {
        this.onDownloadCallBack = onDownloadCallBack;
    }

    public interface OnDownloadCallBack {
        void onDownload(long[] download);
    }
}
