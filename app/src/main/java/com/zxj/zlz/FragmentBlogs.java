package com.zxj.zlz;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FragmentBlogs extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private int lastVisibleItem = 0;
    private List<Blog> list;
    private final int PAGE_COUNT = 10;
    private GridLayoutManager mLayoutManager;

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private MyBlogsRecyclerViewAdapter mBlogsAdapter;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    static  final int SUCCESS=1;
    static  final int FAIL=0;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    if(list.size() > 0) {
                        updateRecyclerView(mBlogsAdapter.getRealLastPosition(), mBlogsAdapter.getRealLastPosition() + PAGE_COUNT);
                    }
                    break;
                case FAIL:
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentBlogs() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FragmentBlogs newInstance(int columnCount) {
        FragmentBlogs fragment = new FragmentBlogs();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blogs_list, container, false);

        initData();

        refreshLayout = view.findViewById(R.id.refreshLayout);
        recyclerView = view.findViewById(R.id.recyclerView);

        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        refreshLayout.setOnRefreshListener(this);

        Context context = view.getContext();
        mLayoutManager = new GridLayoutManager(context, 1);
        recyclerView.setLayoutManager(mLayoutManager);

        List<Blog> newDatas = getDatas(0, PAGE_COUNT);
        mBlogsAdapter = new MyBlogsRecyclerViewAdapter(view.getContext(), newDatas,  mListener);
        recyclerView.setAdapter(mBlogsAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mBlogsAdapter.isFadeTips() == false && lastVisibleItem + 1 == mBlogsAdapter.getItemCount()) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateRecyclerView(mBlogsAdapter.getRealLastPosition(), mBlogsAdapter.getRealLastPosition() + PAGE_COUNT);
                            }
                        }, 500);
                    }

                    if (mBlogsAdapter.isFadeTips() == true && lastVisibleItem + 2 == mBlogsAdapter.getItemCount()) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateRecyclerView(mBlogsAdapter.getRealLastPosition(), mBlogsAdapter.getRealLastPosition() + PAGE_COUNT);
                            }
                        }, 500);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(String item);
    }

    private List<Blog> getDatas(final int firstIndex, final int lastIndex) {
        List<Blog> resList = new ArrayList<>();
        int count = list.size() < lastIndex? list.size() : lastIndex;

        Blog blog;
        for (int i = firstIndex; i < count; i++) {
            resList.add(list.get(i));
        }
        return resList;
    }

    private void updateRecyclerView(int fromIndex, int toIndex) {
        List<Blog> newDatas = getDatas(fromIndex, toIndex);
        int realSize = toIndex - fromIndex;
        if (newDatas.size() > 0) {
            if(realSize == newDatas.size()) {
                mBlogsAdapter.updateList(newDatas, true);
            } else {
                mBlogsAdapter.updateList(newDatas, false);
            }
        } else {
            mBlogsAdapter.updateList(null, false);
        }
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(true);
        mBlogsAdapter.resetDatas();

        updateRecyclerView(0, PAGE_COUNT);

        refreshLayout.postDelayed(new Runnable() { // 发送延迟消息到消息队列
            @Override
            public void run() {
                refreshLayout.setRefreshing(false); // 是否显示刷新进度;false:不显示
            }
        },1000);
    }

    private void initData() {
        list = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    OkHttpClient okHttpClient_get = new OkHttpClient();

                    Request request = new Request.Builder()
                            .get()//get请求方式
                            .url("http://10.33.93.79/api/blogs")//网址
                            .build();

                    Response response = okHttpClient_get.newCall(request).execute();
                    if (response.isSuccessful()) {

                        String str = response.body().string();

                        JSONObject json_data = new JSONObject(str);
                        JSONObject page = json_data.getJSONObject("page");
                        JSONArray blogs = json_data.getJSONArray("blogs");

                        for(int i = 0; i < blogs.length(); i++) {
                            JSONObject blog = blogs.getJSONObject(i);
                            String name = blog.getString("name");
                            String content = blog.getString("content");
                            String user_name = blog.getString("user_name");
                            Long created_at = blog.getLong("created_at");

                            list.add(new Blog(name, user_name, created_at, content));
                        }

                        //list.add(new Blog("示例新闻", "zxj", 1557370765L, "2019-05-13"));

                        Message msg=new Message();
                        msg.what=1;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


}
