package com.itheima.qq17.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itheima.qq17.BaseActivity;
import com.itheima.qq17.R;
import com.itheima.qq17.adapter.ContactAdapter;
import com.itheima.qq17.event.ContactEvent;
import com.itheima.qq17.presenter.ContactPresenter;
import com.itheima.qq17.presenter.impl.ContactPresenterImpl;
import com.itheima.qq17.view.ChatActivity;
import com.itheima.qq17.view.ContactView;
import com.itheima.qq17.view.ToastUtils;
import com.itheima.qq17.widget.ContactLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends BaseFragment implements ContactView, SwipeRefreshLayout.OnRefreshListener, ContactAdapter.OnContactClickListener {

    private ContactLayout mContactLayout;
    private ContactPresenter mContactPresenter;
    private ContactAdapter mContactAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContactLayout = (ContactLayout) view;
        mContactLayout.setOnRefreshListener(this);
        mContactPresenter = new ContactPresenterImpl(this);
        /**
         * 获取联系人数据
         */
        mContactPresenter.initContacts();
        //将当前对象注册为Subscriber
        EventBus.getDefault().register(this);

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ContactEvent contactEvent){
        /**
         * 接收到发布者发布的消息
         * 更新UI
         */
        ToastUtils.showToast(getActivity(),(contactEvent.isAdded?"添加了":"删除了")+""+ contactEvent.username);
        mContactPresenter.updateContacts();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //将当前对象取消注册，取消注册后就接收不到EventBus的消息了
        EventBus.getDefault().unregister(this);
        mContactLayout = null;
        mContactAdapter = null;
    }

    @Override
    public void onInitContacts(List<String> contactsList) {
        /**
         * 创建Adapter，将数据设置给Adapter
         * 将Adapter设置给RecyclerView
         */
        mContactAdapter = new ContactAdapter(contactsList);
        //设置点击监听
        mContactAdapter.setOnContactClickListener(this);
        mContactLayout.setAdapter(mContactAdapter);

    }

    @Override
    public void onUpdateContacts(boolean isSuccess, String msg) {
        /**
         * 如果成功了，就通知contactAdapter 更新UI
         */
        if (isSuccess){
            mContactAdapter.notifyDataSetChanged();
        }else{
            BaseActivity activity = (BaseActivity) getActivity();
            activity.showToast("更新通讯录失败："+msg);
        }
        //隐藏下拉刷新控件
        mContactLayout.setRefreshing(false);


    }

    @Override
    public void onDelete(String contact, boolean isSuccess, String message) {
        if (isSuccess){
            Snackbar.make(mContactLayout,"删除"+contact+"成功",Snackbar.LENGTH_SHORT).show();
        }else{
            Snackbar.make(mContactLayout,"删除"+contact+"失败："+message,Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefresh() {
        //更新数据
        mContactPresenter.updateContacts();
    }

    @Override
    public void onClick(String contact) {
      //跳转到聊天界面
        Intent intent = new Intent();
        intent.setClass(getActivity(),ChatActivity.class);
        //将当前点击的联系人传递给ChatActivity
        intent.putExtra("username",contact);

        startActivity(intent);
    }

    @Override
    public void onLongClick(final String contact) {

        //显示SnackBar
        Snackbar.make(mContactLayout,"您确定和"+contact+"友尽了吗？",Snackbar.LENGTH_LONG)
                .setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //删除好友
                       mContactPresenter.deleteContact(contact);
                    }
                }).show();
    }
}
