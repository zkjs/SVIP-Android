package com.zkjinshi.svip.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.WebViewActivity;
import com.zkjinshi.svip.activity.shop.ShopDetailActivity;
import com.zkjinshi.svip.adapter.ShopAdapter;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.bean.ShopBean;
import com.zkjinshi.svip.listener.OnRefreshListener;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.RefreshListView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 根据城市查询商家列表
 * 开发者：WinkyQin
 * 日期：2015/12/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopCityActivity extends BaseActivity {

    private final static String TAG = ShopCityActivity.class.getSimpleName();

    private RelativeLayout mRlBack;
    private TextView       mTvCity;
    private RefreshListView mLvShopList;
    private List<ShopBean>  mShopList;
    private ShopAdapter     mShopAdapter;

    private int mPage = 1;
    private int mPageSize = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_shop);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mRlBack     = (RelativeLayout) findViewById(R.id.rl_back);
        mTvCity     = (TextView) findViewById(R.id.tv_city);
        mLvShopList = (RefreshListView) findViewById(R.id.lv_shop_list);
    }

    private void initData() {
        mShopList    = new ArrayList<>();
        mShopAdapter = new ShopAdapter(mShopList, this);
        mLvShopList.setAdapter(mShopAdapter);

        String city = getIntent().getStringExtra("city");
        if (!TextUtils.isEmpty(city)){
            mTvCity.setText(city);
            getShopListByCity(city, mPage, mPageSize);
        } else {
            getShopList(mPage, mPageSize);
        }
    }

    private void initListener() {

        //商店条目点击事件
        mLvShopList.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefreshing() {
                String city = mTvCity.getText().toString();
                if (!TextUtils.isEmpty(city)){
                    mTvCity.setText(city);
                    getShopListByCity(city, mPage, mPageSize);
                } else {
                    getShopList(mPage, mPageSize);
                }
            }

            @Override
            public void onLoadingMore() {
                String city = mTvCity.getText().toString();
                if (!TextUtils.isEmpty(city)){
                    mTvCity.setText(city);
                    getShopListByCity(city, mPage, mPageSize);
                } else {
                    getShopList(mPage, mPageSize);
                }
            }

            @Override
            public void implOnItemClickListener(AdapterView<?> parent, View view, int position, long id) {
                //进入商家详情
                ShopBean shopBean = null;
                int realPostion = position - 1;
                int itemType = mShopAdapter.getItemViewType(realPostion);
                if(itemType == mShopAdapter.ITEM_NORMAL_SHOP){
                    shopBean = mShopList.get(realPostion);
                    Intent intent = new Intent(ShopCityActivity.this, ShopDetailActivity.class);
                    String shopid = shopBean.getShopid();
                    intent.putExtra("shopId", shopid);
                    ShopCityActivity.this.startActivity(intent);
                    ShopCityActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }else if(itemType == mShopAdapter.ITEM_ADVERTISE){
                    shopBean = mShopList.get(realPostion);
                    String linkUrl = shopBean.getShopaddress();
                    if(!TextUtils.isEmpty(linkUrl)){
                        Intent intent = new Intent(ShopCityActivity.this, WebViewActivity.class);
                        intent.putExtra("webview_url", linkUrl);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    }
                }
            }
        });

        //输入框点击事件
        mRlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShopCityActivity.this.finish();
            }
        });

    }

    public void getShopList(int page, int pageSize) {
        getShopListByCity(null, page, pageSize);
    }

    private void getShopListByCity(String city, int page, int pageSize){
        String url = null;
        if(!TextUtils.isEmpty(city)){
            url = ProtocolUtil.getShopListByCityUrl(city, page, pageSize);
        }else{
            url = ProtocolUtil.getShopListUrl(page, pageSize);
        }

        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(ShopCityActivity.this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(ShopCityActivity.this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                mLvShopList.refreshFinish();
            }

            @Override
            public void onNetworkRequestCancelled() {
                mLvShopList.refreshFinish();
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    Type listType = new TypeToken<List<ShopBean>>(){}.getType();
                    Gson gson     = new Gson();
                    mPage++;
                    List<ShopBean> shopBeanList = gson.fromJson(result.rawResult, listType);
                    if(null != shopBeanList && !shopBeanList.isEmpty()){
                        mShopList.addAll(shopBeanList);
                        mShopAdapter.setData(mShopList);
                    }
                    LogUtil.getInstance().info(LogLevel.INFO, "getShopListByCity:" + mShopList);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                mLvShopList.refreshFinish();
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        if(mPage == 1){
            netRequestTask.isShowLoadingDialog = true;
        } else {
            netRequestTask.isShowLoadingDialog = false;
        }
        netRequestTask.execute();
    }
}
