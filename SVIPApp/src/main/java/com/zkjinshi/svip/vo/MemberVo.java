package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * 群成员实体
 * 开发者：JimmyZhang
 * 日期：2015/7/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MemberVo implements Serializable{
    /**
     "userid": "c_ae0f4570a5a9fd55",
     "username": "林2哈哈",
     "phone": "13560727890",
     "password": null,
     "realname": null,
     "userimage": "uploads/users/c_ae0f4570a5a9fd55.jpg",
     "sex": 1,
     "email": null,
     "userstatus": 0,
     "viplevel": 0
     */
    private String userid;
    private String username;
    private String phone;
    private String userimage;
    private int sex;
    private String email;
    private int viplevel;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getViplevel() {
        return viplevel;
    }

    public void setViplevel(int viplevel) {
        this.viplevel = viplevel;
    }
}
