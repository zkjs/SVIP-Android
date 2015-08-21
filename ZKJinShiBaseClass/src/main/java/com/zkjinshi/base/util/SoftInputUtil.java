
package com.zkjinshi.base.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 键盘事件控制类
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SoftInputUtil {

	/**
	 * 隐藏软件盘
	 * @param context
	 * @param windowToken
	 */
	@SuppressLint("NewApi") public static void hideSoftInputMode(Context context, View windowToken) {

		InputMethodManager imm = ((InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE));
		imm.hideSoftInputFromWindow(windowToken.getWindowToken(), 0);
	}

	/**
	 * 显示软键盘
	 * @param context
	 * @param windowToken
	 */
	@SuppressLint("NewApi") public static void showSoftInputMode(Context context, View windowToken) {
		final InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(windowToken, InputMethodManager.SHOW_FORCED);
	}
}
