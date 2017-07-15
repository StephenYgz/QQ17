package com.itheima.qq17.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.qq17.R;

/**
 * 作者： itheima
 * 时间：2016-12-02 11:56
 * 网址：http://www.itheima.com
 */

public class ContactLayout extends RelativeLayout {

    private RecyclerView mRecyclerView;
    private Slidebar mSlidebar;
    private TextView mTvFloat;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public ContactLayout(Context context) {
        this(context,null);
    }
    public ContactLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public ContactLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs);
    }

    public ContactLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        /**
         * 将布局转换为View，填充到当前控件中
         */
       LayoutInflater.from(context).inflate(R.layout.contact_layout, this, true);
        /**
         * 初始化子控件
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mTvFloat = (TextView) findViewById(R.id.tv_float);
        mSlidebar = (Slidebar) findViewById(R.id.slideBar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        //设置mSwipeRefreshLayout的颜色
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent),getResources().getColor(R.color.colorPrimary));
    }

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener onRefreshListener){
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);

    }

    public void setRefreshing(boolean isRefreshing){
        mSwipeRefreshLayout.setRefreshing(isRefreshing);
    }


    public void setAdapter(RecyclerView.Adapter adapter){
        /**
         * 注意：给RecyclerView设置布局管理器
         */
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);
    }




}
