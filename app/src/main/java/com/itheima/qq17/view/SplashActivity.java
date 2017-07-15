package com.itheima.qq17.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.widget.ImageView;

import com.itheima.qq17.BaseActivity;
import com.itheima.qq17.MainActivity;
import com.itheima.qq17.R;
import com.itheima.qq17.adapter.AnimatorListenerAdapter;
import com.itheima.qq17.presenter.SplashPresenter;
import com.itheima.qq17.presenter.SplashPresenterImpl;

public class SplashActivity extends BaseActivity implements SplashView {

    public static final int DURATION = 2000;
    private ImageView mImageView;

    private SplashPresenter mSplashPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        /**
         * 创建一个业务逻辑对象，同时将当前对象传入进入
         */
        mSplashPresenter = new SplashPresenterImpl(this);

        mImageView = (ImageView) findViewById(R.id.iv_splash);
        /**
         * 1. 判断是否已经登录了
         * 2. 如果已经登录了则跳转到MainActivity
         * 3. 如果没有登录，则显示2s splash，然后跳转到LoginActivity
         */
        mSplashPresenter.checkIsLogin();
    }

    @Override
    public void onCheckLogin(boolean isLogin) {
        if (isLogin){
            //如果已经登录了则跳转到MainActivity
            startActivity(MainActivity.class,true);
        }else {
            //如果没有登录，则显示2s splash，然后跳转到LoginActivity
            ObjectAnimator animator = ObjectAnimator.ofFloat(mImageView, "alpha", 0, 1).setDuration(DURATION);
            animator.start();
           animator.addListener(new AnimatorListenerAdapter(){
               @Override
               public void onAnimationEnd(Animator animation) {
                   super.onAnimationEnd(animation);
                   startActivity(LoginActivity.class,true);
               }
           });
        }
    }
}
