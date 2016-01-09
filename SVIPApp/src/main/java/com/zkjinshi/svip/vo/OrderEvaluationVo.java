package com.zkjinshi.svip.vo;

/**
 * 订单评价实体
 * 开发者：JimmyZhang
 * 日期：2016/1/9
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderEvaluationVo {

    private String orderNo;
    private Integer score;
    private String content;
    private String userid;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

}
