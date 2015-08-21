package com.zkjinshi.svip.http;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class HttpRequest implements Serializable {

    private String requestMethod;
    private String requestUrl;
    private Map<String,String> stringParamMap;
    private Map<String,File> fileParamMap;

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Map<String, String> getStringParamMap() {
        return stringParamMap;
    }

    public void setStringParamMap(Map<String, String> stringParamMap) {
        this.stringParamMap = stringParamMap;
    }

    public Map<String, File> getFileParamMap() {
        return fileParamMap;
    }

    public void setFileParamMap(Map<String, File> fileParamMap) {
        this.fileParamMap = fileParamMap;
    }

}
