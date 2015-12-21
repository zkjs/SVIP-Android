package com.zkjinshi.svip.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zkjinshi.svip.utils.VIPContext;

/**
 * 基类Fragment
 * 开发者：WinkyQin
 * 日期：2015/11/11
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public abstract class  BaseFragment extends Fragment {

    protected Activity mActivity;
    protected Context  mContext;
    protected FragmentManager mFragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mContext   = VIPContext.getInstance().getContext();
        this.mActivity  = getActivity();
        mFragmentManager = getFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 数据加载的操作
        onRestoreInstanceState(savedInstanceState);
        initData();
        initListener();
    }

    /**
     * fragment数据保存和重现
     * @param savedInstanceState
     */
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

    }

    /**
     * 数据加载的方法
     */
    protected void initData() {
    }

    protected void initListener(){
    }

    protected abstract View initView();

}

