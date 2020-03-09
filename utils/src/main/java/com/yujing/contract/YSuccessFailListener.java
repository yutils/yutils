package com.yujing.contract;

/**
 * 通用成功失败监听
 *
 * @param <Success> 成功回调类型
 * @param <Success> 失败回调类型
 * @author 2020年1月13日17:13:29
 */
public interface YSuccessFailListener<Success, Fail> {
    /**
     * 成功
     *
     * @param success 值
     */
    void success(Success success);

    /**
     * 失败
     *
     * @param fail 值
     */
    void fail(Fail fail);
}
