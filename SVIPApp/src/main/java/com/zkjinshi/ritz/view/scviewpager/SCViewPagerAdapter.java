package com.zkjinshi.ritz.view.scviewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.zkjinshi.ritz.R;

import java.util.ArrayList;

/**
 * Created by Samuel on 2015-07-06.
 */

public class SCViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> mFragmentList;

    private int mNumberOfPage = 0;
    private int mBackgroundColor;

    public void runInAnimation(int page){
        MyPageAnimation pageAnimation = (MyPageAnimation)mFragmentList.get(page);
        pageAnimation.runInAnimation();
    }

    public void runOutAnimation(int page){
        MyPageAnimation pageAnimation = (MyPageAnimation)mFragmentList.get(page);
        pageAnimation.runOutAnimation();
    }

    public SCViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new SCViewPager1Fragment());
        mFragmentList.add(new SCViewPager2Fragment());
    }

    public void setNumberOfPage(int numberOfPage) {
        mNumberOfPage = numberOfPage;
    }

    public void setFragmentBackgroundColor(int colorResource) {
        mBackgroundColor = colorResource;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        if (position <= mFragmentList.size()-1){
            fragment = mFragmentList.get(position);
        }
        if (fragment == null) {
            SCViewPagerFragment sCViewPagerFragment = new SCViewPagerFragment();
            sCViewPagerFragment.setBackground(mBackgroundColor);
            fragment = sCViewPagerFragment;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return mNumberOfPage;
    }

    public static class SCViewPagerFragment extends Fragment {

        private int color;

        public SCViewPagerFragment() {
            this.color = R.color.white;
        }

        public void setBackground(int inColor) {
            this.color = inColor;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            LinearLayout view = new LinearLayout(getActivity());
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            view.setOrientation(LinearLayout.VERTICAL);
            view.setBackgroundColor(getResources().getColor(this.color));
            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }
    }

}
