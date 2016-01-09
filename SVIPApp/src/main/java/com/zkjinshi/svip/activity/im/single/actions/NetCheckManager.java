package com.zkjinshi.svip.activity.im.single.actions;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.view.View;
import android.widget.TextView;

import com.zkjinshi.base.util.IntentUtil;
import com.zkjinshi.base.util.NetWorkUtil;
import com.zkjinshi.svip.R;

/**
 * 网络状态监测管理器
 * 开发者：JimmyZhang
 * 日期：2015/11/5
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class NetCheckManager {

    private Context context;
    private TextView netSetTv;
    private NetCheckReceiver netCheckReceiver;

    public NetCheckManager init(Context context) {
        this.context = context;
        initView(context);
        initData();
        initListeners();
        return this;
    }

    public void initView(Context context) {

        netSetTv = (TextView) ((Activity) context).findViewById(R.id.chat_txt_netset);

    }

    public void initData() {

        netSetTv.setVisibility(NetWorkUtil.isNetworkConnected(context) ? View.GONE
                : View.VISIBLE);
    }

    public void initListeners() {

        //打开网络设置
        netSetTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.showNetworkSetting(v.getContext());
            }
        });
    }

    /**
     * 注册网络监听广播
     */
    public void registernetCheckReceiver() {
        netCheckReceiver = new NetCheckReceiver();
        context.registerReceiver(netCheckReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     * 移除网络监听广播
     */
    public void unregisternetCheckReceiver() {
        if (null != netCheckReceiver) {
            context.unregisterReceiver(netCheckReceiver);
        }
    }

    /**
     * 用广播检查网络是否正常
     */
    private class NetCheckReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(
                    ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (intent.getBooleanExtra(
                        ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {// 网络断开
                    netSetTv.setVisibility(View.VISIBLE);
                } else {// 网络连接正常
                    netSetTv.setVisibility(View.GONE);
                }
            }
        }
    }
}
