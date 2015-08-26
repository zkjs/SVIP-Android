package com.zkjinshi.base.util;

import android.content.Context;
import android.os.Vibrator;

/**
 * Vibrator帮助类
 * 开发者：JimmyZhang
 * 日期：2015/8/26
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class VibratorHelper {

    /**
     * 播放震动效果
     *
     * @param context
     */
    public static void vibratorShark(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 400, 100, 400};
        vibrator.vibrate(pattern, -1);
    }
}
