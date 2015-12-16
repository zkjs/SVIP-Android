
package com.zkjinshi.base.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Patterns;
import android.view.Gravity;

import com.zkjinshi.base.view.CustomDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 调用系统意图工具类
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class IntentUtil {

	public static final Pattern NAME_ADDR_EMAIL_PATTERN = Pattern
			.compile("\\s*(\"[^\"]*\"|[^<>\"]+)\\s*<([^<>]+)>\\s*");

	public static String extractAddrSpec(String address) {
		Matcher match = NAME_ADDR_EMAIL_PATTERN.matcher(address);

		if (match.matches()) {
			return match.group(2);
		}
		return address;
	}

	/**
	 * google封装判断是否是邮箱格式
	 *
	 * @param address
	 * @return
	 */
	public static boolean isEmailAddress(String address) {
		String s = extractAddrSpec(address);
		Matcher match = Patterns.EMAIL_ADDRESS.matcher(s);
		return match.matches();
	}

	/**
	 * 验证手机号码
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles){
		/**
		 中国电信发布中国3G号码段:中国联通185,186;中国移动188,187;中国电信189,180共6个号段。
		 3G业务专属的180-189号段已基本分配给各运营商使用, 其中180、189分配给中国电信,187、188归中国移动使用,185、186属于新联通。
		 中国移动拥有号码段：139、138、137、136、135、134、159、158、157（3G）、152、151、150、188（3G）、187（3G）;14个号段
		 中国联通拥有号码段：130、131、132、155、156（3G）、186（3G）、185（3G）;6个号段
		 中国电信拥有号码段：133、153、189（3G）、180（3G）;4个号码段
		 移动:
		 2G号段(GSM网络)有139,138,137,136,135,134(0-8),159,158,152,151,150
		 3G号段(TD-SCDMA网络)有157,188,187
		 147是移动TD上网卡专用号段.
		 联通:
		 2G号段(GSM网络)有130,131,132,155,156
		 3G号段(WCDMA网络)有186,185
		 电信:
		 2G号段(CDMA网络)有133,153
		 3G号段(CDMA网络)有189,180
		 */
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 拨打电话
	 * @param ctx
	 * @param phoneNum
	 */
	public static void callPhone(Context ctx,String phoneNum) {
		startCallPhone(ctx, phoneNum);
	}

	/**
	 * 弹出拨打电话提示框
	 * @param context
	 * @param phoneNum
	 */
	public static void startCallPhone(final Context context,final String phoneNum) {
		CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
		customBuilder.setTitle("温馨提示");
		customBuilder.setMessage(phoneNum);
		customBuilder.setGravity(Gravity.CENTER);
		customBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		customBuilder.setPositiveButton("呼叫", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_CALL,
						Uri.parse("tel:" + phoneNum));
				context.startActivity(intent);
				dialog.dismiss();
			}
		});
		customBuilder.create().show();
	}

	/**
	 * 发送短信
	 * @param message
	 * @param phoneNum
	 * @param ctx
	 */
	public static void sendMessage(String message, String phoneNum, Context ctx) {
		startSendMessage(message, phoneNum, ctx);
	}

	/**
	 * 弹出发送短信提示框
	 * @param message
	 * @param phoneNum
	 * @param context
	 */
	public static void startSendMessage(final String message, final String phoneNum, final Context context) {
		CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
		customBuilder.setTitle("温馨提示");
		customBuilder.setMessage("确定发送短信给"+phoneNum+"?");
		customBuilder.setGravity(Gravity.CENTER);
		customBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		customBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Uri uri = Uri.parse("smsto:" + phoneNum);
				Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
				intent.putExtra("sms_body", message);
				context.startActivity(intent);
				dialog.dismiss();
			}
		});
		customBuilder.create().show();
	}

	/**
	 * 发送邮件
	 * @param subject 主题
	 * @param body 内容
	 * @param email 邮件地址
	 * @param ctx
	 */
	public static void sendEmail(String subject, String body, String email,
			Context ctx) {
		startSendEmail(subject, body, email, ctx);
	}

	/**
	 * 弹出发送邮箱提示框
	 * @param subject
	 * @param body
	 * @param email
	 * @param ctx
	 */
	public static void startSendEmail(final String subject, final String body,final String email,
			final Context ctx) {
		CustomDialog.Builder customBuilder = new CustomDialog.Builder(ctx);
		customBuilder.setTitle("温馨提示");
		customBuilder.setMessage("确认发送邮件给"+email+"?");
		customBuilder.setGravity(Gravity.CENTER);
		customBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		customBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
				emailIntent.setData(Uri.parse("mailto:" + email));
				String emailSubject = subject;
				String emailBody = body;
				emailIntent
						.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
				emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);
				ctx.startActivity(emailIntent);
				dialog.dismiss();
			}
		});
		customBuilder.create().show();
	}

	/**
	 * 打开浏览器
	 * @param url
	 * @param ctx
	 */
	public static void openBrowser(String url,Context ctx){
		Uri content_url = Uri.parse(url);   
		Intent intent= new Intent(Intent.ACTION_VIEW,content_url);        
	    ctx.startActivity(intent);
	}
	/**
	 *
	 * 打开网络设置页面
	 */
	public static void showNetworkSetting(Context context) {
		Intent i = null;
		if (android.os.Build.VERSION.SDK_INT > 10) {
			// 3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
			i = new Intent(Settings.ACTION_SETTINGS);
		} else {
			i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
		}
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);// 进入无线网络配置界面
	}


}
