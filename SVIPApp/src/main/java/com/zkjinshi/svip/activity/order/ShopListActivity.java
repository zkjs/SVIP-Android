package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.NetWorkUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.ShopAdapter;
import com.zkjinshi.svip.factory.ShopInfoFactory;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.ShopInfoResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.vo.ShopInfoVo;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

/**
 * 商店列表Activity
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopListActivity extends Activity{

    private final static String TAG = ShopListActivity.class.getSimpleName();

//    private ImageButton backIBtn;
//    private TextView searchTv;
    private ItemTitleView mTitle;
    private StringRequest stringRequest;
    private ListView shopListView;
    private List<ShopInfoResponse> shopResponseList;
    private List<ShopInfoVo> shopInfoList;
    private ShopAdapter shopAdapter;

    private void initView(){
//        backIBtn = (ImageButton)findViewById(R.id.hotel_list_header_bar_btn_back);
//        searchTv = (TextView)findViewById(R.id.hotel_list_header_bar_tv_search);
        mTitle = (ItemTitleView) findViewById(R.id.itv_title);
        shopListView = (ListView)findViewById(R.id.shop_list_view);
    }

    private void initData(){
        mTitle.setTextTitle("选择酒店");

        String url = Constants.GET_SHOP_LIST;
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
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
                    Type listType = new TypeToken<List<ShopInfoResponse>>(){}.getType();
                    Gson gson = new Gson();
                    shopResponseList = gson.fromJson(result.rawResult, listType);
                    if(null != shopResponseList && !shopResponseList.isEmpty()){
                        shopInfoList = ShopInfoFactory.getInstance().bulidShopList(shopResponseList);
                        shopAdapter = new ShopAdapter(shopInfoList,ShopListActivity.this);
                        shopListView.setAdapter(shopAdapter);
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

    private void initListeners(){

        //返回
        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);
            }
        });

        //搜索
        mTitle.getmRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //ListView列表
        shopListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShopInfoVo shopInfoVo = (ShopInfoVo)shopAdapter.getItem(position);
                //Intent intent = new Intent(ShopListActivity.this,GoodListActivity.class);
                Intent intent = new Intent(ShopListActivity.this,OrderBookingActivity.class);
                intent.putExtra("shopid", shopInfoVo.getShopid());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);
        initView();
        initData();
        initListeners();
    }

}
