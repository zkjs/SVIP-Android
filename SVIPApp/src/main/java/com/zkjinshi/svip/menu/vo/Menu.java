package com.zkjinshi.svip.menu.vo;

import java.io.Serializable;

/**
 * 菜单对象父类
 * 开发者：JimmyZhang
 * 日期：2015/8/21
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class Menu implements Serializable {

    private String menuName;
    private ActionType actionType;

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }
}
