package com.zkjinshi.svip.activity.order;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.ShopPagerAdapter;

/**
 * 开发者：dujiande
 * 日期：2015/11/11
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private ShopPagerAdapter mShopPagerAdapter;
    private RadioButton hotelRbtn,playRbtn,foodRbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        initView();
        initData();
        initListener();
    }

    private void initView(){
        hotelRbtn = (RadioButton)findViewById(R.id.rbtn_jiudian);
        playRbtn = (RadioButton)findViewById(R.id.rbtn_xiuxian);
        foodRbtn = (RadioButton)findViewById(R.id.rbtn_canyin);

        mViewPager = (ViewPager)findViewById(R.id.vPager);
        mShopPagerAdapter = new ShopPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mShopPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    hotelRbtn.setChecked(true);
                }else if(position == 1){
                    playRbtn.setChecked(true);
                }else if(position == 2){
                    foodRbtn.setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initData(){

    }

    private void initListener(){
        findViewById(R.id.back_llt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        hotelRbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0, true);
            }
        });
        playRbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(1,true);
            }
        });
        foodRbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(2,true);
            }
        });
    }
}
