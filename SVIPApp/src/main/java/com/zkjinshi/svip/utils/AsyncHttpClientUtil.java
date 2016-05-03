package com.zkjinshi.svip.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.base.BaseApplication;
import com.zkjinshi.svip.blueTooth.BlueToothManager;
import com.zkjinshi.svip.manager.YunBaSubscribeManager;
import com.zkjinshi.svip.map.LocationManager;

import io.yunba.android.manager.YunBaManager;

/**
 * Created by dujiande on 2016/3/21.
 */
public class AsyncHttpClientUtil {

    public static void onFailure(Context context, int statusCode){
        if(statusCode == 401){
            forceExit(context);
            Toast.makeText(context,"Token 失效，请重新登录",Toast.LENGTH_SHORT).show();
        }else if(statusCode == 0){
            Toast.makeText(context,"网络超时",Toast.LENGTH_SHORT).show();
        }else if(statusCode == 408){
            Toast.makeText(context,"请求超时",Toast.LENGTH_SHORT).show();
        }else if(statusCode == 504){
            Toast.makeText(context,"网关超时",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
        }
    }

    public static void onFailure(Context context, int statusCode,String url){
        if(statusCode == 401){
            forceExit(context);
            Toast.makeText(context,"Token 失效，请重新登录",Toast.LENGTH_SHORT).show();
        }else if(statusCode == 0){
            Toast.makeText(context,"网络超时",Toast.LENGTH_SHORT).show();
        }else if(statusCode == 408){
            Toast.makeText(context,"请求超时",Toast.LENGTH_SHORT).show();
        }else if(statusCode == 504){
            Toast.makeText(context,"网关超时",Toast.LENGTH_SHORT).show();
        }else if(statusCode == 302){
            LogUtil.getInstance().info(LogLevel.ERROR,"访问："+url+"时出现302错误");
            Toast.makeText(context,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
        }
    }

    public static void forceExit(Context context){
        BlueToothManager.getInstance().stopIBeaconService();
        LocationManager.getInstance().stopLocation();
        YunBaSubscribeManager.getInstance().unSubscribe(context.getApplicationContext());

        CacheUtil.getInstance().setLogin(false);
        BaseApplication.getInst().clear();
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
