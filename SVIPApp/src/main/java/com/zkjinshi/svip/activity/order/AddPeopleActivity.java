package com.zkjinshi.svip.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.view.ItemTitleView;

/**
 * Created by djd on 2015/8/28.
 */
public class AddPeopleActivity extends Activity implements View.OnClickListener{

    private ItemTitleView mTitle;//返回
    private TextView mTipsTv;
    private EditText mInputEt;
    private Button mSaveBtn;
    private int mIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        mTitle = (ItemTitleView)findViewById(R.id.itv_setting_title);
    }

    private void initData() {
        mTitle.getmRight().setVisibility(View.INVISIBLE);
        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String titleStr = getIntent().getStringExtra("title");
        String name = getIntent().getStringExtra("name");
        mIndex = getIntent().getIntExtra("index",0);

        mTitle.setTextTitle(titleStr);
        if(!TextUtils.isEmpty(name)){
            mInputEt.setText(name);
        }
    }

    public void onClick(View view) {
        String name = mInputEt.getText().toString();
        if(TextUtils.isEmpty(name)){
            DialogUtil.getInstance().showToast(this,"不能为空！");
            return;
        }

        Intent data = new Intent();
        data.putExtra("name",name);
        data.putExtra("index",mIndex);
        setResult(RESULT_OK, data);
        finish();
    }


}
