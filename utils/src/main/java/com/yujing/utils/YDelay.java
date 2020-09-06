package com.yujing.utils;

import android.app.Activity;
import android.os.Handler;

/**
 * 延迟类
 *
 * @author 2020年9月6日21:07:25
 */
@SuppressWarnings("unused")
/* 使用举例
//2秒后打印“触发”
 YDelay.run(2000, new YDelay.DRun() {
    @Override
    public void delayedRun() {
       System.out.println("触发");
    }
});
 */
public class YDelay {
    /**
     * 延时运行
     *
     * @param time     时间毫秒
     * @param runnable 回调
     */
    @SuppressWarnings({"UnclearExpression", "ConditionCoveredByFurtherCondition", "ConstantConditions"})
    public static void run(final int time, final Runnable runnable) {
        Object handler = null;
        //如果是能找到Handler对象，说明是安卓
        try {
            Class.forName("android.os.Handler");
            handler = new Handler();
        } catch (Exception ignored) {
        }
        Object finalHandler = handler;
        new Thread(() -> {
            try {
                Thread.sleep(time);
                if (finalHandler != null && finalHandler instanceof Handler) {
                    ((Handler) finalHandler).post(runnable);
                } else {
                    runnable.run();
                }
            } catch (InterruptedException ignored) {
            }
        }).start();
    }

    /**
     * 延时运行
     *
     * @param activity activity
     * @param time     时间毫秒
     * @param runnable 回调
     */
    public static void run(final Activity activity, final int time, final Runnable runnable) {
        new Thread(() -> {
            try {
                Thread.sleep(time);
            } catch (InterruptedException ignored) {
            }
            activity.runOnUiThread(() -> {
                if (activity.isDestroyed()) return;
                runnable.run();
            });
        }).start();
    }
}

