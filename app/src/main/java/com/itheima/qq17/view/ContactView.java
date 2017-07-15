package com.itheima.qq17.view;

import java.util.List;

/**
 * 作者： itheima
 * 时间：2016-12-02 15:40
 * 网址：http://www.itheima.com
 */

public interface ContactView {
    void onInitContacts(List<String> contactsList);

    void onUpdateContacts(boolean isSuccess,String msg);

    void onDelete(String contact, boolean isSuccess, String message);
}
