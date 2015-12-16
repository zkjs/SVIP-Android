package com.zkjinshi.svip.bean;

/**
 * 推荐商家对象
 * 开发者：WinkyQin
 * 日期：2015/12/16
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class RecommendShopBean extends BaseShopBean {

    private String link_url;
    private String recommend_content;
    private String recommend_logo;
    private String recommend_title;
    private String shop_bgimgurl;

    public String getLink_url() {
        return link_url;
    }

    public void setLink_url(String link_url) {
        this.link_url = link_url;
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

    public String getRecommend_content() {
        return recommend_content;
    }

    public void setRecommend_content(String recommend_content) {
        this.recommend_content = recommend_content;
    }

    public String getShop_bgimgurl() {
        return shop_bgimgurl;
    }

    public void setShop_bgimgurl(String shop_bgimgurl) {
        this.shop_bgimgurl = shop_bgimgurl;
    }

}
