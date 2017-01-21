package com.example.administrator.common.commonUtils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/11.
 */

public class CommonUtils {

    /**
     * 对数据（字节）进行Base64编码
     *
     * @param data 要编码的数据（字节数组）
     * @return 返回编码后的字符串
     */
    public static String Base64Encode(byte[] data) {
        String ret = null;
        if (data != null && data.length > 0) {
            ret = Base64.encodeToString(data, Base64.NO_WRAP);
        }
        return ret;
    }

    /**
     * 对Base64编码后的数据进行还原
     *
     * @param data 使用Base64编码过的数据
     * @return 返回还原后的数据（字节数组）
     */
    public static byte[] Base64Decode(String data) {
        byte[] ret = null;
        if (data != null && data.length() > 0) {
            ret = Base64.decode(data, Base64.NO_WRAP);
        }
        return ret;
    }

    /**
     * 使用MD5获取数据的摘要信息
     *
     * @param data 数据
     * @return 摘要信息
     */
    public static String toMD5(byte[] data) {
        String ret = null;
        try {
            byte[] digest = MessageDigest.getInstance("md5").digest(data);
            ret = Base64Encode(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 将GBK转化为UTF8
     * @param gbkStr
     * @return
     */
    public static byte[] getUTF8BytesFromGBKString(String gbkStr) {
        int n = gbkStr.length();
        byte[] utfBytes = new byte[3 * n];
        int k = 0;
        for (int i = 0; i < n; i++) {
            int m = gbkStr.charAt(i);
            if (m < 128 && m >= 0) {
                utfBytes[k++] = (byte) m;
                continue;
            }
            utfBytes[k++] = (byte) (0xe0 | (m >> 12));
            utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));
            utfBytes[k++] = (byte) (0x80 | (m & 0x3f));
        }
        if (k < utfBytes.length) {
            byte[] tmp = new byte[k];
            System.arraycopy(utfBytes, 0, tmp, 0, k);
            return tmp;
        }
        return utfBytes;
    }

    /**
     * 对给定的字符串返回唯一的标记字符串
     * 主要应用于网络url的标记，将url转换映射成唯一的标识字符串
     * @param str 需要转换的字符串
     * @return 返回唯一的标识
     */
    public static String toHash(String str) {
        String ret = null;
        if (str != null && str.length() > 0) {
            int len = str.length();
            String part1 = str.substring(0, len / 2).hashCode() + "";
            String part2 = str.substring(len / 2).hashCode() + "";
            ret = part1 + part2;
        }
        return ret;
    }

    /**
     * 打开或关闭WIFI
     *
     * @param mContext Context
     * @param action   打开使用on 关闭使用off
     */
    public static void onWifi(Context mContext, String action) {
        WifiManager wm = ((WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE));
        if (action.toLowerCase().equalsIgnoreCase("on")) {
            if (!wm.isWifiEnabled()) {
                wm.setWifiEnabled(true);
            }
        }

        if (action.toLowerCase().equalsIgnoreCase("off")) {
            if (wm.isWifiEnabled()) {
                wm.setWifiEnabled(false);
            }
        }
    }

    /**
     * 隐藏键盘
     * @param mContext
     * @param v
     */
    public static void hideKeyboard(Context mContext, View v) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏键盘，自动获取焦点的view
     * @param context
     */
    public static void hideSoftKeyboard(Context context) {
        View focus_view = ((Activity) context).getCurrentFocus();
        if (focus_view != null) {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(focus_view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示软键盘
     * @param context
     * @param v
     */
    public static void showSoftKeyboard(Context context, View v) {
        v.requestFocus();
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(v, InputMethodManager.RESULT_SHOWN);
    }

    /**
     * 复制到剪切板
     * @param context
     * @param str
     */
    public static void copyToClipBoard(Context context, String str) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(str, str);
        clipboard.setPrimaryClip(clip);
    }

    /**
     * 检查是否有网络连接
     * @param context
     * @return
     */
    public static boolean checkNet(Context context) {
        if (context == null) return false;
        boolean isOk = false;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo[] networks = manager.getAllNetworkInfo();
            for (NetworkInfo network : networks) {
                NetworkInfo.State state = network.getState();
                if (NetworkInfo.State.CONNECTED == state) {
                    isOk = true;
                    break;
                }
            }
        }
        return isOk;
    }

    /**
     * 检索当前版本号
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        int verCode = 1;
        try {
            verCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verCode;
    }

    /**
     * 检索当前版本名称
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String verCode = "1.0";
        try {
            verCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verCode;
    }

    /**
     * 用分隔符将数组组合为字符串
     * @param array
     * @param splitStr
     * @return
     */
    public static String join(String[] array, String splitStr) {
        if (array == null)
            return "";

        StringBuilder str = new StringBuilder();

        for (int i = 0; i < array.length; i++) {
            str.append(array[i]);
            if (i != array.length - 1) {
                str.append(splitStr);
            }
        }

        return str.toString();
    }

    /**
     * 获取内存信息
     * @param context
     * @return
     */
    public static ActivityManager.MemoryInfo getMemoryInfo(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(mi);
        return mi;
    }



    /**
     * 获取设备信息
     * @return
     */
    public static DeviceInfo getDeviceInfo() {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.osVersion = System.getProperty("os.version");
        deviceInfo.brand = Build.BRAND;
        deviceInfo.board = Build.BOARD;
        deviceInfo.cpu_abi = Build.CPU_ABI;
        deviceInfo.cpu_abi2 = Build.CPU_ABI2;
        deviceInfo.device = Build.DEVICE;
        deviceInfo.display = Build.DISPLAY;
        deviceInfo.hardware = Build.HARDWARE;
        deviceInfo.host = Build.HOST;
        deviceInfo.manufacturer = Build.MANUFACTURER;
        deviceInfo.model = Build.MODEL;
        deviceInfo.product = Build.PRODUCT;
        deviceInfo.release = Build.VERSION.RELEASE;
        deviceInfo.sdk = Build.VERSION.SDK_INT;
        return deviceInfo;
    }

    public static class DeviceInfo {
        public String osVersion;
        public String brand;
        public String board;
        public String cpu_abi;
        public String cpu_abi2;
        public String device;
        public String display;
        public String hardware;
        public String host;
        public String manufacturer;
        public String model;
        public String product;
        public String release;
        public int sdk;
    }

    /**
     * 获取进程名字
     * @param context
     * @return
     */
    public static String getMyProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return null;
    }

    /**
     * 判断应用是否在前台
     * @param context
     * @return
     */
    public static boolean isForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        if (cn != null) {
            return cn.getPackageName().equals(context.getPackageName());
        }
        return false;
    }

    /**
     * 判断活动是否在前台
     * @param mContext
     * @param activities
     * @return
     */
    public static boolean isActivityForeground(Context mContext, List<String> activities) {
        if (activities == null) return false;
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        for (String activity : activities) {
            if (activity.equals(cn.getClassName())) return true;
        }
        return false;
    }

    /**
     * 解析获取url参数的键值对
     * 此方法采用字符串分割的方法
     * @param url
     * @return
     */
    public static Map<String, String> getUrlParams(String url) {
        Map<String, String> map = new HashMap<String, String>();
        String[] urlSplit = url.split("[?]");
        String paramsStr = "";
        if (urlSplit.length == 2) {
            paramsStr = urlSplit[1];
        }
        if (!paramsStr.isEmpty()) {
            String[] paramsSplit = paramsStr.split("[&]");
            for (String param : paramsSplit) {
                String[] paramSplit = param.split("[=]");
                if (paramSplit.length == 2) {
                    map.put(paramSplit[0], paramSplit[1]);
                }
            }
        }
        return map;
    }

    /**
     * 另外一种方法获得url的param集合(未测试)
     * @param url
     * @return
     */
    public static HashMap<String, String> getUrlParams2(String url){
        Uri uri = Uri.parse(url);
        HashMap<String, String> map = new HashMap<>();
        for (String str : uri.getQueryParameterNames()) {
            map.put(str, uri.getQueryParameters(str).get(0));
        }
        return map;
    }

    /**
     * 判断是否在主线程
     * @return
     */
    public static boolean isInUIThread() {
        return "main".equals(Thread.currentThread().getName());
    }

    /**
     * 判断系统是否处在锁屏状态
     * @param context
     * @return
     */
    private boolean isScreenLocked(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return keyguardManager.inKeyguardRestrictedInputMode();
    }

    /**
     * 自动打开屏幕并解锁（无密码状态）
     * @param context
     */
    private void wakeAndUnlock(Context context) {
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");

        //点亮屏幕
        wl.acquire(1000);

        //得到键盘锁管理器对象
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");

        //解锁
        kl.disableKeyguard();

    }
}
