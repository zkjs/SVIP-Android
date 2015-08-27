package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.NetWorkUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.GoodAdapter;
import com.zkjinshi.svip.bean.BookOrder;
import com.zkjinshi.svip.factory.GoodInfoFactory;
import com.zkjinshi.svip.response.GoodInfoResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.GoodInfoVo;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 商品列表Activity
 * 开发者：JimmyZhang
 * 日期：2015/7/27
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GoodListActivity extends Activity {

    private ImageButton backIBtn;
    private TextView centerTitleTv;
    private ListView roomListView;
   // private boolean scrollFlag;
    private StringRequest stringRequest;
    private List<GoodInfoResponse> goodResponsseList;
    private List<GoodInfoVo> goodInfoList;
    private String shopId;
    private GoodAdapter goodAdapter;
   // private LinearLayout chooseAndPayLaout;
    //private RadioGroup levelRG,breakfastRG;
   // private Button bookCommitBtn;
    //private BookOrder bookOrder;

    private GoodInfoVo goodInfoVo;

    private void initView(){
        backIBtn = (ImageButton)findViewById(R.id.good_list_header_bar_btn_back);
        centerTitleTv = (TextView)findViewById(R.id.good_list_header_bar_tv_title);
        roomListView = (ListView)findViewById(R.id.good_list_list_view);
       // chooseAndPayLaout = (LinearLayout)findViewById(R.id.view_choose_and_pay_layout);
       // levelRG = (RadioGroup)findViewById(R.id.good_list_rg_level);
        //breakfastRG = (RadioGroup)findViewById(R.id.good_list_rg_breakfast);
       // bookCommitBtn = (Button)findViewById(R.id.room_list_book_right_now_btn);
    }

    private void initData(){

        GoodListNetController.getInstance().init(this);
        GoodListUiController.getInstance().init(this);
        //bookCommitBtn.setTag(bookOrder);

       // shopId = getIntent().getStringExtra("shopid");
        shopId = goodInfoVo.getShopid();

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
                                        setResponseData(goodInfoList);
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

    private void initListeners(){

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);
            }
        });

//        //房间档次
//        levelRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//
//            }
//        });

//        //早餐类型
//        breakfastRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//
//            }
//        });

//        //立即预定
//        bookCommitBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                bookOrder = (BookOrder)view.getTag();
//                if(null != bookOrder){
//                    Intent intent = new Intent(GoodListActivity.this,OrderConfirmActivity.class);
//                    intent.putExtra("book_order", bookOrder);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.slide_in_right,
//                            R.anim.slide_out_left);
//                }else{
//                    DialogUtil.getInstance().showCustomToast(view.getContext(), "您还未选择房间!", Toast.LENGTH_LONG);
//                }
//            }
//        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_list);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
           goodInfoVo = (GoodInfoVo) bundle.getSerializable("GoodInfoVo");
            initView();
            initData();
            initListeners();
        }


    }

    private void setResponseData(List<GoodInfoVo> goodInfoList){

        goodAdapter = new GoodAdapter(goodInfoList,GoodListActivity.this);
        roomListView.setAdapter(goodAdapter);
        goodAdapter.selectGood(goodInfoVo.getId());
        //房型列表点击监听
        roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(null != goodAdapter){
                    GoodInfoVo goodInfoVo = (GoodInfoVo)goodAdapter.getItem(position);
                    String goodId = goodInfoVo.getId();
                    if(!TextUtils.isEmpty(goodId)){
                        goodAdapter.selectGood(goodId);
                        //跳转回选择页面
                        Intent inetnt = new Intent();
                        inetnt.putExtra("GoodInfoVo", goodInfoVo);
                        setResult(RESULT_OK, inetnt);
                        finish();
                    }


//                    bookOrder = new BookOrder();
//                    bookOrder.setRoomTypeID(goodId);
//                    bookOrder.setCompany(goodInfoVo.getFullname());
//                    bookOrder.setShopID(goodInfoVo.getShopid());
//                    bookOrder.setUserID(CacheUtil.getInstance().getUserId());
//                    bookOrder.setRoomType(goodInfoVo.getRoom());
//                    bookOrder.setRoomRate(goodInfoVo.getPrice());
//                    bookOrder.setImage(goodInfoVo.getImage());
//                    bookCommitBtn.setTag(bookOrder);
                }
            }
        });

        //房型列表滑动监听
//        roomListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                switch (scrollState) {
//                    // 当不滚动时
//                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 是当屏幕停止滚动时
//                        scrollFlag = false;
//                        LogUtil.getInstance().info(LogLevel.INFO,"滚动停止");
//                        // 判断滚动到底部
//                        if (roomListView.getLastVisiblePosition() == (roomListView
//                                .getCount() - 1)) {
//                            LogUtil.getInstance().info(LogLevel.INFO,"滚动到底部");
//                        }
//                        // 判断滚动到顶部
//                        if (roomListView.getFirstVisiblePosition() == 0) {
//                            LogUtil.getInstance().info(LogLevel.INFO,"滚动到顶部");
//                        }
//
//                        break;
//                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 滚动时
//                        scrollFlag = true;
//                        break;
//                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:// 是当用户由于之前划动屏幕并抬起手指，屏幕产生惯性滑动时
//                        scrollFlag = true;
//                        LogUtil.getInstance().info(LogLevel.INFO,"是当用户由于之前划动屏幕并抬起手指，屏幕产生惯性滑动");
//                        break;
//                }
//                if(scrollFlag){
//                    GoodListUiController.getInstance().hiddleChooseAndPayLaout(chooseAndPayLaout);
//                }else {
//                    GoodListUiController.getInstance().showChooseAndPayLaout(chooseAndPayLaout);
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int firstVisibleItem, int lastVisibleItemPosition, int totalItemCount) {
//                if (firstVisibleItem > lastVisibleItemPosition) {// 上滑
//                    LogUtil.getInstance().info(LogLevel.INFO, "上滑");
//                } else{// 下滑
//                    LogUtil.getInstance().info(LogLevel.INFO, "下滑");
//                }
//            }
//        });
    }
}
