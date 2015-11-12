package com.zkjinshi.svip.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.ShopAdapter;
import com.zkjinshi.svip.response.ShopInfoResponse;
import com.zkjinshi.svip.vo.ShopInfoVo;

import java.util.List;

/**
 * 开发者：dujiande
 * 日期：2015/11/11
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class PlayFragment  extends Fragment {
    public static String TAG = PlayFragment.class.getSimpleName();
    public View view = null;

    private ListView shopListView;
    private List<ShopInfoResponse> shopResponseList;
    private List<ShopInfoVo> shopInfoList;
    private ShopAdapter shopAdapter;

    public PlayFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play,null);
        this.view = view;
        Log.i(TAG, "onCreateView");

        initView();
        initData();
        initListeners();
        return view;
    }

    private void initListeners() {

    }

    private void initData() {

    }

    private void initView() {
        shopListView = (ListView)view.findViewById(R.id.play_list_view);
        View emptyView = getActivity().getLayoutInflater().inflate(R.layout.empty_layout, null);
        TextView tips = (TextView)emptyView.findViewById(R.id.empty_tips);
        tips.setText("暂无商家");
        ((ViewGroup)shopListView.getParent()).addView(emptyView);
        shopListView.setEmptyView(emptyView);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");
    }
}
