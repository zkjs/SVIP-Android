package com.zkjinshi.svip.activity.im.group.actions;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.FaceAdapter;
import com.zkjinshi.svip.adapter.FacePageAdapter;
import com.zkjinshi.svip.utils.EmotionUtil;
import com.zkjinshi.svip.view.CirclePageIndicator;
import com.zkjinshi.svip.view.JazzyViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 表情栏管理器
 * 开发者：JimmyZhang
 * 日期：2015/08/03
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class FaceViewPagerManager {
	
	private int CURRENT_FACE_PAGE = 0; //当前表情页
	
	private Context context;
	private LinearLayout faceLayout;
	private JazzyViewPager faceViewPager;
	private EditText inputEtv;
	private List<String> faceKeyList;//表情的key值集合
	
	public FaceViewPagerManager(Context context,LinearLayout faceLayout,EditText inputEtv){
		this.context = context;
		this.faceLayout = faceLayout;
		this.inputEtv = inputEtv;
	}
	
	public void init(){
		initView((Activity)context);
		initFaceList();
		initFacePage();
	}
	
	private void initView(Activity activity){
		faceViewPager = (JazzyViewPager) activity.findViewById(R.id.face_pager);
	}
	
	private void initFaceList(){
		Set<String> keySet = EmotionUtil.getInstance().getEmotionMap()
				.keySet();
		faceKeyList = new ArrayList<String>();
		faceKeyList.addAll(keySet);
	}
	
	private void initFacePage() {
		List<View> lv = new ArrayList<View>();
		for (int i = 0; i < EmotionUtil.NUM_PAGE; ++i)
			lv.add(getGridView(i));
		FacePageAdapter adapter = new FacePageAdapter(lv, faceViewPager);
		faceViewPager.setAdapter(adapter);
		faceViewPager.setCurrentItem(CURRENT_FACE_PAGE);
		CirclePageIndicator indicator = (CirclePageIndicator)((Activity)context).findViewById(R.id.indicator);
		indicator.setViewPager(faceViewPager);
		adapter.notifyDataSetChanged();
		faceLayout.setVisibility(View.GONE);
		indicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				CURRENT_FACE_PAGE = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}

	private GridView getGridView(int i) {
		GridView gv = new GridView(context);
		gv.setNumColumns(7);
		gv.setSelector(new ColorDrawable(Color.TRANSPARENT));// 屏蔽GridView默认点击效果
		gv.setBackgroundColor(Color.TRANSPARENT);
		gv.setCacheColorHint(Color.TRANSPARENT);
		gv.setHorizontalSpacing(1);
		gv.setVerticalSpacing(DisplayUtil.dip2px(context, 10));
		gv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		gv.setGravity(Gravity.CENTER);
		gv.setAdapter(new FaceAdapter(context, i));
		gv.setOnTouchListener(forbidenScroll());
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == EmotionUtil.NUM) {// 删除键的位置
					int selection = inputEtv.getSelectionStart();
					String text = inputEtv.getText().toString();
					if (selection > 0) {
						String text2 = text.substring(selection - 1);
						if ("]".equals(text2)) {
							int start = text.lastIndexOf("[");
							int end = selection;
							inputEtv.getText().delete(start, end);
							return;
						}
						inputEtv.getText().delete(selection - 1, selection);
					}
				} else {
					int count = CURRENT_FACE_PAGE * EmotionUtil.NUM + arg2;

					Bitmap bitmap = BitmapFactory.decodeResource(
							context.getResources(), (Integer) EmotionUtil.getInstance().getEmotionMap().values()
									.toArray()[count]);
					if (bitmap != null) {
						int rawHeigh = bitmap.getHeight();
						int rawWidth = bitmap.getHeight();
						int newHeight = DisplayUtil.dip2px(
								context, 30);
						int newWidth = DisplayUtil.dip2px(
								context, 30);
						// 计算缩放因子
						float heightScale = ((float) newHeight) / rawHeigh;
						float widthScale = ((float) newWidth) / rawWidth;
						// 新建立矩阵
						Matrix matrix = new Matrix();
						matrix.postScale(heightScale, widthScale);
						// 设置图片的旋转角度
						// 压缩后图片的宽和高以及kB大小均会变化
						Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
								rawWidth, rawHeigh, matrix, true);
						ImageSpan imageSpan = new ImageSpan(
								context, newBitmap);
						String emojiStr = faceKeyList.get(count);
						SpannableString spannableString = new SpannableString(
								emojiStr);
						spannableString.setSpan(imageSpan,
								emojiStr.indexOf('['),
								emojiStr.indexOf(']') + 1,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						inputEtv.append(spannableString);
						inputEtv.requestFocus();
					} else {
						String ori = inputEtv.getText().toString();
						int index = inputEtv.getSelectionStart();
						StringBuilder stringBuilder = new StringBuilder(ori);
						stringBuilder.insert(index, faceKeyList.get(count));
						inputEtv.setText(stringBuilder.toString());
						inputEtv.setSelection(index + faceKeyList.get(count).length());
						inputEtv.requestFocus();
					}
				}
			}
		});
		return gv;
	}
	
	/**
	 * 
	 * 防止乱pageview乱滚动
	 */
	private OnTouchListener forbidenScroll() {
		return new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					return true;
				}
				return false;
			}
		};
	}
}
