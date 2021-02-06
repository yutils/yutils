package com.yujing.contract;

/**
 * YLog 日志回调监听
 *
 * @author yujing 2020年10月15日15:18:01
 */
public interface YLogSaveListener {
    /**
     * 日志监听回调
     *
     * @param type 类型，如：v，d，i，w，e
     * @param tag  tag
     * @param msg  内容
     * @return 是否保存日志, 必须是已经开启日志保存
     */
    boolean value(String type, String tag, String msg);
}
