package com.zkjinshi.svip.response;

import java.io.Serializable;

/**
 * Created by dujiande on 2015/12/19.
 */
public class MessageCardResponse implements Serializable {
    /**
     * content: "长沙豪廷大酒店,无早升单早、单早升双早;请联系商家使用",
     title: "获得早餐升级特权"
     */

    private String content;
    private String title;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
