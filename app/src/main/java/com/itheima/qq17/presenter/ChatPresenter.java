package com.itheima.qq17.presenter;

import com.hyphenate.chat.EMMessage;

/**
 * 作者： itheima
 * 时间：2016-12-05 09:18
 * 网址：http://www.itheima.com
 */

public interface ChatPresenter {
    void initChat(String contact);

    void receiveMessage(EMMessage emMessage);

    void sendMessage(String msg, String contact);
}
