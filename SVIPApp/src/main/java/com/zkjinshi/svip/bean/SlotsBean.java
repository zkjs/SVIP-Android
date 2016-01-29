package com.zkjinshi.svip.bean;

import java.io.Serializable;

/**
 * 开发者：WinkyQin
 * 日期：2016/1/29 0029
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SlotsBean implements Serializable{

    public String date;
    public String roomType;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
}
