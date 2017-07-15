package com.itheima.qq17.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hyphenate.chat.EMClient;
import com.itheima.qq17.BaseActivity;
import com.itheima.qq17.R;
import com.itheima.qq17.presenter.PluginPresenter;
import com.itheima.qq17.presenter.PluginPresenterImpl;
import com.itheima.qq17.view.LoginActivity;
import com.itheima.qq17.view.PluginView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PluginFragment extends BaseFragment implements PluginView, View.OnClickListener {

    private PluginPresenter mPluginPresenter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plugin, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnLogout = (Button) view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(this);
        String username = EMClient.getInstance().getCurrentUser();
        btnLogout.setText("退（"+username+"）出");
        mPluginPresenter = new PluginPresenterImpl(this);

    }

    @Override
    public void onClick(View v) {
        mPluginPresenter.logout();
    }

    @Override
    public void onLogout(boolean isSuccess, String msg) {
        BaseActivity activity = (BaseActivity) getActivity();
        if (!isSuccess){
            activity.showToast(msg);
        }
        activity.startActivity(LoginActivity.class,true);
    }
}
