package com.zkjinshi.svip.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.ConsumeRecordAdapter;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.listener.OnRefreshListener;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.BaseResponse;
import com.zkjinshi.svip.response.OrderConsumeResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;

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
public class ConsumeRecordActivtiy extends BaseActivity {

    private final static String TAG = ConsumeRecordActivtiy.class.getSimpleName();

    private ImageButton backIBtn;
    private TextView titleTv;
    private RefreshListView mSlvBookOrder;

    private ConsumeRecordAdapter    mBookOrderAdapter = null;

    private String mUserID;
    private String mToken;
    private int    mCurrentPage;//记录当前查询页

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_record);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        initListener();
    }

    private void initView() {
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        TextView emptyView = (TextView) findViewById(R.id.empty_view);
        mSlvBookOrder = (RefreshListView) findViewById(R.id.slv_history_order);
        mSlvBookOrder.setEmptyView(emptyView);
    }

    private void initData() {
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("订单管理");

        mUserID = CacheUtil.getInstance().getUserId();
        mToken = CacheUtil.getInstance().getToken();
        mCurrentPage = 1;
        DialogUtil.getInstance().showProgressDialog(this);
        //获取用户订单信息
        getUserOrders(mUserID, mToken, mCurrentPage);
    }

    private void initListener() {
        /** 返回键监听事件 */
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConsumeRecordActivtiy.this.finish();
            }
        });

        mSlvBookOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrderConsumeResponse bookOrder = (OrderConsumeResponse)mBookOrderAdapter.getItem(position);
                String orderStatus = bookOrder.getStatus();
                Intent intent = new Intent();
                if(!TextUtils.isEmpty(orderStatus) && "3".equals(orderStatus)){
                    intent.setClass(ConsumeRecordActivtiy.this, OrderEvaluateActivity.class);
                    intent.putExtra("bookOrder",bookOrder);
                }else{
                    intent.setClass(ConsumeRecordActivtiy.this,OrderDetailActivity.class);
                    intent.putExtra("reservation_no",bookOrder.getReservation_no());
                    intent.putExtra("shopid",bookOrder.getShopid());
                }
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
    }

    /**
     * 获取用户历史订单列表
     * @param userID
     * @param token
     * @param currentPage
     */
    private void getUserOrders(final String userID, final String token, final int currentPage) {
        String url =  ProtocolUtil.getOrderV10list();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid", userID);
        bizMap.put("token", token);
        bizMap.put("status", "0,2,3,4");
        bizMap.put("page", currentPage+"");
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
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    mSlvBookOrder.refreshFinish();//结束刷新状态
                    ArrayList<OrderConsumeResponse> bookOrders = new Gson().fromJson(result.rawResult, new TypeToken<ArrayList<OrderConsumeResponse>>(){}.getType());
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
        bizMap.put("reservation_no", mBookOrderAdapter.datalist.get(position).getReservation_no());//订单更新
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
                super.onNetworkResponseSucceed(result);
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
