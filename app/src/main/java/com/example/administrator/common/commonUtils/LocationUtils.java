package com.example.administrator.common.commonUtils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

import com.example.administrator.newappforutils.BuildConfig;

/**
 * 1,位置信息（物理定位功能是否开启，注意跟定位权限不同）
 * 当定位时，首先检查这两项，如果没有开启，提示开启，如果开启过了，请求定位权限
 * 2，定位权限（ACCESS_COARSE_LOCATION 和 ACCESS_FINE_LOCATION）
 * 获取错略位置    android.permission.ACCESS_COARSE_LOCATION，通过WiFi或移动基站的方式获取用户错略的经纬度信息，定位精度大概误差在30~1500米
 * 获取精确位置    android.permission.ACCESS_FINE_LOCATION，通过GPS芯片接收卫星的定位信息，定位精度达10米以内
 */

public class LocationUtils {

    //如果需要返回做某些操作，可以使用这里的返回码
    public static final int LOC_SETTING = 600;
    public static final int LOC_PERMISSION = 601;

    /**
     * 判断GPS是否打开
     *
     * @param context
     * @return
     */
    public static boolean isGPSEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    /**
     * 是否开启定位权限
     *
     * @param context
     * @return
     */
    public static boolean checkLoactionOpen(Context context) {
        boolean location = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            location = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            location = PermissionChecker.checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION,
                    Process.myUid(), Process.myUid(), context.getPackageName())
                    == PackageManager.PERMISSION_GRANTED;
        }
        if (!location) {
            return false;
        }
        return true;
    }

    /**
     * 跳转开启位置信息
     * 返回码 LOC_SETTING
     *
     * @param context
     */
    public static void showLocServiceDialog(final Activity context) {
        new AlertDialog.Builder(context)
                .setTitle("手机未开启位置服务")
                .setMessage("请在 设置-位置信息 (将位置服务打开))")
                .setNegativeButton("取消", null)
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        try {
                            context.startActivityForResult(intent, LOC_SETTING);
                        } catch (ActivityNotFoundException e) {
                            intent.setAction(Settings.ACTION_SETTINGS);
                            try {
                                context.startActivityForResult(intent, LOC_SETTING);
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                }).show();
    }

    /**
     * 跳转开启位置权限
     * 返回码 LOC_PERMISSION
     *
     * @param context
     */
    public static void showLocIgnoredDialog(final Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("手机已关闭位置权限,无法显示数据!");
        builder.setMessage("请在 设置-应用权限 (将位置权限打开))");
        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent localIntent = new Intent();
                if (Build.VERSION.SDK_INT >= 9) {
                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    localIntent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
                } else if (Build.VERSION.SDK_INT <= 8) {
                    localIntent.setAction("android.intent.action.VIEW");
                    localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                    localIntent.putExtra("com.android.settings.ApplicationPkgName", BuildConfig.APPLICATION_ID);
                }
                context.startActivityForResult(localIntent, LOC_PERMISSION);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(true);
        builder.create().show();
    }
}
