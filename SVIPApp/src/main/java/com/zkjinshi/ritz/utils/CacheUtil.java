package com.zkjinshi.ritz.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;





import java.util.ArrayList;


/**
 * 缓存工具类
 * 开发者：JimmyZhang
 * 日期：2015/7/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */

public class CacheUtil {

	private static final String SVIP_CACHE = "svip_cache";

	private CacheUtil() {

	}

	private static CacheUtil instance;

	public synchronized static CacheUtil getInstance() {
		if (null == instance) {
			instance = new CacheUtil();
		}
		return instance;
	}

	private Context context;

	public void init(Context context) {
		this.context = context;
	}

	/**
	 * 获取锁屏状态
	 * @return
	 */
	public boolean isScreenOff() {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getBoolean("isScreenOff", false);
	}

	/**
	 * 设置锁屏状态
	 * @param isScreenOff
	 */
	public void setScreenOff(boolean isScreenOff) {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putBoolean("isScreenOff",  isScreenOff).commit();
	}

	/**
	 * 获取蓝牙推送是否打开
	 * @return
	 */
	public boolean isBLESwitch() {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getBoolean("isBLESwitch"+CacheUtil.getInstance().getUserId(), true);
	}

	/**
	 * 设置蓝牙推送是否打开
	 * @param isOpen
	 */
	public void setBleSwitch(boolean isOpen) {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putBoolean("isBLESwitch"+CacheUtil.getInstance().getUserId(),  isOpen).commit();
	}

	/**
	 * 获取蓝牙推送是否打开
	 * @return
	 */
	public boolean isServiceSwitch() {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getBoolean("isServiceSwitch"+CacheUtil.getInstance().getUserId(), true);
	}

	/**
	 * 设置蓝牙推送是否打开
	 * @param isOpen
	 */
	public void setServiceSwitch(boolean isOpen) {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putBoolean("isServiceSwitch"+CacheUtil.getInstance().getUserId(),  isOpen).commit();
	}

	/**
	 * 设置用户登录状态
	 * @param isLogin
	 */
	public void setLogin(boolean isLogin) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putBoolean("is_login", isLogin).commit();
	}

	/**
	 * 设置激活状态
	 * @param isActivate
	 */
	public void setActivate(boolean isActivate) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putBoolean("is_activate", isActivate).commit();
	}

	/**
	 * 设置用户是否Guide
	 * @param isGuide
	 */
	public void setGuide(boolean isGuide) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putBoolean("is_guide", isGuide).commit();
	}

	/**
	 * 获取用户是否Guide
	 * @return
	 */
	public boolean isGuide() {
		if (null == context) {
			return false;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getBoolean("is_guide", true);
	}

	/**
	 * 获取用户登录状态
	 * @return
	 */
	public boolean isLogin() {
		if (null == context) {
			return false;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getBoolean("is_login", false);
	}

	public void setFirstTime(boolean isFirstTime) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putBoolean("is_first_time", isFirstTime).commit();
	}


	public boolean isFirstTime() {
		if (null == context) {
			return false;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getBoolean("is_first_time", true);
	}

	public void setAccount(float account) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putFloat("account"+CacheUtil.getInstance().getUserId(), account).commit();
	}

	public float getAccount() {
		if (null == context) {
			return 0.0f;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getFloat("account"+CacheUtil.getInstance().getUserId(), 900.00f);
	}

	/**
	 * 获取激活状态
	 * @return
	 */
	public boolean isActivate() {
		if (null == context) {
			return false;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getBoolean("is_activate", false);
	}

	/**
	 * 保存登录token
	 * @param token
	 */
	public void setToken(String token) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putString("token", token).commit();
	}

	/**
	 * 获取登录token
	 * @return
	 */
	public String getToken() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getString("token", null);
	}

	/**
	 * 保存统一认证登录token
	 * @param token
	 */
	public void setExtToken(String token) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putString("extToken", token).commit();
	}

	/**
	 * 获取统一认证登录token
	 * @return
	 */
	public String getExtToken() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getString("extToken", null);
	}

	/**
	 * 保存用户id
	 * @param userCheckInId
	 */
	public void setUserCheckInId(int userCheckInId) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putInt("userCheckInId", userCheckInId).commit();
	}

	/**
	 * 获取用户id
	 * @return
	 */
	public int getUserCheckInId() {
		if (null == context) {
			return 0;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getInt("userCheckInId", 0);
	}

	/**
	 * 保存用户id
	 * @param userId
	 */
	public void setUserId(String userId) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putString("userId", userId).commit();
	}

	/**
	 * 获取用户id
	 * @return
	 */
	public String getUserId() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getString("userId", null);
	}

	/**
	 * 保存用户真实姓名
	 * @param realName
	 */
	public void setUserRealName(String realName) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putString("realName", realName).commit();
	}

	/**
	 * 获取用户真实姓名
	 * @return
	 */
	public String getUserRealName() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getString("realName","");
	}

	/**
	 * 保存用户姓名
	 * @param userName
	 */
	public void setUserName(String userName) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putString("userName", userName).commit();
	}

	/**
	 * 获取用户姓名
	 * @return
	 */
	public String getUserName() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getString("userName","");
	}

	/**
	 * 保存用户性别
	 * @param sex
	 */
	public void setSex(String sex) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putString("sex", sex).commit();
	}

	/**
	 * 获取用户性别
	 * @return
	 */
	public String getSex() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getString("sex","0");
	}

	/**
	 * 保存用户手机号
	 * @param mobilePhone
	 */
	public void setUserPhone(String mobilePhone) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putString("mobilePhone", mobilePhone).commit();
	}

	/**
	 * 保存用户等级
	 * @param userApplevel
	 */
	public void setUserApplevel(String userApplevel) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putString("user_applevel", userApplevel).commit();
	}

	/**
	 * 获取用户手机号
	 * @return
	 */
	public String getUserPhone() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getString("mobilePhone","");
	}

	/**
	 * 获取用户等级
	 * @return
	 */
	public String getUserApplevel() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getString("user_applevel","0");
	}

	public void savePicName( String picName) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		sp.edit().putString("picName", picName).commit();
	}

	public String getPicName() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		return sp.getString("picName", "");
	}

	public void savePicPath( String picPath) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		sp.edit().putString("picPath", picPath).commit();
	}

	public String getPicPath() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		return sp.getString("picPath", "");
	}

	public void saveAudioPath(String audioPath) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		sp.edit().putString("audioPath", audioPath).commit();
	}

	public String getAudioPath() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getString("audioPath", "");
	}

	public void saveTagsOpen(boolean isOpen){
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		sp.edit().putBoolean("tagsopen", isOpen).commit();
	}

	public boolean getTagsOpen() {
		if (null == context) {
			return true;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		return sp.getBoolean("tagsopen", true);
	}

	/**
	 * 设置开始录音倒计时
	 * @param isCountDown
	 */
	public void setCountDown(boolean isCountDown) {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putBoolean("count_down", isCountDown).commit();
	}

	/**
	 * 是否处于录音倒计时
	 * @return
	 */
	public boolean isCountDown() {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getBoolean("count_down", false);
	}

	/**
	 * 是否录音时间过短
	 * @return
	 */
	public boolean isVoiceTooShort() {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getBoolean("voice_too_short", false);
	}

	/**
	 * 设置录音时间过短
	 * @return
	 */
	public void setVoiceTooShort(boolean voiceTooShort) {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putBoolean("voice_too_short", voiceTooShort).commit();
	}

	/**
	 * 获取用户是否在这个区域内
	 * @param shopId
	 * @return
	 */
	public boolean isInArea(String shopId) {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getBoolean("is_in_"+shopId+"_area", false);
	}

	/**
	 * 设置用户是否在这个区域内
	 * @param shopId
	 * @param isInArea
	 */
	public void setInArea(String shopId,boolean isInArea) {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putBoolean("is_in_"+shopId+"_area",  isInArea).commit();
	}




	/**
	 * 获取用户是否在这个区域内
	 * @param shopId
	 * @return
	 */
	public int getOrderStatus(String shopId) {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getInt(shopId + "_order_status", 0);
	}

	/**
	 * 设置用户是否在这个区域内
	 *
	 * @param shopId
	 * @param orderStatus
	 */
	public void setOrderStatus(String shopId, int orderStatus) {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putInt(shopId + "_order_status", orderStatus).commit();
	}

	/**
	 * 加密存入缓存
	 *
	 * @param cacheObj
	 */
	public void saveObjCache(Object cacheObj) {
		if (null != cacheObj) {
			Gson gson = new Gson();
			String json = gson.toJson(cacheObj);
			String key = cacheObj.getClass().getSimpleName();
			try {
				String encryptedData = Base64Encoder.encode(json);// base 64加密
				SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
						Context.MODE_PRIVATE);
				sp.edit().putString(key, encryptedData).commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 解密取出缓存对象
	 *
	 * @param cacheObj
	 * @return
	 */
	public Object getObjCache(Object cacheObj) {
		if (null == cacheObj) {
			return null;
		}
		if (null != cacheObj) {
			SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
					Context.MODE_PRIVATE);
			String key = cacheObj.getClass().getSimpleName();
			String value = "";
			String encryptedData = sp.getString(key, "");
			if (!TextUtils.isEmpty(encryptedData)) {
				try {
					value = Base64Decoder.decode(encryptedData);
					Gson gson = new Gson();
					cacheObj = gson.fromJson(value, cacheObj.getClass());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return cacheObj;
	}

	/**
	 *  存入集合缓存的通用方法
	 * @param key
	 * @param cacheList
	 * @param <T>
	 */
	public <T> void saveListCache(String key,
								  ArrayList<T> cacheList) {
		if (null != cacheList && cacheList.size() > 0) {
			Gson gson = new Gson();
			String json = gson.toJson(cacheList);
			try {
				String encryptedData = Base64Encoder.encode(json);// base
				// 64加密
				SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
						Context.MODE_PRIVATE);
				sp.edit().putString(key, encryptedData).commit();
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("info", "saveListCache Exception:" + e);
			}
		}
	}

	/**
	 * 取集合缓存的通用方法
	 * @param key
	 * @return
	 */
	public String getListStrCache(String key) {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		String value = "";
		String encryptedData = sp.getString(key, "");
		if (!TextUtils.isEmpty(encryptedData)) {
			try {
				value = Base64Decoder.decode(encryptedData);// base 64解密
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("info", "getListCache Exception:" + e);
			}
		}
		return value;
	}

	/**
	 * 获取当前城市信息
	 */
	public String getCurrentCity() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getString("current_city", "");
	}

	/**
	 * 保存当前城市信息
	 * @param currentCity
	 */
	public void saveCurrentCity(String currentCity) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putString("current_city", currentCity).commit();
	}

	/**
	 * 保存最适合的图片分辨率
	 * @param bestFitPixel
	 */
	public void setBestFitPixel(int bestFitPixel) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putInt("bestFitPixel", bestFitPixel).commit();
	}

	/**
	 * 获取最适合的图片分辨率
	 * @return
	 */
	public int getBestFitPixel() {
		if (null == context) {
			return 0;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getInt("bestFitPixel", 720);
	}

	public void clearCache(String key) {
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putString(key, "").commit();
	}

	public void setHomeGuide(boolean isGuide) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putBoolean("home_guide", isGuide).commit();
	}

	public boolean isHomeGuide() {
		if (null == context) {
			return false;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getBoolean("home_guide", true);
	}

	public void setShopGuide(boolean isGuide) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putBoolean("shop_guide", isGuide).commit();
	}

	public boolean isShopGuide() {
		if (null == context) {
			return false;
		}
		SharedPreferences sp = context.getSharedPreferences(
				SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getBoolean("shop_guide", true);
	}

	public void saveUserPhotoUrl( String userPhotoUrl) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		sp.edit().putString("user_photo_url", userPhotoUrl).commit();
	}

	public String getUserPhotoUrl() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		return sp.getString("user_photo_url", "");
	}

	/**
	 * 设置网络配置版本号
	 * @param netConfigVersion
	 */
	public void setNetConfigVersion(int netConfigVersion) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		sp.edit().putInt("net_config_version", netConfigVersion).commit();
	}

	/**
	 * 获取网络配置版本号
	 * @return
	 */
	public int getNetConfigVersion() {
		if (null == context) {
			return 0;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE, Context.MODE_PRIVATE);
		return sp.getInt("net_config_version", 0);
	}

	/**
	 * 保存商家id
	 * @param shopId
	 */
	public void saveShopId( String shopId) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		sp.edit().putString("shop_id", shopId).commit();
	}

	/**
	 * 获取商家id
	 * @return
	 */
	public String getShopId() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		return sp.getString("shop_id", "9001");
	}

	/**
	 * 保存商家logo
	 * @param shopLogo
	 */
	public void saveShopLogo( String shopLogo) {
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		sp.edit().putString("shop_logo", shopLogo).commit();
	}

	/**
	 * 获取商家logo
	 * @return
	 */
	public String getShopLogo() {
		if (null == context) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		return sp.getString("shop_logo", "");
	}

	/**
	 * 保存更新首页logo时间戳
	 *
	 */
	public void saveExpiryLogoTime(long currentTimeMillis){
		if (null == context) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		sp.edit().putLong("expiry_logo_time", currentTimeMillis).commit();
	}

	/**
	 * 获取更新首页logo时间戳
	 * @return
	 */
	public long getExpiryLogoTime() {
		if (null == context) {
			return 0;
		}
		SharedPreferences sp = context.getSharedPreferences(SVIP_CACHE,
				Context.MODE_PRIVATE);
		return sp.getLong("expiry_logo_time",System.currentTimeMillis());
	}


	/**
	 * 获取是否更新logo(超过一天则恢复默认)
	 * @return
	 */
	public boolean isUpdateLogo(){
		long expiryTime = getExpiryLogoTime();
		long dayNum = System.currentTimeMillis() - expiryTime;
		if(dayNum >= 0){
			return true;
		}
		return false;
	}

}
