package com.zkjinshi.svip.view.slidelistview;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.view.slidelistview.SlideListView.SlideAction;
import com.zkjinshi.svip.view.slidelistview.SlideListView.SlideMode;
import com.zkjinshi.svip.view.slidelistview.wrap.SlideItemWrapLayout;

public abstract class SlideBaseAdapter extends BaseAdapter {
	protected Context mContext;

	private SlideMode mSlideMode;// from SlideListView
	private SlideAction mSlideLeftActon;// from SlideListView
	private SlideAction mSlideRightActon;// from SlideListView

	public SlideBaseAdapter(Context context) {
		mContext = context;
		mSlideMode = SlideMode.getDefault();
		mSlideLeftActon = SlideAction.getDefault();
		mSlideRightActon = SlideAction.getDefault();
	}

	// package access method,you should not change the implement
	void setSlideMode(SlideMode slideMode) {
		mSlideMode = slideMode;
	}

	void setSlideLeftAction(SlideAction slideAction) {
		mSlideLeftActon = slideAction;
	}

	void setSlideRightAction(SlideAction slideAction) {
		mSlideRightActon = slideAction;
	}

	/**
	 * At first,your whole item slide mode is base on the SlideListView's
	 * SlideMode.<br/>
	 * but your can change the slide mode at one or more position in this
	 * adapter by override this method
	 * 
	 * @param position
	 * @return
	 */
	public SlideMode getSlideModeInPosition(int position) {
		return mSlideMode;
	}

	/**
	 * Provide your front view layout id.Must be effective(cann't be 0)
	 * 
	 * @return
	 */
	public abstract int getFrontViewId(int position);

	/**
	 * Provide your left back view layout id.If you don't need left back
	 * view,return 0
	 * 
	 * @return
	 */
	public abstract int getLeftBackViewId(int position);

	/**
	 * Provide your right back view layout id.If you don't need right back
	 * view,return 0
	 * 
	 * @return
	 */
	public abstract int getRightBackViewId(int position);

	/**
	 * In your getView() method,when you want to create convertView,you must
	 * call this method rather than create yourself<br/>
	 * example: <br/>
	 * <code>
	 * ViewHolder holder=null;
	 * if(convertView==null){
	 *    convertView=createConvertView(position);
	 *    holder=new ViewHolder();
	 *    convertView.setTag(holder);
	 * }else{
	 *    holder=(ViewHolder)convertView.getTag();
	 * }
	 * </code>
	 * 
	 * @return
	 */
	protected View createConvertView(int position) {
		LinearLayout convertView = (LinearLayout)View.inflate(mContext, R.layout.item_slidelistview, null);
		SlideItemWrapLayout item = new SlideItemWrapLayout(mContext, mSlideLeftActon, mSlideRightActon, getFrontViewId(position),
				getLeftBackViewId(position), getRightBackViewId(position));
		item.setId(R.id.slide_id_SlideItemWrapLayout);
		convertView.addView(item);
		return convertView;
	}

}
