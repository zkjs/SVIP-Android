package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;


import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;

import com.tencent.mm.sdk.openapi.WXAPIFactory;

import com.zkjinshi.svip.utils.Constants;

import com.zkjinshi.svip.wxapi.WXEntryActivity;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * 开发者：dujiande
 * 日期：2015/9/17
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class WXLoginController {

    private final static String TAG = WXLoginController.class.getSimpleName();

    private WXLoginController(){}
    private static WXLoginController instance;
    private Context context;
    private Activity activity;

    public  IWXAPI WXapi;
    private String weixinCode;
    private final static int LOGIN_WHAT_INIT = 1;
    private static String get_access_token = "";
    // 获取第一步的code后，请求以下链接获取access_token
    public static String GetCodeRequest = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    //获取用户个人信息
    public static String GetUserInfo="https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";

    Bundle thirdBundleData;

    public static synchronized WXLoginController getInstance(){
        if(null ==  instance){
            instance = new WXLoginController();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
        this.activity = (Activity)context;
    }

    public void callback(){
        if (WXEntryActivity.resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            // code返回
            weixinCode = ((SendAuth.Resp)WXEntryActivity.resp).code;
			/*
			 * 将你前面得到的AppID、AppSecret、code，拼接成URL
			 */
            get_access_token = getCodeRequest(weixinCode);
            Thread thread=new Thread(downloadRun);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 登录微信
     */
    public void WXLogin() {
        WXapi = WXAPIFactory.createWXAPI(activity, Constants.WX_APP_ID, true);
        WXapi.registerApp( Constants.WX_APP_ID);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo";
        WXapi.sendReq(req);

    }

    /**
     * 获取access_token的URL（微信）
     * @param code 授权时，微信回调给的
     * @return URL
     */
    public static String getCodeRequest(String code) {
        String result = null;
        GetCodeRequest = GetCodeRequest.replace("APPID",
                urlEnodeUTF8(Constants.WX_APP_ID));
        GetCodeRequest = GetCodeRequest.replace("SECRET",
                urlEnodeUTF8(Constants.WX_APP_SECRE));
        GetCodeRequest = GetCodeRequest.replace("CODE",urlEnodeUTF8( code));
        result = GetCodeRequest;
        return result;
    }

    /**
     * 获取用户个人信息的URL（微信）
     * @param access_token 获取access_token时给的
     * @param openid 获取access_token时给的
     * @return URL
     */
    public static String getUserInfo(String access_token,String openid){
        String result = null;
        GetUserInfo = GetUserInfo.replace("ACCESS_TOKEN",
                urlEnodeUTF8(access_token));
        GetUserInfo = GetUserInfo.replace("OPENID",
                urlEnodeUTF8(openid));
        result = GetUserInfo;
        return result;
    }

    public static String urlEnodeUTF8(String str) {
        String result = str;
        try {
            result = URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public  Runnable downloadRun = new Runnable() {

        @Override
        public void run() {
            WXGetAccessToken();

        }
    };

    /**
     * 获取access_token等等的信息(微信)
     */
    private  void WXGetAccessToken(){
        HttpClient get_access_token_httpClient = new DefaultHttpClient();
        HttpClient get_user_info_httpClient = new DefaultHttpClient();
        String access_token="";
        String openid ="";
        try {
            HttpPost postMethod = new HttpPost(get_access_token);
            HttpResponse response = get_access_token_httpClient.execute(postMethod); // 执行POST方法
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream is = response.getEntity().getContent();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                String str = "";
                StringBuffer sb = new StringBuffer();
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }
                is.close();
                String josn = sb.toString();
                JSONObject json1 = new JSONObject(josn);
                access_token = (String) json1.get("access_token");
                openid = (String) json1.get("openid");


            } else {
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String get_user_info_url=getUserInfo(access_token,openid);
        WXGetUserInfo(get_user_info_url);
    }

    /**
     * 获取微信用户个人信息
     * @param get_user_info_url 调用URL
     */
    private  void WXGetUserInfo(String get_user_info_url){
        HttpClient get_access_token_httpClient = new DefaultHttpClient();
        String openid="";
        String nickname="";
        String headimgurl="";
        try {
            HttpGet getMethod = new HttpGet(get_user_info_url);
            HttpResponse response = get_access_token_httpClient.execute(getMethod); // 执行GET方法
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream is = response.getEntity().getContent();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                String str = "";
                StringBuffer sb = new StringBuffer();
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }
                is.close();
                String josn = sb.toString();
                JSONObject json1 = new JSONObject(josn);
                openid = (String) json1.get("openid");
                nickname = (String) json1.get("nickname");
                headimgurl=(String)json1.get("headimgurl");

                thirdBundleData = new Bundle();
                thirdBundleData.putString("openid",openid);
                thirdBundleData.putString("nickname",nickname);
                thirdBundleData.putString("headimgurl",headimgurl);
                thirdBundleData.putString("sex",(String)json1.get("sex"));

                if(activity instanceof LoginActivity){

                    ((LoginActivity)activity).getUser(openid);
                    ((LoginActivity)activity).thirdBundleData = thirdBundleData;
                }

            } else {
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
