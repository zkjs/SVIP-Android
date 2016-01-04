package com.zkjinshi.svip.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.utils.Constants;

/**
 * 开发者：JimmyZhang
 * 日期：2015/12/31
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopGuideActivity extends BaseActivity{

    private ImageButton konwIBtn;

    private void initView(){
        konwIBtn = (ImageButton)findViewById(R.id.shop_guide_ibtn_konw);
    }

    private void initData(){

    }

    private void initListeners(){
        konwIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("rootCurrentItem",2);
                intent.putExtra("childCurrentItem",1);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_guide);
        initView();
        initData();
        initListeners();
    }
}
