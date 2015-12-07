package com.zkjinshi.svip.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zkjinshi.svip.R;

/**
 * 首页Fragment
 */
public class SetFragment extends Fragment {


    private void initView(View view){

    }

    private void initData(){

    }

    private void initListeners(){

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
        initData();;
        initListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
