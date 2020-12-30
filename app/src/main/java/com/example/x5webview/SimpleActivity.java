package com.example.x5webview;

import android.Manifest;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.HashMap;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by liuyin on 2019/4/15 14:28
 * Describe: 加载Web页面
 */
public class SimpleActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private WebView webView;

    //private String webUrl = "https://192.168.199.100:3001/examination";
    // private String webUrl = "https://www.8000cloud.com/peerconnection_onebyone/room.html?room=1234";
    private String webUrl = "http://recruit2.sit.zretchome.com/mutual-portal/";
    //private String webUrl = "http://192.168.199.100:8848/htmlDemo/file.html";

    private ValueCallback valueCallback;

    private int RC_CAMERA_AND_LOCATION = 100;
    //是否阻止退出
    private boolean isPreventBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        progressBar = findViewById(R.id.pb_webview_load);
        webView = findViewById(R.id.wv_web);
        requestPermission();
    }

    private void initView() {
        progressBar = findViewById(R.id.pb_webview_load);
        webView = findViewById(R.id.wv_web);
        initWebView();
        webView.loadUrl(webUrl);
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }


    public void initWebView() {

        WebSettings webSetting = webView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);

        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        // 当页面宽度大于WebView宽度时，缩小使页面宽度等于WebView宽度
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setGeolocationEnabled(true);


        webSetting.setAppCacheEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);


        webView.addJavascriptInterface(new InteractionChannel(), "androidJSBridge");

        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
//
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    webView.getSettings()
//                            .setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//                }
//                handler.proceed();
//            }
//        });

    }

    // Web视图
    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }


        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //加载完成后可以调用这个查看HTML源码
            //webView.loadUrl("javascript:android.processHTML(document.documentElement.outerHTML);");

        }
    }

    protected class MyWebChromeClient extends WebChromeClient {


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            valueCallback = filePathCallback;
            Toast.makeText(SimpleActivity.this, "选择图片", Toast.LENGTH_SHORT).show();
            if (fileChooserParams.isCaptureEnabled()) {

            } else {
            }
            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                progressBar.setVisibility(GONE);
            } else if (progressBar.getVisibility() == GONE) {
                progressBar.setVisibility(VISIBLE);
            }
            progressBar.setProgress(newProgress);
        }
    }

    private PermissionHelper mHelper;

    private void requestPermission() {
        mHelper = new PermissionHelper(this);
        mHelper.requestPermissions("获取权限",
                new PermissionHelper.PermissionListener() {
                    @Override
                    public void doAfterGrand(String... permission) {
                        initView();

                    }

                    @Override
                    public void doAfterDenied(String... permission) {
                        Log.d("tag", "权限不足");
                        mHelper.againWarnRequestPermission("获取权限", SimpleActivity.this);
                    }
                }, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    //授权回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mHelper != null)
            mHelper.handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    //js和java交互通道
    class InteractionChannel {
        @JavascriptInterface
        public void listenBack(boolean isPrevent) {
            isPreventBack = isPrevent;
            Log.d("tag", "preventBack" + isPreventBack);
        }
    }

    private String getCurrentPath() {
        String url = webView.getUrl();
        String[] urlArr = url.split("/");
        String lastPath = urlArr[urlArr.length - 1];
        return lastPath;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.stopLoading();
            //
//            webView.clearCache(true);
//            webView.clearHistory();
//            webView.pauseTimers();
            webView.destroy();
            webView = null;

        }
    }
}
