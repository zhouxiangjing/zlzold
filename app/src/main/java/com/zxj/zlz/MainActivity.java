package com.zxj.zlz;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
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

    private FragmentHome mFragmentHome;
    private FragmentBlogs mFragmentBlogs;
    private FragmentMine mFragmentMine;
    private Fragment[] mFragments;
    private int lastShowFragment = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragments();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        switchFrament(lastShowFragment, 0);
                        return true;
                    case R.id.navigation_dashboard:
                        switchFrament(lastShowFragment, 1);
                        return true;
                    case R.id.navigation_notifications:
                        switchFrament(lastShowFragment, 2);
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

    private void initFragments() {
        mFragmentHome = new FragmentHome();
        mFragmentBlogs = new FragmentBlogs();
        mFragmentMine = new FragmentMine();
        mFragments = new Fragment[]{mFragmentHome, mFragmentBlogs, mFragmentMine};

        lastShowFragment = 0;
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.FramePage, mFragmentHome)
                .show(mFragmentHome)
                .commit();
    }

    private void switchFrament(int lastIndex, int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(mFragments[lastIndex]);
        if (!mFragments[index].isAdded()) {
            transaction.add(R.id.FramePage, mFragments[index]);
        }
        transaction.show(mFragments[index]).commitAllowingStateLoss();
        lastShowFragment = index;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(String item) {

    }
}
