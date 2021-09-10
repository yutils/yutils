package com.yujing.utils;

/**
 * 延迟类
 *
 * @author 2020年9月6日21:07:25
 */
/* 使用举例
//2秒后打印“触发”
YDelay.run(2000, new Runnable() {
    @Override
    public void delayedRun() {
       System.out.println("触发");
    }
});

kotlin:
YDelay.run(2000){
    YLog.i("延迟运行")
}
 */
public class YDelay {
    /**
     * 延时运行
     *
     * @param time     时间毫秒
     * @param runnable 回调
     */
    /*
      val run =Runnable{ YLog.i("延迟运行") }
      YDelay.run(2000,run)
     */
    @SuppressWarnings({"UnclearExpression"})
    public static void run(final int time, final Runnable runnable) {
        Thread thread= new Thread(() -> {
            try {
                if (YClass.isAndroid()) {
                    YThread.runOnUiThreadDelayed(runnable, time);
                } else {
                    Thread.sleep(time);
                    runnable.run();
                }
            } catch (InterruptedException ignored) {
            }
        });
        thread.setName("延时运行");
        thread.start();
    }

    /**
     * 移除还没运行的线程
     *
     * @param runnable runnable
     */
    /*
      val run =Runnable{ YLog.i("延迟运行") }
      YDelay.remove(run)
     */
    public static void remove(Runnable runnable) {
        YThread.remove(runnable);
    }
}

