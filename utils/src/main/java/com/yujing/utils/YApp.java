package com.yujing.utils;

import android.annotation.SuppressLint;
import android.app.Application;

/**
 * 全局Application
 */

/*用法
//初始化
YUtils.init(this)
//获取Application
YApp.get();
 */
public class YApp {
    /**
     * Application上下文对象
     */
    @SuppressLint("StaticFieldLeak")
    private static Application app;

    public static void set(Application app) {
        YApp.app = app;
    }

    public static Application get() {
        if (app == null)
            YLog.e("YApp.application == null\n请在Application中加入：\n YApp.set(application); \t 或 YUtils.init(application);");
        return app;
    }
}
