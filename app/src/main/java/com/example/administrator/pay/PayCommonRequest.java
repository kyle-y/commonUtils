package com.example.administrator.pay;

/**
 * Created by yxb on 2017/10/28.
 * 用于统一处理支付、充值（支付宝、微信）
 */

public class PayCommonRequest {

//    private static PayCommonRequest instance;
//
//    public static final int CONFIRM_MODE = 1;//payResult有外层，应用于confirm接口
//    public static final int PAY_MODE = 2;//payResult没有外层，应用于pay，recharge接口
//
//    private BaseActivity activity;
//    private static IWXAPI api;
//    private static String appid;
//    private PayListener listener;
//
//    private String orderCode;   //如果是支付，需要传支付订单号,如果不是，传"0";
//    private String code;    //如果是充值或提现，需要传服务器返回的code值
//    private Handler aliHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            handlePayResult((Map<String, String>) msg.obj);
//        }
//    };
//
//    private PayCommonRequest() {
//    }
//
//    public static void init(IWXAPI wxapi, String wxAppid) {
//        api = wxapi;
//        appid = wxAppid;
//    }
//
//    public static PayCommonRequest getInstance() {
//        if (instance == null) {
//            instance = new PayCommonRequest();
//        }
//        return instance;
//    }
//
//    public void bind(BaseActivity activity, PayListener listener) {
//        this.activity = activity;
//        this.listener = listener;
//    }
//
//    /**
//     * 请求服务器订单信息并调用支付宝支付、充值
//     */
//    public void commonByAli(Observable<JSONObject> observable, final int mode) {
//        observable.observeOn(AndroidSchedulers.mainThread())
//                .filter(new Predicate<JSONObject>() {   //判断是否请求成功
//                    @Override
//                    public boolean test(@NonNull JSONObject object) throws Exception {
//                        if (mode == CONFIRM_MODE) {
//                            BaseEntry<JywPayResultBean> result = JSON.parseObject(object.toString(), new TypeReference<BaseEntry<JywPayResultBean>>() {
//                            });
//                            if (!result.isSuccess()) {
//                                Util.showToast(result.getMsg());
//                                Util.cancelLoading();
//                            }
//                            return result.isSuccess() &&
//                                    result.getData().getPayResult() != null &&
//                                    !TextUtils.isEmpty(result.getData().getPayResult().getAliPayResult());
//                        } else if (mode == PAY_MODE) {
//                            BaseEntry<JywPayResultBean.PayResultBean> result = JSON.parseObject(object.toString(), new TypeReference<BaseEntry<JywPayResultBean.PayResultBean>>() {
//                            });
//                            if (!result.isSuccess()) {
//                                Util.showToast(result.getMsg());
//                                Util.cancelLoading();
//                            }
//                            return result.isSuccess() &&
//                                    !TextUtils.isEmpty(result.getData().getAliPayResult());
//                        }
//                        return false;
//                    }
//                })
//                .observeOn(Schedulers.io())
//                .map(new Function<JSONObject, String>() {   //将接受到的订单对象转化为字符串
//                    @Override
//                    public String apply(@NonNull JSONObject object) throws Exception {
//                        if (mode == CONFIRM_MODE) {
//                            BaseEntry<JywPayResultBean> result = JSON.parseObject(object.toString(), new TypeReference<BaseEntry<JywPayResultBean>>() {
//                            });
//                            code = result.getData().getPayResult().getCode();
//                            return result.getData().getPayResult().getAliPayResult();
//                        } else if (mode == PAY_MODE) {
//                            BaseEntry<JywPayResultBean.PayResultBean> result = JSON.parseObject(object.toString(), new TypeReference<BaseEntry<JywPayResultBean.PayResultBean>>() {
//                            });
//                            code = result.getData().getCode();
//                            return result.getData().getAliPayResult();
//                        }
//                        return "";
//                    }
//                })
//                .compose(RxUtils.<String>getSchedulerTransformer())
//                .compose(activity.<String>bindUntilEvent(ActivityEvent.PAUSE))
//                .subscribe(new Consumer<String>() {
//                    @Override
//                    public void accept(final String s) throws Exception {
//                        Util.cancelLoading();
//                        Runnable payRunnable = new Runnable() {
//                            @Override
//                            public void run() {
//                                PayTask aliPay = new PayTask(activity);
//                                Map<String, String> result = aliPay.payV2(s,true);
//                                Message msg = new Message();
//                                msg.obj = result;
//                                aliHandler.sendMessage(msg);
//                            }
//                        };
//                        // 必须异步调用
//                        Thread payThread = new Thread(payRunnable);
//                        payThread.start();
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Util.cancelLoading();
//                        RxErrorHandler.getInstance().handleError(throwable);
//                    }
//                });
//    }
//
//    /**
//     * 根据支付宝返回信息（客户端SDK同步信息判断）
//     *
//     * @param stringStringMap
//     */
//    private void handlePayResult(Map<String, String> stringStringMap) {
//        PayResult payResult = new PayResult(stringStringMap);
//        String resultInfo = payResult.getResult();// 同步返回需要验证的信息
//        String resultStatus = payResult.getResultStatus();
//        // 判断resultStatus 为9000则代表支付成功
//        if (TextUtils.equals(resultStatus, "9000")) {
//            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//            if (Util.isAppDebug()) {
//                if (listener != null) {
//                    listener.success(code);
//                }
//            } else {
//                commonConfirmPay();
//            }
//        } else {
//            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//            if (listener != null) {
//                listener.fail(code);
//            }
//        }
//    }
//
//
//    /**
//     * 请求服务器支付、充值（服务器请求微信服务端得到的预定单）
//     * 请求结束后，调起微信支付，让用户输入密码
//     */
//    public void commonByWeChat(Observable<JSONObject> observable, final int mode) {
//        if (!api.isWXAppInstalled()){
//            Util.showToast("请先安装微信");
//            Util.cancelLoading();
//        }
//        observable
//                .filter(new Predicate<JSONObject>() {   //判断是否请求成功
//                    @Override
//                    public boolean test(@NonNull JSONObject object) throws Exception {
//                        if (mode == CONFIRM_MODE) {
//                            BaseEntry<JywPayResultBean> result = JSON.parseObject(object.toString(), new TypeReference<BaseEntry<JywPayResultBean>>() {
//                            });
//                            if (!result.isSuccess()) {
//                                Util.showToast(result.getMsg());
//                                Util.cancelLoading();
//                            }
//                            return result.isSuccess() &&
//                                    result.getData().getPayResult() != null &&
//                                    result.getData().getPayResult().getWxPayResult() != null;
//                        } else if (mode == PAY_MODE) {
//                            BaseEntry<JywPayResultBean.PayResultBean> result = JSON.parseObject(object.toString(), new TypeReference<BaseEntry<JywPayResultBean.PayResultBean>>() {
//                            });
//                            if (!result.isSuccess()) {
//                                Util.showToast(result.getMsg());
//                                Util.cancelLoading();
//                            }
//                            return result.isSuccess() &&
//                                    result.getData().getWxPayResult() != null;
//                        }
//                        return false;
//                    }
//                })
//                .map(new Function<JSONObject, Boolean>() {
//                    @Override
//                    public Boolean apply(@NonNull JSONObject object) throws Exception {
//                        JywPayResultBean.PayResultBean.WxPayResultBean bean = null;
//                        if (mode == CONFIRM_MODE) {
//                            BaseEntry<JywPayResultBean> result = JSON.parseObject(object.toString(), new TypeReference<BaseEntry<JywPayResultBean>>() {
//                            });
//                            code = result.getData().getPayResult().getCode();
//                            bean = result.getData().getPayResult().getWxPayResult();
//                        } else if (mode == PAY_MODE) {
//                            BaseEntry<JywPayResultBean.PayResultBean> result = JSON.parseObject(object.toString(), new TypeReference<BaseEntry<JywPayResultBean.PayResultBean>>() {
//                            });
//                            code = result.getData().getCode();
//                            bean = result.getData().getWxPayResult();
//                        }
//                        PayReq request = new PayReq();
//                        request.appId = appid;
//                        request.partnerId = bean.getPartnerId();
//                        request.prepayId = bean.getPrepayId();
//                        request.packageValue = bean.getPackageX();
//                        request.nonceStr = bean.getNonceStr();
//                        request.timeStamp = bean.getTimeStamp();
//                        request.sign = bean.getSign();
//                        return api.sendReq(request);
//                    }
//                })
//                .compose(RxUtils.<Boolean>getSchedulerTransformer())
//                .compose(activity.<Boolean>bindUntilEvent(ActivityEvent.PAUSE))
//                .subscribe(new Consumer<Boolean>() {
//                               @Override
//                               public void accept(Boolean aBoolean) throws Exception {
//                               }
//                           },
//                        new Consumer<Throwable>() {
//                            @Override
//                            public void accept(Throwable throwable) throws Exception {
//                                Util.cancelLoading();
//                                RxErrorHandler.getInstance().handleError(throwable);
//                            }
//                        });
//    }
//
//    /**
//     * 请求服务器确认支付情况（已返回结果为准判断是否支付成功）
//     *
//     * @param errCode
//     */
//    public void confirmWeChatPay(int errCode) {
//        if (errCode == BaseResp.ErrCode.ERR_OK) {
//            if (Util.isAppDebug()) {
//                if (listener != null) {
//                    listener.success(code);
//                }
//            } else {
//                commonConfirmPay();
//            }
//        } else {
//            if (listener != null) {
//                listener.fail(code);
//            }
//        }
//    }
//
//    /**
//     * 向服务器请求确认服务端支付信息（只有服务端也返回成功，才算成功）
//     */
//    public void commonConfirmPay() {
//        ServiceUtil.getCommonApi().confirmPay(Long.parseLong(code))
//                .compose(RxUtils.<JSONObject>getSchedulerTransformer())
//                .compose(activity.<JSONObject>bindUntilEvent(ActivityEvent.PAUSE))
//                .subscribe(new Consumer<JSONObject>() {
//                               @Override
//                               public void accept(JSONObject object) throws Exception {
//                                   BaseEntry<String> result = JSON.parseObject(object.toString(), new TypeReference<BaseEntry<String>>() {
//                                   });
//                                   if (result.isSuccess()) {
//                                       if (listener != null) {
//                                           listener.success(code);
//                                       }
//                                   } else {
//                                       Util.cancelLoading();
//                                       Util.showToast(result.getMsg());
//                                   }
//                               }
//                           },
//                        new Consumer<Throwable>() {
//                            @Override
//                            public void accept(Throwable throwable) throws Exception {
//                                Util.cancelLoading();
//                                RxErrorHandler.getInstance().handleError(throwable);
//                            }
//                        });
//    }
//
//    public interface PayListener {
//        void success(String code);
//
//        void fail(String code);
//    }
}
