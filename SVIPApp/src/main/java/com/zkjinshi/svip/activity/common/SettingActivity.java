package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.mine.MineNetController;
import com.zkjinshi.svip.activity.mine.MineUiController;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.ItemUserSettingView;

import org.json.JSONObject;

/**
 * Created by djd on 2015/8/17.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = SettingActivity.class.getSimpleName();

    private ImageButton backIBtn;
    private TextView titleTv;
    private SimpleDraweeView mUserIcon;//用户头像
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

    public void onResume(){
        super.onResume();
        if(!TextUtils.isEmpty(CacheUtil.getInstance().getUserName())){
            mRealName.setTextContent2(CacheUtil.getInstance().getUserName());
        }else{
            mRealName.setTextContent2("立即补全信息");
            mRealName.setTextContent2Color(R.color.orange);
        }
        if(CacheUtil.getInstance().getSex().equals("1")){
            mUserSex.setTextContent2("男");
        }
        else{
            mUserSex.setTextContent2("女");
        }
    }

    private void initView() {
        backIBtn = (ImageButton)findViewById(R.id.header_bar_btn_back);
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        mUserIcon        = (SimpleDraweeView)     findViewById(R.id.civ_user_icon);
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
        LoginController.getInstance().getUserInfo(this, CacheUtil.getInstance().getUserId(), new LoginController.CallBackListener() {
            @Override
            public void successCallback(JSONObject response) {
                try{
                    JSONObject data = response;
                    String emailStr = data.getString("email");
                    String userPhotoUrl = CacheUtil.getInstance().getUserPhotoUrl();
                    if(!TextUtils.isEmpty(userPhotoUrl)){
                        mUserIcon.setImageURI(Uri.parse(userPhotoUrl));
                    }
                    if(!TextUtils.isEmpty(CacheUtil.getInstance().getUserName())){
                        mRealName.setTextContent2(data.getString("username"));
                    }else{
                        mRealName.setTextContent2("立即补全信息");
                        mRealName.setTextContent2Color(R.color.orange);
                    }
                    if(data.getString("sex").equals("1")){
                        mUserSex.setTextContent2("男");
                    }
                    else{
                        mUserSex.setTextContent2("女");
                    }
                    if(!TextUtils.isEmpty(emailStr)){
                        mEmail.setTextContent2(emailStr);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
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
        final String picPath =  CacheUtil.getInstance().getPicPath();
        MineNetController.getInstance().submitInfo(this,"image", picPath, new MineNetController.CallBackListener() {
            @Override
            public void successCallback(JSONObject dataJson) {
                try{
                    String imgurl = dataJson.getString("userimage");
                    imgurl = ProtocolUtil.getHostImgUrl(imgurl);
                    CacheUtil.getInstance().saveUserPhotoUrl(imgurl);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

    }

    //提交资料
    public void submitInfo(final String fieldKey,final String fieldValue){

        MineNetController.getInstance().submitInfo(this,fieldKey, fieldValue, new MineNetController.CallBackListener() {
            @Override
            public void successCallback(JSONObject dataJson) {
                if(fieldKey.equals("sex")){
                    CacheUtil.getInstance().setSex(fieldValue);
                    if(fieldValue.equals("0")){
                        mUserSex.setTextContent2("女");
                    }else{
                        mUserSex.setTextContent2("男");
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
