package com.zkjinshi.svip.vo;

import java.io.Serializable;

/**
 * Created by dujiande on 2016/3/8.
 */
public class PayRecordDataVo implements Serializable {

    private String shopid;
    private String shopname;
    private String alert;
    private String createtime;
    private double amount;
    private String orderno;
    private String paymentno;
    private String status;
    private String statusdesc;
    private String confirmtime;
    private boolean isShow = false;
    private YunBaMsgVo yunBaMsgVo = null;
    private InvitationVo invitationVo = null;

    public YunBaMsgVo getYunBaMsgVo() {
        return yunBaMsgVo;
    }

    public void setYunBaMsgVo(YunBaMsgVo yunBaMsgVo) {
        this.yunBaMsgVo = yunBaMsgVo;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getPaymentno() {
        return paymentno;
    }

    public void setPaymentno(String paymentno) {
        this.paymentno = paymentno;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusdesc() {
        return statusdesc;
    }

    public void setStatusdesc(String statusdesc) {
        this.statusdesc = statusdesc;
    }

    public String getConfirmtime() {
        return confirmtime;
    }

    public void setConfirmtime(String confirmtime) {
        this.confirmtime = confirmtime;
    }

    public InvitationVo getInvitationVo() {
        return invitationVo;
    }

    public void setInvitationVo(InvitationVo invitationVo) {
        this.invitationVo = invitationVo;
    }
}
