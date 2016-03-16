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
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.pyxis.bluetooth.IBeaconController;
import com.zkjinshi.pyxis.bluetooth.NetBeaconVo;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.bean.BaseBean;
import com.zkjinshi.svip.bean.UpdateBean;
import com.zkjinshi.svip.blueTooth.BlueToothManager;
import com.zkjinshi.svip.fragment.HomeFragment;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.BigPicResponse;
import com.zkjinshi.svip.response.GetBigPicResponse;
import com.zkjinshi.svip.response.GetUpgradeResponse;
import com.zkjinshi.svip.sqlite.ShopDetailDBUtil;
import com.zkjinshi.svip.update.NotificationUpdateActivity;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.ShopDetailVo;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    Animation bigAnimation;
    public int bigPicIndex = 0;
    ArrayList<BigPicResponse> bigPicResponseList = null;

    private  MainController(){}
    private static  MainController instance;
    private Context context;
    private Activity activity;
    public int currentVersionCode;
    public String appVersion;
    public SVIPApplication app;

    public static synchronized  MainController getInstance(){
        if(null ==  instance){
            instance = new  MainController();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
        this.activity = (Activity)context;
        app = (SVIPApplication)activity.getApplication();


    }

    /**
     * 设置首页动态图
     */
    public void setBigPicAnimation(SimpleDraweeView homePicIv){
        try {
            bigAnimation = AnimationUtils.loadAnimation(activity, R.anim.anim_bigger);
            homePicIv.startAnimation(bigAnimation);
            if(bigPicResponseList == null) {
                String rawResult = CacheUtil.getInstance().getListStrCache("bigPicResponseList");
                if(TextUtils.isEmpty(rawResult)){
                    return;
                }
                Type listType = new TypeToken<ArrayList<BigPicResponse>>() {
                }.getType();
                Gson gson = new Gson();
                bigPicResponseList = gson.fromJson(rawResult, listType);
            }

            int length = bigPicResponseList.size();
            if(length <= 0){
                return;
            }
            bigPicIndex = (bigPicIndex+1)%length;
            String bigPicUrl = ProtocolUtil.getHostImgUrl(bigPicResponseList.get(bigPicIndex).getUrl());
            homePicIv.setImageURI(Uri.parse(bigPicUrl));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 停止首页大图动画
     */
    public void pauseBigPicAnimation(){
        if(bigAnimation != null){
            bigAnimation.cancel();
            bigAnimation = null;
        }
    }



    /**
     * 初始化首页大图
     */
    public void initBigPic(){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getHomeBigPic();
            client.get(context,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        GetBigPicResponse getBigPicResponse = new Gson().fromJson(response,GetBigPicResponse.class);
                        if (getBigPicResponse == null){
                            return;
                        }
                        if(getBigPicResponse.getRes() == 0){
                            ArrayList<BigPicResponse> bigPicResponseList = getBigPicResponse.getData();
                            if(bigPicResponseList.size() > 0){
                                CacheUtil.getInstance().saveListCache("bigPicResponseList",bigPicResponseList);
                                if(activity instanceof MainActivity){
                                    MainActivity mainActivity = (MainActivity)activity;
                                    if(mainActivity.getSupportFragmentManager().findFragmentByTag(String.valueOf(R.id.footer_tab_rb_home)) != null){
                                        HomeFragment homeFragment = (HomeFragment)mainActivity.getSupportFragmentManager().findFragmentByTag(String.valueOf(R.id.footer_tab_rb_home));
                                        homeFragment.setBigPicAnimation();
                                    }
                                }
                            }
                        }else{
                            Toast.makeText(context,getBigPicResponse.getResDesc(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    Toast.makeText(context,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Toast.makeText(context,"json解析错误",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

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
            String apptype = "1";//(int required) - 应用类型： 1 超级身份 2 超级服务
            String devicetype = "ANDROID";// (String required) - 设备类型：IOS ANDROID
            String verno = currentVersionCode+"";//(String required) - 当前客户端版本号
            String url = ProtocolUtil.newestversion(apptype,devicetype,verno);
            client.get(context,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        GetUpgradeResponse getUpgradeResponse = new Gson().fromJson(response,GetUpgradeResponse.class);
                        if (getUpgradeResponse == null){
                            return;
                        }
                        if(getUpgradeResponse.getRes() == 0){
                            UpdateBean updateBean = getUpgradeResponse.getData();
                            int versionNo = Integer.parseInt(updateBean.getVerno());
                            if(currentVersionCode < versionNo){
                                if(updateBean.getIsforceupgrade() == 0){
                                    showNormalUpdateDialog(updateBean);
                                }else if(updateBean.getIsforceupgrade() == 1){
                                    showForceUpdateDialog(updateBean);
                                }
                            }
                            //showNormalUpdateDialog(updateBean);
                            //showForceUpdateDialog(updateBean);
                        }else{
                            Toast.makeText(context,getUpgradeResponse.getResDesc(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    Toast.makeText(context,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Toast.makeText(context,"json解析错误",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }


    private void showNormalUpdateDialog(final UpdateBean updateBean){
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
                downloadApp(updateBean);
            }
        });
        customerBuilder.create().show();
    }

    private void showForceUpdateDialog(final UpdateBean updateBean){
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
                downloadApp(updateBean);
            }
        });
        dialog = customBuilder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    public void downloadApp( UpdateBean updateBean){
        Intent intent = new Intent(context, NotificationUpdateActivity.class);
        activity.startActivity(intent);
        app.setDownload(true);
        app.updateBean = updateBean;
    }

    /**
     * 请求蓝牙区域信息
     */
    public void startIbeaconService(){
        String url = ProtocolUtil.getLocationListUrl();
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(context,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(context) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                String listStr =  CacheUtil.getInstance().getListStrCache("NetBeanconList");
                if(!TextUtils.isEmpty(listStr)){
                    Type listType = new TypeToken<ArrayList<NetBeaconVo>>(){}.getType();
                    Gson gson = new Gson();
                    ArrayList<NetBeaconVo> beaconList = gson.fromJson(listStr, listType);
                    if (null != beaconList && !beaconList.isEmpty()) {
                        if(CacheUtil.getInstance().isLogin()){
                            if(activity instanceof MainActivity){
                                BlueToothManager.getInstance().startIBeaconService(beaconList);
                            }
                        }
                    }
                }
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    Type listType = new TypeToken<ArrayList<NetBeaconVo>>(){}.getType();
                    Gson gson = new Gson();
                    ArrayList<NetBeaconVo> beaconList = gson.fromJson( result.rawResult, listType);
                    if(null != beaconList && !beaconList.isEmpty()) {
                        if(CacheUtil.getInstance().isLogin()){
                            if(activity instanceof MainActivity){
                                IBeaconController.getInstance().setListenBeancons(beaconList);
                                IBeaconController.getInstance().startBeaconService();
                            }
                        }
                        CacheUtil.getInstance().saveListCache("NetBeanconList", beaconList);
                    }
                } catch (Exception e) {
                   e.printStackTrace();
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }
    
}
