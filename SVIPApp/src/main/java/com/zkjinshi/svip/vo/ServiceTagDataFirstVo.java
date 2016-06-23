package com.zkjinshi.svip.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dujiande on 2016/3/8.
 */
public class ServiceTagDataFirstVo implements Serializable {

    private String firstSrvTagName;
    private String firstSrvTagId;
    private ArrayList<ServiceTagDataSecondVo> secondSrvTag;

    public String getFirstSrvTagName() {
        return firstSrvTagName;
    }

    public void setFirstSrvTagName(String firstSrvTagName) {
        this.firstSrvTagName = firstSrvTagName;
    }

    public String getFirstSrvTagId() {
        return firstSrvTagId;
    }

    public void setFirstSrvTagId(String firstSrvTagId) {
        this.firstSrvTagId = firstSrvTagId;
    }

    public ArrayList<ServiceTagDataSecondVo> getSecondSrvTag() {
        return secondSrvTag;
    }

    public void setSecondSrvTag(ArrayList<ServiceTagDataSecondVo> secondSrvTag) {
        this.secondSrvTag = secondSrvTag;
    }
}
