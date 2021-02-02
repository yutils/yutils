package com.yujing.utils;

import android.annotation.SuppressLint;
import android.app.Application;

/**
 * 全局Application
 */
public class YApp {
    /**
     * Application上下文对象
     */
    @SuppressLint("StaticFieldLeak")
    private static Application mContext;

    public static void init(Application context) {
        mContext = context;
    }

    public static Application get() {
        return mContext;
    }
}
