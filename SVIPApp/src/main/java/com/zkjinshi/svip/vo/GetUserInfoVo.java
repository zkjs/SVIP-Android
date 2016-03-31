package com.zkjinshi.svip.vo;

import java.util.ArrayList;

/**
 * Created by dujiande on 2016/3/31.
 */
public class GetUserInfoVo extends BaseResponseVo {
    private ArrayList<UserInfoVo> data;

    public ArrayList<UserInfoVo> getData() {
        return data;
    }

    public void setData(ArrayList<UserInfoVo> data) {
        this.data = data;
    }
}
