package com.zkjinshi.svip.activity.city.citylist;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.city.adapter.CityAdapter;
import com.zkjinshi.svip.activity.city.helper.CityComparator;
import com.zkjinshi.svip.activity.city.helper.ContactsHelper;
import com.zkjinshi.svip.activity.order.ShopCityActivity;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.map.LocationManager;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.sqlite.CityDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;

/**
 * 城市选择列表
 * 开发者：WinkyQin
 * 日期：2015/12/8
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CityListActivity extends BaseActivity {

    private final static String TAG = CityListActivity.class.getSimpleName();

    private TextView            citysearch;
    private RelativeLayout      mBack;
    private ListView            mLvCityList;
    private CityBean            mCityLocated;
    private ArrayList<CityBean> mCityBeanList;
    private CityComparator      mCityComparator;
    private CityAdapter         mCityAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mBack       = (RelativeLayout) findViewById(R.id.rl_back);
        citysearch  = (TextView)       findViewById(R.id.city_search_edittext);
        mLvCityList = (ListView)       findViewById(R.id.lv_city_list);
    }

    private void initData() {
        mCityComparator = new CityComparator();
        mCityBeanList   = new ArrayList<>();
        mCityLocated    = new CityBean();
        mCityLocated.setCityLocated(true);
        String currentCity = CacheUtil.getInstance().getCurrentCity();

        if (!TextUtils.isEmpty(currentCity)) {
            mCityLocated.setCity(currentCity);
        } else {
            mCityLocated.setCity(getString(R.string.locating));
        }

        mCityBeanList.add(0, mCityLocated);

        mCityAdapter    = new CityAdapter(CityListActivity.this,mCityBeanList);
        mLvCityList.setAdapter(mCityAdapter);
        //初始化定位
        LocationManager.getInstance().registerLocation(this);
        //获取当前热门城市列表
        getCityList();
    }

    private void initListener() {

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                CityListActivity.this.finish();
            }
        });

        citysearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_DOWN == event.getAction()){
                    //开始载入城市
                    ContactsHelper.getInstance().startLoadContacts();
                    Intent intent = new Intent(CityListActivity.this, SearchActivity.class);
                    startActivityForResult(intent, 2);
                }
                return false;
            }
        });

        //获取位置
        LocationManager.getInstance().setLocationChangeListener(
            new LocationManager.LocationChangeListener() {
                @Override
                public void onLocationChanged(final AMapLocation aMapLocation) {
                    if (aMapLocation != null) {
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               String city = aMapLocation.getCity();
                               if (!TextUtils.isEmpty(city)) {
                                   //更新城市显示
                                   mCityLocated.setCity(city.substring(0, city.length()-1));
                                   CacheUtil.getInstance().saveCurrentCity(city);
                                   Setting.Save2SharedPreferences(CityListActivity.this, "city", city);
                                   mCityAdapter.notifyDataSetChanged();
                               }
                           }
                       });
                    }
                }
            });

        //设置条目点击事件
        mLvCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CityBean cityBean = (CityBean) parent.getAdapter().getItem(position);
                String   city     = cityBean.getCity();
                if(!TextUtils.isEmpty(city) && !city.equals(getString(R.string.locating))){
                    //Intent intent = new Intent();
                    //intent.putExtra("city", city);
                    //setResult(RESULT_OK, intent);
                    //finish();
                    Setting.Save2SharedPreferences(CityListActivity.this, "city", city);
                    Intent intent = new Intent(CityListActivity.this, ShopCityActivity.class);
                    intent.putExtra("city", city);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CityListActivity.this, ShopCityActivity.class);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                String city = data.getStringExtra("city");
                Intent intent = new Intent();
                intent.putExtra("city", city);
                setResult(RESULT_OK, intent);
                finish();
                break;

            default:
                break;
        }
    }

    /**
     * 获取城市列表
     */
    private void getCityList(){
        String url = ProtocolUtil.getCityListUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(CityListActivity.this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(CityListActivity.this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    String json = result.rawResult;
                    if(!TextUtils.isEmpty(json)){
                        Gson gson = new Gson();
                        Type cityType = new TypeToken<ArrayList<CityBean>>(){}.getType();
                        List<CityBean> cityBeans = gson.fromJson(json, cityType);

                        if(null != cityBeans && !cityBeans.isEmpty()){
                            //比较城市名称
                            Collections.sort(cityBeans, mCityComparator);
                            mCityAdapter.setCityList(mCityBeanList);
                            mCityBeanList.addAll(cityBeans);

                            //添加城市名称进入数据库
                            List<CityModel> cityModels = CityFactory.getInstance().
                                             convertCityBeans2CityModels(cityBeans);
                            CityDBUtil.getInstance().batchAddCityModels(cityModels);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }

    @Override
    protected void onDestroy() {
        LocationManager.getInstance().removeLocation();
        super.onDestroy();
    }

}