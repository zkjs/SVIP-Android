package com.zkjinshi.svip.activity.call;



import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;

import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.CallReadyVo;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


/**
 * Created by dujiande on 2016/4/5.
 */
public class CallEvaluateActivity extends BaseActivity{

    private TextView nameTv,serviceTv;
    private RelativeLayout mohuRlt;
    private SimpleDraweeView avatarSdv;
    private RatingBar ratingBar;
    private Button confirmBtn;
    private CallReadyVo callReadyVo;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_evaluate);
        mContext = this;
        initView();
        initData();
        initListener();
    }

    public void onDestroy(){
        super.onDestroy();
    }

    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }

    private void initView() {
        nameTv = (TextView)findViewById(R.id.name_tv);
        serviceTv = (TextView)findViewById(R.id.service_tv);
        mohuRlt = (RelativeLayout)findViewById(R.id.mohu_rlt);
        avatarSdv = (SimpleDraweeView)findViewById(R.id.avatar_sdv);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        confirmBtn = (Button)findViewById(R.id.btn_confirm);
    }

    private void initData() {
        callReadyVo = (CallReadyVo)getIntent().getSerializableExtra("callReadyVo");
        nameTv.setText(callReadyVo.getWaitername());
        serviceTv.setText(callReadyVo.getSrvname());
        String avatarurl = ProtocolUtil.getAvatarUrl(this,callReadyVo.getWaiterimage());
        avatarSdv.setImageURI(Uri.parse(avatarurl));
        ratingBar.setRating(0);
        ratingBar.setStepSize(1);
        confirmBtn.setVisibility(View.GONE);
    }


    private void initListener() {
        mohuRlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if(ratingBar.getRating() <1){
                    confirmBtn.setVisibility(View.GONE);
                }else{
                    confirmBtn.setVisibility(View.VISIBLE);
                }

            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitEvaluate();
            }
        });
    }

    private void submitEvaluate() {
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            if(CacheUtil.getInstance().isLogin()){
                client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("taskid",callReadyVo.getTaskid());
            jsonObject.put("operationseq",callReadyVo.getOperationseq());
            jsonObject.put("taskaction",6);
            int scrore = (int)(ratingBar.getRating()*2);
            jsonObject.put("score",scrore);
            jsonObject.put("desc","");
            StringEntity stringEntity = new StringEntity(jsonObject.toString(),"UTF-8");
            String url = ProtocolUtil.callTaskUpdate();
            client.put(mContext,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        //String response = new String(responseBody,"utf-8");
                        finish();
                        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        }catch (Exception e){
            Toast.makeText(mContext,"json解析错误",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


}
