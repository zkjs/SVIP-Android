package com.zkjinshi.svip.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

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

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_shop, container,false);
        initUI(root);
        return root;
    }

    private void initUI(final View root) {
        root.setClickable(true);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });
        tv = (TextView) root.findViewById(R.id.textView);
        root.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                root.setClickable(false);
                ViewPropertyAnimator.animate(root)
                        .rotationY(-90).setDuration(200)
                        .setListener(new AnimatorListenerAdapter(){
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                root.clearAnimation();
                                root.setVisibility(View.INVISIBLE);
                                view.setEnabled(true);
                            }
                        });
            }
        });
    }

    public void show(final View view,Bundle bundle){
        view.setEnabled(false);
        this.view = view;
        String text = bundle.getString("text");
        tv.setText(text);
        ViewHelper.setRotationY(view, 0);
        ViewHelper.setRotationY(root, -90);
        root.setVisibility(View.VISIBLE);

        ViewPropertyAnimator.animate(view).rotationY(90)
                .setDuration(300).setListener(null)
                .setInterpolator(new AccelerateInterpolator());


        ViewPropertyAnimator.animate(root)
                .rotationY(0).setDuration(200).setStartDelay(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewHelper.setRotationY(view, 0);
                    }
                });
    }

    public void hide(){

    }
}
