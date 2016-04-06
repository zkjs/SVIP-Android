package com.zkjinshi.svip.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.zkjinshi.svip.R;

/**
 * Created by dujiande on 2016/4/6.
 */
public class ShopFragment extends Fragment {

    private TextView tv;
    private View root;
    private View view;

    public boolean isVisiable = false;

    private GestureDetector gestureDetector;
    private GestureDetector.OnGestureListener onGestureListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    float x = e2.getX() - e1.getX();

                    if (x > 0 ) {
                        if(isVisiable){
                            hideAction();
                        }
                    }
                    return true;
                }
            };

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_shop, container,false);
        initUI(root);
        return root;
    }

    private void initUI(final View root) {
        gestureDetector = new GestureDetector(getActivity(),onGestureListener);
        tv = (TextView) root.findViewById(R.id.textView);
        root.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                root.setClickable(false);
                ViewPropertyAnimator.animate(root)
                        .rotationY(90).setDuration(200)
                        .setListener(new AnimatorListenerAdapter(){
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                root.clearAnimation();
                                root.setVisibility(View.INVISIBLE);
                                view.setEnabled(true);
                                isVisiable = false;
                            }
                        });
            }
        });
    }



    public void show(final View view,Bundle bundle){
        this.view = view;
        String text = bundle.getString("text");
        tv.setText(text);
        ViewHelper.setRotationY(view, 0);
        ViewHelper.setRotationY(root, 90);
        root.setVisibility(View.VISIBLE);
        root.setEnabled(true);

        ViewPropertyAnimator.animate(view).rotationY(-90)
                .setDuration(300).setListener(null)
                .setInterpolator(new AccelerateInterpolator());


        ViewPropertyAnimator.animate(root)
                .rotationY(0).setDuration(200).setStartDelay(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewHelper.setRotationY(view, 0);
                        isVisiable = true;
                    }
                });

        root.setClickable(true);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    public void hideAction(){
        root.setClickable(false);
        root.setEnabled(false);
        ViewPropertyAnimator.animate(root)
                .rotationY(90).setDuration(200)
                .setListener(new AnimatorListenerAdapter(){
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        root.clearAnimation();
                        root.setVisibility(View.INVISIBLE);
                        isVisiable = false;
                    }
                });
    }

}
