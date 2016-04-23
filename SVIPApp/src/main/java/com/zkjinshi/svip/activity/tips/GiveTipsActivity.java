package com.zkjinshi.svip.activity.tips;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.response.WaitListResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.vo.TipsResultVo;
import com.zkjinshi.svip.vo.WaiterVo;


/**
 * Created by dujiande on 2016/4/22.
 */
public class GiveTipsActivity extends BaseActivity {

    private Context mContext;
    public WaitListResponse waitListResponse = null;
    public int currentIndex = 0;

    private SimpleDraweeView avatarSdv;
    private Button smallBtn,bigBtn;
    private TextView nameTv;
    private RelativeLayout mohuRlt;
    private LinearLayout contentLlt;
    private boolean flingAble = true;

    private GestureDetector gestureDetector;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_tips);
        mContext = this;

        initView();
        initData();
        initListener();
    }

    private void initView() {
        avatarSdv = (SimpleDraweeView)findViewById(R.id.avatarSdv);
        nameTv = (TextView)findViewById(R.id.name_tv);
        smallBtn = (Button)findViewById(R.id.small_btn);
        bigBtn = (Button)findViewById(R.id.big_btn);
        mohuRlt = (RelativeLayout)findViewById(R.id.mohu_rlt);
        contentLlt = (LinearLayout)findViewById(R.id.content_llt);
    }

    private void initData() {

        if(getIntent().getSerializableExtra("waitListResponse") != null){
            waitListResponse = (WaitListResponse)getIntent().getSerializableExtra("waitListResponse");
            WaiterVo waiterVo = waitListResponse.getData().get(currentIndex);
            if(waiterVo != null){
               resetWait(waiterVo);
            }
        }

    }

    private void jumpToSuccessPay(int money){
        float currentMoney = CacheUtil.getInstance().getAccount();
        currentMoney = currentMoney - money;
        CacheUtil.getInstance().setAccount(currentMoney);
        Intent intent = new Intent(mContext,TipSuccesActivity.class);
        TipsResultVo tipsResultVo = new TipsResultVo();
        WaiterVo waiterVo = waitListResponse.getData().get(currentIndex);
        tipsResultVo.setWaiterVo(waiterVo);
        tipsResultVo.setPrice(money);
        intent.putExtra("tipsResultVo",tipsResultVo);
        startActivity(intent);
    }

    private void initListener() {
        smallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToSuccessPay(10);
            }
        });

        bigBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToSuccessPay(20);
            }
        });


        gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                int left = contentLlt.getLeft();
                int top = contentLlt.getTop();
                int bottom = contentLlt.getBottom();
                int right = contentLlt.getRight();
                float x = e.getX();
                float y = e.getY();
                if(x>left && x < right && y > top && y < bottom){
                    return false;
                }else{
                    finish();
                    overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY) {
                if(e1 != null && e2 != null){
                    //float y = Math.abs(e2.getY() - e1.getY());
                    float x = Math.abs(e2.getX() - e1.getX());
                    if(e1.getY() - e2.getY() > 50 && x < 80 ){
                        //Toast.makeText(mContext,"up fling", Toast.LENGTH_SHORT).show();
                        if(flingAble){
                            flingAble = false;
                            nextWaiter();
                        }

                    }
                }

                return false;
            }
        });

    }

    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }


    private void nextWaiter(){
        final long time = 300;
        ViewHelper.setTranslationY(contentLlt,0);
        final int offsetY = contentLlt.getBottom();
        ViewPropertyAnimator.animate(contentLlt).translationYBy(-offsetY).setDuration(time).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                int length = waitListResponse.getData().size();
                currentIndex = (currentIndex+1)%length;
                resetWait(waitListResponse.getData().get(currentIndex));
                int screenHeight = DisplayUtil.getHeightPixel(GiveTipsActivity.this);
                //ViewHelper.setTranslationY(contentLlt,screenHeight);
                ViewPropertyAnimator.animate(contentLlt).translationYBy(offsetY).setDuration(time).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        flingAble = true;
                    }
                });
            }
        });
    }

    private void resetWait(WaiterVo waiterVo) {
        nameTv.setText(waiterVo.getUsername());
        String avatarUrl = ProtocolUtil.getHostImgUrl(waiterVo.getUserimage());
        avatarSdv.setImageURI(Uri.parse(avatarUrl));
    }


}
