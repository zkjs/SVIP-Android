package com.zkjinshi.base.net.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zkjinshi.base.net.core.WebSocketClient;

/**
 * 心跳广播
 * 开发者:赖清泉
 * 修改者：JimmyZhang
 * 日期：2015/7/19
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class HeartbeatReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals("HEARTBEAT")){
			new Thread(){
				@Override
				public void run(){
					WebSocketClient.getInstance().sendHeartbeat();
				}
			}.start();
		}
	}
}
