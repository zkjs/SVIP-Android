package com.zkjinshi.svip.bean;

import java.io.Serializable;

/**
 * Created by dujiande on 2016/1/5.
 */
public class UpdateBean implements Serializable {

//    "created": "2016-03-16 11:42:22",
//            "isforceupgrade": 0,
//            "packageurl": "",
//            "verno": "1.0.0.3",
//            "versionname": "IOS 超级身份0.3"


    private String created;
    /**
     * 是否强制升级
     */
    private int isforceupgrade;
    /**
     * 包地址(只针对andriod,IOS直接跳转到app store下载)
     */
    private String packageurl;

    private String verno;

    private String versionname;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public int getIsforceupgrade() {
        return isforceupgrade;
    }

    public void setIsforceupgrade(int isforceupgrade) {
        this.isforceupgrade = isforceupgrade;
    }

    public String getPackageurl() {
        return packageurl;
    }

    public void setPackageurl(String packageurl) {
        this.packageurl = packageurl;
    }

    public String getVerno() {
        return verno;
    }

    public void setVerno(String verno) {
        this.verno = verno;
    }

    public String getVersionname() {
        return versionname;
    }

    public void setVersionname(String versionname) {
        this.versionname = versionname;
    }
}
