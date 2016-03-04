package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.google.gson.Gson;

import com.loopj.android.http.JsonHttpResponseHandler;
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
import com.zkjinshi.svip.net.SvipHttpClient;
import com.zkjinshi.svip.response.UserInfoResponse;
import com.zkjinshi.svip.sqlite.UserDetailDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.StringUtil;
import com.zkjinshi.svip.vo.UserDetailVo;
import com.zkjinshi.svip.vo.UserInfoVo;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

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

        String url = ProtocolUtil.querySiAll();
        JSONObject jsonObject = new JSONObject();
        SvipHttpClient.get(context,url, jsonObject, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    int res = response.getInt("res");
                    if(res == 0){
                        JSONObject data = response.getJSONObject("data");
                        CacheUtil.getInstance().setUserId(data.getString("userid"));
                        CacheUtil.getInstance().setUserPhone(data.getString("phone"));
                        CacheUtil.getInstance().setUserName(data.getString("username"));
                        CacheUtil.getInstance().setUserRealName(data.getString("realname"));
                        CacheUtil.getInstance().setUserApplevel(data.getString("viplevel"));
                        CacheUtil.getInstance().setSex(data.getString("sex"));
                        CacheUtil.getInstance().setActivate(data.getInt("userstatus")==0? false : true);
                        YunBaSubscribeManager.getInstance().setAlias(context,CacheUtil.getInstance().getUserId());

                        loginHxUser();
                        MobclickAgent.onProfileSignIn(data.getString("userid"));

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

                    }else{
                        Toast.makeText(context,response.getString("resDesc"),Toast.LENGTH_SHORT).show();
                        if(activity instanceof LoginActivity){

                        }else{
                            Intent intent = new Intent(activity,LoginActivity.class);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
               if(null != errorResponse){
                   Log.d(TAG,errorResponse.toString());
               }
            }

        });
    }

    /**
     * 登录环信IM
     */
    public void loginHxUser(){
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
