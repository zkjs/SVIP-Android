package com.zkjinshi.ritz.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zkjinshi.ritz.utils.CacheUtil;

/**
 * 开发者：JimmyZhang
 * 日期：2016/3/31
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ScreenObserverReceiver extends BroadcastReceiver {

    public static final String TAG = ScreenObserverReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        if(Intent.ACTION_SCREEN_ON.equals(intent.getAction())){
            Log.i(TAG, "亮屏");
        } else if(Intent.ACTION_SCREEN_OFF.equals(intent.getAction())){
            Log.i(TAG, "锁屏");
            CacheUtil.getInstance().setScreenOff(true);
        } else if(Intent.ACTION_USER_PRESENT.equals(intent.getAction())){
            Log.i(TAG, "解锁");
            CacheUtil.getInstance().setScreenOff(false);
        }
    }
}
