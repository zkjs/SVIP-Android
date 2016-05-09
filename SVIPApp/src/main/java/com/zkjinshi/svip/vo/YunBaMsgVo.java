package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * 开发者：JimmyZhang
 * 日期：2016/2/26
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class YunBaMsgVo implements Serializable{

    private int id;
    private String title;
    private String locid;
    private String alert;
    private String shopid;
    private String content;
    private String button;
    private String img_url;
    private String button_url;
    private long insert_time;
    private boolean hasLook = false; //是否已经被看过

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isHasLook() {
        return hasLook;
    }

    public void setHasLook(boolean hasLook) {
        this.hasLook = hasLook;
    }

    public long getInsert_time() {
        return insert_time;
    }

    public void setInsert_time(long insert_time) {
        this.insert_time = insert_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocid() {
        return locid;
    }

    public void setLocid(String locid) {
        this.locid = locid;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getButton_url() {
        return button_url;
    }

    public void setButton_url(String button_url) {
        this.button_url = button_url;
    }
}
