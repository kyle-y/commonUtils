package com.example.administrator.umeng;

/**
 * Created by yxb on 2017/10/31.
 * 友盟分享
 * 防止内存泄漏：在相应的activity中onDestroy()中加入UMShareAPI.get(this).release();
 * 如果使用QQ分享，在相应的activity的onActivityResult()中加入UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
 */

public class UmengShare {

//    /**
//     * 单独分享到微信，可配合自定义view使用
//     * @param activity
//     * @param imageStr
//     * @param targetUrl
//     * @param title
//     * @param content
//     * @param listener
//     */
//    public static void shareUrlByWeixin(final Activity activity, String imageStr, String targetUrl, String title, String content, final OnShareListener listener) {
//        shareUrlOnly(activity, SHARE_MEDIA.WEIXIN, imageStr, targetUrl, title, content, listener);
//    }
//    /**
//     * 单独分享到微信朋友圈，可配合自定义view使用
//     * @param activity
//     * @param imageStr
//     * @param targetUrl
//     * @param title
//     * @param content
//     * @param listener
//     */
//    public static void shareUrlByWeixinCircle(final Activity activity, String imageStr, String targetUrl, String title, String content, final OnShareListener listener) {
//        shareUrlOnly(activity, SHARE_MEDIA.WEIXIN_CIRCLE, imageStr, targetUrl, title, content, listener);
//    }
//    /**
//     * 单独分享到QQ，可配合自定义view使用
//     * @param activity
//     * @param imageStr
//     * @param targetUrl
//     * @param title
//     * @param content
//     * @param listener
//     */
//    public static void shareUrlByQQ(final Activity activity, String imageStr, String targetUrl, String title, String content, final OnShareListener listener) {
//        shareUrlOnly(activity, SHARE_MEDIA.QQ, imageStr, targetUrl, title, content, listener);
//    }
//    /**
//     * 单独分享到QQ空间，可配合自定义view使用
//     * @param activity
//     * @param imageStr
//     * @param targetUrl
//     * @param title
//     * @param content
//     * @param listener
//     */
//    public static void shareUrlByQQzone(final Activity activity, String imageStr, String targetUrl, String title, String content, final OnShareListener listener) {
//        shareUrlOnly(activity, SHARE_MEDIA.QZONE, imageStr, targetUrl, title, content, listener);
//    }
//
//    /**
//     * 单独分享到某个平台，只实现功能
//     * @param activity
//     * @param share_media
//     * @param imageStr
//     * @param targetUrl
//     * @param title
//     * @param content
//     * @param listener
//     */
//    private static void shareUrlOnly(final Activity activity, SHARE_MEDIA share_media, String imageStr, String targetUrl, String title, String content, final OnShareListener listener) {
//        UMImage image = new UMImage(activity, imageStr);
//
//        UMWeb umWeb = new UMWeb(targetUrl);
//        umWeb.setThumb(image);
//        umWeb.setDescription(content);
//        umWeb.setTitle(title);
//
//        new ShareAction(activity)
//                .withMedia(umWeb)
//                .setPlatform(share_media)
//                .setCallback(new UMShareListener() {
//                    @Override
//                    public void onStart(SHARE_MEDIA share_media) {
//                    }
//
//                    @Override
//                    public void onResult(SHARE_MEDIA platform) {
//                        Util.showToast("分享成功了");
//                        if (listener != null) listener.onSuccess();
//                    }
//
//                    @Override
//                    public void onError(SHARE_MEDIA platform, Throwable t) {
//                        if (!UMShareAPI.get(activity).isInstall(activity, platform)) {
//                            Util.showToast("未安装该应用");
//                        } else {
//                            Util.showToast("分享失败了");
//                        }
//                        if (listener != null) listener.onFail();
//                    }
//
//                    @Override
//                    public void onCancel(SHARE_MEDIA platform) {
//                        if (listener != null) listener.onCancel();
//                    }
//                }).share();
//    }

    public interface OnShareListener {
        void onSuccess();

        void onFail();

        void onCancel();
    }
}
