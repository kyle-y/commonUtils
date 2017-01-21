package com.example.administrator.common.commonUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Administrator on 2017/1/13.
 */

public class NumberUtils {

    /**
     * 用于计算百分比的方法，可以设置精确的小数位数和四舍五入的模式
     * @param a
     * @param b
     * @param count
     * @return
     */
    public static String perCentum(int a, int b, int count) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        df.setMaximumFractionDigits(count);//可以设置精确几位小数
        df.setRoundingMode(RoundingMode.UP);//设置四舍五入的方式
        String str = df.format((float) a / (float) b * 100);
        return str;
    }
}
