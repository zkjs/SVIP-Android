package com.zkjinshi.svip.activity.order;

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
import com.zkjinshi.base.util.NetWorkUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.bean.BookOrder;
import com.zkjinshi.svip.response.OrderInfoResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单确认网络请求控制层
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderConfirmNetController {
    private OrderConfirmNetController(){}
    private static OrderConfirmNetController instance;
    private Context context;
    private RequestQueue requestQueue;
    private Map<String, String> requestMap;
    private StringRequest stringRequest;
    private OrderInfoResponse orderInfoResponse;
    private boolean isSuccess;
    public static synchronized OrderConfirmNetController getInstance(){
        if(null ==  instance){
            instance = new OrderConfirmNetController();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public void requestGetGoodListTask(final BookOrder bookOrder){
        requestMap = new HashMap<String,String>();
        String shopId = bookOrder.getShopID();
        String roomTypeId = bookOrder.getRoomTypeID();
        String roomType =  bookOrder.getRoomType();
        String roomRate = bookOrder.getRoomRate();
        String arrivalDate = bookOrder.getArrivalDate();
        String departureDate = bookOrder.getDepartureDate();
        String userId = CacheUtil.getInstance().getUserId();
        String token = CacheUtil.getInstance().getToken();
        String userName = CacheUtil.getInstance().getUserName();
        String userPhone = CacheUtil.getInstance().getUserPhone();
        requestMap.put("set","1");
        if(!TextUtils.isEmpty(userId)){
            requestMap.put("userid",userId);
        }

        if(!TextUtils.isEmpty(token)){
            requestMap.put("token",token);
        }

        if(!TextUtils.isEmpty(userName)){
            requestMap.put("guest",userName);
        }

        if(!TextUtils.isEmpty(userPhone)){
            requestMap.put("guesttel",userPhone);
        }

        if(!TextUtils.isEmpty(shopId)){
            requestMap.put("shopid",shopId);
        }

        if(!TextUtils.isEmpty(roomTypeId)){
            requestMap.put("room_typeid",roomTypeId);
        }

        requestMap.put("rooms","1");
        if(!TextUtils.isEmpty(roomType)){
            requestMap.put("room_type",roomType);
        }

        if(!TextUtils.isEmpty(roomRate)){
            requestMap.put("room_rate",roomRate);
        }

        if(!TextUtils.isEmpty(arrivalDate)){
            requestMap.put("arrival_date",arrivalDate);
        }

        if(!TextUtils.isEmpty(departureDate)){
            requestMap.put("departure_date",departureDate);
        }

        stringRequest = new StringRequest(Request.Method.POST, Constants.POST_BOOK_MSG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        DialogUtil.getInstance().cancelProgressDialog();
                        LogUtil.getInstance().info(LogLevel.INFO, "用户预订商品响应结果:" + response);
                        if(!TextUtils.isEmpty(response)){
                            orderInfoResponse = new Gson().fromJson(response,OrderInfoResponse.class);
                            if(null != orderInfoResponse){
                                isSuccess = orderInfoResponse.isSet();
                                if(isSuccess){
                                    bookOrder.setReservationNO(orderInfoResponse.getReservation_no());
                                    Intent intent = new Intent(context,PayOrderActivity.class);
                                    intent.putExtra("bookOrder", bookOrder);
                                    context.startActivity(intent);
                                    ((Activity)context).finish();
                                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right,
                                            R.anim.slide_out_left);
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtil.getInstance().cancelProgressDialog();
                LogUtil.getInstance().info(LogLevel.INFO, "用户预订商品错误信息:" +  error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return requestMap;
            }
        };
        DialogUtil.getInstance().showProgressDialog(context);
        requestQueue.add(stringRequest);
    }
}
