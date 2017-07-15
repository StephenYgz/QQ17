package com.itheima.qq17.view;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.avos.avoscloud.AVUser;
import com.itheima.qq17.BaseActivity;
import com.itheima.qq17.R;
import com.itheima.qq17.adapter.AddFriendAdapter;
import com.itheima.qq17.presenter.AddFriendPresenter;
import com.itheima.qq17.presenter.impl.AddFriendPresenterImpl;

import java.util.List;

public class AddFriendActivity extends BaseActivity implements AddFriendView, AddFriendAdapter.OnAddFriendClickListener {

    private Toolbar mToolbar;
    private ImageView mIvNoData;
    private RecyclerView mRecyclerView;

    private AddFriendPresenter mAddFriendPresenter;
    private InputMethodManager mInputMethodManager;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        mToolbar = (Toolbar) findViewById(R.id.toolBar);
        mIvNoData = (ImageView) findViewById(R.id.iv_nodata);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        mAddFriendPresenter = new AddFriendPresenterImpl(this);

        //获取输入法管理器
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_friend_menu,menu);
        return true;
    }

    /**
     * 当点击菜单时被调用
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        showToast("onPrepareOptionsMenu");
        //从menu对象上获取SearchView
        MenuItem menuItem = menu.findItem(R.id.search_friend);
        mSearchView = (SearchView) menuItem.getActionView();
        //给SearchView设置搜索监听事件
        mSearchView.setQueryHint("用户名");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //显示进度条对话框
                showDialog("正在查询...");
                //搜索好友
                mAddFriendPresenter.searchFriend(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        return true;
    }

    @Override
    public void onSearchResult(List<AVUser> list,List<String> contactsList, boolean isSuccess, String message) {
        hideDialog();
        //隐藏输入法
        mInputMethodManager.hideSoftInputFromWindow(mSearchView.getWindowToken(),0);
        //把SearchView的焦点给取消
        mSearchView.clearFocus();
        if (isSuccess){
            if (list==null){
                //成功了，但是没有数据
                //显示SnackBar告诉没有数据
                //将nodata图片显示出来
                //将RecyclerView隐藏掉
                Snackbar.make(mToolbar,"没有查询到结果",Snackbar.LENGTH_LONG).show();
                mIvNoData.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);

            }else {
                //成功了，有数据
                //将nodata图片隐藏掉
                //将RecyclerView显示出来
                mIvNoData.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(AddFriendActivity.this));
                AddFriendAdapter addFriendAdapter = new AddFriendAdapter(list, contactsList);
                //给addFriendAdapter设置点击监听器
                addFriendAdapter.setOnAddFriendClickListener(this);
                mRecyclerView.setAdapter(addFriendAdapter);
            }
        }else{
            //失败了，网络失败
            //显示SnackBar告诉没有数据
            Snackbar.make(mToolbar,"查询异常："+message,Snackbar.LENGTH_LONG).show();
            //将nodata图片显示出来
            //将RecyclerView隐藏掉
            mIvNoData.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onAddFriend(String username, boolean isSuccess, String message) {
        if (isSuccess){
            Snackbar.make(mToolbar,"添加好友"+username+"请求发送成功",Snackbar.LENGTH_SHORT).show();
        }else{
            Snackbar.make(mToolbar,"添加好友"+username+"请求发送失败："+message,Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(String username) {
        //给username发出添加好友的请求
        mAddFriendPresenter.addContact(username);

    }
}
