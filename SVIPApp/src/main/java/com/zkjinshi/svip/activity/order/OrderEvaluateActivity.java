package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.zkjinshi.svip.R;

/**
 * 订单评价页面
 * 开发者：JimmyZhang
 * 日期：2015/11/3
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderEvaluateActivity extends Activity{

    private CheckBox poorCb,commonCb,gratifyCb,greatGratifyCb,highlyRecommendCb;

    private void initView(){
        poorCb = (CheckBox)findViewById(R.id.order_evaluate_cb_poor);
        commonCb = (CheckBox)findViewById(R.id.order_evaluate_cb_common);
        gratifyCb = (CheckBox)findViewById(R.id.order_evaluate_cb_gratify);
        greatGratifyCb = (CheckBox)findViewById(R.id.order_evaluate_cb_great_gratify);
        highlyRecommendCb = (CheckBox)findViewById(R.id.order_evaluate_cb_highly_recommend);
    }

    private void initData(){

    }

    private void initListeners(){

        //差
        poorCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poorCb.setChecked(true);
                commonCb.setChecked(false);
                gratifyCb.setChecked(false);
                greatGratifyCb.setChecked(false);
                highlyRecommendCb.setChecked(false);
            }
        });

        //一般
        commonCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poorCb.setChecked(true);
                commonCb.setChecked(true);
                gratifyCb.setChecked(false);
                greatGratifyCb.setChecked(false);
                highlyRecommendCb.setChecked(false);
            }
        });

        //满意
        gratifyCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poorCb.setChecked(true);
                commonCb.setChecked(true);
                gratifyCb.setChecked(true);
                greatGratifyCb.setChecked(false);
                highlyRecommendCb.setChecked(false);
            }
        });

        //非常满意
        greatGratifyCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poorCb.setChecked(true);
                commonCb.setChecked(true);
                gratifyCb.setChecked(true);
                greatGratifyCb.setChecked(true);
                highlyRecommendCb.setChecked(false);
            }
        });

        //强烈推荐
        highlyRecommendCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poorCb.setChecked(true);
                commonCb.setChecked(true);
                gratifyCb.setChecked(true);
                greatGratifyCb.setChecked(true);
                highlyRecommendCb.setChecked(true);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_evaluate);
        initView();
        initData();
        initListeners();
    }
}
