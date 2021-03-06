package com.yujing.contract;

/**
 * 通用监听
 *
 * @param <Value> 回调类型
 * @author 余静 2020年1月13日17:14:03
 */
public interface YListener1<Value> {
    /**
     * 返回值
     *
     * @param value 值
     */
    void value(Value value);
}
