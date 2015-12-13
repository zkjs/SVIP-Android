package com.zkjinshi.svip.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.SVIPApplication;
import com.zkjinshi.svip.activity.common.MainActivity;
import com.zkjinshi.svip.bean.CustomerServiceBean;
import com.zkjinshi.svip.bean.HeadBean;
import com.zkjinshi.svip.ibeacon.RegionVo;
import com.zkjinshi.svip.manager.CustomerServicesManager;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.CustomerServiceListResponse;
import com.zkjinshi.svip.response.OrderLastResponse;
import com.zkjinshi.svip.sqlite.ShopDetailDBUtil;
import com.zkjinshi.svip.view.BookingDialog;
import com.zkjinshi.svip.view.CleverDialog;
import com.zkjinshi.svip.view.ListenerDialog;

import org.apache.log4j.chainsaw.Main;

import java.util.ArrayList;

/**
 * Fragment引导管理器
 */
public class TabNavigationFragment extends Fragment implements
		RadioGroup.OnCheckedChangeListener {

	public static final String TAG = TabNavigationFragment.class.getSimpleName();
	private RadioGroup mTabItemGroup;

	private SparseArray<Fragment> mContentFragments = new SparseArray<Fragment>(
			4);
	
	public int mCheckedRadioButtonId = R.id.footer_tab_rb_home;

	@Override
	public void onStart() {
		mTabItemGroup = (RadioGroup) this.getActivity().findViewById(
				R.id.footer_tab_rg);
		mTabItemGroup.setOnCheckedChangeListener(this);

		RadioButton rb = (RadioButton) mTabItemGroup
				.findViewById(mCheckedRadioButtonId);
		if (rb != null) {
			rb.setChecked(true);
		}

		super.onStart();
	}

	public static void setCurrentNavigationChecked(int rbId, Activity activity) {
		RadioGroup mTabItemGroup = (RadioGroup) activity
				.findViewById(R.id.footer_tab_rg);
		RadioButton tabButton = (RadioButton) mTabItemGroup.findViewById(rbId);
		if (tabButton != null) {
			tabButton.setChecked(true);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.footer_tab, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void navigateTo(int radioButtonId) {

		mTabItemGroup = (RadioGroup) this.getActivity().findViewById(
				R.id.footer_tab_rg);
		mCheckedRadioButtonId = radioButtonId;
		RadioButton rb = (RadioButton) mTabItemGroup
				.findViewById(mCheckedRadioButtonId);
		if (rb != null) {
			rb.setChecked(true);
		}
	}

	public void navigateToFragment(int radioButtonId, Fragment target) {
		navigateTo(radioButtonId);
		FragmentTransaction ft = this.getFragmentManager().beginTransaction();
		if (target != null) {
			ft.replace(R.id.main_frame_content, target);
			ft.commit();
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		FragmentManager fragmentManager = getFragmentManager();
		if (null != fragmentManager) {
			FragmentTransaction ft = fragmentManager.beginTransaction();
			if (null != ft) {
				Fragment target = mContentFragments.get(checkedId);
				mCheckedRadioButtonId = checkedId;
				if (target == null) {
					if (mCheckedRadioButtonId == R.id.footer_tab_rb_home) {
						target = new HomeFragment();
					} else if (mCheckedRadioButtonId == R.id.footer_tab_rb_shop) {
						target = new ShopFragment();
					} else if (mCheckedRadioButtonId == R.id.footer_tab_rb_message) {
						target = new MessageFragment();
					} else if (mCheckedRadioButtonId == R.id.footer_tab_rb_set) {
						target = new SetFragment();
					}
					mContentFragments.put(mCheckedRadioButtonId, target);
				}
				if (target != null) {
					ft.replace(R.id.main_frame_content, target,String.valueOf(checkedId));
					ft.commit();
				}
			}
		}
	}

}
