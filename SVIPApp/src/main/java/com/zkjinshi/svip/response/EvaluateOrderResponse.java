package com.zkjinshi.svip.response;

import java.io.Serializable;

/**
 * 评价响应类
 */
public class EvaluateOrderResponse implements Serializable {

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
