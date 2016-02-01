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
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.pyxis.bluetooth.IBeaconController;
import com.zkjinshi.pyxis.bluetooth.NetBeaconVo;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.bean.BaseBean;
import com.zkjinshi.svip.bean.UpdateBean;
import com.zkjinshi.svip.fragment.HomeFragment;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.BigPicResponse;
import com.zkjinshi.svip.sqlite.ShopDetailDBUtil;
import com.zkjinshi.svip.update.NotificationUpdateActivity;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.ShopDetailVo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 开发者：dujiande
 * 日期：2015/11/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MainController {

    private final static String TAG =  MainController.class.getSimpleName();
    private DisplayImageOptions options;
    private DisplayImageOptions bigOptions;
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
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_main_user_default_photo_nor)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.ic_main_user_default_photo_nor)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_main_user_default_photo_nor)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
                .build();
        this.bigOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.mipmap.header_img_default)// 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.mipmap.header_img_default)// 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.mipmap.header_img_default)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
                .build();
    }

    /**
     * 设置首页动态图
     */
    public void setBigPicAnimation(ImageView homePicIv){
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
            ImageLoader.getInstance().displayImage(bigPicUrl, homePicIv,bigOptions);
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
     * 设置头像图片
     * @param photoUrl
     * @param photoImageView
     */
    public void setPhoto(String photoUrl,ImageView photoImageView){
        if(!TextUtils.isEmpty(photoUrl) && photoImageView != null){
            ImageLoader.getInstance().displayImage(photoUrl, photoImageView, options);
        }

    }



    /**
     * 初始化商家列表信息
     */
    public void initShop(){
        String url = Constants.GET_SHOP_LIST;
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(activity,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(activity) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    Type listType = new TypeToken<List<ShopDetailVo>>(){}.getType();
                    Gson gson = new Gson();
                    List<ShopDetailVo> shopResponseList = gson.fromJson(result.rawResult, listType);
                    ShopDetailDBUtil.getInstance().batchAddShopInfo(shopResponseList);

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();

    }


    /**
     * 初始化首页大图
     */
    public void initBigPic(){
        String url = ProtocolUtil.getBigPicUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(activity,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(activity) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    Type listType = new TypeToken<ArrayList<BigPicResponse>>(){}.getType();
                    Gson gson = new Gson();
                    ArrayList<BigPicResponse> bigPicResponseList = gson.fromJson(result.rawResult, listType);
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

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();

    }



    /**
     * 判读是否已经激活
     */
    public void checktActivate(){
        if(CacheUtil.getInstance().isActivate()){
            return;
        }
        String url = ProtocolUtil.getUserMysemp();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(activity,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(activity) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    BaseBean baseBean = new Gson().fromJson(result.rawResult,BaseBean.class);
                    if (baseBean.isSet()) {
                        CacheUtil.getInstance().setActivate(true);
                    } else {
                        CacheUtil.getInstance().setActivate(false);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }

    public void requestArriveNoticeTask(String shopId,String locId){{
        NetRequest netRequest = new NetRequest(ProtocolUtil.getArriveNoticeUrl());
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid", CacheUtil.getInstance().getUserId());
        bizMap.put("locid", locId);
        bizMap.put("shopid", shopId);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(activity,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.JSON;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(activity) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }}

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

        String url = ProtocolUtil.appUpgradeUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("verno", currentVersionCode+"");
        bizMap.put("devicetype", "andriod");
        bizMap.put("appid", "1"); //(1:超级身份 2 超级服务)
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(activity,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.JSON;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(activity) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    UpdateBean updateBean = new Gson().fromJson(result.rawResult,UpdateBean.class);
                    if(currentVersionCode < updateBean.getVersionNo()){
                        if(updateBean.getIsForceUpgrade() == 0){
                            showNormalUpdateDialog(updateBean);
                        }else if(updateBean.getIsForceUpgrade() == 1){
                            showForceUpdateDialog(updateBean);
                        }
                    }
                    //showNormalUpdateDialog(updateBean);
                    //showForceUpdateDialog(updateBean);

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
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
                                IBeaconController.getInstance().setListenBeancons(beaconList);
                                IBeaconController.getInstance().startBeaconService();
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
