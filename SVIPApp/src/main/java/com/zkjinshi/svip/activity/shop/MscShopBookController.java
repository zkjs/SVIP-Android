package com.zkjinshi.svip.activity.shop;

/**
 * 语音订房控制器
 * 开发者：WinkyQin
 * 日期：2016/1/29 0029
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MscShopBookController {

    private static MscShopBookController instance;

    public static synchronized MscShopBookController getInstance(){
        if(null == instance){
            instance = new MscShopBookController();
        }
        return instance;
    }

}
