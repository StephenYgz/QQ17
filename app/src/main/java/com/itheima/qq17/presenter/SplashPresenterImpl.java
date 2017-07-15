package com.itheima.qq17.presenter;

import com.hyphenate.chat.EMClient;
import com.itheima.qq17.view.SplashView;

/**
 * 作者： itheima
 * 时间：2016-12-01 11:31
 * 网址：http://www.itheima.com
 */

public class SplashPresenterImpl implements SplashPresenter {

    private SplashView mSplashView;
    public SplashPresenterImpl(SplashView splashView){
        this.mSplashView = splashView;
    }
    @Override
    public void checkIsLogin() {
        boolean isLogin = EMClient.getInstance().isLoggedInBefore()&&EMClient.getInstance().isConnected();
        mSplashView.onCheckLogin(isLogin);
    }
}
