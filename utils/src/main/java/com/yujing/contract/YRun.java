package com.yujing.contract;

/**
 * run回调，解决handle.Post里面有try，代码复制问题
 * @author yujing 2020年7月28日10:23:45
 */
public interface YRun {
    void run() throws Exception;
}
