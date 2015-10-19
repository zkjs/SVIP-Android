package com.zkjinshi.svip.factory;

import android.content.ContentValues;

import com.zkjinshi.svip.response.ShopInfoResponse;
import com.zkjinshi.svip.vo.ServerPersonalVo;
import com.zkjinshi.svip.vo.ShopDetailVo;
import com.zkjinshi.svip.vo.ShopInfoVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：dujiande
 * 日期：2015/10/19
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ServerPersonalFactory {

    private ServerPersonalFactory(){}
    private static ServerPersonalFactory instance;
    public static synchronized ServerPersonalFactory getInstance(){
        if(null == instance){
            instance = new ServerPersonalFactory();
        }
        return instance;
    }

    /**
     * 根据信息生成contentValues
     * @param serverPersonalVo
     * @return
     */
    public ContentValues buildContentValues(ServerPersonalVo serverPersonalVo) {
        ContentValues values = new ContentValues();
        values.put("shopid", serverPersonalVo.getShopid());
        values.put("salesid", serverPersonalVo.getSalesid());
        return values;
    }
    
}
