/**   
 * 文件名：MediaPlayerUtil.java   
 *   
 * 版本信息：   
 * 日期：2014-4-12   
 * Copyright (C) 2014 深圳市建乔无线信息技术有限公司   
 * 版权所有   
 *   
 */
package com.zkjinshi.svip.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.RingtoneManager;
import android.os.Environment;
import android.util.*;
import android.util.Base64;

import com.zkjinshi.svip.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 播放mp3提示音文件工具类
 * @author JimmyZhang
 * @date 2015-6-10下午5:17:37
 */
public class MediaPlayerUtil {

	private static MediaPlayer 	 mPlayer;
	private static boolean 		 mIsNotify = false;

	/**
	 * 播放【开始录音】提示音
	 * 
	 */
	public static void playStartRecordVoice(Context ctx) {
		MediaPlayer mediaPlayer = MediaPlayer.create(ctx, R.raw.ptt_startrecord);
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
				mp = null;
			}
		});
		mediaPlayer.start();
	}

	/**
	 * 播放【发送录音】结束提示音
	 * 
	 */
	public static void playSendOverRecordVoice(Context ctx) {
		MediaPlayer mediaPlayer = MediaPlayer.create(ctx, R.raw.ptt_sendover);
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
				mp = null;
			}
		});
		mediaPlayer.start();
	}

	/**
	 * 播放【录音文件】结束提示音
	 * 
	 */
	public static void playPlayFinishRecordVoice(Context ctx) {
		MediaPlayer mediaPlayer = MediaPlayer.create(ctx, R.raw.ptt_playfinish);
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
				mp = null;
			}
		});
		mediaPlayer.start();
	}
	
	/**
	 * 播放【其他聊天室来消息提示】提示音
	 * 
	 */
	public static void playNotifyVoice(Context ctx) {
		MediaPlayer mediaPlayer = MediaPlayer.create(ctx, R.raw.qav_gaudio_join);
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
				mp = null;
			}
		});
		mediaPlayer.start();
	}
	
	/**
	 * 设置播放模式
	 * 
	 */
	public static void setMediaMode(Context ctx, int mode){
		AudioManager am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);	 
		//am.setMode(AudioManager.MODE_IN_CALL); //听筒模式
		//am.setMode(AudioManager.MODE_NORMAL);//扬声器模式
		am.setMode(mode);
	}
	
	/**
	 * 获得当前播放模式
	 */
	public static int getMediaMode(Context ctx){
		AudioManager am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);	 
		//am.setMode(AudioManager.MODE_IN_CALL); //听筒模式
		//am.setMode(AudioManager.MODE_NORMAL);//扬声器模式
		return am.getMode();
	}

	/**
	 * 播放音频文件
	 * 输入音频的base64编码并播放
	 * @param context
	 * @param mediaPath
	 */
	public static void play(Context context, String mediaPath){
		try {
 			File mediaFile = new File(mediaPath);
			if(!mediaFile.exists()){
				return ;
			}
			if(mIsNotify){//播放提示音
				MediaPlayer mp = new MediaPlayer();
				mp.reset();
				mp.setDataSource(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
				mp.prepare();
				mp.start();
			}

			if(null != mPlayer){
				mPlayer.release();
				mPlayer = null;
			}
			mPlayer = new MediaPlayer();
			try{
				mPlayer.setDataSource(mediaPath);
				mPlayer.prepare();
				mPlayer.start();
			}catch(IOException e){
				Log.e("play","播放失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void stop(){
		if(null != mPlayer){
			mPlayer.release();
			mPlayer = null;
		}
	}

}
