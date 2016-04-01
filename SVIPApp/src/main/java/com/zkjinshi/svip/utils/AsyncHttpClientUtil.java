package com.zkjinshi.svip.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.activity.common.LoginSiActivity;
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
            Toast.makeText(context,"Token 失效，请重新登录",Toast.LENGTH_SHORT).show();
        }else if(statusCode == 0){
            Toast.makeText(context,"网络超时",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
        }
    }
}
