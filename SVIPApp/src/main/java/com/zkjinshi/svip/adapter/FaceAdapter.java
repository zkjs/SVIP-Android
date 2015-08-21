package com.zkjinshi.svip.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.utils.EmotionType;
import com.zkjinshi.svip.utils.EmotionUtil;

/**
 * 表情栏GridView适配器
 * 开发者：JimmyZhang
 * 日期：2015/08/03
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class FaceAdapter extends BaseAdapter {
	
    private Context mContext;
	private LayoutInflater inflater;
	private int currentPage = 0;
	private Map<String, Integer> mFaceMap;
	private List<Integer> faceList = new ArrayList<Integer>();

	public FaceAdapter(Context context, int currentPage) {
		// TODO Auto-generated constructor stub
	    mContext = context;
		this.inflater = LayoutInflater.from(context);
		this.currentPage = currentPage;
		mFaceMap = EmotionUtil.getInstance().getEmotionMap();
		initData();
	}

	private void initData() {
		for(Map.Entry<String, Integer> entry:mFaceMap.entrySet()){
			faceList.add(entry.getValue());
		}
	}

	@Override
	public int getCount() {
		return EmotionUtil.NUM + 1;
	}

	@Override
	public Object getItem(int position) {
		return faceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.face, null, false);
			viewHolder.faceIV = (ImageView) convertView.findViewById(R.id.face_iv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (position == EmotionUtil.NUM) {
			viewHolder.faceIV.setImageResource(R.drawable.emotion_del_selector);
			viewHolder.faceIV.setBackgroundDrawable(null);
		} else {
			int count = EmotionUtil.NUM * currentPage + position;
			if (count < 107) {
                viewHolder.faceIV.setImageBitmap(EmotionUtil.getInstance().createEmotionBitmap(mContext, EmotionType.FACE_KEYBOARD, faceList.get(count)));
			} else {
				viewHolder.faceIV.setImageDrawable(null);
			}
		}
		return convertView;
	}

	public static class ViewHolder {
		ImageView faceIV;
	}
}
