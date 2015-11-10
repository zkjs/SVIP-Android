package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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


import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.im.ChatActivity;
import com.zkjinshi.svip.ext.ShopListManager;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
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


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


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

    private final static String TAG = OrderDetailActivity.class.getSimpleName();

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
    private boolean modifyEnable = false; //订单是否可修改


    public static final int GOOD_REQUEST_CODE = 6;
    public static final int PEOPLE_REQUEST_CODE = 7;
    public static final int TICKET_REQUEST_CODE = 8;
    public static final int REMARK_REQUEST_CODE = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        reservationNo = getIntent().getStringExtra("reservation_no");
        shopId = getIntent().getStringExtra("shopid");
        initView();
        initListener();
        loadOrderInfoByReservationNo();
    }

    //根据订单号加载订单详细信息。
    private void loadOrderInfoByReservationNo() {
        String url =  ProtocolUtil.getOneOrderUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        bizMap.put("reservation_no", reservationNo);
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    orderDetailResponse = new Gson().fromJson( result.rawResult,OrderDetailResponse.class);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    orderDetailResponse = null;
                    DialogUtil.getInstance().showToast(OrderDetailActivity.this,"获取订单详情失败");
                    finish();
                }
                if(orderDetailResponse != null){
                    initData();
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
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

        mBtnSendOrder.setText("确认订单");
        mTitle.setTextTitle(ShopListManager.getInstance().getShopName(shopId));
        mTitle.setTextColor(this, R.color.White);
        mTitle.getmRight().setVisibility(View.GONE);
    }

    private void initData(){
        //初始化订单状态的显示
        initOrderStatus();
        //初始化入住时间
        initArrivalDate();
        //初始化入住人信息
        initPeople();
        //初始化房间信息信息
        initRoom();
        //初始化房间选项标签
        initRoomTags();
        //初始化其他服务标签
        initServiceTags();
        //初始化发票
        initTicket();
        //初始化订单备注
        initRemark();

        if(modifyEnable){
            //初始化标签点击事件
            initTagClickEvent();
        }
    }
    //初始化入住时间
    private void initArrivalDate(){
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
    }

    //初始化入住人信息
    private void initPeople(){
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
            if(i<customerList.size() && users != null && users.size() > 0 && i<users.size()){
                OrderUsersResponse user = users.get(i);
                customerList.get(i).getmTextContent2().setText(user.getRealname());
            }

        }
    }

    //初始化房间信息信息
    private void initRoom(){
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_room_pic_default)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.ic_room_pic_default)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_room_pic_default)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
        if(!TextUtils.isEmpty(orderDetailResponse.getRoom().getImgurl())){
            String logoUrl = ProtocolUtil.getGoodImgUrl(orderDetailResponse.getRoom().getImgurl());
            ImageLoader.getInstance().displayImage(logoUrl,mIvRoomImg,options);
        }
        mRoomType.setText(orderDetailResponse.getRoom().getRoom_type());
        mRoomRate.setText(orderDetailResponse.getRoom().getRoom_rate());
    }

    //初始化订单状态的显示
    private void initOrderStatus() {
        //订单状态 默认0可取消订单 1已取消订单 2已确认订单 3已经完成的订单 4正在入住中 5已删除订单
        //支付状态 0未支付,1已支付,3支付一部分,4已退款, 5已挂账

        final String orderStatus = orderDetailResponse.getRoom().getStatus();
        String payStatus = orderDetailResponse.getRoom().getPay_status();

        mBtnSendOrder.setVisibility(View.GONE);
        mBtnCancelOrder.setVisibility(View.GONE);
        modifyEnable = false;

        if (orderStatus.equals("0")){
            modifyEnable = true;
            mTvOrderStatus.setText("已提交");
            mBtnSendOrder.setVisibility(View.VISIBLE);
            mBtnCancelOrder.setVisibility(View.VISIBLE);

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
        if(orderInvoiceResponse != null && orderInvoiceResponse.getInvoice_title() != null){
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
        //确认订单
        mBtnSendOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(modifyEnable){
                    confirmOrder();
                }
            }
        });
        //取消订单
        mBtnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelOrder();
            }
        });
        //返回
        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderDetailActivity.this.finish();
            }
        });
        //修改发票
        mLltTicketContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(modifyEnable){
                    Intent intent = new Intent(OrderDetailActivity.this, ChooseInvoiceActivity.class);
                    startActivityForResult(intent, TICKET_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }

            }
        });
        //修改入住人
        for(int i=0;i<customerList.size();i++){
            final int index = i;
            customerList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(modifyEnable){
                        ItemUserSettingView setView = (ItemUserSettingView) view;
                        Intent intent = new Intent(OrderDetailActivity.this, ChoosePeopleActivity.class);
                        intent.putExtra("index",index);
                        startActivityForResult(intent, PEOPLE_REQUEST_CODE);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.slide_out_left);
                    }

                }
            });
        }
        //修改备注
        mlltRemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(modifyEnable){
                    Intent intent = new Intent(OrderDetailActivity.this, AddRemarkActivity.class);
                    intent.putExtra("remark", mTvRemark.getText());
                    intent.putExtra("tips", "如果有其他要求，请在此说明。");
                    intent.putExtra("title", "添加订单备注");
                    intent.putExtra("hint", "请输入订单备注");
                    startActivityForResult(intent, REMARK_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }

            }
        });

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
        String url = ProtocolUtil.updateOrderUrl();
        Log.i(TAG,url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        bizMap.put("reservation_no", reservationNo);
        bizMap.put("status","1");
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    BaseResponse baseResponse = new Gson().fromJson(result.rawResult,BaseResponse.class);
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

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();

    }

    //确认订单
    private void confirmOrder() {
        String url =  ProtocolUtil.updateOrderUrl();
        Log.i(TAG,url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = generatedPostParm();
        bizMap.put("status","2");
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    BaseResponse baseResponse = new Gson().fromJson(result.rawResult,BaseResponse.class);
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

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    //产生post 表单参数
    private HashMap<String, String> generatedPostParm(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userid", CacheUtil.getInstance().getUserId());
        map.put("token", CacheUtil.getInstance().getToken());
        map.put("reservation_no", reservationNo);
        String userids = "";
        if(orderDetailResponse.getUsers() != null && orderDetailResponse.getUsers().size() > 0){
            boolean isFirst = true;
            for(int i=0;i<orderDetailResponse.getRoom().getRooms();i++){

                if(orderDetailResponse.getUsers().get(i) != null){
                    if(isFirst){
                        userids = userids + orderDetailResponse.getUsers().get(i).getId();
                        isFirst = false;
                    }else{
                        userids = userids + ","+orderDetailResponse.getUsers().get(i).getId();
                    }
                }
            }
        }
        if(!TextUtils.isEmpty(userids)){
            map.put("users",userids);
        }

        if(orderDetailResponse.getInvoice() != null && !TextUtils.isEmpty(orderDetailResponse.getInvoice().getInvoice_title())){
            map.put("invoice[invoice_title]",orderDetailResponse.getInvoice().getInvoice_title());
            map.put("invoice[invoice_get_id]","1");
        }


        String roomtags = "";
        if(orderDetailResponse.getRoom_tag() != null){
            for(int i=0;i<orderDetailResponse.getRoom_tag().size();i++){
                if(i == 0){
                    roomtags = "" + orderDetailResponse.getRoom_tag().get(i).getContent();
                }else{
                    roomtags = roomtags + ","+orderDetailResponse.getRoom_tag().get(i).getContent();
                }
            }
        }
        if(!TextUtils.isEmpty(roomtags)){
            map.put("room_tags",roomtags);
        }


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
        if(!TextUtils.isEmpty(privileges)){
            map.put("privilege",privileges);
        }


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
                    for(int i=0;i<3;i++){
                        if(TextUtils.isEmpty(customerList.get(i).getTextContent2())){
                           if(i<index){
                               index = i;
                           }
                            break;
                        }
                    }
                    customerList.get(index).setTextContent2(orderUsersResponse.getRealname());

                    ArrayList<OrderUsersResponse> users = orderDetailResponse.getUsers();
                    if(users == null){
                        users = new ArrayList<OrderUsersResponse>();
                    }
                    if(index >= users.size()){
                        users.add(orderUsersResponse);
                    }else{
                        users.set(index, orderUsersResponse);
                    }
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
