package com.zkjinshi.base.config;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zkjinshi.base.R;
import com.zkjinshi.base.util.Constants;

import java.io.IOException;

/**
 * 设置配置信息相关页面
 * 开发者：JimmyZhang
 * 日期：2015/7/18
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ConfigActivity extends Activity {

    private EditText javaDomainEtv, phpDomainEtv;
    private Button saveBtn;

    private void initView(){
        javaDomainEtv = (EditText)findViewById(R.id.editText_server);
        phpDomainEtv = (EditText)findViewById(R.id.editText_port);
        saveBtn = (Button)findViewById(R.id.button_save);
    }

    private void initData(){
        javaDomainEtv.setText(""+ ConfigUtil.getInst().getJavaDomain());
        phpDomainEtv.setText(""+ ConfigUtil.getInst().getPhpDomain());
    }

    private void initListeners(){

        //保持网络配置信息
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String iHost = javaDomainEtv.getText().toString();
                String iPort = phpDomainEtv.getText().toString();
                try {
                    ConfigUtil.getInst().put(Constants.JAVA_HOST, iHost);
                    ConfigUtil.getInst().put(Constants.PHP_HOST, iPort);
                    ConfigUtil.getInst().save(view.getContext());
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    finish();
                    overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                }
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        initView();
        initData();
        initListeners();
    }
}
