package com.zkjinshi.svip.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.vo.InvitationVo;
import com.zkjinshi.svip.vo.YunBaMsgVo;

import java.util.ArrayList;

/**
 * 活动邀请表
 * 开发者：杜健德
 * 日期：2016/4/27
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class InvitationMsgDBUtil {

    private final static String TAG = InvitationMsgDBUtil.class.getSimpleName();

    private Context context;
    private DBOpenHelper helper;

    private static InvitationMsgDBUtil instance = null;

    private InvitationMsgDBUtil(){}

    public synchronized static InvitationMsgDBUtil getInstance(){
        if(instance == null){
            instance = new InvitationMsgDBUtil();
        }
        instance.init();
        return instance;
    }

    private void init() {
        context = SVIPApplication.getInst().getApplicationContext();
        if(CacheUtil.getInstance().isLogin()){
            DBOpenHelper.DB_NAME = CacheUtil.getInstance().getUserId()+".db";
        }
        helper   = new DBOpenHelper(context);
    }

    /**
     * 插入营销推送消息
     * @param invitationVo
     * @return
     */
    public long insertInvitationMsg( final InvitationVo invitationVo) {
        long rowId = -1;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if(invitationVo == null){
            return rowId;
        }
        try {
            ContentValues values =  new ContentValues();
            values.put("actid",invitationVo.getActid());
            values.put("alert",invitationVo.getAlert());
            values.put("actname",invitationVo.getActname());
            values.put("actcontent",invitationVo.getActcontent());
            values.put("acturl",invitationVo.getActurl());
            values.put("actimage",invitationVo.getActimage());
            values.put("startdate",invitationVo.getStartdate());
            values.put("enddate",invitationVo.getEnddate());
            values.put("maxtake",invitationVo.getMaxtake());
            values.put("insert_time", System.currentTimeMillis());
            values.put("has_look",invitationVo.isHasLook() ? 1 : 0);
            db = helper.getWritableDatabase();
            cursor = db.query(DBOpenHelper.INVITATION_MSG_TBL, null, " actid = ? ",new String[]{ invitationVo.getActid() }, null, null, null);
            if(cursor != null && cursor.getCount() > 0){
                rowId = db.update(DBOpenHelper.INVITATION_MSG_TBL,values," actid = ? ",new String[]{ invitationVo.getActid() });
            }else{               
                rowId = db.insert(DBOpenHelper.INVITATION_MSG_TBL, null, values);
            }

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
            }
            if(null != db){
                db.close();
            }

        }
        return rowId;
    }



    /**
     * 获取前20条邀请推送消息
     * @return
     */
    public ArrayList<InvitationVo> queryInvitationMsg(){
        ArrayList<InvitationVo> msgList = null;
        InvitationVo msgVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.INVITATION_MSG_TBL, null,
                        null, null, null, null,"insert_time desc","20");
                if (cursor != null && cursor.getCount() > 0) {
                    msgList = new ArrayList<InvitationVo>();
                    while (cursor.moveToNext()) {
                       msgVo = new InvitationVo();
                       msgVo.setAlert(cursor.getString(cursor.getColumnIndex("alert")));
                       msgVo.setActid(cursor.getString(cursor.getColumnIndex("actid")));
                       msgVo.setActname(cursor.getString(cursor.getColumnIndex("actname")));
                       msgVo.setActcontent(cursor.getString(cursor.getColumnIndex("actcontent")));
                       msgVo.setActurl(cursor.getString(cursor.getColumnIndex("acturl")));
                       msgVo.setActimage(cursor.getString(cursor.getColumnIndex("actimage")));
                       msgVo.setStartdate(cursor.getString(cursor.getColumnIndex("startdate")));
                       msgVo.setEnddate(cursor.getString(cursor.getColumnIndex("enddate")));
                       msgVo.setMaxtake(cursor.getInt(cursor.getColumnIndex("maxtake")));
                       msgVo.setInsert_time(cursor.getLong(cursor.getColumnIndex("insert_time")));
                       msgVo.setHasLook(cursor.getInt(cursor.getColumnIndex("has_look")) == 1 ? true : false);
                        msgList.add(msgVo);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if (null != cursor) {
                    cursor.close();
                }

                if (null != db) {
                    db.close();
                }
            }

        }
        return msgList;
    }

    /**
     * 更新已读状态
     */
    public void updateInvitationMsgRead(String id){
        SQLiteDatabase db = null;
        String sql = null;
        try {
            db= helper.getWritableDatabase();
            ContentValues values =  new ContentValues();
            values.put("has_look",1);
            db.update(DBOpenHelper.INVITATION_MSG_TBL,values," actid = ? ",new String[]{ id });
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(null != db)
                db.close();
        }
    }

    /**
     * 从数据库获取一条未读信息，并更新已读状态
     * @return
     */
    public InvitationVo popUnReadInvitationMsg(){
        InvitationVo msgVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                String sql = "select * from "+DBOpenHelper.INVITATION_MSG_TBL+" where has_look=? limit 1";
                cursor = db.rawQuery(sql,new String[]{"0"});
                if (cursor != null && cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        msgVo = new InvitationVo();
                        msgVo.setAlert(cursor.getString(cursor.getColumnIndex("alert")));
                        msgVo.setActid(cursor.getString(cursor.getColumnIndex("actid")));
                        msgVo.setActname(cursor.getString(cursor.getColumnIndex("actname")));
                        msgVo.setActcontent(cursor.getString(cursor.getColumnIndex("actcontent")));
                        msgVo.setActurl(cursor.getString(cursor.getColumnIndex("acturl")));
                        msgVo.setActimage(cursor.getString(cursor.getColumnIndex("actimage")));
                        msgVo.setStartdate(cursor.getString(cursor.getColumnIndex("startdate")));
                        msgVo.setEnddate(cursor.getString(cursor.getColumnIndex("enddate")));
                        msgVo.setMaxtake(cursor.getInt(cursor.getColumnIndex("maxtake")));
                        msgVo.setInsert_time(cursor.getLong(cursor.getColumnIndex("insert_time")));
                        msgVo.setHasLook(cursor.getInt(cursor.getColumnIndex("has_look")) == 1 ? true : false);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if (null != cursor) {
                    cursor.close();
                }

                if (null != db) {
                    db.close();
                }
            }

        }
        if(msgVo != null){
            updateInvitationMsgRead(msgVo.getActid());
        }
        return msgVo;
    }

}
