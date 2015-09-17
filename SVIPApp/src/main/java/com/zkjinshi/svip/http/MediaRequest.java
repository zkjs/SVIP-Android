package com.zkjinshi.svip.http;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装媒体附件请求类
 * 开发者：JimmyZhang
 * 日期：2015/9/17
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MediaRequest {

    public static final String TAG = "MediaRequest";

    /**
     * 请求的完整URL
     */
    public String requestUrl;

    /**
     * 响应的数据的编码格式，默认为UTF-8
     *
     * @return
     */
    public String responseEncoding = "UTF-8";

    /**
     * 请求等待的过程的提示信息,一般可以留空
     */
    public String waitingPromptMessage;

    private JSONObject mBizJson = new JSONObject();

    /**
     *
     * @param requestUrl
     *            请求的完整URL
     */
    public MediaRequest(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public void addBizParam(String name, String value) throws JSONException {
        this.mBizJson.put(name, value);
    }

    public JSONObject getBizJson() {
        return mBizJson;
    }

    public String getRawRequestParams(){
        List<NameValuePair> params = new ArrayList<NameValuePair>(1);
        JSONObject json = new JSONObject();
        try {
            json.put("biz", mBizJson);
            Log.i("info", "JimmyZhang-json:" + json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,"The request parameters is invalid for Json." + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"The request parameters is failed to encrypted." + e.toString());
        }

        return json.toString();
    }
}
