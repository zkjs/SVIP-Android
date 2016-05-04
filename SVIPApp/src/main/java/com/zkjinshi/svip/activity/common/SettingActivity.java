package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.pyxis.bluetooth.NetBeaconVo;
import com.zkjinshi.svip.R;

import com.zkjinshi.svip.base.BaseActivity;

import com.zkjinshi.svip.blueTooth.BlueToothManager;
import com.zkjinshi.svip.utils.AsyncHttpClientUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;

import com.zkjinshi.svip.view.ItemCbxView;
import com.zkjinshi.svip.view.ItemShowView;
import com.zkjinshi.svip.vo.GetUserInfoVo;
import com.zkjinshi.svip.vo.UserInfoVo;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by djd on 2015/8/17.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = SettingActivity.class.getSimpleName();

    public static int ismodifyimage = 1;//是否可以修改头像-- 0 可以修改  1 不能修改
    public static int ismodifyusername = 1;//是否可以修改姓名-- 0 可以修改  1 不能修改
    public static String email = "";


    private ImageButton backIBtn;
    private TextView titleTv;
    private SimpleDraweeView mUserIcon;//用户头像
    private RelativeLayout mItemUserIcon;//头像条目
    private ItemShowView mUserSex;//用户性别
    private ItemShowView mRealName;//真实姓名
    private ItemShowView mEmail;//邮箱
    private ItemCbxView sendCbx; //推送开关
    private ItemCbxView serviceCbx; //服务开关

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mContext = this;
        initView();//初始化view
        initData();//初始化data加入
        initListener();
    }

    private void initView() {
        backIBtn = (ImageButton)findViewById(R.id.btn_back);
        titleTv = (TextView)findViewById(R.id.title_tv);
        mUserIcon        = (SimpleDraweeView)     findViewById(R.id.civ_user_icon);
        mItemUserIcon    = (RelativeLayout)      findViewById(R.id.rl_user_icon_img);
        mUserSex        = (ItemShowView) findViewById(R.id.ius_user_sex);
        mEmail            = (ItemShowView) findViewById(R.id.ius_email);
        sendCbx        = (ItemCbxView) findViewById(R.id.icv_send);
        mRealName          = (ItemShowView)findViewById(R.id.ius_real_name);
        serviceCbx = (ItemCbxView) findViewById(R.id.icv_service);

    }

    private void initListener() {
        backIBtn.setOnClickListener(this);
        findViewById(R.id.rl_user_icon_img).setOnClickListener(this);
        mItemUserIcon.setOnClickListener(this);
        mUserSex.setOnClickListener(this);
        mEmail.setOnClickListener(this);
        mRealName.setOnClickListener(this);


        sendCbx.valueCbx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isOpen = sendCbx.valueCbx.isChecked();
                if(isOpen){
                    submitInfo("silentmode","0");
                }else{
                    submitInfo("silentmode","1");
                }
            }
        });

        serviceCbx.valueCbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isOpen) {
                if(isOpen){
                    CacheUtil.getInstance().setServiceSwitch(true);
                    BlueToothManager.getInstance().startIBeaconService(new ArrayList<NetBeaconVo>());
                }else{
                    CacheUtil.getInstance().setServiceSwitch(false);
                    BlueToothManager.getInstance().stopIBeaconService();
                }
            }
        });

    }

    private void initData(){
        MineUiController.getInstance().init(this);
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("账户信息");

        mUserIcon.setImageURI(Uri.parse(CacheUtil.getInstance().getUserPhotoUrl()));
        if(CacheUtil.getInstance().getSex().equals("0")){
            mUserSex.setValue("女");
        }else if(CacheUtil.getInstance().getSex().equals("1")){
            mUserSex.setValue("男");
        }
        mRealName.setValue(CacheUtil.getInstance().getUserName());
        mEmail.setValue(email);

        if(CacheUtil.getInstance().isBLESwitch()){
            sendCbx.valueCbx.setChecked(true);
        }else{
            sendCbx.valueCbx.setChecked(false);
        }

        if(CacheUtil.getInstance().isServiceSwitch()){
            serviceCbx.valueCbx.setChecked(true);
        }else{
            serviceCbx.valueCbx.setChecked(false);
        }

        refreshUserInfo();
    }


    private void refreshUserInfo(){
        try{
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constants.OVERTIMEOUT);
            client.addHeader("Content-Type","application/json; charset=UTF-8");
            client.addHeader("Token",CacheUtil.getInstance().getExtToken());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userids",CacheUtil.getInstance().getUserId());
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            final String url = ProtocolUtil.querySiAll();
            client.get(mContext,url, stringEntity, "application/json", new AsyncHttpResponseHandler(){
                public void onStart(){
                    DialogUtil.getInstance().showAvatarProgressDialog(mContext,"");
                }

                public void onFinish(){
                    DialogUtil.getInstance().cancelProgressDialog();
                }

                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                    try {
                        String response = new String(responseBody,"utf-8");
                        GetUserInfoVo getUserInfoVo = new Gson().fromJson(response,GetUserInfoVo.class);
                        if (getUserInfoVo == null){
                            return;
                        }
                        if(getUserInfoVo.getRes() == 0){
                            if(getUserInfoVo.getData() != null && getUserInfoVo.getData().size() > 0){
                                UserInfoVo userInfoVo = getUserInfoVo.getData().get(0);
                                CacheUtil.getInstance().setUserPhone(userInfoVo.getPhone());
                                CacheUtil.getInstance().setUserName(userInfoVo.getUsername());
                                String imgurl = userInfoVo.getUserimage();
                                //imgurl = ProtocolUtil.getHostImgUrl(imgurl);
                                imgurl = ProtocolUtil.getAvatarUrl(mContext,imgurl);
                                CacheUtil.getInstance().saveUserPhotoUrl(imgurl);
                                CacheUtil.getInstance().setSex(userInfoVo.getSex()+"");
                                CacheUtil.getInstance().setUserRealName(userInfoVo.getRealname());
                                CacheUtil.getInstance().setUserApplevel(userInfoVo.getViplevel());
                                SettingActivity.email = userInfoVo.getEmail();
                                SettingActivity.ismodifyimage = userInfoVo.getIsmodifyimage();
                                SettingActivity.ismodifyusername = userInfoVo.getIsmodifyusername();

                                mUserIcon.setImageURI(Uri.parse(imgurl));
                                if(userInfoVo.getSex() == -1){
                                    mUserSex.setValue("");
                                }else if(userInfoVo.getSex() == 0){
                                    mUserSex.setValue("女");
                                }else if(userInfoVo.getSex() == 1){
                                    mUserSex.setValue("男");
                                }
                                mEmail.setValue(userInfoVo.getEmail());
                                mRealName.setValue(userInfoVo.getUsername());

                                if(userInfoVo.getSilentmode() == 0){
                                    CacheUtil.getInstance().setBleSwitch(true);
                                    sendCbx.valueCbx.setChecked(true);

                                }else{
                                    CacheUtil.getInstance().setBleSwitch(false);
                                    sendCbx.valueCbx.setChecked(false);
                                }

                            }
                        }else{
                            Toast.makeText(mContext,getUserInfoVo.getResDesc(),Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                    AsyncHttpClientUtil.onFailure(mContext,statusCode);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MineUiController.getInstance().onActivityResult(requestCode, resultCode, data, mUserIcon,null);
        // 修改完成
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case Constants.FLAG_MODIFY_FINISH:
                    submitAvatar();
                    break;
                case Constants.FLAG_MODIFY_REAL_NAME:
                    mRealName.setValue(data.getStringExtra("new_value"));
                    refreshUserInfo();
                    break;
                case Constants.FLAG_MODIFY_EMAIL:
                    mEmail.setValue(data.getStringExtra("new_value"));
                    refreshUserInfo();
                    break;
            }

        }
    }

    //提交头像
    public void submitAvatar(){
        final String picPath =  CacheUtil.getInstance().getPicPath();
        MineNetController.getInstance().submitInfo(this, "image", picPath, new MineNetController.CallBackListener() {
            @Override
            public void successCallback(JSONObject response) {
                String imgurl = null;
                try {
                    imgurl = response.getString("userimage");
                    //imgurl = ProtocolUtil.getHostImgUrl(imgurl);
                    imgurl = ProtocolUtil.getAvatarUrl(mContext,imgurl);
                    CacheUtil.getInstance().saveUserPhotoUrl(imgurl);
                    ismodifyimage = 1;
                    refreshUserInfo();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            public void failCallBack(){

            }
        });

    }

    //提交资料
    public void submitInfo(final String fieldKey,final String fieldValue){

        MineNetController.getInstance().submitInfo(this, fieldKey, fieldValue, new MineNetController.CallBackListener() {
            @Override
            public void successCallback(JSONObject response) {
                if(fieldKey.equals("sex")){
                    CacheUtil.getInstance().setSex(fieldValue);
                    if(fieldValue.equals("0")){
                        mUserSex.setValue("女");
                    }else{
                        mUserSex.setValue("男");
                    }
                }else if(fieldKey.equals("silentmode")){
                    if(fieldValue.equals("0")){
                        sendCbx.valueCbx.setChecked(true);
                        CacheUtil.getInstance().setBleSwitch(true);
                    }else{
                        sendCbx.valueCbx.setChecked(false);
                        CacheUtil.getInstance().setBleSwitch(false);
                    }
                }
            }
            public void failCallBack(){
                if(fieldKey.equals("silentmode")){
                    if(fieldValue.equals("0")){
                        sendCbx.valueCbx.setChecked(false);
                        CacheUtil.getInstance().setBleSwitch(false);
                    }else{
                        sendCbx.valueCbx.setChecked(true);
                        CacheUtil.getInstance().setBleSwitch(true);
                    }
                }
            }
        });
    }



    /**
     * 显示性别选择对话框
     */
    public void showChooseSexDialog(){

        final Dialog dlg = new Dialog(this, R.style.ActionTheme_DataSheet);
        LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.set_sex_dialog, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        final Button boyBtn = (Button) layout.findViewById(R.id.dialog_btn_boy);
        final Button girlBtn = (Button) layout.findViewById(R.id.dialog_btn_girl);
        Button cancelBtn = (Button) layout.findViewById(R.id.dialog_btn_cancel);

        setSexText(boyBtn,girlBtn, CacheUtil.getInstance().getSex());

        //男
        boyBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                submitInfo("sex","1");
                setSexText(boyBtn,girlBtn,"1");
                dlg.cancel();
            }
        });
        //女
        girlBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                submitInfo("sex","0");
                setSexText(boyBtn,girlBtn,"0");
                dlg.cancel();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);
        dlg.show();

    }

    private void setSexText(Button boyBtn, Button girlBtn,String sexValue){
        if(sexValue.equals("0")){
            boyBtn.setTextColor(Color.parseColor("#454546"));
            girlBtn.setTextColor(Color.parseColor("#ffc56e"));
        }else{
            boyBtn.setTextColor(Color.parseColor("#ffc56e"));
            girlBtn.setTextColor(Color.parseColor("#454546"));
        }
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            //返回
            case R.id.btn_back:
                finish();
                break;
            //选择头像
            case R.id.rl_user_icon_img:
                if(ismodifyimage == 1){
                    Toast.makeText(mContext,"每月只能修改一次头像",Toast.LENGTH_SHORT).show();
                    return;
                }
                MineUiController.getInstance().init(this);
                MineUiController.getInstance().showChoosePhotoDialog();
                break;
            case R.id.ius_real_name:
                if(ismodifyusername == 1){
                    Toast.makeText(mContext,"姓名只能改一次",Toast.LENGTH_SHORT).show();
                    return;
                }
                intent = new Intent(SettingActivity.this,SettingItemActivity.class);
                intent.putExtra("title","修改姓名");
                intent.putExtra("hint","输入你的真实姓名");
                intent.putExtra("tips","姓名只能改一次,请谨慎修改。");
                intent.putExtra("field_key","username");
                intent.putExtra("field_value",mRealName.getValue());
                startActivityForResult(intent, Constants.FLAG_MODIFY_REAL_NAME);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
                break;
            case R.id.ius_user_sex:
                showChooseSexDialog();
                break;
            case R.id.ius_email:
                intent = new Intent(SettingActivity.this,SettingItemActivity.class);
                intent.putExtra("title","修改邮箱");
                intent.putExtra("hint","输入你的邮箱");
                intent.putExtra("tips","");
                intent.putExtra("field_key","email");
                intent.putExtra("field_value", mEmail.getValue());
                startActivityForResult(intent, Constants.FLAG_MODIFY_EMAIL);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
                break;

        }
    }


}
