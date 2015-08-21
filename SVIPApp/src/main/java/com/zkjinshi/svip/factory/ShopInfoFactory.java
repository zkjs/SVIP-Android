package com.zkjinshi.svip.factory;

import com.zkjinshi.svip.response.ShopInfoResponse;
import com.zkjinshi.svip.vo.ShopInfoVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopInfoFactory {
    private ShopInfoFactory(){}
    private static ShopInfoFactory instance;
    public static synchronized ShopInfoFactory getInstance(){
        if(null == instance){
            instance = new ShopInfoFactory();
        }
        return instance;
    }

    public ShopInfoVo buildShopInfo(ShopInfoResponse shopInfoResponse){
        ShopInfoVo shopInfoVo = new ShopInfoVo();
        shopInfoVo.setFullname(shopInfoResponse.getFullname());
        shopInfoVo.setShopid(shopInfoResponse.getShopid());
        shopInfoVo.setLogo(shopInfoResponse.getLogo());
        return  shopInfoVo;
    }

    public List<ShopInfoVo> bulidShopList(List<ShopInfoResponse> shopResponseList){
        List<ShopInfoVo> shopInfoList = new ArrayList<ShopInfoVo>();
        for (ShopInfoResponse shopResponse : shopResponseList){
            shopInfoList.add(buildShopInfo(shopResponse));
        }
        return shopInfoList;
    }
}
