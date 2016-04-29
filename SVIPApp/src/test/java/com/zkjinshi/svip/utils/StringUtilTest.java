package com.zkjinshi.svip.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dujiande on 2016/4/27.
 */
public class StringUtilTest {

    @Test
    public void testIsEnglishName() throws Exception {
        assertEquals(true,StringUtil.isEnglishName("John"));
        assertEquals(false,StringUtil.isEnglishName("John "));
        assertEquals(true,StringUtil.isEnglishName("John lin"));
        assertEquals(true,StringUtil.isEnglishName("John lin du"));
        assertEquals(false,StringUtil.isEnglishName("John lin du *"));
    }

    @Test
    public void testIsChineseName() throws Exception {
        assertEquals(true,StringUtil.isChineseName("白月初"));
        assertEquals(false,StringUtil.isChineseName("白 月初"));
        assertEquals(false,StringUtil.isChineseName("白 月初 e"));
        assertEquals(false,StringUtil.isChineseName("白 月初 ?"));
    }


}