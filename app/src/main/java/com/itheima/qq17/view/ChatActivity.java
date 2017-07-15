package com.itheima.qq17.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.itheima.qq17.BaseActivity;
import com.itheima.qq17.R;
import com.itheima.qq17.adapter.ChatAdapter;
import com.itheima.qq17.presenter.ChatPresenter;
import com.itheima.qq17.presenter.impl.ChatPresenterImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class ChatActivity extends BaseActivity implements TextWatcher,ChatView, View.OnClickListener {

    private static final String TAG = "ChatActivity";
    //当前的联系人
    private String mContact;
    private Button mBtnSend;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private EditText mEtMsg;
    private TextView mTvTitle;

    private ChatPresenter mChatPresenter;
    private ChatAdapter mChatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //获取聊天对象
        Intent intent = getIntent();
        mContact = intent.getStringExtra("username");
        if (TextUtils.isEmpty(mContact)){
            showToast("没有获取聊天对象，跟鬼聊呀！");
            finish();
            return;
        }
        mBtnSend = (Button) findViewById(R.id.btn_send);
        mToolbar = (Toolbar) findViewById(R.id.toolBar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mEtMsg = (EditText) findViewById(R.id.et_msg);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTitle.setText("与"+mContact+"聊天中");
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String msg = mEtMsg.getText().toString().trim();
        if (TextUtils.isEmpty(msg)){
            mBtnSend.setEnabled(false);
        }else {
            mBtnSend.setEnabled(true);
        }
        //监听输入框的文本改变事件
        mEtMsg.addTextChangedListener(this);
        //当进来的时候初始化历史聊天记录
        mChatPresenter = new ChatPresenterImpl(this);
        mChatPresenter.initChat(mContact);
        //将当前对象注册为观察者
        EventBus.getDefault().register(this);

        mBtnSend.setOnClickListener(this);

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent( EMMessage emMessage){
        /**
         * 判断当前这个EMMessage是不是来自当前的聊天对象
         */
        if (emMessage.getFrom().equals(mContact)){
            //收到消息了，将新的消息发送给P层
            mChatPresenter.receiveMessage(emMessage);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().length()>0){
            mBtnSend.setEnabled(true);
        }else{
            mBtnSend.setEnabled(false);
        }
    }

    @Override
    public void onInitChat(List<EMMessage> emMessageList) {
        //将emMessageList设置到RecyclerView上
        //并且让RecyclerView定位到最后一条记录
        mChatAdapter = new ChatAdapter(emMessageList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mChatAdapter);
        mRecyclerView.scrollToPosition(emMessageList.size()-1);
    }

    @Override
    public void onUpdateMessage() {
        mChatAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(mChatAdapter.getItemCount()-1);
    }

    @Override
    public void onClick(View v) {
        String msg = mEtMsg.getText().toString();
        if (TextUtils.isEmpty(msg)){
            showToast("不能发送空消息！");
            return;
        }
        mChatPresenter.sendMessage(msg,mContact);
        //发送出去消息后，清空EditText
        mEtMsg.getText().clear();
    }
}
