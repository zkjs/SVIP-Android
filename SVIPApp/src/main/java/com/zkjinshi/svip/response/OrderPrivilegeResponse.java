package com.zkjinshi.svip.response;

import com.zkjinshi.svip.http.post.HttpResponse;

/**
 * Created by djd on 2015/9/6.
 */
public class OrderPrivilegeResponse extends HttpResponse {
    private int id;//                   商家特权id  int
    private String privilege_code;//   特权代码
    private String privilege_name;//   特权名称
    private String user_level;//       使用此特权要求最低用户级别  int

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrivilege_code() {
        return privilege_code;
    }

    public void setPrivilege_code(String privilege_code) {
        this.privilege_code = privilege_code;
    }

    public String getPrivilege_name() {
        return privilege_name;
    }

    public void setPrivilege_name(String privilege_name) {
        this.privilege_name = privilege_name;
    }

    public String getUser_level() {
        return user_level;
    }

    public void setUser_level(String user_level) {
        this.user_level = user_level;
    }
}
