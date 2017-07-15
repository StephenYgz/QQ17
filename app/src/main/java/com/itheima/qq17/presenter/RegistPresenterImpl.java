package com.itheima.qq17.presenter;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SignUpCallback;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.itheima.qq17.model.User;
import com.itheima.qq17.utils.ThreadUtils;
import com.itheima.qq17.view.RegistView;

/**
 * 作者： itheima
 * 时间：2016-12-01 15:23
 * 网址：http://www.itheima.com
 */

public class RegistPresenterImpl implements RegistPresenter {

    private static final String TAG = "RegistPresenterImpl";
    private RegistView mRegistView;
    public RegistPresenterImpl(RegistView registView){
        this.mRegistView = registView;
    }

    @Override
    public void regist(final String username, final String pwd) {
        /**
         * 1. 先将用户名密码注册到AVCloud
         * 2. 如果成功了再将数据注册到环信
         * 3. 如果环信失败了，则需要将AVCloud上的数据删除一下
         */
        final User user = new User();
        user.setUsername(username);
        user.setPassword(pwd);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                Log.d(TAG, "done: ThreadName="+Thread.currentThread().getName());
                if (e==null){
                    //成功
                    //注册环信
                    ThreadUtils.runOnSubThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().createAccount(username, pwd);
                                //必须保证View中的方法在主线程中被调用，因为View要修改维护UI
                                ThreadUtils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //注册环信成功
                                        mRegistView.onRegist(username,pwd,true,null);
                                    }
                                });

                            } catch (final HyphenateException e1) {
                                e1.printStackTrace();
                                ThreadUtils.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //注册环信失败,删除AVCloud上的数据，然后告诉View失败了
                                        mRegistView.onRegist(username,pwd,false,e1.getMessage());
                                    }
                                });

                                try {
                                    user.delete();
                                } catch (AVException e2) {
                                    e2.printStackTrace();
                                }
                            }
                        }
                    });


                }else{
                    //失败
                    mRegistView.onRegist(username,pwd,false,e.getMessage());
                }
            }
        });

    }
}
