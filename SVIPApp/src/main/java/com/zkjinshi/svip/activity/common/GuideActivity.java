package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.utils.CacheUtil;

/**
 * Created by dujiande on 2015/12/22.
 */
public class GuideActivity extends Activity {
    private final static String TAG = GuideActivity.class.getSimpleName();

    private EditText passwordEt;
    private Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        passwordEt = (EditText)findViewById(R.id.et_verify_code);
        confirmBtn = (Button)findViewById(R.id.btn_confirm);
    }

    private void initData() {

    }

    private void initListener() {
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               goHome();
            }
        });

        passwordEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“搜查”键*/
                if(actionId == EditorInfo.IME_ACTION_GO){
                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow( v.getApplicationWindowToken(), 0);
                    }
                    String inputStr = passwordEt.getText().toString();
                    if (inputStr.length() > 0 && inputStr.equals("zkjs888")) {
                        confirmBtn.setVisibility(View.VISIBLE);
                    }else{
                        confirmBtn.setVisibility(View.GONE);
                    }
                    return true;
                }
                return false;
            }
        });

        passwordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputStr = editable.toString();
                if (inputStr.length() > 0 && inputStr.equals("zkjs888")) {
                    confirmBtn.setVisibility(View.VISIBLE);
                }else{
                    confirmBtn.setVisibility(View.GONE);
                }
            }
        });
    }

    public void goHome(){
        CacheUtil.getInstance().setGuide(true);
        Intent mainIntent = new Intent(GuideActivity.this, MainActivity.class);
        GuideActivity.this.startActivity(mainIntent);
        GuideActivity.this.finish();
        overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
    }
}

