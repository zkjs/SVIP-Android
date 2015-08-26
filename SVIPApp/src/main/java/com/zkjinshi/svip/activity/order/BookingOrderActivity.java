package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.MathUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.im.ChatActivity;
import com.zkjinshi.svip.bean.BookOrder;
import com.zkjinshi.svip.view.ItemTitleView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * 预订中的订单详情页面
 *
 * 开发者：vincent
 * 日期：2015/7/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class BookingOrderActivity extends Activity{

    private BookOrder       mBookOrder;

    private ItemTitleView   mTitle;
    private TextView        mDate;
    private TextView        mLatestDateLeave;
    private TextView        mOrderPayment;
    private TextView        mRoomType;
    private TextView        mRoomTag;
    private TextView        mTotalDays;

    private Button          mBtnPay;
    private Button          mBtnChat;
    private Button          mBtnCancelOrder;

    private SimpleDateFormat mSimpleFormat;
    private SimpleDateFormat mChineseFormat;

    private StringBuffer        chooseDateBuffer;
    private ArrayList<Calendar> calendarList = null;;

    private String   arriveTimeStr,   leaveTimeStr;
    private Calendar checkInCalendar, checkOutCalendar;
    private double payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_order);

        mBookOrder = (BookOrder) getIntent().getSerializableExtra("book_order");
        if(mBookOrder != null){
            initView();
            initData();
            initListener();
        }
    }

    private void initView() {
        mTitle = (ItemTitleView) findViewById(R.id.Itv_title);

        mBtnPay         = (Button) findViewById(R.id.btn_pay);
        mBtnChat        = (Button) findViewById(R.id.btn_chat);
        mBtnCancelOrder = (Button) findViewById(R.id.btn_cancel_order);

        mDate            = (TextView) findViewById(R.id.tv_date);
        mLatestDateLeave = (TextView) findViewById(R.id.tv_latest_date_leave);
        mTotalDays       = (TextView) findViewById(R.id.tv_total_days);

        mOrderPayment = (TextView) findViewById(R.id.tv_payment);
        mRoomType     = (TextView) findViewById(R.id.tv_room_type);
        mRoomTag      = (TextView) findViewById(R.id.tv_room_tag);
    }

    private void initData() {
        calendarList = new ArrayList<Calendar>();
        mTitle.setTextTitle(getString(R.string.booking_order));
        mTitle.setTextColor(this, R.color.White);

        mSimpleFormat  = new SimpleDateFormat("yyyy-MM-dd");
        mChineseFormat = new SimpleDateFormat("yyyy年MM月dd日");

        chooseDateBuffer = new StringBuffer();
        Date arrivalDate = null;
        if(null == mBookOrder.getArrivalDate()){
            arrivalDate = Calendar.getInstance().getTime();
            mBookOrder.setArrivalDate(mSimpleFormat.format(arrivalDate));
            chooseDateBuffer.append(mChineseFormat.format(arrivalDate));
        } else {
            try {
            String strArrivalDate = mBookOrder.getArrivalDate();
            arrivalDate = mSimpleFormat.parse(strArrivalDate);
            chooseDateBuffer.append(mBookOrder.getArrivalDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(arrivalDate);
        calendarList.add(cal1);

        chooseDateBuffer.append("/");
        Date departureDate = null;
        if(null == mBookOrder.getDepartureDate()){
            long lonDepartureDate = arrivalDate.getTime()  + 1000 * 60 * 60 * 24;
            departureDate= new Date(lonDepartureDate);
            mBookOrder.setDepartureDate(mSimpleFormat.format(departureDate));
            chooseDateBuffer.append(mChineseFormat.format(departureDate));
        } else {
            try {
                String strDepartureDate = mBookOrder.getDepartureDate();
                departureDate = mSimpleFormat.parse(strDepartureDate);
                chooseDateBuffer.append(mBookOrder.getDepartureDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(departureDate);
        calendarList.add(cal2);

        mDate.setText(chooseDateBuffer.toString());
        mLatestDateLeave.setText((departureDate.getMonth() + 1) + "月" + departureDate.getDate() + "日");
        long diff = departureDate.getTime() - arrivalDate.getTime();
        int stayDays = (int)(diff / (1000 * 60 * 60 * 24));
        mTotalDays.setText(stayDays + "");
        mBookOrder.setDayNum(stayDays);
        payment = multiplyPrice(stayDays,mBookOrder.getRoomRate());
        mOrderPayment.setText(payment+"");
        mBookOrder.setPayment("" + payment);
        mRoomType.setText(mBookOrder.getRoomType() + "房");
    }

    private void initListener() {
        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookingOrderActivity.this.finish();
            }
        });

//        mDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(BookingOrderActivity.this, CalendarActivity.class);
//                if(calendarList != null)
//                {
//                    intent.putExtra("calendarList", calendarList);
//                }
//                startActivityForResult(intent,CalendarActivity.CALENDAR_REQUEST_CODE);
//            }
//        });

        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookingOrderActivity.this.finish();
            }
        });

        mBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goChat = new Intent(BookingOrderActivity.this, ChatActivity.class);
                goChat.putExtra("shop_id", mBookOrder.getShopID());
                goChat.putExtra("shop_name", mBookOrder.getFullName());
                startActivity(goChat);
            }
        });

        mBtnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //进入支付
                DialogUtil.getInstance().showToast(BookingOrderActivity.this, "go pay");
            }
        });

        mBtnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //进入支付
                DialogUtil.getInstance().showToast(BookingOrderActivity.this, "go cancel order");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(RESULT_OK == resultCode){
            if(CalendarActivity.CALENDAR_REQUEST_CODE == requestCode){
                if(null != data){
                    chooseDateBuffer = new StringBuffer();
                    calendarList = (ArrayList<Calendar>)data.getSerializableExtra("calendarList");
                    //判断时间选择的先后顺序
                    if(calendarList.get(0).getTime().getTime() < calendarList.get(1).getTime().getTime()){
                        checkInCalendar  = calendarList.get(0);
                        checkOutCalendar = calendarList.get(1);
                    } else {
                        checkInCalendar  = calendarList.get(1);
                        checkOutCalendar = calendarList.get(0);
                    }

                    long diff = checkOutCalendar.getTime().getTime() - checkInCalendar.getTime().getTime();
                    int stayDays = (int)(diff / (1000 * 60 * 60 * 24));
                    mTotalDays.setText(stayDays + "");
                    arriveTimeStr = getCurrentTimeByCalendar(checkInCalendar);
                    leaveTimeStr  = getCurrentTimeByCalendar(checkOutCalendar);
                    chooseDateBuffer.append(arriveTimeStr);
                    chooseDateBuffer.append("/");
                    chooseDateBuffer.append(leaveTimeStr);
                    if(null != mBookOrder){
                        mBookOrder.setArrivalDate(arriveTimeStr);
                        mBookOrder.setDepartureDate(leaveTimeStr);
                        mBookOrder.setDayNum((int) stayDays);
                    }

                    mDate.setText(chooseDateBuffer.toString());
                    payment = multiplyPrice(stayDays,mBookOrder.getRoomRate());
                    mOrderPayment.setText(payment + "");
                    mBookOrder.setPayment("" + payment);
                    mLatestDateLeave.setText(leaveTimeStr);
                }
            }
        }
    }

    public String getCurrentTimeByCalendar( Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);// 得到年
        int month = calendar.get(Calendar.MONTH) + 1;// 得到月（+1获得习惯的月）
        int day = calendar.get(Calendar.DATE);// 得到日
        return year + "年" + month + "月" + day + "日" ;
    }

    private double multiplyPrice(double days, String priceStr){
        if(!TextUtils.isEmpty(priceStr)){
            Double price = Double.parseDouble(priceStr);
            return  MathUtil.convertDouble((price * days));
        }else{
            return 0.00;
        }
    }

}
