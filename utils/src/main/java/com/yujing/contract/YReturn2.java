package com.yujing.contract;

/**
 * 一次性返回两个参数
 *
 * @param <A> 参数1
 * @param <B> 参数2
 */
public class YReturn2<A, B> extends YReturn1<A> {

    /**
     * 第二个返回值
     **/
    private final B second;

    public YReturn2(A first, B second) {
        super(first);
        this.second = second;
    }

    public B getSecond() {
        return second;
    }
}
