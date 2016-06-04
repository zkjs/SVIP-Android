package com.zkjinshi.ritz.net;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zkjinshi.ritz.activity.common.LoginActivity;
import com.zkjinshi.ritz.base.BaseApplication;
import com.zkjinshi.ritz.utils.CacheUtil;

/**
 * 增加token失效跳转回登录页面的网络请求监听器
 * 开发者：JimmyZhang
 * 日期：2015/10/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public abstract class ExtNetRequestListener  implements NetRequestListener {

    public static final String TAG = ExtNetRequestListener.class.getSimpleName();

    private Context context;

    public ExtNetRequestListener(Context context){
        this.context = context;
    }

    @Override
    public void onNetworkRequestError(int errorCode, String errorMessage) {
        Log.i(TAG,"errorCode:"+errorCode);
        Log.i(TAG,"errorMessage:"+errorMessage);
    }

    @Override
    public void onNetworkRequestCancelled() {
        Log.i(TAG,"onNetworkRequestCancelled");
    }

    @Override
    public void onNetworkResponseSucceed(NetResponse result) {

    }

    @Override
    public void beforeNetworkRequestStart() {
        Log.i(TAG,"beforeNetworkRequestStart");
    }

    @Override
    public void onCookieExpired() {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CacheUtil.getInstance().setLogin(false);
                BaseApplication.getInst().clearLeaveTop();
                Intent intent = new Intent(context,LoginActivity.class);
                ((Activity)context).startActivity(intent);
            }
        });
    }
}
