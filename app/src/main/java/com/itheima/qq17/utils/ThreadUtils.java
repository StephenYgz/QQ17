package com.itheima.qq17.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 作者： itheima
 * 时间：2016-12-01 17:00
 * 网址：http://www.itheima.com
 */

public class ThreadUtils {
    private static Handler sHandler = new Handler(Looper.getMainLooper());
    private static Executor sExecutor = Executors.newSingleThreadExecutor();
    public static void runOnSubThread(Runnable runnable){
        sExecutor.execute(runnable);
    }
    public static void runOnUIThread(Runnable runnable){
        sHandler.post(runnable);
    }
}
