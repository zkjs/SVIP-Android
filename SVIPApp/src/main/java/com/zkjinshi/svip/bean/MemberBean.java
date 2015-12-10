package com.zkjinshi.svip.bean;

import java.io.Serializable;

/**
 * 开发者：JimmyZhang
 * 日期：2015/12/10
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MemberBean implements Serializable {

    private String userid;
    private String username;
    private int sex;
    private String user_desc;
    private String phone;
    private int user_applevel;

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

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getUser_desc() {
        return user_desc;
    }

    public void setUser_desc(String user_desc) {
        this.user_desc = user_desc;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getUser_applevel() {
        return user_applevel;
    }

    public void setUser_applevel(int user_applevel) {
        this.user_applevel = user_applevel;
    }
}
