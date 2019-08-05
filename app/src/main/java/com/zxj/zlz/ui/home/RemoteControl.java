package com.zxj.zlz.ui.home;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.zxj.zlz.R;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class RemoteControl extends AppCompatActivity {

    WebView webView;
    AlertDialog alertDialog;
    //HashMap<String, Long> deviceInfos = new HashMap<String, Long>();
    //DevicesAdapter deviceInfos = new DevicesAdapter();
    String[] provinces = new String[]{"河南省","安徽省","北京市","上海市"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //new ScanfPITask().execute();

        webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.setWebViewClient(new WebViewClient());
        //webView.loadUrl("https://www.baidu.com");
        webView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public void onBackPressed() {
//        if(webView.canGoBack()) {
//            webView.goBack();
//        } else {
//            super.onBackPressed();
//        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
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
}
