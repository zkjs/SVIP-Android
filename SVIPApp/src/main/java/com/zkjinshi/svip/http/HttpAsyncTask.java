package com.zkjinshi.svip.http;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.vo.MimeType;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class HttpAsyncTask extends AsyncTask<HttpRequest, Void, HttpResponse> {

    private HttpRequest mRequest;
    private String requestUrl;
    private String requestMethod;
    private MimeType mimeType;
    private Map<String,String> stringParamMap;
    private Map<String,File> fileParamMap;

    public boolean isShowLoadingDialog = true;

    private StringBuffer mErrorLog = new StringBuffer();

    private int errorCode = REQ_RESP_SUCCESS;

    private HttpRequestListener<HttpResponse> mRequestListener;
    private Context mContext;

    private static final int REQ_RESP_SUCCESS = 200;

    /** 没有网络连接 */
    public static final int NO_NETWORK = -0x000001;

    /** 服务器连接失败 */
    public static final int CONNECTION_SERVER_ERROR = -0x000002;

    /** 请求参数错误 */
    public static final int PARAMETER_ERROR = -0x000003;

    /** 解析json错误 */
    public static final int PARSE_JSON_ERROR = -0x000004;

    /** http返回信息错误 */
    public static final int RESPONSE_ERROR = -0x000005;

    /** 不存在服务器 */
    public static final int HOST_ERROR = -0x000006;

    /** 解析二进制流错误 */
    public static final int PARSE_STREAM_ERROR = -0x000007;

    /** 解析二进制流错误 */
    public static final int URL_NOT_FOUND = -0x000008;

    /** 请求超时 */
    public static final int REQ_TIME_OUT = -0x000009;

    /** 未知错误 */
    public static final int UNKNOW_ERROR = -0xFFFFFF;

    public HttpAsyncTask(Context ctx, HttpRequest requestInfo) {
        mRequest = requestInfo;
        mContext = ctx;
    }

    public void setHttpRequestListener(HttpRequestListener<HttpResponse> listener) {
        this.mRequestListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(isShowLoadingDialog){
            DialogUtil.getInstance().showProgressDialog(mContext);
        }
    }

    @Override
    protected HttpResponse doInBackground(HttpRequest... params) {
        HttpResponse resultInfo = null;
        String resultString = null;
        requestUrl = mRequest.getRequestUrl();
        requestMethod = mRequest.getRequestMethod();
        stringParamMap = mRequest.getStringParamMap();
        fileParamMap = mRequest.getFileParamMap();
        Gson gson = new Gson();
        try {
            if(null == mimeType){
                resultString = HttpPostUtil.post(requestUrl+requestMethod,stringParamMap, fileParamMap);
            }else{

            }
            LogUtil.getInstance().info(LogLevel.INFO,"result:"+resultString);
        } catch (UnsupportedEncodingException e) {
            errorCode = RESPONSE_ERROR;
            onExceptionThrown(e, "Encoding Unsupported when requesting: " + mRequest.getRequestUrl());
        } catch (ClientProtocolException e) {
            errorCode = RESPONSE_ERROR;
            onExceptionThrown(e, "ClientProtocolException thrown when requesting: " +  mRequest.getRequestUrl());
        } catch (ConnectTimeoutException ioe) {
            errorCode = REQ_TIME_OUT;
        } catch (SocketTimeoutException se) {
            errorCode = REQ_TIME_OUT;
        } catch (IOException e) {
            errorCode = RESPONSE_ERROR;
            onExceptionThrown(e, "IOException thrown when requesting: " +  mRequest.getRequestUrl());
        } catch (Exception e) {
            errorCode = RESPONSE_ERROR;
            onExceptionThrown(e, "Exception thrown when getResultString from the response.");
        }
        if (errorCode == REQ_RESP_SUCCESS) {
            if (!TextUtils.isEmpty(resultString)) {
                try {
                    resultInfo = new HttpResponse();
                    if (resultInfo != null) {
                        resultInfo.rawResult = resultString;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }else{
            resultInfo = null;
        }

        return resultInfo;
    }

    @Override
    protected void onPostExecute(HttpResponse result) {
        super.onPostExecute(result);
        if (isShowLoadingDialog) {
            DialogUtil.getInstance().cancelProgressDialog();
        }
        if(null != mRequestListener){
            if(null != result){
                this.mRequestListener.onNetworkResponseSucceed(result);
            }else {
                this.mRequestListener.onNetworkRequestError(errorCode, mErrorLog.toString());
            }
        }
    }

    private void onExceptionThrown(Exception e, String message) {
        e.printStackTrace();
        mErrorLog.append(message + "\r\n");
    }

}
