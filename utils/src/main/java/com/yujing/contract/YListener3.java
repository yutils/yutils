package com.yujing.contract;

/**
 * 通用监听
 *
 * @param <Value1> 回调类型
 * @param <Value2> 回调类型
 * @param <Value3> 回调类型
 * @author 余静 2020年4月15日11:30:11
 */

public interface YListener3<Value1, Value2, Value3> {
    void value(Value1 value1, Value2 value2, Value3 value3);
}
