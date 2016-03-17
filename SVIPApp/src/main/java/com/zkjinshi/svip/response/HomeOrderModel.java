package com.zkjinshi.svip.response;

import java.io.Serializable;

/**
 * Created by dujiande on 2016/3/16.
 */
public class HomeOrderModel implements Serializable {

/*            "icon": "order-muti.png",
            "orderno": "H2435074859832749791,H2435074859832749111",
            "shopname": "中科金石test,中科金石test",
            "title": "您 03 月 15 日有一个订单开始"*/

    private String icon;
    private String orderno;
    private String shopname;
    private String title;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
