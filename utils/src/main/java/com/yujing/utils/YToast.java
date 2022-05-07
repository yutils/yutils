package com.yujing.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Toast，当第一个未消失时调用，直接覆盖文本值，并延长两秒显示时间
 * 或者队列显示一条toast 至少显示queueTime时间
 *
 * @author 余静 2019年2月18日11:27:58
 */
@SuppressWarnings("unused")
@SuppressLint("ShowToast")
public class YToast {
    private static Toast toast;
    private static YQueue yQueue;
    private static volatile int queueTime = 1000;//队列显示一条toast至少显示这么长时间
    public static boolean SHOW_LOG = true;//是否显示log
    public static List<String> history = new ArrayList<>();//历史记录，倒序，最多1000条

    /**
     * 显示一条toast
     *
     * @param text 内容
     */
    public static void show(String text) {
        show(YApp.get(), text);
    }

    public static void show(String text, int topClass) {
        show(YApp.get(), text, topClass);
    }

    /**
     * 显示toast 并播放语音
     *
     * @param text 语音内容
     */
    public static void showSpeak(String text) {
        show(YApp.get(), text, 0);
        TTS.speak(text);
    }

    public static void showSpeak(String text, int topClass) {
        show(YApp.get(), text, topClass);
        TTS.speak(text);
    }

    public static void show(Context context, String text) {
        show(context, text, 0);
    }

    /**
     * 多条toast同时过来，只显示最后一条，显示时间为LENGTH_SHORT
     *
     * @param context context
     * @param text    内容
     */
    public static void show(Context context, String text, int topClass) {
        if (context == null || text == null) return;
        if (SHOW_LOG) YLog.i("Toast: " + text, YStackTrace.getTopClassLine(1 + topClass));
        YThread.runOnUiThread(() -> {
            if (toast != null) {
                toast.cancel();
                toast = null;
            }
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
            history.add(0, text);
            if (history.size() > 1000) history.remove(history.size() - 1);
        });
    }

    /**
     * 显示一条toast
     *
     * @param text 内容
     */
    public static void showLong(String text) {
        showLong(YApp.get(), text);
    }

    public static void showLong(String text, int topClass) {
        showLong(YApp.get(), text, topClass);
    }

    /**
     * 显示toast 并播放语音
     *
     * @param text 语音内容
     */
    public static void showLongSpeak(String text) {
        show(YApp.get(), text);
        TTS.speak(text);
    }

    public static void showLongSpeak(String text, int topClass) {
        show(YApp.get(), text, topClass);
        TTS.speak(text);
    }

    public static void showLong(Context context, String text) {
        showLong(context, text, 0);
    }

    /**
     * 多条toast同时过来，只显示最后一条，显示时间为LENGTH_LONG
     *
     * @param context context
     * @param text    内容
     */
    public static void showLong(Context context, String text, int topClass) {
        if (context == null || text == null) return;
        if (SHOW_LOG) YLog.i("Toast: " + text, YStackTrace.getTopClassLine(1 + topClass));
        YThread.runOnUiThread(() -> {
            if (toast != null) {
                toast.cancel();
                toast = null;
            }
            toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
            toast.show();
            history.add(0, text);
            if (history.size() > 1000) history.remove(history.size() - 1);
        });
    }

    public static void showQueue(final String text) {
        showQueue(YApp.get(), text);
    }

    /**
     * 多条toast同时过来，每一条toast至少显示queueTime时间（毫秒）
     *
     * @param context context
     * @param text    内容
     */
    public static void showQueue(final Context context, final String text) {
        if (context == null || text == null) return;
        YThread.runOnUiThread(() -> {
            if (yQueue == null) yQueue = new YQueue();
            yQueue.run(queueTime, () -> show(context, text));
        });
    }

    public static void showQueueLong(final String text) {
        showQueueLong(YApp.get(), text);
    }

    /**
     * 多条toast同时过来，每一条toast至少显示queueTime时间（毫秒）
     *
     * @param context context
     * @param text    内容
     */
    @SuppressLint("ShowToast")
    public static void showQueueLong(final Context context, final String text) {
        if (context == null || text == null) return;
        YThread.runOnUiThread(() -> {
            if (yQueue == null) yQueue = new YQueue();
            yQueue.run(queueTime, () -> showLong(context, text));
        });
    }


    /**
     * 获取队列显示时间
     *
     * @return 毫秒
     */
    public static int getQueueTime() {
        return queueTime;
    }

    /**
     * 设置队列显示时间
     *
     * @param queueTime 毫秒
     */
    public static void setQueueTime(int queueTime) {
        YToast.queueTime = queueTime;
    }

    /**
     * 获取历史记录
     *
     * @return
     */
    public static List<String> getHistory() {
        return history;
    }
}