package com.zkjinshi.svip.response;

import com.zkjinshi.svip.http.post.HttpResponse;

/**
 * Created by djd on 2015/9/6.
 */
public class OrderUsersResponse extends HttpResponse {
   private int id;//       联系人id int
   private String realname;//  真实姓名
   private String idcard;//   证件号
   private String phone;//    联系电话 phone

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
