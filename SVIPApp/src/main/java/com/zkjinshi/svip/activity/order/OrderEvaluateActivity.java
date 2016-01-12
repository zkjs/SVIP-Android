package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
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
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.base.BaseApplication;
import com.zkjinshi.svip.bean.BaseBean;
import com.zkjinshi.svip.bean.EvaluateBean;
import com.zkjinshi.svip.bean.HeadBean;
import com.zkjinshi.svip.bean.OrderDetailBean;
import com.zkjinshi.svip.bean.UserBean;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.EvaluateOrderResponse;
import com.zkjinshi.svip.response.EvaluateResponse;
import com.zkjinshi.svip.response.OrderConsumeResponse;
import com.zkjinshi.svip.response.OrderDetailResponse;
import com.zkjinshi.svip.response.OrderEvaluateResponse;
import com.zkjinshi.svip.sqlite.ShopDetailDBUtil;
import com.zkjinshi.svip.utils.Base64Encoder;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.CircleImageView;
import com.zkjinshi.svip.vo.EvaluateLevel;
import com.zkjinshi.svip.vo.OrderDetailForDisplay;
import com.zkjinshi.svip.vo.OrderEvaluationVo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 订单评价页面
 * 开发者：JimmyZhang
 * 日期：2015/11/3
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class OrderEvaluateActivity extends BaseActivity {

    private final static String TAG = OrderEvaluateActivity.class.getSimpleName();

    private CheckBox poorCb,commonCb,gratifyCb,greatGratifyCb,highlyRecommendCb;
    private CheckBox poorResultCb,commonResultCb,gratifyResultCb,greatGratifyResultCb,highlyRecommendResultCb;
    private EditText inputEvaluateEtv;
    private Animation layoutInBottom;
    private Button submitBtn;
    private boolean isFirst = true;
    private ScrollView bodyScrollView;
    private EvaluateLevel evaluateLevel;
    private ImageButton backIBtn;
    private LinearLayout evaluateLayout,compleEvaluateLayout;
    private TextView compleEvaluateTv;
    private OrderDetailForDisplay orderDetailForDisplay;
    private String reservationNo;
    private OrderEvaluationVo orderEvaluationVo;

    private void initView(){
        poorCb = (CheckBox)findViewById(R.id.order_evaluate_cb_poor);
        commonCb = (CheckBox)findViewById(R.id.order_evaluate_cb_common);
        gratifyCb = (CheckBox)findViewById(R.id.order_evaluate_cb_gratify);
        greatGratifyCb = (CheckBox)findViewById(R.id.order_evaluate_cb_great_gratify);
        highlyRecommendCb = (CheckBox)findViewById(R.id.order_evaluate_cb_highly_recommend);
        inputEvaluateEtv = (EditText)findViewById(R.id.order_evaluate_etv_content);
        submitBtn = (Button)findViewById(R.id.order_evaluate_btn_ok);
        bodyScrollView = (ScrollView)findViewById(R.id.order_evaluate_sv_body);
        backIBtn = (ImageButton)findViewById(R.id.header_back_iv);
        evaluateLayout = (LinearLayout)findViewById(R.id.order_evaluate_layout);
        compleEvaluateLayout = (LinearLayout)findViewById(R.id.order_evaluate_result_layout);
        compleEvaluateTv = (TextView)findViewById(R.id.order_evaluate_result_etv_content);
        poorResultCb = (CheckBox)findViewById(R.id.order_evaluate_result_cb_poor);
        commonResultCb = (CheckBox)findViewById(R.id.order_evaluate_result_cb_common);
        gratifyResultCb = (CheckBox)findViewById(R.id.order_evaluate_result_cb_gratify);
        greatGratifyResultCb = (CheckBox)findViewById(R.id.order_evaluate_result_cb_great_gratify);
        highlyRecommendResultCb = (CheckBox)findViewById(R.id.order_evaluate_result_cb_highly_recommend);
    }

    private void initData(){
        layoutInBottom = AnimationUtils.loadAnimation(this, R.anim.layout_in_bottom);
        if(null != getIntent() && null != getIntent().getSerializableExtra("orderDetailForDisplay")){
            orderDetailForDisplay = (OrderDetailForDisplay) getIntent().getSerializableExtra("orderDetailForDisplay");
            if(null != orderDetailForDisplay){
                reservationNo = orderDetailForDisplay.getOrderno();
            }
        }
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
                if(TextUtils.isEmpty(evaluateContent)){
                    DialogUtil.getInstance().showCustomToast(OrderEvaluateActivity.this,"评价内容不能为空", Gravity.CENTER);
                    return;
                }
                if(!TextUtils.isEmpty(evaluateContent) && evaluateContent.length() < 10){
                    DialogUtil.getInstance().showCustomToast(OrderEvaluateActivity.this,"评价内容不能少于10个字", Gravity.CENTER);
                    return;
                }
                if(!TextUtils.isEmpty(evaluateContent)){
                    requestAddEvaluateTask(evaluateLevel.getVlaue(), evaluateContent, reservationNo);
                }

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

    /**
     * 新增订单评价
     * @param score 评分 12345
     * @param content 评论的的内容
     * @param reservationNo 订单号
     */
    private void requestAddEvaluateTask(int score, final String content, String reservationNo){

        orderEvaluationVo = new OrderEvaluationVo();
        orderEvaluationVo.setContent(content);
        orderEvaluationVo.setOrderNo(reservationNo);
        orderEvaluationVo.setScore(score);
        orderEvaluationVo.setUserid(CacheUtil.getInstance().getUserId());
        Gson gson = new GsonBuilder().serializeNulls().create();
        String dataJson = gson.toJson(orderEvaluationVo);
        String encryptedData = Base64Encoder.encode(dataJson);
        NetRequest netRequest = new NetRequest(ProtocolUtil.getAddEvaluateUrl());
        HashMap<String,Object> objectMap = new HashMap<String,Object>();
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
                if (null != result && !TextUtils.isEmpty(result.rawResult)) {
                    Log.i(TAG, "result.rawResult:" + result.rawResult);
                    try {
                        EvaluateOrderResponse evaluateOrderResponse = new Gson().fromJson(result.rawResult, EvaluateOrderResponse.class);
                        if (null != evaluateOrderResponse) {
                            boolean isSuccess = evaluateOrderResponse.isResult();
                            if (isSuccess) {
                                evaluateLayout.setVisibility(View.GONE);
                                compleEvaluateLayout.setVisibility(View.VISIBLE);
                                if (!TextUtils.isEmpty(content)) {
                                    compleEvaluateTv.setText(content);
                                }
                                showEvaluateDialog();
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

    private void showEvaluateDialog(){
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
        customBuilder.setTitle("提示");
        customBuilder.setMessage("订单评价成功");
        customBuilder.setGravity(Gravity.CENTER);
        customBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(OrderEvaluateActivity.this,ConsumeRecordActivtiy.class);
                startActivity(intent);
                BaseApplication.getInst().clearLeaveTop();
            }
        });
        customBuilder.create().show();
    }
}
