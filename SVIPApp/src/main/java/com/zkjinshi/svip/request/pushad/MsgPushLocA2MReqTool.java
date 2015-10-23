package com.zkjinshi.svip.request.pushad;

import android.text.TextUtils;

import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.svip.bean.jsonbean.MsgPushLocA2M;
import com.zkjinshi.svip.ibeacon.IBeaconEntity;
import com.zkjinshi.svip.ibeacon.RegionVo;
import com.zkjinshi.svip.utils.CacheUtil;

/**
 * 开发者：JimmyZhang
 * 日期：2015/9/7
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MsgPushLocA2MReqTool {

    /**
     * 构建MsgPushLocA2M对象
     * @param regionVo
     * @return
     */
    public static MsgPushLocA2M  buildMsgPushLocA2M(RegionVo regionVo){
        MsgPushLocA2M msgPushLocA2M = new MsgPushLocA2M();
        msgPushLocA2M.setType(ProtocolMSG.MSG_PushLoc_A2M);
        msgPushLocA2M.setTimestamp(System.currentTimeMillis());
        msgPushLocA2M.setUserid(CacheUtil.getInstance().getUserId());
        msgPushLocA2M.setUsername(CacheUtil.getInstance().getUserName());
        IBeaconEntity iBeaconEntity =  regionVo.getiBeacon();
        if(null != iBeaconEntity){
            String shopId = iBeaconEntity.getShopid();
            String locId = iBeaconEntity.getLocid();
            String locDesc = iBeaconEntity.getLocdesc();
            if(!TextUtils.isEmpty(shopId)){
                msgPushLocA2M.setShopid(shopId);
            }
            if(!TextUtils.isEmpty(locId)){
                msgPushLocA2M.setLocid(locId);
            }
            if(!TextUtils.isEmpty(locDesc)){
                msgPushLocA2M.setLocdesc(locDesc);
            }
        }
        return msgPushLocA2M;
    }
}
