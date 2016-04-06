package com.zkjinshi.svip.fragment;

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
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;

/**
 * 商家详情页
 * 开发者：JimmyZhang
 * 日期：2015/7/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ShopFragment extends Fragment {

    public static final String TAG = ShopFragment.class.getSimpleName();

    private TextView tv;
    private View root;
    private View view;

    public boolean isVisiable = false;

    private GestureDetector gestureDetector;
    private GestureDetector.OnGestureListener onGestureListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    float x = e2.getX() - e1.getX();

                    if (x > 0 ) {
                        if(isVisiable){
                            hideAction();
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
        tv = (TextView) root.findViewById(R.id.textView);
        root.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                root.setClickable(false);
                ViewPropertyAnimator.animate(root)
                        .rotationY(90).setDuration(200)
                        .setListener(new AnimatorListenerAdapter(){
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                root.clearAnimation();
                                root.setVisibility(View.INVISIBLE);
                                view.setEnabled(true);
                                isVisiable = false;
                            }
                        });
            }
        });
    }

    public void show(final View view,Bundle bundle){
        this.view = view;
        String text = bundle.getString("text");
        tv.setText(text);
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
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
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
                super.onNetworkResponseSucceed(result);
                Log.i(TAG,"获取商家详情网络请求数据:"+result.rawResult);
            }
        });
        netRequestTask.execute();
    }

}
