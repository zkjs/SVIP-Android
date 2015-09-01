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
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.NetWorkUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.im.ChatActivity;
import com.zkjinshi.svip.activity.mine.MineNetController;
import com.zkjinshi.svip.activity.mine.MineUiController;
import com.zkjinshi.svip.bean.BookOrder;
import com.zkjinshi.svip.factory.GoodInfoFactory;
import com.zkjinshi.svip.factory.UserInfoFactory;
import com.zkjinshi.svip.fragment.MenuLeftFragment;
import com.zkjinshi.svip.response.GoodInfoResponse;
import com.zkjinshi.svip.response.UserInfoResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.StringUtil;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.view.ItemUserSettingView;
import com.zkjinshi.svip.vo.GoodInfoVo;
import com.zkjinshi.svip.vo.OrderInfoVo;
import com.zkjinshi.svip.vo.TicketVo;
import com.zkjinshi.svip.vo.UserInfoVo;

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


    private SimpleDateFormat mSimpleFormat;
    private SimpleDateFormat mChineseFormat;
    private ArrayList<Calendar> calendarList = null;

    private int dayNum;
    private int roomNum;

    private String orderId;
    private String reservationNo;
    private String shopId;
    private GoodInfoVo lastGoodInfoVo;
    private TicketVo tickeVo;
    private OrderInfoVo orderInfoVo;

    private List<GoodInfoResponse> goodResponsseList;
    private List<GoodInfoVo> goodInfoList;
    private DisplayImageOptions options;



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

        reservationNo = getIntent().getStringExtra("reservation_no");
        if(StringUtil.isEmpty(reservationNo)){
            orderInfoVo = (OrderInfoVo)getIntent().getSerializableExtra("orderInfoVo");
            orderId = orderInfoVo.getId();
            shopId = orderInfoVo.getShopid();
            initData();
        }else{
            loadOrderInfoByReservationNo();
        }

        initListener();
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

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtil.getInstance().cancelProgressDialog();
                LogUtil.getInstance().info(LogLevel.INFO, "获取单个订单错误信息:" +  error.getMessage());
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("userid", CacheUtil.getInstance().getUserId());
                map.put("token", CacheUtil.getInstance().getToken());
                map.put("reservation_no", reservationNo);
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
        GoodListNetController.getInstance().init(this);
        GoodListUiController.getInstance().init(this);

        mBtnSendOrder.setText("保存订单");
        //mBtnCancelOrder.setVisibility(View.GONE);

        calendarList = new ArrayList<Calendar>();
       // mTitle.setTextTitle("订单详情");
        mTitle.setTextTitle(orderInfoVo.getFullname());
        mTitle.setTextColor(this, R.color.White);
        mTitle.getmRight().setVisibility(View.GONE);



        mSimpleFormat  = new SimpleDateFormat("yyyy-MM-dd");
        mChineseFormat = new SimpleDateFormat("MM月dd日");

        try{
            Calendar arrivalDate = Calendar.getInstance();
            arrivalDate.setTime(mSimpleFormat.parse(orderInfoVo.getArrival_date()));
            calendarList.add(arrivalDate);

            Calendar departureDate = Calendar.getInstance();
            departureDate.setTime(mSimpleFormat.parse(orderInfoVo.getDeparture_date()));
            calendarList.add(departureDate);
            setOrderDate(calendarList);
        }catch ( Exception e){

        }


        roomNum = 2;
        notifyRoomNumberChange();
        Drawable drawable= getResources().getDrawable(R.mipmap.ic_get_into_w);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mIusvRoomNumber.getmTextContent2().setCompoundDrawables(null, null, drawable, null);

        for(int i=0;i<customerList.size();i++){
            Drawable d= getResources().getDrawable(R.mipmap.ic_get_into_w);
            d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
            customerList.get(i).getmTextContent2().setCompoundDrawables(null, null, d, null);
        }


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
                                        for( GoodInfoVo goodInfoVo : goodInfoList){
                                            if(goodInfoVo.getRoom().equals(orderInfoVo.getRoom_type())){
                                                lastGoodInfoVo = goodInfoVo;
                                                setOrderRoomType(goodInfoVo);
                                            }
                                        }
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

        initRoomTags();
        initServiceTags();
        initTicket();
        initRemark();
        initOrderStatus();
    }

    //初始化订单状态的显示
    private void initOrderStatus() {
        if(orderInfoVo.getStatus().equals("0")){
            mTvOrderStatus.setText("已提交/未确认");
        }
        else if(orderInfoVo.getStatus().equals("1")){
            mTvOrderStatus.setText("已取消");
        }
        else if(orderInfoVo.getStatus().equals("2")){
            mTvOrderStatus.setText("已确认/未支付");
        }
        else if(orderInfoVo.getStatus().equals("3")){
            mTvOrderStatus.setText("已完成订单");
        }
        else if(orderInfoVo.getStatus().equals("4")){
            mTvOrderStatus.setText("已入住");
        }
        else if(orderInfoVo.getStatus().equals("5")){
            mTvOrderStatus.setText("已删除");
        }
    }

    //初始化订单备注
    private void initRemark() {
        if(!StringUtil.isEmpty(orderInfoVo.getRemark())){
            mTvRemark.setText(orderInfoVo.getRemark());
        }
    }

    //初始化发票
    private void initTicket() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ProtocolUtil.geTicketListUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mTvTicket.setText("");
                        DialogUtil.getInstance().cancelProgressDialog();
                        LogUtil.getInstance().info(LogLevel.INFO, "默认发票响应结果:" + response);
                        if(!TextUtils.isEmpty(response)){
                            try {
                                Gson gson = new Gson();
                                tickeVo = gson.fromJson(response, TicketVo.class);
                                if(null != tickeVo){
                                    mTvTicket.setText(tickeVo.getInvoice_title());
                                }else{
                                    mTvTicket.setText("");
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
                mTvTicket.setText("");
                LogUtil.getInstance().info(LogLevel.INFO, "获取默认发票错误信息:" +  error.getMessage());
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("userid", CacheUtil.getInstance().getUserId());
                map.put("token", CacheUtil.getInstance().getToken());
                map.put("set","1");
                return map;
            }
        };
        if(NetWorkUtil.isNetworkConnected(this)){
            GoodListNetController.getInstance().requestGetGoodListTask(stringRequest);
        }
    }

    //初始化房间选项标签
    private void initRoomTags() {
        mRoomTagView.addTag(createTag(1,"无烟房",true));
        mRoomTagView.addTag(createTag(2,"视野好",false));
        mRoomTagView.addTag(createTag(3,"加床",false));
        mRoomTagView.addTag(createTag(4,"安静",false));
        mRoomTagView.addTag(createTag(5,"离电梯近",false));
       // mRoomTagView.addTag(createTag(0,"添加更多",false));
    }

    //初始化其他服务标签
    private void initServiceTags() {
        mServiceTagView.addTag(createTag(1,"免前台",true));
        mServiceTagView.addTag(createTag(2,"接机服务",false));
      //  mServiceTagView.addTag(createTag(0,"添加更多",false));
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

            }
        });
        //取消订单
        mBtnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                Intent intent = new Intent(OrderDetailActivity.this, ChooseTicketActivity.class);
                startActivityForResult(intent, TICKET_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

        mlltRemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderDetailActivity.this, AddRemarkActivity.class);
                intent.putExtra("remark",mTvRemark.getText());
                intent.putExtra("tips","如果有其他要求，请在此说明。");
                intent.putExtra("title", "添加订单备注");
                intent.putExtra("hint", "请输入订单备注");
                startActivityForResult(intent, REMARK_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

        for(int i=0;i<roomNum;i++){
            final int index = i;
            customerList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ItemUserSettingView setView = (ItemUserSettingView)view;
                    Intent intent = new Intent(OrderDetailActivity.this, AddPeopleActivity.class);
                    intent.putExtra("name", setView.getTextContent2());
                    intent.putExtra("index",index);
                    intent.putExtra("title", "设置"+setView.getmTextTitle().getText().toString());
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
                    String name = data.getStringExtra("name");
                    int index = data.getIntExtra("index", 0);
                    customerList.get(index).setTextContent2(name);
                }
            }
            else if(TICKET_REQUEST_CODE == requestCode){
                if(null != data){
                    tickeVo = (TicketVo)data.getSerializableExtra("selectTicketVo");
                    mTvTicket.setText(tickeVo.getInvoice_title());
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
        String imageUrl = goodInfoVo.getImage();
        if(!TextUtils.isEmpty(imageUrl)){
            String logoUrl = ProtocolUtil.getGoodImgUrl(imageUrl);
            ImageLoader.getInstance().displayImage(logoUrl,mIvRoomImg,options);
        }
        mRoomType.setText(goodInfoVo.getRoom());
    }

}
