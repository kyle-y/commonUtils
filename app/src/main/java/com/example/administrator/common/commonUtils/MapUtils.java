package com.example.administrator.common.commonUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

/**
 * 跳转地图的工具类
 * GCJ-02火星坐标系  高德、腾讯、Google中国地图使用
 * WGS-84 地心坐标系，即GPS原始坐标体系  GoogleEarth及GPS芯片使用
 * BD-09坐标  百度地图使用
 */

public class MapUtils {

    public static final int TO_MAP_CODE = 121;
    private static final String BAIDU_MAP = "com.baidu.BaiduMap";
    private static final String GAODE_MAP = "com.autonavi.minimap";

    public static void openBaiduMap(Activity context,
                                    String cityName,
                                    double startLat, double startLng,
                                    double endLat, double endLng,
                                    String startName, String endName) {
        //FIXME 如果是高德转百度，需要转换，如果百度坐标跳百度不需要转换
        double[] newStart = getBaiduLatlon(startLat, startLng);
//        double[] newStart = new double[]{startLat, startLng};
        double[] newEnd = getBaiduLatlon(endLat, endLng);
//        double[] newEnd = new double[]{endLat, endLng};
        if (isAvilible(context, BAIDU_MAP)) {
            jumpToBaiDuMap(context, newStart[0], newStart[1], newEnd[0], newEnd[1], startName, endName);
        } else {
            jumpToBaiduWeb(context, cityName, newStart[0], newStart[1], newEnd[0], newEnd[1], startName, endName);
        }
    }

    public static void openGaodeMap(Activity context, double startLat, double startLng, double endLat, double endLng, String startName, String endName) {
        //FIXME 如果是高德转高德，不需要转换，如果百度坐标跳高德，需要转换
        double[] newStart = getGodelatlon(startLat, startLng);
//        double[] newStart = new double[]{startLat, startLng};
        double[] newEnd = getGodelatlon(endLat, endLng);
//        double[] newEnd = new double[]{endLat, endLng};
        if (isAvilible(context, GAODE_MAP)) {
            jumpToGaodeMap(context, newStart[0], newStart[1], newEnd[0], newEnd[1], startName, endName);
        } else {
            jumpToGaodeWeb(context, newStart[0], newStart[1], newEnd[0], newEnd[1], startName, endName);
        }
    }

    private static double[] getGodelatlon(double lat, double lng) {
        return CoordinateUtils.bd09ToGcj02(lng, lat);
    }

    private static double[] getBaiduLatlon(double lat, double lng) {
        return CoordinateUtils.gcj02ToBd09(lng, lat);
    }


    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context
     * @param packageName：应用包名
     * @return
     */
    private static boolean isAvilible(Context context, String packageName) {
        // 获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        // 用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        // 从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        // 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    /**
     * 跳转到百度地图
     *
     * @param context
     * @param startLat
     * @param startLng
     * @param endLat
     * @param endLng
     */
    private static void jumpToBaiDuMap(Activity context, double startLat, double startLng, double endLat, double endLng, String startName, String endName) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("baidumap://map/direction?" +
                "origin=latlng:" + startLat + "," + startLng + "|name:" + startName +//起点坐标
                "&destination=latlng:" + endLat + "," + endLng + "|name:" + endName +//终点坐标
                "&mode=riding" + // 可选transit（公交）、driving（驾车）、walking（步行）和riding（骑行
                "&sy=0")); //推荐路线
        context.startActivityForResult(intent, TO_MAP_CODE); // 启动调用
    }

    /**
     * 跳转到高德地图
     *
     * @param context
     * @param startLat
     * @param startLng
     * @param endLat
     * @param endLng
     */
    private static void jumpToGaodeMap(Activity context, double startLat, double startLng, double endLat, double endLng, String startName, String endName) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("amapuri://route/plan/?" +
                "&slat=" + startLat +   //起点经度
                "&slon=" + startLng +  //起点纬度
                "&dlat=" + endLat +   //终点经度
                "&dlon=" + endLng +  //终点纬度
                "&sname=" + startName +  //起点名称
                "&dname=" + endName +  //终点名称
                "&dev=0" +  //起终点是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
                "&t=3")); //t = 0（驾车）= 1（公交）= 2（步行）= 3（骑行）= 4（火车）= 5（长途客车）
        context.startActivityForResult(intent, TO_MAP_CODE); // 启动调用
    }

    /**
     * 调起浏览器打开，使用高德web支持
     *
     * @param context
     * @param startLat
     * @param startLng
     * @param endLat
     * @param endLng
     */
    public static void jumpToGaodeWeb(Activity context, double startLat, double startLng, double endLat, double endLng, String startName, String endName) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://uri.amap.com/navigation?" +
                "from=" + startLng + "," + startLat + "," + startName +
                "&to=" + endLng + "," + endLat + "," + endName +
                "&mode=ride"));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(intent, "选择浏览器打开导航"));
        } else {
            Toast.makeText(context, "没有匹配的程序", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 调起浏览器打开，使用百度web支持
     *
     * @param context
     * @param startLat
     * @param startLng
     * @param endLat
     * @param endLng
     */
    public static void jumpToBaiduWeb(Activity context,
                                      String cityName,
                                      double startLat, double startLng,
                                      double endLat, double endLng,
                                      String startName, String endName) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://api.map.baidu.com/direction?" +
                "origin=latlng:" + startLat + "," + startLng + "|name:" + startName +//起点坐标
                "&destination=latlng:" + endLat + "," + endLng + "|name:" + endName +//终点坐标
                "&mode=walking" + // 可选transit（公交）、driving（驾车）、walking（步行)
                "&region=" + cityName +
                "&output=html" +
                "&src=yourCompanyName"));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(intent, "选择浏览器打开导航"));
        } else {
            Toast.makeText(context, "没有匹配的程序", Toast.LENGTH_SHORT).show();
        }
    }

}
