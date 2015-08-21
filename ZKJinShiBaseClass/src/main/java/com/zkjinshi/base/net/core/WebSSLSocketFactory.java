package com.zkjinshi.base.net.core;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * ssl socket工厂
 * 开发者:赖清泉
 * 修改者：JimmyZhang
 * 日期：2015/7/19
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class WebSSLSocketFactory {
	
	private WebSSLSocketFactory(){}
	
	private static WebSSLSocketFactory instance;
	
	public synchronized static WebSSLSocketFactory getInstance(){
		if(null == instance){
			instance = new WebSSLSocketFactory();
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
		context.init(null, new TrustManager[] { x509m }, null);
		return context.getSocketFactory();
	}
}
