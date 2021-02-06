package com.yujing.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import com.yujing.contract.YLogListener;
import com.yujing.contract.YLogSaveListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

/**
 * LOG显示类，解决AndroidStudio的logcat显示超长字符串的问题
 * 保存日志到本地文件夹
 * 清理某个时间点之前的日志
 *
 * @author yujing 2020年10月12日17:06:15
 */
/* 用法
    //保存日志开
    YLog.saveOpen(YPath.getFilePath(this,"log"))
    YLog.setLogListener { type, tag, msg ->
        tag!= "忽略"
    }
    //保存最近30天日志
    YLog.delDaysAgo(30)
 */
@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public class YLog {
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    //是否保存日志
    private static boolean isSave = false;
    //保存日志目录
    private static String saveLogDir;
    //规定每段显示的长度
    private static int LOG_MAX_LENGTH = 4000;
    //默认TAG
    private static String TAG = "YLog";
    //日志回调监听
    private static YLogListener logListener;
    //日志保存回调监听
    private static YLogSaveListener logSaveListener;
    //类型
    private static final int VERBOSE = 2;
    private static final int DEBUG = 3;
    private static final int INFO = 4;
    private static final int WARN = 5;
    private static final int ERROR = 6;

    public static void v(String msg) {
        println(TAG, msg, null, VERBOSE);
    }

    public static void v(String TAG, String msg) {
        println(TAG, msg, null, VERBOSE);
    }

    public static void v(String TAG, String msg, Throwable tr) {
        println(TAG, msg, tr, VERBOSE);
    }

    public static void d(String msg) {
        println(TAG, msg, null, DEBUG);
    }

    public static void d(String TAG, String msg) {
        println(TAG, msg, null, DEBUG);
    }

    public static void d(String TAG, String msg, Throwable tr) {
        println(TAG, msg, tr, DEBUG);
    }

    public static void i(String msg) {
        println(TAG, msg, null, INFO);
    }

    public static void i(String TAG, String msg) {
        println(TAG, msg, null, INFO);
    }

    public static void i(String TAG, String msg, Throwable tr) {
        println(TAG, msg, tr, INFO);
    }

    public static void w(String msg) {
        println(TAG, msg, null, WARN);
    }

    public static void w(String TAG, String msg) {
        println(TAG, msg, null, WARN);
    }

    public static void w(String TAG, String msg, Throwable tr) {
        println(TAG, msg, tr, WARN);
    }

    public static void e(String msg) {
        println(TAG, "ERROR", null, ERROR);
    }

    public static void e(String TAG, String msg) {
        println(TAG, "ERROR", null, ERROR);
    }

    public static void e(String TAG, Throwable tr) {
        println(TAG, "ERROR", tr, ERROR);
    }

    public static void e(Throwable tr) {
        println(TAG, "ERROR", tr, ERROR);
    }

    public static void e(String TAG, String msg, Throwable tr) {
        println(TAG, msg, tr, ERROR);
    }

    public static void dJson(String str) {
        println(TAG, YUtils.jsonFormat(str), null, DEBUG);
    }

    public static void dJson(String TAG, String str) {
        println(TAG, YUtils.jsonFormat(str), null, DEBUG);
    }

    public static void iJson(String str) {
        println(TAG, YUtils.jsonFormat(str), null, INFO);
    }

    public static void iJson(String TAG, String str) {
        println(TAG, YUtils.jsonFormat(str), null, INFO);
    }

    public static YLogListener getLogListener() {
        return logListener;
    }

    public static void setLogListener(YLogListener logListener) {
        YLog.logListener = logListener;
    }

    public static YLogSaveListener getLogSaveListener() {
        return logSaveListener;
    }

    public static void setLogSaveListener(YLogSaveListener logSaveListener) {
        YLog.logSaveListener = logSaveListener;
    }

    //如 save("路径",“v”,“错误”,“网络异常”);
    public static void save(String path, String type, String tag, String msg) {
        String saveString = formatTime.format(new Date()) + "\t" + type + "\t" + (TAG.equals(tag) ? "log" : tag) + ":" + msg + "\n";
        YFileUtil.addStringToFile(new File(path), saveString);
    }

    public static void save(String type, String tag, String msg) {
        if (saveLogDir == null) return;
        if (logSaveListener != null) {
            boolean isSave = logSaveListener.value(type, tag, msg);
            if (isSave)
                save(saveLogDir + "/" + formatDate.format(new Date()) + ".log", type, tag, msg);
        } else {
            save(saveLogDir + "/" + formatDate.format(new Date()) + ".log", type, tag, msg);
        }
    }

    /**
     * 打开日志本地保存
     *
     * @param dir 路径，建议context.getExternalFilesDir(dir).getAbsolutePath();
     */
    public static void saveOpen(String dir) {
        isSave = true;
        saveLogDir = dir;
    }

    /**
     * 关闭日志本地保存
     */
    public static void saveClose() {
        isSave = false;
        saveLogDir = null;
    }

    /**
     * 删除几天前日志
     */
    public static void delDaysAgo(int daysAgo) {
        if (saveLogDir == null) return;
        List<File> files = YFileUtil.getFileAll(new File(saveLogDir));
        for (File item : files) {
            int index = item.getName().lastIndexOf(".log");
            if (index == -1) continue;
            String date = item.getName().substring(0, index);
            try {
                long time = formatDate.parse(date).getTime();
                if (time + (1000 * 60 * 60 * 24) * daysAgo < (new Date().getTime())) {
                    Log.i("清理日志", item.getPath());
                    del(date);
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 删除日志
     */
    public static void del(String date) {
        if (saveLogDir == null) return;
        YFileUtil.delFile(saveLogDir + "/" + date + ".log");
    }

    /**
     * 删除全部日志
     */
    public static void delAll() {
        if (saveLogDir == null) return;
        YFileUtil.delFile(saveLogDir);
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
        if (msg == null) {
            String codeLine = getLine(3);
            Log.e(TAG, codeLine + " \n" + "日志内容为:null", tr);
            return;
        }
        String codeLine = getLine(3);
        List<StringBuilder> lines = YString.groupActual(msg, LOG_MAX_LENGTH - codeLine.length() - 10);
        int i = 1;
        for (StringBuilder item : lines) {
            //第一行要显示代码line，只有行就不显示line1行数
            String value = (i == 1 ? "★" + codeLine : "★--->" + i) + " \n" + item.toString();
            if (type == VERBOSE)
                Log.v(TAG, value, tr);
            else if (type == DEBUG)
                Log.d(TAG, value, tr);
            else if (type == INFO)
                Log.i(TAG, value, tr);
            else if (type == WARN)
                Log.w(TAG, value, tr);
            else if (type == ERROR)
                Log.e(TAG, value, tr);
            i++;
        }
        if (logListener != null) {
            if (type == VERBOSE)
                logListener.value("v", TAG, msg);
            else if (type == DEBUG)
                logListener.value("d", TAG, msg);
            else if (type == INFO)
                logListener.value("i", TAG, msg);
            else if (type == WARN)
                logListener.value("w", TAG, msg);
            else if (type == ERROR)
                logListener.value("e", TAG, msg);
        }
        if (isSave) {
            if (type == VERBOSE)
                save("v", TAG, msg);
            else if (type == DEBUG)
                save("d", TAG, msg);
            else if (type == INFO)
                save("i", TAG, msg);
            else if (type == WARN)
                save("w", TAG, msg);
            else if (type == ERROR)
                save("e", TAG, msg);
        }
    }

    public static String getLine(int stackIndex) {
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        StackTraceElement targetElement = stackTrace[stackIndex];
        final String fileName = getFileName(targetElement);
        return new Formatter()
                .format("%s, %s.%s(%s:%d)",
                        Thread.currentThread().getName(),
                        targetElement.getClassName(),
                        targetElement.getMethodName(),
                        fileName,
                        targetElement.getLineNumber())
                .toString();
    }

    private static String getFileName(final StackTraceElement targetElement) {
        String fileName = targetElement.getFileName();
        if (fileName != null) return fileName;
        String className = targetElement.getClassName();
        String[] classNameInfo = className.split("\\.");
        if (classNameInfo.length > 0) className = classNameInfo[classNameInfo.length - 1];
        int index = className.indexOf('$');
        if (index != -1) className = className.substring(0, index);
        return className + ".java";
    }
}
