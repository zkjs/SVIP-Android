/*
 * Copyright (c) 2015 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zkjinshi.svip.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.AbsListView;

import android.widget.SectionIndexer;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.MyShopVo;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 开发者：dujiande
 * 日期：2015/11/3
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SelectShopAdapter extends KJAdapter<MyShopVo> implements SectionIndexer {


    private ArrayList<MyShopVo> datas;
    private Context mContext;

    public SelectShopAdapter(AbsListView view, ArrayList<MyShopVo> mDatas) {
        super(view, mDatas, R.layout.item_select_shop);
        datas = mDatas;
        mContext = view.getContext();
        if (datas == null) {
            datas = new ArrayList<>();
        }
        Collections.sort(datas);
    }

    @Override
    public void convert(AdapterHolder helper, MyShopVo item, boolean isScrolling) {
    }

    @Override
    public void convert(AdapterHolder holder, MyShopVo item, boolean isScrolling, int position) {

        holder.setText(R.id.contact_title, item.getShopname());
        SimpleDraweeView headImg = holder.getView(R.id.contact_head);
        String logoUrl = ProtocolUtil.getHostImgUrl(item.getShoplogo());
        headImg.setImageURI(Uri.parse(logoUrl));

        TextView tvLetter = holder.getView(R.id.contact_catalog);
        TextView tvLine = holder.getView(R.id.contact_line);

        //如果是第0个那么一定显示#号
        if (position == 0) {
//            tvLetter.setVisibility(View.VISIBLE);
//            tvLetter.setText("#");
//            tvLine.setVisibility(View.VISIBLE);
            tvLetter.setVisibility(View.VISIBLE);
            tvLetter.setText("" + item.getFirstChar());
            tvLine.setVisibility(View.VISIBLE);
        } else {

            //如果和上一个item的首字母不同，则认为是新分类的开始
            MyShopVo prevData = datas.get(position - 1);
            if (item.getFirstChar() != prevData.getFirstChar()) {
                tvLetter.setVisibility(View.VISIBLE);
                tvLetter.setText("" + item.getFirstChar());
                tvLine.setVisibility(View.VISIBLE);
            } else {
                tvLetter.setVisibility(View.GONE);
                tvLine.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        MyShopVo item = datas.get(position);
        return item.getFirstChar();
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            char firstChar = datas.get(i).getFirstChar();
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

}
