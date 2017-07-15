package com.itheima.qq17.presenter.impl;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.itheima.qq17.presenter.ConversationPresenter;
import com.itheima.qq17.view.ConversationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 作者： itheima
 * 时间：2016-12-05 11:49
 * 网址：http://www.itheima.com
 */

public class ConversationPresenterImpl implements ConversationPresenter {
    private ConversationView mConversationView;
    private List<EMConversation> mEMConversationList = new ArrayList<>();

    public ConversationPresenterImpl(ConversationView conversationView) {
        mConversationView = conversationView;
    }

    @Override
    public void initConversation() {
        mEMConversationList.clear();
        //获取所有的会话
        Map<String, EMConversation> allConversations = EMClient.getInstance().chatManager().getAllConversations();
        if (allConversations!=null&&allConversations.size()>0){
            mEMConversationList.addAll(allConversations.values());
        }
        //返回给View
        mConversationView.onInitConversation(mEMConversationList);

    }
}
