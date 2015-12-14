package com.zkjinshi.svip.response;

import java.io.Serializable;

/**
 * Created by dujiande on 2015/12/14.
 */
public class ShopRecommendedResponse implements Serializable {

    private String recommend_content;
    private String recommend_logo;
    private String recommend_title;
    private String shopid;

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getRecommend_content() {
        return recommend_content;
    }

    public void setRecommend_content(String recommend_content) {
        this.recommend_content = recommend_content;
    }

    public String getRecommend_logo() {
        return recommend_logo;
    }

    public void setRecommend_logo(String recommend_logo) {
        this.recommend_logo = recommend_logo;
    }

    public String getRecommend_title() {
        return recommend_title;
    }

    public void setRecommend_title(String recommend_title) {
        this.recommend_title = recommend_title;
    }
}
