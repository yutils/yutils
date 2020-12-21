package com.yujing.contract;

/**
 * 一次性返回两个参数
 *
 * @param <A> 参数1
 * @param <B> 参数2
 */
public class YReturn2<A, B> {
    /**
     * 第一个返回值
     **/
    private final A first;

    /**
     * 第二个返回值
     **/
    private final B second;

    public YReturn2(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }
}
