package com.zkjinshi.svip;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.zkjinshi.svip.utils.PayUtil;

/**
 * Created by dujiande on 2016/4/15.
 */
public class TestClass extends InstrumentationTestCase {

    public void test(){
        double balance = 20822;
        String realBalance = PayUtil.changeMoney(balance);
        assertEquals("208.22",realBalance);
    }
}
