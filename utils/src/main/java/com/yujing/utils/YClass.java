package com.yujing.utils;

/**
 * Class的一些基本操作
 */
@SuppressWarnings("unused")
public class YClass {
    public static boolean findClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * 判断是否是安卓中运行
     *
     * @return 是否安卓
     */
    private static Boolean isAndroid = null;

    public static boolean isAndroid() {
        if (isAndroid != null) return isAndroid;
        isAndroid = findClass("android.os.Handler");
        return isAndroid;
    }
}
