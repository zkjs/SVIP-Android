package com.zkjinshi.svip.menu.vo;

import java.io.Serializable;

/**
 * 菜单项
 * 开发者：JimmyZhang
 * 日期：2015/8/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MenuItem extends Menu  implements Serializable {

    private int itemId;
    private String icon;
    private String shopId;
    private String url;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
