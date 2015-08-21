/**   
 * 文件名：MessageSpanURL.java   
 *   
 * 版本信息：   
 * 日期：2014-5-15   
 * Copyright (C) 2014 深圳市建乔无线信息技术有限公司   
 * 版权所有   
 *   
 */

package com.zkjinshi.svip.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.zkjinshi.base.util.ClipboardUtil;
import com.zkjinshi.base.util.IntentUtil;
import com.zkjinshi.base.util.SoftInputUtil;
import com.zkjinshi.svip.R;

/**
 * 此类描述的是： [MessageSpanURL处理各种事件]
 * 
 * @author: [JimmyZhang]
 * @CreateDate: [2014-5-15 上午10:47:28]
 * @UpdateUser: [JimmyZhang]
 * @UpdateDate: [2014-5-15 上午10:47:28]
 * @UpdateRemark: [说明本次修改内容]
 * @Version: [v1.0.0]
 */

public class MessageSpanURL extends ClickableSpan {

	private String mUrl;
	private Context context;
	private LayoutInflater inflater;

	public MessageSpanURL(Context context, String string) {
		mUrl = string;
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}
	
	@Override
	public void onClick(View widget) {
		if (mUrl.startsWith("tel:")) {
			String phoneNum = mUrl.replace("tel:", "");
			SoftInputUtil.hideSoftInputMode(context, widget);
			showDialog(context,phoneNum);
		}
		if (mUrl.startsWith("http://") || mUrl.startsWith("https://")) {//增加https
			IntentUtil.openBrowser(mUrl,context);
		}
		if (mUrl.startsWith("mailto:")
				|| IntentUtil.isEmailAddress(mUrl.toString())) {
			IntentUtil.sendEmail("", "", mUrl, context);
		}
	}

	/**
	 * 提示操作确认框
	 *
	 */
	protected void showDialog(final Context context,final String phoneNum) {
		final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.optionssheet_dialog, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		Button titleBtn = (Button) layout.findViewById(R.id.dialog_btn_title);
		titleBtn.setText(phoneNum+context.getString(R.string.phone_prompty));
		Button callBtn = (Button) layout.findViewById(R.id.dialog_btn_call);
		Button messageBtn = (Button) layout.findViewById(R.id.dialog_btn_message);
		Button copeBtn = (Button) layout.findViewById(R.id.dialog_btn_copy);
		Button cancelBtn = (Button) layout.findViewById(R.id.dialog_btn_cancel);
		//拨打
		callBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
				IntentUtil.callPhone(context,phoneNum);
			}
		});
		//发短信
		messageBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
				IntentUtil.sendMessage("",phoneNum,context);
			}
		});
		//复制
		copeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dlg.dismiss();
				ClipboardUtil.copy(phoneNum, context);
			}
		});
		//取消
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
	}
}
