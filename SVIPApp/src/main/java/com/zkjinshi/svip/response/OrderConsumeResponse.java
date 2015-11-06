package com.zkjinshi.svip.response;

import java.io.Serializable;

/**
 * 开发者：dujiande
 * 日期：2015/11/6
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderConsumeResponse implements Serializable {

    private String reservation_no;
    private String created;
    private String room_rate;
    private String fullname;
    private String userid;
    private String shopid;
    private String arrival_date;
    private String departure_date;
    private String room_type;
    private String guest;
    private String guesttel;
    private String nologin;
    private String rooms;
    private String status;
    private String pay_status;
    private String score;

    public String getReservation_no() {
        return reservation_no;
    }

    public void setReservation_no(String reservation_no) {
        this.reservation_no = reservation_no;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getRoom_rate() {
        return room_rate;
    }

    public void setRoom_rate(String room_rate) {
        this.room_rate = room_rate;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
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

    public String getNologin() {
        return nologin;
    }

    public void setNologin(String nologin) {
        this.nologin = nologin;
    }

    public String getRooms() {
        return rooms;
    }

    public void setRooms(String rooms) {
        this.rooms = rooms;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPay_status() {
        return pay_status;
    }

    public void setPay_status(String pay_status) {
        this.pay_status = pay_status;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
