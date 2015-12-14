package com.zkjinshi.svip.response;

import java.io.Serializable;

/**
 * Created by dujiande on 2015/12/14.
 */
public class MessageDefaultResponse implements Serializable {

    private String desc;
    private String iconbaseurl;
    private String iconfilename;
    private String title;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIconbaseurl() {
        return iconbaseurl;
    }

    public void setIconbaseurl(String iconbaseurl) {
        this.iconbaseurl = iconbaseurl;
    }

    public String getIconfilename() {
        return iconfilename;
    }

    public void setIconfilename(String iconfilename) {
        this.iconfilename = iconfilename;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
