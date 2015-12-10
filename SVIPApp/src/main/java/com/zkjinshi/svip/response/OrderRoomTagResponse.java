package com.zkjinshi.svip.response;

import com.zkjinshi.svip.net.NetResponse;

/**
 * Created by djd on 2015/9/6.
 */
public class OrderRoomTagResponse extends NetResponse {
   private int id;//        房间选项id int
   private String content;//    房间选项名称
   private String sortorder;//  排序 int

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSortorder() {
        return sortorder;
    }

    public void setSortorder(String sortorder) {
        this.sortorder = sortorder;
    }
}
