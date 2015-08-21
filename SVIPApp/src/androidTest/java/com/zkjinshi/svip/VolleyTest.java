package com.zkjinshi.svip;

import android.test.AndroidTestCase;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.lang.reflect.Method;

/**
 * Volley网络请求测试
 * 开发者：JimmyZhang
 * 日期：2015/7/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class VolleyTest extends AndroidTestCase{

    public static final String TAG = "VolleyTest";

    public void testGetRequest(){
        RequestQueue mQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,"http://www.baidu.com",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG,"响应结果:"+ response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG,"错误信息:"+ error.getMessage(), error);
            }
        });
        mQueue.add(stringRequest);
    }
}
