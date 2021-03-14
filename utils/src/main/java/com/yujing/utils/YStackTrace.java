package com.yujing.utils;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

/**
 * 堆栈跟踪类
 * 返回代码当前线程名，类名，行数
 *
 * @author yujing 2021年3月14日15:07:47
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
    private static String getJavaFileName(final StackTraceElement targetElement) {
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
