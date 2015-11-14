package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.GoodAdapter;

import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.vo.GoodInfoVo;


import java.util.List;

/**
 * 商品列表Activity
 * 开发者：JimmyZhang
 * 日期：2015/7/27
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GoodListActivity extends Activity {

    private ItemTitleView mTitle;
    private ListView roomListView;
    private List<GoodInfoVo> goodInfoList;
    private GoodAdapter goodAdapter;
    private GoodInfoVo goodInfoVo;

    private void initView(){
        mTitle = (ItemTitleView)findViewById(R.id.itv_title);
        roomListView = (ListView)findViewById(R.id.good_list_list_view);

    }

    private void initData(){
        mTitle.setTextTitle("选择房型");
        mTitle.getmRight().setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        goodInfoVo = (GoodInfoVo) bundle.getSerializable("GoodInfoVo");
        goodInfoList = (List<GoodInfoVo>)bundle.getSerializable("goodInfoList");
        setResponseData(goodInfoList);
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

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_list);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            initView();
            initData();
            initListeners();
        }


    }

    private void setResponseData(List<GoodInfoVo> goodInfoList){

        goodAdapter = new GoodAdapter(goodInfoList,GoodListActivity.this);
        roomListView.setAdapter(goodAdapter);
        goodAdapter.selectGood(goodInfoVo.getId());
        //房型列表点击监听
        roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(null != goodAdapter){
                    GoodInfoVo goodInfoVo = (GoodInfoVo)goodAdapter.getItem(position);
                    String goodId = goodInfoVo.getId();
                    if(!TextUtils.isEmpty(goodId)){
                        goodAdapter.selectGood(goodId);
                        //跳转回选择页面
                        Intent inetnt = new Intent();
                        inetnt.putExtra("GoodInfoVo", goodInfoVo);
                        setResult(RESULT_OK, inetnt);
                        finish();
                    }
                }
            }
        });
    }
}
