package com.self.kyle.java_module;

import java.util.Arrays;

public class myClass {

    public static void main(String[] args) {

        int total = 10000_000;

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


        int[] b = new int[total / 2 - 2];
        count = 0;
        for (int i = 6; i <= total; i++) {
            if (i % 2 == 0) {
                b[count++] = i;
            }
        }
//        System.out.println(Arrays.toString(b));


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

        System.out.print(total + "以内的偶数是否都能用“1+1”表示?："+ (b.length == c.length ? "能" : "不能"));
    }
}
