package com.zkjinshi.ritz.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.zkjinshi.base.util.Constants;
import com.zkjinshi.pyxis.bluetooth.NetBeaconVo;
import com.zkjinshi.ritz.blueTooth.BlueToothManager;
import com.zkjinshi.ritz.manager.YunBaSubscribeManager;
import com.zkjinshi.ritz.map.LocationManager;
import com.zkjinshi.ritz.utils.CacheUtil;

import java.util.ArrayList;

/**
 * 开机启动
 * 开发者：JimmyZhang
 * 日期：2016/3/15
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent data) {
        String action = data.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.i(Constants.ZKJINSHI_BASE_TAG, "开机自动启动SVIP.....");
            if(CacheUtil.getInstance().isLogin()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && !BlueToothManager.getInstance().isIBeaconServiceRunning()) {
                    Log.i(Constants.ZKJINSHI_BASE_TAG, "开机自动启动蓝牙服务.....");
                    BlueToothManager.getInstance().startIBeaconService(new ArrayList<NetBeaconVo>());
                }
                if(!LocationManager.getInstance().isRuning()){
                    Log.i(Constants.ZKJINSHI_BASE_TAG, "开机自动启动定位服务.....");
                    LocationManager.getInstance().startLocation();
                }
                //订阅云巴别名
                Log.i(Constants.ZKJINSHI_BASE_TAG, "开机自动云巴订阅别名.....");
                YunBaSubscribeManager.getInstance().setAlias(context, CacheUtil.getInstance().getUserId());
                //增加友盟统计
                Log.i(Constants.ZKJINSHI_BASE_TAG, "开机自动注入友盟统计.....");
                MobclickAgent.onProfileSignIn(CacheUtil.getInstance().getUserId());
            }
        }
    }
}
