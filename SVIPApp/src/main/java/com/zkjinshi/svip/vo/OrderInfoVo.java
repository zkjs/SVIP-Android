package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * Created by djd on 2015/8/25.
 */
public class OrderInfoVo implements Serializable {

    private String id;
    private String fullname;
    private String reservation_no;
    private String userid;
    private String shopid;
    private String res_username;
    private String arrival_date;
    private String departure_date;
    private String arrival_time;
    private String rooms;
    private String room_typeid;
    private String room_type;
    private String room_rate;
    private String company;
    private String payment;
    private String guest;
    private String guesttel;
    private String status;
    private String confirmation_no;
    private String remark;
    private String nologin;
    private String created;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getReservation_no() {
        return reservation_no;
    }

    public void setReservation_no(String reservation_no) {
        this.reservation_no = reservation_no;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getRes_username() {
        return res_username;
    }

    public void setRes_username(String res_username) {
        this.res_username = res_username;
    }

    public String getArrival_date() {
        return arrival_date;
    }

    public void setArrival_date(String arrival_date) {
        this.arrival_date = arrival_date;
    }

    public String getDeparture_date() {
        return departure_date;
    }

    public void setDeparture_date(String departure_date) {
        this.departure_date = departure_date;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(String arrival_time) {
        this.arrival_time = arrival_time;
    }

    public String getRooms() {
        return rooms;
    }

    public void setRooms(String rooms) {
        this.rooms = rooms;
    }

    public String getRoom_typeid() {
        return room_typeid;
    }

    public void setRoom_typeid(String room_typeid) {
        this.room_typeid = room_typeid;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public String getRoom_rate() {
        return room_rate;
    }

    public void setRoom_rate(String room_rate) {
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

    public String getGuesttel() {
        return guesttel;
    }

    public void setGuesttel(String guesttel) {
        this.guesttel = guesttel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConfirmation_no() {
        return confirmation_no;
    }

    public void setConfirmation_no(String confirmation_no) {
        this.confirmation_no = confirmation_no;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getNologin() {
        return nologin;
    }

    public void setNologin(String nologin) {
        this.nologin = nologin;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    //    id               int(10)        (NULL)           NO
//    reservation_no   varchar(32)      YES     预订单号
//    userid           varchar(32)      YES     app用户ID
//    shopid           varchar(32)      YES    商家ID
//    res_username     varchar(30)      YES    姓名
//    arrival_date     date           (NULL)           YES	入住日期
//    departure_date   date           (NULL)           YES	离店日期
//    arrival_time     varchar(32)      YES	预抵时间
//    rooms            int(11)        (NULL)           YES	房数
//    room_typeid      int(10)        (NULL)           YES	房型ID
//    room_type        varchar(50)      YES	房型
//    room_rate        decimal(10,2)  (NULL)           YES	房价
//    company          varchar(100)     YES	公司名
//    payment          varchar(20)      YES	付款方式
//    guest            varchar(30)      YES	预定人
//    guesttel         varchar(30)      YES	预定人电话
//    status           tinyint(1)       YES	状态 默认0 未确认可取消订单 1取消订单 2已确认订单 3已经完成的订单 4正在入住中 5已删除订单
//    confirmation_no  varchar(30)      YES	confirmation no
//    remark           varchar(500)     YES	备注
//    nologin          int                      0有前台 1免前台  默认为0需要前台入住
//    created          datetime         YES    CURRENT_TIMESTAMP生成时间
}
