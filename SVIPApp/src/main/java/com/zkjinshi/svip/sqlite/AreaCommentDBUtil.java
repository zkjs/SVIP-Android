package com.zkjinshi.svip.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.vo.CommentVo;


import java.util.ArrayList;

/**
 * 区域评论记录历史表
 * 开发者：杜健德
 * 日期：2016/4/27
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class AreaCommentDBUtil {

    private final static String TAG = AreaCommentDBUtil.class.getSimpleName();

    private Context context;
    private DBOpenHelper helper;

    private static AreaCommentDBUtil instance = null;

    private AreaCommentDBUtil(){}

    public synchronized static AreaCommentDBUtil getInstance(){
        if(instance == null){
            instance = new AreaCommentDBUtil();
        }
        instance.init();
        return instance;
    }

    private void init() {
        context = SVIPApplication.getInst().getApplicationContext();
        helper   = new DBOpenHelper(context);
    }


    public long insertComment( final CommentVo commentVo) {

        long rowId = -1;
        SQLiteDatabase db = null;
        if(commentVo == null){
            return rowId;
        }
        try {
            ContentValues values =  new ContentValues();
            values.put("avatarUrl",commentVo.getAvatarUrl());
            values.put("name",commentVo.getName());
            values.put("timestamp",commentVo.getTimestamp());
            values.put("area_key",commentVo.getArea_key());
            values.put("comment",commentVo.getComment());          

            db = helper.getWritableDatabase();
            rowId = db.insert(DBOpenHelper.AERE_COMMENT_TBL, null, values);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(null != db)
                db.close();
        }
        return rowId;
    }



   
    public ArrayList<CommentVo> queryComments(){

        ArrayList<CommentVo> beaconMsgList = null;
        CommentVo commentVo = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        if (null != helper) {
            try {
                db = helper.getReadableDatabase();
                cursor = db.query(DBOpenHelper.AERE_COMMENT_TBL, null,
                        null, null, null, null,"timestamp asc");
                if (cursor != null && cursor.getCount() > 0) {
                    beaconMsgList = new ArrayList<CommentVo>();
                    while (cursor.moveToNext()) {
                        commentVo = new CommentVo();
                        /**
                         *  avatarUrl text , "
                         + " name text , "
                         + " timestamp long , "
                         + " area_key text , "
                         + " comment text  "
                         */
                        commentVo.setAvatarUrl(cursor.getString(cursor.getColumnIndex("avatarUrl")));
                        commentVo.setName(cursor.getString(cursor.getColumnIndex("name")));
                        commentVo.setTimestamp(cursor.getLong(cursor.getColumnIndex("timestamp")));
                        commentVo.setArea_key(cursor.getString(cursor.getColumnIndex("area_key")));
                        commentVo.setComment(cursor.getString(cursor.getColumnIndex("comment")));
                        beaconMsgList.add(commentVo);
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
        return beaconMsgList;
    }

}
