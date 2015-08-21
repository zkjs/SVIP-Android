package com.zkjinshi.base.net.core;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * WebSocket创建工厂类
 * 开发者：JimmyZhang
 * 日期：2015/8/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class WebSocketFactory {
    private static WebSocketFactory instance;

    private WebSocketFactory() {
    }

    public synchronized static WebSocketFactory getInstance() {
        if (null == instance) {
            instance = new WebSocketFactory();
        }
        return instance;
    }

    public SSLSocketFactory getSSLSocketFactory()
            throws NoSuchAlgorithmException, KeyManagementException {
        X509TrustManager x509m = new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) {
            }
        };
        SSLContext context = SSLContext.getInstance("TLSv1");
        context.init(null, new TrustManager[]{x509m}, null);
        return context.getSocketFactory();
    }
}
