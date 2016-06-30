package com.zkjinshi.svip.activity.invite;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.call.CallOrderActivity;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.MessageUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.BaseResponseVo;
import com.zkjinshi.svip.vo.InvitationVo;
import com.zkjinshi.svip.vo.ServiceTagDataSecondVo;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by dujiande on 2016/6/21.
 */
public class InviteCreateActivity extends BaseActivity {

    private final static String TAG = InviteCreateActivity.class.getSimpleName();

    private Context mContext;
    private ImageButton backIBtn;
    private TextView titleTv,peopleTv,timeTv,nameTv;

    private ImageButton subBtn,addBtn;
    private Button confirmBtn;

    private InvitationVo invitationVo;

    private int minPeopleNum = 0;
    private int maxPeopleNum = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_create);
        mContext = this;

        initView();
        initData();
        initListener();
    }

    private void initView() {
        backIBtn = (ImageButton)findViewById(R.id.btn_back);
        titleTv = (TextView)findViewById(R.id.title_tv);

        nameTv = (TextView)findViewById(R.id.name_tv);
        timeTv = (TextView)findViewById(R.id.time_tv);
        peopleTv = (TextView)findViewById(R.id.people_tv);

        subBtn = (ImageButton)findViewById(R.id.sub_ibtn);
        addBtn = (ImageButton)findViewById(R.id.add_ibtn);
        confirmBtn = (Button)findViewById(R.id.btn_confirm);
    }

    private void initData() {
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("参加活动");

        nameTv.setText(CacheUtil.getInstance().getUserName());

        invitationVo = (InvitationVo) getIntent().getSerializableExtra("invitationVo");
        maxPeopleNum = invitationVo.getMaxtake();
        titleTv.setText(invitationVo.getActname());
        timeTv.setText(invitationVo.getStartdate() + " - "+invitationVo.getEnddate());

        peopleTv.setText(maxPeopleNum+"");

    }

    private void initListener() {

        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmInvitation();
            }
        });

        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numStr = peopleTv.getText().toString();
                int num = Integer.parseInt(numStr);
                if(num -1 >= minPeopleNum){
                    num = num -1;
                    peopleTv.setText(num+"");
                }
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numStr = peopleTv.getText().toString();
                int num = Integer.parseInt(numStr);
                if(num + 1 <= maxPeopleNum){
                    num = num + 1;
                    peopleTv.setText(num+"");
                }
            }
        });
    }

    public void onResume(){
        super.onResume();
    }

    /**
     * 确认邀请
     */
    private void confirmInvitation(){
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("actid",invitationVo.getActid());
            jsonObject.put("actstatus",1);
            jsonObject.put("takeperson",peopleTv.getText().toString());
            jsonObject.put("userid",CacheUtil.getInstance().getUserId());
            StringEntity stringEntity = new StringEntity(jsonObject.toString(),"utf-8");
            String url = ProtocolUtil.updateActivityStatus();
            client.put(mContext, url, stringEntity, "application/json", new AsyncHttpResponseHandler(){

                public void onStart(){
                    super.onStart();
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    super.onFinish();
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        BaseResponseVo baseResponseVo = new Gson().fromJson(response,BaseResponseVo.class);
                        if(baseResponseVo == null){
                            return;
                        }
                        if(baseResponseVo.getRes() == 0){
                            MessageUtil.showImgToast(mContext,"提交成功");
                            finish();
                        }else{
                            Toast.makeText(mContext, baseResponseVo.getResDesc(),Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
