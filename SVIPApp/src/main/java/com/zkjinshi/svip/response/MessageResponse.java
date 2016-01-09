package com.zkjinshi.svip.response;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dujiande on 2016/1/9.
 */
public class MessageResponse implements Serializable{
    ArrayList<MessageDefaultResponse> defaultNotitifications;
    ArrayList<MessageDefaultResponse> recommShop;
    ArrayList<PrivilegeResponse> userPrivilege;

    public ArrayList<MessageDefaultResponse> getDefaultNotitifications() {
        return defaultNotitifications;
    }

    public void setDefaultNotitifications(ArrayList<MessageDefaultResponse> defaultNotitifications) {
        this.defaultNotitifications = defaultNotitifications;
    }

    public ArrayList<MessageDefaultResponse> getRecommShop() {
        return recommShop;
    }

    public void setRecommShop(ArrayList<MessageDefaultResponse> recommShop) {
        this.recommShop = recommShop;
    }

    public ArrayList<PrivilegeResponse> getUserPrivilege() {
        return userPrivilege;
    }

    public void setUserPrivilege(ArrayList<PrivilegeResponse> userPrivilege) {
        this.userPrivilege = userPrivilege;
    }
}
