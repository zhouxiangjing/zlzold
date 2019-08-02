package com.zxj.zlz;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.zxj.utils.Jni;
import com.zxj.zlz.ui.blog.Blog;
import com.zxj.zlz.ui.blog.BlogPaper;
import com.zxj.zlz.ui.blog.FragmentBlogs;
import com.zxj.zlz.ui.home.FragmentHome;
import com.zxj.zlz.ui.home.RemoteControl;
import com.zxj.zlz.ui.mine.FragmentMine;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements
        FragmentHome.OnFragmentInteractionListener,
        FragmentBlogs.OnListFragmentInteractionListener,
        FragmentMine.OnFragmentInteractionListener{

    private FragmentManager mFragmentManager;
    private FragmentHome mFragmentHome;
    private FragmentBlogs mFragmentBlogs;
    private FragmentMine mFragmentMine;
    private int lastShowFragment = 0;

    private int connectStatus = 0;

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt("lastShowFragment", lastShowFragment);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
                //.logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag("ZLZLOG")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));

        mFragmentManager = getSupportFragmentManager();
        if(null != savedInstanceState) {
            try {
                mFragmentHome = (FragmentHome)mFragmentManager.findFragmentByTag(FragmentHome.class.getSimpleName());
                mFragmentBlogs = (FragmentBlogs)mFragmentManager.findFragmentByTag(FragmentBlogs.class.getSimpleName());
                mFragmentMine = (FragmentMine)mFragmentManager.findFragmentByTag(FragmentMine.class.getSimpleName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            lastShowFragment = savedInstanceState.getInt("lastShowFragment");
        }

        switchFrament(lastShowFragment);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        switchFrament(0);
                        return true;
                    case R.id.navigation_dashboard:
                        switchFrament(1);
                        return true;
                    case R.id.navigation_notifications:
                        switchFrament(2);
                        return true;
                }

                return false;
            }
        });

        //Jni.test(0.93333f, 0.067777f);

//        if(0 == Jni.connectServer()) {
//            connectStatus = 1;
//        }
    }

    //方法：发送网络请求，获取百度首页的数据。在里面开启线程
    private void sendRequestWithHttpClient() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    OkHttpClient okHttpClient_get = new OkHttpClient();

                    Request request = new Request.Builder()
                            .get()//get请求方式
                            .url("http://10.33.93.79/api/users")//网址
                            .build();

                    Response response = okHttpClient_get.newCall(request).execute();
                    if (response.isSuccessful()) {
                        // 打印数据
                        System.out.println(response.body().string());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void switchFrament(int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragments(transaction);

        switch (index) {
            case 0:
                if (mFragmentHome == null) {
                    mFragmentHome = new FragmentHome();
                    transaction.add(R.id.FramePage, mFragmentHome, FragmentHome.class.getSimpleName());
                }
                transaction.show(mFragmentHome);
                break;
            case 1:
                if (mFragmentBlogs == null) {
                    mFragmentBlogs = new FragmentBlogs();
                    transaction.add(R.id.FramePage, mFragmentBlogs, FragmentBlogs.class.getSimpleName());
                }
                transaction.show(mFragmentBlogs);
                break;
            case 2:
                if (mFragmentMine == null) {
                    mFragmentMine = new FragmentMine();
                    transaction.add(R.id.FramePage, mFragmentMine, FragmentMine.class.getSimpleName());
                }
                transaction.show(mFragmentMine);
                break;
        }
        transaction.commit();

        lastShowFragment = index;
    }

    @Override
    public void onFragmentInteraction(float x, float y) {

        Log.i("MainActivity", "y="+y+" x="+x);

        if(0 != Jni.sendData(y, x)) {
            connectStatus = 0;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

        int nn = 0;
    }

    @Override
    public void onListFragmentInteraction(Blog item) {

        Intent intent = new Intent(MainActivity.this, BlogPaper.class);
        intent.putExtra("blog", item);
        startActivity(intent);
    }

    private void hideFragments(FragmentTransaction pTransaction) {
        if (mFragmentHome != null && mFragmentHome.isVisible())
            pTransaction.hide(mFragmentHome);
        if (mFragmentBlogs != null && mFragmentBlogs.isVisible())
            pTransaction.hide(mFragmentBlogs);
        if (mFragmentMine != null && mFragmentMine.isVisible())
            pTransaction.hide(mFragmentMine);
    }

    public void onClickRemoteControl(View view) {

        Intent intent = new Intent(MainActivity.this, RemoteControl.class);
        startActivity(intent);
    }
}
