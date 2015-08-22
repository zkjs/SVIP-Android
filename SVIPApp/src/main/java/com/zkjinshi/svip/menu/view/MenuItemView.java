package com.zkjinshi.svip.menu.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.menu.action.ChatMenuAction;
import com.zkjinshi.svip.menu.action.MenuAction;
import com.zkjinshi.svip.menu.action.PushMenuAction;
import com.zkjinshi.svip.menu.vo.ActionType;
import com.zkjinshi.svip.menu.vo.MenuItem;

import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2015/8/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MenuItemView extends PopupWindow {

    private Context context;
    private LinearLayout menuLayout;
    private ArrayList<MenuItem> menuItemList;
    private LayoutInflater inflater;
    private Button menuBtn;
    private LinearLayout.LayoutParams layoutParams;
    private TextView cutlineTv;
    private MenuAction menuAction;
    public MenuItemView(Context context) {
        super(context);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public MenuItemView(Context context, ArrayList<MenuItem> menuItemList) {
        this(context);
        this.menuItemList = menuItemList;
        init();
    }

    /**初始化界面元素**/
    public void init(){
        menuLayout = (LinearLayout) inflater.inflate(R.layout.menu_popup_window, null);
        menuLayout.setPadding(DisplayUtil.dip2px(context,2), DisplayUtil.dip2px(context,2), DisplayUtil.dip2px(context,2), DisplayUtil.dip2px(context,2));
        for(int i = 0; i< menuItemList.size()-1; i++){
            addView(menuItemList.get(i), true);
        }
        addView(menuItemList.get(menuItemList.size() - 1), false);
        setContentView(menuLayout);
        setAnimationStyle(1);
        setOutsideTouchable(true);
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        setBackgroundDrawable(dw);
    }

    public void addView(MenuItem menuItem,boolean isShowCutLine){
        String menuName = menuItem.getMenuName();
        menuBtn =  (Button) inflater.inflate(R.layout.menu_item_btn, null);
        menuBtn.setBackgroundResource(R.drawable.bg_menu_btn);
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
        menuBtn.setLayoutParams(layoutParams);
        menuBtn.setText(menuName);
        menuBtn.setTag(menuItem);
        menuBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //执行按钮动作
                MenuItem menuItem = (MenuItem) view.getTag();
                ActionType actionType = menuItem.getActionType();
                if (actionType == ActionType.CHAT) {//进行预订聊天
                    menuAction = new ChatMenuAction();
                    menuAction.executeAction();
                } else if (actionType == ActionType.PUSH) {//推送最新预定信息
                    menuAction = new PushMenuAction();
                    menuAction.executeAction();
                }
                dismiss();
            }
        });
        menuLayout.addView(menuBtn);
        if(isShowCutLine){
            cutlineTv = new TextView(context);
            cutlineTv.setBackgroundResource(R.color.menu_pop_cut_line);
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
            cutlineTv.setLayoutParams(layoutParams);
            menuLayout.addView(cutlineTv);
        }

    }

    public LinearLayout getMenuLayout() {
        return menuLayout;
    }

    public void setMenuLayout(LinearLayout menuLayout) {
        this.menuLayout = menuLayout;
    }

}
