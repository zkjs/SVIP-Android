package com.zkjinshi.svip.factory;

import android.content.ContentValues;

import com.zkjinshi.svip.activity.city.citylist.CityModel;

import com.zkjinshi.svip.response.PrivilegeResponse;
import com.zkjinshi.svip.vo.GoodInfoVo;

import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/27
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class PrivilegeFactory {
    private PrivilegeFactory(){}
    private static PrivilegeFactory instance;
    public synchronized static PrivilegeFactory getInstance(){
        if(null ==  instance){
            instance = new PrivilegeFactory();
        }
        return  instance;
    }

    public ContentValues buildUpdateContentValues(PrivilegeResponse privilegeResponse, String shopid) {
        ContentValues values = new ContentValues();
        values.put("privilegeDesc", privilegeResponse.getPrivilegedesc());
        values.put("privilegeIcon", privilegeResponse.getIcon());
        values.put("privilegeName",privilegeResponse.getTitle());
        values.put("shopName",privilegeResponse.getShopname());
        values.put("shopid", shopid);
        return values;
    }


}
