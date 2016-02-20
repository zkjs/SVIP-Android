package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.svip.R;
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
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.ItemCbxView;
import com.zkjinshi.svip.view.ItemNumView;
import com.zkjinshi.svip.view.ItemShowView;
import com.zkjinshi.svip.vo.OrderDetailForDisplay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by dujiande on 2015/12/29.
 */
public class HotelConfirmActivity extends BaseActivity {

    private final static String TAG = HotelConfirmActivity.class.getSimpleName();

    private TextView titleTv,remarkTv,tipsTv,deleteTv;
    private SimpleDraweeView roomIv;
    private ItemShowView dateTimeIsv;
    private ItemShowView roomTypeTsv;
    private ItemNumView roomNumTnv;
    private ItemShowView contactTsv;
    private ItemShowView phoneTsv;
    private ItemShowView payTypeTsv;
    private ItemShowView invoiceTsv;
    private ItemShowView privilegeTsv;
    private ItemShowView noTsv;

    private ItemCbxView breakfastTcv;
    private ItemCbxView noSmokeTcv;

    private String orderNo;
    int payType;

    private Button confirmBtn;
    private OrderDetailForDisplay orderDetailForDisplay = null;

    public static final int PAY_REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_confirm);

        orderNo = getIntent().getStringExtra("orderNo");
        Log.i(com.zkjinshi.base.util.Constants.ZKJINSHI_BASE_TAG,"订单详情页面"+orderNo);
        initView();
        initListener();
        loadOrderInfoByOrderNo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrderInfoByOrderNo();
    }

    private void initView() {
        titleTv = (TextView)findViewById(R.id.header_title_tv);
        remarkTv = (TextView)findViewById(R.id.tv_remark);
        tipsTv = (TextView)findViewById(R.id.tips_tv);
        deleteTv = (TextView)findViewById(R.id.delete_text);
        roomIv = (SimpleDraweeView)findViewById(R.id.iv_room_img);

        dateTimeIsv = (ItemShowView)findViewById(R.id.ahb_date);
        roomTypeTsv = (ItemShowView)findViewById(R.id.ahb_type);
        roomNumTnv = (ItemNumView)findViewById(R.id.ahb_num);
        contactTsv = (ItemShowView)findViewById(R.id.ahb_people);
        phoneTsv = (ItemShowView)findViewById(R.id.ahb_phone);
        payTypeTsv = (ItemShowView)findViewById(R.id.ahb_pay);
        invoiceTsv = (ItemShowView)findViewById(R.id.ahb_ticket);
        privilegeTsv = (ItemShowView)findViewById(R.id.ahb_privilege);


        breakfastTcv = (ItemCbxView)findViewById(R.id.ahb_breakfast);
        noSmokeTcv = (ItemCbxView)findViewById(R.id.ahb_nosmoking);
        breakfastTcv.setVisibility(View.GONE);
        confirmBtn = (Button)findViewById(R.id.btn_send_booking_order);

        Drawable drawable =  getResources().getDrawable(R.mipmap.list_ic_tequan_gary);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        privilegeTsv.titleTv.setCompoundDrawables(drawable,null,null,null);
        privilegeTsv.titleTv.setCompoundDrawablePadding(DisplayUtil.dip2px(this,10));

        noTsv = (ItemShowView)findViewById(R.id.ahb_no);
        noTsv.setValue(orderNo);
    }

    //根据订单号加载订单详细信息。
    private void loadOrderInfoByOrderNo() {
        String url =  ProtocolUtil.orderGetUrl(orderNo);
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
                orderDetailForDisplay = null;
                DialogUtil.getInstance().showToast(HotelConfirmActivity.this,"获取订单详情失败");
                finish();
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    orderDetailForDisplay = new Gson().fromJson( result.rawResult,OrderDetailForDisplay.class);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    orderDetailForDisplay = null;
                    DialogUtil.getInstance().showToast(HotelConfirmActivity.this,"获取订单详情失败");
                    finish();

                }
                if(orderDetailForDisplay != null){
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

    private void initData() {
        titleTv.setText(orderDetailForDisplay.getShopname());
        //订单状态
        if(orderDetailForDisplay.getOrderstatus().equals("待处理")){
            payType = orderDetailForDisplay.getPaytype();
            tipsTv.setVisibility(View.VISIBLE);
            tipsTv.setText("订单待处理中。如需修改，请联系客服");
            confirmBtn.setVisibility(View.GONE);
            deleteTv.setVisibility(View.VISIBLE);
        }else  if(orderDetailForDisplay.getOrderstatus().equals("待确认")){
            tipsTv.setVisibility(View.VISIBLE);
            tipsTv.setText("请您核对订单，并确认。如需修改，请联系客服");
            confirmBtn.setVisibility(View.VISIBLE);
            deleteTv.setVisibility(View.VISIBLE);
        }else  if(orderDetailForDisplay.getOrderstatus().equals("待支付")){
            tipsTv.setVisibility(View.VISIBLE);
            tipsTv.setText("请您核对订单，并确认。如需修改，请联系客服");
            confirmBtn.setVisibility(View.VISIBLE);
            deleteTv.setVisibility(View.VISIBLE);
        }else if(orderDetailForDisplay.getOrderstatus().equals("已取消")){
            tipsTv.setVisibility(View.VISIBLE);
            tipsTv.setText("您的订单已经取消");
            confirmBtn.setVisibility(View.GONE);
            deleteTv.setVisibility(View.GONE);
        }else if(orderDetailForDisplay.getOrderstatus().equals("待评价")){
            tipsTv.setVisibility(View.VISIBLE);
            tipsTv.setText("请对服务进行评价");
            confirmBtn.setVisibility(View.VISIBLE);
            deleteTv.setVisibility(View.GONE);
        }else if(orderDetailForDisplay.getOrderstatus().equals("已完成")){
            tipsTv.setVisibility(View.VISIBLE);
            tipsTv.setText("该订单已经完成");
            confirmBtn.setVisibility(View.GONE);
            deleteTv.setVisibility(View.GONE);
        }else  if(orderDetailForDisplay.getOrderstatus().equals("已确认")){
            tipsTv.setVisibility(View.VISIBLE);
            tipsTv.setText("订单已经确认");
            confirmBtn.setVisibility(View.GONE);
            deleteTv.setVisibility(View.VISIBLE);
        }else{
            tipsTv.setVisibility(View.GONE);
            confirmBtn.setVisibility(View.GONE);
            deleteTv.setVisibility(View.GONE);
        }


        if( orderDetailForDisplay.getDoublebreakfeast() == 1){
            breakfastTcv.valueCbx.setChecked(true);

        }else{
            breakfastTcv.valueCbx.setChecked(false);
        }
        breakfastTcv.valueCbx.setEnabled(false);
        if(orderDetailForDisplay.getNosmoking() == 1){
            noSmokeTcv.valueCbx.setChecked(true);
        }else{
            noSmokeTcv.valueCbx.setChecked(false);
        }
        noSmokeTcv.valueCbx.setEnabled(false);
        //初始化图片
        String logoUrl = ProtocolUtil.getHostImgUrl(orderDetailForDisplay.getImgurl());
        roomIv.setImageURI(Uri.parse(logoUrl));

        //房型
        if(!TextUtils.isEmpty(orderDetailForDisplay.getRoomtype())){
            roomTypeTsv.setValue(orderDetailForDisplay.getRoomtype());
        }
        //房间数量
        roomNumTnv.setValue(orderDetailForDisplay.getRoomcount()+"");
        //初始化时间
        setOrderDate();
        //联系人
        if(!TextUtils.isEmpty(orderDetailForDisplay.getOrderedby())){
            contactTsv.setValue(orderDetailForDisplay.getOrderedby());
        }else{
            contactTsv.setValue(" ");
        }
        //联系人号码
        if(!TextUtils.isEmpty(orderDetailForDisplay.getTelephone())){
            phoneTsv.setValue(orderDetailForDisplay.getTelephone());
        }else{
            phoneTsv.setValue(" ");
        }
        //支付方式
        initPay();
        //发票
        if(!TextUtils.isEmpty(orderDetailForDisplay.getCompany())){
            invoiceTsv.setValue(orderDetailForDisplay.getCompany());
        }else{
            invoiceTsv.setValue(" ");
        }

        //特权
        if(!TextUtils.isEmpty(orderDetailForDisplay.getPrivilegeName())){
            privilegeTsv.setValue(orderDetailForDisplay.getPrivilegeName());
        }else{
            privilegeTsv.setValue("暂无");
        }

        //备注
        if(!TextUtils.isEmpty(orderDetailForDisplay.getRemark())){
            remarkTv.setText(orderDetailForDisplay.getRemark());
        }else{
            remarkTv.setText("");
        }

    }

    private void initPay(){
        payType = orderDetailForDisplay.getPaytype();
        String orderStatus = orderDetailForDisplay.getOrderstatus();
        boolean gotoEvaluate = false;
        if(orderStatus.equals("待评价")){
            gotoEvaluate = true;
        }

        String payTypeStr = "";
        String priceStr ="";
        switch (payType){
            //未设置
            case 0:
                payTypeStr = "未设置";
                confirmBtn.setVisibility(View.GONE);
                break;
            //在线支付
            case 1:
                payTypeStr = "在线支付";
                if(orderDetailForDisplay.getRoomprice() != null){
                    priceStr ="¥"+orderDetailForDisplay.getRoomprice();
                }
                if(orderDetailForDisplay.getOrderstatus().equals("已确认")){
                    payTypeStr = "已支付";
                }
                confirmBtn.setText(priceStr + "  立即支付");
                if(!gotoEvaluate){
                    confirmBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(HotelConfirmActivity.this,OrderPayActivity.class);
                            intent.putExtra("orderDetailForDisplay",orderDetailForDisplay);
                            startActivityForResult(intent,PAY_REQUEST_CODE);
                        }
                    });
                }


                break;
            //到店支付
            case 2:
                payTypeStr = "到店支付";
                if(orderDetailForDisplay.getRoomprice() != null){
                    priceStr ="¥"+orderDetailForDisplay.getRoomprice();
                }
                confirmBtn.setText(priceStr + "  确定");
                if(!gotoEvaluate){
                    confirmBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            confirmOrder();
                        }
                    });
                }

                break;
            //挂账
            case 3:
                payTypeStr = "挂账";
                if(orderDetailForDisplay.getRoomprice() != null){
                    priceStr ="¥"+orderDetailForDisplay.getRoomprice();
                }
                confirmBtn.setText(priceStr + "  确定");
                if(!gotoEvaluate){
                    confirmBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            confirmOrder();
                        }
                    });
                }

                break;
        }
        if(orderDetailForDisplay.getRoomprice() != null){
            payTypeTsv.setValue("¥"+orderDetailForDisplay.getRoomprice()+ "  "+payTypeStr);
        }else{
            payTypeTsv.setValue(payTypeStr);
        }

        if(gotoEvaluate){
            confirmBtn.setText("添加评论");
            confirmBtn.setVisibility(View.VISIBLE);
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HotelConfirmActivity.this,OrderEvaluateActivity.class);
                    intent.putExtra("orderDetailForDisplay",orderDetailForDisplay);
                    startActivity(intent);
                }
            });
        }

    }

    private void initListener() {
        //返回
        findViewById(R.id.header_back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HotelConfirmActivity.this,ConsumeRecordActivtiy.class);
                startActivity(intent);
                BaseApplication.getInst().clearLeaveTop();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        deleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orderDetailForDisplay != null){
                    showCancelDialog();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if(requestCode == PAY_REQUEST_CODE){
                loadOrderInfoByOrderNo();
            }
        }
    }

    //设置离开和到达的日期
    private void setOrderDate() {
        try {
        SimpleDateFormat sdf1 = new SimpleDateFormat("MM月dd日");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date arrivalDate = sdf2.parse(orderDetailForDisplay.getArrivaldate());
        Date leaveDate = sdf2.parse(orderDetailForDisplay.getLeavedate());

        String arriveStr = sdf1.format(arrivalDate);
        String leaveStr = sdf1.format(leaveDate);

        int dayNum = TimeUtil.daysBetween(arrivalDate, leaveDate);
        dateTimeIsv.setValue(arriveStr+"-"+leaveStr+","+dayNum+"晚");

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //确认订单
    private void confirmOrder() {
        String url =  ProtocolUtil.orderConfirmUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("orderno",orderDetailForDisplay.getOrderno());
        bizMap.put("status","2");
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.JSON;
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
                        CustomerServicesManager.getInstance().requestServiceListTask(
                                HotelConfirmActivity.this,
                                orderDetailForDisplay.getShopid(),
                                new ExtNetRequestListener(HotelConfirmActivity.this) {
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
                                                            customerService = CustomerServicesManager.getInstance().getExclusiveCustomerService(customerServiceList, salesId);
                                                        } else {//商家客服
                                                            customerService = CustomerServicesManager.getInstance().getRandomAdminService(customerServiceList);
                                                            if(null != customerService){
                                                                salesId = customerService.getSalesid();
                                                            }
                                                        }
                                                    }
                                                    Intent intent = new Intent(HotelConfirmActivity.this, ChatActivity.class);
                                                    intent.putExtra(Constants.EXTRA_USER_ID, salesId);
                                                    intent.putExtra(Constants.EXTRA_FROM_NAME, CacheUtil.getInstance().getUserName());
                                                    if(null != customerService){
                                                        String userName = customerService.getName();
                                                        if (!TextUtils.isEmpty(userName)) {
                                                            intent.putExtra(Constants.EXTRA_TO_NAME,userName);
                                                        }
                                                    }
                                                    if (!TextUtils.isEmpty(orderDetailForDisplay.getShopid())) {
                                                        intent.putExtra(Constants.EXTRA_SHOP_ID,orderDetailForDisplay.getShopid());
                                                    }
                                                    String shopName = orderDetailForDisplay.getShopname();
                                                    if (!TextUtils.isEmpty(shopName)) {
                                                        intent.putExtra(Constants.EXTRA_SHOP_NAME,shopName);
                                                    }
                                                    intent.putExtra("text_context", "您好，我已确认该订单，请跟进。");
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.slide_in_right,
                                                            R.anim.slide_out_left);
                                                    finish();
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void beforeNetworkRequestStart() {

                                    }
                                });
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

    //取消订单
    private void cancelOrder() {
        String url =  ProtocolUtil.orderCancelUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("orderno",orderDetailForDisplay.getOrderno());
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.JSON;
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
                        CustomerServicesManager.getInstance().requestServiceListTask(
                                HotelConfirmActivity.this,
                                orderDetailForDisplay.getShopid(),
                                new ExtNetRequestListener(HotelConfirmActivity.this) {
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
                                                            customerService = CustomerServicesManager.getInstance().getExclusiveCustomerService(customerServiceList, salesId);
                                                        } else {//商家客服
                                                            customerService = CustomerServicesManager.getInstance().getRandomAdminService(customerServiceList);
                                                            if(null != customerService){
                                                                salesId = customerService.getSalesid();
                                                            }
                                                        }
                                                    }
                                                    Intent intent = new Intent(HotelConfirmActivity.this, ChatActivity.class);
                                                    intent.putExtra(Constants.EXTRA_USER_ID, salesId);
                                                    intent.putExtra(Constants.EXTRA_FROM_NAME, CacheUtil.getInstance().getUserName());
                                                    if(null != customerService){
                                                        String userName = customerService.getName();
                                                        if (!TextUtils.isEmpty(userName)) {
                                                            intent.putExtra(Constants.EXTRA_TO_NAME,userName);
                                                        }
                                                    }
                                                    if (!TextUtils.isEmpty(orderDetailForDisplay.getShopid())) {
                                                        intent.putExtra(Constants.EXTRA_SHOP_ID,orderDetailForDisplay.getShopid());
                                                    }
                                                    String shopName = orderDetailForDisplay.getShopname();
                                                    if (!TextUtils.isEmpty(shopName)) {
                                                        intent.putExtra(Constants.EXTRA_SHOP_NAME,shopName);
                                                    }
                                                    intent.putExtra("text_context", "您好，我已取消该订单，请跟进。");
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.slide_in_right,
                                                            R.anim.slide_out_left);
                                                    finish();
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void beforeNetworkRequestStart() {

                                    }
                                });
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

    private void showCancelDialog() {
        final CustomDialog.Builder customerBuilder = new CustomDialog.Builder(this);
        customerBuilder.setTitle("取消订单");
        customerBuilder.setMessage("你确定要取消该订单吗？");
        customerBuilder.setGravity(Gravity.CENTER);
        customerBuilder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        customerBuilder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                cancelOrder();
            }
        });
        customerBuilder.create().show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(HotelConfirmActivity.this,ConsumeRecordActivtiy.class);
        startActivity(intent);
        BaseApplication.getInst().clearLeaveTop();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
