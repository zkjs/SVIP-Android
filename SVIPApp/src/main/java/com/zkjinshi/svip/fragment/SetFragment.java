package com.zkjinshi.svip.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.SettingActivity;
import com.zkjinshi.svip.activity.mine.SetActivity;
import com.zkjinshi.svip.activity.order.ConsumeRecordActivtiy;

import com.zkjinshi.svip.net.NetDialogUtil;

import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.CircleImageView;

/**
 * 设置Fragment
 *
 */
public class SetFragment extends Fragment{

    public static final String TAG = SetFragment.class.getSimpleName();

    public static final int KILL_MYSELT = 1;

    private RelativeLayout accountInfoLayout,orderManagerLayout,setLayout;
    private CircleImageView userPhotoIv;
    private TextView userNameTv;
    private DisplayImageOptions options;

    private void initView(View view){
        userPhotoIv = (CircleImageView)view.findViewById(R.id.set_uv_user_photo);
        userNameTv = (TextView)view.findViewById(R.id.set_uv_user_name);
        accountInfoLayout = (RelativeLayout)view.findViewById(R.id.set_layout_user_account_info);
        orderManagerLayout = (RelativeLayout)view.findViewById(R.id.set_layout_order_manager);
        setLayout = (RelativeLayout)view.findViewById(R.id.set_layout_set);
    }

    private void initData(){
        if(!CacheUtil.getInstance().isLogin()){
            NetDialogUtil.showLoginDialog(getActivity());
            return;
        }
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.img_logo_zhanwei)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.img_logo_zhanwei)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.img_logo_zhanwei)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
        if(CacheUtil.getInstance().isLogin() && !TextUtils.isEmpty(CacheUtil.getInstance().getUserId())){
            String userId = CacheUtil.getInstance().getUserId();
            String userPhotoUrl = ProtocolUtil.getAvatarUrl(userId);
            ImageLoader.getInstance().displayImage(userPhotoUrl, userPhotoIv, options);
        }
        userNameTv.setText(CacheUtil.getInstance().getUserName());
    }

    private void initListeners(){

        //账户信息
        accountInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

        //订单管理
        orderManagerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ConsumeRecordActivtiy.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

        //设置
        setLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SetActivity.class);
                getActivity().startActivityForResult(intent,KILL_MYSELT);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set,container,false);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

}
