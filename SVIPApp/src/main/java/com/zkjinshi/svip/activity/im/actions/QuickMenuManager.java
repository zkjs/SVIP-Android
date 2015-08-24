package com.zkjinshi.svip.activity.im.actions;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.menu.view.MenuLayoutView;
import com.zkjinshi.svip.menu.vo.ActionType;
import com.zkjinshi.svip.menu.vo.MenuGroup;
import com.zkjinshi.svip.menu.vo.MenuItem;
import com.zkjinshi.svip.menu.vo.MenuType;

import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2015/8/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class QuickMenuManager {

    private Context context;
    private LinearLayout quickMenuLayout,chatKeyboardLayout;
    private ImageButton switchQuickMenuIv,switchChatKeyboardIv;
    private LinearLayout menuContainerLayout;
    private ArrayList<MenuGroup> menuGroupList;
    private ArrayList<MenuItem> menuItemList;
    private MenuGroup menuGroup;
    private MenuItem menuItem;
    private MenuLayoutView menuLayoutView;
    private LinearLayout.LayoutParams layoutParams;

    private QuickMenuManager(){}

    public static QuickMenuManager instance;

    public static synchronized QuickMenuManager getInstance(){
        if(null ==  instance){
            instance = new QuickMenuManager();
        }
        return instance;
    }

    public void init(Context context){
        this.context = context;
        initView(context);
        initData();
        initListeners();
    }

    private void initView(Context context){
        quickMenuLayout = (LinearLayout)((Activity)context).findViewById(R.id.acion_keyboard_layout);
        chatKeyboardLayout = (LinearLayout)((Activity)context).findViewById(R.id.chat_keyboard_layout);
        switchQuickMenuIv = (ImageButton)((Activity)context).findViewById(R.id.switch_quick_menu);
        switchChatKeyboardIv = (ImageButton)((Activity)context).findViewById(R.id.switch_chat_keyboard);
        menuContainerLayout = (LinearLayout)((Activity)context).findViewById(R.id.quick_menu_container_layout);
    }

    private void initData(){
        quickMenuLayout.setVisibility(View.VISIBLE);
        chatKeyboardLayout.setVisibility(View.GONE);
        //第一组按钮实例化
        menuGroupList = new ArrayList<MenuGroup>();
        menuItemList = new ArrayList<MenuItem>();
        menuItem = new MenuItem();
        menuItem.setMenuName("最新推荐");
        menuItem.setActionType(ActionType.PUSH);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("77元特价房");
        menuItem.setActionType(ActionType.PUSH);
        menuItemList.add(menuItem);
        menuGroup = new MenuGroup();
        menuGroup.setMenuItemList(menuItemList);
        menuGroup.setMenuName("订房");
        menuGroup.setMenuType(MenuType.MULTI);
        menuGroupList.add(menuGroup);
        //第二组按钮实例化
        menuItemList = new ArrayList<MenuItem>();
        menuItem = new MenuItem();
        menuGroup = new MenuGroup();
        menuItem.setMenuName("KFC");
        menuItem.setActionType(ActionType.PUSH);
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("必胜客");
        menuItem.setActionType(ActionType.PUSH);
        menuItemList.add(menuItem);
        menuGroup.setMenuItemList(menuItemList);
        menuGroup.setMenuName("订餐");
        menuGroup.setMenuType(MenuType.MULTI);
        menuGroupList.add(menuGroup);
        //第三组按钮实例化
        menuItemList = new ArrayList<MenuItem>();
        menuItem = new MenuItem();
        menuGroup = new MenuGroup();
        menuItem.setMenuName("呼叫前台");
        menuItem.setActionType(ActionType.PUSH);
        menuItemList.add(menuItem);
        menuGroup = new MenuGroup();
        menuGroup.setMenuName("其他");
        menuGroup.setMenuItemList(menuItemList);
        menuGroup.setMenuType(MenuType.MULTI);
        menuGroupList.add(menuGroup);
        //绘制低栏按钮
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        menuLayoutView = new MenuLayoutView(context, menuGroupList);
        menuLayoutView.setLayoutParams(layoutParams);
        if(menuContainerLayout.getChildCount() > 0){
            menuContainerLayout.removeAllViews();
        }
        menuLayoutView.init();
        menuContainerLayout.addView(menuLayoutView);
        menuContainerLayout.setVisibility(View.VISIBLE);
        menuContainerLayout.invalidate();
    }

    private void initListeners(){

        switchQuickMenuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quickMenuLayout.setVisibility(View.VISIBLE);
                chatKeyboardLayout.setVisibility(View.GONE);
            }
        });

        switchChatKeyboardIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quickMenuLayout.setVisibility(View.GONE);
                chatKeyboardLayout.setVisibility(View.VISIBLE);
            }
        });
    }

}
