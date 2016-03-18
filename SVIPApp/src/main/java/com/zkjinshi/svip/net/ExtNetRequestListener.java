package com.zkjinshi.svip.net;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;

import com.google.gson.Gson;


/**
 * 增加token失效跳转回登录页面的网络请求监听器
 * 开发者：JimmyZhang
 * 日期：2015/10/23
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public abstract class ExtNetRequestListener  implements NetRequestListener{

    //public static final String TAG = ExtNetRequestListener.class.getSimpleName();

    private Context context;

    public ExtNetRequestListener(Context context){
        this.context = context;
    }

    @Override
    public void onNetworkRequestError(int errorCode, String errorMessage) {
        Log.i(ExtNetRequestListener.class.getSimpleName(),"errorCode:"+errorCode);
        Log.i(ExtNetRequestListener.class.getSimpleName(),"errorMessage:"+errorMessage);
    }

    @Override
    public void onNetworkRequestCancelled() {
        Log.i(ExtNetRequestListener.class.getSimpleName(),"onNetworkRequestCancelled");
    }

    @Override
    public void onNetworkResponseSucceed(NetResponse result) {


    }

    @Override
    public void beforeNetworkRequestStart() {
        Log.i(ExtNetRequestListener.class.getSimpleName(),"beforeNetworkRequestStart");
    }


}
