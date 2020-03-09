package com.yujing.utils;

/**
 * 延迟类
 * @author yujing 2019年2月15日17:21:46
 */
@SuppressWarnings("unused")
public class YDelay {
    /**
     *  延时运行
     * @param time 时间毫秒
     * @param dRun 回调
     */
    public static void run(final int time, final DRun dRun) {
        new Thread(() -> {
            try {
                Thread.sleep(time);
            } catch (InterruptedException ignored) {
            }
            dRun.delayedRun();
        }).start();
    }

    /**
     * 延时运行接口
     */
    public interface DRun {
        void delayedRun();
    }
}

