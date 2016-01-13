package com.zkjinshi.svip.activity.im.single.actions;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.FileUtil;
import com.zkjinshi.svip.utils.MediaPlayerUtil;

/**
 * 语音录制管理器
 *
 * @author JimmyZhang
 * @date 2015-6-17下午4:44:23
 */
public class VoiceRecordManager extends Handler {

    private SoundMeter	  mSensor;

    private Timer   recordTimer;
    private long 	startRecord;
    private long 	overRecord;
    private int 	recordSecond;// 录音总长度
    private String 	recordFileName;// 录音文件名称
    private Context context;

    private ImageView		volumeAnimView;// 录音分贝变化
    private TextView 		cancelRecordTv;
    private FrameLayout 	recordAudioLayout; // 包含录音中与取消录音View
    private LinearLayout 	tooShortLayout;
    private LinearLayout 	loadingLayout;
    private View 			voiceView; // 整个语音的View
    private ImageView 		volumeIv;
    private RelativeLayout  animAreaLayout;
    private RelativeLayout  cancelAreaLayout;
    private MessageListViewManager messageListViewManager;

    public VoiceRecordManager(Context context, RelativeLayout animAreaLayout,
                              RelativeLayout cancelAreaLayout) {
        this.context = context;
        this.animAreaLayout = animAreaLayout;
        this.cancelAreaLayout = cancelAreaLayout;
    }

    public void init() {
        initView((Activity) context);
    }

    public void setMessageListViewManager(MessageListViewManager messageListViewManager) {
        this.messageListViewManager = messageListViewManager;
    }

    private void initView(Activity activity) {
        // 录音提示父View(包含 tooShortLayout, loadingLayout, recordAudioLayout)
        voiceView 	   = activity.findViewById(R.id.void_rcd_hint_window);
        // 录音时间过短(未超过2s)提醒
        tooShortLayout = (LinearLayout) activity.findViewById(R.id.voice_rcd_hint_tooshort);
        loadingLayout  = (LinearLayout) activity.findViewById(R.id.voice_rcd_hint_loading);
        // 录音进行中
        recordAudioLayout = (FrameLayout) activity.findViewById(R.id.voice_rcd_hint_rcding);
        // 根据说话振幅效果动画
        volumeAnimView = (ImageView) activity.findViewById(R.id.voice_rcd_hint_anim2);
        cancelRecordTv = (TextView) activity.findViewById(R.id.cancel_rcd_tv);
        volumeIv 	   = (ImageView) activity.findViewById(R.id.voice_rcd_hint_anim);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 0x0001:// 倒计时开始
                int recordTime = msg.arg1;
                if (recordTime == 50) {
                    MediaPlayerUtil.playPlayFinishRecordVoice(context);// 播放录制完成提示音
                }
                String time = msg.obj.toString();
                if (null != time && !"".equals(time)) {
                    cancelRecordTv.setText(time);
                }
                break;

            case 0x0100:// 录音结束
                // 隐藏录音显示框
                recordAudioLayout.setVisibility(View.GONE);
                stop();
                // 播放录制完成提示音
                MediaPlayerUtil.playSendOverRecordVoice(context);
                voiceView.setVisibility(View.GONE);
                if(null != messageListViewManager){
                    messageListViewManager.sendVoiceMessage(getMediaPath(),60);
                }
                break;
            default:
                break;
        }
        super.handleMessage(msg);
    }

    /**
     * 录音开始
     */
    public void start() {
        try {
            voiceView.setVisibility(View.VISIBLE);
            recordAudioLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
            tooShortLayout.setVisibility(View.GONE);

            postDelayed(new Runnable() {
                public void run() {
                    if (!CacheUtil.getInstance().isVoiceTooShort()) {
                        loadingLayout.setVisibility(View.GONE);
                        cancelRecordTv.setText(context.getResources().getString(R.string.chatfooter_cancel_rcd));
                        animAreaLayout.setVisibility(View.VISIBLE);
                        cancelAreaLayout.setVisibility(View.GONE);
                        recordAudioLayout.setVisibility(View.VISIBLE);
                    }
                }
            }, 300);

            postDelayed(mPollTask, 300);//执行录音动画更新
            recordFileName = System.currentTimeMillis() + ".aac";
            mSensor   	   = new SoundMeter();
            mSensor.start(recordFileName);//执行开始录音
            startRecordCountDown();
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * 录音结束
     */
    public void stop() {
        try {
            recordAudioLayout.setVisibility(View.GONE);
            voiceView.setVisibility(View.GONE);
            removeCallbacks(mSleepTask);
            removeCallbacks(mPollTask);
            volumeIv.setImageResource(R.mipmap.amp1);
            /** 录音停止 */
            mSensor.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showRecordShortLayout() {
        voiceView.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);
        tooShortLayout.setVisibility(View.VISIBLE);
        postDelayed(new Runnable() {
            public void run() {
                tooShortLayout.setVisibility(View.GONE);
                voiceView.setVisibility(View.GONE);
                CacheUtil.getInstance().setVoiceTooShort(false);
            }
        }, 500);
    }

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            stop();
        }
    };

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
                if (recordSecond == 60) {
                    // 录音结束
                    CacheUtil.getInstance().setCountDown(false);
                    Message message = new Message();
                    message.what = 0x0100;
                    sendMessage(message);
                } else if (recordSecond >= 50) {
                    // 倒计时开始
                    CacheUtil.getInstance().setCountDown(true);
                    Message message = new Message();
                    message.what = 0x0001;
                    message.arg1 = recordSecond;
                    message.obj  = "还可以说" + (60 - recordSecond) + "秒";
                    sendMessage(message);
                }
            }
        }, 0, 1000);
    }

    /**
     * 录音倒计时结束
     */
    public void stopRecordCountDown() {
        if (null != recordTimer) {
            recordTimer.cancel();
            recordTimer = null;
        }
        setCancelRecordText();
    }

    /**
     * 录音取消提示
     */
    public void setCancelRecordText() {
        if (null != cancelRecordTv) {
            cancelRecordTv.setText(context.getResources().getString(
                                    R.string.chatfooter_cancel_rcd));
        }
    }

    /**
     * 更新录音中的效果显示
     */
    private Runnable mPollTask = new Runnable() {
        public void run() {
            try {
                Double amp = mSensor.getAmplitude();
                updateDisplay(amp);//跟新界面显示
                postDelayed(mPollTask, 300);//定时更新界面
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void updateDisplay(double signalEMA) {
        switch ((int) signalEMA) {
            case 0:
            case 1:
                volumeAnimView.setImageResource(R.mipmap.amp1);
                break;
            case 2:
            case 3:
                volumeAnimView.setImageResource(R.mipmap.amp2);
                break;
            case 4:
            case 5:
                volumeAnimView.setImageResource(R.mipmap.amp3);
                break;
            case 6:
            case 7:
                volumeAnimView.setImageResource(R.mipmap.amp4);
                break;
            case 8:
            case 9:
                volumeAnimView.setImageResource(R.mipmap.amp5);
                break;
            case 10:
            case 11:
                volumeAnimView.setImageResource(R.mipmap.amp6);
                break;
            default:
                volumeAnimView.setImageResource(R.mipmap.amp7);
                break;
        }
    }

}
