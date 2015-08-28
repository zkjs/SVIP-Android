package com.zkjinshi.svip.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

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

}
