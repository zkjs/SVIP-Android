package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.base.util.MathUtil;
import com.zkjinshi.base.util.NetWorkUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.bean.BookOrder;
import com.zkjinshi.svip.factory.GoodInfoFactory;
import com.zkjinshi.svip.response.GoodInfoResponse;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.vo.DateVo;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单确认界面
 *
 * 开发者：vincent
 * 日期：2015/7/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderConfirmActivity extends Activity{

    private ItemTitleView   mTitle;
    private Button          mBtnSubmit;
    private BookOrder       mBookOrder;
    private GridView        mGridList;
    private List<String>         mRemarkList;
    private Map<String, Boolean> mRemarkTagMap;//存储tagCheckBox

    private GridAdapter     mGridAdapter;
    private TextView        mLatestDateLeave;
    private TextView        mOrderPayment;
    private TextView        mRoomType;
    private TextView        mRoomTag;
    private TextView        mTotalDays;
    private TextView checkDateTv;
    private StringBuffer chooseDateBuffer;
    private String arriveTimeStr,leaveTimeStr;
    private ArrayList<Calendar> calendarList = null;
    private Calendar checkInCalendar,checkOutCalendar;

    private SimpleDateFormat mSimpleFormat;
    private SimpleDateFormat mChineseFormat;
    private double           payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        mBookOrder = (BookOrder) getIntent().getSerializableExtra("book_order");
        if(mBookOrder != null){
            initView();
            initData();
            initListener();
        }
    }

    private void initView() {
        mTitle      = (ItemTitleView) findViewById(R.id.Itv_title);
        mBtnSubmit  = (Button) findViewById(R.id.btn_submit_order);
        mGridList   = (GridView) findViewById(R.id.gv_request_list);
        mLatestDateLeave = (TextView) findViewById(R.id.tv_latest_date_leave);
        mTotalDays       = (TextView) findViewById(R.id.tv_total_days);

        mOrderPayment = (TextView) findViewById(R.id.tv_payment);
        mRoomType     = (TextView) findViewById(R.id.tv_room_type);
        mRoomTag      = (TextView) findViewById(R.id.tv_room_tag);

        checkDateTv   = (TextView)findViewById(R.id.tv_date);
    }

    private void initData() {
        OrderConfirmNetController.getInstance().init(this);
        mTitle.setTextTitle(getString(R.string.confirm_order));
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

        chooseDateBuffer.append("/");
        Date departureDate = null;
        if(null == mBookOrder.getDepartureDate()){
            long lonDepartureDate = arrivalDate.getTime() + + 1000 * 60 * 60 * 24;
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

        checkDateTv.setText(chooseDateBuffer.toString());
        mLatestDateLeave.setText((departureDate.getMonth() + 1) + "月" + departureDate.getDate() + "日");
        long diff = departureDate.getTime() - arrivalDate.getTime();
        int stayDays = (int)(diff / (1000 * 60 * 60 * 24));
        mTotalDays.setText(stayDays + "");
        mBookOrder.setDayNum(stayDays);
        //计算房间总价
        payment = multiplyPrice(stayDays,mBookOrder.getRoomRate());
        mOrderPayment.setText(payment+"");
        mBookOrder.setPayment("" + payment);
        mRoomType.setText(mBookOrder.getRoomType() + "房");

        //获得remarks
        if(!TextUtils.isEmpty(mBookOrder.getRemark())){
            String[] remark = mBookOrder.getRemark().split(",");
            mRemarkList = new ArrayList<>();
            mRemarkTagMap = new HashMap<>();
            for(int i=0; i<remark.length; i++){
                mRemarkList.add(remark[i]);
                mRemarkTagMap.put(remark[i], false);
            }

            mRemarkList.add("无烟房");
            mRemarkList.add("海景房");
            mRemarkList.add("离电梯近");
            mRemarkList.add("枕头多");
            mRemarkList.add("远离马路");

            for(int i=0; i< mRemarkList.size(); i++){
                mRemarkTagMap.put(mRemarkList.get(i), false);
            }

            mGridAdapter = new GridAdapter();
            mGridList.setAdapter(mGridAdapter);
        }

        mBtnSubmit.setTag(mBookOrder);
    }

    private double multiplyPrice(double days,String priceStr){
        if(!TextUtils.isEmpty(priceStr)){
            Double price = Double.parseDouble(priceStr);
            return  MathUtil.convertDouble((price*days));
        }else{
            return 0.00;
        }
    }

    private void initListener() {
        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderConfirmActivity.this.finish();
            }
        });

        checkDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderConfirmActivity.this,CalendarActivity.class);
                if(calendarList != null)
                {
                    intent.putExtra("calendarList", calendarList);
                }
                startActivityForResult(intent,CalendarActivity.CALENDAR_REQUEST_CODE);
            }
        });

        //确认预订
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBookOrder = (BookOrder)view.getTag();
                OrderConfirmNetController.getInstance().requestGetGoodListTask(mBookOrder);
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
                    leaveTimeStr = getCurrentTimeByCalendar(checkOutCalendar);
                    chooseDateBuffer.append(arriveTimeStr);
                    chooseDateBuffer.append("/");
                    chooseDateBuffer.append(leaveTimeStr);
                    if(null != mBookOrder){
                        mBookOrder.setArrivalDate(arriveTimeStr);
                        mBookOrder.setDepartureDate(leaveTimeStr);
                        mBookOrder.setDayNum(stayDays);
                    }
                    mLatestDateLeave.setText(leaveTimeStr);
                    checkDateTv.setText(chooseDateBuffer.toString());
                    payment = multiplyPrice(stayDays,mBookOrder.getRoomRate());
                    mOrderPayment.setText(payment + "");
                    mBookOrder.setPayment("" + payment);
                    mBtnSubmit.setTag(mBookOrder);
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

    class GridAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            if(mRemarkList != null){
                return mRemarkList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int i) {
            if(mRemarkList != null){
                return mRemarkList.get(i);
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {

            ViewHolder holder = null;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = View.inflate(OrderConfirmActivity.this, R.layout.item_checkbox, null);
                holder.cbTag = (CheckBox) convertView.findViewById(R.id.cb_tag);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.cbTag.setText(mRemarkList.get(position));
            Boolean isChecked = mRemarkTagMap.get(mRemarkList.get(position));
            holder.cbTag.setChecked(isChecked);//初始状态值

            holder.cbTag.setTextColor(Color.BLACK);
            holder.cbTag.setGravity(Gravity.CENTER);
            holder.cbTag.setTextSize(DisplayUtil.sp2px(OrderConfirmActivity.this, 5));
            final int checkPosition = position;
            holder.cbTag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    mRemarkTagMap.put(mRemarkList.get(checkPosition), isChecked);
                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
        CheckBox cbTag;
    }

}
