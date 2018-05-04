package com.example.administrator.pay;


/**
 * Created by yxb on 2017/11/13.
 */

public class JywPayResultBean {

    /**
     * OrderCode : 151304595403
     * PayMoney : 100
     * PayResult : {"Code":"940411050975694848","PayCode":100,"TradeSubject":300,"WxPayResult":null,"AliPayResult":"app_id=2017100909214525&biz_content=%7B%22out_trade_no%22%3A%22940411050975694848%22%2C%22body%22%3A%22%E8%AE%A2%E5%8D%95%E6%94%AF%E4%BB%98151304595403%22%2C%22subject%22%3A%22%E9%94%A6%E8%A1%A3%E5%8D%AB-%E8%AE%A2%E5%8D%95%E6%94%AF%E4%BB%98151304595403%22%2C%22total_amount%22%3A0.01%7D&charset=UTF-8&format=JSON&method=alipay.trade.app.pay&notify_url=http%3A%2F%2Fpay.jyibb.com%2Fnotifypay%2Fali&sign=vbEx1QgQlHhjfRNlbISskRKHEPumZjohCExHM3%2F1nKAh59ClIRqSh3r482hWHhwxk8U294pyQYeQhrZRHbjXvI4Op5qfmJGb8BzkPDSU4ZG%2BL0N%2B%2BzpHoRv0T9S5IwKiegxoOyK%2FHMQuWJ8f5nuzUooIRbsORZoo7EUmz5cpBEpNBL1lnaOTBcKuTmmKbMOvHqZ4hx2dRkEaPdN5bn0PWHcyyXyquVSPlVpyUxCADPvDUWhMLgqXvtskOCCw07aglEeuwLcPY5mMr69UvrV9265PB%2Ff7VANv%2Bi%2F7Jcbgdrfp5M8sJL7vv2U6LpqHRA0Y2t0bXULt4WnssgV9vSXVoA%3D%3D&sign_type=RSA2&timestamp=2017-12-12%2010%3A40%3A41&version=1.0"}
     */

    private String OrderCode;
    private double PayMoney;
    private PayResultBean PayResult;

    public String getOrderCode() {
        return OrderCode;
    }

    public void setOrderCode(String OrderCode) {
        this.OrderCode = OrderCode;
    }

    public double getPayMoney() {
        return PayMoney;
    }

    public void setPayMoney(double PayMoney) {
        this.PayMoney = PayMoney;
    }

    public PayResultBean getPayResult() {
        return PayResult;
    }

    public void setPayResult(PayResultBean PayResult) {
        this.PayResult = PayResult;
    }

    public static class PayResultBean {
        /**
         * Code : 940411050975694848
         * PayCode : 100
         * TradeSubject : 300
         * WxPayResult : null
         * AliPayResult : app_id=2017100909214525&biz_content=%7B%22out_trade_no%22%3A%22940411050975694848%22%2C%22body%22%3A%22%E8%AE%A2%E5%8D%95%E6%94%AF%E4%BB%98151304595403%22%2C%22subject%22%3A%22%E9%94%A6%E8%A1%A3%E5%8D%AB-%E8%AE%A2%E5%8D%95%E6%94%AF%E4%BB%98151304595403%22%2C%22total_amount%22%3A0.01%7D&charset=UTF-8&format=JSON&method=alipay.trade.app.pay&notify_url=http%3A%2F%2Fpay.jyibb.com%2Fnotifypay%2Fali&sign=vbEx1QgQlHhjfRNlbISskRKHEPumZjohCExHM3%2F1nKAh59ClIRqSh3r482hWHhwxk8U294pyQYeQhrZRHbjXvI4Op5qfmJGb8BzkPDSU4ZG%2BL0N%2B%2BzpHoRv0T9S5IwKiegxoOyK%2FHMQuWJ8f5nuzUooIRbsORZoo7EUmz5cpBEpNBL1lnaOTBcKuTmmKbMOvHqZ4hx2dRkEaPdN5bn0PWHcyyXyquVSPlVpyUxCADPvDUWhMLgqXvtskOCCw07aglEeuwLcPY5mMr69UvrV9265PB%2Ff7VANv%2Bi%2F7Jcbgdrfp5M8sJL7vv2U6LpqHRA0Y2t0bXULt4WnssgV9vSXVoA%3D%3D&sign_type=RSA2&timestamp=2017-12-12%2010%3A40%3A41&version=1.0
         */

        private String Code;
        private long PayCode;
        private int TradeSubject;
        private WxPayResultBean WxPayResult;
        private String AliPayResult;


        public String getCode() {
            return Code;
        }

        public void setCode(String Code) {
            this.Code = Code;
        }

        public long getPayCode() {
            return PayCode;
        }

        public void setPayCode(long PayCode) {
            this.PayCode = PayCode;
        }

        public int getTradeSubject() {
            return TradeSubject;
        }

        public void setTradeSubject(int TradeSubject) {
            this.TradeSubject = TradeSubject;
        }

        public WxPayResultBean getWxPayResult() {
            return WxPayResult;
        }

        public void setWxPayResult(WxPayResultBean wxPayResult) {
            WxPayResult = wxPayResult;
        }

        public String getAliPayResult() {
            return AliPayResult;
        }

        public void setAliPayResult(String AliPayResult) {
            this.AliPayResult = AliPayResult;
        }

        public static class WxPayResultBean{
            /**
             * prepayId : wx201712121052229be99e71c70120408825
             * timeStamp : 1513075942
             * sign : E68A7899E9E41F5ABF894CB0D2DFDE1E
             * package : Sign=WXPay
             * partnerId : 1491692212
             * nonceStr : rRuv8UQ6
             */

            private String prepayId;
            private String timeStamp;
            private String sign;
//            @JSONField(name = "package")
            private String packageX;
            private String partnerId;
            private String nonceStr;

            public String getPrepayId() {
                return prepayId;
            }

            public void setPrepayId(String prepayId) {
                this.prepayId = prepayId;
            }

            public String getTimeStamp() {
                return timeStamp;
            }

            public void setTimeStamp(String timeStamp) {
                this.timeStamp = timeStamp;
            }

            public String getSign() {
                return sign;
            }

            public void setSign(String sign) {
                this.sign = sign;
            }

            public String getPackageX() {
                return packageX;
            }

            public void setPackageX(String packageX) {
                this.packageX = packageX;
            }

            public String getPartnerId() {
                return partnerId;
            }

            public void setPartnerId(String partnerId) {
                this.partnerId = partnerId;
            }

            public String getNonceStr() {
                return nonceStr;
            }

            public void setNonceStr(String nonceStr) {
                this.nonceStr = nonceStr;
            }
        }


    }
}
