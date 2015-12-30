package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.AddOrderResponse;
import com.zkjinshi.svip.response.OrderInvoiceResponse;
import com.zkjinshi.svip.sqlite.ShopDetailDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.ItemNumView;
import com.zkjinshi.svip.view.ItemShowView;
import com.zkjinshi.svip.view.datepicker.DatePickerPopWindow;
import com.zkjinshi.svip.vo.GoodInfoVo;
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
public class NormalBookingActivity extends Activity {

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

    public static final int PEOPLE_REQUEST_CODE = 7;
    public static final int TICKET_REQUEST_CODE = 8;
    public static final int REMARK_REQUEST_CODE = 9;
    public static final int PHONE_REQUEST_CODE = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        ImageLoader.getInstance().displayImage(shopImg,roomIv,options);
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
                submitBooking("2");
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
                Date date= orderDetailForDisplay.getArrivaldate();
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
                Intent intent = new Intent(NormalBookingActivity.this, ChooseInvoiceActivity.class);
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
        orderDetailForDisplay.setArrivaldate(arriveDate);
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
        String arriveStr = sdf.format(arriveDate);
        dateTimeIsv.setValue(arriveStr);
    }

    public void submitBooking(String category){
        if(TextUtils.isEmpty(orderDetailForDisplay.getRoomtype())){
            DialogUtil.getInstance().showToast(this,"房型不能为空！");
            return;
        }
        int num = Integer.parseInt(peopleNumTnv.getValue());
        orderDetailForDisplay.setPersoncount(num);
        String dataJson = new Gson().toJson(orderDetailForDisplay);
        String url = ProtocolUtil.orderAddUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("category", category);
        bizMap.put("data", dataJson);
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
                        DialogUtil.getInstance().showToast(NormalBookingActivity.this,"订单添加成功。");
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
