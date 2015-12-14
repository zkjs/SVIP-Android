package com.zkjinshi.svip.response;

import java.io.Serializable;

/**
 * Created by dujiande on 2015/12/14.
 */
public class BigPicResponse implements Serializable {

    private String name;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
