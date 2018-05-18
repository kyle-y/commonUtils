package com.self.kyle.java_module.test;

import java.util.Arrays;

/**
 * Created by yxb on 2018/5/18.
 */

public class MathTest {
    /**
     * 验证哥德巴赫猜想，“1+1=2”
     * 经测试10_000_000(1亿)以内的正整数成立
     */
    public static void testGDBH(){
        int total = 10_000_000;

        //将total内的所有质数放进数组a
        int[] a = new int[total / 2];
        int count = 0;

        for (int i = 2; i <= total; i++) {
            int temp = (int) Math.sqrt(i);
            if (i <= 3) {
                a[count++] = i;
            } else {
                for (int j = 2; j <= temp; j++) {
                    if (i % j == 0) {
                        break;
                    }
                    if (j >= temp) {
                        a[count++] = i;
                    }
                }

            }
        }
        a = Arrays.copyOf(a, count);
//        System.out.println(Arrays.toString(a));

        //将total内所有偶数放进数组b
        int[] b = new int[total / 2 - 2];
        count = 0;
        for (int i = 6; i <= total; i++) {
            if (i % 2 == 0) {
                b[count++] = i;
            }
        }
//        System.out.println(Arrays.toString(b));

        //穷举轮询，找到能表示两个质数之和的偶数放进数组c
        int[] c = new int[total / 2 - 2];
        count = 0;
        for (int i = 0; i < b.length; i++) {
            int sum = b[i];
            out:
            for (int j = 0; j < a.length; j++) {
                int x = a[j];
                for (int k = j; k < a.length; k++) {
                    int y = a[k];
                    if (x + y == sum) {
                        c[count++] = sum;
//                        System.out.println(x + "+" + y + "=" + sum + ";");
                        break out;
                    }
                }
            }
        }
//        System.out.println(Arrays.toString(c));

        //对比数组b和数组c内偶数的个数，相同则表示所有偶数全部都成立
        System.out.print(total + "以内的偶数是否都能用“1+1”表示?："+ (b.length == c.length ? "能" : "不能"));
    }
}
