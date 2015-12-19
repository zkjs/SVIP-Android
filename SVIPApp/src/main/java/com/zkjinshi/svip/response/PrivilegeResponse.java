package com.zkjinshi.svip.response;

import java.io.Serializable;

/**
 * Created by dujiande on 2015/12/18.
 */
public class PrivilegeResponse implements Serializable {

    private MessageCardResponse messageCard;
    private String privilegeDesc;
    private String privilegeIcon;
    private  String privilegeName;
    private String shopName;

    public MessageCardResponse getMessageCard() {
        return messageCard;
    }

    public void setMessageCard(MessageCardResponse messageCard) {
        this.messageCard = messageCard;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getPrivilegeDesc() {
        return privilegeDesc;
    }

    public void setPrivilegeDesc(String privilegeDesc) {
        this.privilegeDesc = privilegeDesc;
    }

    public String getPrivilegeIcon() {
        return privilegeIcon;
    }

    public void setPrivilegeIcon(String privilegeIcon) {
        this.privilegeIcon = privilegeIcon;
    }

    public String getPrivilegeName() {
        return privilegeName;
    }

    public void setPrivilegeName(String privilegeName) {
        this.privilegeName = privilegeName;
    }
}
