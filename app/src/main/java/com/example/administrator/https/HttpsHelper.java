package com.example.administrator.https;

import java.io.IOException;
import java.io.InputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by Administrator on 2017/4/28.
 */

public class HttpsHelper {
    /**
     * 获取证书参数信息,供okhttp使用
     * @param bksFile
     * @param password
     * @param certificates
     * @return
     */
    public static HttpsUtils.SSLParams getSSLParams(InputStream bksFile, String password, InputStream... certificates) {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(bksFile, password, certificates);
        try {
            bksFile.close();
            for (InputStream in : certificates) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sslParams;
    }

    /**
     * 全局域名验证，此处是不安全的
     * 此类是用于主机名验证的基接口。 在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，
     * 则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。策略可以是基于证书的或依赖于其他验证方案。
     * 当验证 URL 主机名使用的默认规则失败时使用这些回调。如果主机名是可接受的，则返回 true
     */
    public static class UnSafeHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;//自行添加判断逻辑，true->Safe，false->unsafe
        }
    }
}
