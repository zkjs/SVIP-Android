package com.zkjinshi.svip.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.zkjinshi.base.util.ActivityManagerHelper;
import com.zkjinshi.svip.activity.facepay.PayActivity;
import com.zkjinshi.svip.notification.NotificationHelper;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.vo.PayRecordDataVo;

import org.json.JSONException;
import org.json.JSONObject;

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

            try {
                String topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
                String msg = intent.getStringExtra(YunBaManager.MQTT_MSG);
                Log.i(TAG, "YunBaMessageReceiver-msg:" + msg);
                Log.i(TAG, "YunBaMessageReceiver-topic:" + topic);
                JSONObject jsonObject = new JSONObject(msg);
                String type = jsonObject.getString("type");
                String data = jsonObject.getString("data");
                if ("PAYMENT_CONFIRM".equals(type)) {
                    PayRecordDataVo amountStatusVo = new Gson().fromJson(data, PayRecordDataVo.class);
                    if (null != amountStatusVo) {
                        if (ActivityManagerHelper.isRunningBackground(context)){
                            NotificationHelper.getInstance().showNotification(context, amountStatusVo);
                        }else{
                            Intent payIntent = new Intent(context, PayActivity.class);
                            payIntent.putExtra("amountStatusVo",amountStatusVo);
                            payIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(payIntent);
                        }

                        Intent showContactIntent = new Intent(Constants.SHOW_CONTACT_RECEIVER_ACTION);
                        context.sendBroadcast(showContactIntent);
                    }
                }else if("PAYMENT_RESULT".equals(type)){
                    PayRecordDataVo amountStatusVo = new Gson().fromJson(data, PayRecordDataVo.class);
                    if (null != amountStatusVo) {
                        NotificationHelper.getInstance().showNotificationResult(context, amountStatusVo);
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

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
