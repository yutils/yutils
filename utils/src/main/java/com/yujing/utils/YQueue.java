package com.yujing.utils;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 队列运行，等待指定时间后运行下一个。
 *
 * @author yujing 2019年2月15日17:23:15
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class YQueue {
    /**
     * 线程队列同时最多运行个数
     */
    private static int threadNum = 1;
    private ScheduledThreadPoolExecutor sTEP = new ScheduledThreadPoolExecutor(threadNum);

    /**
     * 运行
     * @param time 时间毫秒
     * @param qRun 回调
     */
    public void run(final int time, final QRun qRun) {
        Thread thread = new Thread(() -> {
            try {
                qRun.queueRun();
                Thread.sleep(time);
            } catch (InterruptedException ignored) {
            } finally {
                shutdown();
            }

        });
        add(thread);
    }

    /**
     * 把一个线程扔进线程池
     * @param thread 线程
     */
    public void add(Thread thread) {
        synchronized (sTEP) {
            if (sTEP.isShutdown()) {
                sTEP = new ScheduledThreadPoolExecutor(threadNum);
                synchronized (sTEP) {
                    sTEP.execute(thread);
                }
            } else {
                sTEP.execute(thread);
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
     * 延时运行接口
     */
    public interface QRun {
        void queueRun();
    }
}
