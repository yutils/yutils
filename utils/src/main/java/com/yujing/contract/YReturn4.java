package com.yujing.contract;

/**
 * 一次性返回三个参数
 *
 * @param <A> 参数1
 * @param <B> 参数2
 * @param <C> 参数3
 * @param <D> 参数4
 */
public class YReturn4<A, B, C, D> extends YReturn3<A, B, C> {

    /**
     * 第四个返回值
     **/
    private final D forth;

    public YReturn4(A first, B second, C third, D forth) {
        super(first, second, third);
        this.forth = forth;
    }

    public D getForth() {
        return forth;
    }
}
