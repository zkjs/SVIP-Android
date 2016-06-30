package com.zkjinshi.svip.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dujiande on 2016/3/8.
 */
public class GetActivityListVo implements Serializable{
    private int res;
    private String resDesc;
    private ArrayList<ActivityVo> data;

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

    public ArrayList<ActivityVo> getData() {
        return data;
    }

    public void setData(ArrayList<ActivityVo> data) {
        this.data = data;
    }
}
