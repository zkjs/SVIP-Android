package com.zkjinshi.svip.response;

import java.util.ArrayList;

/**
 * Created by dujiande on 2016/3/15.
 */
public class GetMessageLoginResponse extends BaseFornaxResponse {

    private MessageLoginData data;

    public MessageLoginData getData() {
        return data;
    }

    public void setData(MessageLoginData data) {
        this.data = data;
    }
}
