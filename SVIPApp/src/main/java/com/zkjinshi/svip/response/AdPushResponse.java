package com.zkjinshi.svip.response;

import java.io.Serializable;

/**
 * 开发者：dujiande
 * 日期：2015/11/11
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class AdPushResponse implements Serializable{
    /*
       "count": 1, 广告条数
    "start_datetime": "2015年10月11日14:23:38",  广告开始时间
    "end_datetime": "2015年12月11日14:23:55",    广告结束时间
    "ad_title": "测试推送广告1",                  广告标题
    "url": "http://api.zkjinshi.com/ad/index1.html" 广告链接
     */

    private int count;
    private String start_datetime;
    private String end_datetime;
    private String ad_title;
    private String url;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getStart_datetime() {
        return start_datetime;
    }

    public void setStart_datetime(String start_datetime) {
        this.start_datetime = start_datetime;
    }

    public String getEnd_datetime() {
        return end_datetime;
    }

    public void setEnd_datetime(String end_datetime) {
        this.end_datetime = end_datetime;
    }

    public String getAd_title() {
        return ad_title;
    }

    public void setAd_title(String ad_title) {
        this.ad_title = ad_title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
