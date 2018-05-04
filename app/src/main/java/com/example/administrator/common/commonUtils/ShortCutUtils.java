package com.example.administrator.common.commonUtils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * Created by yxb on 2018/5/4.
 * 为不同的手机添加图标右上角的数字
 */

public class ShortCutUtils {


    public static void setBadge(Context paramContext, int paramInt) {
        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            xiaoMiShortCut(paramContext, paramInt + "");
            return;
        }
        if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            samsungShortCut(paramContext, paramInt + "");
            return;
        }
        if (Build.MANUFACTURER.equalsIgnoreCase("huawei")) {
            setHuaweiBadge(paramContext, paramInt);
            return;
        }
        if (Build.MANUFACTURER.equalsIgnoreCase("OPPO")) {
            changeOPPOBadge(paramContext, paramInt);
            return;
        }
    }


    /***
     * 三星手机：应用图标的快捷方式上加数字
     * @param context
     * @param num
     */
    public static void samsungShortCut(Context context, String num) {
        int numInt = Integer.valueOf(num);
        if (numInt < 1) {
            num = "0";
        } else if (numInt > 99) {
            num = "99";
        }
        String activityName = getLauncherClassName(context);
        Intent localIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        localIntent.putExtra("badge_count", Integer.parseInt(num));
        localIntent.putExtra("badge_count_package_name", context.getPackageName());
        localIntent.putExtra("badge_count_class_name", activityName);
        context.sendBroadcast(localIntent);
    }


    /***
     * 索尼手机：应用图标的快捷方式上加数字
     * @param context
     * @param num
     */
    public static void sonyShortCut(Context context, String num) {
        String activityName = getLauncherClassName(context);
        if (activityName == null) {
            return;
        }
        Intent localIntent = new Intent();
        int numInt = Integer.valueOf(num);
        boolean isShow = true;
        if (numInt < 1) {
            num = "";
            isShow = false;
        } else if (numInt > 99) {
            num = "99";
        }
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", isShow);
        localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", activityName);
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", num);
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());
        context.sendBroadcast(localIntent);
    }


    /***
     * 在小米应用图标的快捷方式上加数字<br>
     * @param context
     * @param num 显示的数字：大于99，为"99"，当为""时，不显示数字，相当于隐藏了)<br><br>
     * 注意点：
     * context.getPackageName()+"/."+clazz.getSimpleName() （这个是启动activity的路径）中的"/."不能缺少
     *
     */
    public static void xiaoMiShortCut(Context context, String num) {
        String activityName = getLauncherClassName(context);
        if (activityName == null) {
            return;
        }
        Intent localIntent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
        localIntent.putExtra("android.intent.extra.update_application_component_name", context.getPackageName() + "/." + activityName);
        if (TextUtils.isEmpty(num)) {
            num = "";
        } else {
            int numInt = Integer.valueOf(num);
            if (numInt > 0) {
                if (numInt > 99) {
                    num = "99";
                }
            } else {
                num = "0";
            }
        }
        localIntent.putExtra("android.intent.extra.update_application_message_text", num);
        context.sendBroadcast(localIntent);
    }

    /**
     * vivo手机
     *
     * @param context
     * @param num
     */
    public static void vivoShortCut(Context context, String num) {
        String activityName = getLauncherClassName(context);
        if (activityName == null) {
            return;
        }
        int numInt = Integer.valueOf(num);
        boolean isShow = true;
        if (numInt < 1) {
            num = "";
            isShow = false;
        } else if (numInt > 99) {
            num = "99";
        }
        Intent localIntent1 = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
        localIntent1.putExtra("packageName", context.getPackageName());
        localIntent1.putExtra("className", activityName);
        localIntent1.putExtra("notificationNum", num);
        context.sendBroadcast(localIntent1);
    }

    /**
     * 华为手机
     *
     * @param paramContext
     * @param paramInt
     */
    public static void setHuaweiBadge(Context paramContext, int paramInt) {
        try {
            String className = getLauncherClassName(paramContext);
            if (className != null) {
                Bundle localBundle = new Bundle();
                localBundle.putString("package", paramContext.getPackageName());
                localBundle.putString("class", className);
                localBundle.putInt("badgenumber", paramInt);
                paramContext.getContentResolver().
                        call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, localBundle);
                return;
            }
        } catch (Throwable localThrowable) {
            return;
        }
    }


    /**
     * oppo手机
     *
     * @param paramContext
     * @param paramInt
     */
    public static void changeOPPOBadge(Context paramContext, int paramInt) {
        try {
            Bundle localBundle = new Bundle();
            localBundle.putInt("app_badge_count", paramInt);
            paramContext.getContentResolver().
                    call(Uri.parse("content://com.android.badge/badge"), "setAppBadgeCount", null, localBundle);
        } catch (Exception localException) {
            return;
        }
    }


    private static String mLauncherClassName = "";
//    private static String mLauncherClassName = MainActivity.class.getSimpleName();

    private static String getLauncherClassName(Context context) {
        if (!TextUtils.isEmpty(mLauncherClassName))
            return mLauncherClassName;
        PackageManager packageManager = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        // To limit the components this Intent will resolve to, by setting an
        // explicit package name.
        intent.setPackage(context.getPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // All Application must have 1 Activity at least.
        // Launcher activity must be found!
        ResolveInfo info = packageManager
                .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        // get a ResolveInfo containing ACTION_MAIN, CATEGORY_LAUNCHER
        // if there is no Activity which has filtered by CATEGORY_DEFAULT
        if (info == null) {
            info = packageManager.resolveActivity(intent, 0);
        }

        return info.activityInfo.name;
    }

}
