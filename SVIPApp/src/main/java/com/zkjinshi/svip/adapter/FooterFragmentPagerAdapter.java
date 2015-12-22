package com.zkjinshi.svip.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2015/12/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class FooterFragmentPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> list;

    public FooterFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list){
        super(fm);
        this.list=list;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

}
