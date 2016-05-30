package com.zkjinshi.svip.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.IntentUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;

import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.StringUtil;


import org.json.JSONObject;



/**
 * 修改资料
 * 开发者：杜健德
 * 日期：2015/8/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SettingItemActivity extends BaseActivity implements View.OnClickListener  {

    private final static String TAG = SettingItemActivity.class.getSimpleName();

    private TextView mTipsTv;
    private EditText mInputEt;
    private Button   mSaveBtn;
    private String fieldKey;

    private ImageButton backIBtn;
    private TextView titleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_item);

        initView();//初始化view
        initData();//初始化data加入

    }

    private void initView() {

        mInputEt = (EditText)findViewById(R.id.et_setting_input);
        mTipsTv = (TextView)findViewById(R.id.tv_setting_tips);
        mSaveBtn = (Button)findViewById(R.id.btn_confirm);
        mSaveBtn.setOnClickListener(this);
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);

    }

    private void initData() {


        backIBtn.setVisibility(View.VISIBLE);
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fieldKey = getIntent().getStringExtra("field_key");
        String titleStr = getIntent().getStringExtra("title");
        titleTv.setText(titleStr);

        mInputEt.setHint(getIntent().getStringExtra("hint"));
        mTipsTv.setText(getIntent().getStringExtra("tips"));

        String fieldValue = getIntent().getStringExtra("field_value");

        if(!TextUtils.isEmpty(fieldValue)){
            mInputEt.setText(fieldValue);
        }
        if(fieldKey.equals("email") ){
//            mInputEt.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
//                    String inputStr = editable.toString();
//                    if (inputStr.length() > 0) {
//                        if( IntentUtil.isEmailAddress(inputStr)){
//                            mTipsTv.setText("");
//                        }else{
//                            mTipsTv.setText("格式错误");
//                        }
//                    }
//                }
//            });
        }

        if(fieldKey.equals("username")){
            //姓名最多12个中文字符
            mInputEt.setFilters(new InputFilter[]{ new  InputFilter.LengthFilter(20)});
        }else if(fieldKey.equals("email")){
            mInputEt.setFilters(new InputFilter[]{ new  InputFilter.LengthFilter(40)});
        }

    }

    @Override
    public void onClick(View view) {
        String fieldValue = mInputEt.getText().toString();
        if(TextUtils.isEmpty(fieldValue)){
            DialogUtil.getInstance().showToast(this,"不能为空！");
            return;
        }
        if(fieldKey.equals("email") )
        {
            if( IntentUtil.isEmailAddress(fieldValue)){
                mTipsTv.setText("");
            }else{
                mTipsTv.setText("邮件格式错误！");
                return;
            }
        }else if(fieldKey.equals("username")){
            if(!StringUtil.isNormalName(fieldValue)){
                if(fieldValue.length() > 20){
                    Toast.makeText(this,"填写的姓名超过限制长度。",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"填写不合符规范，请填写真实姓名。",Toast.LENGTH_SHORT).show();
                }
                return;
            }else if(fieldValue.length() > 20){
                Toast.makeText(this,"填写的姓名超过限制长度。",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        submitInfo(fieldKey,fieldValue);
    }

    //提交资料
    public void submitInfo(final String fieldKey,final String fieldValue){

        MineNetController.getInstance().submitInfo(this, fieldKey, fieldValue, new MineNetController.CallBackListener() {
            @Override
            public void successCallback(JSONObject response) {
                Intent data = new Intent();
                data.putExtra("new_value", fieldValue);
                setResult(RESULT_OK, data);
                if(fieldKey.equals("username")){
                    CacheUtil.getInstance().setUserName(fieldValue);
                    SettingActivity.ismodifyusername = 1;
                } else if(fieldKey.equals("real_name")){
                    CacheUtil.getInstance().setUserRealName(fieldValue);
                }
                finish();
            }

            public void failCallBack(){

            }
        });

    }
}
