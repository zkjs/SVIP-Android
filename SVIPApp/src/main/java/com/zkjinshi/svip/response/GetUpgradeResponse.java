package com.zkjinshi.svip.response;

import com.zkjinshi.svip.bean.UpdateBean;

import java.util.ArrayList;

/**
 * Created by dujiande on 2016/3/15.
 */
public class GetUpgradeResponse extends BaseFornaxResponse {

    private UpdateBean data;

    public UpdateBean getData() {
        return data;
    }

    public void setData(UpdateBean data) {
        this.data = data;
    }
}
