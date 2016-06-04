package com.zkjinshi.ritz.response;

import com.zkjinshi.ritz.vo.ShopVo;

import java.util.ArrayList;

/**
 * 酒店详情响应实体
 * 开发者：JimmyZhang
 * 日期：2016/4/7
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopDetailResponse extends BaseResponse {

    private ArrayList<ShopVo> data;

    public ArrayList<ShopVo> getData() {
        return data;
    }

    public void setData(ArrayList<ShopVo> data) {
        this.data = data;
    }
}
