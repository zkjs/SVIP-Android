package com.zkjinshi.svip.test;

import android.test.AndroidTestCase;
import android.util.Log;

import com.zkjinshi.base.util.Constants;
import com.zkjinshi.svip.utils.DistanceUtil;

/**
 * 开发者：JimmyZhang
 * 日期：2016/1/16
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class DistanceTest extends AndroidTestCase {

    public void testGpsLocation(){
        //x1=116.397428，y1=39.90923
        //x2=116.41004950566，y2=39.916979519873
        double distance = DistanceUtil.getDistance(116.397428, 39.90923, 116.41004950566, 39.916979519873);
        Log.i(Constants.ZKJINSHI_BASE_TAG,"gps距离:"+distance);
    }
}
