package com.zkjinshi.svip.activity.im.single.actions;

import java.io.IOException;
import android.media.MediaRecorder;
import android.os.Environment;
import com.zkjinshi.svip.utils.FileUtil;

/**
 * 开发者：vincent
 * 日期：2015/8/12
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SoundMeter {

    private MediaRecorder mRecorder             = null;
    private static final double EMA_FILTER      = 0.6;
    private double        mEMA                  = 0.0;

    public void start(String fileName) {
        if (!Environment.getExternalStorageState().equals(
                             Environment.MEDIA_MOUNTED)) {
            return;
        }

        //音频写入地址
        String mediaPath = FileUtil.getInstance().getAudioPath() + fileName;

        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mediaPath);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            try {
                mRecorder.prepare();
                mRecorder.start();
                mEMA = 0.0;
            } catch (IllegalStateException e) {
                System.out.print(e.getMessage());
            } catch (IOException e) {
                System.out.print(e.getMessage());
            }
        }
    }

    public void stop() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void pause() {
        if (mRecorder != null) {
            mRecorder.stop();
        }
    }

    public void start() {
        if (mRecorder != null) {
            mRecorder.start();
        }
    }

    public double getAmplitude() {
        if (mRecorder != null){
            return (mRecorder.getMaxAmplitude() / 2700.0);
        } else
            return 0;
    }

    public double getAmplitudeEMA() {
        double amp = getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }
}
