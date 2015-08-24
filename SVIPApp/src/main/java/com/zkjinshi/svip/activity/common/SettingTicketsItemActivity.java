package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.mine.MineNetController;
import com.zkjinshi.svip.view.ItemTitleView;

import java.util.HashMap;

/**
 * Created by djd on 2015/8/24.
 */
public class SettingTicketsItemActivity extends Activity {

    private final static String TAG = SettingPhoneActivity.class.getSimpleName();
    private boolean isNew = true;

    private ItemTitleView mTitle;
    private HashMap<String,String> tickeData = null;
    private EditText  mInputEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_ticket_item);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        mTitle            = (ItemTitleView) findViewById(R.id.itv_title);
        mInputEt          = (EditText)findViewById(R.id.et_setting_input);
    }

    private void initData() {
        MineNetController.getInstance().init(this);

        mTitle.getmRight().setVisibility(View.VISIBLE);
        mTitle.mRightImage.setVisibility(View.GONE);
        mTitle.mRightText.setVisibility(View.VISIBLE);
        mTitle.mRightText.setText("保存");
        mTitle.setTextTitle("设置");

        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            isNew = true;
        }else{
            Object data = bundle.getSerializable("hashMap");
            isNew = false;
            tickeData = (HashMap<String,String>)data;
            mInputEt.setText(tickeData.get("ticket_name"));

        }


    }

    private void initListener() {
        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingTicketsItemActivity.this,SettingTicketsActivity.class));
                finish();
            }
        });

        mTitle.getmRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTicket();
            }
        });
    }

    private void saveTicket(){
        startActivity(new Intent(SettingTicketsItemActivity.this,SettingTicketsActivity.class));
        finish();
    }
}
