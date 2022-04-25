package com.yujing.utils;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

/**
 * 堆栈跟踪类
 * 返回代码当前线程名，类名，行数
 *
 * @author 余静 2021年3月14日15:07:47
 */
/*
用法
//打印当前行
//main, com.yujing.test.activity.MainActivity$init$18.onClick(MainActivity.kt:112)
YStackTrace.getLine())

//打印当前行，向上偏移一行
YStackTrace.getLine(1))

//打印全部栈
//[main, com.yujing.test.activity.MainActivity$init$18.onClick(MainActivity.kt:112), main, android.view.View.performClick(View.java:5637), main, android.view.View$PerformClick.run(View.java:22445), main, android.os.Handler.handleCallback(Handler.java:755), main, android.os.Handler.dispatchMessage(Handler.java:95), main, android.os.Looper.loop(Looper.java:154), main, android.app.ActivityThread.main(ActivityThread.java:6157), main, java.lang.reflect.Method.invoke(Method.java:-2), main, com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:912), main, com.android.internal.os.ZygoteInit.main(ZygoteInit.java:802)]
YStackTrace.getLines())

//打印全部栈，自动换行
YStackTrace.printAll()
 */
public class YStackTrace {
    /**
     * 返回调用此方法的堆栈跟踪代码行数
     *
     * @return 线程名，类名，行数
     * 如：main, com.test.MainActivity$init$18.onClick(MainActivity.kt:119)
     */
    public static String getLine() {
        return getLine(1);
    }

    /**
     * 返回调用此方法堆栈跟踪的行数
     *
     * @param lineDeviation 偏移行，1就是向上级方法偏移一行，2就是向上级偏移两行
     * @return 线程名，类名，行数
     */
    public static String getLine(int lineDeviation) {
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        StackTraceElement targetElement = stackTrace[1 + lineDeviation];
        final String fileName = getJavaFileName(targetElement);
        return new Formatter()
                .format("%s, %s.%s(%s:%d)",
                        Thread.currentThread().getName(),
                        targetElement.getClassName(),
                        targetElement.getMethodName(),
                        fileName,
                        targetElement.getLineNumber())
                .toString();
    }

    /**
     * 返回整个堆栈跟踪代码行数
     *
     * @return 全部层级线程名，类名，行数
     */
    public static List<String> getLines() {
        List<String> strings = new ArrayList<>();
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        for (int i = 1; i < stackTrace.length; i++) {
            StackTraceElement targetElement = stackTrace[i];
            final String fileName = getJavaFileName(targetElement);
            strings.add(new Formatter()
                    .format("%s, %s.%s(%s:%d)",
                            Thread.currentThread().getName(),
                            targetElement.getClassName(),
                            targetElement.getMethodName(),
                            fileName,
                            targetElement.getLineNumber())
                    .toString());
        }
        return strings;
    }

    /**
     * 打印堆栈列表
     *
     * @return 全部线程名，类名，行数
     */
    public static String printAll() {
        List<String> s = getLines();
        StringBuilder str = new StringBuilder();
        for (int i = 1; i < s.size(); i++)
            str.append(s.get(i)).append("\n");
        return str.toString();
    }

    /**
     * 获取堆栈中的java行
     *
     * @param targetElement 堆栈跟踪
     * @return java类和行数
     */
    public static String getJavaFileName(final StackTraceElement targetElement) {
        String fileName = targetElement.getFileName();
        if (fileName != null) return fileName;
        String className = targetElement.getClassName();
        String[] classNameInfo = className.split("\\.");
        if (classNameInfo.length > 0) className = classNameInfo[classNameInfo.length - 1];
        int index = className.indexOf('$');
        if (index != -1) className = className.substring(0, index);
        return className + ".java";
    }

    /**
     * 获取堆栈中上一层对象的行数
     *
     * @param lineDeviation 偏移行，1就是向上级方法偏移一个类
     * @return 上一层对象的行数
     */
    /*
     //如：获取上一级调取本方法的类
     var s=YStackTrace.getLine(YStackTrace.getTopClassLine(1))
     */
    public static int getTopClassLine(int lineDeviation) {
        if (lineDeviation < 1) lineDeviation = 0;
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        String className = stackTrace[1].getClassName();
        int line = 0;
        int change = 0;
        //因为第一个是自己（i=0），所以从第二个开始
        for (int i = 1; i < stackTrace.length; i++) {
            StackTraceElement targetElement = stackTrace[i];
            if (!targetElement.getClassName().contains(className)) {
                change++;
                if (change == lineDeviation) {
                    line = i - 1;
                    break;
                }
                className = targetElement.getClassName();
            }
        }
        return line;
    }
}
