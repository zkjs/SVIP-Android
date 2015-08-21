package com.zkjinshi.svip.vo;

/**
 * 聊天室信息ui操作操作相关实体
 * 开发者：JimmyZhang
 * 日期：2015/7/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatRoomVo {

    private String  shopid; //主键 ShopID
    private String  shopName;  //聊天室商家名称
    private String  sessionid; //聊天室ID
    private String  remark;    //备注
    private long    created;   //创建时间
    private long    endtime;   //结束时间
    private String  enduserid; //结束人ID
    private String  clientid;  //创建人ID
    private String  clientname;//创建人姓名
    private VisibleStatus  isVisible;//是否UI显示

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public String getEnduserid() {
        return enduserid;
    }

    public void setEnduserid(String enduserid) {
        this.enduserid = enduserid;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public String getClientname() {
        return clientname;
    }

    public void setClientname(String clientname) {
        this.clientname = clientname;
    }

    public VisibleStatus getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(VisibleStatus isVisible) {
        this.isVisible = isVisible;
    }
}
