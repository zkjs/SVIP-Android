package com.zkjinshi.svip.response;

import com.zkjinshi.svip.vo.ShopVo;

import java.util.ArrayList;

/**
 * 商家详情响应实体
 * 开发者：JimmyZhang
 * 日期：2016/3/15
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopDetailResponse extends BaseFornaxResponse {

    private ArrayList<ShopVo> data;

    public ArrayList<ShopVo> getData() {
        return data;
    }

    public void setData(ArrayList<ShopVo> data) {
        this.data = data;
    }
}
