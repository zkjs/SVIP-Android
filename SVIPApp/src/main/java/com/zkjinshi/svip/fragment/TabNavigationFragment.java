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
	public SVIPApplication svipApplication;
	public OrderLastResponse lastOrderInfo = null;
	public ListenerDialog listenerDialog;

	private static final int LONG_CLICK_TIME = 500;
	private static final int LONG_CLICK_MSG = 1;
	private static final int SHORT_CLICK_MSG = 2;
	private static final int LONG_CLICK_UP_MSG = 3;
	public boolean isLongClick = false;
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what == LONG_CLICK_MSG){
				isLongClick = true;
				longClickDeal();
			}else if(msg.what == SHORT_CLICK_MSG){
				isLongClick = false;
				shortClickDeal();
			}else if(msg.what == LONG_CLICK_UP_MSG){
				isLongClick = false;
				longClickUpDeal();
			}
		}
	};

	private RadioGroup mTabItemGroup;
	private ImageView logoIv;

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

	public void longClickDeal(){
//		if(!listenerDialog.isShowing()){
//			listenerDialog.show();
//			listenerDialog.startRecord();
//		}
	}

	public void longClickUpDeal(){
//		if(listenerDialog.isShowing()){
//			listenerDialog.stopRecord();
//			listenerDialog.cancel();
//		}
//		loadCleverServer();
	}

	public void shortClickDeal(){
		new CleverDialog(getActivity()).show();
	}

	@Override
	public void onPause() {
		super.onPause();
		if(listenerDialog.isShowing()){
			longClickUpDeal();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(listenerDialog != null){
			listenerDialog.stopAnimation();
		}
	}

	//智能选择酒店
	public String getCleverShopId(){
		if(getActivity() instanceof MainActivity){
			MainActivity mainActivity = (MainActivity)getActivity();
			lastOrderInfo = mainActivity.lastOrderInfo;
		}
		if(lastOrderInfo != null){
			return lastOrderInfo.getShopid();
		}else if(svipApplication.mRegionList.size() > 0){
			RegionVo region = svipApplication.mRegionList.get(svipApplication.mRegionList.size() - 1);
			return  region.getiBeacon().getShopid();
		}
		return "120";
	}

	//智能选择客服
	public void loadCleverServer(){
		final String shopid = getCleverShopId();
		CustomerServicesManager.getInstance().requestServiceListTask(getActivity(),shopid , new ExtNetRequestListener(getActivity()) {
			@Override
			public void onNetworkRequestError(int errorCode, String errorMessage) {
				Log.i(TAG, "errorCode:" + errorCode);
				Log.i(TAG, "errorMessage:" + errorMessage);
			}

			@Override
			public void onNetworkRequestCancelled() {

			}

			@Override
			public void onNetworkResponseSucceed(NetResponse result) {
				Log.i(TAG, "result:" + result.rawResult);
				CustomerServiceListResponse customerServiceListResponse = new Gson().fromJson(result.rawResult, CustomerServiceListResponse.class);
				if (null != customerServiceListResponse) {
					HeadBean head = customerServiceListResponse.getHead();
					if (null != head) {
						boolean isSet = head.isSet();
						if (isSet) {
							ArrayList<CustomerServiceBean> customerServiceList = customerServiceListResponse.getData();
							String salesId = head.getExclusive_salesid();
							CustomerServiceBean customerService = null;
							if (null != customerServiceList && !customerServiceList.isEmpty()) {
								if (!TextUtils.isEmpty(salesId)) {//有专属客服
									customerService = CustomerServicesManager.getInstance().getExclusiveCustomerServic(customerServiceList, salesId);
								} else {//无专属客服
									customerService = CustomerServicesManager.getInstance().getRandomCustomerServic(customerServiceList);
								}
								showBookingDailog(shopid,customerService);
							}
						}
					}
				}
			}

			@Override
			public void beforeNetworkRequestStart() {

			}
		});
	}

	public void showBookingDailog(String shopId,CustomerServiceBean customerServiceBean){
		BookingDialog bookingDialog = new BookingDialog(getActivity());
		bookingDialog.shopId = shopId;
		bookingDialog.customerService = customerServiceBean;
		bookingDialog.shopName = ShopDetailDBUtil.getInstance().queryShopNameByShopID(shopId);
		if(listenerDialog != null && !TextUtils.isEmpty(listenerDialog.getMediaPath())){
			bookingDialog.recordFileName = listenerDialog.getMediaPath();
			bookingDialog.recordSecond = listenerDialog.getRecordSecond();
			listenerDialog.setRecordFileName(null);
		}
		bookingDialog.show();
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
		svipApplication = (SVIPApplication)getActivity().getApplication();
		listenerDialog = new ListenerDialog(getActivity());
		logoIv = (ImageView)view.findViewById(R.id.footer_tab_iv_zhijian);
		logoIv.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						handler.sendEmptyMessageDelayed(LONG_CLICK_MSG, LONG_CLICK_TIME);
						return true;
					case MotionEvent.ACTION_UP:
						handler.removeMessages(LONG_CLICK_MSG);
						if(isLongClick){
							isLongClick = false;
							handler.sendEmptyMessage(LONG_CLICK_UP_MSG);
							return false;
						}
						isLongClick = false;
						handler.sendEmptyMessage(SHORT_CLICK_MSG);
						return true;

				}
				return false;
			}
		});
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
