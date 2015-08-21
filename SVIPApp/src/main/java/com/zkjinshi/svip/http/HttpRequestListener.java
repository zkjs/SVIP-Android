package com.zkjinshi.svip.http;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public interface HttpRequestListener <T extends HttpResponse>{

    /**
     * 当网络请求或者响应数据解析时发生的错误回调
     * @param errorCode
     * @param errorMessage 错误的详情信息
     */
    public void onNetworkRequestError(int errorCode, String errorMessage);

    /**
     * 当网络请求被取消时回调
     */
    public void onNetworkRequestCancelled();

    /**
     * 数据解析成功后的回调
     * @param <T> 继承自XMasResponse的具体类型
     */
    public void onNetworkResponseSucceed(T result);

}
