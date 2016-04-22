package com.zkjinshi.svip.activity.tips;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.vo.TipsResultVo;

/**
 * 小费支付结果页面
 * 开发者：JimmyZhang
 * 日期：2016/4/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class TipSuccesActivity extends BaseActivity {

    private TipsResultVo tipsResultVo;
    private ImageButton backIBtn;
    private TextView titleTv;

    private void initView(){
        titleTv = (TextView) findViewById(R.id.title_tips_tv);
        backIBtn = (ImageButton)findViewById(R.id.btn_tips_back);
    }

    private void initData(){
        titleTv.setText("成功");
        if(null != getIntent() && null != getIntent().getSerializableExtra("tipsResultVo")){
            tipsResultVo = (TipsResultVo)getIntent().getSerializableExtra("tipsResultVo");
        }
    }

    private void initListeners(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_succ);
        initView();
        initData();
        initListeners();
    }
}
