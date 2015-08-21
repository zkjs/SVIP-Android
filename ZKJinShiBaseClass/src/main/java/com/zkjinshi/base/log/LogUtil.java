package com.zkjinshi.base.log;

import android.annotation.SuppressLint;
import android.util.Log;

import com.zkjinshi.base.util.Constants;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

/**
 * 日志操作管理类
 * 开发者：JimmyZhang
 * 日期：2015/7/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LogUtil {

	public static LogUtil instance;

	private LogUtil() {
	};

	private LogConfig logConfig;

	private boolean openLog = true;

	public boolean isOpenLog() {
		return openLog;
	}

	public void setOpenLog(boolean openLog) {
		this.openLog = openLog;
	}

	public synchronized static LogUtil getInstance() {
		if (null == instance) {
			instance = new LogUtil();
		}
		return instance;
	}

	public void init(LogConfig logConfig) {
		this.logConfig = logConfig;
		CrashHandler.getInstance().init(logConfig.getContext());
	}

	public LogConfig getLogConfig() {
		return logConfig;
	}
	
	/**
	 * 记录消息日志
	 * @param level
	 * @param logMessage
	 */
	public void info(LogLevel level,String logMessage){
		//打印消息日志
		printLog(level, logMessage);
		//保存消息日志
		saveLog(level, logMessage);
	}
	
	private void printLog(LogLevel level,String logMessage){
		if(LogLevel.DEBUG == level){
			Log.d(Constants.ZKJINSHI_BASE_TAG, logMessage);
		}else if(LogLevel.INFO == level){
			Log.i(Constants.ZKJINSHI_BASE_TAG, logMessage);
		}else if(LogLevel.WARN == level){
			Log.w(Constants.ZKJINSHI_BASE_TAG, logMessage);
		}else{
			Log.e(Constants.ZKJINSHI_BASE_TAG, logMessage);
		}
	}
	
	@SuppressLint("DefaultLocale") private void saveLog(LogLevel level,String logMessage){
		LogSwitch logSwitch = logConfig.getLogSwitch();
		if(LogSwitch.OPEN == logSwitch){
			String logPath = logConfig.getLogPath(level);
			try {
				File dirFile = new File(logPath);
				if (!dirFile.exists()) {
					dirFile.mkdirs();
				}
				Date date = new Date();
				String dateStr = logConfig.getDateSdf().format(date);
				String timeStr = logConfig.getTimeSdf().format(date);
				String logFileName = level.name().toLowerCase()+"_"+dateStr + ".log";
				File logFile = new File(logPath + logFileName);
				FileWriter fw = new FileWriter(logFile, true);
				fw.write("["+timeStr+"]"+logMessage + "\n");
				fw.close();
			} catch (Exception e) {
				printLog(LogLevel.ERROR, e.getMessage());
			}
		}
	}
}
