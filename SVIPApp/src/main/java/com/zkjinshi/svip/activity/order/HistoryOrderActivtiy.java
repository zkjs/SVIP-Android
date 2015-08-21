package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.BookOrderAdapter;
import com.zkjinshi.svip.bean.BookOrder;
import com.zkjinshi.svip.bean.HistoryOrder;
import com.zkjinshi.svip.listener.OnRefreshListener;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.JsonUtil;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.view.swipelistview.SwipeMenu;
import com.zkjinshi.svip.view.swipelistview.SwipeMenuCreator;
import com.zkjinshi.svip.view.swipelistview.SwipeMenuItem;
import com.zkjinshi.svip.view.swipelistview.SwipeMenuListView;
import com.zkjinshi.svip.volley.DataRequestVolley;
import com.zkjinshi.svip.volley.HttpMethod;
import com.zkjinshi.svip.volley.RequestQueueSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 说明：历史订单(用户足迹)界面
 *
 * 开发者：vincent
 * 日期：2015/7/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class HistoryOrderActivtiy extends Activity {

    private final static String TAG = HistoryOrderActivtiy.class.getSimpleName();

    private ItemTitleView       mItvTitle;
    private SwipeMenuListView   mSlvBookOrder;

    private List<BookOrder>     mBookOrders;
    private BookOrderAdapter    mBookOrderAdapter;

    private String mUserID;
    private String mToken;
    private int    mCurrentPage;//记录当前查询页

    private Response.Listener<String>   getOrdersListener;
    private Response.ErrorListener      getOrdersErrorListener;
    private Response.Listener<String>   updateOrderListener;
    private Response.ErrorListener      updateOrderErrorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_order);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mItvTitle = (ItemTitleView) findViewById(R.id.Itv_title);
        mItvTitle.setTextColor(this, R.color.Black);
        mItvTitle.setResTitle(this, R.string.footprint);

        TextView emptyView = (TextView) findViewById(R.id.empty_view);
        mSlvBookOrder = (SwipeMenuListView) findViewById(R.id.slv_history_order);
        mSlvBookOrder.setEmptyView(emptyView);

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());// create "delete" item
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));// set item background
                deleteItem.setWidth(DisplayUtil.dip2px(HistoryOrderActivtiy.this, 90));// set item width
                deleteItem.setIcon(R.mipmap.ic_delete);// set a icon
                menu.addMenuItem(deleteItem);// add to menu
            }
        };
        mSlvBookOrder.setMenuCreator(creator);
    }

    private void initData() {
        mUserID = CacheUtil.getInstance().getUserId();
        mToken = CacheUtil.getInstance().getToken();
        mCurrentPage = 1;
        DialogUtil.getInstance().showProgressDialog(this);
        //获取用户订单信息
        getUserOrders(mUserID, mToken, mCurrentPage);
    }

    private void initListener() {
        /** 返回键监听事件 */
        mItvTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryOrderActivtiy.this.finish();
            }
        });

        /** 右边设置点击事件 */
        mItvTitle.getmRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.getInstance().showToast(HistoryOrderActivtiy.this, "更多设置");
            }
        });

        /** 设置界面刷新加载 */
        mSlvBookOrder.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefreshing() {
            }

            @Override
            public void onLoadingMore() {
                getUserOrders(mUserID, mToken, mCurrentPage);
            }

            @Override
            public void implOnItemClickListener(AdapterView<?> parent, View view, int position, long id) {
                BookOrder bookOrder = mBookOrders.get(position);
//                int orderStatus = Integer.parseInt(bookOrder.getStatus());
//                switch (orderStatus) {
//                    case BookOrder.ORDER_UNCONFIRMED:
                        Intent orderDetail = new Intent(HistoryOrderActivtiy.this, BookingOrderActivity.class);
                        orderDetail.putExtra("book_order", bookOrder);
                        startActivity(orderDetail);
//                        break;
//                    case BookOrder.ORDER_CONFIRMED:
//                        Intent confirmOrder = new Intent(HistoryOrderActivtiy.this, OrderConfirmActivity.class);
//                        confirmOrder.putExtra("book_order", bookOrder);
//                        startActivity(confirmOrder);
//                        break;
//                }
            }
        });

        // step 2. listener item click event
        mSlvBookOrder.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        /** 执行订单状态删除 */
                        int orderStatus = Integer.parseInt(mBookOrders.get(position).getStatus());
                        if (BookOrder.ORDER_DELETED == orderStatus || BookOrder.ORDER_CANCELLED
                                == orderStatus || BookOrder.ORDER_FINISHED == orderStatus) {
                            updateOrderStatus(mUserID, mToken, position, BookOrder.ORDER_DELETED + "");
                        } else {
                            DialogUtil.getInstance().showToast(HistoryOrderActivtiy.this, "当前订单状态不可删除");
                        }
                        break;
                }
                return false;
            }
        });

        // set SwipeListener
        mSlvBookOrder.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {
                // swipe start
                DialogUtil.getInstance().showToast(HistoryOrderActivtiy.this, "onSwipeStart");
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
                DialogUtil.getInstance().showToast(HistoryOrderActivtiy.this, "onSwipeEnd");
            }
        });

        // test item long click
        mSlvBookOrder.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DialogUtil.getInstance().showToast(HistoryOrderActivtiy.this, "long_click");

                return false;
            }
        });
    }

    /**
     * 获取用户历史订单列表
     * @param userID
     * @param token
     * @param currentPage
     */
    private void getUserOrders(final String userID, final String token, final int currentPage) {
        createGetOrdersListener();
        DataRequestVolley getUserOrders = new DataRequestVolley(HttpMethod.POST, Constants.POST_USER_ORDERS,
                getOrdersListener, getOrdersErrorListener){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> map = new HashMap<>();
                map.put("userid", userID);
                map.put("token", token);
                map.put("set", Constants.QUREY_ORDER + "");
                map.put("page", currentPage+"");
                return map;
            }
        };
        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(getUserOrders);
    }

    /**
     * 更新用户订单状态
     * @param userID
     * @param token
     */
    private void updateOrderStatus(final String userID, final String token, final int position , final String orderStatus) {
        createUpdateOrderListener(position);
        DataRequestVolley getUserOrders = new DataRequestVolley(HttpMethod.POST, Constants.POST_USER_ORDERS,
                updateOrderListener, updateOrderErrorListener){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> map = new HashMap<>();
                map.put("userid", userID);
                map.put("token", token);
                map.put("set", orderStatus);//订单状态操作
                map.put("status", Constants.UPDATE_ORDER + "");
                map.put("reservation_no", mBookOrders.get(position).getReservationNO());//订单更新
                return map;
            }
        };
        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(getUserOrders);
    }

    /**
     * 创建获取网络数据监听
     */
    private void createGetOrdersListener() {
        getOrdersListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.getInstance().info(LogLevel.INFO, "response:=" + response);
                DialogUtil.getInstance().cancelProgressDialog();
                mSlvBookOrder.refreshFinish();//结束刷新状态
                if(JsonUtil.isJsonNull(response)) {
                    DialogUtil.getInstance().showToast(HistoryOrderActivtiy.this, "暂无更多记录");
                    return ;
                }

                Gson gson = new Gson();
                //判断返回值
                if(response.toUpperCase().contains("SET")){
                    if(response.toUpperCase().contains("FALSE")) {
                        JSONObject resultObject = gson.fromJson(response, JSONObject.class);
                        try {
                            DialogUtil.getInstance().showToast(HistoryOrderActivtiy.this, "获取订单表err状态码:"
                                    + resultObject.get("err"));//显示
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return ;
                    }
                }
                List<BookOrder> bookOrders = gson.fromJson(response, new TypeToken<List<BookOrder>>(){}.getType());
                Log.v(TAG, "response:=" + bookOrders.toString());
                if(null != bookOrders && bookOrders.size() > 0){
                    if (mCurrentPage == 1) {
                        mBookOrders = new ArrayList<>();
                        mBookOrders.addAll(bookOrders);
                        mBookOrderAdapter = new BookOrderAdapter(mBookOrders,
                                HistoryOrderActivtiy.this);
                        mSlvBookOrder.setAdapter(mBookOrderAdapter);
                        mCurrentPage++;//进入第2页
                    } else {
                        mBookOrders.addAll(bookOrders);
                        int lastPosition = mCurrentPage * 10 - 1;
                        mCurrentPage++;//当前页+1
                        mBookOrderAdapter.notifyDataSetChanged();
                        mSlvBookOrder.setSelection(lastPosition + 1);
                    }
                }
            }
        };

        getOrdersErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError volleyError){
                mSlvBookOrder.refreshFinish();//结束刷新状态
                DialogUtil.getInstance().showToast(HistoryOrderActivtiy.this, "网络异常");
                volleyError.printStackTrace();
            }
        };
    }

    /**
     * 创建获取网络数据监听
     */
    private void createUpdateOrderListener(final int postion) {
        updateOrderListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.getInstance().info(LogLevel.INFO, "response:=" + response);
                if(JsonUtil.isJsonNull(response)) {
                    DialogUtil.getInstance().showToast(HistoryOrderActivtiy.this, "暂无更多记录");
                    return ;
                }

                Gson gson = new Gson();
                //判断返回值
                if(response.toUpperCase().contains("SET")){
                    JSONObject resultObject = gson.fromJson(response, JSONObject.class);
                    try {
                        Boolean updateSuccess = (Boolean) resultObject.get("set");
                        if(!updateSuccess){
                            if(null != resultObject.get("err")){
                                int errCode = (int) resultObject.get("err");
                                if(400 == errCode){
                                    DialogUtil.getInstance().showToast(HistoryOrderActivtiy.this, "当前用户没有操作权限");//显示
                                }
                            }
                        } else {
                            //删除成功
                            mBookOrders.remove(postion);
                            mBookOrderAdapter.notifyDataSetChanged();
                            mSlvBookOrder.setSelection(postion - 1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        updateOrderErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError volleyError){
                DialogUtil.getInstance().showToast(HistoryOrderActivtiy.this, "网络异常");
                volleyError.printStackTrace();
            }
        };
    }

}
