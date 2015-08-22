package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * Created by djd on 2015/8/21.
 */
public class TagInfoVo implements Serializable {

    private int tagid;
    private String tag;

    public int getTagid() {
        return tagid;
    }

    public void setTagid(int tagid) {
        this.tagid = tagid;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String toString(){
        return "tagid="+tagid+",tag="+tag;
    }
}
