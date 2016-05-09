package com.zkjinshi.svip.activity.indoor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zkjinshi.pyxis.bluetooth.IBeaconObserver;
import com.zkjinshi.pyxis.bluetooth.IBeaconVo;
import com.zkjinshi.pyxis.bluetooth.NetBeaconVo;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.blueTooth.BlueToothManager;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.vo.AreaVo;

import org.altbeacon.beacon.Region;

import java.util.ArrayList;

/**
 *
 * 室内导航地图
 * 开发者：JimmyZhang
 * 日期：2016/5/7
 * Copyright (C) 2016 深圳中科金石科技有限公司
 * 版权所有
 */
public class IndoorMapActivity extends BaseActivity implements View.OnTouchListener,IBeaconObserver {

    static final float MIN_ZOOM_SCALE = 0.1f;
    static final float MAX_ZOOM_SCALE = 4.0f;
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    static final float MIN_FINGER_DISTANCE = 10.0f;
    public static final int UPDATE_MAP = 3;

    int gestureMode;
    PointF prevPoint;
    PointF nowPoint;
    PointF midPoint;
    float prevFingerDistance;
    Matrix matrix;
    ImageView mImageView;
    private ImageButton backIBtn;
    private TextView titleTv;
    private ImageView locationIv;
    Bitmap mBitmap;
    DisplayMetrics mDisplayMetrics;
    private float imageScale = 1.0f;
    private AreaVo areaVo;

    private void initView(){
        mImageView = (ImageView) findViewById(R.id.floors_indoor_map_iv);
        backIBtn = (ImageButton)findViewById(R.id.btn_back);
        titleTv = (TextView)findViewById(R.id.title_tv);
        locationIv = (ImageView)findViewById(R.id.indoor_location_iv);
    }

    private void initData(){
        gestureMode = NONE;
        prevPoint = new PointF();
        nowPoint = new PointF();
        midPoint = new PointF();
        matrix = new Matrix();
        try {
            mBitmap = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_first_floor);
            mImageView.setImageBitmap(mBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mImageView.setOnTouchListener(this);

        mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics); // 获取屏幕信息（分辨率等）

        locationIv.setVisibility(View.GONE);

        initImageMatrix();
        mImageView.setImageMatrix(matrix);

        areaVo = BlueToothManager.getInstance().getNearestArea(IndoorMapActivity.this);
        if(null != areaVo){
            updateMap(areaVo);
        }
        if(CacheUtil.getInstance().isServiceSwitch()){
            BlueToothManager.getInstance().addObserver(this);
        }
    }

    private void initListeners(){

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        backIBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_map);
        initView();
        initData();
        initListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BlueToothManager.getInstance().removeObserver(this);
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: // 主点按下
                gestureMode = DRAG;
                prevPoint.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_POINTER_DOWN: // 副点按下
                prevFingerDistance = getFingerDistance(event);
                if (getFingerDistance(event) > MIN_FINGER_DISTANCE) {
                    gestureMode = ZOOM;
                    setMidpoint(midPoint, event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                locationIv.setVisibility(View.GONE);
                if (gestureMode == DRAG) {
                    nowPoint.set(event.getX(), event.getY());
                    matrix.postTranslate(nowPoint.x - prevPoint.x, nowPoint.y
                            - prevPoint.y);
                    prevPoint.set(nowPoint);
                } else if (gestureMode == ZOOM) {
                    float currentFingerDistance = getFingerDistance(event);
                    if (currentFingerDistance > MIN_FINGER_DISTANCE) {
                        float zoomScale = currentFingerDistance
                                / prevFingerDistance;
                        matrix.postScale(zoomScale, zoomScale, midPoint.x,
                                midPoint.y);
                        prevFingerDistance = currentFingerDistance;
                    }
                    checkImageViewSize();
                }
                mImageView.setImageMatrix(matrix);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                gestureMode = NONE;
                break;
        }
        return true;
    }

    /**
     * 限制图片缩放比例：不能太小，也不能太大
     */
    private void checkImageViewSize() {
        float p[] = new float[9];
        matrix.getValues(p);
        if (gestureMode == ZOOM) {
            if (p[0] < MIN_ZOOM_SCALE) {
                float tScale = MIN_ZOOM_SCALE / p[0];
                matrix.postScale(tScale, tScale, midPoint.x, midPoint.y);
            } else if (p[0] > MAX_ZOOM_SCALE) {
                float tScale = MAX_ZOOM_SCALE / p[0];
                matrix.postScale(tScale, tScale, midPoint.x, midPoint.y);
            }
        }
    }

    private void initImageMatrix() {
        imageScale =(Math.min(1.0f, Math.min(
                (float) mDisplayMetrics.widthPixels
                        / (float) mBitmap.getWidth(),
                (float) mDisplayMetrics.heightPixels
                        / (float) mBitmap.getHeight())))*2;
        if (imageScale < 1.0f) { // 图片比屏幕大，需要缩小
            matrix.postScale(imageScale, imageScale);
        }
        RectF rect = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        matrix.mapRect(rect); // 按 initImageScale 缩小矩形，或者不变
        float dx = (mDisplayMetrics.widthPixels - rect.width()) / 2.0f;
        float dy = (mDisplayMetrics.heightPixels - rect.height()) / 2.0f;
        matrix.postTranslate(dx, dy);
    }

    /**
     * 根据坐标点移动
     * @param x
     * @param y
     */
    private void setPostTranslate(float x,float y){
        matrix.reset();
        imageScale = (Math.min(1.0f, Math.min(
                (float) mDisplayMetrics.widthPixels
                        / (float) mBitmap.getWidth(),
                (float) mDisplayMetrics.heightPixels
                        / (float) mBitmap.getHeight())))*2;
        if (imageScale < 1.0f) { // 图片比屏幕大，需要缩小
            matrix.postScale(imageScale, imageScale);
        }
        RectF rect = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        matrix.mapRect(rect); // 按 initImageScale 缩小矩形，或者不变
        float dx = (mDisplayMetrics.widthPixels  / 2.0f) -  x * (imageScale >= 1 ? 1: imageScale);
        float dy = (mDisplayMetrics.heightPixels / 2.0f) - y * (imageScale >= 1 ? 1: imageScale);
        matrix.postTranslate(dx, dy);
        mImageView.setImageMatrix(matrix);
        mImageView.invalidate();
    }

    private float getFingerDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void setMidpoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2.0f, y / 2.0f);
    }

    @Override
    public void intoRegion(IBeaconVo iBeaconVo) {
        handler.sendEmptyMessage(UPDATE_MAP);
    }

    @Override
    public void outRegin(IBeaconVo iBeaconVo) {

    }

    @Override
    public void sacnBeacon(IBeaconVo iBeaconVo) {

    }

    @Override
    public void exitRegion(Region region) {

    }

    @Override
    public void postCollectBeacons() {

    }

    public  Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what == UPDATE_MAP){
                areaVo = BlueToothManager.getInstance().getNearestArea(IndoorMapActivity.this);
                if(null != areaVo){
                    updateMap(areaVo);
                }
            }
        }
    };

    private void updateMap(AreaVo areaVo){
        locationIv.setVisibility(View.VISIBLE);
        int floor = areaVo.getFloor();
        String title  = areaVo.getLocdesc();
        StringBuffer mapTitle = new StringBuffer();
        if(floor == 1){
            mImageView.setImageResource(R.mipmap.ic_first_floor);
            mapTitle.append("F1-");
        }else {
            mImageView.setImageResource(R.mipmap.ic_second_floor);
            mapTitle.append("F2-");
        }
        if(!TextUtils.isEmpty(title)){
            mapTitle.append(title);
        }
        titleTv.setText(mapTitle.toString());
        int coordX = areaVo.getCoord_x();
        int coordY = areaVo.getCoord_y();
        setPostTranslate(coordX,coordY);

    }
}
