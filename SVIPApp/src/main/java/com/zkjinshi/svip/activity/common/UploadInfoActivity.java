package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.vo.BaseResponseVo;

/**
 * Created by dujiande on 2016/3/8.
 */
public class UploadInfoActivity extends BaseActivity {

    private EditText inputEt;
    private Button nextBtn;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_info);

        mContext = this;

        initView();
        initData();
        initListener();
    }

    private void initView() {
        inputEt = (EditText)findViewById(R.id.inputEt);
        nextBtn = (Button) findViewById(R.id.btn_next);
    }

    private void initData() {
        if(!TextUtils.isEmpty(CacheUtil.getInstance().getUserName())){
            inputEt.setText(CacheUtil.getInstance().getUserName());
        }
    }

    private void initListener() {
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameStr = inputEt.getText().toString();
                if(TextUtils.isEmpty(nameStr)){
                    Toast.makeText(mContext,"用户名不能为空。",Toast.LENGTH_SHORT).show();
                    return;
                }
                CacheUtil.getInstance().setUserName(nameStr);
                Intent intent = new Intent(mContext,UploadAvatarActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
