package com.itheima.qq17.view;

import android.content.Context;
import android.widget.Toast;

/**
 * 作者： itheima
 * 时间：2016-12-01 17:24
 * 网址：http://www.itheima.com
 */

public class ToastUtils {
    private static Toast sToast;

    public static void showToast(Context context, String msg) {
        if (sToast == null) {
            sToast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        }
        sToast.setText(msg);
        sToast.show();
    }
}
