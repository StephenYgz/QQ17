package com.itheima.qq17.presenter.impl;

import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.itheima.qq17.adapter.CallBackAdapter;
import com.itheima.qq17.presenter.ChatPresenter;
import com.itheima.qq17.view.ChatView;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者： itheima
 * 时间：2016-12-05 09:20
 * 网址：http://www.itheima.com
 */

public class ChatPresenterImpl implements ChatPresenter {
    private static final String TAG = "ChatPresenterImpl";
    private ChatView mChatView;
    private List<EMMessage> mEMMessageList = new ArrayList<>();
    private EMConversation mConversation;

    public ChatPresenterImpl(ChatView chatView) {
        mChatView = chatView;
    }

    @Override
    public void initChat(String contact) {
        /**
         * 1. 如果曾经有过会话，则最多获取20条历史聊天记录，然后放到集合中返回给View
         * 2. 如果不曾有过会话，则返回一个空集合给View
         */

        EMConversation mConversation =  EMClient.getInstance().chatManager().getConversation(contact);
        if (mConversation != null) {//1. 如果曾经有过会话，则最多获取20条历史聊天记录，然后放到集合中返回给View

            /**
             * 将会话标记为已读
             */
            mConversation.markAllMessagesAsRead();

            //先获取最近的一条聊天记录
            EMMessage lastMessage = mConversation.getLastMessage();
            /**
             * 以lastMessage为分界，往上再获取19条,比如lastMessage的id 是100，则获取到的是[99---81]
             */
            List<EMMessage> emMessages = mConversation.loadMoreMsgFromDB(lastMessage.getMsgId(), 19);

            /**
             * 将总共20条历史聊天对象放到集合中
             */
            mEMMessageList.clear();
            mEMMessageList.addAll(emMessages);
            mEMMessageList.add(lastMessage);
            //将集合返回给View
            mChatView.onInitChat(mEMMessageList);

        } else {//2. 如果不曾有过会话，则返回一个空集合给View
            mEMMessageList.clear();
            mChatView.onInitChat(mEMMessageList);
        }
    }

    @Override
    public void receiveMessage(EMMessage emMessage) {
        /**
         * 将接收到的消息标记为已读
         */
        EMConversation mConversation =  EMClient.getInstance().chatManager().getConversation(emMessage.getFrom());
        if (mConversation!=null){
            mConversation.markAllMessagesAsRead();
        }
        mEMMessageList.add(emMessage);
        mChatView.onUpdateMessage();
    }

    @Override
    public void sendMessage(String msg, String contact) {

        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        final EMMessage message = EMMessage.createTxtSendMessage(msg, contact);
        /**
         * 立即将当前消息添加到集合中，然后更新View
         */
        mEMMessageList.add(message);
        mChatView.onUpdateMessage();

        //发送消息,异步的方法没有回调
        EMClient.getInstance().chatManager().sendMessage(message);
        //给message添加回调监听
        message.setMessageStatusCallback(new CallBackAdapter() {
            @Override
            public void onMainSuccess() {
                Log.d(TAG, "onMainSuccess: " + message);
                //当前消息发送成功了
                mChatView.onUpdateMessage();
            }

            @Override
            public void onMainError(int i, String s) {
                Log.d(TAG, "onMainError: " + s);
                //当前消息发送失败了
                mChatView.onUpdateMessage();
            }
        });
    }
}
