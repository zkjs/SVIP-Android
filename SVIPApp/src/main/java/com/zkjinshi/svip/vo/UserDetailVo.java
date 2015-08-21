package com.zkjinshi.svip.vo;

/**
 * 开发者：vincent
 * 日期：2015/8/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class UserDetailVo {

    private String userid;//用户ID
    private String username;//用户姓名
    private String password;//密码
    private String user_avatar;//用户头像
    private String user_desc;//个性签名
    private String age;//年龄
    private String sex;//性别
    private String userstatus;// 0用户状态,0.生效,1失效 2正常 3匿名 4邀请 5员工
    private String phone;//电话号码
    private String email;//邮箱
    private String msgmode;//消息接收模式:0:震动 1:响铃 2:免打扰 3:震动+响铃
    private String QQ;//QQ
    private String wechart;//微信
    private String weibo;//微博
    private String otherchart;//其它社交帐号
    private String is_payment;//是否已绑定支付方式  0:未绑定 1:绑定
    private String country;//国籍
    private String province;//省份
    private String city;//城市
    private String district;//地区
    private String phone_info;//设备信息
    private String map_longitude;//经度
    private String map_latitude;//纬度
    private String phone_os;//手机os
    private String user_config;//参数配置
    private String deviceToken;//手机唯一标识
    private String preference;//公司
    private String pwd_question;//密保问题
    private String pwd_answer;//密保问题答案
    private String remark;//职位
    private String created;//注册时间
    private String modified;//CURRENT_TIMESTAMP最后修改时间
    private String lasttime;//最后登录时间
    private String bluetooth_key;//蓝牙id
    private String birthday;//生日
    private String tagsid;//标签
    private String real_name;//真实姓名
    private String english_name;//英文名称
    private String idcard;//身份证号

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

    public String getUserAvatar() {
        return user_avatar;
    }

    public void setUserAvatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getUserDesc() {
        return user_desc;
    }

    public void setUserDesc(String user_desc) {
        this.user_desc = user_desc;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
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

    public String getQQ() {
        return QQ;
    }

    public void setQQ(String QQ) {
        this.QQ = QQ;
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

    public String isPayment() {
        return is_payment;
    }

    public void setPayment(String is_payment) {
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

    public String getPhoneInfo() {
        return phone_info;
    }

    public void setPhoneInfo(String phone_info) {
        this.phone_info = phone_info;
    }

    public String getMapLongitude() {
        return map_longitude;
    }

    public void setMapLongitude(String map_longitude) {
        this.map_longitude = map_longitude;
    }

    public String getMapLatitude() {
        return map_latitude;
    }

    public void setMapLatitude(String map_latitude) {
        this.map_latitude = map_latitude;
    }

    public String getPhoneOS() {
        return phone_os;
    }

    public void setPhoneOS(String phone_os) {
        this.phone_os = phone_os;
    }

    public String getUserConfig() {
        return user_config;
    }

    public void setUserConfig(String user_config) {
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

    public String getPwdQuestion() {
        return pwd_question;
    }

    public void setPwdQuestion(String pwd_question) {
        this.pwd_question = pwd_question;
    }

    public String getPwdAnswer() {
        return pwd_answer;
    }

    public void setPwdAnswer(String pwd_answer) {
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

    public String getBluetoothKey() {
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

    public String getRealName() {
        return real_name;
    }

    public void setRealName(String real_name) {
        this.real_name = real_name;
    }

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
}
