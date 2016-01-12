package com.zkjinshi.svip.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;

/**
 * 开发者：JimmyZhang
 * 日期：2015/12/31
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class HomeGuideActivity extends BaseActivity{

    private ImageButton konwIBtn;

    private void initView(){
        konwIBtn = (ImageButton)findViewById(R.id.home_guide_ibtn_konw);
    }

    private void initData(){

    }

    private void initListeners(){
        konwIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // if(CacheUtil.getInstance().isShopGuide()){
                    Intent intent = new Intent(HomeGuideActivity.this, ShopGuideActivity.class);
                    startActivity(intent);
                    CacheUtil.getInstance().setShopGuide(false);
               // }
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_guide);
        initView();
        initData();
        initListeners();
    }

}
