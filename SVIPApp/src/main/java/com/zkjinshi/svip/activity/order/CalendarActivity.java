package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.andexert.calendarlistview.DatePickerController;
import com.andexert.calendarlistview.DayPickerView;
import com.andexert.calendarlistview.SimpleMonthAdapter;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.vo.DateVo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 入住和离开日期选择Activity
 * 开发者：JimmyZhang
 * 日期：2015/8/1
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CalendarActivity extends Activity implements DatePickerController{

    //private ImageButton backIBtn;
    private ItemTitleView mTitle;//返回
    private DayPickerView dayPickerView;
    private LinearLayout tipsLlyt;
    private TextView tipsTv;
    private boolean isChooseOver;
    private ArrayList<Calendar> calendarList;
    private Calendar checkInCalendar,checkOutCalendar;

    public static final int CALENDAR_REQUEST_CODE = 5;

    private void initView(){
        //backIBtn = (ImageButton)findViewById(R.id.calendar_header_bar_btn_back);
        mTitle = (ItemTitleView)findViewById(R.id.itv_title);
        dayPickerView = (DayPickerView)findViewById(R.id.date_picker_choose_view);
        tipsLlyt = (LinearLayout)findViewById(R.id.calendar_tips_llyt);
        tipsTv = (TextView)findViewById(R.id.calendar_tips_tv);
    }

    private void initData(){
        //backIBtn.setVisibility(View.VISIBLE);
        mTitle.getmRight().setVisibility(View.INVISIBLE);
        mTitle.setTextTitle("选择入住和退房日期");
        dayPickerView.setController(this);

        Intent data = getIntent();
        Serializable serializable =  data.getSerializableExtra("calendarList");
        if(serializable != null)
        {
            calendarList =  (ArrayList<Calendar>)serializable;
            SimpleMonthAdapter adapter = (SimpleMonthAdapter)dayPickerView.getAdapter();
            adapter.setFirstDayAndLastDay(calendarList.get(0), calendarList.get(1));
            adapter.notifyDataSetChanged();

            int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
            int selectMonth = calendarList.get(0).get(Calendar.MONTH);
            dayPickerView.setSelection(selectMonth - currentMonth);
        }
    }

    private void initListeners(){

        //返回
        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        initView();
        initData();
        initListeners();
    }

    @Override
    public int getMaxYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day) {
//        if(isChooseOver){
//            checkOutCalendar = Calendar.getInstance();
//            checkOutCalendar.set(Calendar.YEAR, year);
//            checkOutCalendar.set(Calendar.MONTH, month);
//            checkOutCalendar.set(Calendar.DAY_OF_MONTH, day);
//            calendarList.add(checkOutCalendar);
//            //跳转回选择页面
//            Intent inetnt = new Intent();
//            inetnt.putExtra("calendarList", calendarList);
//            setResult(RESULT_OK, inetnt);
//            finish();
//        }else{
//            calendarList = new ArrayList<Calendar>();
//            checkInCalendar = Calendar.getInstance();
//            checkInCalendar.set(Calendar.YEAR,year);
//            checkInCalendar.set(Calendar.MONTH,month);
//            checkInCalendar.set(Calendar.DAY_OF_MONTH,day);
//            calendarList.add(checkInCalendar);
//            isChooseOver = true;
//        }
       // DialogUtil.getInstance().showToast(this, year+"-"+month+"-"+day);
    }

    public void onFirstDaySelected(int year, int month, int day) {
        calendarList = null;
        calendarList = new ArrayList<Calendar>();
        checkInCalendar = Calendar.getInstance();
        checkInCalendar.set(Calendar.YEAR,year);
        checkInCalendar.set(Calendar.MONTH,month);
        checkInCalendar.set(Calendar.DAY_OF_MONTH,day);
        calendarList.add(checkInCalendar);

        showSelectLastDayTips();
    }
    private void showSelectLastDayTips() {
        Animation scaleAnimation1 = new ScaleAnimation(1.0f, 0.0f,1.0f,0.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation1.setDuration(200);
        scaleAnimation1.setFillAfter(true);
        scaleAnimation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tipsTv.setText("请选择离店日期");
                Animation scaleAnimation2 = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation2.setDuration(200);
                scaleAnimation2.setFillAfter(true);
                tipsLlyt.startAnimation(scaleAnimation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        tipsLlyt.startAnimation(scaleAnimation1);
    }

    public void onLastDaySelected(int year, int month, int day) {
        checkOutCalendar = Calendar.getInstance();
        checkOutCalendar.set(Calendar.YEAR, year);
        checkOutCalendar.set(Calendar.MONTH, month);
        checkOutCalendar.set(Calendar.DAY_OF_MONTH, day);
        calendarList.add(checkOutCalendar);
        //跳转回选择页面
        Intent inetnt = new Intent();
        inetnt.putExtra("calendarList", calendarList);
        setResult(RESULT_OK, inetnt);
        finish();
    }
}
