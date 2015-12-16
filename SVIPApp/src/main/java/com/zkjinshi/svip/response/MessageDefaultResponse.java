package com.zkjinshi.svip.response;

import java.io.Serializable;

/**
 * Created by dujiande on 2015/12/14.
 */
public class MessageDefaultResponse implements Serializable {

    private String desc;
    private String iconbaseurl;
    private String iconfilename;
    private String title;
    private String shopid="";
    private String orderNo;

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIconbaseurl() {
        return iconbaseurl;
    }

    public void setIconbaseurl(String iconbaseurl) {
        this.iconbaseurl = iconbaseurl;
    }

    public String getIconfilename() {
        return iconfilename;
    }

    public void setIconfilename(String iconfilename) {
        this.iconfilename = iconfilename;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
