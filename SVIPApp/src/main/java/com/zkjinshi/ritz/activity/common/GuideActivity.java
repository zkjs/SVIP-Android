package com.zkjinshi.ritz.activity.common;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zkjinshi.ritz.R;
import com.zkjinshi.ritz.base.BaseFragmentActivity;
import com.zkjinshi.ritz.view.scviewpager.DotsView;
import com.zkjinshi.ritz.view.scviewpager.SCViewPager;
import com.zkjinshi.ritz.view.scviewpager.SCViewPagerAdapter;


/**
 * 开发者：dujiande
 * 日期：2015/10/29
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GuideActivity extends BaseFragmentActivity {

    private static String TAG = GuideActivity.class.getSimpleName();

    private static final int NUM_PAGES = 2;

    private SCViewPager mViewPager;
    private SCViewPagerAdapter mPageAdapter;
    private DotsView mDotsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);
        mViewPager = (SCViewPager) findViewById(R.id.viewpager_main_activity);
        mDotsView = (DotsView) findViewById(R.id.dotsview_main);
        mDotsView.setDotRessource(R.mipmap.ellipse_pre, R.mipmap.ellipse_nor);
        mDotsView.setNumberOfPage(NUM_PAGES);

        mPageAdapter = new SCViewPagerAdapter(getSupportFragmentManager());
        mPageAdapter.setNumberOfPage(NUM_PAGES);
        mPageAdapter.setFragmentBackgroundColor(R.color.theme_100);
        mViewPager.setAdapter(mPageAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if( positionOffset== 0.0 && positionOffset ==0){
                   mPageAdapter.runInAnimation(position);
                    if(position-1 >= 0){
                        mPageAdapter.runOutAnimation(position-1);
                    }
                    if(position+1 < NUM_PAGES){
                        mPageAdapter.runOutAnimation(position+1);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                mDotsView.selectDot(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}
