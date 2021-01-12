package com.yujing.utils;

/**
 * 延迟类
 *
 * @author 2020年9月6日21:07:25
 */
/* 使用举例
//2秒后打印“触发”
 YDelay.run(2000, new YDelay.DRun() {
    @Override
    public void delayedRun() {
       System.out.println("触发");
    }
});
 */
public class YDelay {
    /**
     * 延时运行
     *
     * @param time     时间毫秒
     * @param runnable 回调
     */
    @SuppressWarnings({"UnclearExpression"})
    public static void run(final int time, final Runnable runnable) {
        new Thread(() -> {
            try {
                if (YUtils.isAndroid()) {
                    YThread.runOnUiThreadDelayed(runnable, time);
                } else {
                    Thread.sleep(time);
                    runnable.run();
                }
            } catch (InterruptedException ignored) {
            }
        }).start();
    }
}

