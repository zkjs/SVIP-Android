package com.zkjinshi.svip.response;

/**
 * 商品信息列表响应
 * 开发者：JimmyZhang
 * 日期：2015/7/27
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GoodInfoResponse extends BaseResponse{

    /**
     "id": "1",
     "room": "房型",
     "type": "床床",
     "imgurl": "房型图片url",
     "meat": "无早",  早餐
     "price": "100.00" 房型价格

     */

    private String id;
    private String room;
    private String type;
    private String imgurl;
    private String meat;
    private String price;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getMeat() {
        return meat;
    }

    public void setMeat(String meat) {
        this.meat = meat;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
