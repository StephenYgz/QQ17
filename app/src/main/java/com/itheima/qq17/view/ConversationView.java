package com.itheima.qq17.view;

import com.hyphenate.chat.EMConversation;

import java.util.List;

/**
 * 作者： itheima
 * 时间：2016-12-05 11:48
 * 网址：http://www.itheima.com
 */

public interface ConversationView {
    void onInitConversation(List<EMConversation> emConversationList);
}
