package com.zkjinshi.svip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zkjinshi.svip.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 更多栏GridView适配器
 * 开发者：JimmyZhang
 * 日期：2015/08/03
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class GroupMoreAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private int currentPage = 0;
	private List<MoreInfo> moreList = new ArrayList<>();
	private Context context;

	public GroupMoreAdapter(Context context, int currentPage) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.currentPage = currentPage;
		initData();
	}

	private void initData() {

		MoreInfo moreInfo = new MoreInfo();
		moreInfo.resId = R.drawable.chat_more_choose_image;
		moreInfo.name = "图片";
		moreList.add(moreInfo);

		moreInfo = new MoreInfo();
		moreInfo.resId = R.drawable.chat_more_take_photo;
		moreInfo.name = "拍照";
		moreList.add(moreInfo);

	}

	@Override
	public int getCount() {
		return moreList.size();
	}

	@Override
	public Object getItem(int position) {
		return moreList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = inflater
					.inflate(R.layout.more_grid_item, null, false);
			vh.iconIv = (ImageView) convertView.findViewById(R.id.more_iv_icon);
			vh.nameTv = (TextView) convertView.findViewById(R.id.more_tv_name);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		MoreInfo moreInfo = moreList.get(position);
		vh.nameTv.setText(moreInfo.name);
		vh.iconIv.setImageResource(moreInfo.resId);
		return convertView;
	}

	public static class ViewHolder {
		ImageView iconIv;
		TextView nameTv;
	}

	public class MoreInfo {
		public int resId;
		public String name;
	}
}
