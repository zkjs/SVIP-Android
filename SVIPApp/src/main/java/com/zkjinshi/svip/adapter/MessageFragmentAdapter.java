package com.zkjinshi.svip.adapter;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zkjinshi.svip.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：WinkyQin
 * 日期：2015/12/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MessageFragmentAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> fragmentList = new ArrayList<BaseFragment>();

    public MessageFragmentAdapter(FragmentManager fm, List<BaseFragment> mFragmentList) {
        super(fm);
        this.fragmentList = mFragmentList;
    }

    @Override
    public BaseFragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
