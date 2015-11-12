package com.zkjinshi.svip.activity.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zkjinshi.base.util.DialogUtil;
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
public class ServiceCenterActivity extends AppCompatActivity {

    private RelativeLayout mRlBack;
    private RelativeLayout mRlSearch;
    private RelativeLayout mRlTianJia;

    private TextView mTvTitle;
    private Spinner  mSpinnerCities;
    private ArrayAdapter mCitiesAdapter;

    private NoScrollViewPager mViewPager;
    private RadioGroup mRgOperation;
    private List<BaseFragment> mFragmentList;
    private FragmentPagerAdapter mFragmentPagerAdapter;

    private Animation rotateAnimation;
    private PopupMenu mMessagePopupMenu;
    private PopupMenu mContactsPopupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_center);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mRlBack    = (RelativeLayout) findViewById(R.id.rl_back);
        mRlSearch  = (RelativeLayout) findViewById(R.id.rl_search);
        mRlTianJia = (RelativeLayout) findViewById(R.id.rl_tianjia);

        mTvTitle       = (TextView) findViewById(R.id.tv_title);
        mSpinnerCities = (Spinner) findViewById(R.id.spinner_cities);

        mViewPager = (NoScrollViewPager) findViewById(R.id.nsvp_scroll_view_pager);
        mRgOperation = (RadioGroup) findViewById(R.id.rg_operation);
    }

    private void initData() {

        rotateAnimation = new RotateAnimation(0f, 45f,
                     Animation.RELATIVE_TO_SELF, 0.5f,
                     Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(500);
        rotateAnimation.setFillAfter(true);

        mFragmentList = new ArrayList<>();
        mFragmentList.add(new MessageCenterFragment());
        mFragmentList.add(new ContactsFragment());
        mFragmentList.add(new ServerListFragment());

        // Create an ArrayAdapter using the string array and a default spinner layout
        mCitiesAdapter = ArrayAdapter.createFromResource(ServiceCenterActivity.this,
                                                         R.array.cities_array,
                                                         R.layout.cities_spinner_item);
        // Specify the layout to use when the list of choices appears
        mCitiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpinnerCities.setAdapter(mCitiesAdapter);

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

        //默认选中消息中心界面
        mRgOperation.check(R.id.rb_liaotian);
        mTvTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(getString(R.string.service_center));
    }

    private void initListener() {
        //spinner城市选择
        mSpinnerCities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mRlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceCenterActivity.this.finish();
            }
        });

        mRlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.getInstance().showToast(ServiceCenterActivity.this, "to do search");
                //TODO: 进去搜索
            }
        });

        mRlTianJia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //消息中心显示界面
                if(mRgOperation.getCheckedRadioButtonId() == R.id.rb_liaotian){
                    //设置绕中心旋转45度
                    mRlTianJia.startAnimation(rotateAnimation);
                    if(null == mMessagePopupMenu){
                        mMessagePopupMenu = new PopupMenu(ServiceCenterActivity.this, mRlTianJia, Gravity.RIGHT);
                        //Inflating the Popup using xml file
                        MenuInflater inflater = mMessagePopupMenu.getMenuInflater();
                        inflater.inflate(R.menu.menu_message_center, mMessagePopupMenu.getMenu());
                    }

                    //窗体消失监听事件
                    mMessagePopupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                        @Override
                        public void onDismiss(PopupMenu menu) {
                            //取消动画
                            mRlTianJia.clearAnimation();
                        }
                    });

                    //registering popup with OnMenuItemClickListener
                    mMessagePopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.add_contact:
                                    DialogUtil.getInstance().showToast(ServiceCenterActivity.this, getString(R.string.add_contact));
                                    mRlTianJia.clearAnimation();
                                    break;

                                case R.id.ignore_all:
                                    DialogUtil.getInstance().showToast(ServiceCenterActivity.this, getString(R.string.ignore_all_msg));
                                    mRlTianJia.clearAnimation();
                                    break;

                                case R.id.clear_all:
                                    DialogUtil.getInstance().showToast(ServiceCenterActivity.this, getString(R.string.clear_all_msg));
                                    mRlTianJia.clearAnimation();
                                    break;
                            }
                            return true;
                        }
                    });
                    mMessagePopupMenu.show();
                }

                //联系人显示界面
                if(mRgOperation.getCheckedRadioButtonId() == R.id.rb_tongxunlu){

                    mRlTianJia.startAnimation(rotateAnimation);
                    if(null == mContactsPopupMenu){
                        mContactsPopupMenu = new PopupMenu(ServiceCenterActivity.this, mRlTianJia, Gravity.RIGHT);
                        //Inflating the Popup using xml file
                        MenuInflater inflater = mContactsPopupMenu.getMenuInflater();
                        inflater.inflate(R.menu.menu_contacts_center, mContactsPopupMenu.getMenu());
                    }

                    //窗体消失监听事件
                    mContactsPopupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                        @Override
                        public void onDismiss(PopupMenu menu) {
                            //取消动画
                            mRlTianJia.clearAnimation();
                        }
                    });

                    //registering popup with OnMenuItemClickListener
                    mContactsPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.add_contact:
                                    DialogUtil.getInstance().showToast(ServiceCenterActivity.this, getString(R.string.add_contact));
                                    mRlTianJia.clearAnimation();
                                    break;

                                case R.id.edit_contacts:
                                    DialogUtil.getInstance().showToast(ServiceCenterActivity.this, getString(R.string.edit_contacts));
                                    mRlTianJia.clearAnimation();
                                    break;

                            }
                            return true;
                        }
                    });
                    mContactsPopupMenu.show();
                }
            }
        });

        //底部radiobutton选中事件
        mRgOperation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_liaotian:
                        mViewPager.setCurrentItem(0);
                        mTvTitle.setVisibility(View.VISIBLE);
                        mTvTitle.setText(getString(R.string.service_center));
                        mSpinnerCities.setVisibility(View.INVISIBLE);
                        mRlTianJia.setVisibility(View.VISIBLE);
                        break;

                    case R.id.rb_tongxunlu:
                        mViewPager.setCurrentItem(1);
                        mTvTitle.setVisibility(View.INVISIBLE);
                        mSpinnerCities.setVisibility(View.INVISIBLE);
                        mRlTianJia.setVisibility(View.VISIBLE);
                        break;

                    case R.id.rb_faxian:
                        mViewPager.setCurrentItem(2);
                        mRlTianJia.setVisibility(View.GONE);
                        mTvTitle.setVisibility(View.INVISIBLE);
                        mSpinnerCities.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

}
