package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.zkjinshi.svip.R;

/**
 * Created by dujiande on 2015/12/29.
 */
public class NormalBookingActivity extends Activity {

    private final static String TAG = NormalBookingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_booking);

        initView();
        initData();
        initListener();
    }

    private void initView() {

    }

    private void initData() {

    }

    private void initListener() {

    }
}
