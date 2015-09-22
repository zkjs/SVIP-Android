package com.zkjinshi.svip.http;

import java.util.HashMap;

/**
 * 封装媒体附件请求类
 * 开发者：JimmyZhang
 * 日期：2015/9/17
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MediaRequest {

    public static final String TAG = "MediaRequest";

    /**
     * 请求的完整URL
     */
    public String requestUrl;

    /**
     * 业务层数据Map
     */
    private HashMap<String, String> bizParamMap;

    /**
     * 文件数据Map
     */
    private HashMap<String, String> fileParamMap;

    /**
     * 响应的数据的编码格式，默认为UTF-8
     *
     * @return
     */
    public String responseEncoding = "UTF-8";

    /**
     * 请求等待的过程的提示信息,一般可以留空
     */
    public String waitingPromptMessage;

    /**
     *
     * @param requestUrl
     *            请求的完整URL
     */
    public MediaRequest(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public HashMap<String, String> getFileParamMap() {
        return fileParamMap;
    }

    public void setFileParamMap(HashMap<String, String> fileParamMap) {
        this.fileParamMap = fileParamMap;
    }

    public HashMap<String, String> getBizParamMap() {
        return bizParamMap;
    }

    public void setBizParamMap(HashMap<String, String> bizParamMap) {
        this.bizParamMap = bizParamMap;
    }

}
