package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.MathUtil;
import com.zkjinshi.base.util.NetWorkUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.im.ChatActivity;
import com.zkjinshi.svip.bean.BookOrder;
import com.zkjinshi.svip.factory.GoodInfoFactory;
import com.zkjinshi.svip.response.GoodInfoResponse;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.StringUtil;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.view.ItemUserSettingView;
import com.zkjinshi.svip.vo.GoodInfoVo;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 预订中的订单详情页面
 *
 * 开发者：dujiande
 * 日期：2015/8/27
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderBookingActivity extends Activity{


    private ItemTitleView   mTitle;
    private TextView        mRoomType;
    private TextView        mTvArriveDate;
    private TextView        mTvLeaveDate;
    private TextView        mTvDateTips;
    private LinearLayout    mLltDateContainer;
    private ImageView       mIvRoomImg;
    private StringRequest stringRequest;

    private Button          mBtnSendOrder;
    private LinearLayout    mLltYuan;

    private ItemUserSettingView mIusvRoomNumber;
    private ArrayList<ItemUserSettingView> customerList;


    private SimpleDateFormat mSimpleFormat;
    private SimpleDateFormat mChineseFormat;

    private StringBuffer        chooseDateBuffer;
    private ArrayList<Calendar> calendarList = null;

    private String   arriveTimeStr,   leaveTimeStr;
    private Calendar checkInCalendar, checkOutCalendar;
    private double payment;

    private String shopId;
    private List<GoodInfoResponse> goodResponsseList;
    private List<GoodInfoVo> goodInfoList;
    private DisplayImageOptions options;
    private GoodInfoVo lastGoodInfoVo;

    public static final int GOOD_REQUEST_CODE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        shopId = getIntent().getStringExtra("shopid");
        if(!StringUtil.isEmpty(shopId)){
            initView();
            initData();
            initListener();
        }
    }

    private void initView() {
        mTitle = (ItemTitleView) findViewById(R.id.itv_title);
        mBtnSendOrder = (Button) findViewById(R.id.btn_send_booking_order);
        mRoomType     = (TextView) findViewById(R.id.tv_room_type);
        mLltYuan      = (LinearLayout)findViewById(R.id.rl_yuan);

        mTvArriveDate = (TextView)findViewById(R.id.tv_arrive_date);
        mTvLeaveDate  = (TextView)findViewById(R.id.tv_leave_date);
        mTvDateTips   = (TextView)findViewById(R.id.tv_date_tips);
        mLltDateContainer = (LinearLayout)findViewById(R.id.llt_date_container);

        mIusvRoomNumber = (ItemUserSettingView)findViewById(R.id.aod_room_number);

        mIvRoomImg = (ImageView)findViewById(R.id.iv_room_img);

        customerList = new ArrayList<ItemUserSettingView>();
        int[] customerIds = {R.id.aod_customer1,R.id.aod_customer2,R.id.aod_customer3};
        for(int i=0;i<customerIds.length;i++){
            customerList.add((ItemUserSettingView)findViewById(customerIds[i]));
        }

    }

    private void initData() {
        GoodListNetController.getInstance().init(this);
        GoodListUiController.getInstance().init(this);

        calendarList = new ArrayList<Calendar>();
        mTitle.setTextTitle(getString(R.string.booking_order));
        mTitle.setTextColor(this, R.color.White);

        mSimpleFormat  = new SimpleDateFormat("yyyy-MM-dd");
        mChineseFormat = new SimpleDateFormat("MM月dd日");

        Calendar today = Calendar.getInstance();
        today.setTime(new Date()); //当天
        calendarList.add(today);

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.setTime(new Date());
        tomorrow.add(Calendar.DAY_OF_YEAR, 1); //下一天
        calendarList.add(tomorrow);

        setOrderDate(calendarList);

        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_room_pic_default)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.ic_room_pic_default)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_room_pic_default)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();

        stringRequest = new StringRequest(Request.Method.GET, ProtocolUtil.getGoodListUrl(shopId),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        DialogUtil.getInstance().cancelProgressDialog();
                        LogUtil.getInstance().info(LogLevel.INFO, "获取商品列表响应结果:" + response);
                        if(!TextUtils.isEmpty(response)){
                            try {
                                Type listType = new TypeToken<List<GoodInfoResponse>>(){}.getType();
                                Gson gson = new Gson();
                                goodResponsseList = gson.fromJson(response, listType);
                                if(null != goodResponsseList && !goodResponsseList.isEmpty()){
                                    goodInfoList = GoodInfoFactory.getInstance().bulidGoodList(goodResponsseList);
                                    if(null != goodInfoList && !goodInfoList.isEmpty()){
                                        GoodInfoVo goodInfoVo = goodInfoList.get(0);
                                        lastGoodInfoVo = goodInfoVo;
                                        mTitle.setTextTitle(goodInfoVo.getFullname());
                                        setOrderRoomType(goodInfoVo);
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtil.getInstance().cancelProgressDialog();
                LogUtil.getInstance().info(LogLevel.INFO, "获取商品列表错误信息:" +  error.getMessage());
            }
        });
        if(NetWorkUtil.isNetworkConnected(this)){
            GoodListNetController.getInstance().requestGetGoodListTask(stringRequest);
        }

    }

    //设置离开和到达的日期
    private void setOrderDate(ArrayList<Calendar> calendarList){
        mChineseFormat = new SimpleDateFormat("MM月dd日");
        Date arrivalDate = calendarList.get(0).getTime();
        Date leaveDate   = calendarList.get(1).getTime();

        mTvArriveDate.setText(mChineseFormat.format(arrivalDate));
        mTvLeaveDate.setText(mChineseFormat.format(leaveDate));
        try{
            int offsetDay = TimeUtil.daysBetween(arrivalDate,leaveDate);
            mTvDateTips.setText("共"+offsetDay+"晚，在"+mTvLeaveDate.getText()+"13点前退房");
        }catch (Exception e){

        }
    }

    private void initListener() {
        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderBookingActivity.this.finish();
            }
        });

        mLltDateContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderBookingActivity.this, CalendarActivity.class);
                if (calendarList != null) {
                    intent.putExtra("calendarList", calendarList);
                }
                startActivityForResult(intent, CalendarActivity.CALENDAR_REQUEST_CODE);
            }
        });

        mLltYuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderBookingActivity.this, GoodListActivity.class);
                if (lastGoodInfoVo != null && !StringUtil.isEmpty(lastGoodInfoVo.getId())) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("GoodInfoVo", lastGoodInfoVo);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, GOOD_REQUEST_CODE);
                }
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
                    setOrderDate(calendarList);
                }
            }
            else if(GOOD_REQUEST_CODE == requestCode){
                if(null != data){
                    lastGoodInfoVo = (GoodInfoVo)data.getSerializableExtra("GoodInfoVo");
                    setOrderRoomType(lastGoodInfoVo);
                }
            }
        }
    }

    private void setOrderRoomType(GoodInfoVo goodInfoVo) {
        lastGoodInfoVo = goodInfoVo;
        String imageUrl = goodInfoVo.getImage();
        if(!TextUtils.isEmpty(imageUrl)){
            String logoUrl = ProtocolUtil.getGoodImgUrl(imageUrl);
            ImageLoader.getInstance().displayImage(logoUrl,mIvRoomImg,options);
        }
        mRoomType.setText(goodInfoVo.getRoom());
    }

}
