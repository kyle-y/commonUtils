package com.example.administrator.umeng;

import java.util.Map;

/**
 * Created by yxb on 2017/10/31.
 * 友盟第三方登录,在回调Map<String, String>中拿到用户的相应信息
 * 防止内存泄漏：在相应的activity中onDestroy()中加入UMShareAPI.get(this).release();
 * 为了能够得到回调：在相应的activity的onActivityResult()中加入UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
 */

public class UmengThird {

//    private ThirdLoginListener listener;
//
//    /**
//     * 第三方QQ登录，回调会返回QQ信息
//     *
//     * @param activity
//     * @param listener
//     */
//    public static void loginFromQQ(final Activity activity, final ThirdLoginListener listener) {
//        thirdLogin(activity, SHARE_MEDIA.QQ, listener);
//    }
//
//    /**
//     * 第三方微信登录，回调会返回微信个人用户信息
//     *
//     * @param activity
//     * @param listener
//     */
//    public static void loginFromWeiXin(final Activity activity, final ThirdLoginListener listener) {
//        thirdLogin(activity, SHARE_MEDIA.WEIXIN, listener);
//    }
//
//    /**
//     * 第三方登录，根据平台选择
//     *
//     * @param activity
//     * @param platform
//     * @param listener
//     */
//    private static void thirdLogin(final Activity activity, SHARE_MEDIA platform, final ThirdLoginListener listener) {
//        UMShareConfig config = new UMShareConfig();
//        config.isNeedAuthOnGetUserInfo(true);
//        UMShareAPI.get(activity).setShareConfig(config);
//
//        UMShareAPI.get(activity).getPlatformInfo(activity, platform, new UMAuthListener() {
//
//            @Override
//            public void onStart(SHARE_MEDIA share_media) {
//
//            }
//
//            @Override
//            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
//                if (listener != null) listener.onSuccess(map);
//            }
//
//            @Override
//            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
//                if (!UMShareAPI.get(activity).isInstall(activity, share_media)) {
//                    Util.showToast("未安装该应用");
//                } else {
//                    Util.showToast("登录失败");
//                }
//                if (listener != null) listener.onFail();
//            }
//
//            @Override
//            public void onCancel(SHARE_MEDIA share_media, int i) {
//                if (listener != null) listener.onCancel();
//            }
//        });
//    }

    public interface ThirdLoginListener {
        void onSuccess(Map<String, String> map);

        void onFail();

        void onCancel();
    }
}
