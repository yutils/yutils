package com.yujing.utils;

import android.content.Context;
import android.util.Log;

import com.yujing.contract.YLogListener;
import com.yujing.contract.YLogSaveListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * LOG显示类
 * 日志显示调用的类名和代码行号
 * 解决AndroidStudio的logcat显示超长字符串的问题
 * 保存日志到本地文件夹
 * 清理某个时间点之前的日志
 *
 * @author 余静 2021年2月8日12:58:06
 */
/* 用法
    YLog.d("你好")
    YLog.i("tag","你好")
    //向上偏移一级，输出调用类和行数时，显示上级调用改函数的类和对应行数
    YLog.i("tag","你好",1)
    //日志监听
    YLog.setLogListener { type, tag, msg -> }
    //保存日志开
    YLog.saveOpen(YPath.getFilePath(this, "log"))
    //保存日志监听,不保存DEBUG
    YLog.setLogSaveListener { type, tag, msg -> return@setLogSaveListener type != YLog.DEBUG }
    //删除30天以前日志
    YLog.delDaysAgo(30)
 */
@SuppressWarnings({"FieldCanBeLocal", "WeakerAccess"})
public class YLog {
    public static SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public static SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
    //是否保存日志
    private static boolean isSave = false;
    //保存日志目录
    private static String saveLogDir;
    //规定每段显示的长度
    private static final int LOG_MAX_LENGTH = 4000;
    //默认TAG
    private static final String TAG = "YLog";
    //日志回调监听
    private static YLogListener logListener;
    //日志保存回调监听
    private static YLogSaveListener logSaveListener;
    //类型
    public static final String VERBOSE = "v";   //VERBOSE
    public static final String DEBUG = "d";     //DEBUG
    public static final String INFO = "i";      //INFO
    public static final String WARN = "w";      //WARN
    public static final String ERROR = "e";     //ERROR

    //-------------------------------------------静态方法↓-------------------------------------------

    public static void v(String msg) {
        println(TAG, msg, null, VERBOSE, 0);
    }

    public static void v(String TAG, String msg) {
        println(TAG, msg, null, VERBOSE, 0);
    }

    public static void v(String TAG, String msg, Throwable tr) {
        println(TAG, msg, tr, VERBOSE, 0);
    }

    public static void d(String msg) {
        println(TAG, msg, null, DEBUG, 0);
    }

    public static void d(String TAG, String msg) {
        println(TAG, msg, null, DEBUG, 0);
    }

    public static void d(String TAG, String msg, Throwable tr) {
        println(TAG, msg, tr, DEBUG, 0);
    }

    public static void i(String msg) {
        println(TAG, msg, null, INFO, 0);
    }

    public static void i(String TAG, String msg) {
        println(TAG, msg, null, INFO, 0);
    }

    public static void i(String TAG, String msg, Throwable tr) {
        println(TAG, msg, tr, INFO, 0);
    }

    public static void w(String msg) {
        println(TAG, msg, null, WARN, 0);
    }

    public static void w(String TAG, String msg) {
        println(TAG, msg, null, WARN, 0);
    }

    public static void w(String TAG, String msg, Throwable tr) {
        println(TAG, msg, tr, WARN, 0);
    }

    public static void e(String msg) {
        println(TAG, msg, null, ERROR, 0);
    }

    public static void e(String TAG, String msg) {
        println(TAG, msg, null, ERROR, 0);
    }

    public static void e(String TAG, String msg, Throwable tr) {
        println(TAG, msg, tr, ERROR, 0);
    }

    public static void e(String msg, Throwable tr) {
        println(TAG, msg, tr, ERROR, 0);
    }

    public static void e(Throwable tr) {
        println(TAG, "ERROR", tr, ERROR, 0);
    }

    public static void dJson(String str) {
        println(TAG, YUtils.jsonFormat(str), null, DEBUG, 0);
    }

    public static void dJson(String TAG, String str) {
        println(TAG, YUtils.jsonFormat(str), null, DEBUG, 0);
    }

    public static void iJson(String str) {
        println(TAG, YUtils.jsonFormat(str), null, INFO, 0);
    }

    public static void iJson(String TAG, String str) {
        println(TAG, YUtils.jsonFormat(str), null, INFO, 0);
    }

    //-------------------------------------------静态方法_偏移行↓-------------------------------------------
    public static void v(String msg, int lineDeviation) {
        println(TAG, msg, null, VERBOSE, lineDeviation);
    }

    public static void v(String TAG, String msg, int lineDeviation) {
        println(TAG, msg, null, VERBOSE, lineDeviation);
    }

    public static void v(String TAG, String msg, Throwable tr, int lineDeviation) {
        println(TAG, msg, tr, VERBOSE, lineDeviation);
    }

    public static void d(String msg, int lineDeviation) {
        println(TAG, msg, null, DEBUG, lineDeviation);
    }

    public static void d(String TAG, String msg, int lineDeviation) {
        println(TAG, msg, null, DEBUG, lineDeviation);
    }

    public static void d(String TAG, String msg, Throwable tr, int lineDeviation) {
        println(TAG, msg, tr, DEBUG, lineDeviation);
    }

    public static void i(String msg, int lineDeviation) {
        println(TAG, msg, null, INFO, lineDeviation);
    }

    public static void i(String TAG, String msg, int lineDeviation) {
        println(TAG, msg, null, INFO, lineDeviation);
    }

    public static void i(String TAG, String msg, Throwable tr, int lineDeviation) {
        println(TAG, msg, tr, INFO, lineDeviation);
    }

    public static void w(String msg, int lineDeviation) {
        println(TAG, msg, null, WARN, lineDeviation);
    }

    public static void w(String TAG, String msg, int lineDeviation) {
        println(TAG, msg, null, WARN, lineDeviation);
    }

    public static void w(String TAG, String msg, Throwable tr, int lineDeviation) {
        println(TAG, msg, tr, WARN, lineDeviation);
    }

    public static void e(String msg, int lineDeviation) {
        println(TAG, msg, null, ERROR, lineDeviation);
    }

    public static void e(String TAG, String msg, int lineDeviation) {
        println(TAG, msg, null, ERROR, lineDeviation);
    }

    public static void e(String TAG, String msg, Throwable tr, int lineDeviation) {
        println(TAG, msg, tr, ERROR, lineDeviation);
    }

    public static void e(String msg, Throwable tr, int lineDeviation) {
        println(TAG, msg, tr, ERROR, lineDeviation);
    }

    public static void e(Throwable tr, int lineDeviation) {
        println(TAG, "ERROR", tr, ERROR, lineDeviation);
    }

    public static void dJson(String str, int lineDeviation) {
        println(TAG, YUtils.jsonFormat(str), null, DEBUG, lineDeviation);
    }

    public static void dJson(String TAG, String str, int lineDeviation) {
        println(TAG, YUtils.jsonFormat(str), null, DEBUG, lineDeviation);
    }

    public static void iJson(String str, int lineDeviation) {
        println(TAG, YUtils.jsonFormat(str), null, INFO, lineDeviation);
    }

    public static void iJson(String TAG, String str, int lineDeviation) {
        println(TAG, YUtils.jsonFormat(str), null, INFO, lineDeviation);
    }
    //-------------------------------------------静态方法↑-------------------------------------------


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
        String filePath = saveLogDir + "/" + formatDate.format(new Date()) + ".log";
        if (logSaveListener != null) {
            if (logSaveListener.value(type, tag, msg)) save(filePath, type, tag, msg);
        } else save(filePath, type, tag, msg);
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
     * 打开日志本地保存
     * <p>
     * 默认路径，/storage/emulated/0/Android/data/com.xx.xx/files/log/
     *
     * @param context Context
     */
    public static void saveOpen(Context context) {
        saveOpen(YPath.getFilePath(context, "log"));
    }

    public static void saveOpen() {
        saveOpen(YPath.getFilePath(YApp.get(), "log"));
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
                long timeOld = formatDate.parse(date).getTime();
                long timeLimit = new Date().getTime() - 1000L * 60 * 60 * 24 * daysAgo;
                if (timeOld < timeLimit) {
                    YLog.i("清理日志", item.getPath());
                    del(date);
                }
            } catch (Throwable e) {
                YLog.e("清理日志失败", e);
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
     * @param TAG           tag
     * @param msg           内容
     * @param tr            异常
     * @param type          类型
     * @param lineDeviation 偏移行
     */
    private static void println(String TAG, String msg, Throwable tr, String type, int lineDeviation) {
        String codeLine = YStackTrace.getLine(2 + lineDeviation);
        if (msg == null) {
            if (YClass.isAndroid())
                Log.e(TAG, codeLine + " \n" + "日志内容为:null", tr);
            else
                System.err.println("TAG:" + TAG + "\tcodeLine:" + codeLine + " \n" + "日志内容为:null" + ((tr == null) ? "" : ("\tThrowable:" + tr.getMessage())));
            return;
        }
        List<StringBuilder> lines = YString.groupActual(msg, LOG_MAX_LENGTH - codeLine.length() - 10);
        int i = 1;
        for (StringBuilder item : lines) {
            //第一行要显示代码line，只有行就不显示line1行数
            String value = (i == 1 ? "★" + codeLine : "★--->" + i) + " \n" + item.toString();
            if (YClass.isAndroid()) {
                switch (type) {
                    case VERBOSE:
                        Log.v(TAG, value, tr);
                        break;
                    case DEBUG:
                        Log.d(TAG, value, tr);
                        break;
                    case INFO:
                        Log.i(TAG, value, tr);
                        break;
                    case WARN:
                        Log.w(TAG, value, tr);
                        break;
                    case ERROR:
                        Log.e(TAG, value, tr);
                        break;
                }
            } else {
                if (ERROR.equals(type)) {
                    System.err.println("TAG:" + TAG + "\tvalue:" + value + ((tr == null) ? "" : ("\tThrowable:" + tr.getMessage())));
                } else {
                    System.out.println("TAG:" + TAG + "\tvalue:" + value + ((tr == null) ? "" : ("\tThrowable:" + tr.getMessage())));
                }
            }
            i++;
        }
        //回调个监听函数
        if (logListener != null) logListener.value(type, TAG, msg);
        if (isSave) save(type, TAG, msg);
    }
}
