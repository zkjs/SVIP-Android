package com.zkjinshi.svip.response;

/**
 * 商品信息列表响应
 * 开发者：JimmyZhang
 * 日期：2015/7/27
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GoodInfoResponse extends BaseResponse{

    private String id;
    private String shopid;
    private String room;
    private String image;
    private String type;
    private String meat;
    private String price;
    private String logo;
    private String fullname;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMeat() {
        return meat;
    }

    public void setMeat(String meat) {
        this.meat = meat;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
