package com.zkjinshi.svip.bean.jsonbean;

/**
 * 开发者：vincent
 * 日期：2015/8/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MsgSessionID {

//    type MsgSessionID struct {
//        SessionID string `json:"sessionid,omitempty"` // timestamp_发起者userid,中间以"_"作连接
//        SeqID     string `json:"seqid,omitempty"`     // 如需回执,则发送方需生成一个消息id并缓存,然后接收方则可针对此id发回执 ChildMsgID
//        IsReadAck uint32 `json:"isreadack,omitempty"` // 是否要求消息已读回执 0:不需要 1:需要
//    }

    public String sessionid;
    public String seqid;
    public String isreadack;


}
