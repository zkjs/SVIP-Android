package com.zkjinshi.base.net.protocol;

/**
 * IM协议封装类
 * 开发者：JimmyZhang
 * 日期：2015/8/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ProtocolMSG {

    //登录
    public static int MSG_ClientLogin = 1;
    public static int MSG_ClientLogin_RSP = 2;

    //登出
    public static int MSG_ClientLogout = 3;
    public static int MSG_ClientLogout_RSP = 4;

    //心跳
    public static int MSG_Ping = 5;

    //user重复连接时,踢走前,给旧连接发条消息
    public static int MSG_ServerRepeatLogin = 6;

    // app用户自定义生成消息
    public static int MSG_UserDefine = 7;
    public static int MSG_UserDefine_RSP = 8;

    //构建讨论组
    public static int MSG_BuildGroup = 10;
    public static int MSG_BuildGroup_RSP = 11;

    //解散讨论组
    public static int MSG_DisbandGroup = 12;
    public static int MSG_DisbandGroup_RSP = 13;

    //组信息
    public static int MSG_GetGroupList = 14;
    public static int MSG_GetGroupList_RSP = 15;

    //加入讨论组
    public static int MSG_JoinGroup = 16;
    public static int MSG_JoinGroup_RSP = 17;

    //离开讨论组
    public static int MSG_LeaveGroup = 18;
    public static int MSG_LeaveGroup_RSP = 19;

    //文本消息
    public static int MSG_C2CTextChat = 20;
    public static int MSG_C2CTextChat_RSP = 21;

    //语音消息
    public static int MSG_C2CMediaChat = 22;
    public static int MSG_C2CMediaChat_RSP = 23;

    //图片消息
    public static int MSG_C2CImgChat = 24;
    public static int MSG_C2CImgChat_RSP = 25;

    //组文本消息
    public static int MSG_GroupTextChat = 26;
    public static int MSG_GroupTextChat_RSP = 27;

    //组语音消息
    public static int MSG_GroupMediaChat = 28;
    public static int MSG_GroupMediaChat_RSP = 29;

    //组图片消息
    public static int MSG_GroupImgChat = 30;
    public static int MSG_GroupImgChat_RSP = 31;

    //离线文本消息
    public static int MSG_C2COfflineText = 32;
    public static int MSG_C2COfflineText_RSP = 33;

    //组离线文本消息
    public static int MSG_GroupOfflineText = 34;
    public static int MSG_GroupOfflineText_RSP = 35;

    //客户排队取号
    public static int MSG_QueGetNo_C2M = 50;
    public static int MSG_QueGetNo_C2M_RSP = 51;
    public static int MSG_QueGetNo_M2S = 52; //转发给商家，有人取号及相关信息

    //客户查询队列状况
    public static int MSG_QueSearchNo = 53;
    public static int MSG_QueSearchNo_RSP = 54;

    //应号
    public static int MSG_QueConfirmNo_S2M = 55; //商家向客户发出应号
    public static int MSG_QueConfirmNo_S2M_RSP = 56;
    public static int MSG_QueConfirmNo_M2C = 57; // 消息服务器转发至客户

    //应号确认
    public static int MSG_QueConfirmNoResult_C2M = 58; //客户向商家发回应号确认结果
    public static int MSG_QueConfirmNoResult_C2M_RSP = 59;
    public static int MSG_QueConfirmNoResult_M2S = 60; // 将客户结果转发至商家

    //商家手工销号
    public static int MSG_QuePinNo = 61;
    public static int MSG_QuePinNo_RSP = 62;

    //解散/重置队列
    public static int MSG_QueDisband = 63;
    public static int MSG_QueDisband_RSP = 64;

    //队列明细
    public static int MSG_QueDetail = 65;
    public static int MSG_QueDetail_RSP = 66;


    //队列是否可取号状态
    public static int MSG_QueStatus = 67;
    public static int MSG_QueStatus_RSP = 68;

    //结帐请求
    public static int MSG_UserAccount_C2MS = 69; //客户发起结帐请求,并将请求转发给商家
    public static int MSG_UserAccount_C2MS_RSP = 70;

    //商家推送帐单至客户,并转发给客户
    public static int MSG_UserAccount_S2MC = 72;
    public static int MSG_UserAccount_S2MC_RSP = 73; //商家帐单推送回应

    //客户手工销号(可用于客户取消排队或重排之类)
    public static int MSG_QueUserPinNo_C2M = 75; //客户向发出应号
    public static int MSG_QueUserPinNo_C2M_RSP = 76;
    public static int MSG_QueUserPinNo_M2S = 77; // 消息服务器转发至商家

    //商家手工指定默认的排队预估等待时间
    public static int MSG_QueDefWaiTime = 78;
    public static int MSG_QueDefWaitTime_RSP = 79;

    //推送消息
    public static int MSG_PushNotification = 99;

    //依标签发起推送消息
    public static int MSG_PushTag = 100;
    public static int MSG_PushTag_RSP = 101;

    //依商家定义区域推送内容
    public static int MSG_PushLocContent = 102;
    public static int MSG_PushLocContent_RSP = 103;

    //查询商家定义区域推送内容
    public static int MSG_PushLocContentSearch = 104;
    public static int MSG_PushLocContentSearch_RSP = 105;

    //当客户进入商家区域时,app端发起推送请求。
    public static int MSG_PushLoc_A2M = 106; //android
    public static int MSG_PushLoc_A2M_RSP = 107;
    public static int MSG_PushLoc_IOS_A2M = 108; //ios触发

    //当客户端触发推消息时,发回反馈信息给服务端
    public static int MSG_PushTrack = 109;
    public static int MSG_PushTrack_RSP = 110;

    //移除指定推送消息
    public static int MSG_PushMsgRemove = 111;
    public static int MSG_PushMsgRemove_RSP = 112;

    //将进入区域的客人信息广播给员工
    public static int MSG_ShopBroadcast_guest = 113; //客户app userid 广播

    //用于商家给员工发广播
    public static int MSG_ShopBroadcast_loc = 114;
    public static int MSG_ShopBroadcast_emp = 115;
    public static int MSG_ShopBroadcast_RSP = 116;
    public static int MSG_ShopBroadcast_M2C = 117;

    //在线员工设置上下班状态
    public static int MSG_EmpWorkStatus_set = 118;
    public static int MSG_EmpWorkStatus_set_RSP = 119;

    //员工上下班状态
    public static int MSG_EmpWorkStatus_search = 120;
    public static int MSG_EmpWorkStatus_search_RSP = 121;

    //商家员工情况列表
    public static int MSG_ShopEmplist = 122;
    public static int MSG_ShopEmplist_RSP = 123;

    //酒店预订
    public static int MSG_ShopRes_form = 124;
    public static int MSG_ShopRes_form_RSP = 125;

    //酒店确认信
    public static int MSG_ShopConf_letter = 126;
    public static int MSG_ShopConf_letter_RSP = 127;

    //客户会员卡信息
    public static int MSG_ShopSendUsercard_loc = 128;
    public static int MSG_ShopSendUsercard_emp = 129;
    public static int MSG_ShopSendUsercard_RSP = 130;

    //将客人userid转发给员工
    public static int MSG_ShopBroadcast_M2E = 131;

    // 商家给员工指派客人
    public static int MSG_ShopAssignment_S2C = 132;
    public static int MSG_ShopAssignment_S2C_RSP = 133;

    //客户呼叫服务
    public static int MSG_RequestWaiter_C2S = 135;
    public static int MSG_RequestWaiter_C2S_RSP = 136;

    //商家管理端将新建组ID分发给客人和服务员
    public static int MSG_DistributeGroupID_S2CE = 137;
    public static int MSG_DistributeGroupID_S2CE_RSP = 138;

    //员工通知商家当前所处最靠近的BeaconID
    public static int MSG_EmpUpdateLocal_E2S = 139;
    public static int MSG_EmpUpdateLocal_E2S_RSP = 140;

    //文本
    public static int MSG_CustomerServiceTextChat = 141;
    public static int MSG_CustomerServiceTextChat_RSP = 142;

    //语音
    public static int MSG_CustomerServiceMediaChat = 143;
    public static int MSG_CustomerServiceMediaChat_RSP = 144;

    //图片
    public static int MSG_CustomerServiceImgChat = 145;
    public static int MSG_CustomerServiceImgChat_RSP = 146;

    //转移至新服务器
    public static int MSG_TransferServer = 200;

    //当客人预订时,发出单号及客人userid至管理端
    public static int MSG_ShopResform_C2S = 203;
    public static int MSG_ShopResform_C2S_RSP = 204;

    //员工在线状态
    public static int MSG_EmpOnlineStatus = 205;

    //客人在线状态
    public static int MSG_GuestOnlineStatus_Search = 206;
    public static int MSG_GuestOnlineStatus_Search_RSP = 207;

    //推送触发人所在位置至商家管理端
    public static int MSG_PushTriggerLocNotification_M2S = 208;

    //在线员工变更其内存中的区域数组信息,以便消息服务器作相关处理。
    public static int MSG_EmpLocal_E2S = 209;
    public static int MSG_EmpLocal_E2S_RSP = 210;

    //进入商家区域后,将消息发送给商家管理端或员工(Android用户)
    public static int MSG_PushTriggerLocNotification_AND = 211;


    // 消息回执
    public static int MSG_SessionMsgReadAck = 212;
    public static int MSG_SessionMsgReadAck_RSP = 213;

    // 商家管理端邀请其它商家管理端或员工来参与
    public static int MSG_ShopAdminAdd_A2E = 214;
    public static int MSG_ShopAdminAdd_A2E_RSP = 215;

    //退出Session,不再参与会话
    public static int MSG_ShopExitSession = 216;
    public static int MSG_ShopExitSession_RSP = 217;

    //查找Session成员及在线状态
    public static int MSG_ShopSessionSearch = 218;
    public static int MSG_ShopSessionSearch_RSP = 219;

    // 商家管理端解散会话
    public static int MSG_ShopDisbandSession = 220;
    public static int MSG_ShopDisbandSession_RSP = 221;

    //当重新用旧会话聊天时,需先发此消息给服务器建立会话缓存,否则会不知道传给哪些用户
    public static int MSG_InsertOldSession = 222;
    public static int MSG_InsertOldSession_RSP = 223;

    // 依规则表分配对应客服,然后转发给客人,通知他已安排人处理
    public static int MSG_ShopAssignment_S2C_MSG = 224;

    //当订单状态发生改变时,推送
    public static int MSG_ShopOrderStatus_IOS = 225;
    public static int MSG_ShopOrderStatus_IOS_RSP = 226;

    public static int MSG_OfflineMssage = 227;
    public static int MSG_OfflineMssage_RSP = 228;

    // 消息规则预埋
    public static int MSG_RuleAdd = 500;
    public static int MSG_RuleAdd_RSP = 501;

    // 消息规则修改
    public static int MSG_RuleModify = 502;
    public static int MSG_RuleModify_RSP = 503;

    // 消息规则查询
    public static int MSG_RuleSearch = 504;
    public static int MSG_RuleSearch_RSP = 505;

    // 消息规则移除
    public static int MSG_RuleRemove = 506;
    public static int MSG_RuleRemove_RSP = 507;
}
