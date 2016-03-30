package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * 开发者：JimmyZhang
 * 日期：2016/2/26
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class YunBaMsgVo implements Serializable{

    /**
     "title": "欢迎光临(标题)",
     "locid": "1000",
     "alert": "欢迎光临1000区域(提示信息)",
     "shopid": "9002",
     "content": "1000区域是李二狗家不可分割的一部分(完整信息)"
     */

    private String title;
    private String locid;
    private String alert;
    private String shopid;
    private String content;

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
}
