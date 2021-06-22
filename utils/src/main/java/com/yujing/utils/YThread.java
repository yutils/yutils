package com.yujing.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * 主线程操作
 *
 * @author 余静 2021年1月11日23:16:27
 */
public final class YThread {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    /**
     * 返回线程是否为主线程。
     */
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * 获取主线程handler
     */
    public static Handler getMainHandler() {
        return HANDLER;
    }

    /**
     * 主线程中运行
     */
    public static void runOnUiThread(final Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            HANDLER.post(runnable);
        }
    }

    /**
     * 主线程中延时运行
     */
    public static void runOnUiThreadDelayed(final Runnable runnable, long delayMillis) {
        HANDLER.postDelayed(runnable, delayMillis);
    }

    /**
     * 移除还未运行的线程
     */
    public static void remove(final Runnable runnable) {
        HANDLER.removeCallbacks(runnable);
    }
}