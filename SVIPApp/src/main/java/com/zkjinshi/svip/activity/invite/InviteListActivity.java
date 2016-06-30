package com.zkjinshi.svip.activity.invite;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.ActivitySlideAdapter;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.utils.AssetUtil;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.MessageUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.slidelistview.SlideListView;
import com.zkjinshi.svip.vo.ActivityVo;
import com.zkjinshi.svip.vo.BaseResponseVo;
import com.zkjinshi.svip.vo.GetActivityListVo;
import com.zkjinshi.svip.vo.InvitationVo;
import com.zkjinshi.svip.vo.ServiceTagVo;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by dujiande on 2016/6/21.
 */
public class InviteListActivity extends BaseActivity {

    private final static String TAG = InviteListActivity.class.getSimpleName();

    private Context mContext;
    private ImageButton backIBtn;
    private TextView titleTv;

    private SlideListView slideListView;
    private ActivitySlideAdapter slideAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_list);
        mContext = this;

        initView();
        initData();
        initListener();
    }

    private void initView() {
        backIBtn = (ImageButton)findViewById(R.id.btn_back);
        titleTv = (TextView)findViewById(R.id.title_tv);
        slideListView = (SlideListView)findViewById(R.id.slv_invite_list);

    }

    private void initData() {
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("我的行程");

        slideAdapter = new ActivitySlideAdapter(this,new ArrayList<ActivityVo>());
        slideListView.setAdapter(slideAdapter);
        getActivityList("");

    }

    private void initListener() {

        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        slideAdapter.setCallBack(new ActivitySlideAdapter.CallBack() {
            @Override
            public void delete(ActivityVo itemVo) {
                cancelActivityStatus(itemVo);
            }
        });


    }

    public void onResume(){
        super.onResume();
    }

    public void test(){
        try {
            String response = AssetUtil.getContent(this,"activitylist.txt");
            GetActivityListVo getActivityListVo = new Gson().fromJson(response,GetActivityListVo.class);
            if(getActivityListVo == null){
                return;
            }
            if(getActivityListVo.getRes() == 0){
                ArrayList<ActivityVo> datas = getActivityListVo.getData();
                if(datas != null && !datas.isEmpty()){
                    slideAdapter.refresh(datas);
                }else{
                    Toast.makeText(mContext, "暂无活动行程",Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(mContext, getActivityListVo.getResDesc(),Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取活动列表
     */
    private void getActivityList(String actid){
//        if(true){
//            test();
//            return;
//        }
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            StringEntity stringEntity = new StringEntity(jsonObject.toString(),"utf-8");
            String url = ProtocolUtil.getActivityList(actid);
            client.get(mContext, url, stringEntity, "application/json", new AsyncHttpResponseHandler(){

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
                        GetActivityListVo getActivityListVo = new Gson().fromJson(response,GetActivityListVo.class);
                        if(getActivityListVo == null){
                            return;
                        }
                        if(getActivityListVo.getRes() == 0){
                            ArrayList<ActivityVo> datas = getActivityListVo.getData();
                            if(datas != null && !datas.isEmpty()){
                                slideAdapter.refresh(datas);
                            }else{
                                Toast.makeText(mContext, "暂无活动行程",Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(mContext, getActivityListVo.getResDesc(),Toast.LENGTH_SHORT).show();
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

    /**
     * 取消活动
     */
    private void cancelActivityStatus(ActivityVo activityVo){
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token", CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("actid",activityVo.getActid());
            jsonObject.put("actstatus",2);
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
                            MessageUtil.showImgToast(mContext,"取消行程成功");
                            getActivityList("");
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
