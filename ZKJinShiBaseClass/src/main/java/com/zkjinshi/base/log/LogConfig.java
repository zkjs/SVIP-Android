package com.zkjinshi.base.log;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.text.SimpleDateFormat;

/**
 * 日志配置类
 * 开发者：JimmyZhang
 * 日期：2015/7/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LogConfig {

	private Context context;
	private String logPath;//日志存储路径
	private LogSwitch logSwitch;//是否开启日志
	private SimpleDateFormat dateSdf,timeSdf;

	@SuppressLint("SimpleDateFormat") public SimpleDateFormat getDateSdf() {
		if(null == dateSdf){
			dateSdf = new SimpleDateFormat("yyyy-MM-dd");
		}
		return dateSdf;
	}
	
	@SuppressLint("SimpleDateFormat") public SimpleDateFormat getTimeSdf() {
		if(null == timeSdf){
			timeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		return timeSdf;
	}

	public LogSwitch getLogSwitch() {
		return logSwitch;
	}

	public LogConfig setLogSwitch(LogSwitch logSwitch) {
		this.logSwitch = logSwitch;
		return this;
	}

	public Context getContext() {
		return context;
	}

	public LogConfig setContext(Context context) {
		this.context = context;
		return this;
	}
	
	public LogConfig setLogPath(String logPath) {
		this.logPath = logPath;
		return this;
	}
	
	/**
	 * 根据日志等级获取存储路径
	 * @param logLevel
	 * @return
	 */
	public String getLogPath(LogLevel logLevel){
		if(LogLevel.DEBUG ==  logLevel){
			return getDebugLogPath();
		}else if(LogLevel.INFO == logLevel){
			return getInfoLogPath();
		}else if(LogLevel.WARN == logLevel){
			return getWarnLogPath();
		}else{
			return getErrorLogPath();
		}
	}
	
	/**
	 * 获取info日志存储路径
	 * @return
	 */
	public String getInfoLogPath(){
		return getLogPath()+"info/";
	}
	
	/**
	 * 获取debug日志存储路径
	 * @return
	 */
	public String getDebugLogPath(){
		return getLogPath()+"debug/";
	}
	
	/**
	 * 获取warm日志存储路径
	 * @return
	 */
	public String getWarnLogPath(){
		return getLogPath()+"warn/";
	}
	
	/**
	 * 获取error日志存储路径
	 * @return
	 */
	public String getErrorLogPath(){
		return getLogPath()+"error/";
	}
	
	/**
	 * 获取crash日志存储路径
	 * @return
	 */
	public String getCrashLogPath(){
		return getLogPath()+"crash/";
	}
	
	/**
	 * 获取日志存储根路径
	 * @return
	 */
	public String getLogPath() {
		if(TextUtils.isEmpty(logPath)){
			return getDefaultLogPath();
		}
		return logPath;
	}
	
	/**
	 * 获取日志默认存储根目录
	 * @return
	 */
	public String getDefaultLogPath(){
		return Environment.getExternalStorageDirectory()+"/"+context.getPackageName()+"/log/";
	}

}
