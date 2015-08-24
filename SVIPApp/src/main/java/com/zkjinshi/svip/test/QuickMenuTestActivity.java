package com.zkjinshi.svip.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
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
 * 日期：2015/8/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class QuickMenuTestActivity extends Activity{

    private LinearLayout menuContainerLayout;
    private ArrayList<MenuGroup> menuGroupList;
    private ArrayList<MenuItem> menuItemList;
    private MenuGroup menuGroup;
    private MenuItem menuItem;
    private MenuLayoutView menuLayoutView;
    private LinearLayout.LayoutParams layoutParams;

    private void initView(){
        menuContainerLayout = (LinearLayout)findViewById(R.id.tab_host_layout);
    }

    private void initData(){
        //第一组按钮实例化
        menuGroupList = new ArrayList<MenuGroup>();
        menuItemList = new ArrayList<MenuItem>();
        menuItem = new MenuItem();
        menuGroup = new MenuGroup();
        menuItem.setMenuName("京东特卖");
        menuItem.setActionType(ActionType.PUSH);
        menuItem.setUrl("http://www.jd.com");
        menuItemList.add(menuItem);
        menuGroup.setMenuItemList(menuItemList);
        menuGroup.setMenuName("促销");
        menuGroup.setMenuType(MenuType.MULTI);
        menuGroupList.add(menuGroup);
        //第二组按钮实例化
        menuItemList = new ArrayList<MenuItem>();
        menuItem = new MenuItem();
        menuGroup = new MenuGroup();
        menuItem.setMenuName("订房");
        menuItem.setActionType(ActionType.PUSH);
        menuItem.setUrl("http://www.7daysinn.cn");
        menuItemList.add(menuItem);
        menuItem = new MenuItem();
        menuItem.setMenuName("订餐");
        menuItem.setActionType(ActionType.PUSH);
        menuItem.setUrl("http://v5.ele.me");
        menuItemList.add(menuItem);
        menuGroup.setMenuItemList(menuItemList);
        menuGroup.setMenuName("服务");
        menuGroup.setMenuType(MenuType.MULTI);
        menuGroupList.add(menuGroup);
        //第三组按钮实例化
        menuItemList = new ArrayList<MenuItem>();
        menuGroup = new MenuGroup();
        menuGroup.setMenuName("关于");
        menuGroup.setUrl("http://www.zkjinshi.com");
        menuGroup.setActionType(ActionType.CHAT);
        menuGroup.setMenuType(MenuType.SINGLE);
        menuGroupList.add(menuGroup);
        //绘制低栏按钮
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        menuLayoutView = new MenuLayoutView(this, menuGroupList);
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

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_menu);
        initView();
        initData();
        initListeners();
    }
}
