package com.yujing.contract;

/**
 * 一次性返回三个参数
 *
 * @param <A> 参数1
 * @param <B> 参数2
 * @param <C> 参数3
 * @param <D> 参数4
 * @param <E> 参数5
 */
public class YReturn5<A, B, C, D, E> extends YReturn4<A, B, C, D> {

    /**
     * 第五个返回值
     **/
    private final E fifth;

    public YReturn5(A first, B second, C third, D forth, E fifth) {
        super(first, second, third, forth);
        this.fifth = fifth;
    }

    public E getFifth() {
        return fifth;
    }
}
