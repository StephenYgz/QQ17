package com.itheima.qq17.presenter;

import com.hyphenate.chat.EMClient;
import com.itheima.qq17.adapter.CallBackAdapter;
import com.itheima.qq17.view.LoginView;

/**
 * 作者： itheima
 * 时间：2016-12-02 09:14
 * 网址：http://www.itheima.com
 */

public class LoginPresenterImpl implements LoginPresenter {

    private static final String TAG = "LoginPresenterImpl";
    private LoginView mLoginView;

    public LoginPresenterImpl(LoginView loginView) {
        mLoginView = loginView;
    }

    @Override
    public void login(final String username, final String pwd) {
        /**
         * 1. 登录环信云服务器
         */
        EMClient.getInstance().login(username, pwd, new CallBackAdapter() {
            @Override
            public void onMainSuccess() {
                mLoginView.onLogin(username,pwd,true,null);
            }

            @Override
            public void onMainError(int i, String s) {
                mLoginView.onLogin(username,pwd,false,s);
            }
        });
    }
}
