package com.zkjinshi.svip.activity.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.nineoldandroids.view.animation.AnimatorProxy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.net.protocol.ProtocolMSG;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.bean.jsonbean.MsgUserDefine;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.vo.PushOfflineMsg;
import com.zkjinshi.svip.volley.DataRequestVolley;
import com.zkjinshi.svip.volley.HttpMethod;
import com.zkjinshi.svip.volley.RequestQueueSingleton;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import me.nereo.multi_image_selector.bean.Image;

/**
 * 输入邀请码页面
 * 开发者：WinkyQin
 * 日期：2015/11/2
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class InviteCodeActivity extends Activity {

    private ItemTitleView mTitle;
    private EditText      mEtInviteCode;
    private TextView      mTvInviteCode;
    private ImageButton   mIbtnQianJin;

    private LinearLayout  mLlSalerInfo;
    private ImageView     mCivSalerAvatar;
    private TextView      mTvSalerName;

    private DisplayImageOptions mOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);

        //进入主页面
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mTitle        = (ItemTitleView) findViewById(R.id.itv_title);
        mEtInviteCode = (EditText)      findViewById(R.id.et_invite_code);
        mTvInviteCode = (TextView)      findViewById(R.id.tv_what_is_invite_code);
        mIbtnQianJin  = (ImageButton)   findViewById(R.id.ibtn_qian_jin);

        mLlSalerInfo    = (LinearLayout)  findViewById(R.id.ll_saler_info);
        mCivSalerAvatar = (ImageView)     findViewById(R.id.civ_saler_avatar);
        mTvSalerName    = (TextView)      findViewById(R.id.tv_saler_name);
    }

    private void initData() {
        mTitle.setTextTitle("");
    }

    private void initListener() {

        mTitle.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteCodeActivity.this.finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        mEtInviteCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable inviteCode) {
                String inviteCodeStr = inviteCode.toString();
                if (inviteCodeStr.length() >= 6) {
                    //TODO 执行view动画 显示专属服务员信息
                    findSalerByInviteCode(inviteCodeStr);
                }

                if (TextUtils.isEmpty(inviteCodeStr)) {
                    showSalerInfo(false, null, null);
                }

            }
        });

        mTvInviteCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.getInstance().showToast(InviteCodeActivity.this, "TODO");
            }
        });

        mIbtnQianJin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inviteCode = mEtInviteCode.getText().toString().trim();
                if (!TextUtils.isEmpty(inviteCode)) {
                    //邀请码验证成功
                    //发送文本消息通知专属客服
                    //MsgUserDefine msgUserDefine = bindInviteCodeMsgText();

                } else {
                    Intent goHome = new Intent(InviteCodeActivity.this, MainActivity.class);
                    startActivity(goHome);
                    InviteCodeActivity.this.finish();
                }
            }
        });
    }

    /**
     * @param inviteCodeStr
     */
    private void findSalerByInviteCode(final String inviteCodeStr) {
        String url = ProtocolUtil.getEmpByInviteCodeUrl();
        DataRequestVolley getSalerVolley = new DataRequestVolley(HttpMethod.POST, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject responseObj = new JSONObject(response);
                        Boolean    isSuccess   = responseObj.getBoolean("set");
                        if(isSuccess) {
                            String userAvatar = responseObj.getString("user_avatar");
                            String salerName  = responseObj.getString("username");
                            showSalerInfo(true, userAvatar, salerName);
                        } else {
                            DialogUtil.getInstance().showCustomToast(InviteCodeActivity.this,
                                                "登录异常，请退出后重新登陆", Gravity.CENTER);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },

            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    DialogUtil.getInstance().showCustomToast(InviteCodeActivity.this,
                                                      "网络访问异常", Gravity.CENTER);
                }
            }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String userID = CacheUtil.getInstance().getUserId();
                String token  = CacheUtil.getInstance().getToken();

                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("userid", userID);
                paramMap.put("token", token);
                paramMap.put("code", inviteCodeStr);
                return paramMap;
            }
        };

        RequestQueueSingleton.getInstance(InviteCodeActivity.this).addToRequestQueue(getSalerVolley);

    }

    /**
     * 绑定邀请码
     */
    private MsgUserDefine bindInviteCodeMsgText(String content, String toID) {

        //确认绑定邀请码的通知是由何种协议
        //1.MsgUserDefine
        MsgUserDefine msgUserDefine = new MsgUserDefine();
        msgUserDefine.setType(ProtocolMSG.MSG_UserDefine);
        msgUserDefine.setChildtype(ProtocolMSG.MSG_ChildType_BindInviteCode);
        msgUserDefine.setContent(content);
        msgUserDefine.setFromid(CacheUtil.getInstance().getUserId());
        //        msgUserDefine.setProtover();
        msgUserDefine.setPushalert("");
        msgUserDefine.setPushofflinemsg(PushOfflineMsg.PUSH_MSG.getValue());
        msgUserDefine.setToid(toID);
        return msgUserDefine;
    }

    /**
     * show the saler info when input the invite code correctly
     * @param show
     */
    private void showSalerInfo(final boolean show, String avatarUrl, String salerName) {
        AnimatorSet animation = null;

        if ((mLlSalerInfo.getVisibility() == View.VISIBLE) && !show) {
            animation = new AnimatorSet();
            ObjectAnimator move = ObjectAnimator.ofFloat(mLlSalerInfo, "translationY", 0, (5/4) * mEtInviteCode.getHeight());
            ObjectAnimator fade = ObjectAnimator.ofFloat(mLlSalerInfo, "alpha", 1, 0);
            animation.playTogether(move, fade);

            if(!TextUtils.isEmpty(avatarUrl)){
                ImageLoader.getInstance().displayImage(avatarUrl, mCivSalerAvatar, mOptions);
            }

            if(!TextUtils.isEmpty(salerName)){
                mTvSalerName.setText(salerName);
            }

        } else if ((mLlSalerInfo.getVisibility() != View.VISIBLE) && show) {
            animation = new AnimatorSet();
            ObjectAnimator move = ObjectAnimator.ofFloat(mLlSalerInfo, "translationY", (5/4) * mEtInviteCode.getHeight(), 0);
            ObjectAnimator fade;
            if (mEtInviteCode.isFocused()) {
                fade = ObjectAnimator.ofFloat(mLlSalerInfo, "alpha", 0, 1);
            } else {
                fade = ObjectAnimator.ofFloat(mLlSalerInfo, "alpha", 0, 0.33f);
            }
            animation.playTogether(move, fade);
        }

        if (animation != null) {
            animation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mLlSalerInfo.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mLlSalerInfo.setVisibility(show ? View.VISIBLE : View.GONE);
                    AnimatorProxy.wrap(mLlSalerInfo).setAlpha(show ? 1 : 0);
                }
            });
            animation.start();
        }
    }

}
