package com.zkjinshi.svip.response;

import java.io.Serializable;

/**
 * Created by dujiande on 2015/12/18.
 */
public class PrivilegeResponse implements Serializable {

    private String privilegeDesc;
    private String privilegeIcon;
    private  String privilegeName;

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
