package com.zkjinshi.svip.activity.tips;

import android.content.Context;
import android.content.Intent;
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
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;


/**
 * Created by dujiande on 2016/4/22.
 */
public class GiveTipsActivity extends BaseActivity {

    private Context mContext;

    private SimpleDraweeView avatarSdv;
    private Button smallBtn,bigBtn;
    private TextView nameTv;
    private RelativeLayout mohuRlt;
    private LinearLayout contentLlt;

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

    }

    private void initListener() {
        smallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bigBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

//        mohuRlt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
//            }
//        });



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
                    if(e1.getY() - e2.getY() > 80 && x < 80 ){
                        //Toast.makeText(mContext,"up fling", Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent bIntent = new Intent(mContext,GiveTipsActivity.class);
                                bIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(bIntent);
                                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                            }
                        },500);
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


}
