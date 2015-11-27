package com.zkjinshi.svip.fragment;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseFragment;
import com.zkjinshi.svip.fragment.contacts.CharacterParser;
import com.zkjinshi.svip.fragment.contacts.ContactsSortAdapter;
import com.zkjinshi.svip.fragment.contacts.PinyinComparator;
import com.zkjinshi.svip.fragment.contacts.SideBar;
import com.zkjinshi.svip.fragment.contacts.SortModel;
import com.zkjinshi.svip.fragment.contacts.SortToken;
import com.zkjinshi.svip.listener.RecyclerItemClickListener;
import com.zkjinshi.svip.manager.CustomerServicesManager;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.HxFriendResponse;
import com.zkjinshi.svip.response.OrderConsumeResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * 通讯录联系人显示列表
 * 开发者：WinkyQin
 * 日期：2015/11/11
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ContactsFragment extends BaseFragment{

    private final static String TAG = ContactsFragment.class.getSimpleName();

    private RecyclerView        mRcvContacts;
    private LinearLayoutManager mLayoutManager;
    private ContactsSortAdapter mContactsAdapter;
    private List<SortModel>     mSortList;
    private PinyinComparator    mPinyinComparator;
    private CharacterParser     mCharacterParser;

    private SideBar     mSideBar;
    private TextView    mTvDialog;

    @Override
    protected View initView() {

        View view = View.inflate(mContext, R.layout.fragment_contacts, null);

        mRcvContacts   = (RecyclerView) view.findViewById(R.id.rcv_contacts);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mRcvContacts.setHasFixedSize(true);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRcvContacts.setLayoutManager(mLayoutManager);

        mSideBar  = (SideBar)  view.findViewById(R.id.sb_sidebar);
        mTvDialog = (TextView) view.findViewById(R.id.tv_dialog);
        mSideBar.setTextView(mTvDialog);

        return view;
    }

    @Override
    protected void initData() {
        super.initData();

        mPinyinComparator = new PinyinComparator();
        mCharacterParser  = CharacterParser.getInstance();
        mSortList         = new ArrayList<>();
        Collections.sort(mSortList, mPinyinComparator);

        mContactsAdapter  = new ContactsSortAdapter(mActivity, mSortList);
        mContactsAdapter.setOnItemClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
            }
        });

        mRcvContacts.setHasFixedSize(true);
        mRcvContacts.setAdapter(mContactsAdapter);

        //设置右侧[A-Z]快速导航栏触摸监听
        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = mContactsAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mRcvContacts.scrollToPosition(position);
                }
            }
        });

        //载入联系人
        //loadContacts();

        //网络获取环信好友
        loadHttpContacts();
    }

    /**
     * 获取网络联系人数据
     */
    private void loadHttpContacts() {

        String url = ProtocolUtil.gethximFriendUrl();
        Log.i(TAG,url);
        NetRequest netRequest = new NetRequest(url);
        HashMap<String,String> bizMap = new HashMap<String,String>();
        bizMap.put("salesid", CacheUtil.getInstance().getUserId());
        bizMap.put("token", CacheUtil.getInstance().getToken());
        bizMap.put("fuid", "");
        bizMap.put("set", "showFriend");
        netRequest.setBizParamMap(bizMap);
        NetRequestTask netRequestTask = new NetRequestTask(getActivity(),netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.PUSH;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(getActivity()) {
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
                super.onNetworkResponseSucceed(result);
                mSortList = new ArrayList<>();
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    ArrayList<HxFriendResponse> hxFriendResponses = new Gson().fromJson(result.rawResult, new TypeToken<ArrayList<HxFriendResponse>>(){}.getType());
                    for(HxFriendResponse friendResponse : hxFriendResponses){
                        String contactName  = friendResponse.getUsername();
                        String phoneNumber = friendResponse.getPhone();
                        String sortKey      = contactName;
                        SortModel sortModel = new SortModel(contactName, phoneNumber, sortKey);
                        String sortLetters  = getSortLetterBySortKey(sortKey);

                        if (sortLetters == null ) {
                            sortLetters = getSortLetter(contactName);
                        }
                        sortModel.sortLetters = sortLetters;
                        sortModel.sortToken   = parseSortKey(sortKey);
                        mSortList.add(sortModel);
                    }


                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }finally {
                    ContactsFragment.this.updateListView(mSortList);
                }

            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    /**
     * 名字转拼音,取首字母
     * @param name
     * @return
     */
    private String getSortLetter(String name) {
        String letter = "#";
        if (name == null) {
            return letter;
        }
        //汉字转换成拼
        String pinyin     = mCharacterParser.getSelling(name);
        String sortString = pinyin.substring(0, 1).toUpperCase(Locale.CHINESE);

        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            letter = sortString.toUpperCase(Locale.CHINESE);
        }
        return letter;
    }

    /**
     * 取sort_key的首字母
     * @param sortKey
     * @return
     */
    private String getSortLetterBySortKey(String sortKey) {
        if (sortKey == null || "".equals(sortKey.trim())) {
            return null;
        }

        String letter = "#";
        //汉字转换成拼音
        String sortString = sortKey.trim().substring(0, 1).toUpperCase(Locale.CHINESE);
        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            letter = sortString.toUpperCase(Locale.CHINESE);
        } else if(sortString.matches(chReg)) {
            letter = getSortLetter(sortString);
        }
        return letter;
    }

    String chReg = "[\\u4E00-\\u9FA5]+";//中文字符串匹
    //String chReg="[^\\u4E00-\\u9FA5]";//除中文外的字符匹

    /**
     * 解析sort_key
     * @param sortKey
     * @return
     */
    public SortToken parseSortKey(String sortKey) {
        SortToken token = new SortToken();
        if (sortKey != null && sortKey.length() > 0) {
            String[] enStrs = sortKey.replace(" ", "").split(chReg);
            for (int i = 0, length = enStrs.length; i < length; i++) {
                if (enStrs[i].length() > 0) {
                    token.simpleSpell += enStrs[i].charAt(0);
                    token.wholeSpell += enStrs[i];
                }
            }
        }
        return token;
    }

    /**
     * 本地获取联系人
     */
    private void loadContacts() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ContentResolver resolver = mContext.getContentResolver();
					Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[] {
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER, "sort_key" },
                            null, null, "sort_key COLLATE LOCALIZED ASC");
					if (phoneCursor == null || phoneCursor.getCount() == 0) {
						Toast.makeText(mContext, "未获得读取联系人权限或者未获得联系人数据", Toast.LENGTH_SHORT).show();
						return;
					}
					int PHONES_NUMBER_INDEX = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
					int PHONES_DISPLAY_NAME_INDEX = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
					int SORT_KEY_INDEX = phoneCursor.getColumnIndex("sort_key");
					if (phoneCursor.getCount() > 0) {

                        if(null == mSortList){
                            mSortList = new ArrayList<>();
                        }

						while (phoneCursor.moveToNext()) {
							String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
							if (TextUtils.isEmpty(phoneNumber))
								continue;
							String contactName  = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
							String sortKey      = phoneCursor.getString(SORT_KEY_INDEX);
							SortModel sortModel = new SortModel(contactName, phoneNumber, sortKey);
							String sortLetters  = getSortLetterBySortKey(sortKey);

							if (sortLetters == null ) {
								sortLetters = getSortLetter(contactName);
							}
							sortModel.sortLetters = sortLetters;
							sortModel.sortToken   = parseSortKey(sortKey);
                            mSortList.add(sortModel);
						}
					}
					phoneCursor.close();
					mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            ContactsFragment.this.updateListView(mSortList);
                        }
                    });
				} catch (Exception e) {
					Log.e(TAG, e.getLocalizedMessage());
				}
			}
		}).start();
    }

    /**
     * 更新listview界面展示
     * @param sortLists
     */
    private void updateListView(List<SortModel> sortLists) {
        if(null == sortLists || sortLists.isEmpty()){
            mTvDialog.setVisibility(View.VISIBLE);
            mTvDialog.setText(mActivity.getString(R.string.current_none));
        }else {
            // 根据a-z进行排序源数据
            mTvDialog.setVisibility(View.GONE);
            Collections.sort(sortLists, mPinyinComparator);
            mContactsAdapter.updateListView(sortLists);
        }
    }
}
