package com.zkjinshi.svip.bean;

import java.io.Serializable;

/**
 * Created by vincent on 2015/6/8.
 */
public class BookOrder implements Serializable{

    public final static String TRADE_FINISHED   = "trade_finished"; //  交易成功
    public final static String TRADE_SUCCESS    = "trade_success";  //  支付成功
    public final static String WAIT_BUYER_PAY   = "wait_buyer_pay"; //  交易创建
    public final static String TRADE_CLOSED     = "trade_closed";   //  交易关闭
    public final static String FALSE            = "false";          //  未支付

    public final static int ORDER_UNCONFIRMED   = 0; //  默认0 未确认
    public final static int ORDER_CANCELLED     = 1; //  1 订单已取消
    public final static int ORDER_CONFIRMED     = 2; //  2 已确认订单
    public final static int ORDER_FINISHED      = 3; //  3 已经完成的订单
    public final static int ORDER_USING         = 4; //  4 正在入住中
    public final static int ORDER_DELETED       = 5; //  5 已删除订单

    private String id;//用户id
    private String reservation_no;//预订单号
    private String userid;//app用户ID
    private String shopid;//商家ID
    private String res_username;//姓名
    private String arrival_date;//入住日期
    private String departure_date;//离店日期
    private int dayNum;
    private String fullname;
    private String arrival_time;//预抵时间
    private String rooms;//房数
    private String room_typeid;//房型ID
    private String room_type;//房型
    private String room_rate;//房价
    private String company;//公司名
    private String payment;//付款方式
    private String guest;//预定人
    private String guesttel;//预定人电话
    private String status;//状态 0:未确认生效 1:取消状态 2:确认成功,3:交易完成,4:推迟
    private String confirmation_no;//confirmation no
    private String remark;//备注
    private String created;//CURRENT_TIMESTAMP生成时间
    private String trade_status;//支付状态
    private String content;//默认发送订单提示消息内容
    private String image;//酒店对应图片后缀
    private String manInStay;  //入住的人，如果多个人，用逗号隔开

    public String getManInStay() {
        return manInStay;
    }

    public void setManInStay(String manInStay) {
        this.manInStay = manInStay;
    }

    public static String getTradeFinished() {
        return TRADE_FINISHED;
    }

    public static String getTradeSuccess() {
        return TRADE_SUCCESS;
    }

    public static String getWaitBuyerPay() {
        return WAIT_BUYER_PAY;
    }

    public static String getTradeClosed() {
        return TRADE_CLOSED;
    }

    public static String getFALSE() {
        return FALSE;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReservationNO() {
        return reservation_no;
    }

    public void setReservationNO(String reservation_no) {
        this.reservation_no = reservation_no;
    }

    public String getUserID() {
        return userid;
    }

    public void setUserID(String userid) {
        this.userid = userid;
    }

    public String getShopID() {
        return shopid;
    }

    public void setShopID(String shopid) {
        this.shopid = shopid;
    }

    public String getResUserName() {
        return res_username;
    }

    public void setResUserName(String res_username) {
        this.res_username = res_username;
    }

    public String getArrivalDate() {
        return arrival_date;
    }

    public void setArrivalDate(String arrival_date) {
        this.arrival_date = arrival_date;
    }

    public String getDepartureDate() {
        return departure_date;
    }

    public void setDepartureDate(String departure_date) {
        this.departure_date = departure_date;
    }

    public String getArrivalTime() {
        return arrival_time;
    }

    public void setArrivalTime(String arrival_time) {
        this.arrival_time = arrival_time;
    }

    public String getRooms() {
        return rooms;
    }

    public void setRooms(String rooms) {
        this.rooms = rooms;
    }

    public String getRoomTypeID() {
        return room_typeid;
    }

    public void setRoomTypeID(String room_typeid) {
        this.room_typeid = room_typeid;
    }

    public String getRoomType() {
        return room_type;
    }

    public void setRoomType(String room_type) {
        this.room_type = room_type;
    }

    public String getRoomRate() {
        return room_rate;
    }

    public void setRoomRate(String room_rate) {
        this.room_rate = room_rate;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getGuest() {
        return guest;
    }

    public void setGuest(String guest) {
        this.guest = guest;
    }

    public String getGuestTel() {
        return guesttel;
    }

    public void setGuestTel(String guesttel) {
        this.guesttel = guesttel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConfirmationNO() {
        return confirmation_no;
    }

    public void setConfirmationNO(String confirmation_no) {
        this.confirmation_no = confirmation_no;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getTradeStatus() {
        return trade_status;
    }

    public void setTradeStatus(String trade_status) {
        this.trade_status = trade_status;
    }
    public String getFullName() {
        return fullname;
    }
    public void setFullame(String fullname) {
        this.fullname = fullname;
    }

    public int getDayNum() {
        return dayNum;
    }

    public void setDayNum(int dayNum) {
        this.dayNum = dayNum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
