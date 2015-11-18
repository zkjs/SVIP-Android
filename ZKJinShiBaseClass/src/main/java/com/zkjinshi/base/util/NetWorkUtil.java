
package com.zkjinshi.base.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 判断网络状态工具类
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class NetWorkUtil {

	public static final int OFF_LINE = -1;// 无网络
	public static final int TYPE_MOBILE = 0;// 2G/3G网络
	public static final int TYPE_WIFI = 1; // WIFI网络
	public static final int TYPE_CMWAP = 2; // CMWAP网络
	public static final int TYPE_CMNET = 3; // CMNET网络

	/**
	 * 
	 * 判断是否有网络
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 
	 * 判断是WIFI是否可用
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 
	 * 判断MOBILE网络是否可用
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 
	 * 获取当前网络连接的类型信息 -1没有网络 0：2G/3G网络 1：WIFI网络 2：wap网络 3：net网络
	 * 
	 * @param context
	 * @return boolean
	 */
	public static int getConnectedType(Context context) {
		int netType = -1;
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				netType = mNetworkInfo.getType();
				//ConnectivityManager.TYPE_BLUETOOTH
				//ConnectivityManager.TYPE_WIFI 
				//ConnectivityManager.TYPE_MOBILE
				//ConnectivityManager.TYPE_MOBILE_DUN DUN-specific移动数据连接
				//ConnectivityManager.TYPE_MOBILE_HIPRI 高优先级的移动数据连接
				//ConnectivityManager.TYPE_MOBILE_一个MMS-specific移动数据连接
				//ConnectivityManager.TYPE_MOBILE_SUPL SUPL-specific移动数据连接
			}
		}
		return netType;
	}

	//获取本地IP地址
	public static String getLocalIpAddress(Context context){
		String ip = null;
		if(isWifiConnected(context)){
			ip = getWifiIpAddress(context);
		}else if(isMobileConnected(context)){
			ip = get3GIpAddress();
		}

		return ip;
	}



	//获取本机WIFI
	public static String getWifiIpAddress(Context content) {
		WifiManager wifiManager = (WifiManager) content.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		// 获取32位整型IP地址
		int ipAddress = wifiInfo.getIpAddress();

		//返回整型地址转换成“*.*.*.*”地址
		return String.format("%d.%d.%d.%d",
				(ipAddress & 0xff), (ipAddress >> 8 & 0xff),
				(ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
	}

	//3G网络IP
	public static String get3GIpAddress() {

		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& inetAddress instanceof Inet4Address) {
						// if (!inetAddress.isLoopbackAddress() && inetAddress
						// instanceof Inet6Address) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
