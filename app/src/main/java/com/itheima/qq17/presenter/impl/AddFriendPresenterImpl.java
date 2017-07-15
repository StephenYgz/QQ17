package com.itheima.qq17.presenter.impl;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.itheima.qq17.db.DBUtils;
import com.itheima.qq17.presenter.AddFriendPresenter;
import com.itheima.qq17.utils.ThreadUtils;
import com.itheima.qq17.view.AddFriendView;

import java.util.List;

/**
 * 作者： itheima
 * 时间：2016-12-04 15:13
 * 网址：http://www.itheima.com
 */

public class AddFriendPresenterImpl implements AddFriendPresenter {

    private AddFriendView mAddFriendView;

    public AddFriendPresenterImpl(AddFriendView addFriendView) {
        mAddFriendView = addFriendView;
    }

    @Override
    public void searchFriend(String keyword) {
        final List<String> contactsList = DBUtils.getContacts(EMClient.getInstance().getCurrentUser());
        /**
         * 1. 去搜素AVCloud服务器上搜索凡是名字中包含keyword的用户
         * 2. 将搜索的结果展示到View上
         */
        //创建一个查询对象
        AVQuery<AVUser> avQuery = new AVQuery<>("_User");
        //给查询对象设置查询条件
        //select username from _User where username 包含 keyword and username!=lisi
        avQuery.whereContains("username",keyword);
        //将当前用户排除在外
        String currentUser = EMClient.getInstance().getCurrentUser();
        avQuery.whereNotEqualTo("username",currentUser);
        //发起查询
        avQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if (e==null){
                    //成功了
                    if (list==null||list.size()==0){
                        //查新成功了，但是没有数据
                        mAddFriendView.onSearchResult(null,null,true,null);
                    }else{
                        //获取到数据了
                        mAddFriendView.onSearchResult(list,contactsList,true,null);
                    }
                }else {
                    //失败了
                    mAddFriendView.onSearchResult(null,null,false,e.getMessage());
                }
            }
        });
    }

    @Override
    public void addContact(final String username) {
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    //参数为要添加的好友的username和添加理由
                    EMClient.getInstance().contactManager().addContact(username, "想和你一起游泳。");
                    //成功，仅仅是请求发送成功了
                    ThreadUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            mAddFriendView.onAddFriend(username,true,null);
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    //失败
                    ThreadUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            mAddFriendView.onAddFriend(username,false,e.getMessage());
                        }
                    });
                }
            }
        });

    }
}
