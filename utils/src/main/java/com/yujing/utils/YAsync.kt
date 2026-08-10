package com.yujing.utils

import java.util.Vector
import java.util.concurrent.TimeoutException


/**
 * 异步转同步，等待执行完毕的方法通知解锁，可以设置超时
 * @author yujing 2022年4月15日10:27:05
 */

/*
用法：

//等待执行完毕的方法通知abc解锁，超时3秒
val value = YAsync.getInstance().submit<String>("abc", 3000)
//结果：value=“YY”

//另外一个线程执行完后，通知解锁
YAsync.getInstance().finish("abc", "YY")



//等待
try {
    val value = YAsync.getInstance().submit<String>("abc", 5000) {
        //立即执行
    }
    YToast.showQueue("收到：$value")
} catch (e: Exception) {
    YToast.showQueue("未等到消息超时")
}

YAsync.getInstance().finish("abc", "YY")


//等待某个tag进入等待状态
YAsync.getInstance().ifNotHaveTagWait("abc", 5000)
YToast.showQueue("等到：abc")
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
    private var tagList: Vector<Command> = Vector()

    /**
     * 单例
     */
    companion object {
        private var instance: YAsync? = null
        fun getInstance(): YAsync {
            if (instance == null) {
                synchronized(YAsync::class.java) {
                    if (instance == null) instance = YAsync()
                }
            }
            return instance!!
        }
    }

    /**
     * 如果没有这个tag，就等待
     */
    @Throws(Exception::class)
    fun ifNotHaveTagWait(tag: String, timeout: Long? = null) {
        //锁内原子检查+注册，不阻塞
        val command = ifNotHaveTagCommand(tag, timeout) ?: return
        //锁外阻塞等待，避免持锁导致finish无法进入
        command.run()
        if (command.isTimeOut) throw TimeoutException("执行超时")
    }

    @Synchronized
    private fun ifNotHaveTagCommand(tag: String, timeout: Long?): Command? {
        for (command in tagList) {
            if (command.tag == tag) return null
        }
        val command = Command("wait_$tag", timeout, null)
        tagList.add(command)
        return command
    }

    /**
     * 执行完毕，释放tag，通知解锁
     */
    @Synchronized
    fun finish(tag: String, result: Any? = null) {
        //删除tag
        tagList.removeAll {
            return@removeAll if (it.tag == tag) it.finish(result) else false
        }
    }

    /**
     * 释放全部tag，通知解锁
     */
    @Synchronized
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
        val command = createCommand(tag, timeOut, runnable)
        command.run()
        //判断是否已经超时
        if (command.isTimeOut) {
            tagList.remove(command)
            throw TimeoutException("执行超时")
        }
        return command.result as T?
    }

    @Synchronized
    private fun createCommand(tag: String, timeOut: Long? = null, runnable: Runnable? = null): Command {
        //先判断是否有tag，检查必须在清理之前，否则永远不生效
        if (!isAllowSameTag && tagList.any { it.tag == tag }) throw Exception("tag:$tag 已存在")
        //清理同tag的旧command，以及等待该tag的wait_哨兵
        finish(tag)
        finish("wait_$tag")
        //创建command，并加入队列
        val command = Command(tag, timeOut, runnable)
        tagList.add(command)
        return command
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
        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
        private val lock = Object()

        /**
         * 是否已经超时
         */
        var isTimeOut = false

        /**
         * 超时线程
         */
        private var timeOutThread: Thread? = null

        // ✅ 新增：完成标志位
        @Volatile
        private var isFinished = false

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
                        //sleep正常结束=超时，加锁后检查是否已被finish
                        synchronized(lock) {
                            if (isFinished) return@Thread
                            isTimeOut = true
                            isFinished = true
                            lock.notifyAll()
                        }
                    } catch (ignore: InterruptedException) {
                        //finish在sleep期间调用interrupt，无需处理
                    }
                }
                timeOutThread?.start()
            }
            //执行方法,同步执行或异步执行
            runnable?.let { if (YAsync.getInstance().isSyncExecute) it.run() else Thread(it).start() }
            //加锁
            synchronized(lock) {
                while (!isFinished) {
                    lock.wait()
                }
            }
        }

        /**
         * 执行完毕，解除超时，解锁
         */
        fun finish(result: Any?): Boolean {
            this.result = result
            //终止倒计时
            timeOutThread?.interrupt()
            //解锁
            synchronized(lock) {
                isFinished = true  // ✅ 先设标志位，再 notify
                lock.notifyAll()
            }
            return true
        }

        override fun toString(): String {
            return "Command(tag='$tag', timeOut=$timeOut)"
        }
    }
}
