package com.zkjinshi.svip.vo;

/**
 * 开发者：WinkyQin
 * 日期：2015/12/29
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CommentVo {

    /**
     "id": "123",
     "userid": "c_iou32jlxz",    //评价用户userid
     "username": "测试",   //评价用户姓名
     "score": 5, //评价分散
     "content": "便宜实惠",  //评价内容
     "createtime": "2016-02-01 08:00:00" //评价时间
     */

    private String id;
    private String userid;
    private String username;
    private float score;
    private String content;
    private String createtime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
}
