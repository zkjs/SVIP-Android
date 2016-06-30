package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * Created by dujiande on 2016/6/30.
 */
public class ActivityVo implements Serializable {

    private String actname;
    private String shopname;
    private String actid;
    private String startdate;
    private String enddate;
    private String actstatus;

    public String getActname() {
        return actname;
    }

    public void setActname(String actname) {
        this.actname = actname;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getActid() {
        return actid;
    }

    public void setActid(String actid) {
        this.actid = actid;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getActstatus() {
        return actstatus;
    }

    public void setActstatus(String actstatus) {
        this.actstatus = actstatus;
    }
}
