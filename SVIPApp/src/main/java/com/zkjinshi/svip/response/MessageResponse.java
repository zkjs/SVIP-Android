package com.zkjinshi.svip.response;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dujiande on 2016/1/9.
 */
public class MessageResponse implements Serializable{
    ArrayList<MessageDefaultResponse> defaultNotitification;
    ArrayList<MessageDefaultResponse> notificationForOrder;
    ArrayList<PrivilegeResponse> userPrivilege;

    public ArrayList<MessageDefaultResponse> getDefaultNotitification() {
        return defaultNotitification;
    }

    public void setDefaultNotitification(ArrayList<MessageDefaultResponse> defaultNotitification) {
        this.defaultNotitification = defaultNotitification;
    }

    public ArrayList<MessageDefaultResponse> getNotificationForOrder() {
        return notificationForOrder;
    }

    public void setNotificationForOrder(ArrayList<MessageDefaultResponse> notificationForOrder) {
        this.notificationForOrder = notificationForOrder;
    }

    public ArrayList<PrivilegeResponse> getUserPrivilege() {
        return userPrivilege;
    }

    public void setUserPrivilege(ArrayList<PrivilegeResponse> userPrivilege) {
        this.userPrivilege = userPrivilege;
    }
}
