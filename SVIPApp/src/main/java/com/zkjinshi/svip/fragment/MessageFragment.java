package com.zkjinshi.svip.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息Fragment
 */
public class MessageFragment extends Fragment {

    private FragmentActivity mActivity;

    private RelativeLayout mRlAdd;
    private RelativeLayout mRlSearch;
    private View vChat;
    private View vAddressBook;

    private ViewPager      mViewPager;
    private RadioGroup     mRgOperation;
    private List<BaseFragment>   mFragmentList;
    private FragmentPagerAdapter mFragmentPagerAdapter;

    private void initView(View view){
        mRlAdd       = (RelativeLayout) view.findViewById(R.id.rl_add);
        mRlSearch    = (RelativeLayout) view.findViewById(R.id.rl_search);
        vChat        = view.findViewById(R.id.v_chat);
        vAddressBook = view.findViewById(R.id.v_address_book);
        mViewPager   = (ViewPager)  view.findViewById(R.id.vp_fragment);
        mRgOperation = (RadioGroup) view.findViewById(R.id.rg_operation);
    }

    private void initData(){
        mActivity     = this.getActivity();
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new MessageCenterFragment());
        mFragmentList.add(new ContactsFragment());

        mFragmentPagerAdapter = new FragmentPagerAdapter(
            getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        };

        mViewPager.setAdapter(mFragmentPagerAdapter);

        mViewPager.setCurrentItem(0);
        mRgOperation.check(R.id.rb_chat);
        vChat.setVisibility(View.VISIBLE);
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

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    mRgOperation.check(R.id.rb_chat);
                }else if(position == 1){
                    mRgOperation.check(R.id.rb_address_book);
                }
            }
        });

        mRgOperation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_chat){
                    mViewPager.setCurrentItem(0);
                    vChat.setVisibility(View.VISIBLE);
                    vAddressBook.setVisibility(View.INVISIBLE);
                }else if(checkedId == R.id.rb_address_book) {
                    mViewPager.setCurrentItem(1);
                    vChat.setVisibility(View.INVISIBLE);
                    vAddressBook.setVisibility(View.VISIBLE);
                }
            }
        });
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
