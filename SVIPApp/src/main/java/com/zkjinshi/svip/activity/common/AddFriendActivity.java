package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zkjinshi.svip.R;

/**
 * Created by dujiande on 2015/12/16.
 */
public class AddFriendActivity extends Activity {
    private final static String TAG = AddFriendActivity.class.getSimpleName();

    private EditText inputTextEt;
    private TextView showTextTv;
    private LinearLayout showLayout;
    private ImageView closeIv;
    private TextView msgTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        inputTextEt = (EditText)findViewById(R.id.input_et);
        showTextTv = (TextView)findViewById(R.id.showtextTv);
        closeIv = (ImageView)findViewById(R.id.close_iv);
        showLayout = (LinearLayout)findViewById(R.id.show_layout);
        msgTv = (TextView)findViewById(R.id.msg_tv);
    }

    private void initData() {
        inputTextEt.setText("");
        showTextTv.setText("");
        closeIv.setVisibility(View.GONE);
        showLayout.setVisibility(View.GONE);
        msgTv.setVisibility(View.GONE);
    }

    private void initListener() {
        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddFriendActivity.this.finish();
            }
        });
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData();
            }
        });
        showLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddFriendActivity.this,"click me",Toast.LENGTH_SHORT).show();
            }
        });

       inputTextEt.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {
           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {

           }

           @Override
           public void afterTextChanged(Editable editable) {
               String inputStr = editable.toString();
               if (inputStr.length() <= 0) {
                   closeIv.setVisibility(View.GONE);
                   showLayout.setVisibility(View.GONE);
               }else{
                   closeIv.setVisibility(View.VISIBLE);
                   showLayout.setVisibility(View.VISIBLE);
                   showTextTv.setText(inputStr);
               }
           }
       });
    }


    public void getServeirByPhone(String phone){
        Intent intent = new Intent(this, ContactActivity.class);
        //intent.putExtra("contact_id", bestServerid);
        this.startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
}
