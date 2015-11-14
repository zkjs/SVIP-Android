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
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.NetWorkUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.im.ChatActivity;
import com.zkjinshi.svip.bean.BookOrder;

import com.zkjinshi.svip.bean.CustomerServiceBean;
import com.zkjinshi.svip.bean.HeadBean;
import com.zkjinshi.svip.factory.GoodInfoFactory;
import com.zkjinshi.svip.manager.CustomerServicesManager;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestListener;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.CustomerServiceListResponse;
import com.zkjinshi.svip.response.GoodInfoResponse;
import com.zkjinshi.svip.response.OrderDetailResponse;
import com.zkjinshi.svip.response.OrderInvoiceResponse;
import com.zkjinshi.svip.response.OrderRoomResponse;
import com.zkjinshi.svip.response.OrderUsersResponse;
import com.zkjinshi.svip.sqlite.ShopDetailDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.StringUtil;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.view.ItemUserSettingView;
import com.zkjinshi.svip.vo.GoodInfoVo;
import com.zkjinshi.svip.vo.TicketVo;

import org.apache.log4j.Level;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.kaede.tagview.OnTagClickListener;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

/**
 * 预订中的订单详情页面
 *
 * 开发者：dujiande
 * 日期：2015/8/27
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderBookingActivity extends Activity{

    private final static String TAG = OrderBookingActivity.class.getSimpleName();

    private ItemTitleView   mTitle;
    private TextView        mRoomType;
    private TextView        mTvArriveDate;
    private TextView        mTvLeaveDate;
    private TextView        mTvDateTips;
    private LinearLayout    mLltDateContainer;
    private ImageView       mIvRoomImg;


    private Button          mBtnSendOrder;
    private Button          mBtnCancelOrder;
    private LinearLayout    mLltYuan;
    private LinearLayout    mLltTicketContainer;
    private RelativeLayout  mrltRoomImg;

    private TagView mRoomTagView;
    private TagView mServiceTagView;

    private ItemUserSettingView mIusvRoomNumber;
    private TextView mTvTicket;
    private ArrayList<ItemUserSettingView> customerList;

    private TextView mTvRemark;
    private LinearLayout mlltRemark;


    private SimpleDateFormat mSimpleFormat;
    private SimpleDateFormat mChineseFormat;

    private ArrayList<Calendar> calendarList = null;

    private int dayNum;
    private int roomNum;

    private String shopId;
    private ArrayList<GoodInfoResponse> goodResponsseList;
    private ArrayList<GoodInfoVo> goodInfoList;
    private DisplayImageOptions options;
    private GoodInfoVo lastGoodInfoVo;
    private TicketVo tickeVo;

    private OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
    private OrderRoomResponse orderRoomResponse = new OrderRoomResponse();
    private ArrayList<OrderUsersResponse> users = new ArrayList<OrderUsersResponse>();


    public static final int GOOD_REQUEST_CODE = 6;
    public static final int PEOPLE_REQUEST_CODE = 7;
    public static final int TICKET_REQUEST_CODE = 8;
    public static final int REMARK_REQUEST_CODE = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_booking);

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
        mBtnCancelOrder = (Button)findViewById(R.id.btn_cancel_order);
        mRoomType     = (TextView) findViewById(R.id.tv_room_type);
        mLltYuan      = (LinearLayout)findViewById(R.id.rl_yuan);

        mRoomTagView = (TagView)findViewById(R.id.tagview_room_tags);
        mServiceTagView = (TagView)findViewById(R.id.tagview_service_tags);

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

        mTvTicket  = (TextView)findViewById(R.id.tv_ticket);
        mLltTicketContainer = (LinearLayout)findViewById(R.id.llt_ticket_container);

        mTvRemark = (TextView)findViewById(R.id.tv_remark);
        mlltRemark = (LinearLayout)findViewById(R.id.llt_order_remark);
        mrltRoomImg = (RelativeLayout)findViewById(R.id.rl_order_top);
    }

    private void initData() {
        GoodListNetController.getInstance().init(this);
        GoodListUiController.getInstance().init(this);

        mBtnSendOrder.setText("发送订单给客服");
        mBtnCancelOrder.setVisibility(View.GONE);

        calendarList = new ArrayList<Calendar>();
        mTitle.setTextTitle(ShopDetailDBUtil.getInstance().queryShopNameByShopID(shopId));
        mTitle.setTextColor(this, R.color.White);
        mTitle.getmRight().setVisibility(View.GONE);

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

        roomNum = 2;
        notifyRoomNumberChange();
        Drawable drawable= getResources().getDrawable(R.mipmap.ic_get_into_w);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mIusvRoomNumber.getmTextContent2().setCompoundDrawables(null, null, drawable, null);

        for(int i=0;i<customerList.size();i++){
            Drawable d= getResources().getDrawable(R.mipmap.ic_get_into_w);
            d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
            customerList.get(i).getmTextContent2().setCompoundDrawables(null, null, d, null);
            users.add(new OrderUsersResponse());
        }


        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_room_pic_default)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.ic_room_pic_default)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_room_pic_default)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();

        String url = ProtocolUtil.getGoodListUrl(shopId);
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
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
                    Type listType = new TypeToken<List<GoodInfoResponse>>(){}.getType();
                     Gson gson = new Gson();
                     goodResponsseList = gson.fromJson(result.rawResult, listType);
                     if(null != goodResponsseList && !goodResponsseList.isEmpty()){
                         goodInfoList = GoodInfoFactory.getInstance().bulidGoodList(goodResponsseList);
                         if(null != goodInfoList && !goodInfoList.isEmpty()){
                             GoodInfoVo goodInfoVo = goodInfoList.get(0);
                             lastGoodInfoVo = goodInfoVo;
                             setOrderRoomType(goodInfoVo);
                         }
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



    //设置离开和到达的日期
    private void setOrderDate(ArrayList<Calendar> calendarList){
        mChineseFormat = new SimpleDateFormat("MM月dd日");
        Date arrivalDate = calendarList.get(0).getTime();
        Date leaveDate   = calendarList.get(1).getTime();

        mTvArriveDate.setText(mChineseFormat.format(arrivalDate));
        mTvLeaveDate.setText(mChineseFormat.format(leaveDate));

        orderRoomResponse.setArrival_date(mSimpleFormat.format(arrivalDate));
        orderRoomResponse.setDeparture_date(mSimpleFormat.format(leaveDate));
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
                if (checkedId == R.id.rbtn_one_room) {
                    roomNum = 1;
                } else if (checkedId == R.id.rbtn_two_room) {
                    roomNum = 2;
                } else {
                    roomNum = 3;
                }
                notifyRoomNumberChange();
                dialog.cancel();
            }
        });

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        };

        mRadio1.setOnClickListener(clickListener);
        mRadio2.setOnClickListener(clickListener);
        mRadio3.setOnClickListener(clickListener);

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
        orderRoomResponse.setRooms(roomNum);
    }

    private void initListener() {
        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderBookingActivity.this.finish();
            }
        });

        mLltTicketContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderBookingActivity.this, ChooseInvoiceActivity.class);
                startActivityForResult(intent, TICKET_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

        mlltRemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderBookingActivity.this, AddRemarkActivity.class);
                intent.putExtra("remark", mTvRemark.getText());
                intent.putExtra("tips", "如果有其他要求，请在此说明。");
                intent.putExtra("title", "添加订单备注");
                intent.putExtra("hint", "请输入订单备注");
                startActivityForResult(intent, REMARK_REQUEST_CODE);
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
                    Intent intent = new Intent(OrderBookingActivity.this, ChoosePeopleActivity.class);
                    intent.putExtra("index",index);
                    startActivityForResult(intent, PEOPLE_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);

                }
            });
        }

        mRoomTagView.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                if(tag.id != 0){
                    if(tag.deleteIcon.equals("")){
                        tag.deleteIcon = "√";
                    }
                    else{
                        tag.deleteIcon = "";
                    }
                    mRoomTagView.drawTags();
                }else{

                }
            }
        });

        mServiceTagView.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                if(tag.id != 0){
                    if(tag.deleteIcon.equals("")){
                        tag.deleteIcon = "√";
                    }
                    else{
                        tag.deleteIcon = "";
                    }
                    mServiceTagView.drawTags();
                }else{

                }
            }
        });


        mIusvRoomNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRoomNumChooseDialog();
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
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

        mLltYuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               gotoChooseRoomTypeList();
            }
        });

        mrltRoomImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoChooseRoomTypeList();
            }
        });

        mBtnSendOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastGoodInfoVo != null && !StringUtil.isEmpty(lastGoodInfoVo.getId())) {
                    orderDetailResponse.setRoom(orderRoomResponse);
                    String shopId = orderDetailResponse.getRoom().getShopid();
                    ArrayList<OrderUsersResponse> userslist = new ArrayList<OrderUsersResponse>();
                    for(int i=0;i<orderRoomResponse.getRooms();i++){
                        OrderUsersResponse user = users.get(i);
                        if(!TextUtils.isEmpty(user.getRealname())){
                            userslist.add(user);
                        }
                    }
                    orderDetailResponse.setUsers(userslist);
                    orderDetailResponse.setContent("您好，帮我预定这间房");
                    CustomerServicesManager.getInstance().requestServiceListTask(OrderBookingActivity.this, shopId, new NetRequestListener() {
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
                            Log.i(TAG, "result:" + result.rawResult);
                            CustomerServiceListResponse customerServiceListResponse = new Gson().fromJson(result.rawResult, CustomerServiceListResponse.class);
                            if (null != customerServiceListResponse) {
                                HeadBean head = customerServiceListResponse.getHead();
                                if (null != head) {
                                    boolean isSet = head.isSet();
                                    if (isSet) {
                                        ArrayList<CustomerServiceBean> customerServiceList = customerServiceListResponse.getData();
                                        String salesId = head.getExclusive_salesid();
                                        CustomerServiceBean customerService = null;
                                        if (null != customerServiceList && !customerServiceList.isEmpty()) {
                                            if (!TextUtils.isEmpty(salesId)) {//有专属客服
                                                customerService = CustomerServicesManager.getInstance().getExclusiveCustomerServic(customerServiceList, salesId);
                                            } else {//无专属客服
                                                customerService = CustomerServicesManager.getInstance().getRandomCustomerServic(customerServiceList);
                                                if(null != salesId){
                                                    salesId = customerService.getSalesid();
                                                }
                                            }
                                        }
                                        if (null != customerService) {
                                            Intent intent = new Intent(OrderBookingActivity.this, ChatActivity.class);
                                            intent.putExtra(Constants.EXTRA_USER_ID, salesId);
                                            intent.putExtra("shop_id", orderDetailResponse.getRoom().getShopid());
                                            intent.putExtra("shop_name", orderDetailResponse.getRoom().getFullname());
                                            intent.putExtra("orderDetailResponse", orderDetailResponse);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.slide_in_right,
                                                    R.anim.slide_out_left);
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void beforeNetworkRequestStart() {

                        }
                    });
                }
            }
        });

    }

    //跳转到选择房型列表
    private void gotoChooseRoomTypeList(){
        Intent intent = new Intent(OrderBookingActivity.this, GoodListActivity.class);
        if (lastGoodInfoVo != null && !StringUtil.isEmpty(lastGoodInfoVo.getId())) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("GoodInfoVo", lastGoodInfoVo);
            bundle.putSerializable("goodInfoList",goodInfoList);
            intent.putExtras(bundle);
            startActivityForResult(intent, GOOD_REQUEST_CODE);
            overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(RESULT_OK == resultCode){
            if(CalendarActivity.CALENDAR_REQUEST_CODE == requestCode){
                if(null != data){
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
            else if(PEOPLE_REQUEST_CODE == requestCode){
                if(null != data){
//                    String name = data.getStringExtra("name");
//                    int index = data.getIntExtra("index", 0);
//                    customerList.get(index).setTextContent2(name);

                    OrderUsersResponse orderUsersResponse = (OrderUsersResponse)data.getSerializableExtra("selectPeople");
                    int index = data.getIntExtra("index", 0);
                    customerList.get(index).setTextContent2(orderUsersResponse.getRealname());
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
                }
            }
        }
    }

    private void setOrderRoomType(GoodInfoVo goodInfoVo) {
        lastGoodInfoVo = goodInfoVo;
        String imageUrl = goodInfoVo.getImgurl();
        if(!TextUtils.isEmpty(imageUrl)){
            String logoUrl = ProtocolUtil.getGoodImgUrl(imageUrl);
            ImageLoader.getInstance().displayImage(logoUrl,mIvRoomImg,options);
            orderRoomResponse.setImgurl(imageUrl);
        }
        mRoomType.setText(goodInfoVo.getRoom()+goodInfoVo.getType());
        orderRoomResponse.setRoom_type(goodInfoVo.getRoom() + goodInfoVo.getType());
        orderRoomResponse.setShopid(shopId);
        orderRoomResponse.setFullname(ShopDetailDBUtil.getInstance().queryShopNameByShopID(shopId));
        orderRoomResponse.setRoom_typeid(goodInfoVo.getId());

    }

}
