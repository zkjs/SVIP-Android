package com.zkjinshi.svip.bean;

import java.io.Serializable;

/**
 * Created by dujiande on 2016/1/5.
 */
public class UpdateBean implements Serializable {

    /**
     * 版本号
     */
    private int versionNo;
    /**
     * 是否强制升级
     */
    private int isForceUpgrade;//“1”＝最新版（不做任何操作）；“2”＝“提醒更新”（弹框提示已有更新；）；“3”＝“强制更新”（弹框提示更新后才能使用，点击确定退出程序）
    /**
     * 包地址(只针对andriod,IOS直接跳转到app store下载)
     */
    private String packageUrl;
    /**
     * 版本名称（升级时提示信息中用到）
     */
    private String versionName;

    public int getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }

    public int getIsForceUpgrade() {
        return isForceUpgrade;
    }

    public void setIsForceUpgrade(int isForceUpgrade) {
        this.isForceUpgrade = isForceUpgrade;
    }

    public String getPackageUrl() {
        return packageUrl;
    }

    public void setPackageUrl(String packageUrl) {
        this.packageUrl = packageUrl;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
}
