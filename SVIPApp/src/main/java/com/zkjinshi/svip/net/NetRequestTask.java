package com.zkjinshi.svip.net;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.HashMap;

/**
 * 网络请求异步任务
 * 开发者：JimmyZhang
 * 日期：2015/9/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class NetRequestTask extends AsyncTask<NetRequest, Void, NetResponse> {

    /** http返回成功 */
    private static final int REQ_RESP_SUCCESS = 200;

    /** http返回信息错误 */
    public static final int RESPONSE_ERROR = -0x000005;

    /** 请求超时 */
    public static final int REQ_TIME_OUT = -0x000009;

    private NetRequest mediaRequest;
    private Class<NetResponse> responseClazz;

    private NetRequestListener requestListener;
    private Context context;

    private StringBuffer errorLog = new StringBuffer();

    private int errorCode = REQ_RESP_SUCCESS;

    public boolean isShowLoadingDialog = false;

    public SchemeType schemeType = SchemeType.HTTP;

    public MethodType methodType = MethodType.POST;

    public NetRequestTask(Context ctx, NetRequest requestInfo, Class<NetResponse> responseClazz) {
        mediaRequest = requestInfo;
        this.responseClazz = responseClazz;
        context = ctx;
    }

    public void setNetRequestListener(NetRequestListener listener) {
        this.requestListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (this.requestListener != null) {
            this.requestListener.beforeNetworkRequestStart();
        }

        if (isShowLoadingDialog) {
            DialogUtil.getInstance().showProgressDialog(context);
        }
    }

    @Override
    protected NetResponse doInBackground(NetRequest... params) {
        NetResponse resultInfo = null;
        String resultStr = null;
        String requestUrl = null;
        HashMap<String,String> bizParamsMap = null;
        HashMap<String,String> fileParamsMap = null;
        HashMap<String,File> fileMap = null;
        try {
            requestUrl = mediaRequest.requestUrl;
            bizParamsMap = mediaRequest.getBizParamMap();
            fileParamsMap = mediaRequest.getFileParamMap();
            fileMap = mediaRequest.getFileMap();
            if(methodType ==  MethodType.POST){
                resultStr = RequestUtil.sendPostRequest(requestUrl, bizParamsMap, fileParamsMap);
            }else if(methodType == MethodType.GET){
                resultStr = RequestUtil.sendGetRequest(requestUrl);
            }else if(methodType == MethodType.PUSH){
                resultStr = RequestUtil.sendPostRequest(requestUrl,bizParamsMap,fileMap);
            } else if(methodType == MethodType.JSON){
                resultStr = RequestUtil.sendPostRequest(requestUrl,bizParamsMap);
            }
            if(!TextUtils.isEmpty(resultStr)){
                resultInfo = responseClazz.newInstance();
                if (resultInfo != null) {
                    resultInfo.rawResult = resultStr;
                }
            }
        } catch (UnsupportedEncodingException e) {
            errorCode = RESPONSE_ERROR;
            onExceptionThrown(e, "Encoding Unsupported when requesting: " + mediaRequest.requestUrl);
        } catch (ClientProtocolException e) {
            errorCode = RESPONSE_ERROR;
            onExceptionThrown(e, "ClientProtocolException thrown when requesting: " + mediaRequest.requestUrl);
        } catch (ConnectTimeoutException ioe) {
            errorCode = REQ_TIME_OUT;
        } catch (SocketTimeoutException se) {
            errorCode = REQ_TIME_OUT;
        } catch (IOException e) {
            errorCode = RESPONSE_ERROR;
            onExceptionThrown(e, "IOException thrown when requesting: " + mediaRequest.requestUrl);
        } catch (Exception e) {
            errorCode = RESPONSE_ERROR;
            onExceptionThrown(e, "Exception thrown when getResultString from the response.");
        }
        return resultInfo;
    }

    @Override
    protected void onPostExecute(NetResponse result) {
        super.onPostExecute(result);

        if (isShowLoadingDialog) {
            DialogUtil.getInstance().cancelProgressDialog();
        }
        if (this.requestListener != null) {
            if (errorCode == REQ_RESP_SUCCESS) {
                if (result != null &&  !TextUtils.isEmpty(result.rawResult)) {
                    this.requestListener.onNetworkResponseSucceed(result);
                } else {
                    errorLog.append(context.getString(R.string.no_data));
                    this.requestListener.onNetworkRequestError(RESPONSE_ERROR, errorLog.toString());
                }
            } else {
                this.requestListener.onNetworkRequestError(errorCode, errorLog.toString());
                if(errorCode == RESPONSE_ERROR){
                    Toast.makeText(context.getApplicationContext(),"返回数据异常或者无网络",Toast.LENGTH_SHORT).show();
                }else if(errorCode == REQ_TIME_OUT){
                    Toast.makeText(context.getApplicationContext(),"网络异常，请检查网络",Toast.LENGTH_SHORT).show();
                }
                cancelTask();
            }
        }
    }

    private void onExceptionThrown(Exception e, String message) {
        e.printStackTrace();
        errorLog.append(message + "\r\n");
    }

    public void cancelTask(){
        if(getStatus() != AsyncTask.Status.FINISHED){
            cancel(true);
        }
        onCancelled();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (requestListener != null) {
            requestListener.onNetworkRequestCancelled();
        }
        if (isShowLoadingDialog) {
           DialogUtil.getInstance().cancelProgressDialog();
        }
    }


}
