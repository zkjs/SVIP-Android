package com.zkjinshi.svip.activity.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
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
import com.zkjinshi.svip.net.RequestUtil;
import com.zkjinshi.svip.response.GetShopListResponse;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.RefreshListView;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

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
    private ArrayList<ShopBean>  mShopList;
    private ShopAdapter     mShopAdapter;

    private int mPage = 0;
    private int mPageSize = 5;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_shop);
        mContext = this;

        initView();
        initData();
        initListener();
    }

    public void onStop(){
        super.onStop();
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
        }
    }

    private void initListener() {

        //商店条目点击事件
        mLvShopList.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefreshing() {
                String city = mTvCity.getText().toString();
                mPage = 0;
                if (!TextUtils.isEmpty(city)){
                    mTvCity.setText(city);
                    getShopListByCity(city, mPage, mPageSize);
                }
            }

            @Override
            public void onLoadingMore() {
                String city = mTvCity.getText().toString();
                if (!TextUtils.isEmpty(city)){
                    mTvCity.setText(city);
                    getShopListByCity(city, mPage, mPageSize);
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



    private void getShopListByCity(String city, int page, int pageSize){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            if(CacheUtil.getInstance().isLogin()){
                client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            }
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getShopListByCity(city,page,pageSize);
            client.get(mContext,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    if(mPage == 0){
                        DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                    }
                }

                public void onFinish(){
                    mLvShopList.refreshFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        GetShopListResponse getShopListResponse = new Gson().fromJson(response,GetShopListResponse.class);
                        if (getShopListResponse == null){
                            return;
                        }
                        if(getShopListResponse.getRes() == 0){
                            List<ShopBean> shopList = getShopListResponse.getData();
                            if(mPage == 0){
                                mShopList.clear();
                                mShopList.addAll(shopList);
                                mShopAdapter.setShopList(mShopList);
                            }else if(!shopList.isEmpty()){
                                mShopList.addAll(shopList);
                                mShopAdapter.setShopList(mShopList);
                            }
                            if(!shopList.isEmpty()){
                                mPage++;
                            }
                        }else{
                            Toast.makeText(mContext,getShopListResponse.getResDesc(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    Toast.makeText(mContext,"API 错误："+statusCode,Toast.LENGTH_SHORT).show();
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        }catch (Exception e){
            Toast.makeText(mContext,"json解析错误",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
