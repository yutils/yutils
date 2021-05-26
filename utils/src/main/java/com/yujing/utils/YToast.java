package com.yujing.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

/**
 * Toast，当第一个未消失时调用，直接覆盖文本值，并延长两秒显示时间
 * 或者队列显示一条toast 至少显示queueTime时间
 *
 * @author 余静 2019年2月18日11:27:58
 */
@SuppressWarnings("unused")
public class YToast {
    private static Toast toast;
    private static  YQueue yQueue;
    private static volatile int queueTime = 500;//队列显示一条toast至少显示这么长时间
    /**
     * 多条toast同时过来，只显示最后一条，显示时间为LENGTH_SHORT
     *
     * @param context context
     * @param text    内容
     */
    @SuppressLint("ShowToast")
    public static void show(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    /**
     * 多条toast同时过来，只显示最后一条，显示时间为LENGTH_LONG
     *
     * @param context context
     * @param text    内容
     */
    @SuppressLint("ShowToast")
    public static void showLong(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.show();
    }

    /**
     * 多条toast同时过来，每一条toast至少显示queueTime时间（毫秒）
     *
     * @param context context
     * @param text    内容
     */
    @SuppressLint("ShowToast")
    public static void showQueue(final Context context, final String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            if (yQueue == null) yQueue = new YQueue();
            yQueue.run(queueTime, () -> show(context, text));
        }
    }

    /**
     * 多条toast同时过来，每一条toast至少显示queueTime时间（毫秒）
     *
     * @param context context
     * @param text    内容
     */
    @SuppressLint("ShowToast")
    public static void showQueueLong(final Context context, final String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
            toast.show();
        } else {
            if (yQueue == null) yQueue = new YQueue();
            yQueue.run(queueTime, () -> showLong(context, text));
        }
    }

    /**
     * 获取队列显示时间
     * @return 毫秒
     */
    public static int getQueueTime() {
        return queueTime;
    }

    /**
     * 设置队列显示时间
     * @param queueTime 毫秒
     */
    public static void setQueueTime(int queueTime) {
        YToast.queueTime = queueTime;
    }
}