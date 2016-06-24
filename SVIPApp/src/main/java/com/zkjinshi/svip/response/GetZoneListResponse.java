package com.zkjinshi.svip.response;



import com.zkjinshi.svip.vo.ZoneVo;

import java.util.ArrayList;

/**
 * Created by dujiande on 2016/3/16.
 */
public class GetZoneListResponse extends BaseFornaxResponse {

    private ArrayList<ZoneVo> data;

    public ArrayList<ZoneVo> getData() {
        return data;
    }

    public void setData(ArrayList<ZoneVo> data) {
        this.data = data;
    }
}
