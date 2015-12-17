package com.zkjinshi.svip.net;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.bean.BaseBean;
import com.zkjinshi.svip.utils.CacheUtil;

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
        try{
            BaseBean baseBean = new Gson().fromJson(result.rawResult,BaseBean.class);
            if(baseBean!= null && !baseBean.isSet() && baseBean.getErr().equals("400")){
                if(context instanceof Activity){
                   // DialogUtil.getInstance().showToast(context, "Token失效，请重新登录!");
                  NetDialogUtil.showLoginDialog((Activity) context);
                }
            }
        }catch (Exception e){
           // Log.e(ExtNetRequestListener.class.getSimpleName(),e.getMessage());
        }

    }

    @Override
    public void beforeNetworkRequestStart() {
        Log.i(ExtNetRequestListener.class.getSimpleName(),"beforeNetworkRequestStart");
    }


}
