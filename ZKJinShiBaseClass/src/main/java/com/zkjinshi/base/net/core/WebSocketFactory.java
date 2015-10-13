package com.zkjinshi.base.net.core;

import android.content.res.AssetManager;

import com.zkjinshi.base.util.BaseContext;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * WebSocket创建工厂类
 * 开发者：JimmyZhang
 * 日期：2015/8/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class WebSocketFactory {

    private static final String CLIENT_KET_PASSWORD = "im_zkjs_tls12_ca";//私钥密码
    private static final String CLIENT_TRUST_PASSWORD = "im_zkjs_tls12_srv";//信任证书密码
    private static final String CLIENT_AGREEMENT = "TLS"; // 使用协议
    private static final String CLIENT_KEY_MANAGER = "X509"; // 密钥管理器
    private static final String CLIENT_TRUST_MANAGER = "X509"; // 信任证书管理器
    private static final String CLIENT_KEY_KEYSTORE = "BKS";
    private static final String CLIENT_TRUST_KEYSTORE = "BKS";

    private AssetManager mAssetManager = null;

    private static WebSocketFactory instance;

    private WebSocketFactory() {
    }

    public synchronized static WebSocketFactory getInstance() {
        if (null == instance) {
            instance = new WebSocketFactory();
        }
        return instance;
    }

    /**
     * 获取自签名ca证书的socket工厂类
     * @return
     * @throws KeyManagementException
     * @throws CertificateException
     * @throws KeyStoreException
     * @throws NoSuchProviderException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableKeyException
     */
    public SSLSocketFactory getSSLSocketFactory()
            throws
            KeyManagementException,
            CertificateException,
            KeyStoreException,
            NoSuchProviderException,
            IOException ,
            NoSuchAlgorithmException,
            UnrecoverableKeyException{
        if(null == mAssetManager){
            mAssetManager = BaseContext.getInstance().getContext().getAssets();
        }
        SSLContext sslContext = SSLContext.getInstance(CLIENT_AGREEMENT);
        KeyManagerFactory keyManager = KeyManagerFactory
                .getInstance(CLIENT_KEY_MANAGER);
        TrustManagerFactory trustManager = TrustManagerFactory
                .getInstance(CLIENT_TRUST_MANAGER);
        KeyStore keyKeyStore = KeyStore.getInstance(CLIENT_KEY_KEYSTORE);
        KeyStore trustKeyStore = KeyStore
                .getInstance(CLIENT_TRUST_KEYSTORE);
        InputStream is = mAssetManager.open("ca.bks");
        keyKeyStore.load(is, CLIENT_KET_PASSWORD.toCharArray());
        is.reset();
        InputStream is2 = mAssetManager.open("server.bks");
        trustKeyStore.load(is2, CLIENT_TRUST_PASSWORD.toCharArray());
        is.close();
        is2.close();
        keyManager.init(keyKeyStore, CLIENT_KET_PASSWORD.toCharArray());
        trustManager.init(trustKeyStore);
        sslContext.init(keyManager.getKeyManagers(),
                trustManager.getTrustManagers(), null);
        return sslContext.getSocketFactory();
    }
}
