package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.activity.common.SettingTicketsActivity;
import com.zkjinshi.svip.activity.im.single.ChatActivity;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.base.BaseApplication;
import com.zkjinshi.svip.bean.CustomerServiceBean;
import com.zkjinshi.svip.bean.HeadBean;
import com.zkjinshi.svip.manager.CustomerServicesManager;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.AddOrderResponse;
import com.zkjinshi.svip.response.CustomerServiceListResponse;
import com.zkjinshi.svip.response.OrderInvoiceResponse;
import com.zkjinshi.svip.sqlite.ShopDetailDBUtil;
import com.zkjinshi.svip.utils.Base64Encoder;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.ItemNumView;
import com.zkjinshi.svip.view.ItemShowView;
import com.zkjinshi.svip.view.datepicker.DatePickerPopWindow;
import com.zkjinshi.svip.vo.OrderDetailForDisplay;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by dujiande on 2015/12/29.
 */
public class NormalBookingActivity extends BaseActivity {

    private final static String TAG = NormalBookingActivity.class.getSimpleName();

    private TextView titleTv,remarkTv;
    private ImageView roomIv;
    private ItemShowView dateTimeIsv;
    private ItemNumView peopleNumTnv;
    private ItemShowView contactTsv;
    private ItemShowView phoneTsv;
    private ItemShowView invoiceTsv;

    private Button confirmBtn;

    private ArrayList<Calendar> calendarList = null;
    private SimpleDateFormat mSimpleFormat;
    private SimpleDateFormat mChineseFormat;
    private DisplayImageOptions options;
    private OrderDetailForDisplay orderDetailForDisplay;
    private String shopId = "120";
    private String shopName;
    private String shopImg;
    private String salesId;
    private String category = "0";

    private CustomerServiceBean customerService = null;

    public static final int SUBMIT_BOOKING = 5;

    public static final int PEOPLE_REQUEST_CODE = 7;
    public static final int TICKET_REQUEST_CODE = 8;
    public static final int REMARK_REQUEST_CODE = 9;
    public static final int PHONE_REQUEST_CODE = 10;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUBMIT_BOOKING:
                    submitBooking(category);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_booking);

        shopId = getIntent().getStringExtra("shopid");
        shopName = getIntent().getStringExtra("shopName");
        shopImg = getIntent().getStringExtra("shopImg");
        initView();
        initData();
        initListener();
    }

    private void initView() {
        titleTv = (TextView)findViewById(R.id.header_title_tv);
        remarkTv = (TextView)findViewById(R.id.tv_remark);
        roomIv = (ImageView)findViewById(R.id.iv_room_img);

        dateTimeIsv = (ItemShowView)findViewById(R.id.ahb_date);
        peopleNumTnv = (ItemNumView)findViewById(R.id.ahb_num);
        contactTsv = (ItemShowView)findViewById(R.id.ahb_people);
        phoneTsv = (ItemShowView)findViewById(R.id.ahb_phone);
        invoiceTsv = (ItemShowView)findViewById(R.id.ahb_ticket);


        confirmBtn = (Button)findViewById(R.id.btn_send_booking_order);
    }

    private void initData() {
        orderDetailForDisplay = new OrderDetailForDisplay();
        shopName = ShopDetailDBUtil.getInstance().queryShopNameByShopID(shopId);
        titleTv.setText(shopName);
        orderDetailForDisplay.setShopname(shopName);
        orderDetailForDisplay.setShopid(shopId);
        orderDetailForDisplay.setUserid(CacheUtil.getInstance().getUserId());

        //初始化图片
        this.options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.mipmap.ic_room_pic_default)// 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.mipmap.ic_room_pic_default)// 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.mipmap.ic_room_pic_default)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(false) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
        ImageLoader.getInstance().displayImage(ProtocolUtil.getHostImgUrl(shopImg),roomIv,options);
        //初始化时间
        setOrderDate(new Date());
    }

    private void initListener() {
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!CacheUtil.getInstance().isLogin()){
                    Intent intent = new Intent(NormalBookingActivity.this,LoginActivity.class);
                    intent.putExtra("isHomeBack",true);
                    startActivity(intent);
                    return;
                }
                findCustomerService();
            }
        });
        //返回
        findViewById(R.id.header_back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });



        //日期
        dateTimeIsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date= new Date();
                try {
                    date = sdf2.parse(orderDetailForDisplay.getArrivaldate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DateFormat df=new SimpleDateFormat("yyyyMMddHHmmss");
                final DatePickerPopWindow popWindow=new DatePickerPopWindow(NormalBookingActivity.this,df.format(date));
                popWindow.showAtLocation(findViewById(R.id.root), Gravity.BOTTOM, 0, 0);
                popWindow.okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            String dateStr = popWindow.dateTv.getText().toString();
                            DateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                            Date arriveDate = sdf.parse(dateStr);
                            setOrderDate(arriveDate);
                            popWindow.dismiss();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        //发票
        invoiceTsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NormalBookingActivity.this, SettingTicketsActivity.class);
                intent.putExtra("isChoose",true);
                startActivityForResult(intent, TICKET_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });
        //备注
        findViewById(R.id.llt_order_remark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NormalBookingActivity.this, AddRemarkActivity.class);
                intent.putExtra("remark", remarkTv.getText());
                intent.putExtra("tips", "如果有其他要求，请在此说明。");
                intent.putExtra("title", "添加订单备注");
                intent.putExtra("hint", "请输入订单备注");
                intent.putExtra("key", "remark");
                startActivityForResult(intent, REMARK_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });
        //联系人
        contactTsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NormalBookingActivity.this, AddRemarkActivity.class);
                intent.putExtra("remark", contactTsv.getValue());
                intent.putExtra("tips", "");
                intent.putExtra("title", "编辑联系人");
                intent.putExtra("hint", "请输入姓名");
                intent.putExtra("key", "name");
                startActivityForResult(intent, PEOPLE_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });
        //手机号码
        phoneTsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NormalBookingActivity.this, AddRemarkActivity.class);
                intent.putExtra("remark", phoneTsv.getValue());
                intent.putExtra("tips", "");
                intent.putExtra("title", "编辑联系方式");
                intent.putExtra("hint", "请输入手机号");
                intent.putExtra("key", "phone");
                startActivityForResult(intent, PHONE_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if(TICKET_REQUEST_CODE == requestCode){
                if(null != data){
                    OrderInvoiceResponse orderInvoiceResponse = (OrderInvoiceResponse)data.getSerializableExtra("selectInvoice");
                    invoiceTsv.setValue(orderInvoiceResponse.getInvoice_title());
                    orderDetailForDisplay.setIsinvoice(1);
                    orderDetailForDisplay.setCompany(orderInvoiceResponse.getInvoice_title());
                }
            }
            else if(REMARK_REQUEST_CODE == requestCode){
                if(null != data){
                    String remark = data.getStringExtra("remark");
                    remarkTv.setText(remark);
                    orderDetailForDisplay.setRemark(remark);
                }
            }
            else if(PEOPLE_REQUEST_CODE == requestCode){
                if(null != data){
                    String remark = data.getStringExtra("remark");
                    contactTsv.setValue(remark);
                    orderDetailForDisplay.setOrderedby(remark);
                }
            }
            else if(PHONE_REQUEST_CODE == requestCode){
                if(null != data){
                    String remark = data.getStringExtra("remark");
                    phoneTsv.setValue(remark);
                    orderDetailForDisplay.setTelephone(remark);
                }
            }

        }
    }

    //设置到达的日期
    private void setOrderDate(Date arriveDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
        String arriveStr = sdf.format(arriveDate);
        dateTimeIsv.setValue(arriveStr);

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        orderDetailForDisplay.setArrivaldate(sdf2.format(arriveDate));
    }

    //查询客服
    public void findCustomerService(){
        CustomerServicesManager.getInstance().requestServiceListTask(
                NormalBookingActivity.this,
                shopId,
                new ExtNetRequestListener(NormalBookingActivity.this) {
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
                        super.onNetworkResponseSucceed(result);
                        try{
                            CustomerServiceListResponse customerServiceListResponse = new Gson().fromJson(result.rawResult, CustomerServiceListResponse.class);
                            if (null != customerServiceListResponse) {
                                HeadBean head = customerServiceListResponse.getHead();
                                if (null != head) {
                                    boolean isSet = head.isSet();
                                    if (isSet) {
                                        ArrayList<CustomerServiceBean> customerServiceList = customerServiceListResponse.getData();
                                        salesId = head.getExclusive_salesid();
                                        if (null != customerServiceList && !customerServiceList.isEmpty()) {
                                            if (!TextUtils.isEmpty(salesId)) {//有专属客服
                                                customerService = CustomerServicesManager.getInstance().getExclusiveCustomerService(customerServiceList, salesId);
                                            } else {//商家客服
                                                customerService = CustomerServicesManager.getInstance().getRandomAdminService(customerServiceList);
                                                if (null != customerService) {
                                                    salesId = customerService.getSalesid();
                                                }
                                            }
                                            if(!TextUtils.isEmpty(salesId)){
                                                handler.sendEmptyMessage(SUBMIT_BOOKING);
                                            }
                                        }

                                    }
                                }
                            }
                        }catch (Exception e){
                            if(e != null){
                                Log.e(TAG,e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void beforeNetworkRequestStart() {

                    }
                });
    }

    public void submitBooking(String category){
        int num = Integer.parseInt(peopleNumTnv.getValue());
        orderDetailForDisplay.setPersoncount(num);
        orderDetailForDisplay.setSaleid(salesId);
        Gson gson = new GsonBuilder().serializeNulls().create();
        String dataJson = gson.toJson(orderDetailForDisplay);
        String encryptedData = Base64Encoder.encode(dataJson);// base 64加密
        String url = ProtocolUtil.orderAddUrl();
        // String url = "http://192.168.199.161:8080/appservice/service/order/add";
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,Object> objectMap = new HashMap<String,Object>();
        objectMap.put("category", category);
        objectMap.put("data", encryptedData);
        netRequest.setObjectParamMap(objectMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.JSONPOST;
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
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {

                    AddOrderResponse addOrderResponse = new Gson().fromJson(result.rawResult,AddOrderResponse.class);
                    if(addOrderResponse.isResult()){
                        orderDetailForDisplay.setOrderno(addOrderResponse.getData());
                        BaseApplication.getInst().clearLeaveTop();
                        Intent intent = new Intent(NormalBookingActivity.this, ChatActivity.class);
                        intent.putExtra("orderDetailForDisplay", orderDetailForDisplay);
                        intent.putExtra(Constants.EXTRA_USER_ID, salesId);
                        intent.putExtra(Constants.EXTRA_FROM_NAME, CacheUtil.getInstance().getUserName());
                        if(null != customerService){
                            String userName = customerService.getName();
                            if (!TextUtils.isEmpty(userName)) {
                                intent.putExtra(Constants.EXTRA_TO_NAME,userName);
                            }
                        }
                        if (!TextUtils.isEmpty(shopId)) {
                            intent.putExtra(Constants.EXTRA_SHOP_ID,shopId);
                        }
                        if (!TextUtils.isEmpty(shopName)) {
                            intent.putExtra(Constants.EXTRA_SHOP_NAME,shopName);
                        }
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }else{
                        DialogUtil.getInstance().showToast(NormalBookingActivity.this,"订单添加失败。");
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
}
