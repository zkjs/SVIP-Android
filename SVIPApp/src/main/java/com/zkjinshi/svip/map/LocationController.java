package com.zkjinshi.svip.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.order.PayOrderActivity;
import com.zkjinshi.svip.bean.BookOrder;
import com.zkjinshi.svip.response.OrderInfoResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.VIPContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 定位网络控制层
 * 开发者：JimmyZhang
 * 日期：2015/8/13
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class LocationController {

    private LocationController (){}

    private static LocationController instance;

    private StringRequest stringRequest;
    private RequestQueue requestQueue;

    private void init(){
        this.requestQueue = Volley.newRequestQueue(VIPContext.getInstance().getContext());
    }

    public synchronized static LocationController getInstance(){
        if(null == instance){
            instance = new LocationController();
            instance.init();
        }
        return instance;
    }

    public void requestAddGpsInfoTask(final HashMap<String,String> requestMap){
        stringRequest = new StringRequest(Request.Method.POST, ProtocolUtil.getAddGpdInfoUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!TextUtils.isEmpty(response)){
                            LogUtil.getInstance().info(LogLevel.INFO, "成功添加用户gps信息:" + response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.getInstance().info(LogLevel.INFO, "添加用户gps信息异常信息:" +  error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return requestMap;
            }
        };
        requestQueue.add(stringRequest);
    }

}
