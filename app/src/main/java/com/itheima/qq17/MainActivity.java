package com.itheima.qq17;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.itheima.qq17.view.AddFriendActivity;
import com.itheima.qq17.view.FragmentFactory;
import com.itheima.qq17.view.fragment.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {

    private static final String[] TITLES = {"消息","联系人","动态"};
    private static final String TAG = "MainActivity";

    private Toolbar mToolbar;
    private TextView mTvTitle;
    private BottomNavigationBar mBottomNavigationBar;
    private BadgeItem mBadgeItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolBar);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mBottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottomNavigationBar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        initBottomNavigationBar();
        initFragment();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EMMessage emMessage){
        updateUnreadMsgCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //更新所有的未读消息
        updateUnreadMsgCount();
    }

    private void updateUnreadMsgCount() {
        int unreadMsgsCount = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        if (unreadMsgsCount>99){
            mBadgeItem.setText("99+");
            mBadgeItem.show();
        }else if (unreadMsgsCount>0){
            mBadgeItem.setText(""+unreadMsgsCount);
            mBadgeItem.show();
        }else{
            mBadgeItem.hide();
        }
    }

    private void initBottomNavigationBar() {
        BottomNavigationItem conversationItem = new BottomNavigationItem(R.mipmap.conversation_selected_2,TITLES[0]);

        //创建一个角标对象
        mBadgeItem = new BadgeItem();
        //设置位置为右侧
        mBadgeItem.setGravity(Gravity.RIGHT);
        mBadgeItem.setBackgroundColor("#ff0000");
        mBadgeItem.setText("0");
        mBadgeItem.setTextColor("#ffffff");
        mBadgeItem.setAnimationDuration(1000);
        mBadgeItem.show();

        conversationItem.setBadgeItem(mBadgeItem);

        BottomNavigationItem contactItem = new BottomNavigationItem(R.mipmap.contact_selected_2,TITLES[1]);
        BottomNavigationItem pluginItem = new BottomNavigationItem(R.mipmap.plugin_selected_2,TITLES[2]);

        mBottomNavigationBar.addItem(conversationItem);
        mBottomNavigationBar.addItem(contactItem);
        mBottomNavigationBar.addItem(pluginItem);

        //配置Tab选中和没选中的颜色
        mBottomNavigationBar.setActiveColor(R.color.colorPrimary);
        mBottomNavigationBar.setInActiveColor("#9c9c9c");

        //初始化，否则上面的代码不生效
        mBottomNavigationBar.initialise();

        //添加Tab选择监听器
        mBottomNavigationBar.setTabSelectedListener(this);
    }

    private void initFragment() {
        /**
         * 解决MainActivity中Fragment重影
         */
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        for(int i=0;i<TITLES.length;i++){

            Fragment fragmentByTag = supportFragmentManager.findFragmentByTag(i+"");
            if (fragmentByTag!=null){
                Log.d(TAG, "initFragment: 发现有老的缓存Fragment"+fragmentByTag);
                //fragmentTransaction.remove(fragmentByTag);
                fragmentTransaction.hide(fragmentByTag);
            }
        }
        fragmentTransaction.commit();
        /**
         * 默认只添加第一个Fragment
         */
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_content, FragmentFactory.getFragment(0),"0")
                .commit();
        mTvTitle.setText(TITLES[0]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //获取Menu布局填充器
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //让菜单显示图标
        MenuBuilder menuBuilder = (MenuBuilder) menu;
        menuBuilder.setOptionalIconsVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_frient:
               startActivity(AddFriendActivity.class,false);
                break;
            case R.id.menu_share:
                showToast("分享好友");
                break;
            case R.id.menu_about:
                showToast("关于我们");
                break;
        }
        return true;
    }

    @Override
    public void onTabSelected(int position) {
        /**
         * 1. 修改标题
         * 2. 根据position获取到对应的Fragment，如果该Fragment没有被添加，则先添加再显示
         */
        mTvTitle.setText(TITLES[position]);
        BaseFragment fragment = FragmentFactory.getFragment(position);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //判断当前Fragment是否被添加
        if (!fragment.isAdded()){
            fragmentTransaction.add(R.id.fl_content,fragment,position+"");
        }
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onTabUnselected(int position) {
        /**
         * 1. 根据position找到对应的Fragment，然后把这个隐藏掉
         */
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        BaseFragment fragment = FragmentFactory.getFragment(position);
        fragmentTransaction.hide(fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onTabReselected(int position) {
        Log.d(TAG, "onTabReselected: position="+position);
    }
}
