package com.zkjinshi.svip.activity.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;

import com.zkjinshi.svip.adapter.GoodAdapter;
import com.zkjinshi.svip.base.BaseActivity;


import com.zkjinshi.svip.listener.OnRefreshListener;
import com.zkjinshi.svip.net.RequestUtil;
import com.zkjinshi.svip.response.GetGoodListResponse;

import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;

import com.zkjinshi.svip.view.RefreshListView;
import com.zkjinshi.svip.vo.GoodInfoVo;


import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;


import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


/**
 * 商品列表Activity
 * 开发者：JimmyZhang
 * 日期：2015/7/27
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GoodListActivity extends BaseActivity {

    private final static String TAG = GoodListActivity.class.getSimpleName();
    private ImageView   backIv;
    private RefreshListView roomListView;
    private ArrayList<GoodInfoVo> goodInfoList;
    private GoodAdapter goodAdapter;
    private GoodInfoVo  goodInfoVo = null;

    private String  shopid;
    private String mCheckedRoomType;
    private Context mContext;
    private int    mCurrentPage = 0;//记录当前查询页
    private int mPageSize = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_list);
        mContext = this;

        initView();
        initData();
        initListeners();
    }

    private void initView(){
        backIv = (ImageView)findViewById(R.id.back_iv);
        roomListView = (RefreshListView)findViewById(R.id.good_list_list_view);
    }

    private void initData(){

        mCheckedRoomType = getIntent().getStringExtra("roomType");
        if(getIntent().getSerializableExtra("GoodInfoVo") != null){
            goodInfoVo = (GoodInfoVo)getIntent().getSerializableExtra("GoodInfoVo");
        }
        shopid = getIntent().getStringExtra("shopid");

        TextView tips  = (TextView)findViewById(R.id.empty_tips);
        tips.setText("暂无房型可选");
        roomListView.setEmptyView(findViewById(R.id.empty_linearlayout));
        findViewById(R.id.empty_linearlayout).setVisibility(View.INVISIBLE);

        goodInfoList = new ArrayList<GoodInfoVo>();
        goodAdapter = new GoodAdapter(GoodListActivity.this,goodInfoList);
        roomListView.setAdapter(goodAdapter);
        if(goodInfoVo != null){
            goodAdapter.setSelectId(goodInfoVo.getId());
            goodAdapter.setSelectName(goodInfoVo.getName());
        }

        DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
        getShopGoods();

    }

    //获取酒店信息
    private void getShopGoods(){

        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            if(CacheUtil.getInstance().isLogin()){
                client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            }
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            String url = ProtocolUtil.getGoodListByCity(shopid,mCurrentPage,mPageSize);
            client.get(mContext,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                }

                public void onFinish(){
                    DialogUtil.getInstance().cancelProgressDialog();
                    roomListView.refreshFinish();//结束刷新状态
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        GetGoodListResponse getGoodListResponse = new Gson().fromJson(response,GetGoodListResponse.class);
                        if (getGoodListResponse == null){
                            return;
                        }
                        if(getGoodListResponse.getRes() == 0){
                            goodInfoList = getGoodListResponse.getData();
                            if (null != goodInfoList && !goodInfoList.isEmpty()) {
                                if(mCurrentPage == 0){
                                    goodAdapter.refresh(goodInfoList);
                                }else{
                                    goodAdapter.loadMore(goodInfoList);
                                }
                                mCurrentPage++;
                            }
                        }else{
                            Toast.makeText(mContext,getGoodListResponse.getResDesc(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    Toast.makeText(mContext,"API 错误："+statusCode, Toast.LENGTH_SHORT).show();
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        }catch (Exception e){
            Toast.makeText(mContext,"json解析错误",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    private void initListeners(){

        //返回
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);
            }
        });

        roomListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefreshing() {
                mCurrentPage = 0;
                getShopGoods();
            }

            @Override
            public void onLoadingMore() {
                getShopGoods();
            }

            @Override
            public void implOnItemClickListener(AdapterView<?> parent, View view, int position, long id) {
                int realPostion = position - 1;
                goodInfoVo = (GoodInfoVo)goodAdapter.getItem(realPostion);
                String goodId = goodInfoVo.getId();
                if(!TextUtils.isEmpty(goodId)){
                    goodAdapter.setSelectId(goodId);
                    goodAdapter.notifyDataSetChanged();
                    //跳转回预定页面
                    Intent intent = new Intent();
                    intent.putExtra("GoodInfoVo", goodInfoVo);
                    intent.putExtra("shopid",shopid);
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });

    }
}
