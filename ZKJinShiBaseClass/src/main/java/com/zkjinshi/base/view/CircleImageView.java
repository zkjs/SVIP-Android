package com.zkjinshi.base.view;

import android.content.Context;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.util.AttributeSet;

/**
 * 说明：自定义圆形图片，用于处理圆形图片
 * 开发者：vincent
 * 日期：2015/7/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CircleImageView extends HoverImageView{

	public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setup();
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup();
	}

	public CircleImageView(Context context) {
		super(context);
		setup();
	}

	protected void setup() {}

	@Override
	public void buildBoundPath(Path borderPath){
		borderPath.reset();

		final int width = getWidth();
		final int height = getHeight();
		final float cx = width * 0.5f;
		final float cy = height * 0.5f;
		final float radius = Math.min(width, height) * 0.5f;
		borderPath.addCircle(cx, cy, radius, Direction.CW);
	}
	
	@Override
	public void buildBorderPath(Path borderPath) {
		borderPath.reset();

		final float halfBorderWidth = getBorderWidth() * 0.5f;

		final int width = getWidth();
		final int height = getHeight();
		final float cx = width * 0.5f;
		final float cy = height * 0.5f;
		final float radius = Math.min(width, height) * 0.5f;

		borderPath.addCircle(cx, cy, radius - halfBorderWidth, Direction.CW);
	}
}
