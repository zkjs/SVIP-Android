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
                        + " idcard   text , " //证件号
                        + " phone    text " //联系电话
                        + " )";

    /** 城市名列表 */
    public static String CITY_TBL_SQL =
            "create table if not exists "
                    + DBOpenHelper.CITY_TBL
                    + "("
                    + " city_name text primary key," //城市名称
                    + " name_sort text " //城市排序字母
                    + " )";
    /** 特权列表 */
    public static String PRIVILEGE_TBL_SQL =
            "create table if not exists "
                    + DBOpenHelper.PRIVILEGE_TBL
                    + "("
                    + " privilegeDesc text ," //特权描述
                    + " privilegeIcon text ," //特权url
                    + " privilegeName text ," //特权名字
                    + " shopid text ," //商店id
                    + " shopName text " //商家名
                    + " )";

    //蓝牙定位统计表
    public static String BLE_STAT_TBL_SQL =
            "create table if not exists "
                    + DBOpenHelper.BLE_STAT_TBL
                    + "("
                    + " imei text primary key , "
                    + " timestamp long , "//数据库更新时间戳
                    + " retry_count long , "//网络请求错误次数
                    + " total_count long "//网络请求总次数
                    + " )";

    //蓝牙定位日志表
    public static String BLE_LOG_TBL_SQL =
            "create table if not exists "
                    + DBOpenHelper.BLE_LOG_TBL
                    + "("
                    + " _id integer primary key autoincrement , "
                    + " phone_num text , "//手机号码
                    + " device_type text , "//android/ios
                    + " brand text , "//手机品牌型号，出厂自带	华为/三星
                    + " imei text , "//设备唯一码
                    + " connected_type integer , "//网络连接类型	0：2G/3G网络
                    + " error_message text , "//网络请求错误信息
                    + " sdk integer , "//系统版本号
                    + " major text , "//区域id
                    + " timestamp long "//时间戳
                    + " )";

    /**
     * 获取数据库所有表名
     * @return
     */
    public static String[] getTableNames(){
        return new String[]{
            DBOpenHelper.USER_INFO_TBL,//详细用户信息表
            DBOpenHelper.SHOP_INFO_TBL,//商家详细信息表
            DBOpenHelper.SERVER_PERSONAL_TBL,//专属客服表
            DBOpenHelper.PERSON_CHECK_IN_TBL,//入住人表
            DBOpenHelper.PRIVILEGE_TBL,//特权
            DBOpenHelper.CITY_TBL,//城市名列表
            DBOpenHelper.BLE_LOG_TBL,//蓝牙定位日志表
            DBOpenHelper.BLE_STAT_TBL//蓝牙定位统计表
        };
    }
}
