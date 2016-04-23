package com.zkjinshi.svip.activity.tips;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;


import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.WaiterListAdapter;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.response.WaitListResponse;

import com.zkjinshi.svip.vo.WaiterVo;



import java.util.ArrayList;



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
    private ArrayList<WaiterVo> waiterList;
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
                Intent intent = new Intent(WaiterListActivity.this,SelectTipsActivity.class);
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
        TipsController.getInstance().requestWaitListTask(this, new TipsController.CallBackListener() {
            @Override
            public void successCallback(WaitListResponse waitListResponse) {
                waiterGridView.setNumColumns(2);
                waiterList = waitListResponse.getData();
                if(null != waiterList && !waiterList.isEmpty()){
                    waiterListAdapter.setWaiterList(waiterList);
                }else {
                    waiterGridView.setEmptyView(noResultTv);
                }
            }
        });
    }
}
