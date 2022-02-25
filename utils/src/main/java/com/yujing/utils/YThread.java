package com.yujing.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 线程操作
 *
 * @author 余静 2021年9月10日10:41:32
 */
/*
用法:
//统计当前有多少线程
YThread.countThread()
//获取全部线程
YThread.getAllThread()
//判断是否是在主线程（UI线程）
YThread.isMainThread()
//在主线程中运行
YThread.runOnUiThread { YLog.i("主线程") }
//在主线程中运行,延迟2秒后
YThread.runOnUiThreadDelayed({ YLog.i("主线程") },2000)
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
        if (runnable == null) return;
        if (!YClass.isAndroid()) {
            runnable.run();
            return;
        }
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
        if (runnable == null) return;
        if (!YClass.isAndroid()) {
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(delayMillis);
                    runnable.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            thread.setName("YThread-主线程中延时运行");
            thread.start();
            return;
        }
        HANDLER.postDelayed(runnable, delayMillis);
    }

    /**
     * 移除还未运行的线程
     */
    public static void remove(final Runnable runnable) {
        HANDLER.removeCallbacks(runnable);
    }

    /**
     * 统计当前有多少线程
     *
     * @return 线程数量
     */
    public static int countThread() {
        return Thread.getAllStackTraces().size();
    }

    /**
     * 获取当前全部线程
     *
     * @return List<Thread>
     */
    public static List<Thread> getAllThread() {
        List<Thread> list = new ArrayList<>();
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> stackTrace : allStackTraces.entrySet()) {
            list.add(stackTrace.getKey());
        }
        return list;
    }

    /**
     * 打印全部线程信息
     *
     * @return 线程信息
     */
    public static String printAllThread() {
        StringBuilder sb = new StringBuilder();
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> stackTrace : allStackTraces.entrySet()) {
            Thread thread = stackTrace.getKey();
            sb.append("线程：").append(thread.getName()).append(",id=").append(thread.getId()).append(",state=").append(thread.getState()).append("\n");
        }
        String str = sb.toString();
        YLog.i(str);
        return str;
    }

    /**
     * 打印全部线程堆栈信息
     *
     * @return 线程信息
     */
    public static String printAllThreadStackTraces() {
        StringBuilder sb = new StringBuilder();
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> stackTrace : allStackTraces.entrySet()) {
            Thread thread = stackTrace.getKey();
            sb.append("线程：").append(thread.getName()).append(",id=").append(thread.getId()).append(",state=").append(thread.getState()).append("\n");
            StackTraceElement[] stack = stackTrace.getValue();
            for (StackTraceElement stackTraceElement : stack) {
                sb.append(stackTraceElement.toString()).append("\n");
            }
        }
        String str = sb.toString();
        YLog.i(str);
        return str;
    }

}