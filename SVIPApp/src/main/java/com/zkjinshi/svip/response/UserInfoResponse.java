package com.zkjinshi.svip.response;

import com.zkjinshi.svip.http.HttpRequest;

/**
 * 用户信息响应类
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class UserInfoResponse extends BaseResponse {

    private String userid;
    private String username;
    private String password;
    private String user_avatar;

    private String user_desc;
    private String age;
    private int sex;
    private String userstatus;

    private String phone;
    private String email;
    private String msgmode;
    private String qq;

    private String wechart;
    private String weibo;
    private String otherchart;
    private String is_payment;

    private String country;
    private String province;
    private String city;
    private String district;

    private String phone_info;
    private String map_longitude;
    private String map_latitude;
    private String phone_os;

    private String user_config;
    private String deviceToken;
    private String preference;
    private String pwd_question;

    private String pwd_answer;
    private String remark;
    private String created;
    private String modified;

    private String lasttime;
    private String bluetooth_key;
    private String birthday;
    private String tagsid;

    private String real_name;
    private String english_name;
    private String idcard;
    private int tagopen;

    public String getEnglishName() {
        return english_name;
    }

    public void setEnglishName(String english_name) {
        this.english_name = english_name;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }
    public String getRealName() {
        return real_name;
    }

    public void setRealName(String real_name) {
        this.real_name = real_name;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getUser_desc() {
        return user_desc;
    }

    public void setUser_desc(String user_desc) {
        this.user_desc = user_desc;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getUserstatus() {
        return userstatus;
    }

    public void setUserstatus(String userstatus) {
        this.userstatus = userstatus;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMsgmode() {
        return msgmode;
    }

    public void setMsgmode(String msgmode) {
        this.msgmode = msgmode;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWechart() {
        return wechart;
    }

    public void setWechart(String wechart) {
        this.wechart = wechart;
    }

    public String getWeibo() {
        return weibo;
    }

    public void setWeibo(String weibo) {
        this.weibo = weibo;
    }

    public String getOtherchart() {
        return otherchart;
    }

    public void setOtherchart(String otherchart) {
        this.otherchart = otherchart;
    }

    public String getIs_payment() {
        return is_payment;
    }

    public void setIs_payment(String is_payment) {
        this.is_payment = is_payment;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPhone_info() {
        return phone_info;
    }

    public void setPhone_info(String phone_info) {
        this.phone_info = phone_info;
    }

    public String getMap_longitude() {
        return map_longitude;
    }

    public void setMap_longitude(String map_longitude) {
        this.map_longitude = map_longitude;
    }

    public String getMap_latitude() {
        return map_latitude;
    }

    public void setMap_latitude(String map_latitude) {
        this.map_latitude = map_latitude;
    }

    public String getPhone_os() {
        return phone_os;
    }

    public void setPhone_os(String phone_os) {
        this.phone_os = phone_os;
    }

    public String getUser_config() {
        return user_config;
    }

    public void setUser_config(String user_config) {
        this.user_config = user_config;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public String getPwd_question() {
        return pwd_question;
    }

    public void setPwd_question(String pwd_question) {
        this.pwd_question = pwd_question;
    }

    public String getPwd_answer() {
        return pwd_answer;
    }

    public void setPwd_answer(String pwd_answer) {
        this.pwd_answer = pwd_answer;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getLasttime() {
        return lasttime;
    }

    public void setLasttime(String lasttime) {
        this.lasttime = lasttime;
    }

    public String getBluetooth_key() {
        return bluetooth_key;
    }

    public void setBluetooth_key(String bluetooth_key) {
        this.bluetooth_key = bluetooth_key;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getTagsid() {
        return tagsid;
    }

    public void setTagsid(String tagsid) {
        this.tagsid = tagsid;
    }

    public int getTagopen() {
        return tagopen;
    }

    public void setTagopen(int tagopen) {
        this.tagopen = tagopen;
    }
}
