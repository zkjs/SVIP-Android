package com.zkjinshi.ritz.view.scviewpager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zkjinshi.ritz.R;
import com.zkjinshi.ritz.activity.common.LoginActivity;
import com.zkjinshi.ritz.utils.CacheUtil;


/**
 * 开发者：dujiande
 * 日期：2015/10/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SCViewPager2Fragment extends MyPageAnimation{

    public static final String TAG = SCViewPager2Fragment.class.getSimpleName();
    public View view;
    private int time = 300;

    public SCViewPager2Fragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guide_page2,null);
        Log.i(TAG, "onCreateView");
        this.view = view;
        runOutAnimation();
        view.findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CacheUtil.getInstance().setGuide(false);
                Intent mainIntent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(mainIntent);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");
    }

    public void runInAnimation(){

    }

    @Override
    public void runOutAnimation() {

    }
}
