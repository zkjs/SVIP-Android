package com.zkjinshi.svip.factory;

import com.zkjinshi.svip.response.GoodInfoResponse;
import com.zkjinshi.svip.response.ShopInfoResponse;
import com.zkjinshi.svip.vo.GoodInfoVo;
import com.zkjinshi.svip.vo.ShopInfoVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/27
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GoodInfoFactory {
    private GoodInfoFactory(){}
    private static GoodInfoFactory instance;
    public synchronized static GoodInfoFactory getInstance(){
        if(null ==  instance){
            instance = new GoodInfoFactory();
        }
        return  instance;
    }

    /**
     * 构建商品信息实体
     * @param goodInfoResponse
     * @return
     */
    public GoodInfoVo buildGoodInfo(GoodInfoResponse goodInfoResponse){
        GoodInfoVo goodInfoVo = new GoodInfoVo();
        goodInfoVo.setId(goodInfoResponse.getId());
        goodInfoVo.setImgurl(goodInfoResponse.getImgurl());
        goodInfoVo.setMeat(goodInfoResponse.getMeat());
        goodInfoVo.setPrice(goodInfoResponse.getPrice());
        goodInfoVo.setRoom(goodInfoResponse.getRoom());
        goodInfoVo.setType(goodInfoResponse.getType());
        return  goodInfoVo;
    }

    /**
     * 构建商品信息列表
     * @param goodResponseList
     * @return
     */
    public ArrayList<GoodInfoVo> bulidGoodList(ArrayList<GoodInfoResponse> goodResponseList){
        ArrayList<GoodInfoVo> goodInfoList = new ArrayList<GoodInfoVo>();
        for (GoodInfoResponse goodResponse : goodResponseList){
            goodInfoList.add(buildGoodInfo(goodResponse));
        }
        return goodInfoList;
    }
}
