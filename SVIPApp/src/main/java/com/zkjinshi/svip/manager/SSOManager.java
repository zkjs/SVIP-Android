package com.zkjinshi.svip.manager;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.zkjinshi.base.util.Constants;
import com.zkjinshi.svip.utils.Base64Decoder;
import com.zkjinshi.svip.vo.PayloadVo;

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

    /**
     * 获取token的负载信息
     * @param tokenStr
     * @return
     */
    public PayloadVo decodeToken(String tokenStr){
        //String tokenStr = "eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiJjXzU2YTZlN2I1NjM0OGMiLCJ0eXBlIjozLCJleHBpcmUiOjE0NTY1NjE5NDI4MzcsInNob3BpZCI6Ijg4ODgiLCJyb2xlcyI6IltdIiwiZmVhdHVyZSI6IltdIn0.gPC-fUdKc-2gLGNvee6J9ZGVLXSJ96iVzZN47MsmO0z3PyQ4BMOq6CxVgIvFKyjeZx1Va_D8wphMSXByK8ppQtcQhPBv-q3CIFby8ttdE3y0yw6RXGrZnJwwusePPXBCAgXG80DtmWPjnjFRS5PVDpB3Ls3RQWPs5bSVTM0HkQ8";
        String[] tokenArr = tokenStr.split("\\.");
        PayloadVo payloadVo = null;
        if(null != tokenArr && tokenArr.length > 0){
            String payloadEncode = tokenArr[1];
            String payloadDecode = Base64Decoder.decode(payloadEncode);
            Log.i(Constants.ZKJINSHI_BASE_TAG,"payloadDecode:"+payloadDecode);
            payloadVo = new Gson().fromJson(payloadDecode,PayloadVo.class);
        }
        return payloadVo;
    }

}
