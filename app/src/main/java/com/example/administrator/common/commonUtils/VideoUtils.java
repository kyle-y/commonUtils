package com.example.administrator.common.commonUtils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by yxb on 2018/5/4.
 */

public class VideoUtils {

    public static final int NETWORK = 100;
    public static final int LOCAL = 101;

    //根据url获取音视频时长，返回毫秒
    public long getDurationLong(String url,int type){
        String duration = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //如果是网络路径
            if(type == NETWORK){
                retriever.setDataSource(url,new HashMap<String, String>());
            }else if(type == LOCAL){//如果是本地路径
                retriever.setDataSource(url);
            }
            duration = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception ex) {
            LogUtils.d("获取音频时长失败");
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                LogUtils.d("释放MediaMetadataRetriever资源失败");
            }
        }
        if(!TextUtils.isEmpty(duration)){
            return Long.parseLong(duration);
        }else{
            return 0;
        }
    }

    //获取视频缩略图
    private Bitmap createVideoThumbnail(String url, int type) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //将网络文件以及本地文件区分开来设置
            if (type == NETWORK) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else if(type == LOCAL){
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_NEXT_SYNC);
        } catch (IllegalArgumentException ex) {
            LogUtils.d("获取视频缩略图失败");
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                LogUtils.d("释放MediaMetadataRetriever资源失败");
            }
        }
        return bitmap;
    }
}
