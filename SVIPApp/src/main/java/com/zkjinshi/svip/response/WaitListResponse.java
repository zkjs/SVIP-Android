package com.zkjinshi.svip.response;

import com.blueware.agent.android.A;
import com.zkjinshi.svip.vo.WaiterVo;

import java.util.ArrayList;

/**
 * 服务员列表响应实体
 * 开发者：JimmyZhang
 * 日期：2016/4/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class WaitListResponse extends BaseResponse {

    private ArrayList<WaiterVo> data;

    public ArrayList<WaiterVo> getData() {
        return data;
    }

    public void setData(ArrayList<WaiterVo> data) {
        this.data = data;
    }
}
