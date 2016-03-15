package com.zkjinshi.svip.response;

import com.zkjinshi.svip.bean.ShopBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dujiande on 2016/3/15.
 */
public class GetShopListResponse extends BaseFornaxResponse {

    private ArrayList<ShopBean> data;

    public ArrayList<ShopBean> getData() {
        return data;
    }

    public void setData(ArrayList<ShopBean> data) {
        this.data = data;
    }
}
