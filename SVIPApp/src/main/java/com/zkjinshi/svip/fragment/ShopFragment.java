package com.zkjinshi.svip.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.ShopAdapter;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.ShopDetailResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.MapUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.ShopListView;
import com.zkjinshi.svip.vo.ShopModeVo;
import com.zkjinshi.svip.vo.ShopVo;

import java.util.ArrayList;

/**
 * 商家详情页
 * 开发者：JimmyZhang
 * 日期：2015/7/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopFragment extends Fragment {

    public static final String TAG = ShopFragment.class.getSimpleName();
    private View root;
    public boolean isVisiable = false;
    private SimpleDraweeView shopBgIv;
    private TextView shopNameTv,addressTv,telephoneTv;
    private ShopListView descListView;
    private ScrollView scrollView;
    private ArrayList<ShopModeVo> shopModeList;
    private ShopAdapter shopAdapter;

    private GestureDetector gestureDetector;
    private GestureDetector.OnGestureListener onGestureListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    if(e1 != null && e2 != null){
                        float x = e2.getX() - e1.getX();
                        float y = Math.abs(e2.getY() - e1.getY());
                        if (x > 0 && y< 80) {
                            if(isVisiable){
                                hideAction();
                            }
                        }
                    }
                    return true;
                }
            };

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_shop, container,false);
        initUI(root);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }

    private void initData(){
        requestShopDetailTask();
    }

    private void initUI(final View root) {
        gestureDetector = new GestureDetector(getActivity(),onGestureListener);
        scrollView = (ScrollView)root.findViewById(R.id.shop_scrollview);
        shopBgIv = (SimpleDraweeView)root.findViewById(R.id.shop_detail_tv_bg);
        shopNameTv = (TextView)root.findViewById(R.id.shop_detail_tv_shop_name);
        addressTv = (TextView)root.findViewById(R.id.shop_detail_tv_address);
        telephoneTv = (TextView)root.findViewById(R.id.shop_detail_tv_telephone);
        descListView = (ShopListView)root.findViewById(R.id.shop_desc_list_view);
        shopAdapter = new ShopAdapter(getActivity(),shopModeList);
        descListView.setAdapter(shopAdapter);
        descListView.setFocusable(false);
    }

    public void show(final View view,Bundle bundle){
        String text = bundle.getString("text");
        ViewHelper.setRotationY(view, 0);
        ViewHelper.setRotationY(root, 90);
        root.setVisibility(View.VISIBLE);
        root.setEnabled(true);
        ViewPropertyAnimator.animate(view).rotationY(-90)
                .setDuration(300).setListener(null)
                .setInterpolator(new AccelerateInterpolator());
        ViewPropertyAnimator.animate(root)
                .rotationY(0).setDuration(200).setStartDelay(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewHelper.setRotationY(view, 0);
                        isVisiable = true;
                    }
                });
        root.setClickable(true);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
        descListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

    }

    public void hideAction(){
        root.setClickable(false);
        root.setEnabled(false);
        ViewPropertyAnimator.animate(root)
                .rotationY(90).setDuration(300)
                .setListener(new AnimatorListenerAdapter(){
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        root.clearAnimation();
                        root.setVisibility(View.INVISIBLE);
                        isVisiable = false;
                    }
                });
    }

    /**
     * 商家详情请求
     *
     */
    private void requestShopDetailTask(){
        String shopId = CacheUtil.getInstance().getShopId();
        String requestUrl = ProtocolUtil.getShopDetailUrl(shopId);
        NetRequest netRequest = new NetRequest(requestUrl);
        NetRequestTask netRequestTask = new NetRequestTask(getActivity(),netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(getActivity()) {
            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                try {
                    super.onNetworkResponseSucceed(result);
                    Log.i(TAG,"result.rawResult:"+result.rawResult);
                    ShopDetailResponse shopDetailResponse = new Gson().fromJson(result.rawResult,ShopDetailResponse.class);
                    if(null != shopDetailResponse){
                        ArrayList<ShopVo> shopList =  shopDetailResponse.getData();
                        if(null != shopList && !shopList.isEmpty()){
                            ShopVo shopVo = shopList.get(0);
                            if(null != shopVo){
                                setShopDescData(shopVo);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        netRequestTask.execute();
    }

    /**
     * 设置商家详情页面相关数据
     * @param shopVo
     */
    private void setShopDescData(ShopVo shopVo){
        String shopBgUrl = shopVo.getShoplogo();
        if(!TextUtils.isEmpty(shopBgUrl)){
            Uri shopUri = Uri.parse(ConfigUtil.getInst().getImgDomain()+shopBgUrl);
            shopBgIv.setImageURI(shopUri);
        }
        String shopName = shopVo.getShopname();
        if(!TextUtils.isEmpty(shopName)){
            shopNameTv.setText(shopName);
        }
        String address = shopVo.getShopaddress();
        if(!TextUtils.isEmpty(address)){
            addressTv.setText(address);
        }
        String telephone = shopVo.getTelephone();
        if(!TextUtils.isEmpty(telephone)){
            telephoneTv.setText(telephone);
        }
        shopModeList = shopVo.getShopmods();
        if(null != shopModeList && !shopModeList.isEmpty()){
            shopAdapter.setShopModeList(shopModeList);
            setListViewHeight(descListView);
        }
    }

    /**
     * 重新计算ListView的高度，解决ScrollView和ListView两个View都有滚动的效果，在嵌套使用时起冲突的问题
     * @param listView
     */
    private void setListViewHeight(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
