package com.zkjinshi.svip.activity.common;


import android.app.Activity;
import android.content.Context;

import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import com.zkjinshi.base.util.DialogUtil;

import com.zkjinshi.svip.manager.YunBaSubscribeManager;

import com.zkjinshi.svip.net.RequestUtil;
import com.zkjinshi.svip.sqlite.DBOpenHelper;
import com.zkjinshi.svip.utils.AESUtil;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.PavoUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.BaseResponseVo;
import com.zkjinshi.svip.vo.GetUserInfoVo;
import com.zkjinshi.svip.vo.UserInfoVo;


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

    public static synchronized LoginController getInstance(){
        if(null ==  instance){
            instance = new LoginController();
        }
        return instance;
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
            client.get(mContext,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        GetUserInfoVo getUserInfoVo = new Gson().fromJson(response,GetUserInfoVo.class);
                        if (getUserInfoVo == null){
                            return;
                        }
                        if(getUserInfoVo.getRes() == 0){
                            if(getUserInfoVo.getData() != null && getUserInfoVo.getData().size() > 0){
                                UserInfoVo userInfoVo = getUserInfoVo.getData().get(0);
                                CacheUtil.getInstance().setUserPhone(userInfoVo.getPhone());
                                CacheUtil.getInstance().setUserName(userInfoVo.getUsername());
                                String imgurl = userInfoVo.getUserimage();
                                imgurl = ProtocolUtil.getHostImgUrl(imgurl);
                                CacheUtil.getInstance().saveUserPhotoUrl(imgurl);
                                CacheUtil.getInstance().setSex(userInfoVo.getSex()+"");
                                CacheUtil.getInstance().setUserRealName(userInfoVo.getRealname());
                                CacheUtil.getInstance().setUserApplevel(userInfoVo.getViplevel());
                                //订阅云巴区域
                                YunBaSubscribeManager.getInstance().setAlias(mContext,CacheUtil.getInstance().getUserId());

                                String userid = CacheUtil.getInstance().getUserId();
                                DBOpenHelper.DB_NAME = userid +".db";
                                CacheUtil.getInstance().setLogin(true);
                                if(callBackListener != null){
                                    callBackListener.successCallback(null);
                                }
                            }
                        }else{
                            Toast.makeText(mContext,getUserInfoVo.getResDesc(),Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    Toast.makeText(mContext,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });






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
                               // CacheUtil.getInstance().setUserRealName(dataJson.getString("realname"));
                                CacheUtil.getInstance().setUserApplevel(dataJson.getString("viplevel"));
                                //订阅云巴区域
                                YunBaSubscribeManager.getInstance().setAlias(mContext,CacheUtil.getInstance().getUserId());

                                String userid = CacheUtil.getInstance().getUserId();
                                DBOpenHelper.DB_NAME = userid +".db";
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
                    AsyncHttpClientUtil.onFailure((Activity) mContext,statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 向手机发送验证码(登录)
     * @param phoneNumber
     */
    public void sendVerifyCodeForLogin(final Context mContext,final String phoneNumber,final CallBackListener callBackListener) {
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            JSONObject jsonObject = new JSONObject();
            String phoneStr = AESUtil.encrypt(phoneNumber, AESUtil.PAVO_KEY);
            jsonObject.put("phone",phoneStr);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.ssoVcodeLogin();
            client.post(mContext,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        BaseResponseVo basePavoResponse = new Gson().fromJson(response,BaseResponseVo.class);
                        if(basePavoResponse != null){
                            if(basePavoResponse.getRes() == 0){
                                callBackListener.successCallback(null);
                            }else{
                                PavoUtil.showErrorMsg(mContext,basePavoResponse.getResDesc());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    Toast.makeText(mContext,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        }catch (Exception e){
            Toast.makeText(mContext,"json解析错误",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }


    /**
     * 向手机发送验证码(注册)
     * @param phoneNumber
     */
    public void sendVerifyCodeForRegister(final Context mContext,final String phoneNumber,final CallBackListener callBackListener) {
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            JSONObject jsonObject = new JSONObject();
            String phoneStr = AESUtil.encrypt(phoneNumber, AESUtil.PAVO_KEY);
            jsonObject.put("phone",phoneStr);
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.ssoVcodeRegister();
            client.post(mContext,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        BaseResponseVo basePavoResponse = new Gson().fromJson(response,BaseResponseVo.class);
                        if(basePavoResponse != null){
                            if(basePavoResponse.getRes() == 0){
                                callBackListener.successCallback(null);
                            }else{
                                PavoUtil.showErrorMsg(mContext,basePavoResponse.getResDesc());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    Toast.makeText(mContext,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        }catch (Exception e){
            Toast.makeText(mContext,"json解析错误",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

}
