package com.zkjinshi.svip.utils;

import android.content.Context;
import android.content.SharedPreferences;

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

}
