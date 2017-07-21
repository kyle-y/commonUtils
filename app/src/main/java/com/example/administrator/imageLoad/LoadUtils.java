package com.example.administrator.imageLoad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileDescriptor;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/7/21.
 */

public class LoadUtils {

    private static final int CUP_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_THREAD_SIZE = CUP_COUNT + 1;
    private static final int MAX_THREAD_SIZE = CUP_COUNT * 2 + 1;
    private static final long KEEP_ALIVE = 60L;
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r);
        }
    };
    public static final Executor THRED_POOL_EXCUTOR = new ThreadPoolExecutor(
            CORE_THREAD_SIZE,
            MAX_THREAD_SIZE,
            KEEP_ALIVE,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(),
            THREAD_FACTORY);

    /**
     * 获得磁盘缓存的缓存路径
     *
     * @param context
     * @return
     */
    public static File getDiskCacheFile(Context context) {
        boolean isSDcardEnable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (isSDcardEnable) {
            return context.getExternalCacheDir();
        } else {
            return context.getCacheDir();
        }
    }

    /**
     * 将url进行转化之后作为key
     * 1，将其Md5加密成固定长度
     * 2，转化为十六进制字符串
     *
     * @param url
     * @return
     */
    public static String hashKeyFromUrl(String url) {
        String key = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(url.getBytes());
            key = bytesToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            key = String.valueOf(url.hashCode());
        }
        return key;
    }

    /**
     * 将bytes转化为16进制
     *
     * @param bytes
     * @return
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0XFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 根据采样压缩比获得bitmap
     *
     * @param fd
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap getBitmapFromFD(FileDescriptor fd, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);  //此次加载是为了获得options的尺寸属性；
        options.inSampleSize = caculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

    /**
     * 计算采样压缩比
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        if (reqWidth == 0 || reqHeight == 0) {
            return inSampleSize;
        }
        int width = options.outWidth;
        int height = options.outHeight;
        while (width / inSampleSize > reqWidth || height / inSampleSize > reqHeight) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }


}
