package com.zkjinshi.svip.response;

/**
 * Created by djd on 2015/8/31.
 */
public class GetUserResponse extends BaseResponse {

    private String userid;
    private String token;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
