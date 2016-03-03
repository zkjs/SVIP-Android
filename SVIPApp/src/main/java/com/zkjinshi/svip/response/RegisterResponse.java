package com.zkjinshi.svip.response;

import java.io.Serializable;

/**
 * Created by dujiande on 2016/3/2.
 */
public class RegisterResponse implements Serializable {

    private int res;
    private String resDesc;
    private String token;

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public String getResDesc() {
        return resDesc;
    }

    public void setResDesc(String resDesc) {
        this.resDesc = resDesc;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
