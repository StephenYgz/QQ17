package com.itheima.qq17.view;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.itheima.qq17.BaseActivity;
import com.itheima.qq17.R;
import com.itheima.qq17.presenter.RegistPresenter;
import com.itheima.qq17.presenter.RegistPresenterImpl;
import com.itheima.qq17.utils.StringUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RegistActivity extends BaseActivity implements RegistView,TextView.OnEditorActionListener {

    @InjectView(R.id.et_username)
    EditText mEtUsername;
    @InjectView(R.id.til_username)
    TextInputLayout mTilUsername;
    @InjectView(R.id.et_pwd)
    EditText mEtPwd;
    @InjectView(R.id.til_pwd)
    TextInputLayout mTilPwd;
    @InjectView(R.id.btn_regist)
    Button mBtnRegist;

    private RegistPresenter mRegistPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_regist);
        ButterKnife.inject(this);
        mEtPwd.setOnEditorActionListener(this);
        mRegistPresenter = new RegistPresenterImpl(this);

    }

    @OnClick(R.id.btn_regist)
    public void onClick() {
        regist();
    }

    private void regist() {
        /**
         * 1. 获取用户名密码
         * 2. 校验数据的合法性
         * 3. 发起注册
         */
        String username = mEtUsername.getText().toString().trim();
        String pwd = mEtPwd.getText().toString().trim();

        if (!StringUtils.checkUsername(username)){
            //在TextInputLayout上显示错误信息
            mTilUsername.setErrorEnabled(true);
            mTilUsername.setError("用户名不合法");
            //将焦点定位到用户名输入框
            mEtUsername.requestFocus(View.FOCUS_RIGHT);
            return;
        }else{
            //隐藏错误信息
            mTilUsername.setErrorEnabled(false);
            mTilUsername.setError("");
        }
        if (!StringUtils.checkPwd(pwd)){
            //在TextInputLayout上显示错误信息
            mTilPwd.setErrorEnabled(true);
            mTilPwd.setError("密码不合法");
            //将焦点定位到用户名输入框
            mEtPwd.requestFocus(View.FOCUS_RIGHT);
            return;
        }else{
            //隐藏错误信息
            mTilPwd.setErrorEnabled(false);
            mTilPwd.setError("");
        }
        //显示进度条对话框
        showDialog("正在注册中...");
        mRegistPresenter.regist(username,pwd);

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId()==R.id.et_pwd){
            if (actionId== EditorInfo.IME_ACTION_DONE){
                regist();
                return true;
            }
        }
        return false;
    }


    @Override
    public void onRegist(String username, String pwd, boolean isSuccess, String message) {
        /**
         * 1. 隐藏对话框
         * 2. 如果失败了，弹吐司告诉用户，失败的原因
         * 3. 如果成功了，弹吐司告诉用户，缓存数据到sp,跳转到LoginActivity
         */
        hideDialog();
        if (isSuccess){
            showToast("注册成功");
            saveUser(username, pwd);
            startActivity(LoginActivity.class,true);
        }else {
            showToast("注册失败："+message);
        }
    }
}
