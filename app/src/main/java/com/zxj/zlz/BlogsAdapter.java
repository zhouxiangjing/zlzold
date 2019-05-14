package com.zxj.zlz;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zxj.zlz.FragmentBlogs.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link String} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class BlogsAdapter extends RecyclerView.Adapter<BlogsAdapter.ViewHolder> {

    private final List<Blog> mValues;
    private final OnListFragmentInteractionListener mListener;

    private Context context;
    private int normalType = 0;
    private int footType = 1;

    private boolean hasMore = true;
    private boolean hasInit = false;

    private boolean fadeTips = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public BlogsAdapter(Context context, List<Blog> items, OnListFragmentInteractionListener listener) {
        this.context = context;
        this.mValues = items;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == normalType) {
            return new NormalHolder(LayoutInflater.from(context).inflate(R.layout.fragment_blogs_item, null));
        } else {
            return new FootHolder(LayoutInflater.from(context).inflate(R.layout.fragment_blogs_footview, null));
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        if(position == mValues.size() - 1 && !hasInit) {
            hasInit = true;
            return;
        }
        if (holder instanceof NormalHolder) {
            final Blog blog = mValues.get(position);
            ((NormalHolder) holder).title.setText(blog.title);
            ((NormalHolder) holder).user.setText(blog.user);
            ((NormalHolder) holder).time.setText(blog.time);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onListFragmentInteraction(blog);
                }
            });
        } else {
            ((FootHolder) holder).tips.setVisibility(View.VISIBLE);
            if (hasMore == true) {
                fadeTips = false;
                if (mValues.size() > 0) {
                    ((FootHolder) holder).tips.setText("正在加载更多...");
                }
            } else {
                if (mValues.size() > 0) {
                    ((FootHolder) holder).tips.setText("没有更多数据了");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((FootHolder) holder).tips.setVisibility(View.GONE);
                            fadeTips = true;
                            hasMore = true;
                        }
                    }, 500);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return footType;
        } else {
            return normalType;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
        }
    }

    public boolean isFadeTips() {
        return fadeTips;
    }

    public int getRealLastPosition() {
        return mValues.size();
    }

    class NormalHolder extends ViewHolder {
        private TextView title;
        private TextView user;
        private TextView time;
        public NormalHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.blog_title);
            user = itemView.findViewById(R.id.blog_user);
            time = itemView.findViewById(R.id.blog_time);
        }
    }

    class FootHolder extends ViewHolder {
        private TextView tips;

        public FootHolder(View itemView) {
            super(itemView);
            tips = itemView.findViewById(R.id.tips);
        }
    }

    public void resetDatas() {
        mValues.clear();
    }

    public void updateList(List<Blog> newDatas, boolean hasMore) {
        if (newDatas != null) {
            if(mValues.size() > 0) {
                mValues.remove(mValues.size()-1);
            }
            mValues.addAll(newDatas);
            mValues.add(new Blog("", "",0L, ""));
        }
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }
}
