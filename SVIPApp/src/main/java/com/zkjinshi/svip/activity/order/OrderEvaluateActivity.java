package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

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
    private EditText inputEvaluateEtv;
    private RelativeLayout inputEvaluateTipLayout;
    private Animation layoutInBottom, layoutOutTop;
    private boolean isFirst = true;

    private void initView(){
        poorCb = (CheckBox)findViewById(R.id.order_evaluate_cb_poor);
        commonCb = (CheckBox)findViewById(R.id.order_evaluate_cb_common);
        gratifyCb = (CheckBox)findViewById(R.id.order_evaluate_cb_gratify);
        greatGratifyCb = (CheckBox)findViewById(R.id.order_evaluate_cb_great_gratify);
        highlyRecommendCb = (CheckBox)findViewById(R.id.order_evaluate_cb_highly_recommend);
        inputEvaluateEtv = (EditText)findViewById(R.id.order_evaluate_etv_content);
        inputEvaluateTipLayout = (RelativeLayout)findViewById(R.id.order_evaluate_layout_tips);
    }

    private void initData(){
        layoutInBottom = AnimationUtils.loadAnimation(this, R.anim.layout_in_bottom);
        layoutOutTop = AnimationUtils.loadAnimation(this,R.anim.layout_out_top);
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
                poorCb.setTextColor(getResources().getColor(R.color.star_check_color));
                commonCb.setTextColor(getResources().getColor(R.color.star_nor_color));
                gratifyCb.setTextColor(getResources().getColor(R.color.star_nor_color));
                greatGratifyCb.setTextColor(getResources().getColor(R.color.star_nor_color));
                highlyRecommendCb.setTextColor(getResources().getColor(R.color.star_nor_color));
                showEvaluateInput();
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
                poorCb.setTextColor(getResources().getColor(R.color.star_nor_color));
                commonCb.setTextColor(getResources().getColor(R.color.star_check_color));
                gratifyCb.setTextColor(getResources().getColor(R.color.star_nor_color));
                greatGratifyCb.setTextColor(getResources().getColor(R.color.star_nor_color));
                highlyRecommendCb.setTextColor(getResources().getColor(R.color.star_nor_color));
                showEvaluateInput();
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
                poorCb.setTextColor(getResources().getColor(R.color.star_nor_color));
                commonCb.setTextColor(getResources().getColor(R.color.star_nor_color));
                gratifyCb.setTextColor(getResources().getColor(R.color.star_check_color));
                greatGratifyCb.setTextColor(getResources().getColor(R.color.star_nor_color));
                highlyRecommendCb.setTextColor(getResources().getColor(R.color.star_nor_color));
                showEvaluateInput();
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
                poorCb.setTextColor(getResources().getColor(R.color.star_nor_color));
                commonCb.setTextColor(getResources().getColor(R.color.star_nor_color));
                gratifyCb.setTextColor(getResources().getColor(R.color.star_nor_color));
                greatGratifyCb.setTextColor(getResources().getColor(R.color.star_check_color));
                highlyRecommendCb.setTextColor(getResources().getColor(R.color.star_nor_color));
                showEvaluateInput();
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
                poorCb.setTextColor(getResources().getColor(R.color.star_nor_color));
                commonCb.setTextColor(getResources().getColor(R.color.star_nor_color));
                gratifyCb.setTextColor(getResources().getColor(R.color.star_nor_color));
                greatGratifyCb.setTextColor(getResources().getColor(R.color.star_nor_color));
                highlyRecommendCb.setTextColor(getResources().getColor(R.color.star_check_color));
                showEvaluateInput();
            }
        });
    }

    /**
     * 显示评价内容
     *
     */
    private void showEvaluateInput(){
        if(isFirst){
            inputEvaluateTipLayout.startAnimation(layoutOutTop);
            inputEvaluateEtv.startAnimation(layoutInBottom);
            layoutInBottom.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    inputEvaluateEtv.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            isFirst = false;
        }
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
