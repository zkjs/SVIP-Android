package com.zkjinshi.base.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import com.zkjinshi.base.R;

/**
 * 对话框工具类
 * 开发者:JimmyZhang
 * 日期：2015/7/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class DialogUtil {

	private static Dialog loadingDialog;

	public static DialogUtil dialogUtil;

	private ProgressDialog progressDialog = null;

	private int mLayoutResId = R.layout.view_progress_dialog;

	public static int mAlertDialogIconId = android.R.drawable.ic_dialog_info;

	private DialogInterface.OnCancelListener mOnCancelListener;

	public static DialogUtil getInstance() {
		if (null == dialogUtil)
			dialogUtil = new DialogUtil();
		return dialogUtil;
	}

	/**
	 * 设置自定义弹出框的显示图标
	 * @param drawableResId
	 */
	public void setAlertDialogIcon(int drawableResId) {
		mAlertDialogIconId = drawableResId;
	}

	/**
	 * 设置进度悬浮框的布局资源
	 * @param layoutResId
	 * @return
	 */
	public DialogUtil setContentView(int layoutResId) {
		this.mLayoutResId = layoutResId;
		return this;
	}

	/**
	 * 设置进度悬浮框的布局资源
	 * @param view
	 * @return
	 */
	public DialogUtil setContentView(View view) {
		this.mLayoutResId = view.getId();
		return this;
	}

	/**
	 * 根据Context显示默认加载框
	 * @param context
	 */
	public void showProgressDialog(Context context) {
		showProgressDialog(context,
				context.getString(R.string.loading_please_waiting));
	}

	/**
	 * 根据Context和String提示信息显示加载框
	 * @param context
	 * @param message
	 */
	public void showProgressDialog(Context context, String message) {
		if (message == null) {
			showProgressDialog(context);
			return;
		}

		cancelProgressDialog();

		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(message);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(true);
		progressDialog.setCanceledOnTouchOutside(false);
		if (mOnCancelListener != null) {
			progressDialog.setOnCancelListener(mOnCancelListener);
		}
		progressDialog.show();
		if (mLayoutResId != -1) {
			progressDialog.setContentView(mLayoutResId);
		}
	}

	public DialogUtil setOnCancelListener(
			DialogInterface.OnCancelListener listener) {
		mOnCancelListener = listener;
		return this;
	}

	/**
	 * 关闭当前加载进度框
	 */
	public void cancelProgressDialog() {
		if (progressDialog != null)
			if (progressDialog.isShowing()) {
				try {
					progressDialog.dismiss();
					progressDialog = null;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();

				}
			}
	}

	/**
	 * 显示吐丝
	 * @param context
	 * @param message
	 */
	public void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 显示吐丝
	 * @param context
	 * @param message
	 * @param gravity
	 */
	public void showCustomToast(Context context, String message,
			int gravity) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toast.setGravity(gravity, 0, 0);
		toast.show();
	}

}
