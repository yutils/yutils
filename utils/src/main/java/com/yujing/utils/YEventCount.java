package com.yujing.utils;

/**
 * 事件统计器，判断在N时间内是否触发了M次
 *
 * @author 余静 2018年5月15日19:00:17
 */
/*
private var eventBack = YEventCount(2000, 2) //2秒内按2次

//退出
eventBack.setEventSuccessListener { finish() }
eventBack.setEventFailListener { _: Long -> show("再按一次退出") }

override fun onBackPressed() {
    eventBack.event()
}
 */
@SuppressWarnings("unused")
public class YEventCount {
    private int frequency = 1;//当前是第几次
    private long oldTime = 0;//上次计时时间
    private long time;//多长时间内
    private long count;//触发多少次
    private EventSuccessListener eventSuccessListener;
    private EventFailListener eventFailListener;

    public YEventCount(long time, long count) {
        this.time = time;
        this.count = count;
    }

    public void setEventSuccessListener(EventSuccessListener eventSuccessListener) {
        this.eventSuccessListener = eventSuccessListener;
    }

    public void setEventFailListener(EventFailListener eventFailListener) {
        this.eventFailListener = eventFailListener;
    }

    public void event() {
        if (count <= 1) {
            eventSuccessListener.success();
            return;
        }
        if (System.currentTimeMillis() - oldTime < time) {
            if (++frequency >= count) {
                frequency = 0;
                oldTime = 0;
                if (eventSuccessListener != null) {
                    eventSuccessListener.success();
                }
            } else {
                if (eventFailListener != null) {
                    eventFailListener.fail(frequency);
                }
            }
        } else {
            frequency = 1;
            oldTime = System.currentTimeMillis();
            if (eventFailListener != null) {
                eventFailListener.fail(frequency);
            }
        }
    }

    public interface EventSuccessListener {
        void success();
    }

    public interface EventFailListener {
        void fail(long count);
    }
}
