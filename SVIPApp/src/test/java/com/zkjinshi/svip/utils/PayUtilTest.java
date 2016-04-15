package com.zkjinshi.svip.utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dujiande on 2016/4/15.
 */
public class PayUtilTest {

    String realBalance;
    double balance;
    @Before
    public void setUp() throws Exception {
        balance = 20822;
    }

    @Test
    public void testChangeMoney() throws Exception {
        realBalance = PayUtil.changeMoney(balance);
        assertEquals("208.22",realBalance);
    }
}