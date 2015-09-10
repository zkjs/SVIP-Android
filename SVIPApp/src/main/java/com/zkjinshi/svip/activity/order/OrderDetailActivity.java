package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.im.ChatActivity;
import com.zkjinshi.svip.activity.mine.MineNetController;
import com.zkjinshi.svip.response.BaseResponse;
import com.zkjinshi.svip.response.OrderDetailResponse;
import com.zkjinshi.svip.response.OrderInvoiceResponse;
import com.zkjinshi.svip.response.OrderPrivilegeResponse;
import com.zkjinshi.svip.response.OrderRoomTagResponse;
import com.zkjinshi.svip.response.OrderUsersResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.StringUtil;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.view.ItemUserSettingView;
import com.zkjinshi.svip.vo.GoodInfoVo;
import com.zkjinshi.svip.vo.TicketVo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.kaede.tagview.OnTagClickListener;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

/**
 * 订单详情页面
 *
 * 开发者：dujiande
 * 日期：2015/8/29
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderDetailActivity extends Activity{


    private ItemTitleView   mTitle;
    private TextView        mRoomType;
    private TextView        mRoomRate;
    private TextView        mTvOrderStatus;
    private TextView        mTvArriveDate;
    private TextView        mTvLeaveDate;
    private TextView        mTvDateTips;
    private LinearLayout    mLltDateContainer;
    private ImageView       mIvRoomImg;
    private StringRequest stringRequest;

    private Button          mBtnSendOrder;
    private Button          mBtnCancelOrder;
    private LinearLayout    mLltYuan;
    private LinearLayout    mLltTicketContainer;

    private TagView mRoomTagView;
    private TagView mServiceTagView;

    private ItemUserSettingView mIusvRoomNumber;
    private TextView mTvTicket;
    private ArrayList<ItemUserSettingView> customerList;

    private TextView mTvRemark;
    private LinearLayout mlltRemark;

    private TextView mTvPayTips; //支付提示语句
    private TextView mTvPay;  //支付跳转


    private SimpleDateFormat mSimpleFormat;
    private SimpleDateFormat mChineseFormat;
    private ArrayList<Calendar> calendarList = null;

    private int dayNum;
    private int roomNum;

    private String reservationNo;
    private String shopId;
    private GoodInfoVo lastGoodInfoVo;
    private OrderInvoiceResponse orderInvoiceResponse;
    private ArrayList<OrderPrivilegeResponse> totalPrivileges;
    private ArrayList<OrderRoomTagResponse> totalRoomTags;
    //private OrderInfoVo orderInfoVo;

    //private List<GoodInfoResponse> goodResponsseList;
    //private List<GoodInfoVo> goodInfoList;
    private DisplayImageOptions options;

    private OrderDetailResponse orderDetailResponse = null;



    public static final int GOOD_REQUEST_CODE = 6;
    public static final int PEOPLE_REQUEST_CODE = 7;
    public static final int TICKET_REQUEST_CODE = 8;
    public static final int REMARK_REQUEST_CODE = 9;

    public OrderDetailActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        initView();
        initListener();

        reservationNo = getIntent().getStringExtra("reservation_no");
        shopId = getIntent().getStringExtra("shopid");
        loadOrderInfoByReservationNo();
    }

    //根据订单号加载订单详细信息。
    private void loadOrderInfoByReservationNo() {
        MineNetController.getInstance().init(this);
        if(CacheUtil.getInstance().getToken() == null){
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ProtocolUtil.getOneOrderUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        DialogUtil.getInstance().cancelProgressDialog();
                        LogUtil.getInstance().info(LogLevel.INFO, "获取单个订单响应结果:" + response);
                        if(!TextUtils.isEmpty(response)){
                            try{
                                orderDetailResponse = new Gson().fromJson(response,OrderDetailResponse.class);
                            }catch (Exception e){
                                orderDetailResponse = null;
                                LogUtil.getInstance().info(LogLevel.ERROR, "获取单个订单错误信息:" +  e.getMessage());
                                DialogUtil.getInstance().showToast(OrderDetailActivity.this,"获取订单详情失败");
                                finish();
                            }

                            if(orderDetailResponse != null){
                                initData();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtil.getInstance().cancelProgressDialog();
                LogUtil.getInstance().info(LogLevel.ERROR, "获取单个订单错误信息:" +  error.getMessage());
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("userid", CacheUtil.getInstance().getUserId());
                map.put("token", CacheUtil.getInstance().getToken());
                map.put("reservation_no", reservationNo);
//                map.put("shopid",shopId);
//                map.put("userid","5551fc5b8c35e");
//                map.put("token", "I1Ae4us4ssrwsWIg");
//                map.put("reservation_no", "H00120180203");
//                map.put("shopid","120");
                LogUtil.getInstance().info(LogLevel.ERROR,"map="+map.toString());
                return map;
            }
        };
        LogUtil.getInstance().info(LogLevel.ERROR,stringRequest.toString());
        MineNetController.getInstance().requestGetUserInfoTask(stringRequest);
    }

    private void initView() {
        mTitle = (ItemTitleView) findViewById(R.id.itv_title);
        mBtnSendOrder = (Button) findViewById(R.id.btn_send_booking_order);
        mBtnCancelOrder = (Button)findViewById(R.id.btn_cancel_order);
        mRoomType     = (TextView) findViewById(R.id.tv_room_type);
        mRoomRate     = (TextView) findViewById(R.id.tv_payment);
        mLltYuan      = (LinearLayout)findViewById(R.id.rl_yuan);
        mTvOrderStatus = (TextView)findViewById(R.id.tv_order_status);

        mRoomTagView = (TagView)findViewById(R.id.tagview_room_tags);
        mServiceTagView = (TagView)findViewById(R.id.tagview_service_tags);

        mTvArriveDate = (TextView)findViewById(R.id.tv_arrive_date);
        mTvLeaveDate  = (TextView)findViewById(R.id.tv_leave_date);
        mTvDateTips   = (TextView)findViewById(R.id.tv_date_tips);
        mLltDateContainer = (LinearLayout)findViewById(R.id.llt_date_container);

        mIusvRoomNumber = (ItemUserSettingView)findViewById(R.id.aod_room_number);


        mIvRoomImg = (ImageView)findViewById(R.id.iv_room_img);

        mTvPayTips = (TextView)findViewById(R.id.tv_pay_tips);
        mTvPay = (TextView)findViewById(R.id.tv_pay);

        customerList = new ArrayList<ItemUserSettingView>();
        int[] customerIds = {R.id.aod_customer1,R.id.aod_customer2,R.id.aod_customer3};
        for(int i=0;i<customerIds.length;i++){
            customerList.add((ItemUserSettingView) findViewById(customerIds[i]));
        }

        mTvTicket  = (TextView)findViewById(R.id.tv_ticket);
        mLltTicketContainer = (LinearLayout)findViewById(R.id.llt_ticket_container);

        mTvRemark = (TextView)findViewById(R.id.tv_remark);
        mlltRemark = (LinearLayout)findViewById(R.id.llt_order_remark);
    }

    private void initData(){
        mBtnSendOrder.setText("确认订单");
        mTitle.setTextTitle(orderDetailResponse.getRoom().getFullname());
        mTitle.setTextColor(this, R.color.White);
        mTitle.getmRight().setVisibility(View.GONE);
        //初始化入住时间
        calendarList = new ArrayList<Calendar>();
        mSimpleFormat  = new SimpleDateFormat("yyyy-MM-dd");
        mChineseFormat = new SimpleDateFormat("MM月dd日");

        try{
            Calendar arrivalDate = Calendar.getInstance();
            arrivalDate.setTime(mSimpleFormat.parse(orderDetailResponse.getRoom().getArrival_date()));
            calendarList.add(arrivalDate);

            Calendar departureDate = Calendar.getInstance();
            departureDate.setTime(mSimpleFormat.parse(orderDetailResponse.getRoom().getDeparture_date()));
            calendarList.add(departureDate);
            setOrderDate(calendarList);
        }catch ( Exception e){

        }
        //初始化入住人信息
        roomNum = orderDetailResponse.getRoom().getRooms();
        notifyRoomNumberChange();
        Drawable drawable= getResources().getDrawable(R.mipmap.ic_get_into_w);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mIusvRoomNumber.getmTextContent2().setCompoundDrawables(null, null, drawable, null);

        for(int i=0;i<customerList.size();i++){
            Drawable d= getResources().getDrawable(R.mipmap.ic_get_into_w);
            d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
            customerList.get(i).getmTextContent2().setCompoundDrawables(null, null, d, null);
        }

        ArrayList<OrderUsersResponse> users = orderDetailResponse.getUsers();
        for(int i=0;i< roomNum;i++){
            if(i<3){
                OrderUsersResponse user = users.get(i);
                customerList.get(i).getmTextContent2().setText(user.getRealname());
            }

        }

        //初始化商品信息
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_room_pic_default)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.ic_room_pic_default)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_room_pic_default)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
        if(!TextUtils.isEmpty(orderDetailResponse.getRoom().getImageurl())){
            String logoUrl = ProtocolUtil.getGoodImgUrl(orderDetailResponse.getRoom().getImageurl());
            ImageLoader.getInstance().displayImage(logoUrl,mIvRoomImg,options);
        }
        mRoomType.setText(orderDetailResponse.getRoom().getRoom_type());
        mRoomRate.setText(orderDetailResponse.getRoom().getRoom_rate());

        initOrderStatus();
        initRoomTags();
        initServiceTags();
        initTicket();
        initRemark();


    }



    //初始化订单状态的显示
    private void initOrderStatus() {
        //订单状态 默认0可取消订单 1已取消订单 2已确认订单 3已经完成的订单 4正在入住中 5已删除订单
        //支付状态 0未支付,1已支付,3支付一部分,4已退款, 5已挂账

        final String orderStatus = orderDetailResponse.getRoom().getStatus();
        String payStatus = orderDetailResponse.getRoom().getPay_status();

        mBtnSendOrder.setVisibility(View.GONE);
        mBtnCancelOrder.setVisibility(View.GONE);

        if (orderStatus.equals("0")){
            mTvOrderStatus.setText("已提交");
            mBtnSendOrder.setVisibility(View.VISIBLE);
            mBtnCancelOrder.setVisibility(View.VISIBLE);
            initTagClickEvent();
        }
        else if(orderStatus.equals("1")){
            mTvOrderStatus.setText("已取消");
        }
        else if(orderStatus.equals("2")){
            mTvOrderStatus.setText("已确认");
            mBtnCancelOrder.setVisibility(View.VISIBLE);
        }
        else if(orderStatus.equals("3")){
            mTvOrderStatus.setText("已完成");
        }
        else if(orderStatus.equals("4")){
            mTvOrderStatus.setText("已入住");
        }
        else if(orderStatus.equals("5")){
            mTvOrderStatus.setText("已删除");
        }

        if (payStatus.equals("0")){
            mTvOrderStatus.setText(mTvOrderStatus.getText().toString()+"/未支付");
            mTvPayTips.setText("你应该支付"+orderDetailResponse.getRoom().getRoom_rate()+"元，还需要支付"+orderDetailResponse.getRoom().getRoom_rate()+"元");
            mTvPay.setText("立即支付");
            mTvPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(OrderDetailActivity.this,PayOrderActivity.class);
                    intent.putExtra("orderDetailResponse",orderDetailResponse);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);

                }
            });

        }
        else if(payStatus.equals("1")){
            mTvOrderStatus.setText(mTvOrderStatus.getText().toString()+"/已支付");
            mTvPayTips.setText("你应该支付"+orderDetailResponse.getRoom().getRoom_rate()+"元，还需要支付0元");
            mTvPay.setText("已支付");
        }
        else if(payStatus.equals("2")){
            mTvOrderStatus.setText(mTvOrderStatus.getText().toString()+"");
        }
        else if(payStatus.equals("3")){
            mTvOrderStatus.setText(mTvOrderStatus.getText().toString()+"/支付一部分");
            mTvPayTips.setText("");
            mTvPay.setText("支付一部分");
        }
        else if(payStatus.equals("4")){
            mTvOrderStatus.setText(mTvOrderStatus.getText().toString()+"/已退款");
            mTvPayTips.setText("");
            mTvPay.setText("已退款");
        }
        else if(payStatus.equals("5")){
            mTvOrderStatus.setText(mTvOrderStatus.getText().toString()+"/已挂账");
            mTvPayTips.setText("你应该支付"+orderDetailResponse.getRoom().getRoom_rate()+"元，还需要支付0元");
            mTvPay.setText("已挂账");
        }
    }

    //初始化订单备注
    private void initRemark() {
        if(!StringUtil.isEmpty(orderDetailResponse.getRoom().getRemark())){
            mTvRemark.setText(orderDetailResponse.getRoom().getRemark());
        }
    }

    //初始化发票
    private void initTicket() {
        orderInvoiceResponse = orderDetailResponse.getInvoice();
        if(orderInvoiceResponse != null){
            mTvTicket.setText(orderInvoiceResponse.getInvoice_title());
        }
        else{
            mTvTicket.setText("");
        }

    }

    //初始化房间选项标签
    private void initRoomTags() {

        ArrayList<OrderRoomTagResponse> roomTags = orderDetailResponse.getRoom_tag();
        totalRoomTags = new ArrayList<OrderRoomTagResponse>();
        totalRoomTags.addAll(roomTags);
        if(roomTags != null){
            for(OrderRoomTagResponse item : roomTags){
                if(orderDetailResponse.getRoom().getStatus().equals("0")){
                    mRoomTagView.addTag(createTag(item.getId(),item.getContent(),false));
                }else{
                    mRoomTagView.addTag(createTag(item.getId(),item.getContent(),true));
                }

            }
            if(orderDetailResponse.getRoom().getStatus().equals("0")){
                orderDetailResponse.getRoom_tag().clear();
            }

        }
    }

    //初始化其他服务标签
    private void initServiceTags() {

        ArrayList<OrderPrivilegeResponse> privileges = orderDetailResponse.getPrivilege();
        totalPrivileges = new ArrayList<OrderPrivilegeResponse>();
        totalPrivileges.addAll(privileges);
        if(privileges != null){
            for(OrderPrivilegeResponse item : privileges){
                if(orderDetailResponse.getRoom().getStatus().equals("0")){
                    mServiceTagView.addTag(createTag(item.getId(),item.getPrivilege_name(),false));
                }else{
                    mServiceTagView.addTag(createTag(item.getId(),item.getPrivilege_name(),true));
                }

            }
            if(orderDetailResponse.getRoom().getStatus().equals("0")){
                orderDetailResponse.getPrivilege().clear();
            }
        }
    }

    //根据id 获取房间服务
    private OrderPrivilegeResponse getPrivilegeById(int id){

        if(totalPrivileges != null){
            for(OrderPrivilegeResponse privilege : totalPrivileges){
                if(id == privilege.getId()){
                    return privilege;
                }
            }
        }
        return null;
    }

    //根据id 获取 房间选项
    private OrderRoomTagResponse getRoomTagById(int id){
        if(totalRoomTags != null){
            for(OrderRoomTagResponse roomTag : totalRoomTags){
                if(id == roomTag.getId()){
                    return roomTag;
                }
            }
        }
        return null;
    }

    private Tag createTag(int id,String tagstr,boolean isChecked){
        Tag tag = new Tag(id,tagstr);
        tag.tagTextColor = Color.parseColor("#000000");
        tag.layoutColor =  Color.parseColor("#ffffff");
        tag.layoutColorPress = Color.parseColor("#DDDDDD");
        //or tag.background = this.getResources().getDrawable(R.drawable.custom_bg);
        tag.radius = 40f;
        tag.tagTextSize = 18f;
        tag.layoutBorderSize = 1f;
        tag.layoutBorderColor = Color.parseColor("#000000");
        tag.deleteIndicatorColor =  Color.parseColor("#ff0000");
        tag.deleteIndicatorSize =  18f;
        tag.isDeletable = true;
        if(isChecked){
            tag.deleteIcon = "√";
        }else{
            tag.deleteIcon = "";
        }

        return tag;
    }

    //设置离开和到达的日期
    private void setOrderDate(ArrayList<Calendar> calendarList){
        mChineseFormat = new SimpleDateFormat("MM月dd日");
        Date arrivalDate = calendarList.get(0).getTime();
        Date leaveDate   = calendarList.get(1).getTime();

        mTvArriveDate.setText(mChineseFormat.format(arrivalDate));
        mTvLeaveDate.setText(mChineseFormat.format(leaveDate));
        try{
            dayNum = TimeUtil.daysBetween(arrivalDate,leaveDate);
            mTvDateTips.setText("共"+dayNum+"晚，在"+mTvLeaveDate.getText()+"13点前退房");
        }catch (Exception e){

        }
    }

    //显示房间数量选择对话框
    private void showRoomNumChooseDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_room_number);

        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        int width = DeviceUtils.getScreenWidth(this);

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (width*0.8); // 宽度
        // lp.height = 300; // 高度
        //lp.alpha = 0.7f; // 透明度
        dialogWindow.setAttributes(lp);
        dialog.show();
        RadioGroup group = (RadioGroup)dialog.findViewById(R.id.gendergroup);
        RadioButton mRadio1 = (RadioButton) dialog.findViewById(R.id.rbtn_one_room);
        RadioButton mRadio2 = (RadioButton) dialog.findViewById(R.id.rbtn_two_room);
        RadioButton mRadio3 = (RadioButton) dialog.findViewById(R.id.rbtn_three_room);
        if(roomNum == 1){
            mRadio1.setChecked(true);
        }
        else  if(roomNum == 2){
            mRadio2.setChecked(true);
        }
        else  if(roomNum == 3){
            mRadio3.setChecked(true);
        }
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId ==R.id.rbtn_one_room) {
                    roomNum = 1;
                }
                else if (checkedId ==R.id.rbtn_two_room) {
                    roomNum = 2;
                }else{
                    roomNum = 3;
                }
                 notifyRoomNumberChange();
                dialog.cancel();
            }
        });
    }

    //房间数量已经变 通知UI做调整
    private void notifyRoomNumberChange(){
        mIusvRoomNumber.setTextContent2(roomNum + "间");
        for(int i=0;i<customerList.size();i++){
            if(i < roomNum){
                customerList.get(i).setVisibility(View.VISIBLE);
            }else{
                customerList.get(i).setVisibility(View.GONE);
            }
        }
    }

    private void initListener() {
        //保存订单
        mBtnSendOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmOrder();
            }
        });
        //取消订单
        mBtnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelOrder();
            }
        });

        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderDetailActivity.this.finish();
            }
        });

        mLltTicketContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderDetailActivity.this, ChooseInvoiceActivity.class);
                startActivityForResult(intent, TICKET_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

        for(int i=0;i<customerList.size();i++){
            final int index = i;
            customerList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ItemUserSettingView setView = (ItemUserSettingView) view;
                    Intent intent = new Intent(OrderDetailActivity.this, ChoosePeopleActivity.class);
                    intent.putExtra("index",index);
                    startActivityForResult(intent, PEOPLE_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);

                }
            });
        }

        mlltRemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderDetailActivity.this, AddRemarkActivity.class);
                intent.putExtra("remark", mTvRemark.getText());
                intent.putExtra("tips", "如果有其他要求，请在此说明。");
                intent.putExtra("title", "添加订单备注");
                intent.putExtra("hint", "请输入订单备注");
                startActivityForResult(intent, REMARK_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });






//        mIusvRoomNumber.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showRoomNumChooseDialog();
//            }
//        });

//        mLltDateContainer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(OrderDetailActivity.this, CalendarActivity.class);
//                if (calendarList != null) {
//                    intent.putExtra("calendarList", calendarList);
//                }
//                startActivityForResult(intent, CalendarActivity.CALENDAR_REQUEST_CODE);
//                overridePendingTransition(R.anim.slide_in_right,
//                        R.anim.slide_out_left);
//            }
//        });

//        mLltYuan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(OrderDetailActivity.this, GoodListActivity.class);
//                if (lastGoodInfoVo != null && !StringUtil.isEmpty(lastGoodInfoVo.getId())) {
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("GoodInfoVo", lastGoodInfoVo);
//                    intent.putExtras(bundle);
//                    startActivityForResult(intent, GOOD_REQUEST_CODE);
//                    overridePendingTransition(R.anim.slide_in_right,
//                            R.anim.slide_out_left);
//                }
//            }
//        });
    }

    //初始化标签点击事件
    private void initTagClickEvent(){
        mRoomTagView.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                if(tag.deleteIcon.equals("")){
                    tag.deleteIcon = "√";
                }
                else{
                    tag.deleteIcon = "";
                }
                mRoomTagView.drawTags();
                ArrayList<OrderRoomTagResponse> roomTags = orderDetailResponse.getRoom_tag();
                if(roomTags == null){
                    roomTags = new ArrayList<OrderRoomTagResponse>();
                }else{
                    roomTags.clear();
                }
                for(Tag item : mRoomTagView.getTags()){
                    if(item.deleteIcon.equals("√")){
                        roomTags.add(getRoomTagById(item.id));
                    }
                }
                orderDetailResponse.setRoom_tag(roomTags);

            }
        });

        mServiceTagView.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                if(tag.deleteIcon.equals("")){
                    tag.deleteIcon = "√";
                }
                else{
                    tag.deleteIcon = "";
                }
                mServiceTagView.drawTags();
                ArrayList<OrderPrivilegeResponse> privileges = orderDetailResponse.getPrivilege();
                if(privileges == null){
                    privileges = new ArrayList<OrderPrivilegeResponse>();
                }else{
                    privileges.clear();
                }
                for(Tag item : mServiceTagView.getTags()){
                    if(item.deleteIcon.equals("√")){
                        privileges.add(getPrivilegeById(item.id));
                    }
                }

            }
        });
    }

    //取消订单
    private void cancelOrder(){
        MineNetController.getInstance().init(this);
        if(CacheUtil.getInstance().getToken() == null){
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ProtocolUtil.updateOrderUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        DialogUtil.getInstance().cancelProgressDialog();
                        LogUtil.getInstance().info(LogLevel.ERROR, "取消订单响应结果:" + response);
                        if(!TextUtils.isEmpty(response)){
                            BaseResponse baseResponse = new Gson().fromJson(response,BaseResponse.class);
                            if(baseResponse.isSet()){
                                Intent intent = new Intent(OrderDetailActivity.this, ChatActivity.class);
                                intent.putExtra("shop_id", orderDetailResponse.getRoom().getShopid());
                                intent.putExtra("shop_name", orderDetailResponse.getRoom().getFullname());
                                intent.putExtra("text_context", "您好，我已取消该订单，请跟进。");
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.slide_out_left);
                                finish();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtil.getInstance().cancelProgressDialog();
                LogUtil.getInstance().info(LogLevel.INFO, "取消订单错误信息:" +  error.getMessage());
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                //status 订单状态=2 确认 , 1 取消 *
                Map<String, String> map = new HashMap<String, String>();
                map.put("userid", CacheUtil.getInstance().getUserId());
                map.put("token", CacheUtil.getInstance().getToken());
                map.put("reservation_no", reservationNo);
                map.put("status","1");
                LogUtil.getInstance().info(LogLevel.ERROR,"map="+map.toString());
                return map;
            }
        };
        LogUtil.getInstance().info(LogLevel.ERROR,stringRequest.toString());
        MineNetController.getInstance().requestGetUserInfoTask(stringRequest);
    }

    //确认订单
    private void confirmOrder() {

        MineNetController.getInstance().init(this);
        if(CacheUtil.getInstance().getToken() == null){
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ProtocolUtil.updateOrderUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        DialogUtil.getInstance().cancelProgressDialog();
                        LogUtil.getInstance().info(LogLevel.ERROR, "确认订单响应结果:" + response);
                        if(!TextUtils.isEmpty(response)){
                            BaseResponse baseResponse = new Gson().fromJson(response,BaseResponse.class);
                           if(baseResponse.isSet()){
                               Intent intent = new Intent(OrderDetailActivity.this, ChatActivity.class);
                               intent.putExtra("shop_id", orderDetailResponse.getRoom().getShopid());
                               intent.putExtra("shop_name", orderDetailResponse.getRoom().getFullname());
                               intent.putExtra("text_context", "您好，我已确认该订单，请跟进。");
                               startActivity(intent);
                               overridePendingTransition(R.anim.slide_in_right,
                                       R.anim.slide_out_left);
                               finish();
                           }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtil.getInstance().cancelProgressDialog();
                LogUtil.getInstance().info(LogLevel.ERROR, "确认订单错误信息:" +  error.getMessage());
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                //status 订单状态=2 确认 , 1 取消 *
                Map<String, String> map = generatedPostParm();
                map.put("status","2");
                LogUtil.getInstance().info(LogLevel.ERROR,"map="+map.toString());
                return map;
            }
        };
        LogUtil.getInstance().info(LogLevel.ERROR,stringRequest.toString());
        MineNetController.getInstance().requestGetUserInfoTask(stringRequest);
    }

    //产生post 表单参数
    private Map<String, String> generatedPostParm(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("userid", CacheUtil.getInstance().getUserId());
        map.put("token", CacheUtil.getInstance().getToken());
        map.put("reservation_no", reservationNo);
        String userids = "";
        if(orderDetailResponse.getUsers() != null){
            for(int i=0;i<orderDetailResponse.getRoom().getRooms();i++){
                if(i == 0){
                    userids = userids + orderDetailResponse.getUsers().get(i).getId();
                }else{
                    userids = userids + ","+orderDetailResponse.getUsers().get(i).getId();
                }
            }
        }
        map.put("users",userids);

        map.put("invoice[invoice_title]",orderDetailResponse.getInvoice().getInvoice_title());
        map.put("invoice[invoice_get_id]","1");

        String roomtags = "";
        if(orderDetailResponse.getRoom_tag() != null){
            for(int i=0;i<orderDetailResponse.getRoom_tag().size();i++){
                if(i == 0){
                    roomtags = "" + orderDetailResponse.getRoom_tag().get(i).getId();
                }else{
                    roomtags = roomtags + ","+orderDetailResponse.getRoom_tag().get(i).getId();
                }
            }
        }
        map.put("room_tags",roomtags);

        String privileges = "";
        if(orderDetailResponse.getPrivilege() != null){
            for(int i=0;i<orderDetailResponse.getPrivilege().size();i++){
                if(i == 0){
                    privileges = "" + orderDetailResponse.getPrivilege().get(i).getId();
                }else{
                    privileges = privileges + ","+orderDetailResponse.getPrivilege().get(i).getId();
                }
            }
        }
        map.put("privilege",privileges);

        map.put("remark",TextUtils.isEmpty(orderDetailResponse.getRoom().getRemark())? "" : orderDetailResponse.getRoom().getRemark());
       // map.put("pay_status",orderDetailResponse.getRoom().getPay_status());

        return map;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(RESULT_OK == resultCode){
            if(CalendarActivity.CALENDAR_REQUEST_CODE == requestCode){
                if(null != data){
                    //calendarList = (ArrayList<Calendar>)data.getSerializableExtra("calendarList");
                    //setOrderDate(calendarList);
                }
            }
            else if(GOOD_REQUEST_CODE == requestCode){
                if(null != data){
                    //lastGoodInfoVo = (GoodInfoVo)data.getSerializableExtra("GoodInfoVo");
                    //setOrderRoomType(lastGoodInfoVo);
                }
            }
            else if(PEOPLE_REQUEST_CODE == requestCode){
                if(null != data){
                    OrderUsersResponse orderUsersResponse = (OrderUsersResponse)data.getSerializableExtra("selectPeople");
                    int index = data.getIntExtra("index", 0);
                    customerList.get(index).setTextContent2(orderUsersResponse.getRealname());
                    ArrayList<OrderUsersResponse> users = orderDetailResponse.getUsers();
                    users.set(index,orderUsersResponse);
                    orderDetailResponse.setUsers(users);
                }
            }
            else if(TICKET_REQUEST_CODE == requestCode){
                if(null != data){
                    OrderInvoiceResponse orderInvoiceResponse = (OrderInvoiceResponse)data.getSerializableExtra("selectInvoice");
                    mTvTicket.setText(orderInvoiceResponse.getInvoice_title());
                    orderDetailResponse.setInvoice(orderInvoiceResponse);
                }
            }
            else if(REMARK_REQUEST_CODE == requestCode){
                if(null != data){
                    String remark = data.getStringExtra("remark");
                    mTvRemark.setText(remark);
                    orderDetailResponse.getRoom().setRemark(remark);
                }
            }
        }
    }



}
