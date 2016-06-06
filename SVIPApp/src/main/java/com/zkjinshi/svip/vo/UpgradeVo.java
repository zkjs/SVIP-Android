package com.zkjinshi.svip.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dujiande on 2016/3/8.
 */
public class UpgradeVo implements Serializable {

    private int res;
    private String resDesc;
    private UpdateVo data;

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

    public UpdateVo getData() {
        return data;
    }

    public void setData(UpdateVo data) {
        this.data = data;
    }
}
