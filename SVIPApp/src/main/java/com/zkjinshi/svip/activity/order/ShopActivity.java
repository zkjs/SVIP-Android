package com.zkjinshi.svip.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.ShopAdapter;
import com.zkjinshi.svip.adapter.ShopPagerAdapter;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.ShopListResponse;
import com.zkjinshi.svip.sqlite.ShopDetailDBUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

/**
 * 开发者：dujiande
 * 日期：2015/11/11
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopActivity extends AppCompatActivity {

    public static final String TAG = ShopActivity.class.getSimpleName();

    private ViewPager mViewPager;
    private ShopPagerAdapter mShopPagerAdapter;
    private RadioButton hotelRbtn,playRbtn,foodRbtn;
    private MaterialSearchView mSearchView;

    HashMap<String,String> datamap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        initView();
        initData();
        initListener();
    }

    private void initView(){
        mSearchView    = (MaterialSearchView) findViewById(R.id.sv_search_view);
        hotelRbtn = (RadioButton)findViewById(R.id.rbtn_jiudian);
        playRbtn = (RadioButton)findViewById(R.id.rbtn_xiuxian);
        foodRbtn = (RadioButton)findViewById(R.id.rbtn_canyin);

        mViewPager = (ViewPager)findViewById(R.id.vPager);
        mShopPagerAdapter = new ShopPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mShopPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    hotelRbtn.setChecked(true);
                } else if (position == 1) {
                    playRbtn.setChecked(true);
                } else if (position == 2) {
                    foodRbtn.setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initData(){
        datamap = ShopDetailDBUtil.getInstance().queryShopNames();
        int pageIndex = getIntent().getIntExtra("pageIndex",0);
        mViewPager.setCurrentItem(pageIndex);

        //是否加入语音设置
        mSearchView.setVoiceSearch(true);
        mSearchView.setCursorDrawable(R.drawable.custom_cursor);
        //设置搜索键入搜索框 此处本地联系人
       // mSearchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        mSearchView.setSuggestions(datamap);
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //DialogUtil.getInstance().showToast(ShopActivity.this, "Query: " + query);
                Intent intent = new Intent(ShopActivity.this,ShopSearchActivity.class);
                intent.putExtra("key",query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

        //搜索选中框
        mSearchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(com.miguelcatalan.materialsearchview.R.id.suggestion_text);
                Intent intent = new Intent(ShopActivity.this, OrderBookingActivity.class);
                String fullname = textView.getText().toString();
                intent.putExtra("shopid", mSearchView.mapData.get(fullname));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });
    }

    private void initListener(){
        findViewById(R.id.back_llt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        hotelRbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0, true);
            }
        });
        playRbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(1, true);
            }
        });
        foodRbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(2, true);
            }
        });

        findViewById(R.id.search_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setVisibility(View.VISIBLE);
                mSearchView.showSearch();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_default_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mSearchView.isSearchOpen()) {
            mSearchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }


}
