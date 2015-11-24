package com.zkjinshi.svip.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.SvipBaseAdapter;
import com.zkjinshi.svip.bean.BookOrder;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.OrderUtil;
import com.zkjinshi.svip.view.CircleImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 用户订单适配器
 *
 * 开发者：vincent
 * 日期：2015/7/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class BookOrderAdapter extends SvipBaseAdapter<BookOrder> {

    private DisplayImageOptions options;

    public BookOrderAdapter(List datas, Activity activity) {
        super(datas, activity);
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.img_hotel_anli03)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.img_hotel_anli03)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.img_hotel_anli03)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BookOrder itemOrder = mDatas.get(position);
        ViewHolder holder = null;
        if(convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        }else {
            holder = new ViewHolder();
            convertView = View.inflate(mActivity, R.layout.listitem_history_order, null);
            holder.shopIcon     = (CircleImageView) convertView.findViewById(R.id.civ_shop_icon);
            holder.shopBookIcon = (CircleImageView) convertView.findViewById(R.id.civ_shop_book_icon);
            holder.orderCeated  = (TextView) convertView.findViewById(R.id.tv_order_created);
            holder.shopStayDays = (TextView) convertView.findViewById(R.id.tv_shop_stay_days);
            holder.orderStatus  = (TextView) convertView.findViewById(R.id.tv_order_status);
            holder.shopName     = (TextView) convertView.findViewById(R.id.tv_shop_name);
            holder.costAmount   = (TextView) convertView.findViewById(R.id.tv_cost_amount);
            holder.upLine       = convertView.findViewById(R.id.v_up_line);
            holder.downLine     = convertView.findViewById(R.id.v_down_line);
            convertView.setTag(holder);
        }

        if(position == mDatas.size() -1){
            holder.downLine.setVisibility(View.GONE);
        }else{
            holder.downLine.setVisibility(View.VISIBLE);
        }

        //获得shopID网络路径
        if(!TextUtils.isEmpty(itemOrder.getShopID())){
            String logoUrl = Constants.GET_SHOP_LOGO + itemOrder.getShopID() + ".png";
            ImageLoader.getInstance().displayImage(logoUrl, holder.shopIcon, options);
        }

        //根据订单状态进行预订图标的显示
        holder.shopBookIcon.setVisibility(View.VISIBLE);
        SimpleDateFormat detailFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            //转换成指定之间格式
            Date date = detailFormat.parse(itemOrder.getCreated());
            String simpleDate= simpleFormat.format(date);
            holder.orderCeated.setText(simpleDate + "");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diff;
        long stayDays = 0;
        try {
            if (null != itemOrder && !TextUtils.isEmpty(itemOrder.getArrivalDate()) && !TextUtils.isEmpty(itemOrder.getDepartureDate())) {
                Date date1 = simpleFormat.parse(itemOrder.getArrivalDate());
                Date date2 = simpleFormat.parse(itemOrder.getDepartureDate());
                diff = date2.getTime() - date1.getTime();
                stayDays = diff / (1000 * 60 * 60 * 24);
                holder.shopStayDays.setText(stayDays + "天");
            } else {
                holder.shopStayDays.setText("1天");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //计算订单状态
        if(!TextUtils.isEmpty(itemOrder.getStatus())) {
           switch (Integer.valueOf(itemOrder.getStatus())){
                case BookOrder.ORDER_UNCONFIRMED:
                   holder.orderStatus.setText("状态: "+ mActivity.getString(R.string.order_unconfirmed));
                   holder.shopBookIcon.setVisibility(View.VISIBLE);
                    checkTimeOut(holder,itemOrder);
                break;
                case BookOrder.ORDER_CANCELLED:
                   holder.orderStatus.setText("状态: "+ mActivity.getString(R.string.order_cancelled));
                    holder.shopBookIcon.setVisibility(View.GONE);
                break;
                case BookOrder.ORDER_CONFIRMED:
                   holder.orderStatus.setText("状态: "+ mActivity.getString(R.string.order_confirmed));
                    holder.shopBookIcon.setVisibility(View.GONE);
                    checkTimeOut(holder,itemOrder);
                break;
                case BookOrder.ORDER_FINISHED:
                   holder.orderStatus.setText("状态: "+ mActivity.getString(R.string.order_finished));
                    holder.shopBookIcon.setVisibility(View.GONE);
                break;
                case BookOrder.ORDER_USING:
                   holder.orderStatus.setText("状态: "+ mActivity.getString(R.string.order_using));
                    holder.shopBookIcon.setVisibility(View.GONE);
                break;
                case BookOrder.ORDER_DELETED:
                   holder.orderStatus.setText("状态: "+ mActivity.getString(R.string.trade_deleted));
                    holder.shopBookIcon.setVisibility(View.GONE);
                break;
           }

        }

        holder.shopName.setText(itemOrder.getFullName() + "");
        float payment = stayDays * Float.parseFloat(itemOrder.getRoomRate());
        holder.costAmount.setText(payment+"元");
        return convertView;
    }

    public void checkTimeOut(ViewHolder viewHolder, BookOrder itemOrder){
        if( OrderUtil.isOrderTimeOut(itemOrder.getArrivalDate())){
            viewHolder.orderStatus.setText("订单已过期");
        }
    }

    static class ViewHolder{
        CircleImageView shopIcon;//商家图标
        CircleImageView shopBookIcon;//商家图标
        TextView        orderCeated;//订单创建时间
        TextView        shopStayDays;//订单持续天数
        TextView        orderStatus;//订单状态
        TextView        shopName;//商店名称
        TextView        costAmount;//消费金额
        View            upLine;
        View            downLine;
    }
}
