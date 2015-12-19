package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.config.ConfigUtil;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.net.core.WebSocketManager;
import com.zkjinshi.base.util.DeviceUtils;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.view.CustomDialog;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.city.citylist.Setting;
import com.zkjinshi.svip.activity.mine.MineNetController;
import com.zkjinshi.svip.activity.mine.MineUiController;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.emchat.EasemobIMHelper;
import com.zkjinshi.svip.factory.UserInfoFactory;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.BaseResponse;
import com.zkjinshi.svip.response.UserInfoResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.utils.VIPContext;
import com.zkjinshi.svip.view.CircleImageView;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.view.ItemUserSettingView;
import com.zkjinshi.svip.vo.Sex;
import com.zkjinshi.svip.vo.UserInfoVo;

import java.io.File;
import java.util.HashMap;

/**
 * Created by djd on 2015/8/17.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = SettingActivity.class.getSimpleName();

    private ImageButton backIBtn;
    private TextView titleTv;
    private CircleImageView mUserIcon;//用户头像
    private RelativeLayout mItemUserIcon;//头像条目
    private ItemUserSettingView mUserPhone;//用户手机
    private ItemUserSettingView mUserSex;//用户性别
    private ItemUserSettingView mRealName;//真实姓名
    private ItemUserSettingView mTicketInfo;//发票信息
    private ItemUserSettingView mEmail;//邮箱

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();//初始化view
        initData();//初始化data加入
        initListener();
    }

    private void initView() {
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        mUserIcon        = (CircleImageView)     findViewById(R.id.civ_user_icon);
        mItemUserIcon    = (RelativeLayout)      findViewById(R.id.rl_user_icon_img);
        mUserPhone      = (ItemUserSettingView) findViewById(R.id.ius_user_phone);
        mUserSex        = (ItemUserSettingView) findViewById(R.id.ius_user_sex);
        mEmail            = (ItemUserSettingView) findViewById(R.id.ius_email);
        mTicketInfo        = (ItemUserSettingView) findViewById(R.id.ius_ticket_info);
        mRealName          = (ItemUserSettingView)findViewById(R.id.ius_real_name);

    }

    private void initListener() {
        backIBtn.setOnClickListener(this);
       // mUserIcon.setOnClickListener(this);
        findViewById(R.id.rl_user_icon_img).setOnClickListener(this);
        mItemUserIcon.setOnClickListener(this);
        mUserPhone.setOnClickListener(this);
        mUserSex.setOnClickListener(this);
        mEmail.setOnClickListener(this);
        mTicketInfo.setOnClickListener(this);
        mRealName.setOnClickListener(this);


    }

    private void initData(){
        MineUiController.getInstance().init(this);
        MineNetController.getInstance().init(this);
        backIBtn.setVisibility(View.VISIBLE);
        titleTv.setText("账户信息");


        String url = ProtocolUtil.getUserInfoUrl(CacheUtil.getInstance().getToken(),CacheUtil.getInstance().getUserId());
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
                    UserInfoResponse userInfoResponse =  new Gson().fromJson(result.rawResult, UserInfoResponse.class);
                    if(null != userInfoResponse){
                        UserInfoVo userInfoVo = UserInfoFactory.getInstance().buildUserInfoVo(userInfoResponse);
                        if(null != userInfoVo){
                            setViewData(userInfoVo);
                        }
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

    /**
     * 根据UserInfoVo设置页面数据
     * @param userInfoVo
     */
    private void setViewData(UserInfoVo userInfoVo){
        String userPhotoSuffix = userInfoVo.getUserAvatar();
        String mobilePhoneStr = userInfoVo.getMobilePhoto();
        Sex sex = userInfoVo.getSex();
        String emailStr = userInfoVo.getEmail();

        String userId = CacheUtil.getInstance().getUserId();
        String userPhotoUrl = ProtocolUtil.getAvatarUrl(userId);
        if(!TextUtils.isEmpty(userPhotoUrl)){
            MineUiController.getInstance().setUserPhoto(userPhotoUrl,mUserIcon);
        }
        if(!TextUtils.isEmpty(CacheUtil.getInstance().getUserName())){
            mRealName.setTextContent2(CacheUtil.getInstance().getUserName());
            mRealName.setTextContent2Color(R.color.light_black);
        }else{
            mRealName.setTextContent2("立即补全信息");
            mRealName.setTextContent2Color(R.color.light_yellow);
        }
        if(null != sex && sex == Sex.BOY){
            mUserSex.setTextContent2("男");
        }
        else{
            mUserSex.setTextContent2("女");
        }
        if(!TextUtils.isEmpty(emailStr)){
            mEmail.setTextContent2(emailStr);
        }
        if(!TextUtils.isEmpty(mobilePhoneStr)){
           // mUserPhone.setTextContent2(mobilePhoneStr);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MineUiController.getInstance().onActivityResult(requestCode, resultCode, data, mUserIcon);
        // 修改完成
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case Constants.FLAG_MODIFY_FINISH:
                    submitAvatar();
                    break;
                case Constants.FLAG_MODIFY_REAL_NAME:
                    mRealName.setTextContent2(data.getStringExtra("new_value"));
                    break;
                case Constants.FLAG_MODIFY_EMAIL:
                    mEmail.setTextContent2(data.getStringExtra("new_value"));
                    break;
            }

        }
    }

    //提交头像
    public void submitAvatar(){
        String url = ProtocolUtil.getUserUploadUrl();
        NetRequest httpRequest = new NetRequest(url);

        HashMap<String, String> stringMap = new HashMap<String, String>();
        HashMap<String, File>   fileMap   = new HashMap<String, File>();

        final String picPath =  CacheUtil.getInstance().getPicPath();

        if(!TextUtils.isEmpty(picPath)){
            fileMap.put("UploadForm[file]", new File(picPath));
        }
        stringMap.put("userid",CacheUtil.getInstance().getUserId());
        stringMap.put("token", CacheUtil.getInstance().getToken());
        httpRequest.setBizParamMap(stringMap);

        httpRequest.setFileMap(fileMap);
        MineNetController.getInstance().init(this);
        MineNetController.getInstance().requestSetInfoTask(httpRequest,
            new ExtNetRequestListener(SettingActivity.this) {
            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                LogUtil.getInstance().info(LogLevel.ERROR, "errorMessage:" + errorMessage);
                LogUtil.getInstance().info(LogLevel.ERROR, "errorCode:" + errorCode);
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {

                if (null != result && null != result.rawResult) {
                    LogUtil.getInstance().info(LogLevel.INFO, "rawResult:" + result.rawResult);
                    BaseResponse baseResponse = new Gson().fromJson(result.rawResult, BaseResponse.class);
                    if (null != baseResponse && baseResponse.isSet()) {
                        ImageLoader.getInstance().clearDiskCache();
                        ImageLoader.getInstance().clearMemoryCache();

                    } else {
                        DialogUtil.getInstance().showCustomToast(SettingActivity.this, "长传头像失败!", Toast.LENGTH_LONG);
                    }
                } else {
                    DialogUtil.getInstance().showCustomToast(SettingActivity.this, "长传头像失败!", Toast.LENGTH_LONG);
                }

            }
        });
    }

    //提交资料
    public void submitInfo(final String fieldKey,final String fieldValue){

        String url = ProtocolUtil.getUserUploadUrl();
        NetRequest httpRequest = new NetRequest(url);
        HashMap<String, String> stringMap = new HashMap<String, String>();
        stringMap.put("userid",CacheUtil.getInstance().getUserId());
        stringMap.put("token", CacheUtil.getInstance().getToken());
        stringMap.put(fieldKey, fieldValue);
        httpRequest.setBizParamMap(stringMap);
        MineNetController.getInstance().init(this);
        MineNetController.getInstance().requestSetInfoTask(httpRequest,
            new ExtNetRequestListener(SettingActivity.this) {
            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                LogUtil.getInstance().info(LogLevel.ERROR, "errorMessage:" + errorMessage);
                LogUtil.getInstance().info(LogLevel.ERROR, "errorCode:" + errorCode);
            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {

                if (null != result && null != result.rawResult) {
                    LogUtil.getInstance().info(LogLevel.INFO, "rawResult:" + result.rawResult);
                    BaseResponse baseResponse = new Gson().fromJson(result.rawResult, BaseResponse.class);
                    if (null != baseResponse && baseResponse.isSet()) {
                        if(fieldKey.equals("sex")){
                            CacheUtil.getInstance().setSex(fieldValue);
                        }
                    }
                }
            }
        });
    }

    //显示性别选择对话框
    private void showSexDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_sex);

        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        int width = DeviceUtils.getScreenWidth(this);

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (width*0.8); // 宽度
        // lp.height = 300; // 高度
        //lp.alpha = 0.7f; // 透明度
        dialogWindow.setAttributes(lp);
        dialog.show();
        RadioGroup group = (RadioGroup)dialog.findViewById(R.id.gendergroup);
        RadioButton mRadio1 = (RadioButton) dialog.findViewById(R.id.girl);
        RadioButton mRadio2 = (RadioButton) dialog.findViewById(R.id.boy);
        if(mUserSex.getTextContent2().toString().equals("女")){
            mRadio1.setChecked(true);
        }
        else  if(mUserSex.getTextContent2().toString().equals("男")){
            mRadio2.setChecked(true);
        }
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.girl) {
                    submitInfo("sex","0");
                    mUserSex.setTextContent2("女");
                }
                else{
                    submitInfo("sex","1");
                    mUserSex.setTextContent2("男");
                }
                dialog.cancel();
            }
        });
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            //返回
            case R.id.header_bar_btn_back:
            finish();
                break;
            //选择头像
            case R.id.rl_user_icon_img:
                MineUiController.getInstance().init(this);
                MineUiController.getInstance().showChoosePhotoDialog();
                break;
            case R.id.ius_real_name:
                intent = new Intent(SettingActivity.this,SettingItemActivity.class);
                intent.putExtra("modify_type",Constants.FLAG_MODIFY_REAL_NAME);
                intent.putExtra("title","修改姓名");
                intent.putExtra("hint","输入你的真实姓名");
                intent.putExtra("tips","该名字用于你订房时确定，不会透露给他人");
                intent.putExtra("field_key","username");
                intent.putExtra("field_value",mRealName.getTextContent2());
                startActivityForResult(intent, Constants.FLAG_MODIFY_REAL_NAME);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
                break;
            case R.id.ius_user_sex:
                showSexDialog();
                break;
            case R.id.ius_email:
                intent = new Intent(SettingActivity.this,SettingItemActivity.class);
                intent.putExtra("modify_type",Constants.FLAG_MODIFY_EMAIL);
                intent.putExtra("title","修改邮箱");
                intent.putExtra("hint","输入你的邮箱");
                intent.putExtra("tips","");
                intent.putExtra("field_key","email");
                intent.putExtra("field_value", mEmail.getTextContent2());
                startActivityForResult(intent, Constants.FLAG_MODIFY_EMAIL);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
                break;
            case R.id.ius_user_phone:
                startActivity(new Intent(SettingActivity.this,SettingPhoneActivity.class));
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
                break;
            case R.id.ius_ticket_info:
                startActivity(new Intent(SettingActivity.this,SettingTicketsActivity.class));
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
                break;

        }
    }


}
