package com.zkjinshi.svip.base;

import android.app.Activity;
import android.widget.BaseAdapter;

import com.zkjinshi.svip.vo.MessageVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 适配器基类
 * 开发者：vincent
 * 日期：2015/7/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public abstract class SvipBaseAdapter<T> extends BaseAdapter {

    protected List<T>  mDatas;
    protected Activity mActivity;

    public SvipBaseAdapter(List<T> datas, Activity activity) {
        this.mActivity = activity;
        this.mDatas = datas;
    }

    @Override
    public int getCount() {
        if (mDatas != null) {
            return mDatas.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mDatas != null) {
            return mDatas.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void setData(List<T> datas) {
        if (null == datas) {
            this.mDatas = new ArrayList<T>();
        } else {
            this.mDatas = datas;
        }
        notifyDataSetChanged();
    }

}
