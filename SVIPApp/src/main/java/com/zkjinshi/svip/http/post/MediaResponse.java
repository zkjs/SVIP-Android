package com.zkjinshi.svip.http.post;

import java.io.InputStream;

/**
 * 封装媒体附件响应类
 * 开发者：JimmyZhang
 * 日期：2015/9/17
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MediaResponse {

    /**
     * 返回的原始文本数据
     */
    public transient String rawResult;

    /**
     * 二进制的数据流， 注意：调用者需要负责关闭流！
     */
    public transient InputStream binaryStream;
}
