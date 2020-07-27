package com.yujing.crypt;

import com.yujing.contract.YListener3;
import com.yujing.utils.YNumber;

//计算进度线程
public class YProgressThread extends Thread {
    long length;// 总长度
    long available;// 当前剩余长度
    private boolean show = true;// 是否允许获取进度
    private boolean finish = false;// 是否完成
    private YListener3<Long, Long, Double> progressListener;

    public void setProgressListener(YListener3<Long, Long, Double> progressListener) {
        this.progressListener = progressListener;
    }

    public YProgressThread(long length) {
        this.length = length;
        this.start();
    }

    public void set(long available) {
        this.available = available;
        this.show = false;
    }

    @Override
    public void run() {
        try {
            while (!finish) {
                if (available != 0) {
                    print();
                }
                Thread.sleep(100);
                this.show = true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isShow() {
        return show;
    }

    private void print() {
        long finish = length - available;
        double progress = YNumber.D2D((finish / (double) length) * 100d);
        if (progressListener != null) {
            progressListener.value(finish, length, progress);
        }
        String show = "长度：" + length + "完成：" + finish + "剩余：" + available + "进度：" + progress;
        System.out.println(show);
    }

    public void finish() {
        finish = true;
        available = 0;
        print();
    }

    public void stopShow() {
        finish = true;
    }
}
