package com.yujing.utils;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 线程池管理类
 *
 * @author 余静 2019年5月31日15:53:51
 */

/*举例

YThreadPool yPool = new YThreadPool();
yPool.setThreadNum(3);
yPool.setFinishListener(() -> yPool.shutdown());
yPool.setRunListener(pool -> System.out.println("当前排队线程数：" + pool.getQueue().size()));
for (int i = 0; i < 10; i++) {
    yPool.add(() -> {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("1111");
    });
}
 */
public class YThreadPool {
    private volatile int threadNum = 99;
    private ScheduledThreadPoolExecutor pool;
    private FinishListener finishListener;
    private RunListener runListener;

    private static volatile YThreadPool yThreadPool = null;

    public static YThreadPool getInstance() {
        if (yThreadPool == null) {
            synchronized (YThreadPool.class) {
                if (yThreadPool == null) yThreadPool = new YThreadPool();
            }
        }
        return yThreadPool;
    }

    /**
     * 停止当前队列中全部请求
     */
    public void stopAll() {
        if (pool != null)
            pool.getQueue().clear();
    }

    /**
     * 把一个线程扔进线程池
     *
     * @param runnable 要执行的线程
     */
    public synchronized void add(Runnable runnable) {
        if (pool == null || pool.isShutdown()) pool = new ScheduledThreadPoolExecutor(threadNum);
        pool.execute(() -> {
            runnable.run();
            //执行完一个线程回调
            if (runListener != null) runListener.run(pool);
            //全部执行完毕回调，当前排队线程数pool.getQueue().size()
            if (finishListener != null && pool.getQueue().size() == 0) finishListener.success();
        });
    }

    /**
     * 获取当前有多少线程
     *
     * @return 线程数量
     */
    /*
        int queueSize = pool.getQueue().size();
        System.out.println("当前排队线程数：" + queueSize);
        int activeCount = pool.getActiveCount();
        System.out.println("当前活动线程数：" + activeCount);
        long completedTaskCount = pool.getCompletedTaskCount();
        System.out.println("执行完成线程数：" + completedTaskCount);
        long taskCount = pool.getTaskCount();
        System.out.println("总线程数：" + taskCount);
     */
    public int getPoolSize() {
        return pool.isShutdown() ? -1 : pool.getPoolSize();
    }

    /**
     * 关闭释放线程池
     */
    public void shutdown() {
        synchronized (this) {
            if (!pool.isShutdown()) pool.shutdown();
        }
    }

    /**
     * 释放当前线程池，并重新创建线程池一个最大值未threadNum的线程池
     *
     * @param threadNum 线程池最大值
     */
    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
        if (pool != null && !pool.isShutdown()) pool.shutdownNow().clear();
    }

    /**
     * 移除队列中线程
     */
    public void remove(Runnable runnable) {
        if (pool != null) pool.remove(runnable);
    }

    /**
     * 线程执行完毕监听
     */
    public FinishListener getFinishListener() {
        return finishListener;
    }

    public void setFinishListener(FinishListener finishListener) {
        this.finishListener = finishListener;
    }

    public RunListener getRunListener() {
        return runListener;
    }

    /**
     * 每次执行完监听
     */
    public void setRunListener(RunListener runListener) {
        this.runListener = runListener;
    }

    /**
     * 全部执行完毕监听
     */
    public interface FinishListener {
        void success();
    }

    /**
     * 每次执行完监听
     */
    public interface RunListener {
        void run(ScheduledThreadPoolExecutor pool);
    }
}
