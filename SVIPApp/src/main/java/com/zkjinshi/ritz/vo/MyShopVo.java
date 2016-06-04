package com.zkjinshi.ritz.vo;

import java.io.Serializable;

/**
 * Created by dujiande on 2016/5/24.
 */
public class MyShopVo implements Serializable {

    private String shopid;
    private String shopname;
    private String shoplogo;

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getShoplogo() {
        return shoplogo;
    }

    public void setShoplogo(String shoplogo) {
        this.shoplogo = shoplogo;
    }
}
