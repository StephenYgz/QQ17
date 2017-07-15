package com.itheima.qq17.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.itheima.qq17.BaseActivity;
import com.itheima.qq17.MainActivity;
import com.itheima.qq17.R;
import com.itheima.qq17.presenter.LoginPresenter;
import com.itheima.qq17.presenter.LoginPresenterImpl;
import com.itheima.qq17.utils.StringUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements LoginView, TextView.OnEditorActionListener {

    @InjectView(R.id.et_username)
    EditText mEtUsername;
    @InjectView(R.id.til_username)
    TextInputLayout mTilUsername;
    @InjectView(R.id.et_pwd)
    EditText mEtPwd;
    @InjectView(R.id.til_pwd)
    TextInputLayout mTilPwd;
    @InjectView(R.id.btn_login)
    Button mBtnLogin;
    @InjectView(R.id.tv_newuser)
    TextView mTvNewuser;

    private LoginPresenter mLoginPresenter;

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
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        mEtPwd.setOnEditorActionListener(this);

        mLoginPresenter = new LoginPresenterImpl(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        String username = getUsername();
        String pwd = getPwd();
        mEtUsername.setText(username);
        mEtPwd.setText(pwd);
    }

    @OnClick({R.id.btn_login, R.id.tv_newuser})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_newuser:
                startActivity(RegistActivity.class,false);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //被授权了，就可以重新登录了
                showToast("被授权了");
                login();
            }else{
                //被拒绝了
                showToast("被拒绝了，不让你登录了");
            }
        }
    }

    private void login() {
        /**
         * 1. 先检查用户是否已经授权了权限
         * 2. 如果用户授权了，则直接登录
         * 3. 如果没有，则动态申请，然后等用户授权了再登录
         */

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            //没有权限
            //参数3：请求码必须大于0
            /**
             * 系统会弹出一个对话框，当用户点击同意或者拒绝时，系统会回调当前Activity的一个方法onRequestPermissionsResult
             */
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

            return;
        }
        /**
         * 1. 获取用户名密码
         * 2. 校验数据的合法性
         * 3. 发起登录
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
        showDialog("正在登录中...");
        mLoginPresenter.login(username,pwd);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId()==R.id.et_pwd){
            if (actionId== EditorInfo.IME_ACTION_DONE){
                login();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onLogin(String username, String pwd, boolean isSuccess, String msg) {
        /**
         * 1. 隐藏对话框
         * 2. 如果成功，保存数据到sp，然后跳转带MainActivity
         * 3. 如果失败，弹吐司
         */
        hideDialog();
        if (isSuccess){
            saveUser(username, pwd);
            startActivity(MainActivity.class,true);
        }else{
            showToast("登录失败："+msg);
        }
    }
}
