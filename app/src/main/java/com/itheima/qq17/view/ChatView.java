package com.itheima.qq17.view;

import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * 作者： itheima
 * 时间：2016-12-05 09:18
 * 网址：http://www.itheima.com
 */

public interface ChatView {
    void onInitChat(List<EMMessage> emMessageList);

    void onUpdateMessage();

}
