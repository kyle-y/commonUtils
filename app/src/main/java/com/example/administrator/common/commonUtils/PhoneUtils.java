package com.example.administrator.common.commonUtils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2017/6/14.
 */

public class PhoneUtils {
    public static boolean isMonitor(Context context) {
        if (getIMIEStatus(context).equals("000000")) {
            return true;
        }

        if (isOpenBlueTooth1(context) == false || isOpenBlueTooth2(context) == false) {
            return true;
        }

        if (getSerial().equals("unknown") || getSerial().equals("Android")) {
            return true;
        }

        if (readCpuInfo().startsWith("processor\t: 0vendor_id")) {
            return true;
        }
        return false;
    }

    // IMEI码
    private  static String getIMIEStatus(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = null;
        try {
            deviceId = tm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
            deviceId = "000000";
        }
        return deviceId;
    }

    // Mac地址
    private static String getLocalMac(Context context) {
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    // Android Id
    private static String getAndroidId(Context context) {
        String androidId = Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    private static String getBuildSdk(){
        return Build.MODEL;
    }

    private static String getSerial(){
        return android.os.Build.SERIAL;
    }

    private static String readCpuInfo() {
        String result = "";
        try {
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            ProcessBuilder cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            StringBuffer sb = new StringBuffer();
            String readLine = "";
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
            while ((readLine = responseReader.readLine()) != null) {
                sb.append(readLine);
            }
            responseReader.close();
            result = sb.toString().toLowerCase();
        } catch (IOException ex) {
        }
        return result;
    }

    private static boolean isOpenBlueTooth1(Context context){
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
    }

    private static boolean isOpenBlueTooth2(Context context){
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }
}
