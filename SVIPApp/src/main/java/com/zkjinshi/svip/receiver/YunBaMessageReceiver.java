package com.zkjinshi.svip.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zkjinshi.svip.utils.Constants;

import io.yunba.android.manager.YunBaManager;

/**
 * 开发者：JimmyZhang
 * 日期：2016/2/26
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class YunBaMessageReceiver extends BroadcastReceiver {

    public static final String TAG = YunBaMessageReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, final Intent intent) {

        //云巴推送处理
        if (YunBaManager.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {

            String topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
            String msg = intent.getStringExtra(YunBaManager.MQTT_MSG);
            Log.i(TAG, "YunBaMessageReceiver-msg:" + msg);
            Log.i(TAG, "YunBaMessageReceiver-topic:" + topic);
            Intent iBeaconIntent = new Intent();
            iBeaconIntent.setAction(Constants.SHOW_IBEACON_PUSH_MSG_RECEIVER_ACTION);
            iBeaconIntent.putExtra("msg",msg);
            context.sendBroadcast(iBeaconIntent);

        } else if (YunBaManager.PRESENCE_RECEIVED_ACTION.equals(intent.getAction())) {
            String topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
            String payload = intent.getStringExtra(YunBaManager.MQTT_MSG);
            StringBuilder showMsg = new StringBuilder();
            showMsg.append("Received message presence: ").append(YunBaManager.MQTT_TOPIC)
                    .append(" = ").append(topic).append(" ")
                    .append(YunBaManager.MQTT_MSG).append(" = ").append(payload);
            Log.d(TAG, showMsg.toString());

        }
    }
}
