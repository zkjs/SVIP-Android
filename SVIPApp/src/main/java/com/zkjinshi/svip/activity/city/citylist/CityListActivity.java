package com.zkjinshi.svip.activity.city.citylist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.city.adapter.HotCityGridAdapter;
import com.zkjinshi.svip.activity.city.helper.CityComparator;
import com.zkjinshi.svip.activity.city.helper.ContactsHelper;
import com.zkjinshi.svip.map.LocationManager;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.sqlite.CityDBUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 城市选择列表
 * 开发者：WinkyQin
 * 日期：2015/12/8
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class CityListActivity extends Activity {

    private final static String TAG = CityListActivity.class.getSimpleName();

    private BaseAdapter adapter;
    private ListView    mCityLit;
    private TextView overlay, citysearch;
    private ImageButton backbutton;
    private MyLetterListView letterListView;
    private HashMap<String, Integer> alphaIndexer;
    private String[] sections;
    private Handler handler;
    private OverlayThread overlayThread;
    private SQLiteDatabase database;
    private ArrayList<CityModel> mCityNames;
    private View city_locating_state;
    private View city_locate_failed;
    private TextView city_locate_state;
    private ProgressBar city_locating_progress;
    private ImageView city_locate_success_img;
    private HotCityGridAdapter mAdapter;
    private GridView localGridView;

    private View hotcityall;
    private WindowManager windowManager;
    private CityComparator mCityComparator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化定位
        LocationManager.getInstance().registerLocation(this);
        LayoutInflater localLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        View city_layout = localLayoutInflater.inflate(R.layout.public_cityhot, null);
        setContentView(city_layout);

        citysearch = (TextView) city_layout.findViewById(R.id.city_search_edittext);
        backbutton = (ImageButton) city_layout.findViewById(R.id.title_left_btn);
        mCityLit = (ListView) city_layout.findViewById(R.id.public_allcity_list);
        letterListView = (MyLetterListView) city_layout.findViewById(R.id.cityLetterListView);

        View cityhot_header_blank = localLayoutInflater.inflate(R.layout.public_cityhot_header_padding_blank, mCityLit, false);
        mCityLit.addHeaderView(cityhot_header_blank, null, false);
        cityhot_header_blank = localLayoutInflater.inflate(R.layout.city_locate_layout, mCityLit, false);
        city_locating_state = cityhot_header_blank.findViewById(R.id.city_locating_state);
        city_locate_state = ((TextView) cityhot_header_blank.findViewById(R.id.city_locate_state));
        city_locating_progress = ((ProgressBar) cityhot_header_blank.findViewById(R.id.city_locating_progress));
        city_locate_success_img = ((ImageView) cityhot_header_blank.findViewById(R.id.city_locate_success_img));
        city_locate_failed = cityhot_header_blank.findViewById(R.id.city_locate_failed);
        mCityLit.addHeaderView(cityhot_header_blank);

        View hotheadview = localLayoutInflater.inflate(R.layout.public_cityhot_header_padding, mCityLit, false);
        mCityLit.addHeaderView(hotheadview, null, false);
        hotcityall = localLayoutInflater.inflate(R.layout.public_cityhot_allcity, mCityLit, false);

        localGridView = (GridView) hotcityall.findViewById(R.id.public_hotcity_list);
        mCityLit.addHeaderView(hotcityall);

        mCityComparator = new CityComparator();
        //获取当前热门城市列表
        getCityList();

        city_locating_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityModel=city_locate_state.getText().toString();
                Setting.Save2SharedPreferences(CityListActivity.this, "city",
                        cityModel);
                Intent intent =new Intent();
                intent.putExtra("city",cityModel);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        //获取位置
        LocationManager.getInstance().setLocationChangeListener(
            new LocationManager.LocationChangeListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {

                    city_locating_progress.setVisibility(View.GONE);
                    if (aMapLocation != null) {
                        if (aMapLocation.getCity() != null  && !aMapLocation.getCity().equals("")) {
                            city_locate_failed.setVisibility(View.GONE);
                            city_locate_state.setVisibility(View.VISIBLE);
                            city_locating_progress.setVisibility(View.GONE);
                            city_locate_success_img.setVisibility(View.VISIBLE);
                            String city = aMapLocation.getCity();
                            if(!TextUtils.isEmpty(city)){
                                city_locate_state.setText(city);
                            }
                        } else {
                            city_locating_state.setVisibility(View.GONE);
                            city_locate_failed.setVisibility(View.VISIBLE);
                        }
                    } else {
                        // 定位失败
                        city_locating_state.setVisibility(View.GONE);
                        city_locate_failed.setVisibility(View.VISIBLE);
                    }
                }
        });

          /** start 获取全国城市名 并加载显示 暂时不显示 add by WinkyQin at 2015/12/8 */
//        DBManager dbManager = new DBManager(this);
//        dbManager.openDateBase();
//        dbManager.closeDatabase();
//        database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
//        mCityNames = getCityNames();
//        database.close();
        /** end add by WinkyQin at 2015/12/8 */

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                String city = data.getStringExtra("city");
                Intent intent = new Intent();
                intent.putExtra("city", city);
                setResult(RESULT_OK, intent);
                finish();
                break;

            default:
                break;
        }
    }

    /**
     * 获取城市列表
     */
    private void getCityList(){
        String url = ProtocolUtil.getCityListUrl();
        Log.i(TAG, url);
        NetRequest netRequest = new NetRequest(url);
        NetRequestTask netRequestTask = new NetRequestTask(CityListActivity.this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(CityListActivity.this) {
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
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                try {
                    Gson gson = new Gson();
                    List<CityBean> citys = gson.fromJson(result.rawResult,
                        new TypeToken<ArrayList<CityBean>>(){}.getType());

                    mAdapter = new HotCityGridAdapter(CityListActivity.this, citys);
                    localGridView.setAdapter(mAdapter);

                    localGridView.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            CityBean cityBean = (CityBean) parent.getAdapter().getItem(position);
                            String city =  cityBean.getCity();
                            Setting.Save2SharedPreferences(CityListActivity.this, "city", city);
                            Intent intent =new Intent();
                            intent.putExtra("city", city);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });

                    mCityNames = CityFactory.getInstance().convertCityBeans2CityModels(citys);
                    Collections.sort(mCityNames, mCityComparator);//比较城市名称

                    letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
                    alphaIndexer = new HashMap<>();
                    handler = new Handler();
                    overlayThread = new OverlayThread();
                    initOverlay();
                    setAdapter(mCityNames);

                    //添加城市名称进入数据库
                    CityDBUtil.getInstance().batchAddCityModels(mCityNames);

                    mCityLit.setOnItemClickListener(new CityListActivityOnItemClick());
                    backbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });

                    citysearch.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            boolean startLoad = ContactsHelper.getInstance().startLoadContacts();
                            Intent intent = new Intent(CityListActivity.this, SearchActivity.class);
                            startActivityForResult(intent, 2);
                            return false;
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = false;
        netRequestTask.execute();
    }

//    private ArrayList<CityModel> getCityNames() {
//        ArrayList<CityModel> names = new ArrayList<CityModel>();
//        Cursor cursor = database.rawQuery(
//                "SELECT * FROM T_City ORDER BY NameSort", null);
//        for (int i = 0; i < cursor.getCount(); i++) {
//            cursor.moveToPosition(i);
//            CityModel cityModel = new CityModel();
//            cityModel.setCityName(cursor.getString(cursor
//                    .getColumnIndex("CityName")));
//            cityModel.setNameSort(cursor.getString(cursor
//                    .getColumnIndex("NameSort")));
//            names.add(cityModel);
//        }
//        cursor.close();
//        return names;
//    }

    class CityListActivityOnItemClick implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                long arg3) {
            CityModel cityModel = (CityModel) mCityLit.getAdapter()
                    .getItem(pos);
            if(cityModel!=null) {
                Setting.Save2SharedPreferences(CityListActivity.this, "city",
                        cityModel.getCityName());
                Intent intent =new Intent();
                intent.putExtra("city",cityModel.getCityName());
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    /**
     * ListView
     * @param list
     */
    private void setAdapter(List<CityModel> list) {
        if (list != null) {
            adapter = new ListAdapter(this, list);
            mCityLit.setAdapter(adapter);
        }
    }

    /**
     * ListViewAdapter
     */
    private class ListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<CityModel> list;

        public ListAdapter(Context context, List<CityModel> list) {

            this.inflater = LayoutInflater.from(context);
            this.list = list;
            alphaIndexer = new HashMap<>();
            sections = new String[list.size()];

            for (int i = 0; i < list.size(); i++) {
                // getAlpha(list.get(i));
                String currentStr = list.get(i).getNameSort();
                String previewStr = (i - 1) >= 0 ? list.get(i - 1)
                        .getNameSort() : " ";
                if (!previewStr.equals(currentStr)) {
                    String name = list.get(i).getNameSort();
                    alphaIndexer.put(name, i);
                    sections[i] = name;
                }
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.public_cityhot_item,
                        null);
                holder = new ViewHolder();
                holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
                holder.name  = (TextView) convertView
                        .findViewById(R.id.public_cityhot_item_textview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name.setText(list.get(position).getCityName());
            String currentStr = list.get(position).getNameSort();
            String previewStr = (position - 1) >= 0 ? list.get(position - 1).getNameSort() : " ";
            if (!previewStr.equals(currentStr)) {
                holder.alpha.setVisibility(View.VISIBLE);
                holder.alpha.setText(currentStr);
            } else {
                holder.alpha.setVisibility(View.GONE);
            }
            return convertView;
        }

        private class ViewHolder {
            TextView alpha;
            TextView name;
        }

    }

    private void initOverlay() {
        LayoutInflater inflater = LayoutInflater.from(this);
        overlay = (TextView) inflater.inflate(R.layout.overlay, null);
        overlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                                        LayoutParams.WRAP_CONTENT,
                                        LayoutParams.WRAP_CONTENT,
                                        WindowManager.LayoutParams.TYPE_APPLICATION,
                                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        PixelFormat.TRANSLUCENT);
        windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(overlay, lp);
    }

    @Override
    protected void onDestroy() {
        if(null != overlay){
            windowManager.removeView(overlay);
        }
        LocationManager.getInstance().removeLocation();
        super.onDestroy();
    }

    private class LetterListViewListener implements
            MyLetterListView.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {
            if (alphaIndexer.get(s) != null) {
                int position = alphaIndexer.get(s);
                mCityLit.setSelection(position);
                overlay.setText(sections[position]);
                overlay.setVisibility(View.VISIBLE);
                handler.removeCallbacks(overlayThread);
                handler.postDelayed(overlayThread, 1500);
            }
        }
    }

    // overlay
    private class OverlayThread implements Runnable {
        @Override
        public void run() {
            overlay.setVisibility(View.GONE);
        }
    }

}