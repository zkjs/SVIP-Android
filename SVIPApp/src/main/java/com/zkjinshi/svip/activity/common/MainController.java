package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.view.CustomDialog;

import com.zkjinshi.svip.SVIPApplication;

import com.zkjinshi.svip.update.NotificationUpdateActivity;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;

import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.UpdateVo;
import com.zkjinshi.svip.vo.UpgradeVo;

import org.json.JSONObject;



import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 开发者：dujiande
 * 日期：2015/11/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MainController {

    private final static String TAG =  MainController.class.getSimpleName();

    private MainController(){}
    private static MainController instance;
    private Context context;
    private Activity activity;
    public int currentVersionCode;
    public String appVersion;
    public SVIPApplication app;

    public static synchronized MainController getInstance(){
        if(null ==  instance){
            instance = new MainController();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
        this.activity = (Activity)context;
        app = (SVIPApplication)activity.getApplication();        
       
    }


    //检测版本
    public void checkAppVersion(){
        PackageManager manager = context.getPackageManager();

        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            appVersion = info.versionName; // 版本名
            currentVersionCode = info.versionCode; // 版本号

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG,e.getMessage());
        }
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.upgradeNewestVersion(1,"ANDROID",currentVersionCode+"");
            client.get(context,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    DialogUtil.getInstance().showAvatarProgressDialog(context,"");
                }

                public void onFinish(){
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        UpgradeVo upgradeVo = new Gson().fromJson(response,UpgradeVo.class);
                        if(upgradeVo.getRes() == 0 && upgradeVo.getData() != null){
                            UpdateVo updateVo = upgradeVo.getData();
                            int romoteCode = Integer.parseInt(updateVo.getVerno());
                            if(romoteCode  > currentVersionCode){
                                if(updateVo.getIsforceupgrade() == 0){
                                    showNormalUpdateDialog(updateVo);
                                }else if(updateVo.getIsforceupgrade() == 1){
                                    showForceUpdateDialog(updateVo);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    Toast.makeText(context,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
                    AsyncHttpClientUtil.onFailure(context,statusCode);
                }
            });
        }catch (Exception e){
            Toast.makeText(context,"json解析错误",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void showNormalUpdateDialog(final UpdateVo updateVo){
        final CustomDialog.Builder customerBuilder = new CustomDialog.Builder(context);
        customerBuilder.setTitle("检测到新版本");
        customerBuilder.setMessage("是否下载更新?");
        customerBuilder.setGravity(Gravity.CENTER);
        customerBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        customerBuilder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                activity.finish();
                downloadApp(updateVo);
            }
        });
        customerBuilder.create().show();
    }

    private void showForceUpdateDialog(final UpdateVo updateVo){
        Dialog dialog = null;
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
        customBuilder.setTitle("检测到新版本");
        customBuilder.setMessage("您的版本过旧，请更新到最新版本。");
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setNegativeButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                activity.finish();
                downloadApp(updateVo);
            }
        });
        dialog = customBuilder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    public void downloadApp( UpdateVo updateVo){
        Intent intent = new Intent(context, NotificationUpdateActivity.class);
        activity.startActivity(intent);
        app.setDownload(true);
        app.updateVo = updateVo;
    }
    
}
