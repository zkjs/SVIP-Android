package com.zkjinshi.svip.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.AddFriendActivity;
import com.zkjinshi.svip.activity.common.InviteCodeActivity;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.base.BaseApplication;
import com.zkjinshi.svip.base.BaseFragment;
import com.zkjinshi.svip.net.NetDialogUtil;
import com.zkjinshi.svip.net.RequestUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息Fragment
 */
public class MessageFragment extends Fragment {

    private FragmentActivity mActivity;

    public static final String TAG = MessageFragment.class.getSimpleName();
    private View vChat;
    private View vAddressBook;
    private ImageView addIv;

    private ViewPager      mViewPager;
    private RadioGroup     mRgOperation;
    private List<BaseFragment>   mFragmentList;
    private FragmentPagerAdapter mFragmentPagerAdapter;

    private ShowContactPageReceiver mShowContactPageReceiver;
    private class ShowContactPageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            if(mViewPager != null){
                mViewPager.setCurrentItem(1,true);
                LogUtil.getInstance().info(LogLevel.DEBUG,TAG+"收到显示通信录列表广播");
            }
        }
    }

    private void initView(View view){
        addIv       = (ImageView) view.findViewById(R.id.add_iv);

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

        mShowContactPageReceiver = new ShowContactPageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.SHOW_CONTACT_F_RECEIVER_ACTION);
        getActivity().registerReceiver(mShowContactPageReceiver, filter);
    }

    public void setCurrentItem(int position){
        if(null != mViewPager){
            mViewPager.setCurrentItem(position);
        }
    }

    private void initListeners(){

        addIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CacheUtil.getInstance().isLogin()){
                    Intent intent = new Intent(getActivity(),LoginActivity.class);
                    intent.putExtra("isHomeBack",true);
                    startActivity(intent);
                    return;
                }
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                getActivity().startActivity(intent);
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

    public void onDestroy(){
        super.onDestroy();
        if(null != mShowContactPageReceiver){
            getActivity().unregisterReceiver(mShowContactPageReceiver);
        }
    }


}
