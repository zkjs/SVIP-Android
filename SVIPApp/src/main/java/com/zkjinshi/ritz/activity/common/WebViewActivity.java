package com.zkjinshi.ritz.activity.common;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zkjinshi.base.util.ClipboardUtil;
import com.zkjinshi.base.util.IntentUtil;
import com.zkjinshi.ritz.R;
import com.zkjinshi.ritz.base.BaseActivity;

/**
 * 轻应用html5载入界面
 * 开发者：JimmyZhang
 * 日期：2015/9/17
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
@SuppressLint("JavascriptInterface")
public class WebViewActivity extends BaseActivity {

    public static String TAG = "WebViewActivity";
    private ImageView prePageIv, nextPageIv, reflushPageIv;
    private WebView webView;
    private String webViewURL;
    private String sureStr, cancelStr, promptStr;
    private ImageButton backIBtn;
    private ImageButton listIBtn;
    private TextView centerTitleBarTv;
    private ProgressBar refreshBar;

    private LayoutInflater inflater;

    @Override
    protected void onPause() {
        super.onPause();
        if (null != webView) {
            webView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != webView) {
            webView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        if (null != webView) {
            webView.getSettings().setBuiltInZoomControls(true);
            webView.setVisibility(View.GONE);// 把destroy()延后
            long timeout = ViewConfiguration.getZoomControlsTimeout();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    webView.destroy();
                }
            }, timeout);
        }
        super.onDestroy();
    }

    /**
     * 初始化界面
     */
    public void initView() {

        backIBtn = (ImageButton) findViewById(R.id.header_bar_btn_back);
        listIBtn = (ImageButton) findViewById(R.id.header_bar_btn_more);
        backIBtn.setVisibility(View.VISIBLE);
        listIBtn.setVisibility(View.VISIBLE);
        centerTitleBarTv = (TextView) findViewById(R.id.header_bar_tv_title);
        centerTitleBarTv.setVisibility(View.VISIBLE);
        centerTitleBarTv.setText(R.string.app_name);
        promptStr = getResources().getString(R.string.sweet_prompt);// 温馨提示
        sureStr = getResources().getString(R.string.sure_option);// 确认
        cancelStr = getResources().getString(R.string.cancel_option);// 取消
        webView = (WebView) findViewById(R.id.webview);
        refreshBar = (ProgressBar) findViewById(R.id.refresh_progress);
        prePageIv = (ImageView) findViewById(R.id.back);
        nextPageIv = (ImageView) findViewById(R.id.forward);
        reflushPageIv = (ImageView) findViewById(R.id.refresh);
        inflater = LayoutInflater.from(WebViewActivity.this);
        Intent intent = getIntent();
        String action = intent.getAction();
        String scheme = intent.getScheme();
        if (null != action && action.equals(Intent.ACTION_VIEW)) {
            if (null != scheme && scheme.startsWith("http")) {
                Uri data = intent.getData();
                String uriString = data.toString();
                if (null != uriString && !"".equals(uriString)) {
                    webViewURL = uriString;
                }
            }
        }
        if (null != intent && null != intent.getStringExtra("webview_url")) {
            webViewURL = intent.getStringExtra("webview_url");
        }
        listIBtn.setTag(webViewURL);
        initWebView();
    }

    /**
     * 注册监听器
     */
    public void initListeners() {

        // 上一页
        prePageIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (webView.canGoBack()) {
                    webView.goBack();
                }
            }
        });

        // 下一页
        nextPageIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (webView.canGoForward()) {
                    webView.goForward();
                }
            }
        });

        // 刷新
        reflushPageIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                webView.reload();
            }
        });

        // 返回
        backIBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);
            }
        });

        // 列表
        listIBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String url = view.getTag().toString();
                showDialog(WebViewActivity.this, url);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initView();
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
        webView.getSettings().setBuiltInZoomControls(true);// 设置支持缩放
        String dir = this.getApplicationContext()
                .getDir("database", Context.MODE_PRIVATE).getPath();
        webView.getSettings().setDatabasePath(dir);
        webView.getSettings().setGeolocationEnabled(true);// 使用localStorage则必须打开
        webView.addJavascriptInterface(new RemoteInteractiveController(),// WebView启用Javascript脚本执行
                "remoteInteractiveController");
        webView.getSettings().setSupportZoom(true);// 设置webview支持页面缩放
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
                Log.i(TAG, "onFormResubmission");
                super.onFormResubmission(view, dontResend, resend);
                view.requestFocusFromTouch();
            }

            /**
             * 打开连接前的事件
             */
            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                Log.i(TAG, "shouldOverrideKeyEvent");
                return super.shouldOverrideKeyEvent(view, event);
            }

            /**
             * 点击事件未被响应时的事件
             */
            @Override
            public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
                Log.i(TAG, "onUnhandledKeyEvent");
                super.onUnhandledKeyEvent(view, event);
            }

            /**
             * 载入页面完成的事件 |同样道理，我们知道一个页面载入完成，于是我们可以关闭loading条，切换程序动作
             */
            @Override
            public void onPageFinished(WebView view, String url) {

                if (null != refreshBar) {
                    refreshBar.setVisibility(View.GONE);
                }

                if (webView.canGoBack()) {
                    prePageIv.setEnabled(true);
                } else {
                    prePageIv.setEnabled(false);
                }

                if (webView.canGoForward()) {
                    nextPageIv.setEnabled(true);

                } else {
                    nextPageIv.setEnabled(false);
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
                if (null != centerTitleBarTv && null != title
                        && !"".equals(title)) {
                    centerTitleBarTv.setText(title);
                }
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
                // TODO Auto-generated method stub
                Log.i(TAG, "onJsConfirm");
                final JsResult newResult = result;
                final AlertDialog.Builder builder = new AlertDialog.Builder(
                        view.getContext());
                builder.setTitle(promptStr)
                        .setMessage(message)
                        .setPositiveButton(sureStr,
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        newResult.confirm();
                                    }
                                })
                        .setNeutralButton(cancelStr,
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        newResult.cancel();
                                    }
                                });
                builder.setOnCancelListener(new AlertDialog.OnCancelListener() {
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
                        Log.v("onJsConfirm", "keyCode==" + keyCode + "event="
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
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        newResult.confirm(et.getText()
                                                .toString());
                                    }

                                })
                        .setNeutralButton(cancelStr,
                                new AlertDialog.OnClickListener() {
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

        //新增下载监听
		webView.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition,
					String mimetype, long contentLength) {
				IntentUtil.openBrowser(url, WebViewActivity.this);
			}
		});

    }

    /**
     * 初始化WebView控件参数
     */
    public void initWebView() {

        webView.loadUrl(webViewURL);
        webViewURL = "";
        if (null != refreshBar) {
            refreshBar.setVisibility(View.VISIBLE);
        }
        initWebViewSetting();
    }

    /**
     * 远程交互设计控制类
     *
     */
    private class RemoteInteractiveController {

    }

    /**
     * 提示操作确认框
     *
     */
    protected void showDialog(final Context context, final String url) {
        final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.listsheet_dialog, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        Button titleBtn = (Button) layout.findViewById(R.id.dialog_btn_title);
        titleBtn.setText(context.getString(R.string.option_prompty));
        Button mianqiantaiBtn = (Button)layout.findViewById(R.id.dialog_btn_mianqiantai);//免前台
        mianqiantaiBtn.setVisibility(View.GONE);
        Button copeBtn = (Button) layout.findViewById(R.id.dialog_btn_cope_url);// 复制链接
        Button cancelBtn = (Button) layout.findViewById(R.id.dialog_btn_cancel);// 取消
        Button browserBtn = (Button)layout.findViewById(R.id.dialog_btn_open_browser);
        mianqiantaiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        copeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dlg.dismiss();
                ClipboardUtil.copy(url, context);
            }
        });
        browserBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                IntentUtil.openBrowser(url, context);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);
        dlg.show();
    }

}
