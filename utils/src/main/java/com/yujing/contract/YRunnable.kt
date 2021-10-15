package com.yujing.contract

/**
 * 给runnable外加了Exception注解
 */
/*
用法：
解决Thread.sleep必须try
Thread t  = new Thread((YRunnable) () -> {
    Thread.sleep(1000);
});
t.start();
 */
interface YRunnable : Runnable {
    @Throws(Throwable::class)
    override fun run()
}