package com.zkjinshi.svip.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.adapter.MessageFragmentAdapter;
import com.zkjinshi.svip.base.BaseFragment;
import com.zkjinshi.svip.view.zoomview.ImageViewTouchBase;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息Fragment
 */
public class MessageFragment extends Fragment {

    private FragmentActivity mActivity;

    private RelativeLayout mRlAdd;
    private RelativeLayout mRlSearch;
    private TextView       mTvChat;
    private TextView       mTvContacts;
    private ImageView      mIvTabLine;

    private ViewPager      mViewPager;
    private List<BaseFragment>   mFragmentList;
    private FragmentPagerAdapter mFragmenAdapter;
    private FragmentManager      mFragmentManager;

    private int mCurrentIndex ;
    private int mScreenWidth ;

    private void initView(View view){

        mRlAdd       = (RelativeLayout) view.findViewById(R.id.rl_add);
        mRlSearch    = (RelativeLayout) view.findViewById(R.id.rl_search);
        mTvChat      = (TextView) view.findViewById(R.id.tv_chat);
        mTvContacts  = (TextView) view.findViewById(R.id.tv_contacts);
        mIvTabLine   = (ImageView)  view.findViewById(R.id.iv_tab_line);
        mViewPager   = (ViewPager)  view.findViewById(R.id.vp_fragment);
    }

    private void initData(){
        mActivity        = this.getActivity();
        mFragmentManager = mActivity.getSupportFragmentManager();

        initTabLineWidth();
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new MessageCenterFragment());
        mFragmentList.add(new ContactsFragment());
        mFragmenAdapter = new MessageFragmentAdapter(mFragmentManager, mFragmentList);

        mViewPager.setAdapter(mFragmenAdapter);
        mViewPager.setCurrentItem(0);
    }

    private void initListeners(){

        mRlAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.getInstance().showCustomToast(mActivity, "TODO", Gravity.CENTER);
            }
        });

        mRlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.getInstance().showCustomToast(mActivity, "TODO", Gravity.CENTER);
            }
        });

        mTvChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentIndex != 0){
                    mViewPager.setCurrentItem(0);
                }
            }
        });

        mTvContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentIndex != 1){
                    mViewPager.setCurrentItem(1);
                }
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比
             * offsetPixels:当前页面偏移的像素位置
             */
            @Override
            public void onPageScrolled(int position, float offset, int i1) {

                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mIvTabLine.getLayoutParams();
                Log.e("offset:", offset + "");
                /**
                 * 利用currentIndex(当前所在页面)和position(下一个页面)以及offset来
                 * 设置mTabLineIv的左边距 滑动场景：
                 * 从左到右分别为0,1  0->1; 1->2; 2->1; 1->0
                 */
                // 0->1
                if (mCurrentIndex == 0 && position == 0){
                    lp.leftMargin = (int) (offset * (mScreenWidth * 1.0 / 2)
                            + mCurrentIndex * (mScreenWidth / 2));
                    // 1->0
                } else if (mCurrentIndex == 1 && position == 0){
                    lp.leftMargin = (int) ( - (1 - offset) * (mScreenWidth * 1.0 / 2)
                            + mCurrentIndex * (mScreenWidth / 2));
                }
                mIvTabLine.setLayoutParams(lp);
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentIndex = position;
            }

            /**
             * state滑动中的状态
             * 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。
             */
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    /**
     * 设置滑动条的宽度为屏幕的1/2(根据Tab的个数而定)
     */
    private void initTabLineWidth() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        mActivity.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dpMetrics);
        mScreenWidth = dpMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mIvTabLine.getLayoutParams();
        lp.width = mScreenWidth / 2;
        mIvTabLine.setLayoutParams(lp);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
        initListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
