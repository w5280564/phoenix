package com.zhengshuo.phoenix.common;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;


import com.zhengshuo.phoenix.viewmodel.livedatabus.LiveDataBus;
import com.fastchat.sdk.client.HTClient;
import com.fastchat.sdk.service.MessageService;
import com.zhengshuo.phoenix.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 专门用于维护生明周期
 */

public class UserActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks, ActivityState {
    private List<Activity> activityList=new ArrayList<>();
    private List<Activity> resumeActivity=new ArrayList<>();
    private boolean isOnBack;//判断应用是否在前台

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        LogUtil.d("ActivityLifecycle", "onActivityCreated "+activity.getLocalClassName());
        activityList.add(0, activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (isOnBack) {
            LogUtil.d("ActivityLifecycle", "在前台了");
            isOnBack = false;
            if (HTClient.getInstance().isLoginEd()) {
                Intent intent = new Intent(activity, MessageService.class);
                intent.putExtra("TYPE", MessageService.TYPE_CONNECT_FOREGROUND);
                activity.startService(intent);
            }
        }
        LogUtil.d("ActivityLifecycle", "onActivityStarted "+activity.getLocalClassName());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (!resumeActivity.contains(activity)) {
            resumeActivity.add(activity);
            if(resumeActivity.size() == 1) {
                //do nothing
            }
            restartSingleInstanceActivity(activity);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        LogUtil.d("ActivityLifecycle", "onActivityPaused "+activity.getLocalClassName());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        LogUtil.d("ActivityLifecycle", "onActivityStopped "+activity.getLocalClassName());
        resumeActivity.remove(activity);
        if(resumeActivity.isEmpty()) {
            LogUtil.d("ActivityLifecycle", "在后台了");
            isOnBack = true;
            if (HTClient.getInstance().isLoginEd()) {
                LiveDataBus.get().with(EventConstants.APP_ON_BACK).postValue(true);
                Intent intent = new Intent(activity, MessageService.class);
                intent.putExtra("TYPE", MessageService.TYPE_DISCONNECT_BACKGROUND);
                activity.startService(intent);
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        LogUtil.d("ActivityLifecycle", "onActivityDestroyed "+activity.getLocalClassName());
        activityList.remove(activity);
    }

    @Override
    public Activity current() {
        return activityList.size()>0 ? activityList.get(0):null;
    }

    @Override
    public List<Activity> getActivityList() {
        return activityList;
    }

    @Override
    public int count() {
        return activityList.size();
    }

    @Override
    public boolean isFront() {
        return resumeActivity.size() > 0;
    }

    /**
     * 跳转到目标activity
     * @param cls
     */
    public void skipToTarget(Class<?> cls) {
        if(activityList != null && activityList.size() > 0) {
            current().startActivity(new Intent(current(), cls));
            for (Activity activity : activityList) {
                activity.finish();
            }
        }

    }

    /**
     * finish target activity
     * @param cls
     */
    public void finishTarget(Class<?> cls) {
        if(activityList != null && !activityList.isEmpty()) {
            for (Activity activity : activityList) {
                if(activity.getClass() == cls) {
                    activity.finish();
                }
            }
        }
    }

    /**
     * 判断app是否在前台
     * @return
     */
    public boolean isOnForeground() {
        return resumeActivity != null && !resumeActivity.isEmpty();
    }


    /**
     * 用于按下home键，点击图标，检查启动模式是singleInstance，且在activity列表中首位的Activity
     * 下面的方法，专用于解决启动模式是singleInstance, 为开启悬浮框的情况
     * @param activity
     */
    private void restartSingleInstanceActivity(Activity activity) {
        boolean isClickByFloat = activity.getIntent().getBooleanExtra("isClickByFloat", false);
        if(isClickByFloat) {
            return;
        }
        //刚启动，或者从桌面返回app
        //至少需要activityList中至少两个activity
        if(resumeActivity.size() == 1 && activityList.size() > 1) {
            Activity topActivity = activityList.get(0);
            if(!topActivity.isFinishing() //没有正在finish
                    && topActivity != activity //当前activity和列表中首个activity不相同
                    && topActivity.getTaskId() != activity.getTaskId()
            ){
                LogUtil.d("ActivityLifecycle", "启动了activity = "+topActivity.getClass().getName());
                activity.startActivity(new Intent(activity, topActivity.getClass()));
            }
        }
    }

}
