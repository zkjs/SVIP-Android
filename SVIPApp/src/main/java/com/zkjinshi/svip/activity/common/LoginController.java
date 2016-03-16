package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.order.HotelBookingActivity;
import com.zkjinshi.svip.activity.order.KTVBookingActivity;
import com.zkjinshi.svip.activity.order.NormalBookingActivity;
import com.zkjinshi.svip.emchat.EasemobIMManager;
import com.zkjinshi.svip.manager.YunBaSubscribeManager;
import com.zkjinshi.svip.net.SvipHttpClient;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

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
                        JSONArray dataArr = response.getJSONArray("data");
                        if(dataArr != null && dataArr.length()> 0){
                            JSONObject data = dataArr.getJSONObject(0);
                            CacheUtil.getInstance().setUserId(data.getString("userid"));
                            CacheUtil.getInstance().setUserPhone(data.getString("phone"));
                            CacheUtil.getInstance().setUserName(data.getString("username"));
                            CacheUtil.getInstance().setUserRealName(data.getString("realname"));
                            CacheUtil.getInstance().setUserApplevel(data.getString("viplevel"));
                            CacheUtil.getInstance().setSex(data.getString("sex"));
                            CacheUtil.getInstance().setActivate(data.getInt("userstatus")==0? false : true);
                            String imgurl = data.getString("userimage");
                            imgurl = ProtocolUtil.getHostImgUrl(imgurl);
                            CacheUtil.getInstance().saveUserPhotoUrl(imgurl);
                            //订阅云巴区域
                            YunBaSubscribeManager.getInstance().setAlias(context,CacheUtil.getInstance().getUserId());
                            //登陆环信
                            EasemobIMManager.getInstance().loginHxUser();
                            //增加友盟统计
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
                                    int category = intent.getIntExtra("category",0);
                                    if(category > 0){
                                        /**
                                         行业： 酒店行业  50   KTV休闲  60  其他行业  70，在商家列表及详情中，yo那个后面的数字判断行业
                                         */
                                        if(50 == category){
                                            intent.setClass(activity, HotelBookingActivity.class);
                                        }else if(60 == category){
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

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                Log.d(TAG,"API 错误："+statusCode);

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

    public interface CallBackListener{
        public void successCallback(JSONObject response);
    }

    /**
     * 获取用户资料-批量，所有用户
     * @param mContext
     * @param userids
     * @param callBackListener
     */
    public void getUserInfo(final Context mContext,final String userids,final CallBackListener callBackListener){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userids",userids);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.querySiAll();
            client.get(mContext,url, stringEntity, "application/json", new JsonHttpResponseHandler(){
                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                    super.onSuccess(statusCode,headers,response);
                    try {
                        if(response.getInt("res") == 0){
                            JSONArray dataArr = response.getJSONArray("data");
                            if(dataArr != null && dataArr.length() > 0){
                                JSONObject dataJson = dataArr.getJSONObject(0);
                                CacheUtil.getInstance().setUserPhone(dataJson.getString("phone"));
                                CacheUtil.getInstance().setUserName(dataJson.getString("username"));
                                String imgurl = dataJson.getString("userimage");
                                imgurl = ProtocolUtil.getHostImgUrl(imgurl);
                                CacheUtil.getInstance().saveUserPhotoUrl(imgurl);
                                CacheUtil.getInstance().setSex(dataJson.getString("sex"));
                                CacheUtil.getInstance().setLogin(true);
                                if(callBackListener != null){
                                    callBackListener.successCallback(dataJson);
                                }
                            }

                        }else{
                            Toast.makeText(mContext,response.getString("resDesc"),Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                    super.onFailure(statusCode,headers,throwable,errorResponse);
                    Toast.makeText(mContext,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
