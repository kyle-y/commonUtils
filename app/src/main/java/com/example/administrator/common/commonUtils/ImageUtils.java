package com.example.administrator.common.commonUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by Administrator on 2017/1/13.
 */

public class ImageUtils {
    /**
     * 判断某个url是否为图片
     * @param url
     * @return
     */
    public static boolean isImage(String url) {
        String reg = "(?i).+?\\.(jpg|jpeg|png|bmp|gif|JPG|JPEG|PNG|BMP|GIF)";
        return url.matches(reg);
    }

    /**
     * 将bitmap存到某个地址
     */
    public static void storeImage(Bitmap bitmap, String outPath) throws FileNotFoundException {
        FileOutputStream os = new FileOutputStream(outPath);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
    }
    /**
     * 从某个地址取得bitmap
     */
    public static Bitmap getBitmap(String imgPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        newOpts.inSampleSize = 1;// Do not compress
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(imgPath, newOpts);
    }
    /**
     * Byte[]转Bitmap
     */
    public static Bitmap bytes2Bitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    /**
     * Bitmap转Byte[]
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * Bitmap转Drawable
     */
    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    /**
     * Drawable转Bitmap
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    /**
     * 旋转图像
     */
    public static Bitmap rotateBitmap(Bitmap bmp, int degrees) {
        if (degrees != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(degrees);
            return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        }
        return bmp;
    }

    /**
     * 得到bitmap的大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }

    /**
     * 压缩图片，并将图片转化为base4,该方法适用于小图片的上传
     * @param bitmap
     * @return
     * @throws IOException
     */
    public static String compressImageToBase64(Bitmap bitmap) throws IOException {
        if (bitmap == null) return "";
        String base64String;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);//压缩质量的设置
        baos.flush();
        baos.close();
        byte[] imgByte = baos.toByteArray();
        base64String = Base64.encodeToString(imgByte, Base64.DEFAULT);
        return base64String;
    }

    /**
     * 根据当前时间，获取文件名,时间戳+5位随机
     * @return
     */
    public static String fileNameFromTime() {
        Date dt = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String fileName = sdf.format(dt);
        Random random = new Random();
        int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;// 获取5位随机数
        return fileName + rannum;
    }

    //====================================以下是压缩图片的几种方法=====================================================
    /**
     * 根据尺寸压缩像素，达到1280*960(这个是默认值，比较合适的值，可以更改)
     */
    public static void compressBitmapToFile(Bitmap bmp, File file){
        // 尺寸压缩倍数,值越大，图片尺寸越小
        int ratio = getRatioSize(bmp.getWidth(), bmp.getHeight());
        // 压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap(bmp.getWidth() / ratio, bmp.getHeight() / ratio, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, bmp.getWidth() / ratio, bmp.getHeight() / ratio);
        canvas.drawBitmap(bmp, null, rect, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        result.compress(Bitmap.CompressFormat.JPEG, 100 ,baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            result.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
        获得缩放比
     */
    private static int getRatioSize(int bitWidth, int bitHeight) {
        // 图片最大分辨率
        int imageHeight = 1280;
        int imageWidth = 960;
        // 缩放比
        int ratio = 1;
        // 缩放比,由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        if (bitWidth > bitHeight && bitWidth > imageWidth) {
            // 如果图片宽度比高度大,以宽度为基准
            ratio = bitWidth / imageWidth;
        } else if (bitWidth < bitHeight && bitHeight > imageHeight) {
            // 如果图片高度比宽度大，以高度为基准
            ratio = bitHeight / imageHeight;
        }
        // 最小比率为1
        if (ratio <= 0)
            ratio = 1;
        return ratio;
    }

    /**
     * 根据采样压缩像素，做出某个大小的缩略图
     */
    public static Bitmap getScaleBitmap(String filePath, int destWidth, int destHeight){
        //第一次采样处理
        BitmapFactory.Options options = new BitmapFactory.Options();
        //该属性设置为true，那么只会加载图片的边界进来，不会加载图片内容的全部像素点。
        options.inJustDecodeBounds = true;
        //第一次加载图片
        BitmapFactory.decodeFile(filePath, options);

        //得到图片原始的宽高值,像素
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;

        //设置图片默认缩放比例
        int sampleSize = 1;
        //如果图片的宽高有任意一个，没有按比例缩放到参数所指定的范围内的宽高值，则进行缩放比例的扩大
        while(outHeight / sampleSize > destHeight || outWidth / sampleSize > destWidth){
            //缩放比例必须是2的n次幂，反之，则系统会默认取近似值。
            sampleSize *= 2;
        }
        //以上为第一次采样,目的：计算出要缩放的比例值

        //以下开始第二次采样
        //第二次采样需要加载图片的全部内容
        options.inJustDecodeBounds = false;
        //设置刚才的缩放比例
        options.inSampleSize = sampleSize;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        //加载图片，并返回
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 开启线程压缩图片质量，使图片达到某个大小
     * @param bitmap
     * @param filePath
     * @param maxSize kb
     */
    public static void compressBitmapToMaxSize(final Bitmap bitmap, final String filePath, final int maxSize) {
        new Thread(new Runnable() {//开启线程进行压缩处理
            @Override
            public void run() {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int options = 90;   //图片压缩从100到90的时候，压缩的幅度比较大，建议直接从90开始
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，把压缩后的数据存放到baos中 (100表示不压缩，0表示压缩到最小)
                while (baos.toByteArray().length / 1024 > maxSize) {//循环判断如果压缩后图片是否大于100kb,大于继续压缩
                    baos.reset();//重置baos即让下一次的写入覆盖之前的内容
                    options -= 10;//图片质量每次减少10
                    if (options < 0) options = 0;//如果图片质量小于10，则将图片的质量压缩到最小值
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//将压缩后的图片保存到baos中
                    if (options == 0) break;//如果图片的质量已降到最低则，不再进行压缩
                }
                try {
                    FileOutputStream fos = new FileOutputStream(new File(filePath));//将压缩后的图片保存的本地上指定路径中
                    fos.write(baos.toByteArray());
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 压缩图片,综合使用
     */
    public static void compressBitmap(Bitmap image, String filePath, int maxSize) {
        // 获取尺寸压缩倍数
        int ratio = getRatioSize(image.getWidth(), image.getHeight());
        // 压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap(image.getWidth() / ratio, image.getHeight() / ratio, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, image.getWidth() / ratio, image.getHeight() / ratio);
        canvas.drawBitmap(image, null, rect, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        result.compress(Bitmap.CompressFormat.JPEG, options, baos);
        // 循环判断如果压缩后图片是否大于期望大小,大于继续压缩
        while (baos.toByteArray().length / 1024 > maxSize) {
            // 重置baos即清空baos
            baos.reset();
            // 每次都减少10
            options -= 10;
            if (options < 0) options = 0;//如果图片质量小于10，则将图片的质量压缩到最小值
            // 这里压缩options%，把压缩后的数据存放到baos中
            result.compress(Bitmap.CompressFormat.JPEG, options, baos);
            if (options == 0) break;//如果图片的质量已降到最低则，不再进行压缩
        }
        try {
            storeImage(result, filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // JNI调用保存图片到SD卡 这个关键
//        NativeUtil.compressBitmap(result, options, filePath, true);
        // 释放Bitmap
        if (result != null && !result.isRecycled()) {
            result.recycle();
            result = null;
        }
    }

    /**
     * 获取图片大小
     */
    public static int getSize(Bitmap bitmap){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        return out.toByteArray().length / 1024;
    }


}
