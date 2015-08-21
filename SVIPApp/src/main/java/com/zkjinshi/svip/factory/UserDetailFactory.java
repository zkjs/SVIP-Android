package com.zkjinshi.svip.factory;

import android.content.ContentValues;
import android.database.Cursor;

import com.zkjinshi.svip.sqlite.UserDetailDBUtil;
import com.zkjinshi.svip.vo.ChatRoomVo;
import com.zkjinshi.svip.vo.UserDetailVo;
import com.zkjinshi.svip.vo.VisibleStatus;

/**
 * 用户详细信息操作工厂类
 * 开发者：vincent
 * 日期：2015/8/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class UserDetailFactory {

    private UserDetailFactory(){}

    private static UserDetailFactory instance;

    public static synchronized UserDetailFactory getInstance(){
        if(null == instance){
            instance = new UserDetailFactory();
        }
        return instance;
    }

    /**
     * 根据详细用户信息生成contentValues
     * @param userDetailVo
     * @return
     */
    public ContentValues buildContentValues(UserDetailVo userDetailVo) {
        ContentValues values = new ContentValues();
        values.put("userid", userDetailVo.getUserid());
        values.put("username", userDetailVo.getUsername());
        values.put("password", userDetailVo.getPassword());
        values.put("user_avatar", userDetailVo.getUserAvatar());
        values.put("user_desc", userDetailVo.getUserDesc());
        values.put("age", userDetailVo.getAge());
        values.put("sex", userDetailVo.getSex());
        values.put("userstatus", userDetailVo.getUserstatus());
        values.put("phone", userDetailVo.getPhone());
        values.put("email", userDetailVo.getEmail());
        values.put("msgmode", userDetailVo.getMsgmode());
        values.put("qq", userDetailVo.getQQ());
        values.put("wechart", userDetailVo.getWechart());
        values.put("weibo", userDetailVo.getWeibo());
        values.put("otherchart", userDetailVo.getOtherchart());
        values.put("is_payment", userDetailVo.isPayment());
        values.put("country", userDetailVo.getCountry());
        values.put("province", userDetailVo.getProvince());
        values.put("city", userDetailVo.getCity());
        values.put("district", userDetailVo.getDistrict());
        values.put("phone_info", userDetailVo.getPhoneInfo());
        values.put("map_longitude", userDetailVo.getMapLongitude());
        values.put("map_latitude", userDetailVo.getMapLatitude());
        values.put("phone_os", userDetailVo.getPhoneOS());
        values.put("user_config", userDetailVo.getUserConfig());
        values.put("deviceToken", userDetailVo.getDeviceToken());
        values.put("preference", userDetailVo.getPreference());
        values.put("pwd_question", userDetailVo.getPwdQuestion());
        values.put("pwd_answer", userDetailVo.getPwdAnswer());
        values.put("remark", userDetailVo.getRemark());
        values.put("created", userDetailVo.getCreated());
        values.put("modified", userDetailVo.getModified());
        values.put("lasttime", userDetailVo.getLasttime());
        values.put("bluetooth_key", userDetailVo.getBluetoothKey());
        values.put("birthday", userDetailVo.getBirthday());
        values.put("tagsid", userDetailVo.getTagsid());
        values.put("real_name", userDetailVo.getRealName());
        values.put("english_name", userDetailVo.getEnglishName());
        values.put("idcard", userDetailVo.getIdcard());
        return values;
    }

    /**
     * 根据游标创建具体用户对象
     * @param cursor
     * @return
     */
    public UserDetailVo buildUserDetailVoByCursor(Cursor cursor) {
        //新建具体用户对象
        UserDetailVo userDetailVo = new UserDetailVo();
        userDetailVo.setUserid(cursor.getString(0));
        userDetailVo.setUsername(cursor.getString(1));
        userDetailVo.setPassword(cursor.getString(2));
        userDetailVo.setUserAvatar(cursor.getString(3));
        userDetailVo.setUserDesc(cursor.getString(4));
        userDetailVo.setAge(cursor.getString(5));
        userDetailVo.setSex(cursor.getString(6));
        userDetailVo.setUserstatus(cursor.getString(7));
        userDetailVo.setPhone(cursor.getString(8));
        userDetailVo.setEmail(cursor.getString(9));
        userDetailVo.setMsgmode(cursor.getString(10));
        userDetailVo.setQQ(cursor.getString(11));
        userDetailVo.setWechart(cursor.getString(12));
        userDetailVo.setWeibo(cursor.getString(13));
        userDetailVo.setOtherchart(cursor.getString(14));
        userDetailVo.setPayment(cursor.getString(15));
        userDetailVo.setCountry(cursor.getString(16));
        userDetailVo.setProvince(cursor.getString(17));
        userDetailVo.setCity(cursor.getString(18));
        userDetailVo.setDistrict(cursor.getString(19));
        userDetailVo.setPhoneInfo(cursor.getString(20));
        userDetailVo.setMapLongitude(cursor.getString(21));
        userDetailVo.setMapLatitude(cursor.getString(22));
        userDetailVo.setPhoneOS(cursor.getString(23));
        userDetailVo.setUserConfig(cursor.getString(24));
        userDetailVo.setDeviceToken(cursor.getString(25));
        userDetailVo.setPreference(cursor.getString(26));
        userDetailVo.setPwdQuestion(cursor.getString(27));
        userDetailVo.setPwdAnswer(cursor.getString(28));
        userDetailVo.setRemark(cursor.getString(29));
        userDetailVo.setCreated(cursor.getString(30));
        userDetailVo.setModified(cursor.getString(31));
        userDetailVo.setLasttime(cursor.getString(32));
        userDetailVo.setBluetooth_key(cursor.getString(33));
        userDetailVo.setBirthday(cursor.getString(34));
        userDetailVo.setTagsid(cursor.getString(35));
        userDetailVo.setRealName(cursor.getString(36));
        userDetailVo.setEnglishName(cursor.getString(37));
        userDetailVo.setIdcard(cursor.getString(38));
        return userDetailVo;
    }

}
