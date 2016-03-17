package com.zkjinshi.svip.response;

import java.io.Serializable;

/**
 * Created by dujiande on 2015/12/18.
 */
public class PrivilegeResponse implements Serializable {

/*             "icon": "",
            "privilegedesc": "您可以免费将已预订房型升级高一级别房型",
            "shopname": "中科金石test",
            "title": "房型免费升级"*/

    private String icon;
    private String privilegedesc;
    private  String shopname;
    private String title;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPrivilegedesc() {
        return privilegedesc;
    }

    public void setPrivilegedesc(String privilegedesc) {
        this.privilegedesc = privilegedesc;
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
