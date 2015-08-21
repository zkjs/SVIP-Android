package com.zkjinshi.svip.factory;

import android.content.ContentValues;
import android.database.Cursor;
import com.zkjinshi.svip.vo.ShopDetailVo;

/**
 * 用户详细信息操作工厂类
 * 开发者：vincent
 * 日期：2015/8/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopDetailFactory {

    private ShopDetailFactory(){}

    private static ShopDetailFactory instance;

    public static synchronized ShopDetailFactory getInstance(){
        if(null == instance){
            instance = new ShopDetailFactory();
        }
        return instance;
    }

    /**
     * 根据详细用户信息生成contentValues
     * @param shopDetailVo
     * @return
     */
    public ContentValues buildContentValues(ShopDetailVo shopDetailVo) {
        ContentValues values = new ContentValues();
        values.put("shopid", shopDetailVo.getShopid());
        values.put("shopcode", shopDetailVo.getShopcode());
        values.put("logo", shopDetailVo.getLogo());
        values.put("fullname", shopDetailVo.getFullname());
        values.put("known_as", shopDetailVo.getKnown_as());
        values.put("location", shopDetailVo.getLocation());
        values.put("countrycode", shopDetailVo.getCountrycode());
        values.put("province", shopDetailVo.getProvince());
        values.put("telcode", shopDetailVo.getTelcode());
        values.put("city", shopDetailVo.getCity());
        values.put("postcode", shopDetailVo.getPostcode());
        values.put("fulladdrs", shopDetailVo.getFulladdrs());
        values.put("phone", shopDetailVo.getPhone());
        values.put("fax", shopDetailVo.getFax());
        values.put("email", shopDetailVo.getEmail());
        values.put("url", shopDetailVo.getUrl());
        values.put("wechart", shopDetailVo.getWechart());
        values.put("weibo", shopDetailVo.getWeibo());
        values.put("others", shopDetailVo.getOthers());
        values.put("annualincome", shopDetailVo.getAnnualincome());
        values.put("staffnumber", shopDetailVo.getStaffnumber());
        values.put("industry", shopDetailVo.getIndustry());
        values.put("type", shopDetailVo.getType());
        values.put("cstsource", shopDetailVo.getCstsource());
        values.put("status", shopDetailVo.getStatus());
        values.put("is_onlinepay", shopDetailVo.getIs_onlinepay());
        values.put("curr_code", shopDetailVo.getCurr_code());
        values.put("work_begin", shopDetailVo.getWork_begin());
        values.put("work_end", shopDetailVo.getWork_end());
        values.put("created", shopDetailVo.getCreated());
        values.put("map_longitude", shopDetailVo.getMap_longitude());
        values.put("map_latitude", shopDetailVo.getMap_latitude());
        values.put("shop_desc", shopDetailVo.getShop_desc());
        values.put("shop_recomm", shopDetailVo.getShop_recomm());
        values.put("shop_bg", shopDetailVo.getShop_bg());
        values.put("shop_title", shopDetailVo.getShop_title());
        return values;
    }

    /**
     * 根据游标创建具体用户对象
     * @param cursor
     * @return
     */
    public ShopDetailVo buildshopDetailVoByCursor(Cursor cursor) {
        //新建具体用户对象
        ShopDetailVo shopDetailVo = new ShopDetailVo();
        shopDetailVo.setShopid(cursor.getString(0));
        shopDetailVo.setShopcode(cursor.getString(1));
        shopDetailVo.setLogo(cursor.getString(2));
        shopDetailVo.setFullname(cursor.getString(3));
        shopDetailVo.setKnown_as(cursor.getString(4));
        shopDetailVo.setLocation(cursor.getString(5));
        shopDetailVo.setCountrycode(cursor.getString(6));
        shopDetailVo.setProvince(cursor.getString(7));
        shopDetailVo.setTelcode(cursor.getString(8));
        shopDetailVo.setCity(cursor.getString(9));
        shopDetailVo.setPostcode(cursor.getString(10));
        shopDetailVo.setFulladdrs(cursor.getString(11));
        shopDetailVo.setPhone(cursor.getString(12));
        shopDetailVo.setFax(cursor.getString(13));
        shopDetailVo.setEmail(cursor.getString(14));
        shopDetailVo.setUrl(cursor.getString(15));
        shopDetailVo.setWechart(cursor.getString(16));
        shopDetailVo.setWeibo(cursor.getString(17));
        shopDetailVo.setOthers(cursor.getString(18));
        shopDetailVo.setAnnualincome(cursor.getString(19));
        shopDetailVo.setStaffnumber(cursor.getString(20));
        shopDetailVo.setIndustry(cursor.getString(21));
        shopDetailVo.setType(cursor.getString(22));
        shopDetailVo.setCstsource(cursor.getString(23));
        shopDetailVo.setStatus(cursor.getString(24));
        shopDetailVo.setIs_onlinepay(cursor.getString(25));
        shopDetailVo.setCurr_code(cursor.getString(26));
        shopDetailVo.setWork_begin(cursor.getString(27));
        shopDetailVo.setWork_end(cursor.getString(28));
        shopDetailVo.setCreated(cursor.getString(29));
        shopDetailVo.setMap_longitude(cursor.getString(30));
        shopDetailVo.setMap_latitude(cursor.getString(31));
        shopDetailVo.setShop_desc(cursor.getString(32));
        shopDetailVo.setShop_recomm(cursor.getString(33));
        shopDetailVo.setShop_bg(cursor.getString(34));
        shopDetailVo.setShop_title(cursor.getString(35));
        return shopDetailVo;
    }

}
