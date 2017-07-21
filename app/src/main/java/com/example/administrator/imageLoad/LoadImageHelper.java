package com.example.administrator.imageLoad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.support.v4.util.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by Administrator on 2017/7/21.
 */

public class LoadImageHelper {

    private static final int INDEX = 0;
    private static final int BUFFER_SIZE = 8 * 1024;

    private LruCache<String, Bitmap> memoryCache;
    private DiskLruCache diskLruCache;

    private int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

    public LoadImageHelper(Context context) {
        memoryCache = new LruCache<>(maxMemory / 8);
        try {
            diskLruCache = DiskLruCache.open(LoadUtils.getDiskCacheFile(context), 1, 1, 50 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get bitmap from memory directly;
     * @param url
     * @return
     */
    public Bitmap loadFromMemoryCache(String url) {
        return memoryCache.get(LoadUtils.hashKeyFromUrl(url));
    }

    /**
     * Get bitmap from disk (do not need resize)
     * @see LoadImageHelper#loadFromDiskCache(String, int, int)
     * @param url
     * @return
     */
    public Bitmap loadFromDiskCache(String url) {
        return loadFromDiskCache(url, 0, 0);
    }

    /**
     * Get bitmap from disk, and then store it to memory;
     * @param url
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public Bitmap loadFromDiskCache(String url, int reqWidth, int reqHeight) {
        boolean isNeedResize = !(reqWidth == 0 && reqHeight == 0);
        String key = LoadUtils.hashKeyFromUrl(url);
        Bitmap bitmap = null;
        if (diskLruCache == null) {     //可能未创建成功
            return null;
        }
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
            if (snapshot == null) {
                return null;
            }
            InputStream is = snapshot.getInputStream(INDEX);
            if (isNeedResize) {
                FileInputStream fi = (FileInputStream) is;
                FileDescriptor fd = fi.getFD();
                bitmap = LoadUtils.getBitmapFromFD(fd, reqWidth, reqHeight);
            } else {
                bitmap = BitmapFactory.decodeStream(is);
            }
            //存到内存缓存，以便下次使用
            memoryCache.put(key, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * Get bitmap from web, use HttpURLConnection .After that, restore it to disk;
     * @param urlString
     * @return
     */
    public Bitmap loadFromWeb(String urlString) {
        Bitmap bitmap = null;
        String key = LoadUtils.hashKeyFromUrl(urlString);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("can not visit network in UI thread !");
        }
        HttpURLConnection connection = null;
        InputStream is = null;
        InputStream is1 = null;
        InputStream is2 = null;
        BufferedInputStream bi = null;
        BufferedOutputStream bo = null;
        ByteArrayOutputStream bos = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            is = connection.getInputStream();
            bos = new ByteArrayOutputStream();
            int b1;
            while ((b1 = is.read()) != -1) {
                bos.write(b1);
            }
            bos.flush();
            //将流复制为两份
            is1 = new ByteArrayInputStream(bos.toByteArray());
            is2 = new ByteArrayInputStream(bos.toByteArray());

            //一份用来加载成bitmap
            bi = new BufferedInputStream(is1, BUFFER_SIZE);
            bitmap = BitmapFactory.decodeStream(bi);

            //另一份存到本地磁盘缓存，以便下次使用
            DiskLruCache.Editor editor = diskLruCache.edit(key);
            OutputStream os = editor.newOutputStream(INDEX);
            bo = new BufferedOutputStream(os, BUFFER_SIZE);
            int b;
            while ((b = is2.read()) != -1) {
                bo.write(b);
            }
            bo.flush();
            editor.commit();
            diskLruCache.flush();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
            try {
                is.close();
                bos.close();
                is1.close();
                is2.close();
                bi.close();
                bo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}
