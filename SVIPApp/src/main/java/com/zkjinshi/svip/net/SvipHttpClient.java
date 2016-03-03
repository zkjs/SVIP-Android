package com.zkjinshi.svip.net;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zkjinshi.svip.utils.CacheUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by dujiande on 2016/3/2.
 */
public class SvipHttpClient {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void put(Context context, String url, JSONObject jsonObject, JsonHttpResponseHandler handler){
        try{
            client.setMaxRetriesAndTimeout(3,500);
            client.setTimeout(3000);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            client.put(context,url, stringEntity, "application/json", handler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public static void get(Context context, String url, JSONObject jsonObject, JsonHttpResponseHandler handler){
        try{
            client.setTimeout(5000);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            client.get(context,url, stringEntity, "application/json", handler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

}
