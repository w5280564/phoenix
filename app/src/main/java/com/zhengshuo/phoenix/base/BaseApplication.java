package com.zhengshuo.phoenix.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.multidex.MultiDexApplication;

import com.bumptech.glide.request.target.ViewTarget;
import com.fastchat.sdk.utils.PathUtil;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.util.ToastUtil;
import com.fastchat.sdk.client.HTClient;
import com.fastchat.sdk.db.DBManager;
import com.fastchat.sdk.logcollect.logcat.LogcatHelper;
import com.tencent.mmkv.MMKV;
import com.zhengshuo.phoenix.common.HTClientHelper;
import com.zhengshuo.phoenix.common.UserActivityLifecycleCallbacks;
import com.zhengshuo.phoenix.common.manager.NotifierManager;
import com.zhengshuo.phoenix.common.manager.UserManager;
import com.zhengshuo.phoenix.ui.login.LoginActivity;

import java.util.List;


/**
 * @Description: 程序入口类
 * @Author: ouyang
 * @CreateDate: 2022/3/9 0009
 */
public class BaseApplication extends MultiDexApplication {
    private UserActivityLifecycleCallbacks mLifecycleCallbacks = new UserActivityLifecycleCallbacks();
    public static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registerActivityLifecycleCallbacks();
        NotifierManager.init(instance);
        MMKV.initialize(instance);//初始化MMKV
//        HTClientHelper.init(instance);
        ViewTarget.setTagId(R.id.glideIndexTag);
    }

    /**
     * 获取一个Application对象
     */
    public static synchronized BaseApplication getInstance() {
        return instance;
    }

    public Context getContext() {
        return instance.getApplicationContext();
    }


    /**
     * 注册生明周期维护类
     */
    private void registerActivityLifecycleCallbacks() {
        this.registerActivityLifecycleCallbacks(mLifecycleCallbacks);
    }


    public UserActivityLifecycleCallbacks getLifecycleCallbacks() {
        return mLifecycleCallbacks;
    }


    public String getUsername() {

        return UserManager.get().getMyUserId();
    }

    public String getUserNick() {
        return UserManager.get().getMyNick();
    }

    public String getVideoPath() {
        String localPath = PathUtil.getInstance().getVideoPath() + "/";
        return localPath;
    }

    public String getImagePath() {
        String localPath = PathUtil.getInstance().getImagePath() + "/";
        return localPath;
    }

    public String getVoicePath() {
        String localPath = PathUtil.getInstance().getVoicePath() + "/";
        return localPath;
    }

    public void logoutApp(int type) {
        //type=1,被提出，0主动退出
        //清除登录者信息
        HTClient.getInstance().logout(new HTClient.HTCallBack() {
            @Override
            public void onSuccess() {
                UserManager.get().clearMyData();
                DBManager.getInstance(instance.getApplicationContext()).closeDb();//关闭数据库
                LogcatHelper.getInstance().stop();//停止日志打印
                HTClient.getInstance().conversationManager().clearAllConversations();//退出清除所有会话
                NotifierManager.getInstance().cancel();
                finishAllActivity();
                Intent intent=new Intent(instance, LoginActivity.class);
                intent.putExtra("type",type);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                instance.startActivity(intent);
            }

            @Override
            public void onError() {
                ToastUtil.ss("退出失败");
            }
        });
        //  JPushInterface.stopPush(getApplicationContext());
    }


    public void finishAllActivity() {
        UserActivityLifecycleCallbacks lifecycleCallbacks = getLifecycleCallbacks();
        if(lifecycleCallbacks == null) {
            return;
        }
        List<Activity> activities = lifecycleCallbacks.getActivityList();
        if(activities == null || activities.isEmpty()) {
            return;
        }
        for(Activity activity : activities) {
            activity.finish();
        }
    }

}
