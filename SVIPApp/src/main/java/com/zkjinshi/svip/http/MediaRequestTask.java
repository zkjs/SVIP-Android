package com.zkjinshi.svip.http;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * 媒体请求Task封装类
 * 开发者：JimmyZhang
 * 日期：2015/9/17
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MediaRequestTask extends AsyncTask<MediaRequest, Void, MediaResponse> {

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

    private MediaRequest mediaRequest;
    private Class<MediaResponse> responseClazz;

    private MediaRequestListener requestListener;
    private Context context;

    private StringBuffer errorLog = new StringBuffer();

    private int errorCode = REQ_RESP_SUCCESS;

    public boolean isShowLoadingDialog = false;

    public MediaRequestTask(Context ctx, MediaRequest requestInfo, Class<MediaResponse> responseClazz) {
        mediaRequest = requestInfo;
        this.responseClazz = responseClazz;
        context = ctx;
    }

    public void setMediaRequestListener(MediaRequestListener listener) {
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
    protected MediaResponse doInBackground(MediaRequest... params) {
        MediaResponse resultInfo = null;
        String requestUrl = null;
        String bizContent = null;
        URL url = null;
        HttpURLConnection conn = null;
        OutputStream os = null;
        try {
            requestUrl = mediaRequest.requestUrl;
            bizContent = mediaRequest.getBizJson().toString();
            url = new URL(requestUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Fiddler");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Charset", "UTF-8");
            os = conn.getOutputStream();
            os.write(bizContent.getBytes());
            os.close();
            int respCode = 0;
            if (errorCode == REQ_RESP_SUCCESS) {
                if (conn != null && ((respCode = conn.getResponseCode()) == HttpStatus.SC_OK ||
                        respCode == 404)) {
                    resultInfo = buildResultData(conn,respCode);
                } else if (conn == null) {
                    errorCode = RESPONSE_ERROR;
                } else {
                    errorCode = HOST_ERROR;
                }
            } else {
                resultInfo = null;
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

    private MediaStatusMsg buildStatusMessage(String rawRespString) {
        Gson gson = new Gson();
        MediaStatusMsg statusMsg = gson.fromJson(rawRespString, MediaStatusMsg.class);
        errorCode = statusMsg.code;
        return statusMsg;
    }

    private MediaResponse buildResultData(HttpURLConnection conn,int respCode) {
        MediaResponse resultInfo = null;
        String resultString = null;
        ByteArrayOutputStream baos = null;
        InputStream is = null;
        try {
            is = conn.getInputStream();
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1 ;
            while((len = is.read(buffer)) != -1){
                baos.write(buffer, 0, len);
            }
            resultString = new String(baos.toByteArray());
            Gson gson = new Gson();
            if (respCode == REQ_RESP_SUCCESS) {
                resultInfo = gson.fromJson(resultString, responseClazz);
                if (resultInfo != null) {
                    resultInfo.rawResult = resultString;
                }
            } else {
                MediaStatusMsg statusMsg = buildStatusMessage(resultString);
                if (statusMsg == null) {
                    errorLog.append("Failed to convert json to objet: " + responseClazz + "\r\n");
                }else{
                    errorLog.append(statusMsg.message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(null != is){
                    is.close();
                }
                if(null != baos){
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultInfo;
    }

    @Override
    protected void onPostExecute(MediaResponse result) {
        super.onPostExecute(result);

        if (isShowLoadingDialog) {
            DialogUtil.getInstance().cancelProgressDialog();
        }
        if (this.requestListener != null) {
            if (errorCode == REQ_RESP_SUCCESS) {
                if (result != null) {
                    this.requestListener.onNetworkResponseSucceed(result);
                } else {
                    errorLog.append(context.getString(R.string.no_data));
                    this.requestListener.onNetworkRequestError(RESPONSE_ERROR, errorLog.toString());
                }
            } else {
                this.requestListener.onNetworkRequestError(errorCode, errorLog.toString());
            }
        }
    }

    private void onExceptionThrown(Exception e, String message) {
        e.printStackTrace();
        errorLog.append(message + "\r\n");
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
