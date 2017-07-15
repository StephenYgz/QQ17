package com.itheima.qq17.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.itheima.qq17.R;
import com.itheima.qq17.adapter.ConversationAdapter;
import com.itheima.qq17.presenter.ConversationPresenter;
import com.itheima.qq17.presenter.impl.ConversationPresenterImpl;
import com.itheima.qq17.view.ChatActivity;
import com.itheima.qq17.view.ConversationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationFragment extends BaseFragment implements ConversationView, ConversationAdapter.OnConversationClickListener {

    private RecyclerView mRecyclerView;
    private ConversationPresenter mConversationPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mConversationPresenter = new ConversationPresenterImpl(this);
        EventBus.getDefault().register(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        //不仅可以保证第一次进来初始化会话，还能保证从ChatActivity界面跳转过来后也初始化一下
        mConversationPresenter.initConversation();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EMMessage emMessage){
        //更新界面
        mConversationPresenter.initConversation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        //释放View资源
        mRecyclerView = null;
    }

    @Override
    public void onInitConversation(List<EMConversation> emConversationList) {

        ConversationAdapter conversationAdapter = new ConversationAdapter(emConversationList);

        conversationAdapter.setOnConversationClickListener(this);

        mRecyclerView.setAdapter(conversationAdapter);

    }

    @Override
    public void onConversationClick(EMConversation emConversation) {
        //跳转到聊天界面
        Intent intent = new Intent();
        intent.setClass(getActivity(), ChatActivity.class);
        intent.putExtra("username",emConversation.getUserName());
        startActivity(intent);
    }
}
