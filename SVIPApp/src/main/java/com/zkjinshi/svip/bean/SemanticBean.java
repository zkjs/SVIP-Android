package com.zkjinshi.svip.bean;

import java.io.Serializable;

/**
 * 开发者：WinkyQin
 * 日期：2016/1/29 0029
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SemanticBean implements Serializable {

    public SlotsBean slots;

    public SlotsBean getSlots() {
        return slots;
    }

    public void setSlots(SlotsBean slots) {
        this.slots = slots;
    }
}
