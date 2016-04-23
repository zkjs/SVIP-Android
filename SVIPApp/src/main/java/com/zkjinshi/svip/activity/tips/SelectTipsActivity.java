package com.zkjinshi.svip.activity.tips;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.zkjinshi.svip.vo.TipsResultVo;
import com.zkjinshi.svip.vo.WaiterVo;

import java.util.ArrayList;
import java.util.Random;


/**
 * Created by dujiande on 2016/4/22.
 */
public class SelectTipsActivity extends BaseActivity {

    private Context mContext;
    private double currentMoney = 30;
    private int[] radioIds = {R.id.radio_tv0,R.id.radio_tv1,R.id.radio_tv2,R.id.radio_tv3,R.id.radio_tv4,R.id.radio_tv5};
    private int clickRadioId = -1;

    private int[] tipsIds = {R.id.m50_tv,R.id.m100_tv,R.id.m10_tv,R.id.m5_tv,R.id.m20_tv,R.id.rand_tv};
    private int[] moneys = {50,100,10,5,20,-1};
    private int clickTipsId = R.id.m20_tv;

    private int[] rate = {15,5,30,30,20,0};
    private ArrayList<Integer> selectMoneyList = new ArrayList<Integer>();
    private ArrayList<Integer> selectMoneyRate = new ArrayList<Integer>();

    private View.OnClickListener tipsOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            clickTipsId = view.getId();
            for(int i=0;i<tipsIds.length;i++){
                int id = tipsIds[i];
                int money = moneys[i];
                TextView tipsTv = (TextView)findViewById(id);
                tipsTv.setTag(money);
                if(currentMoney < money){
                    tipsTv.setBackgroundColor(Color.parseColor("#A5A5A5"));
                    tipsTv.setOnClickListener(null);
                }else if(id != clickTipsId){
                    tipsTv.setBackgroundColor(Color.parseColor("#C78118"));
                    tipsTv.setOnClickListener(tipsOnClickListener);
                }else{
                    tipsTv.setBackgroundColor(Color.parseColor("#FF9900"));
                    tipsTv.setOnClickListener(tipsOnClickListener);
                }

            }
        }
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tips);
        mContext = this;

        initView();
        initData();
        initListener();

    }

    private void initView() {
        for(int i=0;i<tipsIds.length;i++){
            int id = tipsIds[i];
            int money = moneys[i];
            TextView tipsTv = (TextView)findViewById(id);
            tipsTv.setTag(money);
            if(currentMoney < money){
                tipsTv.setBackgroundColor(Color.parseColor("#A5A5A5"));
                tipsTv.setOnClickListener(null);
                if(clickTipsId == id){
                    clickTipsId = -1;
                }
            }else if(id != clickTipsId){
                tipsTv.setBackgroundColor(Color.parseColor("#C78118"));
                tipsTv.setOnClickListener(tipsOnClickListener);
            }else{
                tipsTv.setBackgroundColor(Color.parseColor("#FF9900"));
                tipsTv.setOnClickListener(tipsOnClickListener);
            }

            if(currentMoney >= money && id != R.id.rand_tv){
                selectMoneyList.add(money);
                selectMoneyRate.add(rate[i]);
            }
        }
    }

    private void initData() {

    }

    private void initListener() {
        for(int i=0;i<radioIds.length;i++){
            findViewById(radioIds[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickRadioId = view.getId();
                    for(int i= 0;i<radioIds.length;i++){
                        int id = radioIds[i];
                        TextView radioTv = (TextView)findViewById(id);
                        if(id == clickRadioId){
                            radioTv.setBackgroundResource(R.drawable.radio_select_shape);
                        }else{
                            radioTv.setBackgroundResource(R.drawable.radio_unselect_shape);
                        }
                    }
                }
            });
        }

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitTips();
            }
        });
    }

    private void submitTips() {
        if(clickTipsId == -1){
            Toast.makeText(this,"请指示选择一种小费。",Toast.LENGTH_SHORT).show();
            return;
        }
        TextView tipsTv = (TextView)findViewById(clickTipsId);
        int money = -1;
        if(clickTipsId == R.id.rand_tv){
            money = randTips();
        }else{
            money = (int)tipsTv.getTag();
        }
        if(money == -1){
            Toast.makeText(this,"没有可用选择的小费",Toast.LENGTH_SHORT).show();
            return;
        }
        String comment = "";
        if(clickRadioId != -1){
            TextView radioTv = (TextView)findViewById(clickRadioId);
            comment = radioTv.getText().toString();
        }

        //Toast.makeText(this,"你选择小费。"+money+comment,Toast.LENGTH_SHORT).show();
        WaiterVo waiterVo = (WaiterVo)getIntent().getSerializableExtra("waiterVo");
        Intent intent = new Intent(mContext,TipSuccesActivity.class);
        TipsResultVo tipsResultVo = new TipsResultVo();
        tipsResultVo.setWaiterVo(waiterVo);
        tipsResultVo.setPrice(money);
        intent.putExtra("tipsResultVo",tipsResultVo);
        startActivity(intent);
        finish();
    }

    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }


    public int randTips(){
        int num = 0;
        for(int i=0;i<selectMoneyRate.size();i++){
            num +=selectMoneyRate.get(i);
        }
        Random rand = new Random();
        int randNum = rand.nextInt(num);
        int total = 0;
        for(int i=0;i<selectMoneyRate.size();i++){
            int left = total;
            int right = total + selectMoneyRate.get(i);
            total = right;
            if(randNum >= left && randNum <= right){
                return selectMoneyList.get(i);
            }
        }
        return -1;
    }

}
