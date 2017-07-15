package com.itheima.qq17.presenter;

import com.hyphenate.chat.EMClient;
import com.itheima.qq17.adapter.CallBackAdapter;
import com.itheima.qq17.view.PluginView;

/**
 * 作者： itheima
 * 时间：2016-12-02 11:24
 * 网址：http://www.itheima.com
 */

public class PluginPresenterImpl implements PluginPresenter {

    private PluginView mPluginView;

    public PluginPresenterImpl(PluginView pluginView) {
        mPluginView = pluginView;
    }

    @Override
    public void logout() {
        EMClient.getInstance().logout(true, new CallBackAdapter() {
            @Override
            public void onMainSuccess() {
                mPluginView.onLogout(true,null);
            }

            @Override
            public void onMainError(int i, String s) {
                mPluginView.onLogout(false,s);
            }
        });
    }
}
