package com.zkjinshi.svip.response;

import java.io.Serializable;

/**
 * Created by dujiande on 2016/3/3.
 */
public class UpdateSiResponseData implements Serializable {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
