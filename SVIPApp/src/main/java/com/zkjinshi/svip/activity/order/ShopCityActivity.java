package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.ShopAdapter;
import com.zkjinshi.svip.bean.BaseShopBean;
import com.zkjinshi.svip.bean.RecommendShopBean;
import com.zkjinshi.svip.bean.ShopBean;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.utils.ProtocolUtil;

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
public class ShopCityActivity extends Activity{

    private final static String TAG = ShopCityActivity.class.getSimpleName();

    private RelativeLayout mRlBack;
    private TextView       mTvCity;
    private ListView       mLvShopList;
    private List<BaseShopBean> mShopList;
    private ShopAdapter        mShopAdapter;

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
        mLvShopList = (ListView) findViewById(R.id.lv_shop_list);
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
        mLvShopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BaseShopBean baseShopBean = (BaseShopBean) mShopAdapter.getItem(position);
                if(baseShopBean instanceof RecommendShopBean){
                    //TODO:进入推荐的链接地址
                    String linkUrl = ((RecommendShopBean) baseShopBean).getLink_url();
                    Uri uri = Uri.parse(linkUrl);
                    Intent intent = new  Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(ShopCityActivity.this, GoodListActivity.class);
                    String shopid = baseShopBean.getShopid();
                    ShopBean shopBean = (ShopBean)baseShopBean;
                    intent.putExtra("shopBean", shopBean);
                    intent.putExtra("showHeader",true);
                    intent.putExtra("shopid",shopid);
                    ShopCityActivity.this.startActivity(intent);
                    ShopCityActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
            }

            @Override
            public void onNetworkRequestCancelled() {

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
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }
}
