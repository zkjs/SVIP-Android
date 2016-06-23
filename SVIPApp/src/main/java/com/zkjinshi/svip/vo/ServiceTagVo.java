package com.zkjinshi.svip.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dujiande on 2016/3/8.
 */
public class ServiceTagVo implements Serializable{
    private int res;
    private String resDesc;
    private ArrayList<ServiceTagTopVo> data;

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

    public ArrayList<ServiceTagTopVo> getData() {
        return data;
    }

    public void setData(ArrayList<ServiceTagTopVo> data) {
        this.data = data;
    }
}
