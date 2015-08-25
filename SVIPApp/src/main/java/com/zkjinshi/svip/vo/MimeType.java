package com.zkjinshi.svip.vo;

/**
 * 文件类型枚举类
 * 开发者：JimmyZhang
 * 日期：2015/7/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public enum MimeType {

    TEXT(0),//文本
    AUDIO(1),//语音
    IMAGE(2),//图片
    VIDEO(3),//视频
    APPLICATION(4);//文件
    private MimeType(int value){
        this.value = value;
    }
    private final int value;
    public int getVlaue(){
        return value;
    }
}
