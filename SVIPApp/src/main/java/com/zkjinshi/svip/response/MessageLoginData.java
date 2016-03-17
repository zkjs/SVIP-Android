package com.zkjinshi.svip.response;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dujiande on 2016/3/16.
 */
public class MessageLoginData implements Serializable {

    private HomeOrderModel homeOrderModel;
    private ArrayList<PrivilegeResponse> homePrivilegeModels;
    private ArrayList<MessageDefaultResponse> recommendShops;

    public HomeOrderModel getHomeOrderModel() {
        return homeOrderModel;
    }

    public void setHomeOrderModel(HomeOrderModel homeOrderModel) {
        this.homeOrderModel = homeOrderModel;
    }

    public ArrayList<PrivilegeResponse> getHomePrivilegeModels() {
        return homePrivilegeModels;
    }

    public void setHomePrivilegeModels(ArrayList<PrivilegeResponse> homePrivilegeModels) {
        this.homePrivilegeModels = homePrivilegeModels;
    }

    public ArrayList<MessageDefaultResponse> getRecommendShops() {
        return recommendShops;
    }

    public void setRecommendShops(ArrayList<MessageDefaultResponse> recommendShops) {
        this.recommendShops = recommendShops;
    }
}
