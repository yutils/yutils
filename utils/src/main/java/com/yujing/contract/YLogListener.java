package com.yujing.contract;

/**
 * YLog 日志回调监听
 *
 * @author 余静 2020年10月15日15:18:01
 */
public interface YLogListener {
    /**
     * 日志监听回调
     *
     * @param type 类型，如：v，d，i，w，e
     * @param tag  tag
     * @param msg  内容
     */
    void value(String type, String tag, String msg);
}
