package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * Created by dujiande on 2016/5/7.
 */
public class AreaVo implements Serializable {

//    "major" : 851,
//            "minior" : 1,
//            "uuid" : "931ddf8e-10e4-11e5-9493-1697f925ec7b",
//            "locdesc" : "收款-大堂吧",
//            "locid" : 851,
//            "floor" : 0,
//            "map" : "map.png",
//            "logo" : "",
//            "brief" : "",
//            "coord_x" : 0,
//            "coord_y" : 0,
//            "video_url": "http://web.zkjinshi.com/zhuanti/video/851"
    private int major;
    private int minior;
    private String uuid;
    private String locdesc;
    private int locid;
    private int floor;
    private String map;
    private String logo;
    private String brief;
    private int coord_x;
    private int coord_y;
    private String video_url;

    public String getKey(){
        return getUuid()+getMajor();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinior() {
        return minior;
    }

    public void setMinior(int minior) {
        this.minior = minior;
    }

    public String getLocdesc() {
        return locdesc;
    }

    public void setLocdesc(String locdesc) {
        this.locdesc = locdesc;
    }

    public int getLocid() {
        return locid;
    }

    public void setLocid(int locid) {
        this.locid = locid;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public int getCoord_x() {
        return coord_x;
    }

    public void setCoord_x(int coord_x) {
        this.coord_x = coord_x;
    }

    public int getCoord_y() {
        return coord_y;
    }

    public void setCoord_y(int coord_y) {
        this.coord_y = coord_y;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }
}
