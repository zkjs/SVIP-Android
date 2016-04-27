package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * Created by dujiande on 2016/3/31.
 */
public class UserInfoVo implements Serializable {

    private String phone;
    private String username;
    private String userimage;
    private int sex;
    private String realname;
    private String viplevel;
    private String email;
    private int userstatus;

    private int ismodifyimage;
    private int ismodifyusername;
    private int silentmode;

    public int getSilentmode() {
        return silentmode;
    }

    public void setSilentmode(int silentmode) {
        this.silentmode = silentmode;
    }

    public int getIsmodifyimage() {
        return ismodifyimage;
    }

    public void setIsmodifyimage(int ismodifyimage) {
        this.ismodifyimage = ismodifyimage;
    }

    public int getIsmodifyusername() {
        return ismodifyusername;
    }

    public void setIsmodifyusername(int ismodifyusername) {
        this.ismodifyusername = ismodifyusername;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUserstatus() {
        return userstatus;
    }

    public void setUserstatus(int userstatus) {
        this.userstatus = userstatus;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getViplevel() {
        return viplevel;
    }

    public void setViplevel(String viplevel) {
        this.viplevel = viplevel;
    }
}
