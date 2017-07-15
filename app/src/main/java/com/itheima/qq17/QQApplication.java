package com.itheima.qq17;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.itheima.qq17.db.DBUtils;
import com.itheima.qq17.event.ContactEvent;
import com.itheima.qq17.utils.ThreadUtils;
import com.itheima.qq17.view.ChatActivity;
import com.itheima.qq17.view.LoginActivity;
import com.itheima.qq17.view.ToastUtils;
import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 作者： itheima
 * 时间：2016-12-01 10:39
 * 网址：http://www.itheima.com
 */

/**
 * 全局的初始化
 */
public class QQApplication extends Application {
    private static final String TAG = "QQApplication";
    private ActivityManager mActivityManager;
    private NotificationManager mNotificationManager;
    private SoundPool mSoundPool;
    private int mDuanSound;
    private int mYuluSound;
    private List<BaseActivity> mBaseActivityList;

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化内存泄露检测
        LeakCanary.install(this);

        initHuanXin();
        initAVOSCloud();
        initDBUtils();


//        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
//            @Override
//            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//
//            }
//
//            @Override
//            public void onActivityStarted(Activity activity) {
//
//            }
//
//            @Override
//            public void onActivityResumed(Activity activity) {
//
//            }
//
//            @Override
//            public void onActivityPaused(Activity activity) {
//
//            }
//
//            @Override
//            public void onActivityStopped(Activity activity) {
//
//            }
//
//            @Override
//            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//
//            }
//
//            @Override
//            public void onActivityDestroyed(Activity activity) {
//
//            }
//        });


        mBaseActivityList = new ArrayList<>();

        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //初始化音乐池
        initSoundPool();
    }

    public void addActivity(BaseActivity activity){
        if (!mBaseActivityList.contains(activity)){
            mBaseActivityList.add(activity);
        }
    }

    public void removeActivity(BaseActivity activity){
        mBaseActivityList.remove(activity);
    }

    private void initSoundPool() {
        mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC,0);
        //预加载音乐
        mDuanSound = mSoundPool.load(this, R.raw.duan, 1);
        mYuluSound = mSoundPool.load(this, R.raw.yulu, 1);
    }

    private void initDBUtils() {
        DBUtils.init(this);
    }

    private void initAVOSCloud() {
        AVOSCloud.initialize(this,"W8vTQkjyIEVynxy3qlEcCl7W-gzGzoHsz","jsuj3GE6Ksv0Vp6WEdLyB2rQ");
    }

    private void initHuanXin() {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);//别人添加我，需要我的同意之后才能成为好友

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回

        if (processAppName == null ||!processAppName.equalsIgnoreCase(getPackageName())) {
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
         //初始化
        EMClient.getInstance().init(this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);

        //初始化好友变化监听器
        intitContactListener();
        //初始化新消息监听器
        initMessageListener();
        //初始化连接状态监听
        initConnectListener();

    }

    private void initConnectListener() {
        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override
            public void onConnected() {
            }

            @Override
            public void onDisconnected(int i) {
                if (i== EMError.USER_LOGIN_ANOTHER_DEVICE){
                    //被挤掉线了
                    //重新跳转到登录界面
                    Intent intent = new Intent(QQApplication.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    //将app中的所有的Activity全部销毁
                    for (BaseActivity activity : mBaseActivityList) {
                        activity.finish();
                    }

                    startActivity(intent);
                    ThreadUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(getApplicationContext(),"您的账号在其他设备登录了，请重新登录！");
                        }
                    });


                }
            }
        });
    }

    private boolean isRuningBackground(){
        /**
         * 获取手机中所有正在运行的任务栈
         * 需要权限： <uses-permission android:name="android.permission.GET_TASKS"/>
         */
        List<ActivityManager.RunningTaskInfo> runningTasks = mActivityManager.getRunningTasks(100);
        //获取第一个任务栈
        ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
        //获取任务栈中第一个Activity
        ComponentName topActivity = runningTaskInfo.topActivity;
        //如果这个Activity的包名和app的包名一致则说明在前台
        if (topActivity.getPackageName().equals(getPackageName())){
            return false;
        }else {
            return true;
        }
    }

    private void initMessageListener() {
        EMMessageListener msgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                EMMessage emMessage = messages.get(0);
                /**
                 * 需要判断当前App是否是在后台，如果是后台怎弹通知栏
                 */
                if (isRuningBackground()){

                    showNotification(emMessage);
                    //播放长声音
                    mSoundPool.play(mYuluSound,1,1,0,0,1);
                }else{
                    //播放长声音
                    mSoundPool.play(mDuanSound,1,1,0,0,1);
                }
                //收到消息
                EventBus.getDefault().post(emMessage);
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> messages) {
                //收到已读回执
            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> message) {
                //收到已送达回执
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    /**
     * 显示消息到通知栏
     * @param emMessage
     */
    private void showNotification(EMMessage emMessage) {

        String message = "";
        EMMessageBody body = emMessage.getBody();
        if (body instanceof EMTextMessageBody){
            EMTextMessageBody textMessageBody = (EMTextMessageBody) body;
            message = textMessageBody.getMessage();
        }

        Intent mainIntent = new Intent(this,MainActivity.class);
        //因为在非Activity中不允许启动Activity，如果要启动必须添加如下flag
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent chatIntent = new Intent(this, ChatActivity.class);
        chatIntent.putExtra("username",emMessage.getFrom());
        Intent[] intents = {mainIntent,chatIntent};

        PendingIntent pendingIntent = PendingIntent.getActivities(this,1,intents,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setAutoCancel(true) //消息点击之后可以自动删除
                .setPriority(Notification.PRIORITY_MAX)//设置通知的优先级
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.default_avatar))//大图标
                .setSmallIcon(R.mipmap.message)
                .setContentTitle("你有一条新消息")
                .setContentText(message)
                .setContentInfo("来自"+emMessage.getFrom())
                .setContentIntent(pendingIntent)
                .build();
        mNotificationManager.notify(1,notification);

    }

    private void intitContactListener() {
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {

            @Override
            public void onContactAgreed(String username) {
                //好友请求被同意
                Log.d(TAG, "onContactAgreed: "+username);
            }

            @Override
            public void onContactRefused(String username) {
                //好友请求被拒绝
                Log.d(TAG, "onContactRefused: "+username);
            }

            @Override
            public void onContactInvited(String username, String reason) {
                //收到好友邀请
                Log.d(TAG, "onContactInvited: "+username+"/"+reason);
                //直接同意对象为好友
                try {
                    EMClient.getInstance().contactManager().acceptInvitation(username);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    Log.d(TAG, "onContactInvited: 添加好友失败："+e);
                }
            }

            @Override
            public void onContactDeleted(String username) {
                //被删除时回调此方法
                Log.d(TAG, "onContactDeleted: "+username);

                ContactEvent contactEvent = new ContactEvent();
                contactEvent.isAdded = false;
                contactEvent.username = username;
                //发送消息
                EventBus.getDefault().post(contactEvent);

            }


            @Override
            public void onContactAdded(String username) {
                //增加了联系人时回调此方法
                Log.d(TAG, "onContactAdded: "+username);
                ContactEvent contactEvent = new ContactEvent();
                contactEvent.isAdded = true;
                contactEvent.username = username;
                //发送消息
                EventBus.getDefault().post(contactEvent);
            }
        });
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }
}
