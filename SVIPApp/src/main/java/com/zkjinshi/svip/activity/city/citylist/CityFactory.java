package com.zkjinshi.svip.activity.city.citylist;

import android.text.TextUtils;

import com.zkjinshi.svip.fragment.contacts.CharacterParser;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：WinkyQin
 * 日期：2015/12/8
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CityFactory {

    private final static String TAG = CityFactory.class.getSimpleName();

    private static CityFactory instance;

    private CityFactory(){}

    public synchronized static CityFactory getInstance(){
        if(null == instance){
            instance = new CityFactory();
        }
        return instance;
    }

    public CityModel convertCityBean2CityModel(CityBean cityBean){
        CityModel cityModel = null;
        CharacterParser characterParser = null;
        if(null != cityBean && !TextUtils.isEmpty(cityBean.getCity())){
            cityModel = new CityModel();
            //中文城市名
            String city = cityBean.getCity();
            //汉字转换成拼
            characterParser   = new CharacterParser();
            String cityPinYin = characterParser.getSelling(city);
            cityModel.setCityName(city);
            cityModel.setNameSort(cityPinYin.substring(0, 1));
        }
        return cityModel;
    }

    public ArrayList<CityModel> convertCityBeans2CityModels(List<CityBean> cityBeans) {
        ArrayList<CityModel> cityModels = null;
        if(null != cityBeans && !cityBeans.isEmpty()){
            cityModels = new ArrayList<CityModel>();
            CityModel cityModel = null;
            for(CityBean cityBean : cityBeans){
                if(null != cityBean){
                    cityModel = convertCityBean2CityModel(cityBean);
                    cityModels.add(cityModel);
                }
            }
        }
        return cityModels;
    }
}
