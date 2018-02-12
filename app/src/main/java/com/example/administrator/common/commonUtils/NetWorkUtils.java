package com.example.administrator.common.commonUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Administrator on 2016/6/30.
 */
public class NetWorkUtils {
    private static final String TAG = "NetWorkUtils";
    /**
     * 无网络
     */
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    /**
     * 2G网络
     */
    public static final int NETWORK_TYPE_2G_GPRS = 1;
    public static final int NETWORK_TYPE_2G_EDGE = 2;
    public static final int NETWORK_TYPE_2G_CDMA = 4;
    public static final int NETWORK_TYPE_2G_1xRTT = 7;
    public static final int NETWORK_TYPE_2G_IDEN = 11;
    public static final int NETWORK_TYPE_2G_GSM = 16;
    /**
     * 3G网络
     */
    public static final int NETWORK_TYPE_3G_UMTS = 3;
    public static final int NETWORK_TYPE_3G_EVDO_0 = 5;
    public static final int NETWORK_TYPE_3G_EVDO_A = 6;
    public static final int NETWORK_TYPE_3G_HSDPA = 8;
    public static final int NETWORK_TYPE_3G_HSUPA = 9;
    public static final int NETWORK_TYPE_3G_HSPA = 10;
    public static final int NETWORK_TYPE_3G_EVDO_B = 12;
    public static final int NETWORK_TYPE_3G_EHRPD = 14;
    public static final int NETWORK_TYPE_3G_HSPAP = 15;
    public static final int NETWORK_TYPE_3G_TD_SCDMA = 17;
    /**
     * 4G 网络
     */
    public static final int NETWORK_TYPE_4G_LTE = 13;
    /**
     * 未知待考察网络
     */
    public static final int NETWORK_TYPE_IWLAN = 18;

    public static void showLog(String content) {
        Log.i(TAG, content);
    }

    /**
     * 得到当前活跃的网络类型
     *
     * @param context
     * @return
     */
    public static String getNetWorkType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        String result = null;

        if (networkInfo != null && networkInfo.isAvailable()) {
            String name = networkInfo.getTypeName();
            showLog("networkInfo.getTypeName():" + name);
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    result = "当前是WIFI网络";
                    break;
                case ConnectivityManager.TYPE_ETHERNET:
                    result = "当前是以太有线网络";
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    result = getMobileNetType(networkInfo);
                    break;
            }
        } else {
            result = "网络不可用";
        }
        return result;
    }

    public static String getMobileNetType(NetworkInfo networkInfo) {
        String result = null;
        String subtypeName = networkInfo.getSubtypeName();
        showLog(subtypeName);
        int netWorkType = networkInfo.getSubtype();
        switch (netWorkType) {
            case 0:
                result = "无网络";
                break;
            case 1:
                result = "2G网络";
                break;
            case 2:
                result = "2G网络";
                break;
            case 3:
                result = "3G网络";
                break;
            case 4:
                result = "2G网络";
                break;
            case 5:
            case 6:
                result = "3G网络";
                break;
            case 7:
                result = "2G网络";
                break;
            case 8:
            case 9:
            case 10:
                result = "3G网络";
                break;
            case 11:
                result = "2G网络";
                break;
            case 12:
                result = "3G网络";
                break;
            case 13:
            case 14:
            case 15:
                result = "3G网络";
                break;
            case 16:
                result = "2G网络";
                break;
            case 17:
                result = "3G网络";
                break;
            case 18:
                result = "IWLAN网络";
                break;
            default:
                result = "无网络";
                break;
        }
        return result;
    }
}
