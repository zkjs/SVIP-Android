package com.zkjinshi.svip.response;

import java.util.ArrayList;

/**
 * Created by dujiande on 2016/3/15.
 */
public class GetBigPicResponse extends BaseFornaxResponse {

    private ArrayList<BigPicResponse> data;

    public ArrayList<BigPicResponse> getData() {
        return data;
    }

    public void setData(ArrayList<BigPicResponse> data) {
        this.data = data;
    }
}
