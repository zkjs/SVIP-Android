package com.zkjinshi.svip.factory;

import android.content.ContentValues;
import android.database.Cursor;

import com.zkjinshi.svip.response.OrderUsersResponse;
import com.zkjinshi.svip.vo.PersonCheckInVo;

/**
 * 开发者：WinkyQin
 * 日期： 2015/11/16
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class PersonCheckInFactory {

    private static PersonCheckInFactory personCheckInFactory;

    private PersonCheckInFactory() {
    }

    public synchronized static PersonCheckInFactory getInstance(){
        if(null == personCheckInFactory){
            personCheckInFactory = new PersonCheckInFactory();
        }
        return personCheckInFactory;
    }

    /**
     * 构建入住人对象键对值
     * @param orderUser
     * @return
     */
    public PersonCheckInVo buildPersonCheckInVoByOrderUser(OrderUsersResponse orderUser) {
        PersonCheckInVo personCheckInVo = new PersonCheckInVo();
        personCheckInVo.setId(orderUser.getId());
        personCheckInVo.setIdcard(orderUser.getIdcard());
        personCheckInVo.setRealname(orderUser.getPhone());
        personCheckInVo.setPhone(orderUser.getRealname());
        return personCheckInVo;
    }

    /**
     * 构建入住人对象键对值
     * @param personCheckInVo
     * @return
     */
    public ContentValues buildContentValues(PersonCheckInVo personCheckInVo) {
        ContentValues values = new ContentValues();
        values.put("id", personCheckInVo.getId());
        values.put("idcard", personCheckInVo.getIdcard());
        values.put("phone", personCheckInVo.getPhone());
        values.put("realname", personCheckInVo.getRealname());
        return values;
    }

    /**
     * 构建更新入住人对象键对值
     * @param orderUser
     * @return
     */
    public ContentValues buildUpdateContentValues(PersonCheckInVo orderUser) {
        ContentValues values = new ContentValues();
        values.put("phone", orderUser.getPhone());
        values.put("idcard", orderUser.getIdcard());
        values.put("realname", orderUser.getRealname());
        return values;
    }

    /**
     * 根据游标生成入住人对象
     * @param cursor
     * @return
     */
    public PersonCheckInVo buildPersonCheckInVoByCursor(Cursor cursor) {
        PersonCheckInVo personCheckInVo = new PersonCheckInVo();
        personCheckInVo.setId(cursor.getInt(0));
        personCheckInVo.setRealname(cursor.getString(1));
        personCheckInVo.setIdcard(cursor.getString(2));
        personCheckInVo.setPhone(cursor.getString(3));
        return null;
    }
}
