package com.zkjinshi.base.net.core;

import org.json.JSONObject;

/**
 * 网络状态封装类
 * 开发者:赖清泉
 * 日期：2015/7/19
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SocketStatus {
    public final String TAG = "SockjetStatus";

    public final static int WEBSOCKET_CONNECT_SUCCESS = 1;
    public final static int WEBSOCKET_EOF = 10;
    public final static int WEBSOCKET_SSL_ERROR = 11;
    public final static int WEBSOCKET_TEXT_READ = 30;
    public final static int WEBSOCKET_BYTE_READ = 31;
    public final static int WEBSOCKET_SEND_RES = 40;
    public final static int WEBSOCKET_SEND_TIMEOUT = 50;

    private int code;
    private JSONObject msg = null;

    public SocketStatus(int code, JSONObject msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public JSONObject getMsg() {
        return msg;
    }
}
