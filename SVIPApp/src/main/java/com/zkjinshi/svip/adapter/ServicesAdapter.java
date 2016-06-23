package com.zkjinshi.svip.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zkjinshi.svip.R;
import com.zkjinshi.svip.vo.ServiceTagDataFirstVo;
import com.zkjinshi.svip.vo.ServiceTagDataSecondVo;

import java.util.ArrayList;

/**
 * Created by dujiande on 2016/6/22.
 */
public class ServicesAdapter extends BaseExpandableListAdapter {

    private ArrayList<ServiceTagDataFirstVo> datalist;
    private Context context;

    public ServicesAdapter(Context context, ArrayList<ServiceTagDataFirstVo> list) {
        this.context = context;
        this.datalist = list;
    }

    public void refresh( ArrayList<ServiceTagDataFirstVo> list){
        this.datalist = list;
        notifyDataSetChanged();

    }

    @Override
    public int getGroupCount() {
        return null == this.datalist ? 0 : this.datalist.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return null == this.datalist.get(groupPosition).getSecondSrvTag() ? 0
                : this.datalist.get(groupPosition).getSecondSrvTag().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.datalist.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.datalist.get(groupPosition).getSecondSrvTag().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return this.datalist.get(groupPosition).hashCode();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return this.datalist.get(groupPosition).getSecondSrvTag().get(childPosition)
                .hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        View view = convertView;
        ViewHolderGroup holderGroup;
        if (view == null) {
            holderGroup = new ViewHolderGroup();
            view = ((Activity) context).getLayoutInflater().inflate(
                    R.layout.item_parent, null);
            holderGroup.imgExpand = (ImageView) view.findViewById(R.id.parent_iv);
            holderGroup.parentTv = (TextView) view.findViewById(R.id.parent_tv);

            view.setTag(holderGroup);
        } else {
            holderGroup = (ViewHolderGroup) view.getTag();
        }

        if (isExpanded) {
            holderGroup.imgExpand.setImageResource(R.drawable.btn_expand_selector);
        } else {
            holderGroup.imgExpand.setImageResource(R.drawable.btn_collapse_selector);
        }
        ServiceTagDataFirstVo serviceTagDataFirstVo = datalist.get(groupPosition);
        holderGroup.parentTv.setText(serviceTagDataFirstVo.getFirstSrvTagName());
        return view;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        View view = convertView;
        ViewHolderChild holderChild;
        if (view == null) {
            holderChild = new ViewHolderChild();
            view = ((Activity) context).getLayoutInflater().inflate(
                    R.layout.item_child, null);
            holderChild.imgExpand = (ImageView) view.findViewById(R.id.child_iv);
            holderChild.childTv = (TextView) view.findViewById(R.id.child_tv);

            view.setTag(holderChild);
        } else {
            holderChild = (ViewHolderChild) view.getTag();
        }
        ServiceTagDataSecondVo serviceTagDataSecondVo = datalist.get(groupPosition).getSecondSrvTag().get(childPosition);
        holderChild.childTv.setText(serviceTagDataSecondVo.getSecondSrvTagName());
        return view;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }



    class ViewHolderGroup {
        ImageView imgExpand;
        TextView parentTv;
    }

    class ViewHolderChild {
        ImageView imgExpand;
        TextView childTv;
    }
}
