package com.zkjinshi.ritz.manager;

import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.ritz.sqlite.BleStatDBUtil;
import com.zkjinshi.ritz.utils.CacheUtil;
import com.zkjinshi.ritz.vo.BleStatVo;

/**
 * 蓝牙定义统计管理器
 * 开发者：JimmyZhang
 * 日期：2016/3/10
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class BleStatManager {

    private BleStatManager(){}

    private static BleStatManager instance;

    public synchronized static BleStatManager getInstance(){
        if(null == instance){
            instance = new BleStatManager();
            instance.init();
        }
        return instance;
    }

    /**
     * 初始化状态管理
     */
    private void init(){
        boolean statExist = BleStatDBUtil.getInstance().isStatExist();
        if(!statExist){
            BleStatVo bleStatVo = new BleStatVo();
            bleStatVo.setIMEI(DeviceUtils.getIMEI());
            bleStatVo.setTimestamp(System.currentTimeMillis());
            bleStatVo.setRetryCount(0l);
            bleStatVo.setTotalCount(0l);
            BleStatDBUtil.getInstance().insertBleLog(bleStatVo);
        }
    }

    /**
     * 更新网络请求总个数
     */
    public void updateTotalCount(){
        BleStatDBUtil.getInstance().updateTotalCount();
    }

    /**
     * 更新网络失败总个数
     */
    public void updateRetryCount(){
        BleStatDBUtil.getInstance().updateRetryCount();
    }

    /**
     * 更新上传统计的时间戳
     */
    public void updateStatTime(){
        BleStatDBUtil.getInstance().updateStatTime(System.currentTimeMillis());
    }

    /**
     * 获取统计数据
     * @return
     */
    public BleStatVo getStatLog(){
        return BleStatDBUtil.getInstance().queryBleStat();
    }

    /**
     *  判断是否需要更新统计
     * @return
     */
    public boolean isUpdateStat(){
        BleStatVo bleStatVo = BleStatDBUtil.getInstance().queryBleStat();
        if(null != bleStatVo && CacheUtil.getInstance().isLogin()){
            long lastUpdateTime = bleStatVo.getTimestamp();
            if(lastUpdateTime > 0){
                long dayNum = (System.currentTimeMillis() - lastUpdateTime) / (1000 * 60 * 60 * 24);
                if(dayNum >= 1){//超过一天则进行更新
                    return true;
                }
            }
        }
        return false;
    }
}
