package com.yujing.utils;

import android.util.Log;

import java.util.List;

/**
 * LOG显示类，解决AndroidStudio的logcat显示超长字符串的问题
 *
 * @author yujing 2020年4月28日14:59:59
 */

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public class YLog {
    //规定每段显示的长度
    private static int LOG_MAX_LENGTH = 4000;
    private static String TAG = "YLog";
    private static final int VERBOSE = 2;
    private static final int DEBUG = 3;
    private static final int INFO = 4;
    private static final int WARN = 5;
    private static final int ERROR = 6;

    public static void v(String msg) {
        v(TAG, msg);
    }

    public static void v(String TAG, String msg) {
        v(TAG, msg, null);
    }

    public static void v(String TAG, String msg, Throwable tr) {
        println(TAG, msg, tr, VERBOSE);
    }

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void d(String TAG, String msg) {
        d(TAG, msg, null);
    }

    public static void d(String TAG, String msg, Throwable tr) {
        println(TAG, msg, tr, DEBUG);
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

    public static void i(String TAG, String msg) {
        i(TAG, msg, null);
    }

    public static void i(String TAG, String msg, Throwable tr) {
        println(TAG, msg, tr, INFO);
    }

    public static void w(String msg) {
        w(TAG, msg);
    }

    public static void w(String TAG, String msg) {
        w(TAG, msg, null);
    }

    public static void w(String TAG, String msg, Throwable tr) {
        println(TAG, msg, tr, WARN);
    }

    public static void e(String msg) {
        e(TAG, msg);
    }

    public static void e(String TAG, String msg) {
        e(TAG, msg, null);
    }

    public static void e(String TAG, Throwable tr) {
        e(TAG, "ERROR", tr);
    }

    public static void e(Throwable tr) {
        e(TAG, "ERROR", tr);
    }

    public static void e(String TAG, String msg, Throwable tr) {
        println(TAG, msg, tr, ERROR);
    }

    public static void json(String str) {
        json(TAG, str);
    }

    public static void json(String TAG, String str) {
        d(TAG, YUtils.jsonFormat(str));
    }

    /**
     * 打印日志
     *
     * @param TAG  tag
     * @param msg  内容
     * @param tr   异常
     * @param type 类型
     */
    private static void println(String TAG, String msg, Throwable tr, int type) {
        List<StringBuilder> lines = YString.groupActual(msg, LOG_MAX_LENGTH);
        int i = 1;
        for (StringBuilder item : lines) {
            String tag = lines.size() == 1 ? TAG : TAG + i;
            if (type == VERBOSE)
                Log.v(tag, item.toString(), tr);
            else if (type == DEBUG)
                Log.d(tag, item.toString(), tr);
            else if (type == INFO)
                Log.i(tag, item.toString(), tr);
            else if (type == WARN)
                Log.w(tag, item.toString(), tr);
            else if (type == ERROR)
                Log.e(tag, item.toString(), tr);
            i++;
        }
    }

//    /**
//     * 打印日志
//     *
//     * @param TAG  tag
//     * @param msg  内容
//     * @param tr   异常
//     * @param type 类型
//     */
//    @Deprecated
//    private static void println(String TAG, String msg, Throwable tr, int type) {
//        int strLength = msg.length();
//        int start = 0;
//        int end = LOG_MAX_LENGTH;
//        for (int i = 0; i < 100; i++) {
//            //剩下的文本还是大于规定长度则继续重复截取并输出
//            if (strLength > end) {
//                String tag = TAG + i;
//                if (type == VERBOSE)
//                    Log.v(tag, msg.substring(start, end), tr);
//                else if (type == DEBUG)
//                    Log.d(tag, msg.substring(start, end), tr);
//                else if (type == INFO)
//                    Log.i(tag, msg.substring(start, end), tr);
//                else if (type == WARN)
//                    Log.w(tag, msg.substring(start, end), tr);
//                else if (type == ERROR)
//                    Log.e(tag, msg.substring(start, end), tr);
//                start = end;
//                end = end + LOG_MAX_LENGTH;
//            } else {
//                String tag = i == 0 ? TAG : TAG + i;
//                if (type == VERBOSE) {
//                    Log.v(tag, msg.substring(start, strLength), tr);
//                } else if (type == DEBUG)
//                    Log.d(tag, msg.substring(start, strLength), tr);
//                else if (type == INFO)
//                    Log.i(tag, msg.substring(start, strLength), tr);
//                else if (type == WARN)
//                    Log.w(tag, msg.substring(start, strLength), tr);
//                else if (type == ERROR)
//                    Log.e(tag, msg.substring(start, strLength), tr);
//                break;
//            }
//        }
//    }
}
