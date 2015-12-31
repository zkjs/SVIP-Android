package com.zkjinshi.svip.net;

import java.io.File;
import java.util.HashMap;

/**
 * 网络请求参数类
 * 开发者：JimmyZhang
 * 日期：2015/9/25
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class NetRequest {

    public static final String TAG = "NetRequest";

    /**
     * 请求的完整URL
     */
    public String requestUrl;

    /**
     * 业务层数据Map
     */
    private HashMap<String, String> bizParamMap;

    /**
     * 业务层数据Map
     */
    private HashMap<String, Object> objectParamMap;

    /**
     * 上传文件路径Map
     */
    private HashMap<String, String> fileParamMap;

    /**
     * 文件Map
     */
    private HashMap<String,File> fileMap;

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
    public NetRequest(String requestUrl) {
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

    public HashMap<String, File> getFileMap() {
        return fileMap;
    }

    public void setFileMap(HashMap<String, File> fileMap) {
        this.fileMap = fileMap;
    }

    public HashMap<String, Object> getObjectParamMap() {
        return objectParamMap;
    }

    public void setObjectParamMap(HashMap<String, Object> objectParamMap) {
        this.objectParamMap = objectParamMap;
    }
}
