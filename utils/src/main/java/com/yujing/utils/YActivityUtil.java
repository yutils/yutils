package com.yujing.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.Stack;

public class YActivityUtil {
    //Stack(栈)，后进先出
    private static Stack<Activity> activityStack = new Stack<>();
    private static YActivityLifecycleCallbacks yAlc;

    //单例
    public static YActivityLifecycleCallbacks getActivityLifecycleCallbacks() {
        if (yAlc == null)
            yAlc = new YActivityLifecycleCallbacks();
        return yAlc;
    }

    public static class YActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

        private YActivityLifecycleCallbacks() {
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            activityStack.remove(activity);
            activityStack.push(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            activityStack.remove(activity);
        }
    }


    /**
     * 获得当前栈顶Activity
     */
    public static Activity getCurrentActivity() {
        Activity activity = null;
        if (!activityStack.isEmpty())
            activity = activityStack.peek();
        return activity;
    }

    /**
     * 获得当前Activity名字
     */
    public static String getCurrentActivityName() {
        Activity activity = getCurrentActivity();
        String name = "";
        if (activity != null) {
            name = activity.getComponentName().getClassName();
        }
        return name;
    }

    /**
     * 关闭当前Activity
     */
    public static void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 关闭所有Activity
     */
    public static void closeAllActivity() {
        while (true) {
            Activity activity = getCurrentActivity();
            if (null == activity) {
                break;
            }
            finishActivity(activity);
        }
    }

    /**
     * 通过名称关闭Activity
     */
    public static void closeActivityByName(String name) {
        int index = activityStack.size() - 1;
        while (true) {
            Activity activity = activityStack.get(index);
            if (null == activity) {
                break;
            }
            String activityName = activity.getComponentName().getClassName();
            if (!TextUtils.equals(name, activityName)) {
                index--;
                if (index < 0) {
                    break;
                }
                continue;
            }
            finishActivity(activity);
            break;
        }
    }

    /**
     * 获取Activity栈
     */
    public static Stack<Activity> getActivityStack() {
        Stack<Activity> stack = new Stack<>();
        stack.addAll(activityStack);
        return stack;
    }
}
