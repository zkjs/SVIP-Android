package com.zkjinshi.svip.manager;

import android.util.Base64;

/**
 * 开发者：JimmyZhang
 * 日期：2016/2/26
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class SSOManager {

    public static final String TAG = SSOManager.class.getSimpleName();

    private SSOManager(){}

    private static SSOManager instance;

    public synchronized static SSOManager getInstance(){
        if(null == instance){
            instance = new SSOManager();
        }
        return instance;
    }

    public void ssoTest(){

        String tokenStr = "eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiJjXzU2YTZlN2I1NjM0OGMiLCJ0eXBlIjozLCJleHBpcmUiOjE0NTY1NjE5NDI4MzcsInNob3BpZCI6Ijg4ODgiLCJyb2xlcyI6IltdIiwiZmVhdHVyZSI6IltdIn0.gPC-fUdKc-2gLGNvee6J9ZGVLXSJ96iVzZN47MsmO0z3PyQ4BMOq6CxVgIvFKyjeZx1Va_D8wphMSXByK8ppQtcQhPBv-q3CIFby8ttdE3y0yw6RXGrZnJwwusePPXBCAgXG80DtmWPjnjFRS5PVDpB3Ls3RQWPs5bSVTM0HkQ8";
        String[] tokenArr = tokenStr.split(".");
        String payloadEncod = tokenArr[1];

    }

}
