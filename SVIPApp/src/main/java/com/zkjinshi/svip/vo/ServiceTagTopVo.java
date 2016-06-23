package com.zkjinshi.svip.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dujiande on 2016/3/8.
 */
public class ServiceTagTopVo implements Serializable{

    private String locdesc;
    private ArrayList<ServiceTagDataFirstVo> services;

    public String getLocdesc() {
        return locdesc;
    }

    public void setLocdesc(String locdesc) {
        this.locdesc = locdesc;
    }

    public ArrayList<ServiceTagDataFirstVo> getServices() {
        return services;
    }

    public void setServices(ArrayList<ServiceTagDataFirstVo> services) {
        this.services = services;
    }
}
