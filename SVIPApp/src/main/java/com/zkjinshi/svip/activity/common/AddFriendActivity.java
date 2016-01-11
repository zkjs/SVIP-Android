package com.zkjinshi.svip.activity.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.UserGetuserResponse;
import com.zkjinshi.svip.utils.ProtocolUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * 添加好友
 */
public class AddFriendActivity extends BaseActivity {
    private final static String TAG = AddFriendActivity.class.getSimpleName();

    private EditText inputTextEt;
    private TextView showTextTv;
    private LinearLayout showLayout;
    private ImageView closeIv;
    private TextView msgTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                //Toast.makeText(AddFriendActivity.this,"click me",Toast.LENGTH_SHORT).show();
                getServeirByPhone(inputTextEt.getText().toString());
            }
        });
        inputTextEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“搜查”键*/
                if(actionId == EditorInfo.IME_ACTION_SEARCH && showLayout.getVisibility() == View.VISIBLE){
                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow( v.getApplicationWindowToken(), 0);
                    }
                    getServeirByPhone(inputTextEt.getText().toString());
                    return true;
                }
                return false;
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


    //根据手机号查找服务员ID并跳转
    public void getServeirByPhone(String phone){
        String url = ProtocolUtil.getServerByPhone(phone);
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(this,netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    Type listType = new TypeToken<ArrayList<UserGetuserResponse>>() {}.getType();
                    ArrayList<UserGetuserResponse> userList = new Gson().fromJson(result.rawResult, listType);
                    if(userList.size() > 0){
                        Intent intent = new Intent(AddFriendActivity.this, ContactActivity.class);
                        intent.putExtra("contact_id", userList.get(0).getUserId());
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        msgTv.setVisibility(View.GONE);
                    }
                    else{
                        msgTv.setVisibility(View.VISIBLE);
                    }


                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }
}
