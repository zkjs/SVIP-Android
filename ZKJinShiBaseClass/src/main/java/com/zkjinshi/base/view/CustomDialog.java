package com.zkjinshi.base.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zkjinshi.base.R;

/**
 * 自定义提示对话框
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CustomDialog extends Dialog {

	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomDialog(Context context) {
		super(context);
	}

	public static class Builder {

		private Context context;
		private String title;
		private String message;
		private Uri imagePath;
		private int gravity = -1;
		private int visibility = -1;
		private String positiveButtonText;
		private String negativeButtonText;

		private OnClickListener positiveButtonClickListener,
				negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		public Builder setMessageIcon(Uri imagePath) {
			this.imagePath = imagePath;
			return this;
		}

		public Builder setGravity(int gravity) {
			this.gravity = gravity;
			return this;
		}

		public Builder setIconVisibility(int visibility) {
			this.visibility = visibility;
			return this;
		}

		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setPositiveButton(int positiveButtonText,
				OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
				OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
				OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final CustomDialog dialog = new CustomDialog(context,
					R.style.customDialog);
			View layout = inflater.inflate(R.layout.custom_dialog, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			((TextView) layout.findViewById(R.id.dialogTitle)).setText(title);
			if (positiveButtonText != null) {
				((TextView) layout.findViewById(R.id.dialogRightBtn))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((TextView) layout.findViewById(R.id.dialogRightBtn))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				layout.findViewById(R.id.dialogRightBtn).setVisibility(
						View.GONE);
			}
			if (negativeButtonText != null) {
				((TextView) layout.findViewById(R.id.dialogLeftBtn))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((TextView) layout.findViewById(R.id.dialogLeftBtn))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				layout.findViewById(R.id.dialogLeftBtn)
						.setVisibility(View.GONE);
			}
			if (message != null) {
				((ImageView) layout.findViewById(R.id.dialogIcon))
						.setVisibility(View.GONE);
				((TextView) layout.findViewById(R.id.dialogContent))
						.setVisibility(View.VISIBLE);
				((TextView) layout.findViewById(R.id.dialogContent))
						.setText(message);
			} else if (imagePath != null) {
				((ImageView) layout.findViewById(R.id.dialogIcon))
						.setVisibility(View.VISIBLE);
				((TextView) layout.findViewById(R.id.dialogContent))
						.setVisibility(View.GONE);
				((ImageView) layout.findViewById(R.id.dialogIcon))
						.setImageURI(imagePath);
				((LinearLayout) layout.findViewById(R.id.dialogText))
						.setGravity(Gravity.CENTER);
			}
			if (gravity != -1) {
				((LinearLayout) layout.findViewById(R.id.dialogText))
						.setGravity(gravity);
			}
			if (visibility != -1) {
				((ImageView) layout.findViewById(R.id.dialogIcon))
						.setVisibility(View.VISIBLE);
				LayoutParams layoutParams = ((ImageView) layout
						.findViewById(R.id.dialogIcon)).getLayoutParams();
				layoutParams.width = LayoutParams.WRAP_CONTENT;
				layoutParams.height = LayoutParams.WRAP_CONTENT;
				((ImageView) layout
						.findViewById(R.id.dialogIcon)).setLayoutParams(layoutParams);
			}
			dialog.setContentView(layout);
			return dialog;
		}
	}
}