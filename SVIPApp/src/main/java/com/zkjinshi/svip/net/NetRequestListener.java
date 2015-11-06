package com.zkjinshi.svip.net;

/**
 * 网络请求监听器
 * 开发者：JimmyZhang
 * 日期：2015/9/25
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public interface NetRequestListener {

    /**
     * 当网络请求或者响应数据解析时发生的错误回调
     * @param errorCode 参考{@code NetworkRequestTask} 常量定义
     * @param errorMessage 错误的详情信息
     */
    public void onNetworkRequestError(int errorCode, String errorMessage);

    /**
     * 当网络请求被取消时回调
     */
    public void onNetworkRequestCancelled();

    /**
     * 网络请求没有发生错误， 并且数据解析成功后的回调
     * @param result
     */
    public void onNetworkResponseSucceed(NetResponse result);

    /**
     * 网络请求发生前 的回调
     */
    public void beforeNetworkRequestStart();

}
