package com.zkjinshi.svip.response;

import com.zkjinshi.svip.vo.GoodInfoVo;

import java.util.ArrayList;

/**
 * Created by dujiande on 2016/3/15.
 */
public class GetMessageDefaultResponse extends BaseFornaxResponse {

    private ArrayList<MessageDefaultResponse> data;

    public ArrayList<MessageDefaultResponse> getData() {
        return data;
    }

    public void setData(ArrayList<MessageDefaultResponse> data) {
        this.data = data;
    }
}
