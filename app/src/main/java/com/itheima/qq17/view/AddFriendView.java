package com.itheima.qq17.view;

import com.avos.avoscloud.AVUser;

import java.util.List;

/**
 * 作者： itheima
 * 时间：2016-12-04 15:12
 * 网址：http://www.itheima.com
 */

public interface AddFriendView {
    void onSearchResult(List<AVUser> list, List<String> contactsList,boolean isSuccess, String message);

    void onAddFriend(String username, boolean isSuccess, String message);
}
