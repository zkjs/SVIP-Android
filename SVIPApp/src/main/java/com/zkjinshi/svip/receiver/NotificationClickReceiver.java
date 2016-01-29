package com.zkjinshi.svip.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.utils.Constants;

/**
 * Created by dujiande on 2016/1/29.
 */
public class NotificationClickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.getInstance().info(LogLevel.DEBUG,"NotificationClickReceiver");

        Intent realIntent = intent.getParcelableExtra("realIntent");
        context.startActivity(realIntent);

        Intent showContactIntent = new Intent(Constants.SHOW_CONTACT_RECEIVER_ACTION);
        context.sendBroadcast(showContactIntent);
    }
}
