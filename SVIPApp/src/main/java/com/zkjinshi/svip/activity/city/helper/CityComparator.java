package com.zkjinshi.svip.activity.city.helper;

import com.zkjinshi.svip.activity.city.citylist.CityModel;

import java.util.Comparator;

/**
 * 开发者：WinkyQin
 * 日期：2015/12/9
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CityComparator implements Comparator<CityModel> {

    @Override
    public int compare(CityModel c1, CityModel c2) {
        return c1.getNameSort().compareTo(c2.getNameSort());
    }

}
