package com.zkjinshi.jni.aes;

/**
 * AES秘钥管理器
 * 开发者：JimmyZhang
 * 日期：2016/3/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class AESManager {

    private AESManager(){}

    private static AESManager instance;

    public synchronized static AESManager getInstance(){
        if(null == instance){
            instance = new AESManager();
        }
        return instance;
    }

    static{
        System.loadLibrary("aes-jni");
    }

    /**
     * 获取AES解密的key
     * @return
     */
    public native String getEncryptKey();

}
