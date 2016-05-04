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

    private EditText pavDomainEtv, pyxDomainEtv;
    private EditText forDomainEtv, cdnDomainEtv, pcdDomaionEtv;
    private Button saveBtn;

    private void initView(){
        pavDomainEtv = (EditText)findViewById(R.id.editText_pav);
        pyxDomainEtv = (EditText)findViewById(R.id.editText_pyx);
        forDomainEtv = (EditText)findViewById(R.id.editText_for);
        cdnDomainEtv = (EditText)findViewById(R.id.editText_cdn);
        pcdDomaionEtv = (EditText)findViewById(R.id.editText_pcm);
        saveBtn = (Button)findViewById(R.id.button_save);
    }

    private void initData(){
        pavDomainEtv.setText(""+ ConfigUtil.getInst().getConfigValue(Constants.PAV_HOST));
        pyxDomainEtv.setText(""+ ConfigUtil.getInst().getConfigValue(Constants.PYX_HOST));
        forDomainEtv.setText(""+ ConfigUtil.getInst().getConfigValue(Constants.FOR_HOST));
        cdnDomainEtv.setText(""+ ConfigUtil.getInst().getConfigValue(Constants.CDN_HOST));
        pcdDomaionEtv.setText(""+ConfigUtil.getInst().getConfigValue(Constants.PCD_HOST));
    }

    private void initListeners(){

        //保持网络配置信息
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pavDomain = pavDomainEtv.getText().toString();
                String pyxDomain = pyxDomainEtv.getText().toString();
                String forDomain = forDomainEtv.getText().toString();
                String cdnDomain = cdnDomainEtv.getText().toString();
                String pcmDomain = pcdDomaionEtv.getText().toString();
                try {
                    ConfigUtil.getInst().put(Constants.PAV_HOST, pavDomain);
                    ConfigUtil.getInst().put(Constants.PYX_HOST, pyxDomain);
                    ConfigUtil.getInst().put(Constants.FOR_HOST, forDomain);
                    ConfigUtil.getInst().put(Constants.CDN_HOST, cdnDomain);
                    ConfigUtil.getInst().put(Constants.PCD_HOST, pcmDomain);
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
