package com.zkjinshi.svip.factory;

import com.zkjinshi.svip.response.UserInfoResponse;
import com.zkjinshi.svip.vo.Sex;
import com.zkjinshi.svip.vo.UserInfoVo;

/**
 * 用户信息工厂类
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class UserInfoFactory {
    private UserInfoFactory (){}
    private static UserInfoFactory instance;
    public synchronized static UserInfoFactory getInstance(){
        if(null ==  instance){
            instance = new UserInfoFactory();
        }
        return  instance;
    }

    public UserInfoVo buildUserInfoVo(UserInfoResponse userInfoResponse){
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setUserid(userInfoResponse.getUserid());
        userInfoVo.setUsername(userInfoResponse.getUsername());
        userInfoVo.setPassword(userInfoResponse.getPassword());
        userInfoVo.setCompany(userInfoResponse.getPreference());
        userInfoVo.setMobilePhoto(userInfoResponse.getPhone());
        userInfoVo.setPosition(userInfoResponse.getRemark());
        userInfoVo.setUserAvatar(userInfoResponse.getUser_avatar());
        userInfoVo.setSex(userInfoResponse.getSex() == 1 ? Sex.BOY : Sex.GIRL);
        userInfoVo.setEmail(userInfoResponse.getEmail());
        userInfoVo.setRealName(userInfoResponse.getRealName());
        return  userInfoVo;
    }
}
