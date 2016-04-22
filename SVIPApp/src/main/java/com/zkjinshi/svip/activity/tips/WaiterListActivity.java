package com.zkjinshi.svip.activity.tips;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.WaiterListAdapter;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.response.WaitListResponse;
import com.zkjinshi.svip.test.WaiterListBiz;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.WaiterVo;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * 服务员列表页面
 * 开发者：JimmyZhang
 * 日期：2016/4/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class WaiterListActivity extends BaseActivity {

    private GridView waiterGridView;
    private ImageButton backIBtn;
    private TextView titleTv;
    private ArrayList<WaiterVo> waiterList = WaiterListBiz.getWaiterList();
    private WaiterListAdapter waiterListAdapter;
    private TextView noResultTv;

    private void initView(){
        waiterGridView = (GridView) findViewById(R.id.waiter_grid_view);
        backIBtn = (ImageButton)findViewById(R.id.btn_tips_back);
        titleTv = (TextView)findViewById(R.id.title_tips_tv);
        noResultTv = (TextView)findViewById(R.id.no_result);
    }

    private void initData(){
        titleTv.setText("服务员选择");
        backIBtn.setVisibility(View.VISIBLE);
        waiterListAdapter = new WaiterListAdapter(this,waiterList);
        waiterGridView.setAdapter(waiterListAdapter);
    }

    private void initListeners(){

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //点击item
        waiterGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WaiterVo waiterVo = waiterList.get(position);
                Intent intent = new Intent();
                intent.putExtra("waiterVo",waiterVo);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_list);
        initView();
        initData();
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestWaitListTask();
    }

    /**
     * 获取服务员列表
     */
    private void requestWaitListTask(){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            final String url = ProtocolUtil.getWaiterListUrl();
            client.get(WaiterListActivity.this,url,stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    DialogUtil.getInstance().showAvatarProgressDialog(WaiterListActivity.this,"");
                }

                public void onFinish(){
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        WaitListResponse waitListResponse = new Gson().fromJson(response,WaitListResponse.class);
                        if(null != waitListResponse){
                            int resultCode = waitListResponse.getRes();
                            if(0 == resultCode || 30001 == resultCode){
                                waiterGridView.setNumColumns(2);
                                waiterList = waitListResponse.getData();
                                if(null != waiterList && !waiterList.isEmpty()){
                                    waiterListAdapter.setWaiterList(waiterList);
                                }
                                if( 30001 == resultCode){
                                    waiterGridView.setEmptyView(noResultTv);
                                }
                            }else {
                                String errorMsg = waitListResponse.getResDesc();
                                if(!TextUtils.isEmpty(errorMsg)){
                                    DialogUtil.getInstance().showCustomToast(WaiterListActivity.this,errorMsg, Gravity.CENTER);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(WaiterListActivity.this,statusCode,url);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
