package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * 销售员信息
 * 开发者：JimmyZhang
 * 日期：2016/3/12
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class SalesVo implements Serializable {

    private String userid;
    private String username;
    private String phone;
    private String userimage;
    private int sex;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }
}
