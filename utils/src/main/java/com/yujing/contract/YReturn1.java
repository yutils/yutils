package com.yujing.contract;

/**
 * 一次性返回两个参数
 *
 * @param <A> 参数1
 */
public class YReturn1<A> {
    /**
     * 第一个返回值
     **/
    private final A first;


    public YReturn1(A first) {
        this.first = first;
    }

    public A getFirst() {
        return first;
    }
}
