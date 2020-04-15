package com.yujing.contract;

/**
 * 通用监听
 *
 * @param <Value1> 回调类型
 * @param <Value2> 回调类型
 * @author yujing 2020年1月13日17:11:44
 */

public interface YListener2<Value1, Value2> {
    void value(Value1 value1, Value2 value2);
}
