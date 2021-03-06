package com.zkjinshi.svip.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.ActivityManagerHelper;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.svip.activity.common.MainActivity;
import com.zkjinshi.svip.activity.facepay.PayActivity;
import com.zkjinshi.svip.notification.NotificationHelper;
import com.zkjinshi.svip.sqlite.BeaconMsgDBUtil;
import com.zkjinshi.svip.sqlite.InvitationMsgDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.vo.CallReadyVo;
import com.zkjinshi.svip.vo.InvitationVo;
import com.zkjinshi.svip.vo.OtherShopVo;
import com.zkjinshi.svip.vo.PayRecordDataVo;
import com.zkjinshi.svip.vo.YunBaMsgVo;

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

    private int width;

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
                        if (ActivityManagerHelper.isRunningBackground(context) || CacheUtil.getInstance().isScreenOff()){
                            NotificationHelper.getInstance().showNotification(context, amountStatusVo);
                            MainActivity.showMsgAnimation = true;
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
                }else if("BLE_ACTIVITY".equals(type)){
                    YunBaMsgVo yunBaMsgVo = new Gson().fromJson(data,YunBaMsgVo.class);
                    LogUtil.getInstance().info(LogLevel.WARN,"营销信息推送内容:"+msg);
                    BeaconMsgDBUtil.getInstance().insertBeaconMsg(yunBaMsgVo);
                    if(ActivityManagerHelper.isRunningBackground(context) || CacheUtil.getInstance().isScreenOff()){
                        if(null != yunBaMsgVo){
                            NotificationHelper.getInstance().showNotification(context,yunBaMsgVo);
                        }
                    }else {
                        Intent iBeaconIntent = new Intent();
                        iBeaconIntent.setAction(Constants.SHOW_IBEACON_PUSH_MSG_RECEIVER_ACTION);
                        iBeaconIntent.putExtra("data",yunBaMsgVo);
                        context.sendBroadcast(iBeaconIntent);
                    }
                }else if("ACT_INVITATION".equals(type) || "ACT_IVT_UPDATE".equals(type)){//活动新增或更新
                    InvitationVo invitationVo = new Gson().fromJson(data,InvitationVo.class);
                    InvitationMsgDBUtil.getInstance().insertInvitationMsg(invitationVo);
                    if(ActivityManagerHelper.isRunningBackground(context) || CacheUtil.getInstance().isScreenOff()){
                        if(null != invitationVo){
                            NotificationHelper.getInstance().showInvitationNotification(context,invitationVo,false);
                        }
                    }else {
                        Intent invitationIntent = new Intent();
                        invitationIntent.setAction(Constants.ACTIVITY_INVITATION_ACTION);
                        invitationIntent.putExtra("data",invitationVo);
                        context.sendBroadcast(invitationIntent);
                    }
                }else if("ACT_IVT_CANCELLED".equals(type)){//活动取消
                    InvitationVo invitationVo = new Gson().fromJson(data,InvitationVo.class);
                    if(ActivityManagerHelper.isRunningBackground(context) || CacheUtil.getInstance().isScreenOff()){
                        if(null != invitationVo){
                            NotificationHelper.getInstance().showInvitationNotification(context,invitationVo,true);
                        }
                    }else {
                        Intent invitationIntent = new Intent();
                        invitationIntent.setAction(Constants.ACTIVITY_INVITATION_CANCELLED_ACTION);
                        invitationIntent.putExtra("data",invitationVo);
                        context.sendBroadcast(invitationIntent);
                    }
                }else if("CALL_READY".equals(type)){
                    CallReadyVo callReadyVo = new Gson().fromJson(data,CallReadyVo.class);
                    if(ActivityManagerHelper.isRunningBackground(context) || CacheUtil.getInstance().isScreenOff()){
                        if(null != callReadyVo){
                           NotificationHelper.getInstance().showCallReadyNotification(context,callReadyVo);
                        }
                    }else {
                        Intent readyIntent = new Intent();
                        readyIntent.setAction(Constants.CALL_READY_ACTION);
                        readyIntent.putExtra("data",callReadyVo);
                        context.sendBroadcast(readyIntent);
                    }

                }else if("CALL_DONE".equals(type)){
                    CallReadyVo callReadyVo = new Gson().fromJson(data,CallReadyVo.class);
                    Intent doneIntent = new Intent();
                    doneIntent.setAction(Constants.CALL_DONE_ACTION);
                    doneIntent.putExtra("data",callReadyVo);
                    context.sendBroadcast(doneIntent);

                }else if("ANOTHER_SHOP".equals(type)){//获取商店信息
                    OtherShopVo otherShopVo = new Gson().fromJson(data,OtherShopVo.class);
                    if(null != otherShopVo){
                        String shopId = otherShopVo.getShopid();
                        if(!TextUtils.isEmpty(shopId)){
                            CacheUtil.getInstance().saveShopId(shopId);
                        }
                        String shopLogo = otherShopVo.getLogo();
                        if(!TextUtils.isEmpty(shopLogo)){
                            width = DisplayUtil.dip2px(context,96);
                            CacheUtil.getInstance().saveShopLogo(ConfigUtil.getInst().getPcdDomain()+shopLogo+"@"+width+"h.png");
                        }
                        String expiryTimeStr = otherShopVo.getValidthru();
                        if(!TextUtils.isEmpty(expiryTimeStr)){
                            long expiryTime = TimeUtil.timeStrToTimeStamp(expiryTimeStr);
                            CacheUtil.getInstance().saveExpiryLogoTime(expiryTime);
                        }
                        Intent updateIntent = new Intent();
                        updateIntent.setAction(Constants.UPDATE_LOGO_RECEIVER_ACTION);
                        context.sendBroadcast(updateIntent);
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}
