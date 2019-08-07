package com.zxj.zlz.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.zxj.zlz.R;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class RemoteControl extends Activity {

    WebView webView;
    AlertDialog alertDialog;
    //HashMap<String, Long> deviceInfos = new HashMap<String, Long>();
    //DevicesAdapter deviceInfos = new DevicesAdapter();
    String[] provinces = new String[]{"河南省","安徽省","北京市","上海市"};

    /** 视频全屏参数 */
    protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private View customView;
    private FrameLayout fullscreenContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);

        initWebView();

        //new ScanfPITask().execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        webView.stopLoading();
    }

    public void onClickDeviceAddr(View view) {

        alertDialog = new AlertDialog.Builder(RemoteControl.this)
        .setTitle("选择省份")
        .setSingleChoiceItems(provinces, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //index是一个int类型的类变量，保存当前选中的列表项索引
                final int index = which;
                alertDialog.dismiss();
            }

        }).setPositiveButton("关闭", null).show();
    }

    private class ScanfPITask extends AsyncTask<Void, String, String> {

        private static final int BROADCAST_PORT = 10001;

        @Override
        protected String doInBackground(Void... voids) {
            Logger.i("ScanfPITask start.");
            String addr = "";
            try {
                DatagramSocket dgSocket = new DatagramSocket(BROADCAST_PORT);
                dgSocket.setReuseAddress(true);
                dgSocket.setBroadcast(true);

                byte[] by = new byte[512];
                DatagramPacket packet = new DatagramPacket(by, by.length);
                while(true) {
                    dgSocket.receive(packet);
                    String signData = new String(packet.getData());
                    if(signData.contains("~")) {
                        publishProgress(packet.getAddress().toString().replaceAll("/", ""));
                    }
                    if(isFinishing())
                        break;
                }

            } catch (IOException e) {
                Logger.i("udp init faild." + e.getMessage());
            }
            Logger.i("ScanfPITask end.");
            return addr;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Logger.i("udp addr." + values[0]);
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);


        }
    }

    public void initWebView() {
        webView = findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());

        // 加载Web地址
        webView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if ((webView == null) && webView.canGoBack()) {
                    webView.goBack();
                }
                finish();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }
    }

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public View getVideoLoadingProgressView() {
            return super.getVideoLoadingProgressView();
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
        }
    }
}
