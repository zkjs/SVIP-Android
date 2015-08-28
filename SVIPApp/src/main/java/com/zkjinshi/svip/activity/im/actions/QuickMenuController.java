package com.zkjinshi.svip.activity.im.actions;

import com.zkjinshi.svip.menu.vo.ActionType;
import com.zkjinshi.svip.menu.vo.MenuGroup;
import com.zkjinshi.svip.menu.vo.MenuItem;
import com.zkjinshi.svip.menu.vo.MenuType;
import com.zkjinshi.svip.utils.CacheUtil;

import java.util.ArrayList;

/**
 * 快捷菜单控制层
 * 开发者：JimmyZhang
 * 日期：2015/8/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class QuickMenuController {

    private QuickMenuController() {
    }

    private static QuickMenuController instance;

    public static synchronized QuickMenuController getInstance() {
        if (null == instance) {
            instance = new QuickMenuController();
        }
        return instance;
    }

    public ArrayList<MenuGroup> getMenuGroupList(String shopId) {
        boolean isInArea = CacheUtil.getInstance().isInArea(shopId);
        int orderStaus = CacheUtil.getInstance().getOrderStatus(shopId);
        ArrayList<MenuGroup> menuGroupList = null;
        switch (orderStaus) {
            case 2://已发送/确认
                menuGroupList = getSureOrderMenuGroupList();
                break;
            case 4://已入住
                if (isInArea) {//店内
                    menuGroupList = getInnerCheckInOrderMenuGroupList();
                } else {//店外
                    menuGroupList = getOuterCheckInOrderMenuGroupList();
                }
                break;
            default:
                menuGroupList = getNoOrderMenuGroupList();
                break;
        }
        return menuGroupList;
    }

    /**
     * 获取无订单菜单组集合
     *
     * @return
     */
    public ArrayList<MenuGroup> getNoOrderMenuGroupList() {
        //第一组按钮实例化
        ArrayList<MenuGroup> menuGroupList = new ArrayList<MenuGroup>();
        ArrayList<MenuItem> menuItemList = new ArrayList<MenuItem>();
        MenuItem menuItem = new MenuItem();
        menuItem.setMenuName("大床房");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("双床房");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("高层房");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("无烟房");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("角落房");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("我要加床");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setMenuItemList(menuItemList);
        menuGroup.setMenuName("订房");
        menuGroup.setMenuType(MenuType.MULTI);
        menuGroupList.add(menuGroup);
        //第二组按钮实例化
        menuItemList = new ArrayList<MenuItem>();
        menuItem = new MenuItem();
        menuItem.setMenuName("中餐厅");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("西餐厅");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuGroup = new MenuGroup();
        menuGroup.setMenuItemList(menuItemList);
        menuGroup.setMenuName("订餐");
        menuGroup.setMenuType(MenuType.MULTI);
        menuGroupList.add(menuGroup);
        //第三组按钮实例化
        menuItemList = new ArrayList<MenuItem>();
        menuItem = new MenuItem();
        menuItem.setMenuName("免前台");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("接送");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("客房定制");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuGroup = new MenuGroup();
        menuGroup.setMenuName("我的特权");
        menuGroup.setMenuItemList(menuItemList);
        menuGroup.setMenuType(MenuType.MULTI);
        menuGroupList.add(menuGroup);
        return menuGroupList;
    }

    /**
     * 获取已确认/已发送订单菜单组集合
     *
     * @return
     */
    public ArrayList<MenuGroup> getSureOrderMenuGroupList() {
        //第一组按钮实例化
        ArrayList<MenuGroup> menuGroupList = new ArrayList<MenuGroup>();
        ArrayList<MenuItem> menuItemList = new ArrayList<MenuItem>();
        MenuItem menuItem = new MenuItem();
        menuItem.setMenuName("预定状态");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("修改订单");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("发票要求");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setMenuItemList(menuItemList);
        menuGroup.setMenuName("我的预定");
        menuGroup.setMenuType(MenuType.MULTI);
        menuGroupList.add(menuGroup);
        //第二组按钮实例化
        menuItemList = new ArrayList<MenuItem>();
        menuItem = new MenuItem();
        menuItem.setMenuName("免前台");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("接送");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("客房定制");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuGroup = new MenuGroup();
        menuGroup.setMenuItemList(menuItemList);
        menuGroup.setMenuName("我的特权");
        menuGroup.setMenuType(MenuType.MULTI);
        menuGroupList.add(menuGroup);
        //第三组按钮实例化
        menuItemList = new ArrayList<MenuItem>();
        menuItem = new MenuItem();
        menuItem.setMenuName("出行信息");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("旅游信息");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("娱乐信息");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuGroup = new MenuGroup();
        menuGroup.setMenuName("我的推荐");
        menuGroup.setMenuItemList(menuItemList);
        menuGroup.setMenuType(MenuType.MULTI);
        menuGroupList.add(menuGroup);
        return menuGroupList;
    }

    /**
     * 获取店外已入住订单菜单组集合
     *
     * @return
     */
    public ArrayList<MenuGroup> getOuterCheckInOrderMenuGroupList() {
        //第一组按钮实例化
        ArrayList<MenuGroup> menuGroupList = new ArrayList<MenuGroup>();
        ArrayList<MenuItem> menuItemList = new ArrayList<MenuItem>();
        MenuItem menuItem = new MenuItem();
        menuItem.setMenuName("订单状态");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("修改订单");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("发票要求");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setMenuItemList(menuItemList);
        menuGroup.setMenuName("我的预定");
        menuGroup.setMenuType(MenuType.MULTI);
        menuGroupList.add(menuGroup);
        //第二组按钮实例化
        menuItemList = new ArrayList<MenuItem>();
        menuItem = new MenuItem();
        menuItem.setMenuName("打扫");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("洗衣");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("订餐");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("加床");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("更换床品");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("添加物品");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuGroup = new MenuGroup();
        menuGroup.setMenuItemList(menuItemList);
        menuGroup.setMenuName("我的客房");
        menuGroup.setMenuType(MenuType.MULTI);
        menuGroupList.add(menuGroup);
        //第三组按钮实例化
        menuItemList = new ArrayList<MenuItem>();
        menuItem = new MenuItem();
        menuItem.setMenuName("出行信息");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("旅游信息");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("娱乐信息");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuGroup = new MenuGroup();
        menuGroup.setMenuName("我的推荐");
        menuGroup.setMenuItemList(menuItemList);
        menuGroup.setMenuType(MenuType.MULTI);
        menuGroupList.add(menuGroup);
        return menuGroupList;
    }

    /**
     * 获取店内已入住订单菜单组集合
     *
     * @return
     */
    public ArrayList<MenuGroup> getInnerCheckInOrderMenuGroupList() {
        //第一组按钮实例化
        ArrayList<MenuGroup> menuGroupList = new ArrayList<MenuGroup>();
        ArrayList<MenuItem> menuItemList = new ArrayList<MenuItem>();
        MenuItem menuItem = new MenuItem();
        menuItem.setMenuName("打扫");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("洗衣");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("订餐");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("加床");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("更换床品");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("添加物品");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setMenuItemList(menuItemList);
        menuGroup.setMenuName("我的客房");
        menuGroup.setMenuType(MenuType.MULTI);
        menuGroupList.add(menuGroup);
        //第二组按钮实例化
        menuItemList = new ArrayList<MenuItem>();
        menuItem = new MenuItem();
        menuItem.setMenuName("我的账单");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("发票要求");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("预约退房");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuGroup = new MenuGroup();
        menuGroup.setMenuItemList(menuItemList);
        menuGroup.setMenuName("我的消费");
        menuGroup.setMenuType(MenuType.MULTI);
        menuGroupList.add(menuGroup);
        //第三组按钮实例化
        menuItemList = new ArrayList<MenuItem>();
        menuItem = new MenuItem();
        menuItem.setMenuName("出行信息");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("旅游信息");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("娱乐信息");
        menuItem.setActionType(ActionType.CHAT);
        menuItemList.add(menuItem);
        menuGroup = new MenuGroup();
        menuGroup.setMenuName("我的推荐");
        menuGroup.setMenuItemList(menuItemList);
        menuGroup.setMenuType(MenuType.MULTI);
        menuGroupList.add(menuGroup);
        return menuGroupList;
    }

}
