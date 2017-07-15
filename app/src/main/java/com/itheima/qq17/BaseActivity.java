package com.itheima.qq17;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.itheima.qq17.view.ToastUtils;

/**
 * 作者： itheima
 * 时间：2016-12-01 11:12
 * 网址：http://www.itheima.com
 */

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private SharedPreferences mSP;


    public void startActivity(Class clazz, boolean isFinish){
        startActivity(new Intent(this,clazz));
        if (isFinish){
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化进度条对话框
        mProgressDialog = new ProgressDialog(this);
        //设置进度条对话框不可取消
        mProgressDialog.setCancelable(false);
        mSP = getSharedPreferences("config", MODE_PRIVATE);

        //将当前Activity放到Application的集合中
        QQApplication application = (QQApplication) getApplication();
        application.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //将当前Activity从Application的集合中移除
        QQApplication application = (QQApplication) getApplication();
        application.removeActivity(this);
    }

    public void saveUser(String username, String pwd){
        mSP.edit()
                .putString("username",username)
                .putString("pwd",pwd)
                .commit();
    }
    public String  getUsername(){
        return  mSP.getString("username","");
    }
    public String getPwd(){
        return  mSP.getString("pwd","");
    }

    public void showDialog(String msg){
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    public void hideDialog(){
        mProgressDialog.dismiss();
    }

    public void showToast(String msg){
        ToastUtils.showToast(this,msg);
    }
}
