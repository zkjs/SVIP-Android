package com.zkjinshi.svip.view.scviewpager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zkjinshi.svip.R;


/**
 * 开发者：dujiande
 * 日期：2015/10/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public  class SCViewPager1Fragment extends MyPageAnimation {

    public static final String TAG = SCViewPager1Fragment.class.getSimpleName();
    private int time = 300;

    public View view = null;

    public SCViewPager1Fragment(){

    }

    public void runInAnimation(){

    }

    @Override
    public void runOutAnimation() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guide_page1,null);
        this.view = view;
        runOutAnimation();
        Log.i(TAG, "onCreateView");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");
    }
}
