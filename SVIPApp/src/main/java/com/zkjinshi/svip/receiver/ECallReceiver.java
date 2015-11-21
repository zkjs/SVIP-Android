package com.zkjinshi.svip.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.easemob.chat.EMChat;
import com.easemob.util.EMLog;
import com.zkjinshi.svip.activity.im.VideoCallActivity;
import com.zkjinshi.svip.activity.im.VoiceCallActivity;

/**
 * 开发者：JimmyZhang
 * 日期：2015/11/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ECallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!EMChat.getInstance().isLoggedIn())
            return;
        //拨打方username
        String from = intent.getStringExtra("from");
        //call type
        String type = intent.getStringExtra("type");
        if("video".equals(type)){ //视频通话
            context.startActivity(new Intent(context, VideoCallActivity.class).
                    putExtra("username", from).putExtra("isComingCall", true).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }else{ //音频通话
            context.startActivity(new Intent(context, VoiceCallActivity.class).
                    putExtra("username", from).putExtra("isComingCall", true).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}
