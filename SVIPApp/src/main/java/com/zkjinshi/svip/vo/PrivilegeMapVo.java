package com.zkjinshi.svip.vo;

import com.zkjinshi.svip.response.PrivilegeResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dujiande on 2016/1/11.
 */
public class PrivilegeMapVo implements Serializable{

    private HashMap<String,ArrayList<PrivilegeResponse>> privilegeMap;

    public HashMap<String, ArrayList<PrivilegeResponse>> getPrivilegeMap() {
        return privilegeMap;
    }

    public void setPrivilegeMap(HashMap<String, ArrayList<PrivilegeResponse>> privilegeMap) {
        this.privilegeMap = privilegeMap;
    }
}
