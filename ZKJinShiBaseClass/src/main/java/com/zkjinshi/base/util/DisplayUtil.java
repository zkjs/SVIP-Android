
package com.zkjinshi.base.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 分辨率操作工具类
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class DisplayUtil {

	/**
	 *
	 * 此方法描述的是：   获得屏幕宽度
	 * @author:JimmyZhang
	 * @since: 2013-12-17 下午7:50:54
	 * @param activity
	 * @return
	 * @return int
	 */
	public static int getWidthPixel(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	/**
	 *
	 * 此方法描述的是：   获得屏幕高度
	 * @author:JimmyZhang
	 * @since: 2013-12-17 下午7:51:01
	 * @param activity
	 * @return
	 * @return int
	 */
	public static int getHeightPixel(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
 	 * @param context
	 * @param dpValue
	 * @return
	 */
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
 	 * @param context
	 * @param pxValue
	 * @return
	 */
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * @param context
	 * @param pxValue
	 * @return
	 */
    public static int px2sp(Context context, float pxValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (pxValue / fontScale + 0.5f);  
    }

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * @param context
	 * @param spValue
	 * @return
	 */
    public static int sp2px(Context context, float spValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (spValue * fontScale + 0.5f);  
    }  

}
