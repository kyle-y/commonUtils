package com.example.administrator.reflect.reflectType1;

import android.os.IBinder;
import android.os.IInterface;

import com.example.administrator.reflect.reflectType1.core.MethodParams;
import com.example.administrator.reflect.reflectType1.core.RefClass;
import com.example.administrator.reflect.reflectType1.core.RefStaticMethod;

/**
 * Demo:展示这个框架的用法
 * 此框架来自@lody -- VirtualApp，用于建立影像
 * 优点：用于大量反射一些既有的类，根据类加载的特性，只有用到该类时才发生反射
 * 用法：自定义的镜像类中需要反射的属性和方法置为static，并使用RefClass.load（包括内部类）
 * 方法的反射有两种：@MethodParams和@MethodReflectParams用于设置参数，方法泛型用来设置返回值
 */
public class IWifiManager {
    public static Class<?> TYPE = RefClass.load(IWifiManager.class, "android.net.wifi.IWifiManager");

    public static class Stub {
        public static Class<?> TYPE = RefClass.load(Stub.class, "android.net.wifi.IWifiManager$Stub");
        @MethodParams({IBinder.class})
        public static RefStaticMethod<IInterface> asInterface;
    }
}
