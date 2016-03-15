package com.zkjinshi.svip.activity.shop;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.activity.common.CommentListActivity;
import com.zkjinshi.svip.activity.common.LoginActivity;
import com.zkjinshi.svip.activity.order.HotelBookingActivity;
import com.zkjinshi.svip.activity.order.KTVBookingActivity;
import com.zkjinshi.svip.activity.order.NormalBookingActivity;
import com.zkjinshi.svip.base.BaseActivity;
import com.zkjinshi.svip.net.ExtNetRequestListener;
import com.zkjinshi.svip.net.MethodType;
import com.zkjinshi.svip.net.NetRequest;
import com.zkjinshi.svip.net.NetRequestTask;
import com.zkjinshi.svip.net.NetResponse;
import com.zkjinshi.svip.response.ShopDetailResponse;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.ObservableScrollView;
import com.zkjinshi.svip.view.ScrollViewListener;
import com.zkjinshi.svip.vo.ShopVo;

import java.util.ArrayList;

/**
 * 商家详情页面
 * 开发者：JimmyZhang
 * 日期：2015/12/29
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
@SuppressLint("JavascriptInterface")
public class ShopDetailActivity extends BaseActivity {

    public static final String TAG = ShopDetailActivity.class.getSimpleName();

    private ObservableScrollView scrollView;
    private ImageButton backIBtn;
    private TextView titleTv;
    private WebView webView;
    private LinearLayout headLayout;
    private TextView headCutLineTv;
    private String webViewURL;
    private String webContent;
    private ProgressBar loadingBar;
    private String promptStr,sureStr,cancelStr;
    private ArrayList<String> imageList;
    private SlideShowViewController slideShowViewController;
    private Button commitBtn;
    private TextView addressTv,phoneTv,evaluateTv,recommendTv,remindTv;
    private RelativeLayout webViewLayout;
    private RatingBar evaluateRb;
    private RelativeLayout evaluateLayout;
    private int ratingNum;
    private String shopId;
    private ShopVo shopVo;
    private int shopStatus;

    private void initView(){
        backIBtn = (ImageButton) findViewById(R.id.header_bar_btn_back);
        scrollView = (ObservableScrollView)findViewById(R.id.detail_scr_body);
        titleTv = (TextView)findViewById(R.id.header_bar_tv_title);
        webView = (WebView)findViewById(R.id.detail_webview_content);
        headLayout = (LinearLayout) findViewById(R.id.detail_head_layout);
        loadingBar = (ProgressBar)findViewById(R.id.detail_progress_refresh);
        headCutLineTv = (TextView)findViewById(R.id.head_cut_line);
        commitBtn = (Button)findViewById(R.id.btn_send_booking_order);
        addressTv = (TextView)findViewById(R.id.shop_detail_tv_address);
        phoneTv = (TextView)findViewById(R.id.shop_detail_tv_phone);
        evaluateTv = (TextView)findViewById(R.id.shop_detail_tv_evaluate);
        recommendTv = (TextView)findViewById(R.id.shop_detail_tv_recommend);
        remindTv = (TextView)findViewById(R.id.shop_detail_tv_remind);
        evaluateRb = (RatingBar)findViewById(R.id.shop_detail_rb_evaluate);
        evaluateLayout = (RelativeLayout)findViewById(R.id.shop_detail_layout_evaluate);
        webViewLayout = (RelativeLayout)findViewById(R.id.webview_wrapper);
    }

    private void initData(){
        headLayout.getBackground().setAlpha(0);
        titleTv.setTextColor(getResources().getColor(R.color.white));
        headCutLineTv.setVisibility(View.GONE);
        if(null != getIntent() && !TextUtils.isEmpty(getIntent().getStringExtra("shopId"))){
            shopId = getIntent().getStringExtra("shopId");
        }
        titleTv.setText("酒店详情");
        scrollView.fullScroll(ScrollView.FOCUS_UP);
        requestShopDetailTask(shopId);
    }

    /**
     * 初始化WebView控件参数
     */
    public void initWebView() {
        if(!TextUtils.isEmpty(webViewURL)){
            webView.loadUrl(webViewURL);
            webViewURL = "";
        }
        if(!TextUtils.isEmpty(webContent)){
            webView.loadDataWithBaseURL(null,webContent, "text/html", "UTF-8",null);
        }
        if (null != loadingBar) {
            loadingBar.setVisibility(View.VISIBLE);
        }
        initWebViewSetting();
    }


    private void initListeners(){

        //返回
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //可拖动scrollView
        scrollView.setScrollViewListener(new ScrollViewListener() {
            @Override
            public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy) {
                int headerHeight = DisplayUtil.dip2px(ShopDetailActivity.this, 250);
                if (y <= headerHeight && y > 0) {
                    float f = (float) y / (float) headerHeight;
                    headLayout.getBackground().setAlpha((int) (f * 255));
                    titleTv.setTextColor(getResources().getColor(R.color.white));
                    headCutLineTv.setVisibility(View.GONE);
                } else if (y > headerHeight) {
                    headLayout.getBackground().setAlpha(255);
                    titleTv.setTextColor(getResources().getColor(R.color.light_black));
                    headCutLineTv.setVisibility(View.VISIBLE);
                } else {
                    headLayout.getBackground().setAlpha(0);
                    titleTv.setTextColor(getResources().getColor(R.color.white));
                    headCutLineTv.setVisibility(View.GONE);
                }
            }
        });

        //评价页面
        evaluateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopDetailActivity.this, CommentListActivity.class);
                intent.putExtra("shopID",shopId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        initView();
        initData();
        initListeners();
    }

    private void initWebViewSetting() {
        webView.getSettings().setJavaScriptEnabled(true); // 是否支持javascript
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY); // 隐藏滚动条
        webView.getSettings().setDomStorageEnabled(true);// 设置可以使用localStorage
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);// 默认使用缓存
        webView.getSettings().setAppCacheMaxSize(8 * 1024 * 1024);// 缓存最多可以有8M
        webView.getSettings().setAllowFileAccess(true);// 可以读取文件缓存(manifest生效)
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAppCacheEnabled(false);// 应用可以有缓存
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setBuiltInZoomControls(false);// 设置支持缩放
        String dir = this.getApplicationContext()
                .getDir("database", Context.MODE_PRIVATE).getPath();
        webView.getSettings().setDatabasePath(dir);
        webView.getSettings().setGeolocationEnabled(true);// 使用localStorage则必须打开
        webView.addJavascriptInterface(new RemoteInteractiveController(),// WebView启用Javascript脚本执行
                "remoteInteractiveController");
        webView.getSettings().setSupportZoom(false);// 设置webview支持页面缩放
        webView.getSettings().setDatabaseEnabled(true);
        webView.requestFocus();// 使页面获得焦点
        webView.requestFocusFromTouch();// 设置支持获取手势焦点
        webView.setWebViewClient(new WebViewClient() {

            /**
             * 处理安全证书无法处理的情况
             */
            @Override
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error) {
                Log.i(TAG, "onReceivedSslError");
                handler.proceed();// 接收证书 1
                // handler.cancel(); //默认的处理方式，WebView变成空白页 2
                // handleMessage(Message msg); 其他处理 3
            }

            /**
             * 处理浏览器不响应的情况
             */
            @Override
            public void onFormResubmission(WebView view, Message dontResend,
                                           Message resend) {
                // TODO Auto-generated method stub
                Log.i(TAG, "onFormResubmission");
                super.onFormResubmission(view, dontResend, resend);
                view.requestFocusFromTouch();
            }

            /**
             * 打开连接前的事件
             */
            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                // TODO Auto-generated method stub
                Log.i(TAG, "shouldOverrideKeyEvent");
                return super.shouldOverrideKeyEvent(view, event);
            }

            /**
             * 点击事件未被响应时的事件
             */
            @Override
            public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
                // TODO Auto-generated method stub
                Log.i(TAG, "onUnhandledKeyEvent");
                super.onUnhandledKeyEvent(view, event);
            }

            /**
             * 载入页面完成的事件 |同样道理，我们知道一个页面载入完成，于是我们可以关闭loading条，切换程序动作
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                if (null != loadingBar) {
                    loadingBar.setVisibility(View.GONE);
                }
                super.onPageFinished(view, url);
            }

        });

        // 辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等
        webView.setWebChromeClient(new WebChromeClient() {

            /**
             * 通知当前页面的标题
             */
            @Override
            public void onReceivedTitle(WebView view, String title) {
                Log.i(TAG, "onReceivedTitle : " + title);
                super.onReceivedTitle(view, title);
            }

            /**
             * 进度条改变时触发
             */
            @Override
            public void onProgressChanged(WebView view, int progress) {
                Log.i(TAG, "onProgressChanged:" + progress);
            }

            /**
             * 创建另一个WebView时调用
             */
            @Override
            public boolean onCreateWindow(WebView view, boolean dialog,
                                          boolean userGesture, Message resultMsg) {
                Log.i(TAG, "onCreateWindow");
                view.requestFocusFromTouch();
                return super.onCreateWindow(view, dialog, userGesture,
                        resultMsg);
            }

            /**
             * 关闭窗口时调用
             */
            @Override
            public void onCloseWindow(WebView window) {
                super.onCloseWindow(window);

            }

            /**
             * 覆盖js弹出提示框事件
             */
            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                // TODO Auto-generated method stub
                Log.i(TAG, "onJsAlert");
                final AlertDialog.Builder builder = new AlertDialog.Builder(
                        view.getContext());

                builder.setTitle(promptStr).setMessage(message)
                        .setPositiveButton(sureStr, null);

                // 不需要绑定按键事件
                // 屏蔽keycode等于84之类的按键
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    public boolean onKey(DialogInterface dialog, int keyCode,
                                         KeyEvent event) {
                        Log.v("onJsAlert", "keyCode==" + keyCode + "event="
                                + event);
                        return true;
                    }
                });
                // 禁止响应按back键的事件
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
                result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
                return true;
            }

            /**
             * 获得焦点时触发
             */
            @Override
            public void onRequestFocus(WebView view) {
                // TODO
                Log.i(TAG, "onRequestFocus");
                super.onRequestFocus(view);
            }

            /**
             * 覆盖js弹出确认框事件
             */
            @Override
            public boolean onJsConfirm(WebView view, String url,
                                       String message, JsResult result) {

                Log.i(TAG, "onJsConfirm");
                final JsResult newResult = result;
                final AlertDialog.Builder builder = new AlertDialog.Builder(
                        view.getContext());
                builder.setTitle(promptStr)
                    .setMessage(message)
                    .setPositiveButton(
                        sureStr,
                        new android.app.AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                newResult.confirm();
                            }
                        })
                    .setNeutralButton(
                        cancelStr,
                        new android.app.AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                newResult.cancel();
                            }
                        });

                builder.setOnCancelListener(new android.app.AlertDialog.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        newResult.cancel();
                    }
                });

                // 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode,
                                         KeyEvent event) {
                        Log.v("onJsConfirm", "keyCode==" + keyCode + "event=" + event);
                        return true;
                    }
                });

                // 禁止响应按back键的事件
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }

            /**
             * 覆盖js弹出提示框事件
             */
            @Override
            public boolean onJsPrompt(WebView view, String url, String message,
                                      String defaultValue, JsPromptResult result) {
                final JsPromptResult newResult = result;
                Log.i(TAG, "onJsPrompt");
                final AlertDialog.Builder builder = new AlertDialog.Builder(
                        view.getContext());

                builder.setTitle(promptStr).setMessage(message);
                final EditText et = new EditText(view.getContext());
                et.setSingleLine();
                et.setText(defaultValue);
                builder.setView(et)
                        .setPositiveButton(sureStr,
                                new android.app.AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        newResult.confirm(et.getText().toString());
                                    }
                                })
                        .setNeutralButton(cancelStr,
                                new android.app.AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        newResult.cancel();
                                    }
                                });

                // 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    public boolean onKey(DialogInterface dialog, int keyCode,
                                         KeyEvent event) {
                        Log.v("onJsPrompt", "keyCode==" + keyCode + "event="
                                + event);
                        return true;
                    }
                });

                // 禁止响应按back键的事件
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }

            /**
             * 当js代码还未被调用时，触发
             */
            @Override
            public boolean onJsBeforeUnload(WebView view, String url,
                                            String message, JsResult result) {
                Log.i(TAG, "onJsBeforeUnload");
                return super.onJsBeforeUnload(view, url, message, result);
            }

            /**
             * 解决加载本地数据库时报的跨域错误，4.4以下的版本不支持sqlite数据库的容量自增
             */
            @Override
            @Deprecated
            public void onExceededDatabaseQuota(String url,
                                                String databaseIdentifier, long quota,
                                                long estimatedDatabaseSize, long totalQuota,
                                                WebStorage.QuotaUpdater quotaUpdater) {
                quotaUpdater.updateQuota(estimatedDatabaseSize * 2);
            }

        });

    }

    /**
     * 远程交互设计控制类
     *
     */
    private class RemoteInteractiveController {

    }

    /**
     * 请求商家详细信息
     * @param shopId
     */
    private void requestShopDetailTask(final String shopId){
        NetRequest netRequest = new NetRequest(ProtocolUtil.getShopDetailUrl(shopId));
        NetRequestTask netRequestTask = new NetRequestTask(this, netRequest, NetResponse.class);
        netRequestTask.methodType = MethodType.GET;
        netRequestTask.setNetRequestListener(new ExtNetRequestListener(this) {
            @Override
            public void onNetworkRequestError(int errorCode, String errorMessage) {
                Log.i(TAG, "errorCode:" + errorCode);
                Log.i(TAG, "errorMessage:" + errorMessage);
            }

            @Override
            public void onNetworkRequestCancelled() {

            }

            @Override
            public void onNetworkResponseSucceed(NetResponse result) {
                super.onNetworkResponseSucceed(result);
                Log.i(TAG, "result.rawResult:" + result.rawResult);
                ShopDetailResponse shopDetailResponse = new Gson().fromJson(result.rawResult,ShopDetailResponse.class);
                if(null != shopDetailResponse){
                    ArrayList<ShopVo>  shopList = shopDetailResponse.getData();
                    if(null != shopList && !shopList.isEmpty()){
                        shopVo = shopList.get(0);
                        if(null != shopVo){
                            final String shopName = shopVo.getShopname();
                            shopStatus = shopVo.getShopstatus();
                            if(shopStatus == 0){
                                commitBtn.setText("即将入驻");
                                commitBtn.setEnabled(false);
                            }else {
                                commitBtn.setText("立即预定");
                                commitBtn.setEnabled(true);
                            }

                            if(!TextUtils.isEmpty(shopName)){
                                titleTv.setText(shopName);
                            }

                            String address = shopVo.getShopaddress();
                            if(!TextUtils.isEmpty(address)){
                                addressTv.setText(address);
                            }

                            int evaluate = shopVo.getEvaluation();
                            evaluateTv.setText("客人评价("+evaluate+")");

                            String telephone = shopVo.getTelephone();
                            if(!TextUtils.isEmpty(telephone)){
                                phoneTv.setText(telephone);
                            }
                            ratingNum = shopVo.getScore();
                            evaluateRb.setRating(ratingNum);
                            imageList = shopVo.getImages();

                            final int industrycode = shopVo.getIndustrycode();

                            //长点击发送语音订房
//                    commitBtn.setOnLongClickListener(new View.OnLongClickListener() {
//                        @Override
//                        public boolean onLongClick(View v) {
//                            //设置按钮长点击语音听写
//                            //1.创建RecognizerDialog对象
//                            RecognizerDialog mDialog = new RecognizerDialog(
//                                    ShopDetailActivity.this,
//                                    new InitListener() {
//                                        @Override
//                                        public void onInit(int i) {
//                                            //接受语音成功
//                                            LogUtil.getInstance().info(LogLevel.INFO, TAG+"开始语音听写");
//                                        }
//                                    });
//
//                            // 2.设置accent、language等参数
//                            mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
//                            mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
//
//                            // 若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解
//                            mDialog.setParameter("asr_sch", "1");
//                            mDialog.setParameter("nlp_version", "2.0");
//
//                            // 3.设置回调接口
//                            mDialog.setListener(new RecognizerDialogListener() {
//                                @Override
//                                public void onResult(RecognizerResult recognizerResult, boolean b) {
//                                    //接收语音成功
//                                    String result = recognizerResult.getResultString();
//                                    LogUtil.getInstance().info(LogLevel.INFO, TAG + "语音识别成功" + result);
//                                    Gson gson = new Gson();
//                                    MscBookBean mscBookBean = gson.fromJson(result, MscBookBean.class);
//                                    if(null != mscBookBean){
//                                        int rc = mscBookBean.getRc();
//                                        SemanticBean semanticBean = mscBookBean.getSemantic();
//                                        if(null != semanticBean){
//                                            SlotsBean slotsBean = semanticBean.getSlots();
//                                            if(null != slotsBean){
//                                                if(rc != 0){
//                                                    //返回失败
//                                                    //开启语音合成提示
//                                                    goBook(shopId, shopName, industrycode, null);
//                                                } else {
//                                                    if(null != slotsBean) {
//                                                        //时间
//                                                        String date     = slotsBean.date;
//                                                        //房型
//                                                        String roomType = slotsBean.roomType;
//                                                        Bundle bundle = new Bundle();
//                                                        bundle.putString("date", date);
//                                                        bundle.putString("roomType", roomType);
//                                                        goBook(shopId, shopName, industrycode, bundle);
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onError(SpeechError speechError) {
//                                    //语音识别失败
//                                    LogUtil.getInstance().info(LogLevel.INFO, TAG+"语音识别失败" +
//                                                                speechError.getErrorDescription());
//                                }
//                            });
//                            // 4.显示dialog，接收语音输入
//                            mDialog.show();
//                            return false;
//                        }
//                    });

                            //立即预订
                            commitBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    goBook(shopId, shopName, industrycode, null);
                                }
                            });

                            if(null != imageList && !imageList.isEmpty()){
                                slideShowViewController = new SlideShowViewController();
                                slideShowViewController.init(ShopDetailActivity.this, imageList);
                            }

                            promptStr = getResources().getString(R.string.sweet_prompt);
                            sureStr   = getResources().getString(R.string.sure_option);
                            cancelStr = getResources().getString(R.string.cancel_option);
                            String webUrl = shopVo.getShopdesc();
                            if(!TextUtils.isEmpty(webUrl)){
                                webContent = webUrl;
                                webViewLayout.setVisibility(View.VISIBLE);
                            }else {
                                webViewLayout.setVisibility(View.GONE);
                            }
                            initWebView();
                            scrollView.fullScroll(ScrollView.FOCUS_UP);
                        }
                    }
                }
            }

            @Override
            public void beforeNetworkRequestStart() {

            }
        });
        netRequestTask.isShowLoadingDialog = true;
        netRequestTask.execute();
    }

    /**
     * 发起预订服务
     */
    private void goBook(String shopID, String shopName, int industrycode, Bundle bundle) {
        if(!CacheUtil.getInstance().isLogin()){
            Intent intent = new Intent(ShopDetailActivity.this, LoginActivity.class);
            intent.putExtra("isHomeBack",true);
            intent.putExtra("shopid",shopID);
            intent.putExtra("shopName",shopName);

            if(null != imageList && !imageList.isEmpty()){
                intent.putExtra("shopImg",imageList.get(0));
            }
            intent.putExtra("category", industrycode);
            startActivity(intent);
            return;
        } else {
            /**
             行业： 酒店行业  50   KTV休闲  60  其他行业  70，在商家列表及详情中，yo那个后面的数字判断行业
             */
            Intent intent = new Intent();
            if(50 == industrycode){
                intent.setClass(ShopDetailActivity.this, HotelBookingActivity.class);
            }else if(60 == industrycode){
                intent.setClass(ShopDetailActivity.this, KTVBookingActivity.class);
            }else {
                intent.setClass(ShopDetailActivity.this, NormalBookingActivity.class);
            }
            intent.putExtra("shopid",shopId);
            intent.putExtra("shopName",shopName);
            if(null != imageList && !imageList.isEmpty()){
                intent.putExtra("shopImg",imageList.get(0));
            }
            if(null != bundle){
                intent.putExtra("bundle", bundle);
            }
            startActivity(intent);
        }
    }

}
