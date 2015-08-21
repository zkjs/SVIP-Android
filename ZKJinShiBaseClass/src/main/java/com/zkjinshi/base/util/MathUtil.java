package com.zkjinshi.base.util;

import java.math.BigDecimal;

/**
 * 数字换算工具类
 * 开发者：JimmyZhang
 * 日期：2015/7/30
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MathUtil {

    /**
     * 四舍五入，保留两位
     * @param srcDouble
     * @return
     */
    public static  double convertDouble(double srcDouble){
        double descDouble = 0.00;
        BigDecimal bigDecimal   =   new   BigDecimal(srcDouble);
        descDouble   =   bigDecimal.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
        return descDouble;
    }
}
