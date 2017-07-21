package com.example.administrator.imageLoad;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

import com.example.administrator.newappforutils.R;

/**
 * Created by Administrator on 2017/7/21.
 */

public class ImageLoader {

    private static ImageLoader instance = new ImageLoader();
    private LoadImageHelper helper;

    public static ImageLoader getInstance() {
        return instance;
    }

    /**
     * 需要初始化
     *
     * @param context
     */
    public void init(Context context) {
        helper = new LoadImageHelper(context);
    }

    private Handler mainHanler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Result result = (Result) msg.obj;
            ImageView imageView = result.getImageView();
            String tagStr = (String) imageView.getTag();
            if (tagStr!= null && tagStr.equals(result.getUrl())) {
                imageView.setImageBitmap(result.getBitmap());
            } else {
                imageView.setImageResource(R.mipmap.ic_launcher);
            }
        }
    };

    public void bindToView(final String url, final ImageView imageView) {
        if (helper == null) {
            throw new RuntimeException("imageLoader need execute init() firstly !");
        }

        Bitmap bitmap = helper.loadFromMemoryCache(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = helper.loadFromDiskCache(url);
                if (bitmap != null) {
                    Message message = mainHanler.obtainMessage();
                    message.obj = new Result(url, imageView, bitmap);
                    message.sendToTarget();
                    return;
                }

                bitmap = helper.loadFromWeb(url);
                if (bitmap != null) {
                    Message message = mainHanler.obtainMessage();
                    message.obj = new Result(url, imageView, bitmap);
                    message.sendToTarget();
                }
            }
        };
        LoadUtils.THRED_POOL_EXCUTOR.execute(runnable);
    }

    class Result{
        private String url;
        private ImageView imageView;
        private Bitmap bitmap;

        public Result(String url, ImageView imageView, Bitmap bitmap) {
            this.url = url;
            this.imageView = imageView;
            this.bitmap = bitmap;
        }

        public String getUrl() {
            return url;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }
    }

}
