package com.yujing.contract;

/**
 * 通用监听
 *
 * @param <Value1> 回调类型
 * @param <Value2> 回调类型
 * @param <Value3> 回调类型
 * @param <Value4> 回调类型
 * @author 余静 2020年4月15日11:30:07
 */

public interface YListener4<Value1, Value2, Value3, Value4> {
    void value(Value1 value1, Value2 value2, Value3 value3, Value4 value4);
}
