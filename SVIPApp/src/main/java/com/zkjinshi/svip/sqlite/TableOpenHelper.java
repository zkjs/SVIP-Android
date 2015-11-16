package com.zkjinshi.svip.sqlite;

/**
 * 表语句操作帮助类
 * 开发者：JimmyZhang
 * 日期：2015/7/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class TableOpenHelper {

    /**
     * 创建messagetbl表sql语句
     */
    public static String MESSAGE_TBL_SQL =
            "create table if not exists "
                    + DBOpenHelper.MESSAGE_TBL
                    + "("
                    + " message_id text primary key , "
                    + " session_id text , "//聊天室唯一标识
                    + " shop_id text , "//聊点指定商家ID
                    + " contact_id text , "//发送者ID
                    + " contact_name text , "//发送者名称
                    + " content text , "//消息内容
                    + " send_time long , "//发送时间
                    + " title text , "//聊天室名称
                    + " voice_time text , "//语音时间
                    + " mime_type integer , "//消息类别
                    + " send_status integer ,  "//发送状态
                    + " is_read integer , "//是否已读
                    + " attach_id text , "//附件id
                    + " temp_id text , "//临时消息id
                    + " rule_type text , "//消息规则类型
                    + " file_name text , "//文件名称
                    + " file_path text , "//文件路径
                    + " url text , "//URl
                    + " scale_url text "//缩略图URL
                    + ")";

    /**
     * 创建chatroomtbl表sql语句
     */
    public static String CHAT_ROOM_TBL_SQL =
            "create table if not exists "
                    + DBOpenHelper.CHAT_ROOM_TBL
                    + "("
                    + " shop_id text primary key, "
                    + " shop_name text, "//聊天室名称
                    + " session_id text, "
                    + " remark text, "//备注
                    + " created long, "//创建时间
                    + " end_time long, "//会话结束时间
                    + " end_user_id text, "//会话结束人
                    + " client_id text, "//会话创建人
                    + " client_name text , "//会话创建姓名
                    + " is_visible integer"//是否显示此聊天室 //0:隐藏 //1:显示
                    + " )";

    /**
     * 创建chatmembertbl表sql语句
     */
    public static String CHAT_MEMBER_TBL_SQL =
            "create table if not exists "
                    + DBOpenHelper.CHAT_MEMBER_TBL
                    + "("
                    + " session_id text , "
                    + " userid text, "//用户ID
                    + " logintype integer, "//用户类型 0:app用户  1:商家员工 默认为:0
                    + " shopid text, "//商家ID
                    + " empid text, "//员工ID
                    + " roleid text, "//角色ID
                    + " created long "//创建时间
                    + " )";

    /**
     * 创建user_info用户信息表sql语句
     */
    public static String USER_INFO_TBL_SQL =
            "create table if not exists "
                    + DBOpenHelper.USER_INFO_TBL
                    + "("
                    + " userid text primary key, "//用户ID
                    + " username text, "//用户姓名
                    + " password text, "//密码
                    + " user_avatar text, "//用户头像
                    + " user_desc text , "//个性签名
                    + " age text, "//年龄
                    + " sex text, "//性别
                    + " userstatus text, "//0用户状态,0.生效,1失效 2正常 3匿名 4邀请 5员工
                    + " phone text, "//电话号码
                    + " email text, "//邮箱
                    + " msgmode text, "//消息接收模式:0:震动 1:响铃 2:免打扰 3:震动+响铃
                    + " qq text, "//QQ
                    + " wechart text, "//微信
                    + " weibo text, "//微博
                    + " otherchart text, "//其它社交帐号
                    + " is_payment integer, "//是否已绑定支付方式 0:未绑定 1:绑定
                    + " country text, "//国籍
                    + " province text, "//省份
                    + " city text, "//城市
                    + " district text, "//地区
                    + " phone_info text, "//设备信息
                    + " map_longitude text, "//经度
                    + " map_latitude text, "//纬度
                    + " phone_os text, "//手机os
                    + " user_config text, "//参数配置
                    + " deviceToken text, "//手机唯一标识
                    + " preference text, "//公司
                    + " pwd_question text, "//密保问题
                    + " pwd_answer text, "//密保问题答案
                    + " remark text, "//职位
                    + " created text, "//注册时间
                    + " modified text, "//CURRENT_TIMESTAMP最后修改时间
                    + " lasttime text, "//最后登录时间
                    + " bluetooth_key text, "//蓝牙id
                    + " birthday text, "//YES生日
                    + " tagsid text, "//YES标签
                    + " real_name text, "//真实姓名
                    + " english_name text, "//英文名称
                    + " idcard text "//身份证号
                    + " )";

    /**
     * 创建shopinfotbl表sql语句
     */
    public static String SHOP_INFO_TBL_SQL =
            "create table if not exists "
                    + DBOpenHelper.SHOP_INFO_TBL
                    + "("
                    + " shopid text primary key, "//商家ID
                    + " shopcode text, "
                    + " logo text, "     // 商家logo
                    + " fullname text, " // 商家全称
                    + " known_as text, " // 简称
                    + " location text, " // 国家/地区
                    + " countrycode text, " // 国际区号
                    + " province text, " // 省份
                    + " telcode text, " // 区号
                    + " city text, " // 城市
                    + " postcode text, " // 邮编
                    + " fulladdrs text, " // 详细地址
                    + " phone text, " // 商家电话
                    + " fax text, " // 商家传真
                    + " email text, " // 邮箱
                    + " url text, " // 主页
                    + " wechart text, " // 微信公众号
                    + " weibo text, " // 官方微博
                    + " others text, " // 其它公开方式
                    + " annualincome text, " // 年收入
                    + " staffnumber text, " // 管理端数
                    + " industry text, " // 所属行业
                    + " type text, " // 商家类型
                    + " cstsource text, " // 商家来源
                    + " status text, " // 0商家状态 0.激活,1.暂停服务 2.已停业 3.已结束签约
                    + " is_onlinepay text, " // 0是否支持线上支付 0:不支持 1:支持线上支付
                    + " curr_code text, " // RMB本位币别 默认为RMB
                    + " work_begin text, " // 营业时间起
                    + " work_end text, " // 营业时间止
                    + " created text, " // 创建时间
                    + " map_longitude text, "
                    + " map_latitude text, "
                    + " shop_desc text, " //商家简介
                    + " shop_recomm text, " //商家推荐
                    + " shop_bg text, " //商家背景 值格式uploads/shops/shopid_bg.jpg
                    + " shop_title text" //商家标语
                    + " )";
        /**
         * 创建专属客服表sql语句
         */
        public static String SERVER_PERSONAL_TBL_SQL =
                "create table if not exists "
                        + DBOpenHelper.SERVER_PERSONAL_TBL
                        + "("
                        + " shopid text primary key, "//商家ID
                        + " salesid text" //商家标语
                        + " )";

        /**
         * 入住人信息表
         */
        public static String PERSON_CHECK_IN_TBL_SQL =
                "create table if not exists "
                        + DBOpenHelper.PERSON_CHECK_IN_TBL
                        + "("
                        + " id       int  primary key," //入住人ID
                        + " realname text ," //真实姓名
                        + " idcard   text " //证件号
                        + " phone    text ," //联系电话
                        + " )";

    /**
     * 获取数据库所有表名
     * @return
     */
    public static String[] getTableNames(){
        return new String[]{
                DBOpenHelper.MESSAGE_TBL,     //消息记录表
                DBOpenHelper.CHAT_ROOM_TBL,   //聊天室表
                DBOpenHelper.CHAT_MEMBER_TBL, //聊天成员表
                DBOpenHelper.USER_INFO_TBL,   //详细用户信息表
                DBOpenHelper.SHOP_INFO_TBL,    //商家详细信息表
                DBOpenHelper.SERVER_PERSONAL_TBL,    //专属客服表
                DBOpenHelper.PERSON_CHECK_IN_TBL     //入住人信息表
        };
    }
}
