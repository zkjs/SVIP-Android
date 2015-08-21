package com.zkjinshi.base.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * 格式化时间工具类
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtil {
	
	public static String getYearTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return format.format(new Date(time));
	}

	public static String getTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
		return format.format(new Date(time));
	}

	public static String getHourAndMin(long time) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(new Date(time));
	}

	public static String getWeakHourAndMin(long time) {
		SimpleDateFormat format = new SimpleDateFormat("EEEE HH:mm");
		return format.format(new Date(time));
	}

	public static String getChatTime(long timesamp) {
		String result = "";
		SimpleDateFormat yearsdf = new SimpleDateFormat("yyyy");
		SimpleDateFormat monthsdf = new SimpleDateFormat("MM");
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		long currentSamp = System.currentTimeMillis();
		Date today = new Date(currentSamp);
		Date otherDay = new Date(timesamp);
		String todayYearStr = yearsdf.format(today);
		String otherYearStr = yearsdf.format(otherDay);
		String todayMonthStr = monthsdf.format(today);
		String otherMonthStr = monthsdf.format(otherDay);
		if(todayYearStr.equals(otherYearStr)){//同一年
			int temp = 0;
			if(todayMonthStr.equals(otherMonthStr)){//同一个月份
				temp = Integer.parseInt(sdf.format(today))
						- Integer.parseInt(sdf.format(otherDay));
			}else{//不同月份计算相差天数
				Calendar todayCalendar = Calendar.getInstance();
				Calendar otherCalendar = Calendar.getInstance();
				todayCalendar.setTimeInMillis(currentSamp);
				otherCalendar.setTimeInMillis(timesamp);
				temp =  todayCalendar.get(Calendar.DAY_OF_YEAR) - otherCalendar.get(Calendar.DAY_OF_YEAR);
			}
			switch (temp) {
			case 0:
				result = "今天 " + getHourAndMin(timesamp);
				break;
			case 1:
				result = "昨天 " + getHourAndMin(timesamp);
				break;
			case 2:
				result = "前天 " + getHourAndMin(timesamp);
				break;
			case 3:
			case 4:
			case 5:
			case 6:
				result = getWeakHourAndMin(timesamp);
				break;

			default:
				result = getTime(timesamp);
				break;
			}
		}else{
			result = getYearTime(timesamp);
		}
		return result;
	}
}
