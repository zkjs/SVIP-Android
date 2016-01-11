package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.mine.MineNetController;
import com.zkjinshi.svip.activity.mine.MineUiController;
import com.zkjinshi.svip.base.BaseActivity;
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
import com.zkjinshi.svip.view.CircleImageView;
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
           // mRealName.setTextContent2Color(R.color.light_black);
        }else{
            mRealName.setTextContent2("立即补全信息");
            mRealName.setTextContent2Color(R.color.orange);
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
                        String userPhotoUrl = ProtocolUtil.getAvatarUrl(CacheUtil.getInstance().getUserId());
                        ImageLoader.getInstance().getDiskCache().remove(userPhotoUrl);
                        ImageLoader.getInstance().getMemoryCache().remove(userPhotoUrl);

                    } else {
                        DialogUtil.getInstance().showCustomToast(SettingActivity.this, "上传头像失败!", Toast.LENGTH_LONG);
                    }
                } else {
                    DialogUtil.getInstance().showCustomToast(SettingActivity.this, "上传头像失败!", Toast.LENGTH_LONG);
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
                            if(fieldValue.equals("0")){
                                mUserSex.setTextContent2("女");
                            }else{
                                mUserSex.setTextContent2("男");
                            }
                        }
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

        setSexText(boyBtn,girlBtn,CacheUtil.getInstance().getSex());

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
                intent.putExtra("tips","该姓名用于您预订服务时使用,不会透露给他人");
                intent.putExtra("field_key","username");
                intent.putExtra("field_value",mRealName.getTextContent2());
                startActivityForResult(intent, Constants.FLAG_MODIFY_REAL_NAME);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
                break;
            case R.id.ius_user_sex:
                showChooseSexDialog();
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
