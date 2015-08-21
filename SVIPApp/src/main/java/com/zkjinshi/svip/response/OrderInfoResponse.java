package com.zkjinshi.svip.response;

/**
 * 开发者：JimmyZhang
 * 日期：2015/7/29
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderInfoResponse extends BaseResponse{

    private String reservation_no;
    private int err;

    public String getReservation_no() {
        return reservation_no;
    }

    public void setReservation_no(String reservation_no) {
        this.reservation_no = reservation_no;
    }

    public int getErr() {
        return err;
    }

    public void setErr(int err) {
        this.err = err;
    }
}
