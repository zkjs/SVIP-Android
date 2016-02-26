package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.google.gson.Gson;

import com.umeng.analytics.MobclickAgent;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.R;

import com.zkjinshi.svip.activity.order.HotelBookingActivity;
import com.zkjinshi.svip.activity.order.KTVBookingActivity;
import com.zkjinshi.svip.activity.order.NormalBookingActivity;
import com.zkjinshi.svip.emchat.EMConversationHelper;
import com.zkjinshi.svip.emchat.EasemobIMHelper;
import com.zkjinshi.svip.emchat.observer.EMessageListener;
import com.zkjinshi.svip.factory.UserInfoFactory;

import com.zkjinshi.svip.manager.YunBaSubscribeManager;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.UserInfoResponse;
import com.zkjinshi.svip.sqlite.UserDetailDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.StringUtil;
import com.zkjinshi.svip.vo.UserDetailVo;
import com.zkjinshi.svip.vo.UserInfoVo;

/**
 * 开发者：dujiande
 * 日期：2015/9/17
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LoginController {

    private final static String TAG = LoginController.class.getSimpleName();

    private LoginController(){}
    private static LoginController instance;
    private Context context;
    private Activity activity;

    public static synchronized LoginController getInstance(){
        if(null ==  instance){
            instance = new LoginController();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
        this.activity = (Activity)context;
    }

    /**
     * 获取用户详细信息
     * @param userid
     * @param token
     * @param isNewRegister 是否是新注册用户
     */
    public void getUserDetailInfo(final String userid, String token,final boolean isNewRegister,final boolean isHomeBack,final Bundle thirdBundleData) {

        String url =  Constants.GET_USER_DETAIL_URL + "userid=" + userid + "&token=" + token;
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(activity,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(activity) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(LoginController.class.getSimpleName(), "errorCode:" + errorCode);
                Log.i(LoginController.class.getSimpleName(), "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(LoginController.class.getSimpleName(), "result.rawResult:" + result.rawResult);
                try {
                    Gson gson = new Gson();
                    UserInfoResponse userInfoResponse =  gson.fromJson(result.rawResult, UserInfoResponse.class);
                    if(null != userInfoResponse && userInfoResponse.isSet()){
                        //存数据库
                        UserDetailVo userDetailVo = gson.fromJson(result.rawResult, UserDetailVo.class);
                        UserDetailDBUtil.getInstance().addUserDetail(userDetailVo);
                        //存缓存
                        UserInfoVo userInfoVo = UserInfoFactory.getInstance().buildUserInfoVo(userInfoResponse);
                        if(null != userInfoVo){
                            CacheUtil.getInstance().setUserId(userInfoVo.getUserid());
                            CacheUtil.getInstance().saveTagsOpen(userInfoVo.isTagopen());
                            CacheUtil.getInstance().setUserPhone(userInfoVo.getMobilePhoto());
                            CacheUtil.getInstance().setUserName(userInfoVo.getUsername());
                            CacheUtil.getInstance().setUserRealName(userInfoVo.getRealName());
                            CacheUtil.getInstance().setUserApplevel(userDetailVo.getUser_applevel());
                            CacheUtil.getInstance().setSex(userDetailVo.getSex());
                            CacheUtil.getInstance().setActivate(userInfoResponse.isActivated());
                            YunBaSubscribeManager.getInstance().setAlias(context,CacheUtil.getInstance().getUserId());
                        }
                        loginHxUser();
                        MobclickAgent.onProfileSignIn(userInfoVo.getUserid());
                        //判读是否新注册用户
                        if(isNewRegister){
                            Intent intent = new Intent(activity, CompleteInfoActivity.class);
                            if(thirdBundleData != null){
                                intent.putExtra("from_third", true);
                                intent.putExtras(thirdBundleData);
                            }
                            activity.startActivity(intent);
                            activity.finish();
                        } else {
                            if(isHomeBack){
                                Intent intent = activity.getIntent();
                                String category = intent.getStringExtra("category");
                                /**
                                 行业： 酒店行业  50   KTV休闲  60  其他行业  70，在商家列表及详情中，yo那个后面的数字判断行业
                                 */
                                if(!StringUtil.isEmpty(category)){
                                    if("50".equals(category)){
                                        intent.setClass(activity, HotelBookingActivity.class);
                                    }else if("60".equals(category)){
                                        intent.setClass(activity, KTVBookingActivity.class);
                                    }else {
                                        intent.setClass(activity, NormalBookingActivity.class);
                                    }
                                    activity.startActivity(intent);
                                }
                                activity.finish();


                            }else {
                                goHome();
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
     * 登录环信IM
     */
    private void loginHxUser(){
        EasemobIMHelper.getInstance().loginUser(CacheUtil.getInstance().getUserId(), "123456", new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "环信登录成功");
                EMGroupManager.getInstance().loadAllGroups();
                EMChatManager.getInstance().loadAllConversations();
                EMessageListener.getInstance().registerEventListener();
                EMConversationHelper.getInstance().requestGroupListTask();
                EMChatManager.getInstance().updateCurrentUserNick(CacheUtil.getInstance().getUserName());
            }

            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "环信登录失败-errorCode:" + i);
                Log.i(TAG, "环信登录失败-errorMessage:" + s);
                LogUtil.getInstance().info(LogLevel.ERROR,"环信登录失败-errorCode:" + i);
                LogUtil.getInstance().info(LogLevel.ERROR,"环信登录失败-errorMessage:" + s);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    private void goHome() {
        Intent mainIntent = new Intent(activity, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(mainIntent);
        activity.finish();
        activity.overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
    }
}
