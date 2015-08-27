package com.zkjinshi.svip.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zkjinshi.svip.R;


/**
 * Created by winkyqin on 2015/5/7.
 * 自定义设置item
 */
public class ItemUserSettingView extends RelativeLayout{

    private TextView  mTextTitle;       //功能说明
    private TextView  mTextContent1;    //内容1
    private TextView  mTextContent2;    //内容2
    private TextView  mTextSlash;       //斜杠

    public ItemUserSettingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemUserSettingView(Context context) {
        this(context, null);
    }

    public ItemUserSettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);

        String attrsNameSpace = "http://schemas.android.com/apk/res-auto";
        String textTitle    = attrs.getAttributeValue(attrsNameSpace, "itemSettingTitle");
        String textContent1 =  attrs.getAttributeValue(attrsNameSpace, "itemSettingContent1");
        String textContent2 =  attrs.getAttributeValue(attrsNameSpace, "itemSettingContent2");
        String textContent  =  attrs.getAttributeValue(attrsNameSpace, "itemTextSlash");

        mTextTitle.setText(textTitle);
        mTextContent1.setText(textContent1);
        mTextContent2.setText(textContent2);
        mTextSlash.setText(textContent);
    }

    /**
     * 自定义view的初始化
     */
    private void initView(Context context){
        View.inflate(context, R.layout.item_user_info_setting, this);

        mTextTitle    = (TextView)  findViewById(R.id.tv_text_title);
        mTextContent1 = (TextView)  findViewById(R.id.tv_text_content1);
        mTextContent2 = (TextView)  findViewById(R.id.tv_text_content2);
        mTextSlash    = (TextView)  findViewById(R.id.tv_text_slash);
    }

    /**
     * 设置textTitle
     * @param textTitle
     */
    public void setTextTitle(String textTitle){
        mTextTitle.setText(textTitle);
    }

    /**
     * 设置参数1
     * @param textContent1
     */
    public void setTextContent1(String textContent1){
        mTextContent1.setText(textContent1);
    }

    /**
     * 设置参数2
     * @param textContent2
     */
    public void setTextContent2(String textContent2){
        mTextContent2.setText(textContent2);
    }

    /**
     * 取参数2
     *
     */
    public String getTextContent2(){
        if(mTextContent2.getText() != null){
            return mTextContent2.getText().toString();
        }
        return "";
    }

    /**
     * 设置斜线填充
     * @param textSlash
     */
    public void setTextSlash(String textSlash){
        mTextSlash.setText(textSlash);
    }

    public TextView getmTextTitle() {
        return mTextTitle;
    }

    public void setmTextTitle(TextView mTextTitle) {
        this.mTextTitle = mTextTitle;
    }

    public TextView getmTextContent1() {
        return mTextContent1;
    }

    public void setmTextContent1(TextView mTextContent1) {
        this.mTextContent1 = mTextContent1;
    }

    public TextView getmTextContent2() {
        return mTextContent2;
    }

    public void setmTextContent2(TextView mTextContent2) {
        this.mTextContent2 = mTextContent2;
    }

    public TextView getmTextSlash() {
        return mTextSlash;
    }

    public void setmTextSlash(TextView mTextSlash) {
        this.mTextSlash = mTextSlash;
    }
}
