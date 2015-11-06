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


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.svip.R;

import com.zkjinshi.svip.adapter.ConsumeRecordAdapter;
import com.zkjinshi.svip.bean.BookOrder;
import com.zkjinshi.svip.listener.OnRefreshListener;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.BaseResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;

import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.view.swipelistview.SwipeMenu;
import com.zkjinshi.svip.view.swipelistview.SwipeMenuCreator;
import com.zkjinshi.svip.view.swipelistview.SwipeMenuItem;
import com.zkjinshi.svip.view.swipelistview.SwipeMenuListView;



import java.util.ArrayList;
import java.util.HashMap;



/**
 * 说明：消费记录界面
 *
 * 开发者：dujiande
 * 日期：2015/11/03
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ConsumeRecordActivtiy extends Activity {

    private final static String TAG = ConsumeRecordActivtiy.class.getSimpleName();

    private ItemTitleView       mItvTitle;
    private SwipeMenuListView   mSlvBookOrder;


    private ConsumeRecordAdapter    mBookOrderAdapter = null;

    private String mUserID;
    private String mToken;
    private int    mCurrentPage;//记录当前查询页


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_order);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        initListener();
    }

    private void initView() {
        mItvTitle = (ItemTitleView) findViewById(R.id.Itv_title);
        TextView emptyView = (TextView) findViewById(R.id.empty_view);
        mSlvBookOrder = (SwipeMenuListView) findViewById(R.id.slv_history_order);
        mSlvBookOrder.setEmptyView(emptyView);

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());// create "delete" item
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));// set item background
                deleteItem.setWidth(DisplayUtil.dip2px(ConsumeRecordActivtiy.this, 90));// set item width
                deleteItem.setIcon(R.mipmap.ic_delete);// set a icon
                menu.addMenuItem(deleteItem);// add to menu
            }
        };
        mSlvBookOrder.setMenuCreator(creator);
    }

    private void initData() {
        mItvTitle.setTextTitle("消费记录");
        mItvTitle.getmRight().setVisibility(View.GONE);

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
                ConsumeRecordActivtiy.this.finish();
            }
        });

        /** 右边设置点击事件 */
        mItvTitle.getmRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mSlvBookOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ConsumeRecordActivtiy.this, OrderEvaluateActivity.class);
                startActivity(intent);
            }
        });

        /** 设置界面刷新加载 */
        mSlvBookOrder.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefreshing() {
                mCurrentPage = 1;
                getUserOrders(mUserID, mToken, mCurrentPage);
            }

            @Override
            public void onLoadingMore() {
                getUserOrders(mUserID, mToken, mCurrentPage);
            }

            @Override
            public void implOnItemClickListener(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        // step 2. listener item click event
        mSlvBookOrder.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        /** 执行订单状态删除 */
                        int orderStatus = Integer.parseInt(mBookOrderAdapter.datalist.get(position).getStatus());
                        if (BookOrder.ORDER_DELETED == orderStatus || BookOrder.ORDER_CANCELLED
                                == orderStatus || BookOrder.ORDER_FINISHED == orderStatus) {
                            updateOrderStatus(mUserID, mToken, position, BookOrder.ORDER_DELETED + "");
                        } else {
                            DialogUtil.getInstance().showToast(ConsumeRecordActivtiy.this, "当前订单状态不可删除");
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
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });

        // test item long click
        mSlvBookOrder.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

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
        String url =  ProtocolUtil.getOrderList();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid", userID);
        bizMap.put("token", token);
        bizMap.put("set", Constants.QUREY_ORDER + "");
        bizMap.put("page", currentPage+"");
        bizMap.put("status", "3");
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                mSlvBookOrder.refreshFinish();//结束刷新状态
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    mSlvBookOrder.refreshFinish();//结束刷新状态
                    ArrayList<BookOrder> bookOrders = new Gson().fromJson(result.rawResult, new TypeToken<ArrayList<BookOrder>>(){}.getType());
                    if(null != bookOrders && bookOrders.size() > 0){
                        if (mCurrentPage == 1) {
                            if(mBookOrderAdapter == null){
                                mBookOrderAdapter = new ConsumeRecordAdapter(bookOrders, ConsumeRecordActivtiy.this);
                                mSlvBookOrder.setAdapter(mBookOrderAdapter);
                            }else{
                                mBookOrderAdapter.refresh(bookOrders);
                            }
                            mCurrentPage++;//进入第2页
                        } else {
                            mBookOrderAdapter.loadMore(bookOrders);
                            mCurrentPage++;//当前页+1
                            mSlvBookOrder.setSelection(mBookOrderAdapter.datalist.size() - 1);
                        }
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    /**
     * 更新用户订单状态
     * @param userID
     * @param token
     */
    private void updateOrderStatus(final String userID, final String token, final int position , final String orderStatus) {

        String url = ProtocolUtil.updateOrderUrl();
        Log.i(TAG,url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid", userID);
        bizMap.put("token", token);
        bizMap.put("status", orderStatus);
        bizMap.put("reservation_no", mBookOrderAdapter.datalist.get(position).getReservationNO());//订单更新
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    Gson gson = new Gson();
                    BaseResponse baseResponse = gson.fromJson(result.rawResult,BaseResponse.class);
                    if(baseResponse.isSet()){
                        //删除成功
                        mBookOrderAdapter.datalist.remove(position);
                        mBookOrderAdapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }




}
