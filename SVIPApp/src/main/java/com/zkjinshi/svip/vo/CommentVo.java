package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * Created by dujiande on 2016/5/7.
 */
public class CommentVo implements Serializable {

    private String avatarUrl;
    private String name;
    private long timestamp;
    private String comment;
    private String area_key;

    public String getArea_key() {
        return area_key;
    }

    public void setArea_key(String area_key) {
        this.area_key = area_key;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
