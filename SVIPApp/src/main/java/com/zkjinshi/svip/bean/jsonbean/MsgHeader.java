package com.zkjinshi.svip.bean.jsonbean;

/**
 * Created by vincent on 2015/7/11.
 * 协议消息头
 */
public class MsgHeader {

    public int     type;//not  null //协议消息类型
    public long    timestamp;//not  null //当前时间
    public String  tempid;//app端临时ID
    public long    srvmsgid;
    public int     protover;//消息协议版本

}
