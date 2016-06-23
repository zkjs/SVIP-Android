package com.zkjinshi.svip.adapter;

import android.content.Context;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.zkjinshi.svip.R;

import com.zkjinshi.svip.view.MyExpandableListView;
import com.zkjinshi.svip.vo.ServiceTagDataSecondVo;
import com.zkjinshi.svip.vo.ServiceTagTopVo;


import java.util.ArrayList;

/**
 * Created by dujiande on 2016/6/22.
 */
public class ServiceTagAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<ServiceTagTopVo> datelist;

    public ServiceTagAdapter(Context context, ArrayList<ServiceTagTopVo> datelist){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.datelist = datelist;
    }

    public void refresh(ArrayList<ServiceTagTopVo> datelist){
        this.datelist = datelist;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datelist.size();
    }

    @Override
    public Object getItem(int position) {
        return datelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(null == convertView){
            convertView = inflater.inflate(R.layout.item_servicetag,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.titleTv = (TextView)convertView.findViewById(R.id.area_tv);
            viewHolder.logoIv = (ImageView) convertView.findViewById(R.id.logo_iv);
            viewHolder.expandableListView = (MyExpandableListView)convertView.findViewById(R.id.expandableListView1);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(position == 0){
            viewHolder.logoIv.setVisibility(View.VISIBLE);
        }else{
            viewHolder.logoIv.setVisibility(View.GONE);
        }

        ServiceTagTopVo serviceTagTopVo = datelist.get(position);
        viewHolder.titleTv.setText(serviceTagTopVo.getLocdesc());

        viewHolder.expandableListView.setDivider(null);
        viewHolder.expandableListView.setGroupIndicator(null);
        final ServicesAdapter servicesAdapter = new ServicesAdapter(context,serviceTagTopVo.getServices());
        viewHolder.expandableListView.setAdapter(servicesAdapter);
        for(int i=0;i<serviceTagTopVo.getServices().size();i++){
            viewHolder.expandableListView.expandGroup(i);
        }
        servicesAdapter.notifyDataSetChanged();
        viewHolder.expandableListView.invalidateViews();

        viewHolder.expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                ServiceTagDataSecondVo serviceTagDataSecondVo = (ServiceTagDataSecondVo)servicesAdapter.getChild(groupPosition,childPosition);
                Toast.makeText(context,"你点击了"+serviceTagDataSecondVo.getSecondSrvTagName(),Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return convertView;
    }

    static class ViewHolder{
        TextView titleTv;
        ImageView logoIv;
        private MyExpandableListView expandableListView;
    }

}
