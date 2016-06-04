package com.zkjinshi.ritz.vo;

import com.blueware.agent.android.harvest.P;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 商家描述模板
 * 开发者：JimmyZhang
 * 日期：2016/4/7
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopModeVo implements Serializable{

    private String title;
    private String body;
    private String shopid;
    private String modid;
    private String sort;
    private ArrayList<String> photos;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getModid() {
        return modid;
    }

    public void setModid(String modid) {
        this.modid = modid;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }
}
