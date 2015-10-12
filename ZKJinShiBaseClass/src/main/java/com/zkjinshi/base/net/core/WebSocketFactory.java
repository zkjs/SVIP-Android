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

    private static final String CLIENT_KET_PASSWORD = "im_zkjs_tls12_ca";// "changeit";//
    // //私钥密码
    private static final String CLIENT_TRUST_PASSWORD = "im_zkjs_tls12_srv";// "changeit";//信任证书密码
    private static final String CLIENT_AGREEMENT = "TLS"; // 使用协议
    private static final String CLIENT_KEY_MANAGER = "X509"; // 密钥管理器
    private static final String CLIENT_TRUST_MANAGER = "X509"; // 信任证书管理器
    private static final String CLIENT_KEY_KEYSTORE = "BKS"; // "JKS";//密库，这里用的是BouncyCastle密库
    private static final String CLIENT_TRUST_KEYSTORE = "BKS"; // "JKS";//

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
        // 取得SSL的SSLContext实例
        SSLContext sslContext = SSLContext.getInstance(CLIENT_AGREEMENT);
        // 取得KeyManagerFactory实例
        KeyManagerFactory keyManager = KeyManagerFactory
                .getInstance(CLIENT_KEY_MANAGER);
        // 取得TrustManagerFactory的X509密钥管理器
        TrustManagerFactory trustManager = TrustManagerFactory
                .getInstance(CLIENT_TRUST_MANAGER);

        // 取得BKS密库实例
        KeyStore keyKeyStore = KeyStore.getInstance(CLIENT_KEY_KEYSTORE);
        KeyStore trustKeyStore = KeyStore
                .getInstance(CLIENT_TRUST_KEYSTORE);
        /**
         * ok:client.p12\server.p12 client.p12\ca.p12 服务端接收为：cl/ca
         * 如果为：ca.p12/server.p12 ca.p12/ca.p12服务端接收为：ca/ca
         * 如果为：server.p12/ca.p12 server.p12/server.p12 服务端接收为：svr/ca
         */
        // 加载证书和私钥,通过读取资源文件的方式读取密钥和信任证书（kclient:密钥;lt_client:信任证书）
        InputStream is = mAssetManager.open("ca.bks");// VC端传p12证书转成bks
        keyKeyStore.load(is, CLIENT_KET_PASSWORD.toCharArray());// kclient:密钥
        is.reset();
        InputStream is2 = mAssetManager.open("server.bks");// VC端传p12证书转成bks
        trustKeyStore.load(is2, CLIENT_TRUST_PASSWORD.toCharArray());// lt_client:信任证书
        is.close();
        is2.close();

        // 初始化密钥管理器、信任证书管理器
        keyManager.init(keyKeyStore, CLIENT_KET_PASSWORD.toCharArray());
        trustManager.init(trustKeyStore);

        // 初始化SSLContext
        sslContext.init(keyManager.getKeyManagers(),
                trustManager.getTrustManagers(), null);
        return sslContext.getSocketFactory();
    }
}
