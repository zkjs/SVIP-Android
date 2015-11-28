package com.zkjinshi.svip.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.im.single.actions.SoundMeter;
import com.zkjinshi.svip.utils.FileUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 开发者：dujiande
 * 日期：2015/11/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */



 public class ListenerDialog extends Dialog {

     private SoundMeter mSensor;
    private Timer recordTimer;
    private long 	startRecord;
    private long 	overRecord;
     private String 	recordFileName;// 录音文件名称
    private int 	recordSecond;// 录音总长度
     private Activity activity;
     CircleLoadingView circleLoadingView;
     public ListenerDialog(Context context) {
         super(context, R.style.dialog_no_title);
         this.activity = (Activity)context;

     }


     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         requestWindowFeature(Window.FEATURE_NO_TITLE);
         setCanceledOnTouchOutside(true);
         getWindow().setGravity(Gravity.CENTER);
         init();
     }

     public void init() {
         LayoutInflater inflater = LayoutInflater.from(activity);
         View view = inflater.inflate(R.layout.dialog_listener, null);
         setContentView(view);

         circleLoadingView = (CircleLoadingView)findViewById(R.id.circleLoadingView);
     }

    public void stopAnimation(){
        if(circleLoadingView != null){
            circleLoadingView.destroy();
        }
    }

    public void startRecord(){
        try{
            recordFileName = System.currentTimeMillis() + ".aac";
            mSensor   	   = new SoundMeter();
            mSensor.start(recordFileName);//执行开始录音
            startRecordCountDown();
        }catch (Exception e){
            e.getStackTrace();
        }

    }

    public void stopRecord(){
        try{
            stopRecordCountDown();
            mSensor.stop();
        }catch (Exception e){
            e.getStackTrace();
        }

    }

    /**
     * 获得音频存入地址
     * @return
     */
    public String getMediaPath(){
        if(!TextUtils.isEmpty(recordFileName)){
            return FileUtil.getInstance().getAudioPath() + recordFileName;
        }
        return null;
    }

    public void setRecordFileName(String recordFileName) {
        this.recordFileName = recordFileName;
    }

    public int getRecordSecond() {
        return recordSecond;
    }

    /**
     * 录音倒计时结束
     */
    public void stopRecordCountDown() {
        if (null != recordTimer) {
            recordTimer.cancel();
            recordTimer = null;
        }
    }

    /**
     * 录音倒计时开始
     */
    public void startRecordCountDown() {
        stopRecordCountDown();
        recordTimer = new Timer();
        startRecord = System.currentTimeMillis();
        recordTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                overRecord   = System.currentTimeMillis();
                recordSecond = (int) ((overRecord - startRecord) / 1000);
            }
        }, 0, 1000);
    }
}
