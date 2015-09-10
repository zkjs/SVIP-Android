package com.zkjinshi.svip.response;

import com.zkjinshi.svip.http.HttpResponse;

/**
 * Created by djd on 2015/9/6.
 */
public class OrderRoomResponse  extends HttpResponse {

    private String  reservation_no;// 预定单号
    private String  userid ;//用户id
    private String  shopid ;//商家id int
    private String  fullname ;//商家名称
    private String  room_typeid ;//房间id
    private String  room_type;// 房间类型
    private int  rooms;// 房间数 int
    private String  arrival_date;// 入住时间 data
    private String  departure_date;// 离店时间 data
    private String  room_rate;// 房价 int.00
    private String  status;// 订单状态 默认0可取消订单 1已取消订单 2已确认订单 3已经完成的订单 4正在入住中 5已删除订单 int
    private String  remark;// 备注内容
    private String  pay_status;// 支付状态 0未支付,1已支付,3支付一部分,4已退款, 5已挂账   int
    private String imgurl;

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
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

    public String getRoom_rate() {
        return room_rate;
    }

    public void setRoom_rate(String room_rate) {
        this.room_rate = room_rate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPay_status() {
        return pay_status;
    }

    public void setPay_status(String pay_status) {
        this.pay_status = pay_status;
    }
}
