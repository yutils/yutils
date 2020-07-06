package com.yujing.utils;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 线程池管理类
 *
 * @author yujing 2019年5月31日15:53:51
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class YThreadPool {
    private static int threadNum = 99;
    private static ScheduledThreadPoolExecutor sTpe = new ScheduledThreadPoolExecutor(threadNum);

    /**
     * 停止当前队列中全部请求
     */
    public static void stopAll() {
        if (sTpe != null)
            sTpe.getQueue().clear();
    }
    /**
     * 把一个线程扔进线程池
     * @param runnable 要执行的线程
     */
    public synchronized static void add(Runnable runnable) {
        if (sTpe.isShutdown()) {
            sTpe = new ScheduledThreadPoolExecutor(threadNum);
            synchronized (sTpe) {
                sTpe.execute(runnable);
            }
        }else {
            sTpe.execute(runnable);
        }
    }

    /**
     *  获取当前有多少线程
     * @return 线程数量
     */
    public static int getPoolSize() {
        return sTpe.isShutdown() ? -1 : sTpe.getPoolSize();
    }

    /**
     * 关闭释放线程池
     */
    public synchronized static void shutdown() {
        synchronized (sTpe) {
            if (!sTpe.isShutdown())
                sTpe.shutdown();
        }
    }

    /**
     * 释放当前线程池，并重新创建线程池一个最大值未threadNum的线程池
     *
     * @param threadNum 线程池最大值
     */
    public static void setThreadNum(int threadNum) {
        YThreadPool.threadNum = threadNum;
        shutdown();
        sTpe = new ScheduledThreadPoolExecutor(threadNum);
    }
}
