package com.zkjinshi.svip.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.MainActivity;
import com.zkjinshi.svip.activity.common.MainUiController;
import com.zkjinshi.svip.activity.common.SettingActivity;
import com.zkjinshi.svip.activity.mine.MineActivity;
import com.zkjinshi.svip.activity.order.HistoryOrderActivtiy;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.view.CircleImageView;

/**
 * Created by djd on 2015/8/10.
 */
public class MenuLeftFragment extends Fragment implements View.OnClickListener{
    public static final String TAG = "MenuLeftFragment";
    private View mView;
    private CircleImageView photoCtv;
    private TextView nameTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        if (mView == null)
        {
            initView(inflater, container);
        }

        return mView;
    }

    public void onStart()    {
        super.onStart();
        LogUtil.getInstance().info(LogLevel.DEBUG, "onStart...");

    }
    public void onResume(){
        super.onResume();
        LogUtil.getInstance().info(LogLevel.DEBUG,"onResume...");
        initData();
    }

    private void initView(LayoutInflater inflater, ViewGroup container)
    {
        mView = inflater.inflate(R.layout.left_menu, container, false);

        photoCtv = (CircleImageView)mView.findViewById(R.id.leftmenu_user_photo_civ);
        nameTv = (TextView)mView.findViewById(R.id.leftmenu_user_name);

        mView.findViewById(R.id.leftmenu_home_tv).setOnClickListener(this);
        mView.findViewById(R.id.leftmenu_set_tv).setOnClickListener(this);
        mView.findViewById(R.id.leftmenu_front_tv).setOnClickListener(this);
        mView.findViewById(R.id.leftmenu_order_tv).setOnClickListener(this);
        mView.findViewById(R.id.leftmenu_footprint_tv).setOnClickListener(this);
    }

    private void initData()
    {
        setAvatar();
    }

    public void setAvatar(){
        MainUiController.getInstance().init(getActivity());
        MainUiController.getInstance().setUserPhoto(CacheUtil.getInstance().getUserPhotoUrl(), photoCtv);
        nameTv.setText(CacheUtil.getInstance().getUserPhone().toString());
    }

    // 点击事件处理
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            //主页
            case R.id.leftmenu_home_tv:
            {
                Activity activity = getActivity();
                if(activity instanceof MainActivity)
                {
                    ((MainActivity)activity).toggleMenu();
                }
            }
            break;
            //设置
            case R.id.leftmenu_set_tv:
            {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
            break;
            //免前台
            case R.id.leftmenu_front_tv:
            {

            }
            break;
            //订单
            case R.id.leftmenu_order_tv:
            {

            }
            break;
            //足迹
            case R.id.leftmenu_footprint_tv:
            {
                Intent intent = new Intent(getActivity(), HistoryOrderActivtiy.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
            break;
        }
    }
}
