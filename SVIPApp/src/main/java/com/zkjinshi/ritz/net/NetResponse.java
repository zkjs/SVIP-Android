package com.zkjinshi.ritz.net;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 网络请求响应类
 * 开发者：JimmyZhang
 * 日期：2015/9/25
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class NetResponse implements Serializable{

    /**
     * 返回的原始文本数据
     */
    public transient String rawResult;

    /**
     * 二进制的数据流， 注意：调用者需要负责关闭流！
     */
    public transient InputStream binaryStream;
}
