package com.zkjinshi.svip.ibeacon;

import android.content.Intent;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.VIPContext;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 开发者：JimmyZhang
 * 日期：2015/8/7
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class IBeaconController {

    private IBeaconController(){}

    private static IBeaconController instance;
    private StringRequest stringRequest;
    private RequestQueue requestQueue;
    private List<IBeaconEntity> beaconList;

    public synchronized static IBeaconController getInstance(){
        if(null == instance){
            instance = new IBeaconController();
        }
        return  instance;
    }

    public void init(){
        requestQueue = Volley.newRequestQueue(VIPContext.getInstance().getContext());
        requestIBeanconList();
    }

    private void requestIBeanconList(){
        stringRequest = new StringRequest(Request.Method.GET, ProtocolUtil.getLocationListUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtil.getInstance().info(LogLevel.INFO, "获取蓝牙列表响应结果:" + response);
                        if(!TextUtils.isEmpty(response)){
                            try {
                                Type listType = new TypeToken<List<IBeaconEntity>>(){}.getType();
                                Gson gson = new Gson();
                                beaconList = gson.fromJson(response, listType);
                                if(null != beaconList && !beaconList.isEmpty()){
                                    //将List转为成Map
                                    for (IBeaconEntity beancon:beaconList ) {
                                        IBeaconContext.getInstance().getBeanconMap().put(beancon.getBeaconKey(),beancon);
                                    }
                                    startBeaconService();
                                }
                            }catch (Exception e){
                                e.printStackTrace();;
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.getInstance().info(LogLevel.INFO, "获取蓝牙列表错误信息:" +  error.getMessage());
            }
        });
        requestQueue.add(stringRequest);
    }

    /**
     * 启动IBeacon蓝牙扫描服务
     */
    public void startBeaconService(){
        Intent intent = new Intent(VIPContext.getInstance().getContext(), IBeaconService.class);
        intent.setAction("com.zkjinshi.svip.im.Beacon_SERVICE");
        VIPContext.getInstance().getContext().startService(intent);
    }

    /**
     * 停止IBeacon蓝牙扫描服务
     */
    public void stopBeaconService(){
        Intent intent = new Intent(VIPContext.getInstance().getContext(), IBeaconService.class);
        intent.setAction("com.zkjinshi.svip.im.Beacon_SERVICE");
        VIPContext.getInstance().getContext().stopService(intent);
    }
}
