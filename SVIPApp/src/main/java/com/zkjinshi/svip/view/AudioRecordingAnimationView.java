
package com.zkjinshi.svip.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.zkjinshi.svip.R;

/**
 * 自定义录音声音变化控件
 * 
 * @author JimmyZhang
 * @date 2015-6-17下午2:03:52
 */
public class AudioRecordingAnimationView extends ImageView {

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				switch (msg.arg1) {
				case 0:
				case 1:
					setImageResource(R.mipmap.amp1);
					break;
				case 2:
				case 3:
					setImageResource(R.mipmap.amp2);
					break;
				case 4:
				case 5:
					setImageResource(R.mipmap.amp3);
					break;
				case 6:
				case 7:
					setImageResource(R.mipmap.amp4);
					break;
				case 8:
				case 9:
					setImageResource(R.mipmap.amp5);
					break;
				case 10:
				case 11:
					setImageResource(R.mipmap.amp6);
					break;
				default:
					setImageResource(R.mipmap.amp7);
					break;
				}
			}
		}
	};

	/**
	 * @param context
	 */
	public AudioRecordingAnimationView(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public AudioRecordingAnimationView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public AudioRecordingAnimationView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public void startAnim() {
		mHandler.sendEmptyMessage(1);
	}

	public void updateAnim(int amp) {
		Message msg = new Message();
		msg.what = 1;
		msg.arg1 = amp;
		mHandler.sendMessage(msg);
	}

	public void stopAnim() {
		mHandler.removeMessages(1);
	}
}
