package com.zkjinshi.svip.response;

/**
 * Created by dujiande on 2015/9/9.
 */
public class OrderLastResponse extends BaseResponse{

   private String reservation_no;// 预定单号
   private String userid;// 用户id
   private String shopid;// 商家id
   private String fullname;// 商家名称
   private String phone;// 商家电话 order/last only #
   private String room_type;// 房间类型
   private String rooms;// 房间数
   private String arrival_date;// 入住时间
   private String departure_date;// 离店时间
   private String room_rate;// 房价
   private String guest;// 预定人
   private String nologin;// 免前台 1真,0假
   private String status;// 订单状态 默认0可取消订单 1已取消订单 2已确认订单 3已经完成的订单 4正在入住中 5已删除订单
   private String pay_status;// 支付状态 0未支付,1已支付,3支付一部分,4已退款, 5已挂账

    private int score;
    private int star;

    private double map_longitude;
    private double map_latitude;

    public double getMap_longitude() {
        return map_longitude;
    }

    public void setMap_longitude(double map_longitude) {
        this.map_longitude = map_longitude;
    }

    public double getMap_latitude() {
        return map_latitude;
    }

    public void setMap_latitude(double map_latitude) {
        this.map_latitude = map_latitude;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public String getRooms() {
        return rooms;
    }

    public void setRooms(String rooms) {
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

    public String getGuest() {
        return guest;
    }

    public void setGuest(String guest) {
        this.guest = guest;
    }

    public String getNologin() {
        return nologin;
    }

    public void setNologin(String nologin) {
        this.nologin = nologin;
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
}
