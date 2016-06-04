package com.zkjinshi.ritz.utils;

import java.text.DecimalFormat;

/**
 * 开发者：dujiande
 * 日期：2015/11/13
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class PayUtil {

    public static String changeMoney(double balance){
        balance = balance/100;
        String moneyStr = new DecimalFormat("0.00").format(balance);
        return  moneyStr;
    }
}
