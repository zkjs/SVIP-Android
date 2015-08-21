package com.zkjinshi.base.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

/**
 * 设备信息存储工具类
 * 开发者:JimmyZhang
 * 日期：2015/7/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class DeviceUtils {

	private static String IMEI;
	private static String OS = "android";
	private static int sdk;
	private static String version;
	private static String model;
	private static String SDCardPath;
	private static boolean sdCardExist;
	private static DisplayMetrics mDisplayMetrics;
	private static String release;
	private static String brand;
	private static String macAddress;

	/**
	 * 初始化读取所有设备相关信息
	 * @param context
	 */
	@SuppressLint("NewApi")
	public static void init(Context context) {
		if (context != null) {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			sdk = Build.VERSION.SDK_INT;
			brand = Build.BRAND;
			release = Build.VERSION.RELEASE;
			IMEI = tm.getDeviceId();
			version = tm.getDeviceSoftwareVersion();
			// Display display =
			// ((Activity)context).getWindowManager().getDefaultDisplay();
			// screenHeight = display.getHeight();
			// screenWidth = display.getWidth();
			sdCardExist = Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED);
			macAddress = getMacAddress(context);
			if (sdCardExist)
				SDCardPath = Environment.getExternalStorageDirectory() + "";
			int phoneType = tm.getPhoneType();
			switch (phoneType) {
			case TelephonyManager.PHONE_TYPE_CDMA:
				model = "PHONE_TYPE_CDMA";
				break;
			case TelephonyManager.PHONE_TYPE_GSM:
				model = "PHONE_TYPE_GSM";
				break;
			case TelephonyManager.PHONE_TYPE_NONE:
				model = "PHONE_TYPE_CDMA";
				break;
			// Android 2.1 不支持，高版本才能支持
			// case TelephonyManager.PHONE_TYPE_SIP:
			// model = "PHONE_TYPE_SIP";
			// break;
			}
		}
	}

	/**
	 * 取得手机的默认设备号
	 * @return
	 */
	public static String getIMEI() {
		return IMEI;
	}

	/**
	 * 取得手机的操作系统-android
	 * @return
	 */
	public static String getOS() {
		return OS;
	}

	/**
	 * 取得手机的开发SDK
	 * @return
	 */
	public static int getSdk() {
		return sdk;
	}

	/**
	 * 取得手机操作系统的版本号
	 * @return
	 */
	public static String getVersion() {
		return version;
	}

	/**
	 * 取得手机网络提供商型号
	 * @return
	 */
	public static String getModel() {
		return model;
	}

	/**
	 * 取得SDCard路径
	 * @return
	 */
	public static String getSDCardPath() {
		return SDCardPath;
	}

	/**
	 * 判断是否有SDCard
	 * @return
	 */
	public static boolean hasSDCard() {
		return sdCardExist;
	}

	/**
	 * 取得手机屏幕高度
	 * @param act
	 * @return
	 */
	public static int getScreenHeight(Activity act) {
		if (mDisplayMetrics == null) {
			mDisplayMetrics = new DisplayMetrics();
			act.getWindowManager().getDefaultDisplay()
					.getMetrics(mDisplayMetrics);
		}
		return mDisplayMetrics.heightPixels;
	}

	/**
	 * 取得手机屏幕宽度
	 * @param act
	 * @return
	 */
	public static int getScreenWidth(Activity act) {
		if (mDisplayMetrics == null) {
			mDisplayMetrics = new DisplayMetrics();
			act.getWindowManager().getDefaultDisplay()
					.getMetrics(mDisplayMetrics);
		}
		return mDisplayMetrics.widthPixels;
	}

	/**
	 * 取得手机品牌
	 * @return
	 */
	public static String getBrand() {
		return brand;
	}

	public static String getRelease() {
		return release;
	}

	/**
	 * 获取mac物理地址
	 * @param context
	 * @return
	 */
	public static String getMacAddress(Context context) {
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		return wm.getConnectionInfo().getMacAddress();
	}

	/**
	 * 获取设备唯一识别码，如果IMEI为空，则获取mac物理地址
	 * @return
	 */
	public static String getDeviceUniqueCode() {
		if (TextUtils.isEmpty(IMEI)) {
			return macAddress;
		} else {
			return IMEI;
		}
	}
}
