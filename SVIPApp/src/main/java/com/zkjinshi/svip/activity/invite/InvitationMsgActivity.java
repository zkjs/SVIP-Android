package com.zkjinshi.svip.activity.invite;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.config.ConfigUtil;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.call.CallOrderActivity;
import com.zkjinshi.svip.activity.common.MainActivity;
import com.zkjinshi.svip.activity.common.SplashActivity;
import com.zkjinshi.svip.activity.common.WebViewActivity;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.sqlite.BeaconMsgDBUtil;
import com.zkjinshi.svip.sqlite.InvitationMsgDBUtil;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.MessageUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.StringUtil;
import com.zkjinshi.svip.utils.qclCopy.BlurBehind;
import com.zkjinshi.svip.vo.BaseResponseVo;
import com.zkjinshi.svip.vo.InvitationVo;
import com.zkjinshi.svip.vo.YunBaMsgVo;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by dujiande on 2016/4/5.
 */
public class InvitationMsgActivity extends BaseActivity{

    private TextView titleTv,contentTv;
    private RelativeLayout mohuRlt;
    private TextView openTv;
    private LinearLayout openLayout;
    private Button confirmBtn;
    private SimpleDraweeView logoDv;
    private InvitationVo invitationVo;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_msg);
        MainActivity.INVITATION_MSG_DELAY_TIME = 0;
        SplashActivity.invitationMsg = null;

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
        titleTv = (TextView)findViewById(R.id.ad_title_tv);
        contentTv = (TextView)findViewById(R.id.ad_des_tv);
        mohuRlt = (RelativeLayout)findViewById(R.id.mohu_rlt);
        openTv = (TextView)findViewById(R.id.ad_open_tv);
        openLayout = (LinearLayout)findViewById(R.id.ad_open_layout);
        logoDv = (SimpleDraweeView)findViewById(R.id.imageSdv);

        confirmBtn = (Button)findViewById(R.id.btn_confirm);
    }

    private void initData() {
        invitationVo = (InvitationVo) getIntent().getSerializableExtra("data");
        if(invitationVo != null){
            if(!invitationVo.isHasLook() && !StringUtil.isEmpty(invitationVo.getActid())){
                InvitationMsgDBUtil.getInstance().updateInvitationMsgRead(invitationVo.getActid());
            }
            String title = invitationVo.getActname();
            if(!TextUtils.isEmpty(title)){
                titleTv.setText(title);
            }
            String content = invitationVo.getActcontent();
            if(!TextUtils.isEmpty(content)){
                contentTv.setText(content);
            }
            String marketingLogoUrl = invitationVo.getActimage();
            if(!TextUtils.isEmpty(marketingLogoUrl)){
                Uri marketingLogoUri = Uri.parse(ConfigUtil.getInst().getCdnDomain()+marketingLogoUrl);
                if(null != marketingLogoUri){
                    logoDv.setImageURI(marketingLogoUri);
                }
            }
            final String buttonUrl = invitationVo.getActurl();
            openLayout.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(buttonUrl)){
                openTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(InvitationMsgActivity.this,WebViewActivity.class);
                        intent.putExtra("webview_url",buttonUrl);
                        startActivity(intent);
                    }
                });
            }
        }
        BlurBehind.getInstance()
                .withAlpha(255)
                .withBlurRadius(5)
                .setBackground(this);
    }


    private void initListener() {
        mohuRlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                if(invitationVo.getInsert_time() > 0){
                    //overridePendingTransition(R.anim.anim_small_big, R.anim.anim_big_small);
                }else{
                    overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                }

            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,InviteCreateActivity.class);
                intent.putExtra("invitationVo",invitationVo);
                startActivity(intent);
                finish();
            }
        });
    }


}
