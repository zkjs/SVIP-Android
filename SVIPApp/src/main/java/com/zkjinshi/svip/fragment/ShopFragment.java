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
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.FlingCallback;
import com.zkjinshi.svip.view.GestureListView;
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
    private GestureListView descListView;
    private ArrayList<ShopModeVo> shopModeList;
    private ShopAdapter shopAdapter;
    private View shopHeadLayout;
    private View homeView;

    private ImageButton backIBtn;
    private TextView titleTv;
    private View headLayout;



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

    public void initData(){
        requestShopDetailTask();
    }

    private void initUI(final View root) {
        backIBtn = (ImageButton) root.findViewById(R.id.btn_back);
        titleTv = (TextView)root.findViewById(R.id.title_tv);
        headLayout = root.findViewById(R.id.my_header);

        shopHeadLayout = LayoutInflater.from(getActivity()).inflate(R.layout.layout_shop_head,null);
        shopBgIv = (SimpleDraweeView)shopHeadLayout.findViewById(R.id.shop_detail_tv_bg);
        shopNameTv = (TextView)shopHeadLayout.findViewById(R.id.shop_detail_tv_shop_name);
        addressTv = (TextView)shopHeadLayout.findViewById(R.id.shop_detail_tv_address);
        telephoneTv = (TextView)shopHeadLayout.findViewById(R.id.shop_detail_tv_telephone);
        descListView = (GestureListView)root.findViewById(R.id.shop_desc_list_view);
        shopAdapter = new ShopAdapter(getActivity(),shopModeList);
        shopAdapter.shopFragment = this;
        descListView.addHeaderView(shopHeadLayout);
        descListView.setAdapter(shopAdapter);

        descListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem == 0){
                    View firstView = descListView.getChildAt(0);
                    if(firstView != null){
                        int headerHeight = shopBgIv.getHeight();
                        int y = -firstView.getTop();
                        if (y <= headerHeight && y > 0) {
                            float f = (float) y / (float) headerHeight;
                            headLayout.getBackground().setAlpha((int) (f * 255));
                            titleTv.setTextColor(getResources().getColor(R.color.white));
                        } else if (y > headerHeight) {
                            headLayout.getBackground().setAlpha(255);
                        } else {
                            headLayout.getBackground().setAlpha(0);
                        }

                    }
                }
            }
        });

        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideAction();
            }
        });

    }

    public void show(final View view){
        this.homeView = view;
        final float rotation = 90;
        final long duration = 400;
        ViewHelper.setRotationY(view, 0);
        ViewPropertyAnimator.animate(view).rotationY(-rotation)
                .setDuration(duration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ViewHelper.setRotationY(root, rotation);
                root.setVisibility(View.VISIBLE);
                root.setEnabled(true);
                homeView.setVisibility(View.GONE);
                ViewPropertyAnimator.animate(root)
                        .rotationY(0).setDuration(duration)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                ViewHelper.setRotationY(view, 0);
                                isVisiable = true;
                            }
                        }).setInterpolator(new AccelerateInterpolator());
            }
        })
        .setInterpolator(new AccelerateInterpolator());

        if(descListView != null){
            descListView.setSelection(0);
        }
        headLayout.getBackground().setAlpha(0);
    }



    public void hideAction(){
        final float rotation = -90;
        final long duration = 400;
        ViewHelper.setRotationY(root, 0);
        ViewPropertyAnimator.animate(root).rotationY(-rotation).setInterpolator(new AccelerateInterpolator())
                .setDuration(duration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                root.clearAnimation();
                root.setVisibility(View.GONE);
                homeView.setVisibility(View.VISIBLE);
                isVisiable = false;
                headLayout.getBackground().setAlpha(255);
                ViewHelper.setRotationY(homeView, rotation);
                ViewPropertyAnimator.animate(homeView)
                        .rotationY(0).setDuration(duration)
                        .setListener(null).setInterpolator(new AccelerateInterpolator());
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
        String shopBgUrl = shopVo.getShopbg();
        if(!TextUtils.isEmpty(shopBgUrl)){
            Uri shopUri = Uri.parse(ConfigUtil.getInst().getCdnDomain()+shopBgUrl);
            shopBgIv.setImageURI(shopUri);
        }
        String shopName = shopVo.getShopname();
        if(!TextUtils.isEmpty(shopName)){
            shopNameTv.setText(shopName);
            titleTv.setText(shopName);
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
        }
    }

}
