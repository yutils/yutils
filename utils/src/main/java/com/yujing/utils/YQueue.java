package com.yujing.utils;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 队列运行，等待指定时间后运行下一个。
 *
 * @author yujing 2019年2月15日17:23:15
 */
@SuppressWarnings({"unused", "WeakerAccess"})
/* 用法举例
val yQueue=YQueue()
//每秒最多赋值一次你好
yQueue.run(1000) { text.text ="你好1" }
yQueue.run(1000) { text.text ="你好2" }
yQueue.run(1000) { text.text ="你好3" }
 */
public class YQueue {
    /**
     * 线程队列同时最多运行个数
     */
    private static volatile int threadNum = 1;
    private ScheduledThreadPoolExecutor sTEP = new ScheduledThreadPoolExecutor(threadNum);

    /**
     * 运行
     *
     * @param time     时间毫秒
     * @param runnable 回调
     */
    @SuppressWarnings({"UnclearExpression"})
    public void run(final int time, final Runnable runnable) {
        Thread thread = new Thread(() -> {
            try {
                if (YUtils.isAndroid())
                    YThread.runOnUiThread(runnable);
                else
                    runnable.run();
                Thread.sleep(time);
            } catch (InterruptedException ignored) {
            }
        });
        add(thread);
    }

    /**
     * 把一个线程扔进线程池
     *
     * @param runnable 线程
     */
    private void add(Runnable runnable) {
        synchronized (sTEP) {
            if (sTEP.isShutdown()) {
                sTEP = new ScheduledThreadPoolExecutor(threadNum);
                synchronized (sTEP) {
                    sTEP.execute(runnable);
                }
            } else {
                sTEP.execute(runnable);
            }
        }
    }

    /**
     * 停止当前队列中全部请求
     */
    public void stopAll() {
        if (sTEP != null)
            sTEP.getQueue().clear();
    }

    /**
     * 关闭释放线程池,线程池有线程在运行时，运行完才会关闭
     */
    public void shutdown() {
        synchronized (sTEP) {
            if (!sTEP.isShutdown())
                sTEP.shutdown();
        }
    }

    /**
     * 退出释放线程
     */
    public void onDestroy() {
        shutdown();
        stopAll();
    }
}
