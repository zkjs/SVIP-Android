package com.zkjinshi.svip.activity.im.group;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.SoftInputUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.im.group.actions.FaceViewPagerManager;
import com.zkjinshi.svip.activity.im.group.actions.MessageListViewManager;
import com.zkjinshi.svip.activity.im.group.actions.MoreViewPagerManager;
import com.zkjinshi.svip.activity.im.group.actions.NetCheckManager;
import com.zkjinshi.svip.activity.im.group.actions.QuickMenuManager;
import com.zkjinshi.svip.activity.im.group.actions.VoiceRecordManager;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.bean.BookOrder;
import com.zkjinshi.svip.response.OrderDetailResponse;
import com.zkjinshi.svip.response.OrderRoomResponse;
import com.zkjinshi.svip.response.OrderUsersResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.FileUtil;
import com.zkjinshi.svip.utils.MediaPlayerUtil;
import com.zkjinshi.svip.view.ItemTitleView;
import com.zkjinshi.svip.vo.OrderDetailForDisplay;

import java.io.File;
import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * 群聊Activity
 * 开发者：JimmyZhang
 * 日期：2015/11/28
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatGroupActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener{

    private final static String TAG = ChatGroupActivity.class.getSimpleName();

    private String  textContext;
    private OrderDetailForDisplay orderDetailForDisplay;

    private ItemTitleView titleIv;
    private EditText mMsgTextInput;
    private Button mBtnMsgSend;

    private MessageListViewManager messageListViewManager;
    private boolean                isShowSoftInput;//是否展示软件盘
    private boolean isVoiceShow = false;//是否显示语音
    private LinearLayout faceLinearLayout, moreLinearLayout;
    private FaceViewPagerManager facePagerManager;
    private MoreViewPagerManager moreViewPagerManager;
    private VoiceRecordManager voiceRecordManager;
    private NetCheckManager netCheckManager;
    private CheckBox faceCb, moreCb;
    private ImageButton toggleAudioBtn;//切换到录音按钮
    private TextView startAudioBtn;//开始录音
    private RelativeLayout animAreaLayout, cancelAreaLayout; // 录音中View，取消录音View
    private int            flag = 1; // 1：正常 2：语音录音中
    private long           startVoiceT, endVoiceT; // 语音开始时间，结束时间
    private String            choosePicName;//选择图片名称
    private ArrayList<String> chooseImageList = new ArrayList<String>();
    private String bookOrderStr;
    private ArrayList<OrderUsersResponse> users;
    private String filePath;
    private int voiceTime;
	private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        titleIv = (ItemTitleView) findViewById(R.id.Itv_title);
        mMsgTextInput = (EditText) findViewById(R.id.et_msg_text_input);
        mBtnMsgSend   = (Button)   findViewById(R.id.btn_msg_send);
        faceLinearLayout = (LinearLayout) findViewById(R.id.face_ll);
        moreLinearLayout = (LinearLayout) findViewById(R.id.more_ll);
        faceCb = (CheckBox) findViewById(R.id.face_cb);
        moreCb = (CheckBox) findViewById(R.id.more_cb);
        toggleAudioBtn = (ImageButton) findViewById(R.id.voice_btn);
        startAudioBtn  = (TextView) findViewById(R.id.say_btn);
        animAreaLayout   = (RelativeLayout) findViewById(R.id.voice_rcd_hint_anim_area);
        cancelAreaLayout = (RelativeLayout) findViewById(R.id.voice_rcd_hint_cancel_area);
    }

    private void initData() {

        titleIv.setTextTitle("聊天");
        titleIv.setTextColor(this, R.color.Black);
         if(!TextUtils.isEmpty(getIntent().getStringExtra("groupId"))){
            groupId = getIntent().getStringExtra("groupId");
        }
        if(!TextUtils.isEmpty(getIntent().getStringExtra("filePath"))){
            filePath = getIntent().getStringExtra("filePath");
        }
        voiceTime = getIntent().getIntExtra("voiceTime",1);
        //初始化消息ListView管理器
        messageListViewManager = new MessageListViewManager(this, groupId);
        messageListViewManager.init();
        messageListViewManager.setTitle(titleIv);
        //初始化表情框
        facePagerManager = new FaceViewPagerManager(this, faceLinearLayout, mMsgTextInput);
        facePagerManager.init();
        //初始化更多框
        moreViewPagerManager = new MoreViewPagerManager(this, moreLinearLayout);
        moreViewPagerManager.init();
        //初始化录音管理器
        voiceRecordManager = new VoiceRecordManager(this, animAreaLayout, cancelAreaLayout);
        voiceRecordManager.init();
        voiceRecordManager.setMessageListViewManager(messageListViewManager);
        //初始化网络状态管理器
        netCheckManager = new NetCheckManager();
        netCheckManager.init(this);
        netCheckManager.registernetCheckReceiver();
        //初始化快捷菜单
        QuickMenuManager.getInstance().init(this).setMessageListViewManager(messageListViewManager);
        if(null!= getIntent() && null != getIntent().getSerializableExtra("orderDetailForDisplay")){
            orderDetailForDisplay = (OrderDetailForDisplay) getIntent().getSerializableExtra("orderDetailForDisplay");
            if (null != orderDetailForDisplay) {
                orderDetailForDisplay.setContent("您好，帮我预定这间房");
                try {
                    bookOrderStr = new Gson().toJson(orderDetailForDisplay);
                    if (!TextUtils.isEmpty(bookOrderStr)) {
                        messageListViewManager.sendCardMessage(bookOrderStr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //自动发送文本消息
        if(null != getIntent() && null != getIntent().getStringExtra("text_context")){
            textContext = getIntent().getStringExtra("text_context");
            if(!TextUtils.isEmpty(textContext)){
                messageListViewManager.sendTextMessage(textContext);
            }
        }
        //发送语音消息
        if(!TextUtils.isEmpty(filePath)){
            messageListViewManager.sendVoiceMessage(filePath,voiceTime);
        }
    }

    private void initListener() {
        titleIv.getmLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatGroupActivity.this.finish();
            }
        });
        titleIv.getmRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatGroupActivity.this.finish();
            }
        });
        faceCb.setOnCheckedChangeListener(this);
        moreCb.setOnCheckedChangeListener(this);
        //输入文本控件touch监听
        mMsgTextInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideFaceLayout();
                hideMoreLayout();
                showSoftInput();
                return false;
            }
        });
        //语音键盘切换监听
        toggleAudioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isVoiceShow) {
                    toggleAudioBtn.setImageResource(R.drawable.aio_keyboard);
                    if (moreCb.isChecked()) {
                        Drawable restdrawable = getResources().getDrawable(
                                R.drawable.aio_fold);
                        restdrawable.setBounds(0, 0,
                                restdrawable.getMinimumWidth(),
                                restdrawable.getMinimumHeight());
                        moreCb.setCompoundDrawables(restdrawable, null, null, null);
                    }
                    if (faceCb.isChecked()) {
                        Drawable restdrawable = getResources().getDrawable(
                                R.drawable.aio_favorite);
                        restdrawable.setBounds(0, 0,
                                restdrawable.getMinimumWidth(),
                                restdrawable.getMinimumHeight());
                        faceCb.setCompoundDrawables(restdrawable, null, null, null);
                    }
                    mMsgTextInput.setVisibility(View.GONE);
                    faceCb.setVisibility(View.VISIBLE);
                    startAudioBtn.setVisibility(View.VISIBLE);
                    hideFaceLayout();
                    hideMoreLayout();
                    hideSoftInput();
                    isVoiceShow = true;
                } else {
                    toggleAudioBtn.setImageResource(R.drawable.aio_voice);
                    mMsgTextInput.setVisibility(View.VISIBLE);
                    faceCb.setVisibility(View.VISIBLE);
                    startAudioBtn.setVisibility(View.GONE);
                    showSoftInput();
                    isVoiceShow = false;
                }
            }
        });
        //文本控件输入监听
        mMsgTextInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mBtnMsgSend.setEnabled(true);
                    mBtnMsgSend.setVisibility(VISIBLE);
                    moreCb.setVisibility(GONE);
                } else {
                    mBtnMsgSend.setEnabled(false);
                    mBtnMsgSend.setVisibility(GONE);
                    moreCb.setVisibility(VISIBLE);
                }
            }
        });
        mBtnMsgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = mMsgTextInput.getText().toString();
                if (!TextUtils.isEmpty(msg) && msg.length() < 200) {
                    messageListViewManager.sendTextMessage(msg);
                    mMsgTextInput.setText("");
                } else {
                    DialogUtil.getInstance().showCustomToast(ChatGroupActivity.this, "发送消息不能超过200字符!", Gravity.CENTER);
                }
            }
        });
        // 触摸ListView隐藏表情和输入法
        messageListViewManager.getMessageListView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    resetKeyboard();
                }
                return false;
            }
        });
        //录音事件交给父控件处理
        startAudioBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    public void resetKeyboard(){
        hideFaceLayout();
        hideMoreLayout();
        hideSoftInput();
        Drawable plusawable = getResources().getDrawable(
                R.drawable.aio_fold);
        plusawable.setBounds(0, 0, plusawable.getMinimumWidth(),
                plusawable.getMinimumHeight());
        moreCb.setCompoundDrawables(plusawable, null, null, null);
        Drawable facedrawable = getResources().getDrawable(
                R.drawable.aio_favorite);
        facedrawable.setBounds(0, 0,
                facedrawable.getMinimumWidth(),
                facedrawable.getMinimumHeight());
        faceCb.setCompoundDrawables(facedrawable, null, null, null);
    }

    @Override
    public void onBackPressed() {
        if (!faceCb.isChecked() && !moreCb.isChecked()) {
            finish();
        } else {
            hideFaceLayout();
            hideMoreLayout();
        }
    }

    /**
     * 隐藏表情区域
     */
    private void hideFaceLayout() {
        faceLinearLayout.setVisibility(GONE);
        faceCb.setOnCheckedChangeListener(null);
        faceCb.setChecked(false);
        faceCb.setOnCheckedChangeListener(ChatGroupActivity.this);
    }

    /**
     * 隐藏更多区域
     */
    private void hideMoreLayout() {
        moreLinearLayout.setVisibility(GONE);
        moreCb.setOnCheckedChangeListener(null);
        moreCb.setChecked(false);
        moreCb.setOnCheckedChangeListener(ChatGroupActivity.this);
    }

    /**
     * 隐藏软键盘
     */
    private void hideSoftInput() {
        isShowSoftInput = false;
        SoftInputUtil.hideSoftInputMode(this, mMsgTextInput);
    }

    /**
     * 显示软键盘并显示最底部消息
     */
    private void showSoftInput() {
        isShowSoftInput = true;
        mMsgTextInput.requestFocus();// 强制显示软键盘
        SoftInputUtil.showSoftInputMode(this, mMsgTextInput);
        messageListViewManager.scrollBottom();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.FLAG_CHOOSE_IMG) {//选择图片
                chooseImageList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (null != chooseImageList && !chooseImageList.isEmpty()) {
                    for (String imagePath : chooseImageList) {
                        Log.i(TAG, "imagePath:" + imagePath);
                        String imageName = FileUtil.getInstance().getFileName(imagePath);
                        messageListViewManager.sendImageMessage(imagePath);
                    }
                }
            } else if (requestCode == Constants.FLAG_CHOOSE_PHOTO) {//拍照上传
                choosePicName = CacheUtil.getInstance().getPicName();
                String imageTempPath = FileUtil.getInstance().getImageTempPath();
                messageListViewManager.sendImageMessage(
                        imageTempPath + choosePicName);
                Log.i(TAG, "imagePath:" + FileUtil.getInstance().getImageTempPath() + choosePicName);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        messageListViewManager.destoryMessageListViewManager();
        MediaPlayerUtil.stop();
        if(null != netCheckManager){
            netCheckManager.unregisternetCheckReceiver();
        }
        ImageLoader.getInstance().clearMemoryCache();
    }

    @Override
    protected void onResume() {
        MediaPlayerUtil.stop();
        super.onResume();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.face_cb) {
            if (moreCb.isChecked()) {
                Drawable restdrawable = getResources().getDrawable(
                        R.drawable.aio_fold);
                restdrawable.setBounds(0, 0, restdrawable.getMinimumWidth(),
                        restdrawable.getMinimumHeight());
                moreCb.setCompoundDrawables(restdrawable, null, null, null);
            }
            if (!isChecked) {
                Drawable drawable = getResources().getDrawable(
                        R.drawable.aio_favorite);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                        drawable.getMinimumHeight());
                faceCb.setCompoundDrawables(drawable, null, null, null);
                faceLinearLayout.setVisibility(GONE);
                moreLinearLayout.setVisibility(GONE);
                showSoftInput();
            } else {
                Drawable drawable = getResources().getDrawable(
                        R.drawable.aio_keyboard);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                        drawable.getMinimumHeight());
                faceCb.setCompoundDrawables(drawable, null, null, null);
                toggleAudioBtn.setImageResource(R.drawable.aio_voice);
                mMsgTextInput.setVisibility(VISIBLE);
                faceCb.setVisibility(VISIBLE);
                startAudioBtn.setVisibility(GONE);
                isVoiceShow = false;
                hideMoreLayout();
                if (isShowSoftInput) {
                    hideSoftInput();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            faceLinearLayout.setVisibility(VISIBLE);
                        }
                    }, 100);
                } else {
                    faceLinearLayout.setVisibility(VISIBLE);
                }
            }
        } else if (id == R.id.more_cb) {
            if (faceCb.isChecked()) {
                Drawable restdrawable = getResources().getDrawable(
                        R.drawable.aio_favorite);
                restdrawable.setBounds(0, 0, restdrawable.getMinimumWidth(),
                        restdrawable.getMinimumHeight());
                faceCb.setCompoundDrawables(restdrawable, null, null, null);
            }
            if (!isChecked) {
                faceLinearLayout.setVisibility(GONE);
                moreLinearLayout.setVisibility(GONE);
                Drawable drawable = getResources().getDrawable(
                        R.drawable.aio_fold);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                        drawable.getMinimumHeight());
                moreCb.setCompoundDrawables(drawable, null, null, null);
                showSoftInput();
            } else {
                toggleAudioBtn.setImageResource(R.drawable.aio_voice);
                mMsgTextInput.setVisibility(VISIBLE);
                faceCb.setVisibility(VISIBLE);
                startAudioBtn.setVisibility(GONE);
                Drawable drawable = getResources().getDrawable(
                        R.drawable.aio_keyboard);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                        drawable.getMinimumHeight());
                moreCb.setCompoundDrawables(drawable, null, null, null);
                isVoiceShow = false;
                hideFaceLayout();
                if (isShowSoftInput) {
                    hideSoftInput();
                    moreViewPagerManager.showMoreViewPager();
                } else {
                    moreLinearLayout.setVisibility(VISIBLE);
                }
            }
        }
    }

    public String generateSessionID(String userID, String shopID, String ruleType){
        return userID + "_" + shopID + "_" + ruleType;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!Environment.getExternalStorageDirectory().exists()) {
            Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
            return false;
        }

        if (isVoiceShow) {
            int[] location = new int[2];
            startAudioBtn.getLocationInWindow(location);
            int btn_rc_Y = location[1];
            int btn_rc_X = location[0];

            // 获得录音空间高度
            float btnHeight = startAudioBtn.getHeight();
            int[] anim_location = new int[2];
            animAreaLayout.getLocationInWindow(anim_location);
            int anim_Y = anim_location[1];
            int anim_X = anim_location[0];
            if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
                /** 判断手势按下位置是否是语音录制按钮的范围内 开始录音 */
                if (event.getY() > btn_rc_Y &&  event.getY() < (btn_rc_Y + btnHeight) && event.getX() > btn_rc_X) {
                    /** 播放开始录音提示音 */
                    MediaPlayerUtil.playStartRecordVoice(ChatGroupActivity.this);
                    startAudioBtn.setText(R.string.chatfooter_releasetofinish);
                    startAudioBtn.setBackgroundResource(R.mipmap.cm_btn_bg_pressed);
                    startAudioBtn.setTextColor(Color.WHITE);
                    startVoiceT = System.currentTimeMillis();
                    voiceRecordManager.start();// 开始录音
                    flag = 2;
                }
            } else {
                /** 松开手势时执行录制完成 */
                if (event.getAction() == MotionEvent.ACTION_UP && flag == 2) {
                    startAudioBtn.setText(R.string.chatfooter_presstorcd);
                    startAudioBtn.setBackgroundResource(R.mipmap.cm_btn_bg_normal);
                    startAudioBtn.setTextColor(Color.BLACK);
                    voiceRecordManager.stopRecordCountDown();
                    flag = 1;
                    boolean isCountDown = CacheUtil.getInstance().isCountDown();
                    if (!isCountDown) {
                        if (event.getY() < btn_rc_Y - 100) {
                            voiceRecordManager.stop();
                        } else {
                            voiceRecordManager.stop();
                            endVoiceT = System.currentTimeMillis();
                            int voiceTime = (int) ((endVoiceT - startVoiceT) / 1000);
                            if (voiceTime < 2) {
                                CacheUtil.getInstance().setVoiceTooShort(true);
                                voiceRecordManager.showRecordShortLayout();
                                /** 删除过短音频文件 */
                                String mediaPath = voiceRecordManager.getMediaPath();//音频文件路径
                                File tooShortMedia = new File(mediaPath);
                                if(tooShortMedia.exists()){
                                    tooShortMedia.delete();
                                }
                                return false;
                            }
                            MediaPlayerUtil.playSendOverRecordVoice(ChatGroupActivity.this);// 录音结束提示音
                            //开始文件写入
                            String mediaPath = voiceRecordManager.getMediaPath();//音频文件路径
                            messageListViewManager.sendVoiceMessage(mediaPath, voiceTime);
                        }
                    } else {
                        CacheUtil.getInstance().setCountDown(false);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_MOVE && flag == 2) {// 取消录音
                    Log.i("rcd", "ACTION_MOVE and rcding");
                    startAudioBtn.setBackgroundResource(R.mipmap.cm_btn_bg_pressed);
                    startAudioBtn.setTextColor(Color.WHITE);
                    if (event.getY() < btn_rc_Y - 100) {// 手势按下的位置不在语音录制按钮的范围内
                        startAudioBtn.setText(R.string.chatfooter_cancel_tips);
                        animAreaLayout.setVisibility(View.GONE);
                        cancelAreaLayout.setVisibility(View.VISIBLE);
                        if (event.getY() >= anim_Y
                                && event.getY() <= anim_Y
                                + animAreaLayout.getHeight()
                                && event.getX() >= anim_X
                                && event.getX() <= anim_X
                                + animAreaLayout.getWidth()) {
                        }
                    } else {
                        startAudioBtn.setText(R.string.chatfooter_releasetofinish);
                        animAreaLayout.setVisibility(View.VISIBLE);
                        cancelAreaLayout.setVisibility(View.GONE);
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
