package com.zkjinshi.svip.activity.city.helper;

import com.zkjinshi.svip.activity.city.citylist.CityBean;
import com.zkjinshi.svip.activity.city.citylist.CityModel;

import java.util.Comparator;

/**
 * 开发者：WinkyQin
 * 日期：2015/12/9
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CityComparator implements Comparator<CityBean> {

    @Override
    public int compare(CityBean c1, CityBean c2) {
        return c1.getCity().compareTo(c2.getCity());
    }

}
