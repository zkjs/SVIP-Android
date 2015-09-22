package com.zkjinshi.svip.http.post;

/**
 * 媒体状态响应封装类
 * 开发者：JimmyZhang
 * 日期：2015/9/17
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MediaStatusMsg extends MediaResponse {

    public static final String CHANNEL_NOTINIT_ID = "3001";
    public static final String CHANNEL_NOTINIT_NAME = "ChannelNotInitComplete";

    public static final String HTTPREQUEST_NULL_ID = "3002";
    public static final String HTTPREQUEST_NULL_NAME = "HttpRequestIsNull";

    public static final String STRINGBUILDER_NULL_ID = "3003";
    public static final String STRINGBUILDER_NULL_NAME = "StringBuilderIsNull";

    public static final String BLACKLIST_FOUND_ID = "3004";
    public static final String BLACKLIST_FOUND_NAME = "FoundInBlackList";

    public static final String SERVICE_LOADING_ID = "3005";
    public static final String SERVICE_LOADING_NAME = "ServiceIsLoading";

    public static final String PARSER_LOADING_ID = "3006";
    public static final String PARSER_LOADING_NAME = "ParserIsLoading";

    public static final String FUNCTION_NOTFOUND_ID = "4001";
    public static final String FUNCTION_NOTFOUND_NAME = "FunctionNotFound";

    public static final String PROTOCOL_NOTFOUND_ID = "4002";
    public static final String PROTOCOL_NOTFOUND_NAME = "ProtocolNotFound";

    public static final String SESSION_NOTFOUND_ID = "4003";
    public static final String SESSION_NOTFOUND_NAME = "SessionNotFound";

    public static final String OUTLET_NETWORK_ERROR_ID = "4004";
    public static final String OUTLET_NETWORK_ERROR_NAME = "OutletNetworkError";

    public static final String PARAMETER_INVALID_ID = "4005";
    public static final String PARAMETER_INVALID_NAME = "ParameterInvalid";

    public static final String FILE_NOTFOUND_ID = "4006";
    public static final String FILE_NOTFOUND_NAME = "FileNotFound";

    public static final String KEYCODE_ERROR_ID = "4007";
    public static final String KEYCODE_ERROR_NAME = "KeyCodeError";

    public static final String HANDLE_SUCCESS_ID = "5001";
    public static final String HANDLE_SUCCESS = "success";

    public static final String HANDLE_FAILURE_ID = "5002";
    public static final String HANDLE_FAILURE = "failure";

    public static final int JSON_RESPONSE = 1;
    public static final int FILE_RESPONSE = 2;

    public static final int STATUS_SUCCESS = 200;
    public static final int STATUS_FAILURE = 404;

    public int code;
    public String message;
    public String name;
}
