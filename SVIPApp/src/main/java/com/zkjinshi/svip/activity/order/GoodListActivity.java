package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.GoodAdapter;

import com.zkjinshi.svip.bean.RecommendShopBean;
import com.zkjinshi.svip.bean.ShopBean;
import com.zkjinshi.svip.factory.GoodInfoFactory;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.GoodInfoResponse;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.CircleImageView;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.vo.GoodInfoVo;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品列表Activity
 * 开发者：JimmyZhang
 * 日期：2015/7/27
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GoodListActivity extends Activity {

    private final static String TAG = GoodListActivity.class.getSimpleName();

    private ImageView backIv;
    private ListView roomListView;
    private Button bookingBtn;
    private List<GoodInfoVo> goodInfoList;
    private GoodAdapter goodAdapter;
    private GoodInfoVo goodInfoVo = null;
    private ViewHolder viewHolder = null;
    private View headerView;
    private LinearLayout headerLayout;
    private ShopBean shopBean = null;
    private String shopid;
    private boolean showHeader = false;

    private DisplayImageOptions shopOptions;
    private DisplayImageOptions avatarOptions;


    private void initView(){
        backIv = (ImageView)findViewById(R.id.back_iv);
        headerLayout = (LinearLayout)findViewById(R.id.headerLayout);
        roomListView = (ListView)findViewById(R.id.good_list_list_view);
        bookingBtn = (Button)findViewById(R.id.btn_send_booking_order);

        showHeader = getIntent().getBooleanExtra("showHeader",false);

        if(getIntent().getSerializableExtra("shopBean") != null){
            showHeader = true;
            shopBean = (ShopBean)getIntent().getSerializableExtra("shopBean");
            initHeader();
        }
        if(showHeader && shopBean == null){
            shopid = getIntent().getStringExtra("shopid");
            getShopBaseInfo(shopid);
        }

    }

    //获取商家基本信息
    private void getShopBaseInfo(String shopid){
        String url = ProtocolUtil.getShopBaseInfoUrl(shopid);
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
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
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {

                    shopBean = new Gson().fromJson(result.rawResult, ShopBean.class);
                    if (null != shopBean) {
                        initHeader();
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

    private void initHeader(){
        headerView = LayoutInflater.from(this).inflate(R.layout.item_list_shop, null);
        //headerLayout.addView(headerView);
        roomListView.addHeaderView(headerView);

        viewHolder = new ViewHolder();
        viewHolder.ivShopLogo     = (ImageView)headerView.findViewById(R.id.iv_shop_logo);
        viewHolder.civSalerAvatar = (CircleImageView)headerView.findViewById(R.id.civ_saler_avatar);
        viewHolder.tvShopName     = (TextView) headerView.findViewById(R.id.tv_shop_name);
        viewHolder.tvShopBusiness = (TextView) headerView.findViewById(R.id.tv_shop_business);
        viewHolder.tvShopDes      = (TextView) headerView.findViewById(R.id.tv_shop_des);
        viewHolder.llShopInfo     = (LinearLayout) headerView.findViewById(R.id.ll_shop_info);
        viewHolder.rlSalerInfo    = (RelativeLayout) headerView.findViewById(R.id.rl_saler_info);

        this.shopOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.img_dingdanxiangqing)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.img_dingdanxiangqing)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.img_dingdanxiangqing)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
        this.avatarOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.img_avatar_hotel)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.img_avatar_hotel)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.img_avatar_hotel)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();

        String   shopName = shopBean.getShopname();
        String   shopBusi = shopBean.getShopbusiness();
        String   shopDesc = shopBean.getShopdesc();
        String   salesID  = shopBean.getSalesid();
        String   imgUrl   = shopBean.getBgImgUrl();

        if(!TextUtils.isEmpty(shopName)){
            viewHolder.tvShopName.setText(shopName);
        }

        if(!TextUtils.isEmpty(shopBusi)){
            viewHolder.tvShopBusiness.setText(shopBusi);
        }

        if(!TextUtils.isEmpty(shopDesc)){
            viewHolder.tvShopDes.setText(shopDesc);
        }

        if(!TextUtils.isEmpty(imgUrl)){
            ImageLoader.getInstance().displayImage(imgUrl, viewHolder.ivShopLogo, shopOptions);
        }

        if(!TextUtils.isEmpty(salesID)){
            String avatarUrl = ProtocolUtil.getAvatarUrl(salesID);
            ImageLoader.getInstance().displayImage(avatarUrl,viewHolder.civSalerAvatar, avatarOptions);
        }
    }

    private void initData(){
        if(getIntent().getSerializableExtra("GoodInfoVo") != null){
            goodInfoVo = (GoodInfoVo)getIntent().getSerializableExtra("GoodInfoVo");
        }
        shopid = getIntent().getStringExtra("shopid");
        bookingBtn.setVisibility(View.GONE);

        TextView tips  = (TextView)findViewById(R.id.empty_tips);
        tips.setText("暂无房型可选");
        roomListView.setEmptyView(findViewById(R.id.empty_linearlayout));
        findViewById(R.id.empty_linearlayout).setVisibility(View.INVISIBLE);
        getShopGoods();

    }

    //获取酒店信息
    private void getShopGoods(){
        String url = ProtocolUtil.getGoodListUrl(shopid);
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
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
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    Type listType = new TypeToken<ArrayList<GoodInfoResponse>>() {
                    }.getType();
                    Gson gson = new Gson();
                    ArrayList<GoodInfoResponse> goodResponsseList = gson.fromJson(result.rawResult, listType);
                    if (null != goodResponsseList && !goodResponsseList.isEmpty()) {
                        goodInfoList = GoodInfoFactory.getInstance().bulidGoodList(goodResponsseList);
                        if (null != goodInfoList && !goodInfoList.isEmpty()) {
                            setResponseData(goodInfoList);
                        }
                    }else{
                        findViewById(R.id.empty_linearlayout).setVisibility(View.VISIBLE);
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

    private void initListeners(){

        //返回
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);
            }
        });

        bookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GoodListActivity.this,OrderBookingActivity.class);
                intent.putExtra("GoodInfoVo", goodInfoVo);
                intent.putExtra("shopid",shopid);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        //房型列表点击监听
        roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(null != goodAdapter){
                    int index = 0;
                    if(showHeader){
                        if(position == 0){
                            return;
                        }else{
                            index = position - 1;
                        }
                    }else{
                        index = position;
                    }

                    goodInfoVo = (GoodInfoVo)goodAdapter.getItem(index);
                    String goodId = goodInfoVo.getId();
                    if(!TextUtils.isEmpty(goodId)){
                        goodAdapter.selectGood(goodId);
                        if(!showHeader){
                            //跳转回预定页面
                            Intent intent = new Intent();
                            intent.putExtra("GoodInfoVo", goodInfoVo);
                            intent.putExtra("shopid",shopid);
                            setResult(RESULT_OK, intent);
                            finish();
                        }else{
                            bookingBtn.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_list);
        initView();
        initData();
        initListeners();
    }

    private void setResponseData(List<GoodInfoVo> goodInfoList){
//        goodAdapter.mDatas = goodInfoList;
//        goodAdapter.notifyDataSetChanged();
        goodAdapter = new GoodAdapter(goodInfoList,GoodListActivity.this);
        roomListView.setAdapter(goodAdapter);
        if(goodInfoVo != null){
            goodAdapter.selectGood(goodInfoVo.getId());
        }
    }

    static class ViewHolder{
        ImageView ivShopLogo;
        CircleImageView civSalerAvatar;
        TextView        tvShopName;
        TextView        tvShopBusiness;
        TextView        tvShopDes;

        LinearLayout    llShopInfo;
        RelativeLayout rlSalerInfo;
    }
}
