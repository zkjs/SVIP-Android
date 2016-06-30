package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * Created by dujiande on 2016/6/29.
 */
public class InvitationVo implements Serializable {

    /*
     "alert": "您有一条活动邀请消息", //通知消息
            "actid": "S9E44U",   //邀请活动id
            "actname": "拍卖会邀请",  //活动主题
            "actcontent": "君子汇邀请您参加齐白石画作拍卖会", //活动描述
            "acturl": "http://ea.zkjinshi.com",   //活动详情链接
            "actimage": "/uploads/head.png",  //活动图片
            "startdate": "2016-07-01 06:00:00", //活动开始时间
            "enddate": "2016-07-01 09:00:00", //活动结束时间
            "maxtake": 3	//携带人数上限
     */

    private String alert;
    private String actid;
    private String actname;
    private String actcontent;
    private String acturl;
    private String actimage;
    private String startdate;
    private String enddate;
    private int maxtake;
    private boolean hasLook = false;
    private long insert_time;

    public String getActname() {
        return actname;
    }

    public void setActname(String actname) {
        this.actname = actname;
    }

    public boolean isHasLook() {
        return hasLook;
    }

    public void setHasLook(boolean hasLook) {
        this.hasLook = hasLook;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getActid() {
        return actid;
    }

    public void setActid(String actid) {
        this.actid = actid;
    }

    public String getActcontent() {
        return actcontent;
    }

    public void setActcontent(String actcontent) {
        this.actcontent = actcontent;
    }

    public String getActurl() {
        return acturl;
    }

    public void setActurl(String acturl) {
        this.acturl = acturl;
    }

    public String getActimage() {
        return actimage;
    }

    public void setActimage(String actimage) {
        this.actimage = actimage;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public int getMaxtake() {
        return maxtake;
    }

    public void setMaxtake(int maxtake) {
        this.maxtake = maxtake;
    }

    public long getInsert_time() {
        return insert_time;
    }

    public void setInsert_time(long insert_time) {
        this.insert_time = insert_time;
    }
}
