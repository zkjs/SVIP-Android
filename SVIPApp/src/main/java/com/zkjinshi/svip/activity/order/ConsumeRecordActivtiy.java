package com.zkjinshi.svip.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.activity.common.MainActivity;
import com.zkjinshi.svip.adapter.ConsumeRecordAdapter;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.base.BaseApplication;
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
import com.zkjinshi.svip.view.RefreshListView;

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
    private LinearLayout    mEmptyView;
    private ConsumeRecordAdapter    mBookOrderAdapter = null;

    private RelativeLayout mRlNoticeCard;
    private LinearLayout   mViewNoOrder;
    private RelativeLayout mViewNoLogin;
    private TextView       mTvLogin;
    private Button         mBtnDiscover;

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

        mEmptyView = (LinearLayout) findViewById(R.id.empty_view);
        mSlvBookOrder = (RefreshListView) findViewById(R.id.slv_history_order);
        mSlvBookOrder.setEmptyView(mEmptyView);

        //卡片显示View
        mRlNoticeCard = (RelativeLayout)findViewById(R.id.rl_notice_card);
        mViewNoOrder = (LinearLayout)   mEmptyView.findViewById(R.id.ll_no_order_view);
        mViewNoLogin = (RelativeLayout) mEmptyView.findViewById(R.id.rl_no_login_view);
        mTvLogin     = (TextView)       mEmptyView.findViewById(R.id.tv_login);
        mBtnDiscover = (Button)  mEmptyView.findViewById(R.id.btn_discover_service);
    }

    private void initData() {

        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("订单管理");

        //1.区分登录状态  a.未登录 b.登录无数据
        boolean isLogin = CacheUtil.getInstance().isLogin();
        if(!isLogin){
            mViewNoLogin.setVisibility(View.VISIBLE);
            return ;
        }

        mUserID = CacheUtil.getInstance().getUserId();
        mToken  = CacheUtil.getInstance().getToken();
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
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
            }
        });

        /** 进入登录界面 */
        mTvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goLogin();
            }
        });

        /** 进入城市列表选择 */
        mBtnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                ConsumeRecordActivtiy.this.finish();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_right);
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

                int realPostion = position - 1;
                OrderConsumeResponse bookOrder = (OrderConsumeResponse)mBookOrderAdapter.getItem(realPostion);
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
    }

    /**
     * 跳转到登录页面
     */
    private void goLogin() {
        Intent intent = new Intent(ConsumeRecordActivtiy.this, LoginActivity.class);
        intent.putExtra("isHomeBack",true);
        ConsumeRecordActivtiy.this.startActivity(intent);
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
                    } else {
                        /** 设置是否显示空数据提醒 */
                        if(mCurrentPage == 1){
                            mRlNoticeCard.setVisibility(View.VISIBLE);
                            mViewNoLogin.setVisibility(View.INVISIBLE);
                            mViewNoOrder.setVisibility(View.VISIBLE);
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
