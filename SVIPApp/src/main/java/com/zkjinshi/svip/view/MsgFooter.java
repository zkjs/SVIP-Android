/**
 * @file PullToRefreshExpandableViewHeader.java
 * @create Apr 18, 2012 5:22:27 PM
 * @author Maxwin
 * @description PullToRefreshExpandableListView's header
 */
package com.zkjinshi.svip.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.zkjinshi.svip.R;

public class MsgFooter extends LinearLayout {
	private LinearLayout mContainer;
	private LinearLayout mLineLayout;
	private ProgressBar mProgressBar;
	private int mState = STATE_NORMAL;

	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_LOADING = 2;

	public MsgFooter(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public MsgFooter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		// 初始情况，设置下拉刷新view高度为0
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, 0);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
												R.layout.msg_footer, null);
		addView(mContainer, lp);
		setGravity(Gravity.BOTTOM);

		mProgressBar = (ProgressBar)  findViewById(R.id.xlistview_header_progressbar);
		mLineLayout  = (LinearLayout) findViewById(R.id.msg_header_line);

	}

	public void setState(int state) {
		if (state == mState)
			return;

		switch (state) {
		case STATE_NORMAL:
			mProgressBar.setVisibility(View.VISIBLE);
			break;
		case STATE_READY:
			mProgressBar.setVisibility(View.VISIBLE);
			break;
		case STATE_LOADING:
			mProgressBar.setVisibility(View.VISIBLE);
			break;
		default:
		}

		mState = state;
	}

	public void setVisiableHeight(int height) {
		if (height < 0)
			height = 0;
		LayoutParams lp = (LayoutParams) mContainer
				.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}

	public int getVisiableHeight() {
		return mContainer.getHeight();
	}
	
	public void setLineVisable(int visable){
		if(null != mLineLayout){
			mLineLayout.setVisibility(visable);
		}
	}

}
