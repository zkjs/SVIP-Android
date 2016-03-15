package com.zkjinshi.svip.activity.shop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.preview.ScanImagesActivity;
import com.zkjinshi.svip.utils.ProtocolUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SlideShowViewController {

    private boolean isAutoPlay = false;
    private ArrayList<String> imageUrls;
    private List<SimpleDraweeView> imageViewsList;
    private List<View> dotViewsList;
    private ViewPager viewPager;
    private int currentItem  = 0;
    private ScheduledExecutorService scheduledExecutorService;

    private Context context;
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            viewPager.setCurrentItem(currentItem);
        }

    };

    public void init(Context context,ArrayList<String> imageUrls){
        this.context = context;
        initData(context,imageUrls);
        if(isAutoPlay){
            startPlay();
        }
    }
    /**
     * 开始轮播图切换
     */
    private void startPlay(){
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 4, TimeUnit.SECONDS);
    }
    /**
     * 停止轮播图切换
     */
    private void stopPlay(){
        scheduledExecutorService.shutdown();
    }

    private void initData(Context context,ArrayList<String> imageUrls){
        imageViewsList = new ArrayList<SimpleDraweeView>();
        dotViewsList = new ArrayList<View>();
        this.imageUrls = imageUrls;
        initView(context);
    }

    private void initView(Context context){
        if(imageUrls == null || imageUrls.size() == 0)
            return;
        LinearLayout dotLayout = (LinearLayout)((Activity)context).findViewById(R.id.dotLayout);
        dotLayout.removeAllViews();
        for (int i = 0; i < imageUrls.size(); i++) {
            SimpleDraweeView view =  new SimpleDraweeView(context);
            view.setTag(imageUrls.get(i));
            GenericDraweeHierarchyBuilder builder =
                    new GenericDraweeHierarchyBuilder(context.getResources());
            GenericDraweeHierarchy hierarchy = builder
                    .setFadeDuration(300)
                   .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                    .build();
            view.setHierarchy(hierarchy);
            view.setClickable(true);
            view.setId(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = v.getId();
                    openScanImages(imageUrls,position);
                }
            });
            imageViewsList.add(view);
            ImageView dotView =  new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = DisplayUtil.dip2px(context,2);
            params.rightMargin = DisplayUtil.dip2px(context,2);
            dotLayout.addView(dotView, params);
            dotViewsList.add(dotView);
            for(int j=0;j < dotViewsList.size();j++){
                if(i == 0){
                    dotViewsList.get(0).setBackgroundResource(R.mipmap.ic_dian_yellow_pre);
                }else {
                    dotViewsList.get(i).setBackgroundResource(R.mipmap.ic_dian_white_nor);
                }
            }
        }
        viewPager = (ViewPager)((Activity)context).findViewById(R.id.viewPager);
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setFocusable(true);
        viewPager.addOnPageChangeListener(new MyPageChangeListener());
    }

    /**
     * 填充ViewPager的页面适配器
     *
     */
    private class MyPagerAdapter  extends PagerAdapter {

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager)container).removeView(imageViewsList.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            SimpleDraweeView simpleDraweeView = imageViewsList.get(position);
            simpleDraweeView.setImageURI(Uri.parse(ProtocolUtil.getHostImgUrl(simpleDraweeView.getTag()+ "")));
            ((ViewPager)container).addView(imageViewsList.get(position));
            return imageViewsList.get(position);
        }

        @Override
        public int getCount() {
            return imageViewsList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

        @Override
        public void finishUpdate(View arg0) {

        }

    }

    private class MyPageChangeListener implements OnPageChangeListener {

        boolean isAutoPlay = false;

        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                case 1:// 手势滑动，空闲中
                    isAutoPlay = false;
                    break;
                case 2:// 界面切换中
                    isAutoPlay = true;
                    break;
                case 0:// 滑动结束，即切换完毕或者加载完毕
                    // 当前为最后一张，此时从右向左滑，则切换到第一张
                    if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
                        viewPager.setCurrentItem(0);
                    }
                    // 当前为第一张，此时从左向右滑，则切换到最后一张
                    else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
                        viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
                    }
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int pos) {
            currentItem = pos;
            for(int i=0;i < dotViewsList.size();i++){
                if(i == pos){
                    dotViewsList.get(pos).setBackgroundResource(R.mipmap.ic_dian_yellow_pre);
                }else {
                    dotViewsList.get(i).setBackgroundResource(R.mipmap.ic_dian_white_nor);
                }
            }
        }

    }

    private class SlideShowTask implements Runnable{

        @Override
        public void run() {
            synchronized (viewPager) {
                currentItem = (currentItem+1)%imageViewsList.size();
                handler.obtainMessage().sendToTarget();
            }
        }
    }

    /**
     * 打开图片预览
     * @param imageList
     * @param position
     */
    private void openScanImages(ArrayList<String> imageList,int position){
        Intent it = new Intent(context,
                ScanImagesActivity.class);
        it.putStringArrayListExtra(
                ScanImagesActivity.EXTRA_IMAGE_URLS, imageList);
        it.putExtra(ScanImagesActivity.EXTRA_IMAGE_INDEX,
                position);
        context.startActivity(it);
    }
}