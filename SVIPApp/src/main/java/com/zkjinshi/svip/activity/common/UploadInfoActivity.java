package com.zkjinshi.svip.activity.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.FastBlur;
import com.zkjinshi.svip.vo.BaseResponseVo;

/**
 * Created by dujiande on 2016/3/8.
 */
public class UploadInfoActivity extends BaseActivity {

    private EditText inputEt;
    private Button nextBtn;

    private Context mContext;
    private View maskView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_info);

        mContext = this;

        initView();
        initData();
        initListener();
    }

    private void initView() {
        inputEt = (EditText)findViewById(R.id.inputEt);
        nextBtn = (Button) findViewById(R.id.btn_next);
        maskView = findViewById(R.id.mask_view);
    }

    private void initData() {

    }

    private void initListener() {
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameStr = inputEt.getText().toString();
                if(TextUtils.isEmpty(nameStr)){
                    Toast.makeText(mContext,"用户名不能为空。",Toast.LENGTH_SHORT).show();
                    return;
                }
                CacheUtil.getInstance().setUserName(nameStr);
                Intent intent = new Intent(mContext,UploadAvatarActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.info_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(mContext,AlertActivity.class);
//                intent.putExtra("title","温馨提示");
//                intent.putExtra("content","在用户注册及使用本服务时，超级身份开发中心需要搜集能识别用户身份的个人信息以便为用户提供更好的使用体验。超级身份开发中心搜集的信息包括但不限于用户的姓名、头像；超级身份开发中心同意对这些信息的使用将受限于第三条用户个人隐私信息保护的约束。");
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                showPopupWindow(view);

            }
        });
    }

    private void showPopupWindow(View view) {
        maskView.setVisibility(View.VISIBLE);

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mContext).inflate( R.layout.pop_window, null);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                AbsoluteLayout.LayoutParams.WRAP_CONTENT, AbsoluteLayout.LayoutParams.WRAP_CONTENT, true);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
       //popupWindow.setBackgroundDrawable(getResources().getDrawable(R.mipmap.bg_tishikuang));
        ColorDrawable color = new ColorDrawable(Color.parseColor("#8f101010"));
        popupWindow.setBackgroundDrawable(color);
        // 设置好参数之后再show
        int offsetX = DisplayUtil.dip2px(this,200);
        int offsetY = DisplayUtil.dip2px(this,40);
        popupWindow.showAsDropDown(view,-offsetX,-offsetY);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                maskView.setVisibility(View.GONE);
            }
        });
    }

}
