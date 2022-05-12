package com.yujing.utils

import java.util.concurrent.TimeoutException


/**
 * 异步转同步，等待执行完毕的方法通知解锁，可以设置超时
 * @author yujing 2022年4月15日10:27:05
 */

/*
用法：
//等待执行完毕的方法通知abc解锁，超时3秒
val value = YAsync.getInstance().submit<String>("abc", 3000) {
    //执行的方法
}
//结果：value=“YY”

//另外一个线程执行完后，通知解锁
YAsync.getInstance().finish("abc", "YY")

 */
class YAsync private constructor() {
    /**
     * 是否同步执行，默认同步，可以设置为异步
     */
    var isSyncExecute = true

    /**
     * 是否允许相同tag，如果不许相同tag，则抛出异常
     */
    var isAllowSameTag = true

    /**
     * 队列
     */
    private var tagList: MutableList<Command> = ArrayList()

    /**
     * 单例
     */
    companion object {
        private var instance: YAsync? = null
        fun getInstance(): YAsync {
            if (instance == null) instance = YAsync()
            return instance!!
        }
    }

    /**
     * 如果没有这个tag，就等待
     */
    @Throws(Exception::class)
    fun ifNotHaveTagWait(tag: String, timeout: Long? = null) {
        var isHave = false
        for (command in tagList) {
            if (command.tag == tag) {
                isHave = true
                break
            }
        }
        if (!isHave) submit<Any?>("wait_${tag}", timeout)
    }

    /**
     * 执行完毕，释放tag，通知解锁
     */
    fun finish(tag: String, result: Any? = null) {
        //删除tag
        tagList.removeAll {
            return@removeAll if (it.tag == tag) it.finish(result) else false
        }
    }

    /**
     * 释放全部tag，通知解锁
     */
    fun clear() {
        for (i in tagList.indices) tagList[i].finish(null)
        tagList.clear()
    }

    /**
     * 执行方法，等待结果
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(Exception::class)
    fun <T> submit(tag: String, timeOut: Long? = null, runnable: Runnable? = null): T? {
        finish("wait_${tag}")
        //判断是否有tag,有则抛出异常
        if (!isAllowSameTag && tagList.any { it.tag == tag }) throw Exception("tag:$tag 已存在")
        //创建command，并加入队列
        val command = Command(tag, timeOut, runnable)
        tagList.add(command)
        command.run()
        //判断是否已经超时
        if (command.isTimeOut) {
            tagList.remove(command)
            throw TimeoutException("执行超时")
        }
        return command.result as T?
    }

    /**
     * 执行类
     * @author yujing 2022年4月15日10:27:01
     */
    private class Command(var tag: String,/*tag 唯一标识*/var timeOut: Long?,/*t超时时间*/var runnable: Runnable? = null/*t执行*/) {
        /**
         * 执行结果
         */
        @Volatile
        var result: Any? = null

        /**
         * 锁
         */
        private val lock = Object()

        /**
         * 是否已经超时
         */
        var isTimeOut = false

        /**
         * 超时线程
         */
        private var timeOutThread: Thread? = null

        /**
         * 开始超时计时，执行，加锁
         */
        fun run() {
            //开始超时倒计时
            timeOut?.let {
                //超时后处理，标记已超时，并解锁
                timeOutThread = Thread {
                    try {
                        Thread.sleep(it)
                        //如果已经释放，则不再处理
                        if (Thread.interrupted()) return@Thread
                        //设置已经超时
                        isTimeOut = true
                        //解锁
                        synchronized(lock) { lock.notifyAll() }
                    } catch (ignore: InterruptedException) {
                        //线程提前被打断（interrupt）
                    }
                }
                timeOutThread?.start()
            }
            //执行方法,同步执行或异步执行
            runnable?.let { if (YAsync.getInstance().isSyncExecute) it.run() else Thread(it).start() }
            //加锁
            synchronized(lock) { lock.wait() }
        }

        /**
         * 执行完毕，解除超时，解锁
         */
        fun finish(result: Any?): Boolean {
            this.result = result
            //终止倒计时
            timeOutThread?.interrupt()
            //解锁
            synchronized(lock) { lock.notifyAll() }
            return true
        }
    }
}
