package com.zkjinshi.svip.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.listener.OnRefreshListener;
import java.text.SimpleDateFormat;

/**
 * 开发者：vincent
 * 日期：2015/7/20
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class RefreshListview extends ListView implements AbsListView.OnScrollListener {

    private static final String TAG = "RefreshListview";

    public static final int STATE_NONE_ACTION		  = 0;// 无响应
    public static final int STATE_DOWN_EXPAND 		  = 1;// 下拉展开
    public static final int STATE_UP_HIDE 		      = 2;// 向上移动收缩

    public static final int STATE_DRAG_UP_REFRESH 		  = 3;// 上拉刷新
    public static final int STATE_DRAG_UP_RELEASE_REFRESH = 4;// 上拉释放刷新
    public static final int STATE_DRAG_UP_REFRESHING	  = 5;// 上拉刷新中

    private int mCurrentDragUpState   = STATE_DRAG_UP_REFRESH;// 上拉加载默认状态
    private int STATE_DEFAULT = STATE_NONE_ACTION;

    //头布局界面主要控件
    private LinearLayout mHeaderLayout;

    //尾布局
    private LinearLayout	mFooterLayout;
    private int				mFooterLayoutHeight;
    private TextView 		mFooterText; // 上拉状态提示

    private boolean 		isLoadingMore 	= false;	// 是否正在加载更多中
    private boolean 		isTouching		= false;	// listView是否触摸状态
    private boolean 		onceTouch 		= true;

    private int 			scrollState = OnScrollListener.SCROLL_STATE_IDLE;//初始的滚动状态
    private float 			mDownY;		// actionDown的Y值
    private float 			hiddenTop;  // 底布局需隐藏的位置
    private View 			mHeaderView;// 自定义添加的headview
    private int				mHeaderViewHeight;

    private static final int   MAX_Y_OVERSCROLL_DISTANCE = 200;
    private static final float SCROLL_RATIO 			 = 0.5f;// 阻尼系数
    private Context 		   mContext;
    private int 			   mMaxYOverscrollDistance;
    private OnRefreshListener  mOnRefershListener; 	// 刷新时的回调监听

    public RefreshListview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initBounceListView();//添加阻尼效果

        initHeaderLayout();// 加载头布局
        initFooterLayout(); // 加载尾布局
        //设置条目点击监听
        this.setOnItemClickListener(new InterceptOnItemClickListener());
    }

    public RefreshListview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshListview(Context context) {
        this(context, null);
    }

    /**
     * 添加阻尼效果
     */
    private void initBounceListView(){
        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        final float density = metrics.density;
        mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent){
        int newDeltaY = deltaY;
        int delta = (int) (deltaY  * SCROLL_RATIO);
        if (delta != 0)  newDeltaY = delta;
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);
    }

    /**
     * 预留给以后拓展
     */
    public void addMoreHeaderView(View mHeaderView) {
        this.mHeaderView = mHeaderView;
        mHeaderView.measure(0, 0);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        mHeaderLayout.addView(mHeaderView);
    }

    /**
     * 下拉加载头布局初始化，开始时界面隐藏
     */
    private void initHeaderLayout() {
        // 加载头布局以及头布局控件
//        mHeaderLayout		= (LinearLayout)	View.inflate(getContext(), R.layout.listview_header, null);
        // 系统会帮我们测量出headerView的高度
//		mHeaderLayout.measure(0, 0);
//		mHeaderLayoutHeight = mHeaderLayout.getMeasuredHeight();
//		Log.i(TAG, "测量后的高度: " + mHeaderLayoutHeight);
//		// 设置初始显示位置
//		mHeaderLayout.setPadding(0, -mHeaderLayoutHeight, 0, 0);
        // 添加headerView
//        this.addHeaderView(mHeaderLayout);
    }

    /**
     * 上拉刷新尾布局初始化，开始界面隐藏
     */
    private void initFooterLayout() {
        // 加载尾布局
        mFooterLayout = (LinearLayout)	View.inflate(getContext(), R.layout.listview_footer, null);
        mFooterText	  = (TextView)		mFooterLayout.findViewById(R.id.tv_footer_text);

        // 测量出footerView的高度
        mFooterLayout.measure(0, 0);
        mFooterLayoutHeight = mFooterLayout.getMeasuredHeight();
        Log.i(TAG, "脚布局的高度: " + mFooterLayoutHeight);
        // 设置布局隐藏
        mFooterLayout.setPadding(0, -mFooterLayoutHeight, 0, 0);
        // 添加FooterView
        this.addFooterView(mFooterLayout);
        // 设置当listView滑动时的监听
        this.setOnScrollListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
                isTouching = false;
                mDownY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:

                float moveY = ev.getY();

                int diffY = (int) (moveY - mDownY);	//获得touch移动距离
                if(Math.abs(diffY) > 3) {
                    isTouching = true;	//touching状态
                }

                if(this.mHeaderView != null){
                    if((getFirstVisiblePosition() == 0) && diffY > 0){
                        STATE_DEFAULT = STATE_DOWN_EXPAND;
                    } else if((getTop()> -mHeaderViewHeight) && diffY < 0){
                        STATE_DEFAULT = STATE_UP_HIDE;
                    } else {
                        STATE_DEFAULT = STATE_NONE_ACTION;
                    }
                }

                // 当前状态是上拉正在加载
                if (mCurrentDragUpState == STATE_DRAG_UP_REFRESHING) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                }

                //当前显示界面已到达最后
                if ((getLastVisiblePosition() == getAdapter().getCount() - 1) && diffY < 0) {
                    if (onceTouch) {
                        hiddenTop = ev.getY() + 0.5f;
                        onceTouch = false;
                    }
                    diffY = (int) (hiddenTop - ev.getY() + 0.5f);
                    // 当前显示的位置是最后一个并且是向上滑动 为上拉 加载
                    // Log.d(TAG, "mCurrentDragUpState"+mCurrentDragUpState);
                    // 给底布局设置paddingBottom
                    int hiddenHeight = (int) (mFooterLayoutHeight - diffY + 0.5f);
                    mFooterLayout.setPadding(0, 0, 0, -hiddenHeight);

                    if (hiddenHeight > 0 && mCurrentDragUpState == STATE_DRAG_UP_RELEASE_REFRESH) {
                        // 更新状态
                        mCurrentDragUpState = STATE_DRAG_UP_REFRESH;
                        refreshUIByState(mCurrentDragUpState);// UI 更新
                        Log.d(TAG, "上拉加载更多...");
                    } else if (hiddenHeight <= 0 && mCurrentDragUpState == STATE_DRAG_UP_REFRESH) {
                        // 更新状态
                        mCurrentDragUpState = STATE_DRAG_UP_RELEASE_REFRESH;
                        refreshUIByState(mCurrentDragUpState);// UI 更新
                        Log.d(TAG, "松开加载更多...");
                    }
                    // 需要自己响应touch 不能返回true
                    // return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                //判断当前是否存在headerView
                if(this.mHeaderView != null){
                    if (STATE_DEFAULT == STATE_DOWN_EXPAND){
                        getParent().requestDisallowInterceptTouchEvent(true);
                        this.setPadding(0, 0, 0, 0);
                    }

                    //判断当前是否存在headerView
                    if(STATE_DEFAULT == STATE_UP_HIDE){
                        getParent().requestDisallowInterceptTouchEvent(true);
                        this.setPadding(0, -mHeaderViewHeight, 0, 0);
                    }
                }

                mDownY = 0;
                onceTouch = true;

                // 处理上拉加载
                if (mCurrentDragUpState == STATE_DRAG_UP_REFRESH) {
                    // 如果是 上拉刷新状态，直接缩回去
                    mFooterLayout.setPadding(0, 0, 0, -mFooterLayoutHeight);
                } else if (mCurrentDragUpState == STATE_DRAG_UP_RELEASE_REFRESH) {
                    // 如果是释放刷新状态，用户希望去 刷新数据--》正在刷新状态
                    mCurrentDragUpState = STATE_DRAG_UP_REFRESHING;

                    // 设置paddingTop 为0
                    mFooterLayout.setPadding(0, 0, 0, 0);
                    isLoadingMore = true;
                    refreshUIByState(mCurrentDragUpState);// UI更新

                    // 通知 调用者 现在处于 正在刷新状态
                    if (mOnRefershListener != null) {
                        mOnRefershListener.onLoadingMore();
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 根据当前动画状态显示动画
     * @param CurrentState
     */
    private void refreshUIByState(int CurrentState) {
        switch (CurrentState) {

            case STATE_DRAG_UP_REFRESH:// 上拉刷新
                mFooterText.setText("上拉加载更多");
                break;

            case STATE_DRAG_UP_RELEASE_REFRESH:// 松开刷新
                mFooterText.setText("松开加载更多");
                break;

            case STATE_DRAG_UP_REFRESHING:// 正在刷新
                mFooterText.setText("加载中...");
                break;
        }
    }

    /**
     * 告知 ListView刷新完成
     */
    public void refreshFinish() {
        if (isLoadingMore) {
            // 上拉加载
            mFooterLayout.setPadding(0, 0, 0, -mFooterLayoutHeight);
            mCurrentDragUpState = STATE_DRAG_UP_REFRESH;
            refreshUIByState(mCurrentDragUpState);
            isLoadingMore = false;
        }
    }

    /**
     * listView滚动时的调用方法, 用于判断是否滑动到底部
     * firstVisibleItem    当前屏幕显示在顶部的item的position
     * visibleItemCount 当前屏幕显示了多少个条目的总数.
     * totalItemCount	   ListView的总条目的总数
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;
    }

    /**
     * 当滚动状态改变时回调
     * SCROLL_STATE_IDLE  					停滞状态
     * SCROLL_STATE_TOUCH_SCROLL	按住时滚动的状态
     * SCROLL_STATE_FLING 					猛地一滑
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    /**
     * 设置刷新的监听事件
     * @param listener
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mOnRefershListener = listener;
    }

    /**
     *	用于实现条目点击事件的监听
     */
    public class InterceptOnItemClickListener implements OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if( isTouching || mCurrentDragUpState == STATE_DRAG_UP_REFRESHING){
                return ;
            }
            mOnRefershListener.implOnItemClickListener(parent, view, position, id);
        }
    }

}

