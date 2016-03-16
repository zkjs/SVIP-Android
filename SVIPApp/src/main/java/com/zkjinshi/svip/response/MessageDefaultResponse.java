package com.zkjinshi.svip.response;

import java.io.Serializable;

/**
 * Created by dujiande on 2015/12/14.
 */
public class MessageDefaultResponse implements Serializable {

    private String desc;
    private String iconfilename;
    private String shopName;
    private String shopid="";
    private String title;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIconfilename() {
        return iconfilename;
    }

    public void setIconfilename(String iconfilename) {
        this.iconfilename = iconfilename;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
