package com.zkjinshi.ritz.activity.common;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.ritz.utils.AsyncHttpClientUtil;
import com.zkjinshi.ritz.utils.CacheUtil;
import com.zkjinshi.ritz.utils.ProtocolUtil;

import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;

/**
 * 个人设置网络请求控制器
 * 开发者：dujiande
 * 日期：2016/4/19
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MineNetController {

    private static MineNetController instance;

    public interface CallBackListener{
        public void successCallback(JSONObject response);
        public void failCallBack();
    }

    private MineNetController(){}
    public synchronized static MineNetController getInstance(){
        if(null == instance){
            instance = new MineNetController();
        }
        return instance;
    }

    //提交资料
    public void submitInfo(final Context context,final String fieldKey, final String fieldValue, final CallBackListener callBackListener){
        try{
            String url = ProtocolUtil.loginUpdateSi();
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(5000);
            String token = CacheUtil.getInstance().getExtToken();
            client.addHeader("Token", token);
            RequestParams params = new RequestParams();
            if(fieldKey.equals("image")){
                params.put(fieldKey,new File(fieldValue));
            }else{
                params.put(fieldKey,fieldValue);
            }

            client.post(url, params, new JsonHttpResponseHandler() {

                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(context,"");
                }
                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                    super.onSuccess(statusCode,headers,response);
                    try{
                        if(response.getInt("res") == 0){//更新成功
                            JSONObject dataJson = response.getJSONObject("data");
                            if(callBackListener != null){
                                callBackListener.successCallback(dataJson);
                            }
                        }else{
                            DialogUtil.getInstance().showCustomToast(context, response.getString("resDesc") , Toast.LENGTH_LONG);
                            if(callBackListener != null){
                                callBackListener.failCallBack();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                    super.onFailure(statusCode,headers,throwable,errorResponse);
                    DialogUtil.getInstance().showCustomToast(context, "修改用户资料失败!", Toast.LENGTH_LONG);
                    AsyncHttpClientUtil.onFailure(context,statusCode);
                    if(callBackListener != null){
                        callBackListener.failCallBack();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
