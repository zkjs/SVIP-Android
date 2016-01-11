package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.view.ItemTitleView;

/**
 * Created by djd on 2015/8/28.
 */
public class AddRemarkActivity extends BaseActivity implements View.OnClickListener{

    private TextView titleTv;
    private ImageView backIv;
    private TextView mTipsTv;
    private EditText mInputEt;
    private Button mSaveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people);

        initView();
        initData();
    }

    private void initView() {
        mInputEt = (EditText)findViewById(R.id.et_setting_input);
        mTipsTv = (TextView)findViewById(R.id.tv_setting_tips);
        mSaveBtn = (Button)findViewById(R.id.btn_confirm);
        mSaveBtn.setOnClickListener(this);
        titleTv = (TextView) findViewById(R.id.header_title_tv);
        backIv = (ImageView)findViewById(R.id.header_back_iv);
    }

    private void initData() {
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String titleStr = getIntent().getStringExtra("title");
        String remark = getIntent().getStringExtra("remark");
        String tips = getIntent().getStringExtra("tips");
        String hint = getIntent().getStringExtra("hint");
        String key = getIntent().getStringExtra("key");
        titleTv.setText(titleStr);
        mInputEt.setHint(hint);
        mTipsTv.setText(tips);
        if(!TextUtils.isEmpty(remark)){
            mInputEt.setText(remark);
            mInputEt.setSelection(remark.length());//将光标移至文字末尾
        }
        if(key.equals("phone")){
            mInputEt.setInputType(InputType.TYPE_CLASS_PHONE);
        }

    }

    public void onClick(View view) {
        String name = mInputEt.getText().toString();
//        if(TextUtils.isEmpty(name)){
//            DialogUtil.getInstance().showToast(this,"不能为空！");
//            return;
//        }

        Intent data = new Intent();
        data.putExtra("remark",name);
        setResult(RESULT_OK, data);
        finish();
    }


}
