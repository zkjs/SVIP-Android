package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * 用户信息封装vo
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class UserInfoVo implements Serializable{

    private String userid;
    private String username;
    private String password;
    private String userAvatar;
    private String mobilePhoto;
    private String position;
    private String company;
    private Sex sex;
    private String email;
    private String realName;

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
     return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
     this.userAvatar = userAvatar;
    }

    public String getMobilePhoto() {
     return mobilePhoto;
    }

    public void setMobilePhoto(String mobilePhoto) {
     this.mobilePhoto = mobilePhoto;
    }

    public String getPosition() {
     return position;
    }

    public void setPosition(String position) {
     this.position = position;
    }

    public String getCompany() {
     return company;
    }

    public void setCompany(String company) {
     this.company = company;
    }

    public Sex getSex() {
     return sex;
    }

    public void setSex(Sex sex) {
     this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    /**
     {
     userid         varchar(32)     NO      PRI用精确到纳秒或uuid来做id,不使用数库自增。
     username       varchar(30)     NO      MUL用户名
     password       varchar(300)    NO	 密码
     user_avatar    varchar(100)    YES	用户头像
     user_desc      varchar(100)    YES	个性签名
     age            varchar(10)     YES	年龄
     sex            tinyint(1)    (NULL)             YES性别
     userstatus     tinyint(1)    (NULL)             NO 0用户状态,0.生效,1失效.
     phone          varchar(30)     YES	电话号码
     email          varchar(30)     YES	邮箱
     msgmode        tinyint(1)    (NULL)             YES 消息接收模式:0:震动 1:响铃 2:免打扰 3:震动+响铃
     qq             varchar(30)     YES	QQ
     wechart        varchar(30)     YES	微信
     weibo          varchar(30)     YES	微博
     otherchart     varchar(50)     YES	其它社交帐号
     is_payment     tinyint(1)    (NULL)             YES 0是否已绑定支付方式 0:未绑定 1:绑定
     country        varchar(20)     YES	国籍
     province       varchar(20)     YES	省份
     city           varchar(20)     YES	城市
     district       varchar(150)    YES	地区
     phone_info     varchar(50)     YES	设备信息
     map_longitude  varchar(20)     YES	经度
     map_latitude   varchar(20)     YES	纬度
     phone_os       varchar(20)     YES	手机os
     user_config    varchar(10)     YES	参数配置
     deviceToken    varchar(50)     YES	手机唯一标识
     preference     varchar(60)    YES	公司
     pwd_question   varchar(200)    YES	密保问题
     pwd_answer     varchar(150)    YES	密保问题答案
     remark         varchar(60)    YES	职位
     created        datetime      (NULL)             YES注册时间
     modified       timestamp     (NULL)             YES             CURRENT_TIMESTAMP最后修改时间
     lasttime       datetime      (NULL)             YES最后登录时间
     bluetooth_key  varchar(32)     YES蓝牙id
     birthday       date          (NULL)             YES生日
     tagsid         varchar(640)    YES标签
     }
     */
}
