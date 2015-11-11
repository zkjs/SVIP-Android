package com.zkjinshi.svip.activity.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseFragment;
import com.zkjinshi.svip.fragment.ContactsFragment;
import com.zkjinshi.svip.fragment.MessageCenterFragment;
import com.zkjinshi.svip.fragment.ServerListFragment;
import com.zkjinshi.svip.view.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 采用toolbar显示的服务中心效果界面
 * 开发者：WinkyQin
 * 日期：2015/11/11
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ServiceCenterActivity extends AppCompatActivity{

    private Toolbar  mToolbar;
    private TextView mTvCenterTitle;

    private NoScrollViewPager    mViewPager;
    private RadioGroup           mRgOperation;
    private List<BaseFragment>   mFragmentList;
    private FragmentPagerAdapter mFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_center);

        initView();
        initData();
        initListener();
    }

    private void initView() {

        mToolbar       = (Toolbar)  findViewById(R.id.toolbar);
        mTvCenterTitle = (TextView) findViewById(R.id.tv_center_title);

        mViewPager   = (NoScrollViewPager) findViewById(R.id.nsvp_scroll_view_pager);
        mRgOperation = (RadioGroup) findViewById(R.id.rg_operation);
    }

    private void initData() {
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.mipmap.ic_fanhui);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTvCenterTitle.setText(getString(R.string.service_center));

        mFragmentList = new ArrayList<>();
        mFragmentList.add(new MessageCenterFragment());
        mFragmentList.add(new ContactsFragment());
        mFragmentList.add(new ServerListFragment());

        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        };

        mViewPager.setAdapter(mFragmentPagerAdapter);
    }

    private void initListener() {
        //底部radiobutton选中事件
        mRgOperation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_liaotian:
                    mViewPager.setCurrentItem(0);
                        break;

                case R.id.rb_tongxunlu:
                    mViewPager.setCurrentItem(1);
                    break;

                case R.id.rb_faxian:
                    mViewPager.setCurrentItem(2);
                    break;
            }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_service_center, menu);
        return true;
    }
}
