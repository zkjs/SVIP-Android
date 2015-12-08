package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.map.LocationController;
import com.zkjinshi.svip.map.LocationManager;
import com.zkjinshi.svip.utils.CacheUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * 城市选择
 * 开发者：WinkyQin
 * 日期：2015/12/7
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CityChooseActivity extends Activity{

    private final static String TAG = CityChooseActivity.class.getSimpleName();

    private ImageButton mIbtnCancel;
    private TextView    mTvCityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_choose);

        //初始化定位
        LocationManager.getInstance().registerLocation(this);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mIbtnCancel = (ImageButton) findViewById(R.id.ibtn_cancel);
        mTvCityName = (TextView)    findViewById(R.id.tv_city_name);
    }

    private void initData() {

    }

    private void initListener() {

        mIbtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CityChooseActivity.this.finish();
            }
        });

        LocationManager.getInstance().setLocationChangeListener(new LocationManager.LocationChangeListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if(aMapLocation != null ){
                    Log.i(TAG, "aMapLocation.getAMapException().getErrorCode()=" +
                          aMapLocation.getAMapException().getErrorCode());
                }

                if(aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
                    //获取位置信息
                    String address  = aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果
                    String country  = aMapLocation.getCountry();//国家信息
                    String province = aMapLocation.getProvince();//省信息
                    String city     = aMapLocation.getCity();//城市信息
                    String district = aMapLocation.getDistrict();//城区信息
                    String rode     = aMapLocation.getRoad();//街道信息
                    String cityCode = aMapLocation.getCityCode();//城市编码
                    String code     = aMapLocation.getAdCode();//地区编码

                    DialogUtil.getInstance().showCustomToast(CityChooseActivity.this,
                                                        "city:"+city, Gravity.CENTER);
                    mTvCityName.setText( "city:"+city);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocationManager.getInstance().removeLocation();
    }
}
