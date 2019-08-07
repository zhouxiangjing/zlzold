package com.zxj.zlz.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.zxj.zlz.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class RemoteControl extends Activity {

    WebView webView;

    String htmlDataStart = "<!DOCTYPE HTML>\n" +
            "<html>\n" +
            "<body style=\"overflow-x: hidden;overflow-y: hidden;margin: 0; padding: 0;\">\n" +
            "    <div style=\"position:absolute; width:100%; height:100%; z-index:-1\">\n" +
            "        <img style=\"-webkit-user-select:none; max-width: 100%; margin: 0; padding: 0;\" src=\"";

    String htmlDataEnd = "\" height=\"100%\" width=\"100%\"/>\n" +
    "    </div>\n" +
    "</body>\n" +
    "</html>";

    ScanfPITask scanfPITask;
    TextView tvDeviceAddr;
    boolean isFinished = false;
    int isStopLoading = -1;
    String deviceAddr = "000.000.000.000";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);
        Logger.i("onCreate");

        tvDeviceAddr = findViewById(R.id.device_addr);
        scanfPITask = new ScanfPITask();
        scanfPITask.execute();
        initWebView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.i("onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.i("onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.i("onRestart");
    }

    @Override
    public void onStateNotSaved() {
        super.onStateNotSaved();
        Logger.i("onStateNotSaved");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.i("onResume");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Logger.i("onPostResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.i("onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.i("onDestroy");

        if(scanfPITask !=null && scanfPITask.getStatus() == AsyncTask.Status.RUNNING){
            scanfPITask.cancel(true);
        }

        if ((webView == null) && webView.canGoBack()) {
            webView.stopLoading();
            webView.goBack();
            webView.clearCache(true);
            webView.clearHistory();
            webView.destroy();
        }
    }

    private class ScanfPITask extends AsyncTask<Void, String, Void> {

        private static final int BROADCAST_PORT = 10001;

        @Override
        protected Void doInBackground(Void... voids) {
            Logger.i("ScanfPITask start.");

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
                        String addr = packet.getAddress().toString().replaceAll("/", "");
                        publishProgress(addr);
                    }
                    if(isFinishing())
                        break;
                }
                dgSocket.close();
            } catch (IOException e) {
                Logger.i("udp init faild. " + e.getMessage());
            }
            Logger.i("ScanfPITask end.");
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String data =  values[0];
            if(!data.equals(deviceAddr)) {
                deviceAddr = data;
                tvDeviceAddr.setText(deviceAddr);
                isStopLoading = 1;
                webView.loadData(htmlDataStart + "http://" + deviceAddr + ":8080/?action=stream" + htmlDataEnd, "text/html", "utf-8");
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public void initWebView() {
        webView = findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webSettings.setUseWideViewPort(true);
        //webSettings.setAllowFileAccess(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        //webView.loadData(htmlDataStart + "http://" + "192.168.31.202" + ":8080/?action=stream" + htmlDataEnd, "text/html", "utf-8");
        //webView.loadUrl("file:///android_asset/index.html");
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
