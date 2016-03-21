package com.zkjinshi.svip.utils;

import android.content.Context;
import android.content.Intent;

import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.base.BaseApplication;

/**
 * Created by dujiande on 2016/3/21.
 */
public class AsyncHttpClientUtil {

    public static void onFailure(Context context, int statusCode){
        if(statusCode == 401){
            CacheUtil.getInstance().setLogin(false);
            BaseApplication.getInst().clear();
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }
    }
}
