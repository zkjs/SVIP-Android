package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * 服务员实体
 * 开发者：JimmyZhang
 * 日期：2016/4/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class WaiterVo implements Serializable {

    /**
     "locid": "821",     //区域编号
     "locdesc" "",       //区域描述
     "userid": "b_32891281212",       //用户编号
     "username": "超级服务员",     //用户名称
     "userimage": "/uploads/test.jpg",       //用户头像
     "sex": "1",     //性别
     "shopid": "2009",       //商家编号
     "arrivetime": "2016-04-20 12:00:00",       //最近在其区域的时间
     "validthru": "2016-04-20 12:05:00"    //服务过期时间
     */

    private String locid;
    private String locdesc;
    private String userid;
    private String username;
    private String userimage;
    private String sex;
    private String shopid;
    private String arrivetime;
    private String validthru;

    public String getLocid() {
        return locid;
    }

    public void setLocid(String locid) {
        this.locid = locid;
    }

    public String getLocdesc() {
        return locdesc;
    }

    public void setLocdesc(String locdesc) {
        this.locdesc = locdesc;
    }

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

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getArrivetime() {
        return arrivetime;
    }

    public void setArrivetime(String arrivetime) {
        this.arrivetime = arrivetime;
    }

    public String getValidthru() {
        return validthru;
    }

    public void setValidthru(String validthru) {
        this.validthru = validthru;
    }
}
