package com.itheima.qq17.adapter;

import android.util.Log;

import com.hyphenate.EMCallBack;
import com.itheima.qq17.utils.ThreadUtils;

/**
 * 作者： itheima
 * 时间：2016-12-02 09:32
 * 网址：http://www.itheima.com
 */

public abstract class CallBackAdapter implements EMCallBack {

    private static final String TAG = "CallBackAdapter";

    public abstract void onMainSuccess();

    public abstract void onMainError(int i, String s);
    @Override
    public void onSuccess() {
        Log.d(TAG, "onSuccess: ");
        ThreadUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                onMainSuccess();
            }
        });
    }

    @Override
    public void onError(final int i, final String s) {
        ThreadUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                onMainError(i,s);
            }
        });
    }

    @Override
    public void onProgress(int i, String s) {

    }
}
