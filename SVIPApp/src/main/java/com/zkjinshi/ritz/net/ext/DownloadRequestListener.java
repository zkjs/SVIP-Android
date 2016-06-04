package com.zkjinshi.ritz.net.ext;

/**
 * 下载请求回调
 * 开发者：JimmyZhang
 * 日期：2015/9/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public interface DownloadRequestListener {

    /**
     * 当下载请求或者响应数据解析时发生的错误回调
     * @param errorMessage 错误的详情信息
     */
    public void onDownloadRequestError(String errorMessage);

    /**
     * 当下载请求被取消时回调
     */
    public void onDownloadRequestCancelled();

    /**
     * 下载请求没有发生错误， 并且数据解析成功后的回调
     * @param filePath
     */
    public void onDownloadResponseSucceed(String filePath);

    /**
     * 下载请求发生前的回调
     */
    public void beforeDownloadRequestStart();

}
