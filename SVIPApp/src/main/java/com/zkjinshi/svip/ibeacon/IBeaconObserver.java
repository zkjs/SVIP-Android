package com.zkjinshi.svip.ibeacon;

/**
 * 开发者：JimmyZhang
 * 日期：2015/8/6
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public interface IBeaconObserver {

    /**
     * 进入IBeacon某个区域
     * @param regionVo
     */
    public void intoRegion(RegionVo regionVo);

    /**
     * 从某个区域离开
     * @param regionVo
     */
    public void outRegin(RegionVo regionVo);

}
