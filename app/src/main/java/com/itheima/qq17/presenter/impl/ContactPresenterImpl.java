package com.itheima.qq17.presenter.impl;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.itheima.qq17.db.DBUtils;
import com.itheima.qq17.presenter.ContactPresenter;
import com.itheima.qq17.utils.ThreadUtils;
import com.itheima.qq17.view.ContactView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 作者： itheima
 * 时间：2016-12-02 15:41
 * 网址：http://www.itheima.com
 */

public class ContactPresenterImpl implements ContactPresenter {
    private ContactView mContactView;
    private List<String> contactsList = new ArrayList<>();

    public ContactPresenterImpl(ContactView contactView) {
        mContactView = contactView;
    }
    @Override
    public void initContacts() {
        /**
         * 1. 先从本地缓存中获取联系人，然后返回给View
         * 2. 发起网络请求，从服务器上获取当前用户最新的联系人数据
         * 3. 当获取到网络数据的时候，将最新的数据保存到缓存，然后将最新的数据更新到UI上
         */
        final String currentUser = EMClient.getInstance().getCurrentUser();
        List<String> contacts = DBUtils.getContacts(currentUser);
        contactsList.clear();
        contactsList.addAll(contacts);
        mContactView.onInitContacts(contactsList);
        //发起环信网络请求，从服务器上获取当前用户最新的联系人数据
        updateFromServer(currentUser);


    }

    private void updateFromServer(final String currentUser) {
        //发起环信网络请求，从服务器上获取当前用户最新的联系人数据
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> allContactsFromServer = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    //获取成功
                    //当获取到网络数据的时候，将最新的数据保存到缓存，然后将最新的数据更新到UI上
                    DBUtils.updateContacts(currentUser,allContactsFromServer);
                    contactsList.clear();
                    contactsList.addAll(allContactsFromServer);
                    //因为服务器返回的数据没有顺序，异常你需要对数据进行排序
                    Collections.sort(contactsList, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    //通知给UI
                    ThreadUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            mContactView.onUpdateContacts(true,null);
                        }
                    });

                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    //获取失败
                    //通知给UI
                    ThreadUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            mContactView.onUpdateContacts(false,e.getMessage());
                        }
                    });
                }
            }
        });
    }

    @Override
    public void updateContacts() {
        String currentUser = EMClient.getInstance().getCurrentUser();
        updateFromServer(currentUser);
    }

    @Override
    public void deleteContact(final String contact) {
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().deleteContact(contact);
                    //删除成功
                    ThreadUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            mContactView.onDelete(contact,true,null);
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    //删除失败
                    ThreadUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            mContactView.onDelete(contact,false,e.getMessage());
                        }
                    });
                }
            }
        });

    }
}
