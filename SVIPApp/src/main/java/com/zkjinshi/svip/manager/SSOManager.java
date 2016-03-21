package com.zkjinshi.svip.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.Constants;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;

import com.zkjinshi.svip.net.RequestUtil;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.Base64Decoder;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.BaseResponseVo;
import com.zkjinshi.svip.vo.PayloadVo;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 开发者：JimmyZhang
 * 日期：2016/2/26
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class SSOManager {

    public static final String TAG = SSOManager.class.getSimpleName();

    private SSOManager(){}

    private static SSOManager instance;

    public synchronized static SSOManager getInstance(){
        if(null == instance){
            instance = new SSOManager();
        }
        return instance;
    }

    /**
     * 获取token的负载信息
     * @param tokenStr
     * @return
     */
    public PayloadVo decodeToken(String tokenStr){
        //String tokenStr = "eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiJjXzU2YTZlN2I1NjM0OGMiLCJ0eXBlIjozLCJleHBpcmUiOjE0NTY1NjE5NDI4MzcsInNob3BpZCI6Ijg4ODgiLCJyb2xlcyI6IltdIiwiZmVhdHVyZSI6IltdIn0.gPC-fUdKc-2gLGNvee6J9ZGVLXSJ96iVzZN47MsmO0z3PyQ4BMOq6CxVgIvFKyjeZx1Va_D8wphMSXByK8ppQtcQhPBv-q3CIFby8ttdE3y0yw6RXGrZnJwwusePPXBCAgXG80DtmWPjnjFRS5PVDpB3Ls3RQWPs5bSVTM0HkQ8";
        String[] tokenArr = tokenStr.split("\\.");
        PayloadVo payloadVo = null;
        if(null != tokenArr && tokenArr.length > 0){
            String payloadEncode = tokenArr[1];
            String payloadDecode = Base64Decoder.decode(payloadEncode);
            payloadVo = new Gson().fromJson(payloadDecode,PayloadVo.class);
        }
        return payloadVo;
    }




    public interface SSOCallBack{
        public void onNetworkResponseSucceed();
    }


    public void refreshToken(final Context context,final SSOCallBack ssoCallBack){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout( com.zkjinshi.svip.utils.Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            if(CacheUtil.getInstance().isLogin()){
                client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            }
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getTokenRefreshUrl();
            client.put(context,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    //DialogUtil.getInstance().showAvatarProgressDialog(context,"");
                }

                public void onFinish(){
                    //DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        BaseResponseVo basePavoResponse = new Gson().fromJson(response,BaseResponseVo.class);
                        if(null != basePavoResponse){
                            int restult = basePavoResponse.getRes();
                            if(0 == restult){
                                String token = basePavoResponse.getToken();
                                if(!TextUtils.isEmpty(token)){
                                    CacheUtil.getInstance().setExtToken(token);
                                    if(null != ssoCallBack){
                                        ssoCallBack.onNetworkResponseSucceed();
                                    }
                                }
                            }else{
                                String errorMsg = basePavoResponse.getResDesc();
                                if(!TextUtils.isEmpty(errorMsg)){
                                    DialogUtil.getInstance().showCustomToast(context,errorMsg, Gravity.CENTER);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    Toast.makeText(context,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
                    AsyncHttpClientUtil.onFailure( context,statusCode);
                }
            });
        }catch (Exception e){
            Toast.makeText(context,"json解析错误",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}
