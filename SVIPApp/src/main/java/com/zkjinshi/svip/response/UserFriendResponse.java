package com.zkjinshi.svip.response;

import java.io.Serializable;

/**
 * Created by dujiande on 2015/11/27.
 */
public class UserFriendResponse implements Serializable {
    /*
        "id": 1,  数据库列id,前端无用
        "userid": "5551fc5b8c35e", 自己的id
        "phone": null, 好友手机号
        "fuid": "5555ee0c86e4c", 好友id
        "fname": "张排排", 好友名称
        "shopid": 120, 商家id
        "created": "2015-11-27 15:41:44", 添加时间
        "shop_name": null, 商家名称
        "teamid": int  分组id 预留,目前没用
     */

    private String id;
    private String userid;
    private String phone;
    private String fuid;
    private String fname;
    private String shopid;
    private String created;
    private String shop_name;
    private String teamid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFuid() {
        return fuid;
    }

    public void setFuid(String fuid) {
        this.fuid = fuid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getTeamid() {
        return teamid;
    }

    public void setTeamid(String teamid) {
        this.teamid = teamid;
    }
}
