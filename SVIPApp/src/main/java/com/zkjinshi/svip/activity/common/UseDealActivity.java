package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;

/**
 * 使用说明
 * 开发者：JimmyZhang
 * 日期：2015/12/17
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class UseDealActivity extends BaseActivity {

    private ImageButton backIBtn;
    private TextView titleTv;

    private void initView(){
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
    }

    private void initData(){
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("使用协议");
    }

    private void initListeners(){

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_top,
                        R.anim.slide_out_bottom);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_deal);
        initView();
        initData();
        initListeners();
    }
}
