package com.yujing.utils;

import com.yujing.contract.YRun;

/**
 * Runnable线程，里面包含try，解决Runnable.run包含try过于复杂问题
 */
public class YRunnable implements Runnable {
    private final YRun yRun;

    public YRunnable(YRun yRun) {
        this.yRun = yRun;
    }

    @Override
    public void run() {
        if (yRun != null) {
            try {
                yRun.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}