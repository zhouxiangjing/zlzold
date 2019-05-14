package com.zxj.zlz;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import java.io.IOException;

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

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt("lastShowFragment", lastShowFragment);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    public void onFragmentInteraction(Uri uri) {

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
}
