package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.bean.BaseBean;
import com.zkjinshi.svip.bean.BookOrder;
import com.zkjinshi.svip.bean.EvaluateBean;
import com.zkjinshi.svip.bean.HeadBean;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.EvaluateResponse;
import com.zkjinshi.svip.sqlite.ShopDetailDBUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.EvaluateLevel;

import java.util.HashMap;

/**
 * 订单评价页面
 * 开发者：JimmyZhang
 * 日期：2015/11/3
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderEvaluateActivity extends Activity{

    private CheckBox poorCb,commonCb,gratifyCb,greatGratifyCb,highlyRecommendCb;
    private CheckBox poorResultCb,commonResultCb,gratifyResultCb,greatGratifyResultCb,highlyRecommendResultCb;
    private EditText inputEvaluateEtv;
    private RelativeLayout inputEvaluateTipLayout;
    private Animation layoutInBottom, layoutOutTop;
    private Button submitBtn;
    private boolean isFirst = true;
    private ScrollView bodyScrollView;
    private EvaluateLevel evaluateLevel;
    private ImageButton backIBtn,moreIBtn;
    private LinearLayout evaluateLayout,compleEvaluateLayout;
    private TextView compleEvaluateTv;
    private BookOrder bookOrder;
    private String reservationNo;
    private ImageView hotelIconIv;
    private TextView hotelNameTv,roomInfoTv,priceTv,checkInDateTv,checkInNamesTv,invoiceInfoTv,remarkInfoTv;
    private DisplayImageOptions options;

    private void initView(){
        poorCb = (CheckBox)findViewById(R.id.order_evaluate_cb_poor);
        commonCb = (CheckBox)findViewById(R.id.order_evaluate_cb_common);
        gratifyCb = (CheckBox)findViewById(R.id.order_evaluate_cb_gratify);
        greatGratifyCb = (CheckBox)findViewById(R.id.order_evaluate_cb_great_gratify);
        highlyRecommendCb = (CheckBox)findViewById(R.id.order_evaluate_cb_highly_recommend);
        inputEvaluateEtv = (EditText)findViewById(R.id.order_evaluate_etv_content);
        inputEvaluateTipLayout = (RelativeLayout)findViewById(R.id.order_evaluate_layout_tips);
        submitBtn = (Button)findViewById(R.id.order_evaluate_btn_ok);
        bodyScrollView = (ScrollView)findViewById(R.id.order_evaluate_sv_body);
        backIBtn = (ImageButton)findViewById(R.id.order_evaluate_ibtn_back);
        moreIBtn = (ImageButton)findViewById(R.id.order_evaluate_ibtn_more);
        evaluateLayout = (LinearLayout)findViewById(R.id.order_evaluate_layout);
        compleEvaluateLayout = (LinearLayout)findViewById(R.id.order_evaluate_result_layout);
        compleEvaluateTv = (TextView)findViewById(R.id.order_evaluate_result_etv_content);
        poorResultCb = (CheckBox)findViewById(R.id.order_evaluate_result_cb_poor);
        commonResultCb = (CheckBox)findViewById(R.id.order_evaluate_result_cb_common);
        gratifyResultCb = (CheckBox)findViewById(R.id.order_evaluate_result_cb_gratify);
        greatGratifyResultCb = (CheckBox)findViewById(R.id.order_evaluate_result_cb_great_gratify);
        highlyRecommendResultCb = (CheckBox)findViewById(R.id.order_evaluate_result_cb_highly_recommend);
        hotelIconIv = (ImageView)findViewById(R.id.order_evaluate_iv_hotel_photo);
        hotelNameTv = (TextView)findViewById(R.id.order_evaluate_tv_hotel_name);
        roomInfoTv = (TextView)findViewById(R.id.order_evaluate_tv_room_info);
        priceTv = (TextView)findViewById(R.id.order_evaluate_tv_room_total_price);
        checkInDateTv = (TextView)findViewById(R.id.order_evaluate_tv_check_in_date);
        checkInNamesTv = (TextView)findViewById(R.id.order_evaluate_tv_check_in_names);
        invoiceInfoTv = (TextView)findViewById(R.id.order_evaluate_tv_invoice_info);
        remarkInfoTv = (TextView)findViewById(R.id.order_evaluate_tv_remark_info);
    }

    private void initData(){
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        layoutInBottom = AnimationUtils.loadAnimation(this, R.anim.layout_in_bottom);
        layoutOutTop = AnimationUtils.loadAnimation(this, R.anim.layout_out_top);
        if(null != getIntent() && null != getIntent().getSerializableExtra("bookOrder")){
            bookOrder = (BookOrder)getIntent().getSerializableExtra("bookOrder");
            if(null != bookOrder){
                reservationNo = bookOrder.getReservationNO();
                String shopId = bookOrder.getShopID();
                if(null != shopId){
                    String shopName = ShopDetailDBUtil.getInstance().queryShopNameByShopID(shopId);
                    if(!TextUtils.isEmpty(shopName)){
                        hotelNameTv.setText(shopName);
                    }
                    String logo = ShopDetailDBUtil.getInstance().queryShopLogoByShopID(shopId);
                    if(!TextUtils.isEmpty(logo)){
                        String logoUrl = ProtocolUtil.getShopLogoUrl(logo);
                        ImageLoader.getInstance().displayImage(logoUrl, hotelIconIv, options);
                    }
                    String roomType = bookOrder.getRoomType();
                    String rooms = bookOrder.getRooms();
                    roomInfoTv.setText(roomType + "*" + rooms);
                    String roomRate = bookOrder.getRoomRate();
                    if(!TextUtils.isEmpty(roomRate)){
                        priceTv.setText("¥"+roomRate);
                    }
                    String checkInDate = bookOrder.getArrivalDate();
                    String checkOutDate = bookOrder.getDepartureDate();
                    checkInDateTv.setText(checkInDate + "到" + checkOutDate);
                    String guest = bookOrder.getGuest();
                    if(!TextUtils.isEmpty(guest)){
                        checkInNamesTv.setText(guest);
                    }
                    String remark = bookOrder.getRemark();
                    if(!TextUtils.isEmpty(remark)){
                        remarkInfoTv.setText(remark);
                    }
                }
            }
        }
        requestGetEvaluateTask("559f378c6a8f3",reservationNo);
    }

    private void initListeners(){

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //差
        poorCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evaluateLevel = EvaluateLevel.POOR;
                setEvaluateHint(evaluateLevel);
                setEvaluateStar(evaluateLevel);
                showEvaluateInput();
            }
        });

        //一般
        commonCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evaluateLevel = EvaluateLevel.COMMON;
                setEvaluateHint(evaluateLevel);
                setEvaluateStar(evaluateLevel);
                showEvaluateInput();
            }
        });

        //满意
        gratifyCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evaluateLevel = EvaluateLevel.GRATIFY;
                setEvaluateHint(evaluateLevel);
                setEvaluateStar(evaluateLevel);
                showEvaluateInput();
            }
        });

        //非常满意
        greatGratifyCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evaluateLevel = EvaluateLevel.GREAT_GRATIFY;
                setEvaluateHint(evaluateLevel);
                setEvaluateStar(evaluateLevel);
                showEvaluateInput();
            }
        });

        //强烈推荐
        highlyRecommendCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evaluateLevel = EvaluateLevel.HIGHLY_RECOMMEND;
                setEvaluateHint(evaluateLevel);
                setEvaluateStar(evaluateLevel);
                showEvaluateInput();
            }
        });

        //确认
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String evaluateContent = inputEvaluateEtv.getText().toString();
                requestAddEvaluateTask(evaluateLevel.getVlaue(), evaluateContent, reservationNo);
            }
        });

    }

    /**
     * 设置评价Hint提示内容
     * @param evaluateLevel
     */
    private void setEvaluateHint(EvaluateLevel evaluateLevel){
        if (evaluateLevel.equals(EvaluateLevel.POOR)) {
            inputEvaluateEtv.setHint("感谢您的评价，说说您差评的理由");
        }else if (evaluateLevel.equals(EvaluateLevel.COMMON)) {
            inputEvaluateEtv.setHint("感谢您的评价，说说您一般的理由");
        }else if (evaluateLevel.equals(EvaluateLevel.GRATIFY)) {
            inputEvaluateEtv.setHint("感谢您的评价，说说您满意的理由");
        }else if (evaluateLevel.equals(EvaluateLevel.GREAT_GRATIFY)) {
            inputEvaluateEtv.setHint("感谢您的评价，说说您非常满意的理由");
        }else if (evaluateLevel.equals(EvaluateLevel.HIGHLY_RECOMMEND)) {
            inputEvaluateEtv.setHint("感谢您的评价，说说您强烈推荐的理由");
        }
    }

    private void setEvaluateStar(EvaluateLevel evaluateLevel){
        if (evaluateLevel.equals(EvaluateLevel.POOR)) {
            poorCb.setChecked(true);
            commonCb.setChecked(false);
            gratifyCb.setChecked(false);
            greatGratifyCb.setChecked(false);
            highlyRecommendCb.setChecked(false);
            poorResultCb.setChecked(true);
            commonResultCb.setChecked(false);
            gratifyResultCb.setChecked(false);
            greatGratifyResultCb.setChecked(false);
            highlyRecommendResultCb.setChecked(false);
            poorCb.setTextColor(getResources().getColor(R.color.star_check_color));
            commonCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            gratifyCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            greatGratifyCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            highlyRecommendCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            poorResultCb.setTextColor(getResources().getColor(R.color.star_check_color));
            commonResultCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            gratifyResultCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            greatGratifyResultCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            highlyRecommendResultCb.setTextColor(getResources().getColor(R.color.star_nor_color));
        }else if (evaluateLevel.equals(EvaluateLevel.COMMON)) {
            poorCb.setChecked(true);
            commonCb.setChecked(true);
            gratifyCb.setChecked(false);
            greatGratifyCb.setChecked(false);
            highlyRecommendCb.setChecked(false);
            poorResultCb.setChecked(true);
            commonResultCb.setChecked(true);
            gratifyResultCb.setChecked(false);
            greatGratifyResultCb.setChecked(false);
            highlyRecommendResultCb.setChecked(false);
            poorCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            commonCb.setTextColor(getResources().getColor(R.color.star_check_color));
            gratifyCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            greatGratifyCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            highlyRecommendCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            poorResultCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            commonResultCb.setTextColor(getResources().getColor(R.color.star_check_color));
            gratifyResultCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            greatGratifyResultCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            highlyRecommendResultCb.setTextColor(getResources().getColor(R.color.star_nor_color));
        }else if (evaluateLevel.equals(EvaluateLevel.GRATIFY)) {
            poorCb.setChecked(true);
            commonCb.setChecked(true);
            gratifyCb.setChecked(true);
            greatGratifyCb.setChecked(false);
            highlyRecommendCb.setChecked(false);
            poorResultCb.setChecked(true);
            commonResultCb.setChecked(true);
            gratifyResultCb.setChecked(true);
            greatGratifyResultCb.setChecked(false);
            highlyRecommendResultCb.setChecked(false);
            poorCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            commonCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            gratifyCb.setTextColor(getResources().getColor(R.color.star_check_color));
            greatGratifyCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            highlyRecommendCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            poorResultCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            commonResultCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            gratifyResultCb.setTextColor(getResources().getColor(R.color.star_check_color));
            greatGratifyResultCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            highlyRecommendResultCb.setTextColor(getResources().getColor(R.color.star_nor_color));
        }else if (evaluateLevel.equals(EvaluateLevel.GREAT_GRATIFY)) {
            poorCb.setChecked(true);
            commonCb.setChecked(true);
            gratifyCb.setChecked(true);
            greatGratifyCb.setChecked(true);
            highlyRecommendCb.setChecked(false);
            poorResultCb.setChecked(true);
            commonResultCb.setChecked(true);
            gratifyResultCb.setChecked(true);
            greatGratifyResultCb.setChecked(true);
            highlyRecommendResultCb.setChecked(false);
            poorCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            commonCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            gratifyCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            greatGratifyCb.setTextColor(getResources().getColor(R.color.star_check_color));
            highlyRecommendCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            poorResultCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            commonResultCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            gratifyResultCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            greatGratifyResultCb.setTextColor(getResources().getColor(R.color.star_check_color));
            highlyRecommendResultCb.setTextColor(getResources().getColor(R.color.star_nor_color));
        }else if (evaluateLevel.equals(EvaluateLevel.HIGHLY_RECOMMEND)) {
            poorCb.setChecked(true);
            commonCb.setChecked(true);
            gratifyCb.setChecked(true);
            greatGratifyCb.setChecked(true);
            highlyRecommendCb.setChecked(true);
            poorResultCb.setChecked(true);
            commonResultCb.setChecked(true);
            gratifyResultCb.setChecked(true);
            greatGratifyResultCb.setChecked(true);
            highlyRecommendResultCb.setChecked(true);
            poorCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            commonCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            gratifyCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            greatGratifyCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            highlyRecommendCb.setTextColor(getResources().getColor(R.color.star_check_color));
            poorResultCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            commonResultCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            gratifyResultCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            greatGratifyResultCb.setTextColor(getResources().getColor(R.color.star_nor_color));
            highlyRecommendResultCb.setTextColor(getResources().getColor(R.color.star_check_color));
        }
    }

    /**
     * 显示评价内容
     *
     */
    private void showEvaluateInput(){
        if(isFirst){
            inputEvaluateTipLayout.startAnimation(layoutOutTop);
            inputEvaluateEtv.startAnimation(layoutInBottom);
            layoutInBottom.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    inputEvaluateEtv.setVisibility(View.VISIBLE);
                    submitBtn.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    bodyScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            isFirst = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_evaluate);
        initView();
        initData();
        initListeners();
    }

    private void requestGetEvaluateTask(String salesId, String reservationNo){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getGetEvaluateUrl());
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("token",CacheUtil.getInstance().getToken());
        bizMap.put("salesid", salesId);
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
                if (null != result && !TextUtils.isEmpty(result.rawResult)) {
                    Log.i(TAG, "result.rawResult:" + result.rawResult);
                    try {
                        EvaluateResponse evaluateResponse = new Gson().fromJson(result.rawResult,EvaluateResponse.class);
                        if(null != evaluateResponse){
                            HeadBean headBean = evaluateResponse.getHead();
                            if(null != headBean){
                                boolean isSet = headBean.isSet();
                                if(isSet){
                                    String errorInfo = headBean.getErr();
                                    if(!TextUtils.isEmpty(errorInfo) && "404".equals(errorInfo)){//还未评价
                                        evaluateLayout.setVisibility(View.VISIBLE);
                                        compleEvaluateLayout.setVisibility(View.GONE);
                                    }else{
                                        EvaluateBean evaluateBean = evaluateResponse.getData();
                                        if(null != evaluateBean){
                                            evaluateLayout.setVisibility(View.GONE);
                                            compleEvaluateLayout.setVisibility(View.VISIBLE);
                                            //设置评价内容和几颗星
                                            String content = evaluateBean.getContent();
                                            if(!TextUtils.isEmpty(content)){
                                                compleEvaluateTv.setText(content);
                                            }
                                            int score = evaluateBean.getScore();
                                            if(1 == score){
                                                evaluateLevel = EvaluateLevel.POOR;
                                            }else if(2 == score){
                                                evaluateLevel = EvaluateLevel.COMMON;
                                            }else if(3 == score){
                                                evaluateLevel = EvaluateLevel.GRATIFY;
                                            }else if(4 == score){
                                                evaluateLevel = EvaluateLevel.GREAT_GRATIFY;
                                            }else {
                                                evaluateLevel = EvaluateLevel.HIGHLY_RECOMMEND;
                                            }
                                            setEvaluateStar(evaluateLevel);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    /**
     *
     * @param score 评分 12345
     * @param content 评论的的内容
     * @param reservationNo 订单号
     */
    private void requestAddEvaluateTask(int score, final String content, String reservationNo){

        NetRequest netRequest = new NetRequest(ProtocolUtil.getAddEvaluateUrl());
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("userid",CacheUtil.getInstance().getUserId());
        bizMap.put("token",CacheUtil.getInstance().getToken());
        bizMap.put("score", score+"");
        bizMap.put("content", content);
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
                if (null != result && !TextUtils.isEmpty(result.rawResult)) {
                    Log.i(TAG, "result.rawResult:" + result.rawResult);
                    try {
                        BaseBean baseBean = new Gson().fromJson(result.rawResult, BaseBean.class);
                        if (null != baseBean) {
                            boolean isSet = baseBean.isSet();
                            if (isSet) {
                                evaluateLayout.setVisibility(View.GONE);
                                compleEvaluateLayout.setVisibility(View.VISIBLE);
                                if (!TextUtils.isEmpty(content)) {
                                    compleEvaluateTv.setText(content);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
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
