package com.zkjinshi.svip.activity.im.group.actions;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.im.group.ChatGroupActivity;
import com.zkjinshi.svip.menu.view.ChatMenuType;
import com.zkjinshi.svip.menu.view.MenuLayoutView;
import com.zkjinshi.svip.menu.vo.MenuGroup;
import com.zkjinshi.svip.menu.vo.MenuItem;

import java.util.ArrayList;

/**
 * 快捷指令管理器
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
    private Animation menuInBottom, menuOutTop;

    public MessageListViewManager getMessageListViewManager() {
        return messageListViewManager;
    }

    public void setMessageListViewManager(MessageListViewManager messageListViewManager) {
        this.messageListViewManager = messageListViewManager;
    }

    private MessageListViewManager messageListViewManager;

    private QuickMenuManager(){}

    public static QuickMenuManager instance;

    public static synchronized QuickMenuManager getInstance(){
        if(null ==  instance){
            instance = new QuickMenuManager();
        }
        return instance;
    }

    public QuickMenuManager init(Context context){
        this.context = context;
        initView(context);
        initData();
        initListeners();
        return this;
    }

    private void initView(Context context){
        quickMenuLayout = (LinearLayout)((Activity)context).findViewById(R.id.acion_keyboard_layout);
        chatKeyboardLayout = (LinearLayout)((Activity)context).findViewById(R.id.chat_keyboard_layout);
        switchQuickMenuIv = (ImageButton)((Activity)context).findViewById(R.id.switch_quick_menu);
        switchChatKeyboardIv = (ImageButton)((Activity)context).findViewById(R.id.switch_chat_keyboard);
        menuContainerLayout = (LinearLayout)((Activity)context).findViewById(R.id.quick_menu_container_layout);
    }

    private void initData(){
        quickMenuLayout.setVisibility(View.GONE);
        chatKeyboardLayout.setVisibility(View.VISIBLE);
        menuInBottom = AnimationUtils.loadAnimation(context,R.anim.menu_in_bottom);
        menuOutTop = AnimationUtils.loadAnimation(context,R.anim.menu_out_top);
        menuGroupList = QuickMenuController.getInstance().getNoOrderMenuGroupList();
        //绘制低栏按钮
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        menuLayoutView = new MenuLayoutView(context, menuGroupList, ChatMenuType.GROUP);
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

        //显示快捷菜单
        switchQuickMenuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ChatGroupActivity)context).resetKeyboard();
                quickMenuLayout.startAnimation(menuInBottom);
                chatKeyboardLayout.startAnimation(menuOutTop);
                menuInBottom.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        quickMenuLayout.setVisibility(View.VISIBLE);
                        chatKeyboardLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }
        });

        //显示聊天菜单
        switchChatKeyboardIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ChatGroupActivity)context).resetKeyboard();
                quickMenuLayout.startAnimation(menuOutTop);
                chatKeyboardLayout.startAnimation(menuInBottom);
                menuInBottom.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        quickMenuLayout.setVisibility(View.GONE);
                        chatKeyboardLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });
    }

}
