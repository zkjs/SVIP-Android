package com.zkjinshi.base.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.ClipboardManager;
import android.view.Gravity;

/**
 * 复制、黏贴操作工具类
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
@SuppressLint("NewApi")
public class ClipboardUtil {

	/**
	 * 实现文本复制功能
	 * @param content
	 * @param context
	 */
	public static void copy(String content, Context context) {
	    ClipboardManager cmb = null;
		if (DeviceUtils.getSdk() > 10) { // 得到剪贴板管理器
		    cmb = (android.content.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);			
		} else { // 得到剪贴板管理器		    
            cmb = (ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
		}
		cmb.setText(content.trim());
        DialogUtil.getInstance().showCustomToast(context, "复制成功", Gravity.CENTER);
	}

	/**
	 * 实现粘贴功能
	 * @param context
	 * @return
	 */
	public static String paste(Context context) {
		if (DeviceUtils.getSdk() > 10) {
			// 得到剪贴板管理器
			ClipboardManager cmb = (ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			return cmb.getText().toString().trim();
		}else{
			return null;
		}
	}
}
