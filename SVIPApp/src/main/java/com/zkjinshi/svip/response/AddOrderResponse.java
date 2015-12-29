package com.zkjinshi.svip.response;

import java.io.Serializable;

/**
 * Created by dujiande on 2015/12/29.
 */
public class AddOrderResponse implements Serializable {
    //{"data":"H14513531753485236","result":true} data为订单号

    private String data;
    private boolean result;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
