package com.zkjinshi.svip.menu.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.menu.action.ChatMenuAction;
import com.zkjinshi.svip.menu.action.MenuAction;
import com.zkjinshi.svip.menu.action.PushMenuAction;
import com.zkjinshi.svip.menu.vo.ActionType;
import com.zkjinshi.svip.menu.vo.MenuGroup;
import com.zkjinshi.svip.menu.vo.MenuItem;

import java.util.ArrayList;

/**
 * 开发者：JimmyZhang
 * 日期：2015/8/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class MenuGroupView extends LinearLayout {
    private Context context;
    private LayoutInflater inflater;
    private MenuGroup menuGroup;
    private ArrayList<MenuItem> menuItemList;
    private String url;
    private String menuName;
    private Button menuBtn;
    private TextView cutlineTv;
    private MenuItemView menuItemView;
    private LayoutParams layoutParams;
    private int position;
    private PostionType postionType;
    private MenuAction menuAction;

    @SuppressLint("NewApi")
    public MenuGroupView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public MenuGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public MenuGroupView(Context context) {
        super(context);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public MenuGroupView(Context context, MenuGroup menuGroup) {
        this(context);
        this.menuGroup = menuGroup;
    }

    public MenuGroupView(Context context, MenuGroup menuGroup,
                           boolean isShowCutline) {
        this(context, menuGroup);
        initView(isShowCutline);
        initListeners();
    }

    public MenuGroupView(Context context, MenuGroup menuGroup,
                           boolean isShowCutline,int position) {
        this(context, menuGroup, isShowCutline);
        this.position = position;
    }

    public MenuGroupView(Context context, MenuGroup menuGroup,
                         boolean isShowCutline,int position,PostionType type) {
        this(context, menuGroup, isShowCutline);
        this.position = position;
        this.postionType = type;
    }

    public void initView(boolean isShowCutline) {
        menuBtn = (Button) inflater.inflate(R.layout.light_app_menu_btn, null);
        menuBtn.setBackgroundResource(R.drawable.bg_menu_btn);
        layoutParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
        url = menuGroup.getUrl();
        menuName = menuGroup.getMenuName();
        menuBtn.setText(menuName);
        menuBtn.setLayoutParams(layoutParams);
        cutlineTv = new TextView(context);
        cutlineTv.setBackgroundResource(R.color.menu_pop_cut_line);
        layoutParams = new LayoutParams(DisplayUtil.dip2px(context,1), ViewGroup.LayoutParams.MATCH_PARENT);
        cutlineTv.setGravity(Gravity.CENTER_VERTICAL);
        cutlineTv.setLayoutParams(layoutParams);
        addView(menuBtn);
        if (isShowCutline) {
            addView(cutlineTv);
        }
        invalidate();
    }

    /** 初始化监听器 **/
    public void initListeners() {
        menuItemList = menuGroup.getMenuItemList();
        if (null != menuItemList && !menuItemList.isEmpty()) {// 打开popwindow
            menuItemView = new MenuItemView(context, menuItemList);
            menuBtn.setTag(menuItemView);
            menuBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    menuItemView = (MenuItemView) view.getTag();
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    int widthSpec = View.MeasureSpec.makeMeasureSpec(0,
                            View.MeasureSpec.UNSPECIFIED);
                    int heightSpec = View.MeasureSpec.makeMeasureSpec(0,
                            View.MeasureSpec.UNSPECIFIED);
                    menuItemView.getMenuLayout().measure(widthSpec, heightSpec);
                    layoutParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
                    int popupHeight = menuItemView.getMenuLayout().getMeasuredHeight();
                    int popupWidth = menuItemView.getMenuLayout().getMeasuredWidth();
                    int viewWidth = view.getMeasuredWidth();
                    if (postionType == PostionType.LEFT) {
                        menuItemView.showAtLocation(view,
                                Gravity.NO_GRAVITY, location[0] + 20, location[1] -
                                        popupHeight);
                    } else if (postionType == PostionType.CENTER) {
                        menuItemView.showAtLocation(view,
                                Gravity.NO_GRAVITY, location[0], location[1] -
                                        popupHeight);
                    } else if (postionType == PostionType.RIGHT) {
                        if (popupWidth > viewWidth) {
                            menuItemView.showAtLocation(view,
                                    Gravity.NO_GRAVITY, location[0] - 20 - (popupWidth - viewWidth), location[1] -
                                            popupHeight);
                        } else {
                            menuItemView.showAtLocation(view,
                                    Gravity.NO_GRAVITY, location[0], location[1] -
                                            popupHeight);
                        }
                    }
                }
            });
        } else {
            if (!TextUtils.isEmpty(url)) {// 主按钮栏跳转

                menuBtn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        //执行按钮动作
                        ActionType actionType = menuGroup.getActionType();
                        if (actionType == ActionType.CHAT) {//进行预订聊天
                            menuAction = new ChatMenuAction();
                            menuAction.executeAction();
                        } else if (actionType == ActionType.PUSH) {//推送最新预定信息
                            menuAction = new PushMenuAction();
                            menuAction.executeAction();
                        }
                    }
                });
            }
        }
    }
}
