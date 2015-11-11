package com.zkjinshi.svip.fragment;

import android.view.View;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseFragment;

/**
 * 根据地域显示专属客服列表
 * 开发者：WinkyQin
 * 日期：2015/11/11
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ServerListFragment extends BaseFragment{

    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.fragment_servier_list, null);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
    }
}