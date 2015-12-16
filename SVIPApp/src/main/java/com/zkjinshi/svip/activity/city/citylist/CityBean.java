package com.zkjinshi.svip.activity.city.citylist;

/**
 * 开发者：WinkyQin
 * 日期：2015/12/8
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CityBean {

    private String   city;
    private boolean isCityLocated;
    private int      count_city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isCityLocated() {
        return isCityLocated;
    }

    public void setCityLocated(boolean isCityLocated) {
        this.isCityLocated = isCityLocated;
    }

    public int getCount_city() {
        return count_city;
    }

    public void setCount_city(int count_city) {
        this.count_city = count_city;
    }
}
