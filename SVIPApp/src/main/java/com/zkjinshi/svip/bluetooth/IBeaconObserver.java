package com.zkjinshi.svip.bluetooth;


/**
 * 开发者：杜健德
 * 日期：2016/01/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public interface IBeaconObserver {

    /**
     * 进入IBeacon某个区域
     * @param iBeaconVo
     */
    public void intoRegion(IBeaconVo iBeaconVo);

    /**
     * 从某个区域离开
     * @param iBeaconVo
     */
    public void outRegin(IBeaconVo iBeaconVo);

}
