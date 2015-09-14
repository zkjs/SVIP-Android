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
import com.zkjinshi.base.net.core.MessageReceiver;
import com.zkjinshi.base.util.NetWorkUtil;
import com.zkjinshi.svip.response.ShopInfoResponse;
import com.zkjinshi.svip.sqlite.ShopDetailDBUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.vo.ShopDetailVo;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by djd on 2015/9/2.
 */
public class ShopListManager {
    public static final String TAG = MessageReceiver.class.getSimpleName();
   // private Map<String,String> shopNameMap = new HashMap<String,String>();
   // private Map<String,String> shopPhoneMap = new HashMap<String,String>();
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
                        LogUtil.getInstance().info(LogLevel.ERROR, "获取商户列表响应结果:" + response);
                        if(!TextUtils.isEmpty(response)){
                            try {
                                Type listType = new TypeToken<List<ShopDetailVo>>(){}.getType();
                                Gson gson = new Gson();
                                List<ShopDetailVo> shopResponseList = gson.fromJson(response, listType);
                                ShopDetailDBUtil.getInstance().batchAddShopInfo(shopResponseList);
                            }catch (Exception e){
                                e.printStackTrace();
                                LogUtil.getInstance().info(LogLevel.ERROR, TAG+"错误信息:" + e.getMessage());
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                LogUtil.getInstance().info(LogLevel.ERROR, "获取商户列表错误信息:" +  error.getMessage());
            }
        });
        if(NetWorkUtil.isNetworkConnected(context)){
          //  LogUtil.getInstance().info(LogLevel.ERROR, "获取商户列表" + stringRequest.toString());
            this.requestQueue.add(stringRequest);
        }
    }

    public String getShopName(String shopid){
       // return shopNameMap.get(shopid);
        String name = ShopDetailDBUtil.getInstance().queryShopNameByShopID(shopid);
       return TextUtils.isEmpty(name)? "":name;
    }

    public String getShopPhone(String shopid){
       // return shopPhoneMap.get(shopid);
        String phone = ShopDetailDBUtil.getInstance().queryShopPhoneByShopID(shopid);
        return TextUtils.isEmpty(phone)? "":phone;
    }
}
