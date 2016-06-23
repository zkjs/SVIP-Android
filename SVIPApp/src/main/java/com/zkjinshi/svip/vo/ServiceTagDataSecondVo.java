package com.zkjinshi.svip.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dujiande on 2016/3/8.
 */
public class ServiceTagDataSecondVo implements Serializable {

    private String secondSrvTagName;
    private String secondSrvTagId;
    private String secondSrvTagDesc;

    public String getSecondSrvTagName() {
        return secondSrvTagName;
    }

    public void setSecondSrvTagName(String secondSrvTagName) {
        this.secondSrvTagName = secondSrvTagName;
    }

    public String getSecondSrvTagId() {
        return secondSrvTagId;
    }

    public void setSecondSrvTagId(String secondSrvTagId) {
        this.secondSrvTagId = secondSrvTagId;
    }

    public String getSecondSrvTagDesc() {
        return secondSrvTagDesc;
    }

    public void setSecondSrvTagDesc(String secondSrvTagDesc) {
        this.secondSrvTagDesc = secondSrvTagDesc;
    }
}
