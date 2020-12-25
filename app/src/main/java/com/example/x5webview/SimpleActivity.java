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
    private String webUrl = "https://www.8000cloud.com/peerconnection_onebyone/room.html?room=1234";
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
        // initView();
    }

    private void initView() {
        progressBar = findViewById(R.id.pb_webview_load);
        webView = findViewById(R.id.wv_web);
        initWebView();
        webView.loadUrl(webUrl);
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

    private void initContainer() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.loadUrl("https://www.8000cloud.com/peerconnection_onebyone/room.html?room=1234");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        //判断页面加载的过程
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                if (newProgress == 100) {
                    // 网页加载完成
                    Log.d("加载完成...", "success");
                } else {
                    // 加载中
                    Log.d("加载中...", +newProgress + "");
                }
            }
        });
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

//        @Override
//        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
//            sslErrorHandler.proceed();
//        }

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

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            //要做的事情
            super.handleMessage(msg);
            Log.d("mytag", "发送消息");
//            Intent intent = new Intent(WebActivity.this, SimpleActivity.class);
//            startActivity(intent);
//            boolean b = QbSdk.canLoadX5(getApplicationContext());
//            Log.d("mytag", "onInstallFinish  是否可以加载X5内核 -->" + b);
//            if (b){
//                initContainer();
//            }
        }
    };

    public class MyThread implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                Thread.sleep(5000);//线程暂停10秒，单位毫秒
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);//发送消息
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void requestPermission() {
        initX5();
        mHelper = new PermissionHelper(this);
        mHelper.requestPermissions("获取权限",
                new PermissionHelper.PermissionListener() {
                    @Override
                    public void doAfterGrand(String... permission) {
                        boolean b = QbSdk.canLoadX5(getApplicationContext());
                        Log.d("mytag", "是否可以加载X5内核 -->" + b);
                        if (!b) {
                            initX5();
                        }else{
                            initContainer();
                        }

                        QbSdk.setDownloadWithoutWifi(true);
                        QbSdk.setTbsListener(new TbsListener() {
                            @Override
                            public void onDownloadFinish(int i) {
                                Log.d("mytag", "onDownloadFinish -->下载X5内核完成：" + i);
                            }

                            @Override
                            public void onInstallFinish(int i) {
                                Log.d("mytag", "onInstallFinish -->安装X5内核进度：" + i);
//                                boolean b = QbSdk.canLoadX5(getApplicationContext());
//                                Log.d("mytag", "onInstallFinish  是否可以加载X5内核 -->" + b);
                                // initX5();
//
                                new Thread(new MyThread()).start();
                            }

                            @Override
                            public void onDownloadProgress(int i) {
                                Log.d("mytag", "onDownloadProgress -->下载X5内核进度：" + i);
                            }
                        });
//                        HashMap map = new HashMap();
//                        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
//                        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
//                        QbSdk.initTbsSettings(map);
//                        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
//
//                            @Override
//                            public void onViewInitFinished(boolean arg0) {
//                                // TODO Auto-generated method stub
//                                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
//                                Log.d("mytag", " onViewInit is " + arg0);
//                                initContainer();
//                            }
//
//                            @Override
//                            public void onCoreInitFinished() {
//                                Log.d("mytag", "finsih");
//                            }
//                        };
//                        //x5内核初始化接口
//                        QbSdk.initX5Environment(getApplicationContext(), cb);

                    }

                    @Override
                    public void doAfterDenied(String... permission) {
                        Log.d("tag", "权限不足");
                        mHelper.againWarnRequestPermission("获取权限", SimpleActivity.this);
                    }
                }, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.READ_PHONE_STATE);
    }

    private void initX5() {
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("mytag", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                Log.d("mytag", "finsih");
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    //授权回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mHelper != null)
            mHelper.handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }

//    private void requestPermission() {
//        String[] perms = {Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAPTURE_AUDIO_OUTPUT,Manifest.permission.CAMERA,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
//        if (EasyPermissions.hasPermissions(this, perms)) {
//
//        } else {
//            // Do not have permissions, request them now
//            EasyPermissions.requestPermissions(this, "申请权限",
//                    RC_CAMERA_AND_LOCATION, perms);
//        }
//    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        // Forward results to EasyPermissions
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
//    }


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
//            String lastPath = getCurrentPath();
//            if ("paper".equals(lastPath) && isPreventBack) {
//                showWarningDialog();
//            } else {
//                webView.goBack();
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    protected void onUserLeaveHint() {
//        super.onUserLeaveHint();
//        String lastPath = getCurrentPath();
//        if ("paper".equals(lastPath) && isPreventBack) {
//            webView.evaluateJavascript("javascript:clickHome()", new ValueCallback<String>() {
//                @Override
//                public void onReceiveValue(String value) {
//                    //此处为 js 返回的结果
//                    Log.d("tag", "发送成功");
//                }
//            });
//        }
//    }

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
