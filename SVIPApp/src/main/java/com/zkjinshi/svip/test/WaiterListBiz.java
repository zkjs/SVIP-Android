package com.zkjinshi.svip.test;

import com.zkjinshi.svip.vo.WaiterVo;

import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2016/4/22
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class WaiterListBiz {

    /**
     * 获取服务员列表数据
     * @return
     */
    public static ArrayList<WaiterVo> getWaiterList(){
        ArrayList<WaiterVo> waiterList = new ArrayList<WaiterVo>();
        WaiterVo waiterVo = new WaiterVo();
        waiterVo.setUserimage("uploads/users/c_25174689826741ac_1461133626939.jpg");
        waiterVo.setUsername("王露露");
        waiterList.add(waiterVo);
        waiterVo.setUserimage("uploads/users/c_472b4c659c895e64_1460531297309.jpg");
        waiterVo.setUsername("张三");
        waiterList.add(waiterVo);
        waiterVo.setUserimage("uploads/users/c_87db4f52b941071b_1460429060795.jpg");
        waiterVo.setUsername("李斯");
        waiterList.add(waiterVo);
        waiterVo.setUserimage("uploads/users/c_41bc43d5afe3e213_1460208000486.jpg");
        waiterVo.setUsername("贾宝玉");
        waiterList.add(waiterVo);
        waiterVo.setUserimage("uploads/users/c_41bc43d5afe3e213_1460208000486.jpg");
        waiterVo.setUsername("王熙凤");
        waiterList.add(waiterVo);
        waiterVo.setUserimage("uploads/users/c_41bc43d5afe3e213_1460208000486.jpg");
        waiterVo.setUsername("西门庆");
        waiterList.add(waiterVo);
        waiterVo.setUserimage("uploads/users/c_25174689826741ac_1460085424862.jpg");
        waiterVo.setUsername("古天乐");
        waiterList.add(waiterVo);
        waiterVo.setUserimage("uploads/users/c_8ff243c687ec0b96_1460201923497.jpg");
        waiterVo.setUsername("周杰伦");
        waiterList.add(waiterVo);
        return waiterList;
    }
}
