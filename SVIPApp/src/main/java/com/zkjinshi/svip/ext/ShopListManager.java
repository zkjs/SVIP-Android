package com.zkjinshi.svip.ext;

import android.content.Context;
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
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.NetWorkUtil;
import com.zkjinshi.svip.activity.order.ShopListNetController;
import com.zkjinshi.svip.adapter.ShopAdapter;
import com.zkjinshi.svip.factory.ShopInfoFactory;
import com.zkjinshi.svip.response.ShopInfoResponse;
import com.zkjinshi.svip.utils.Constants;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by djd on 2015/9/2.
 */
public class ShopListManager {
    private Map<String,String> shopMap = new HashMap<String,String>();
    private static ShopListManager instance;
    private Context context;
    private RequestQueue requestQueue;

    private ShopListManager(){}
    public static synchronized ShopListManager getInstance(){
        if(null ==  instance){
            instance = new ShopListManager();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.GET_SHOP_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // DialogUtil.getInstance().cancelProgressDialog();
                        LogUtil.getInstance().info(LogLevel.INFO, "获取商户列表响应结果:" + response);
                        if(!TextUtils.isEmpty(response)){
                            try {
                                Type listType = new TypeToken<List<ShopInfoResponse>>(){}.getType();
                                Gson gson = new Gson();
                                List<ShopInfoResponse> shopResponseList = gson.fromJson(response, listType);
                                if(null != shopResponseList && !shopResponseList.isEmpty()){
                                    for(ShopInfoResponse item : shopResponseList){
                                        shopMap.put(item.getShopid(),item.getKnown_as());
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // DialogUtil.getInstance().cancelProgressDialog();
                LogUtil.getInstance().info(LogLevel.INFO, "获取商户列表错误信息:" +  error.getMessage());
            }
        });
        if(NetWorkUtil.isNetworkConnected(context)){
            //DialogUtil.getInstance().showProgressDialog(context);
            this.requestQueue.add(stringRequest);
        }
    }

    public Map<String, String> getShopMap() {
        return shopMap;
    }

    public void setShopMap(Map<String, String> shopMap) {
        this.shopMap = shopMap;
    }
}
