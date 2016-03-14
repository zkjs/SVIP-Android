package com.zkjinshi.svip.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.BaseFornaxResponse;
import com.zkjinshi.svip.response.BaseResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * 个人设置网络请求控制器
 * 开发者：JimmyZhang
 * 日期：2015/7/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MineNetController {

    private static MineNetController instance;
    private Context context;

    public interface CallBackListener{
        public void successCallback(JSONObject response);
    }

    private MineNetController(){}
    public synchronized static MineNetController getInstance(){
        if(null == instance){
            instance = new MineNetController();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
    }

    public void requestSetInfoTask(NetRequest request, ExtNetRequestListener requestListener){
        NetRequestTask httpAsyncTask = new NetRequestTask(context, request, NetResponse.class);
        httpAsyncTask.methodType = MethodType.PUSH;
        httpAsyncTask.setNetRequestListener(requestListener);
        httpAsyncTask.isShowLoadingDialog = true;
        httpAsyncTask.execute();
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
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                    super.onFailure(statusCode,headers,throwable,errorResponse);
                    DialogUtil.getInstance().showCustomToast(context, "修改用户资料失败!", Toast.LENGTH_LONG);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
