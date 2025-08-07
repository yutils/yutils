package com.yujing.base.contract;

/**
 * 生命周期事件监听，不只是生命周期
 */
@Deprecated
public interface YLifeEventListener {
    void event(YLifeEvent event, Object obj);
}
