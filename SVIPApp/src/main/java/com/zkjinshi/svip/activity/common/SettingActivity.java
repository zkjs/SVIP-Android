package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.mine.MineNetController;
import com.zkjinshi.svip.activity.mine.MineUiController;
import com.zkjinshi.svip.factory.UserInfoFactory;
import com.zkjinshi.svip.response.UserInfoResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.CircleImageView;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.view.ItemUserSettingView;
import com.zkjinshi.svip.vo.Sex;
import com.zkjinshi.svip.vo.UserInfoVo;

/**
 * Created by djd on 2015/8/17.
 */
public class SettingActivity extends Activity implements View.OnClickListener {

    private ItemTitleView mTitle;//返回
    private CircleImageView mUserIcon;//用户头像
    private RelativeLayout mItemUserIcon;//头像条目
    private ItemUserSettingView mUserName;//用户昵称
    private ItemUserSettingView mUserPhone;//用户手机
    private ItemUserSettingView mUserSex;//用户性别
    private ItemUserSettingView mRealName;//真实姓名
    private ItemUserSettingView mLabelTaste;//口味标签
    private ItemUserSettingView mTicketInfo;//发票信息
    private ItemUserSettingView mCompany;//单位
    private ItemUserSettingView mEmail;//邮箱

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_setting);

        initView();//初始化view
        initData();//初始化data加入
        initListener();
    }

    private void initView() {
        mTitle            = (ItemTitleView)       findViewById(R.id.itv_setting_title);
        mUserIcon        = (CircleImageView)     findViewById(R.id.civ_user_icon);
        mItemUserIcon    = (RelativeLayout)      findViewById(R.id.rl_user_icon_img);
        mUserName         = (ItemUserSettingView) findViewById(R.id.ius_user_name);
        mUserPhone      = (ItemUserSettingView) findViewById(R.id.ius_user_phone);
        mUserSex        = (ItemUserSettingView) findViewById(R.id.ius_user_sex);
        mCompany        = (ItemUserSettingView) findViewById(R.id.ius_company);
        mLabelTaste     = (ItemUserSettingView) findViewById(R.id.ius_fan_label);
        mEmail            = (ItemUserSettingView) findViewById(R.id.ius_email);
        mTicketInfo        = (ItemUserSettingView) findViewById(R.id.ius_ticket_info);
        mRealName          = (ItemUserSettingView)findViewById(R.id.ius_real_name);
    }

    private void initListener() {
        mTitle.getmLeft().setOnClickListener(this);
        mUserIcon.setOnClickListener(this);
        mItemUserIcon.setOnClickListener(this);
        mUserName.setOnClickListener(this);
        mUserPhone.setOnClickListener(this);
        mUserSex.setOnClickListener(this);
        mCompany.setOnClickListener(this);
        mLabelTaste.setOnClickListener(this);
        mEmail.setOnClickListener(this);
        mTicketInfo.setOnClickListener(this);
        mRealName.setOnClickListener(this);
    }

    private void initData(){
        MineUiController.getInstance().init(this);
        MineNetController.getInstance().init(this);
        mTitle.getmRight().setVisibility(View.INVISIBLE);
        mTitle.setTextTitle("设置");

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                ProtocolUtil.getUserInfoUrl(CacheUtil.getInstance().getToken(),
                        CacheUtil.getInstance().getUserId()),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        DialogUtil.getInstance().cancelProgressDialog();
                        LogUtil.getInstance().info(LogLevel.INFO, "获取用户信息响应结果:" + response);
                        if(!TextUtils.isEmpty(response)){
                            try {
                                UserInfoResponse userInfoResponse =  new Gson().fromJson(response, UserInfoResponse.class);
                                if(null != userInfoResponse){
                                    UserInfoVo userInfoVo = UserInfoFactory.getInstance().buildUserInfoVo(userInfoResponse);
                                    if(null != userInfoVo){
                                       setViewData(userInfoVo);
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();;
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtil.getInstance().cancelProgressDialog();
                LogUtil.getInstance().info(LogLevel.INFO, "获取用户信息错误信息:" +  error.getMessage());
            }
        });
        MineNetController.getInstance().requestGetUserInfoTask(stringRequest);
    }

    /**
     * 根据UserInfoVo设置页面数据
     * @param userInfoVo
     */
    private void setViewData(UserInfoVo userInfoVo){
        String userPhotoSuffix = userInfoVo.getUserAvatar();
        String userNameStr = userInfoVo.getUsername();
        String mobilePhoneStr = userInfoVo.getMobilePhoto();
        String positionStr = userInfoVo.getPosition();
        String companyStr = userInfoVo.getCompany();
        String realNameStr = userInfoVo.getRealName();
        Sex sex = userInfoVo.getSex();
        String emailStr = userInfoVo.getEmail();

        if(!TextUtils.isEmpty(userPhotoSuffix)){
            String userPhotoUrl = ConfigUtil.getInst().getHttpDomain()+userPhotoSuffix;
            CacheUtil.getInstance().saveUserPhotoUrl(userPhotoUrl);
            MineUiController.getInstance().setUserPhoto(userPhotoUrl,mUserIcon);
        }
        if(!TextUtils.isEmpty(realNameStr)){
            mRealName.setTextContent2(realNameStr);
        }
        if(!TextUtils.isEmpty(userNameStr)){
            mUserName.setTextContent2(userNameStr);
        }
        if(null != sex && sex == Sex.BOY){
            mUserSex.setTextContent2("男");
        }
        else{
            mUserSex.setTextContent2("女");
        }
        if(!TextUtils.isEmpty(companyStr)){
            mCompany.setTextContent2(companyStr);
        }
        if(!TextUtils.isEmpty(emailStr)){
            mEmail.setTextContent2(emailStr);
        }
        if(!TextUtils.isEmpty(mobilePhoneStr)){
            mUserPhone.setTextContent2(mobilePhoneStr);
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_left:
            finish();
                break;
            case R.id.ius_real_name:
                break;
            case R.id.ius_user_name:
                break;
            case R.id.ius_user_sex:
                break;
            case R.id.ius_company:
                break;
            case R.id.ius_email:
                break;
            case R.id.ius_user_phone:
                break;
            case R.id.ius_ticket_info:
                break;

        }
    }
}
