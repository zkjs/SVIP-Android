package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.ShopAdapter;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.ShopListResponse;
import com.zkjinshi.svip.utils.ProtocolUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 开发者：dujiande
 * 日期：2015/11/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopSearchActivity extends Activity {

    private final static String TAG = ShopSearchActivity.class.getSimpleName();

    private EditText searchEt;
    private ListView shopListView;
    private List<ShopListResponse> shopResponseList = new ArrayList<ShopListResponse>();
    private ShopAdapter shopAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_search);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        searchEt = (EditText)findViewById(R.id.et_search_input);

        shopListView = (ListView)findViewById(R.id.shop_list_view);
        View emptyView = getLayoutInflater().inflate(R.layout.empty_layout, null);
        TextView tips = (TextView)emptyView.findViewById(R.id.empty_tips);
        tips.setText("暂无商家");
        ((ViewGroup)shopListView.getParent()).addView(emptyView);
        shopListView.setEmptyView(emptyView);
    }

    private void initData() {
        String key = getIntent().getStringExtra("key");
        if(!TextUtils.isEmpty(key)){
            searchEt.setText(key);
            searchShopInfo(key);
        }

        shopAdapter = new ShopAdapter(shopResponseList,this);
        shopListView.setAdapter(shopAdapter);


    }

    private void initListener() {
        findViewById(R.id.search_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(searchEt.getText())){
                    Toast.makeText(ShopSearchActivity.this,"请输入你要搜索的关键字。",Toast.LENGTH_SHORT).show();
                    return;
                }

                searchShopInfo(searchEt.getText().toString());
            }
        });

        findViewById(R.id.back_llt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //ListView列表
        shopListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShopListResponse shopInfoVo = (ShopListResponse) shopAdapter.getItem(position);
                Intent intent = new Intent(ShopSearchActivity.this, OrderBookingActivity.class);
                intent.putExtra("shopid", shopInfoVo.getShopid());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });
    }

    //搜索商家信息
    public void searchShopInfo(String query){
        String url = ProtocolUtil.getUserSearch();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("key", query);
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
                    Type listType = new TypeToken<List<ShopListResponse>>(){}.getType();
                    Gson gson = new Gson();
                    shopResponseList = gson.fromJson(result.rawResult, listType);
                    if(null != shopResponseList){
                        shopAdapter.setData(shopResponseList);
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
