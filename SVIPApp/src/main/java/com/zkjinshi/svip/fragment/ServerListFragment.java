package com.zkjinshi.svip.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.ServerAdapter;
import com.zkjinshi.svip.base.BaseFragment;
import com.zkjinshi.svip.bean.CustomerServiceBean;
import com.zkjinshi.svip.bean.HeadBean;
import com.zkjinshi.svip.manager.CustomerServicesManager;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.CustomerServiceListResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 根据地域显示专属客服列表
 * 开发者：WinkyQin
 * 日期：2015/11/11
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ServerListFragment extends BaseFragment{

    private SwipeRefreshLayout  mSrlContainer;
    private RecyclerView        mRcvServerList;
    private LinearLayoutManager mLayoutManager;
    private ServerAdapter       mServerAdapter;

    private List<CustomerServiceBean> mServerLists;

    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.fragment_server_list, null);
        mSrlContainer  = (SwipeRefreshLayout) view.findViewById(R.id.srl_container);
        mRcvServerList = (RecyclerView) view.findViewById(R.id.rcv_server_list);
        mRcvServerList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRcvServerList.setLayoutManager(mLayoutManager);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        mServerLists    = new ArrayList();
        mServerAdapter  = new ServerAdapter(mActivity, mServerLists);
        mRcvServerList.setAdapter(mServerAdapter);
        initListener();
        //TODO: present the data
        loadServerList();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mSrlContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadServerList();
            }
        });
    }

    /**
     * 载入数据
     */
    private void loadServerList() {
        NetRequest netRequest = new NetRequest(ProtocolUtil.getCustomerServiceUrl());
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        bizMap.put("shopid","120");
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(mContext, netRequest, NetResponse.class);
        netRequestTask.setNetRequestListener( new ExtNetRequestListener(mContext) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                super.onNetworkRequestError(errorCode, errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {
                super.onNetworkRequestCancelled();
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                CustomerServiceListResponse customerServiceListResponse = new Gson().fromJson(
                                         result.rawResult, CustomerServiceListResponse.class);

                if (null != customerServiceListResponse) {
                    HeadBean head = customerServiceListResponse.getHead();
                    if (null != head) {
                        boolean isSet = head.isSet();
                        if (isSet) {
                            ArrayList<CustomerServiceBean> customerServiceList = customerServiceListResponse.getData();
                            if (null != customerServiceList && !customerServiceList.isEmpty()) {
                                mServerAdapter.clear();
                                mServerAdapter.addAll(customerServiceList);
                                mSrlContainer.setRefreshing(false);
                            }
                        }
                    }
                }
            }

            @Override
            public void beforeNetworkRequestStart() {
                super.beforeNetworkRequestStart();
            }
        });
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }
}
