package com.yujing.contract;

/**
 * 一次性返回三个参数
 *
 * @param <A> 参数1
 * @param <B> 参数2
 * @param <C> 参数3
 */
public class YReturn3<A, B, C> extends YReturn2<A, B> {

    /**
     * 第三个返回值
     **/
    private final C third;

    public YReturn3(A first, B second, C third) {
        super(first, second);
        this.third = third;
    }

    public C getThird() {
        return third;
    }
}
