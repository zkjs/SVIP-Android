package com.zkjinshi.svip.response;

import com.zkjinshi.svip.vo.CommentVo;

import java.util.ArrayList;

/**
 * 获取评价列表响应实体
 * 开发者：JimmyZhang
 * 日期：2016/3/15
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class CommentsResponse extends BaseFornaxResponse {

    private ArrayList<CommentVo> data;

    public ArrayList<CommentVo> getData() {
        return data;
    }

    public void setData(ArrayList<CommentVo> data) {
        this.data = data;
    }
}
