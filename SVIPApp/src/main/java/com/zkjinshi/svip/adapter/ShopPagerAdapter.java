package com.zkjinshi.svip.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zkjinshi.svip.fragment.FoodFragment;
import com.zkjinshi.svip.fragment.HotelFragment;
import com.zkjinshi.svip.fragment.PlayFragment;

import java.util.ArrayList;

/**
 * 开发者：dujiande
 * 日期：2015/11/11
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> mFragmentList;

    public ShopPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new HotelFragment());
        mFragmentList.add(new PlayFragment());
        mFragmentList.add(new FoodFragment());
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        if (position <= mFragmentList.size()-1){
            fragment = mFragmentList.get(position);
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
