package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.bean.BaseBean;
import com.zkjinshi.svip.fragment.HomeFragment;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.BigPicResponse;
import com.zkjinshi.svip.sqlite.ServerPeronalDBUtil;
import com.zkjinshi.svip.sqlite.ShopDetailDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.ServerPersonalVo;
import com.zkjinshi.svip.vo.ShopDetailVo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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

    public static synchronized  MainController getInstance(){
        if(null ==  instance){
            instance = new  MainController();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
        this.activity = (Activity)context;
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_main_user_default_photo_nor)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.ic_main_user_default_photo_nor)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_main_user_default_photo_nor)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
        this.bigOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }

    /**
     * 设置首页动态图
     */
    public void setBigPicAnimation(ImageView homePicIv){
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
        int number = new Random().nextInt(length);
        if(number == bigPicIndex){
            bigPicIndex = (bigPicIndex+1)%length;
        }else{
            bigPicIndex = number;
        }
        String bigPicUrl = bigPicResponseList.get(bigPicIndex).getUrl();
        ImageLoader.getInstance().displayImage(bigPicUrl, homePicIv,bigOptions);

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

    /**
     * 初始化专属客服列表信息
     */
//    public void initServerPersonal(){
//        String url = ProtocolUtil.getUserMysemp();
//        Log.i(TAG, url);
//        NetRequest netRequest = new NetRequest(url);
//        HashMap<String,String> bizMap = new HashMap<String,String>();
//        bizMap.put("userid", CacheUtil.getInstance().getUserId());
//        bizMap.put("token", CacheUtil.getInstance().getToken());
//        netRequest.setBizParamMap(bizMap);
//        NetRequestTask netRequestTask = new NetRequestTask(activity,netRequest, NetResponse.class);
//        netRequestTask.methodType = MethodType.PUSH;
//        netRequestTask.setNetRequestListener(new ExtNetRequestListener(activity) {
//            @Override
//            public void onNetworkRequestError(int errorCode, String errorMessage) {
//                Log.i(TAG, "errorCode:" + errorCode);
//                Log.i(TAG, "errorMessage:" + errorMessage);
//            }
//
//            @Override
//            public void onNetworkRequestCancelled() {
//
//            }
//
//            @Override
//            public void onNetworkResponseSucceed(NetResponse result) {
//                super.onNetworkResponseSucceed(result);
//                Log.i(TAG, "result.rawResult:" + result.rawResult);
//                try {
//                    Type listType = new TypeToken<List<ServerPersonalVo>>() {
//                    }.getType();
//                    Gson gson = new Gson();
//                    List<ServerPersonalVo> serverPersonalVoList = gson.fromJson(result.rawResult, listType);
//                    TextView activateTv = (TextView)activity.findViewById(R.id.activate_tv);
//                    if (serverPersonalVoList != null && serverPersonalVoList.size() > 0) {
//                        CacheUtil.getInstance().setActivate(true);
//                        activateTv.setText("（已激活）");
//                        activity.findViewById(R.id.tips_layout).setVisibility(View.GONE);
//                        ServerPeronalDBUtil.getInstance().batchAddServerPernal(serverPersonalVoList);
//                    } else {
//                        activateTv.setText("（未激活）");
//                        activity.findViewById(R.id.tips_layout).setVisibility(View.VISIBLE);
//                        CacheUtil.getInstance().setActivate(false);
//                    }
//
//
//                } catch (Exception e) {
//                    Log.e(TAG, e.getMessage());
//                }
//
//            }
//
//            @Override
//            public void beforeNetworkRequestStart() {
//
//            }
//        });
//        netRequestTask.isShowLoadingDialog = true;
//        netRequestTask.execute();
//    }
    
}
