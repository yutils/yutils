package com.yujing.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * 循环执行
 * @author yujing 2022年6月7日10:53:30
 */
/*
用法：
val yTimer = YTimer()

//每秒调用一次
yTimer.loopIO(1000) {  }

//每秒调用一次，并且取消上一次的循环，不重复多个timer
var job: Job? = null
job?.cancel()
job = yTimer.loopIO(1000) {  }

//每秒调用一次，最多调用 5 次（maxNumber 为精确次数），或累计不超过 10 秒，回调 UI 线程
yTimer.loopUI(1000, 5, 10000) {  }

//同步轮询：listener 返回非 null 即停，或达到 maxNumber / 超时
val result = YTimer.loopSync(200, 10, 5000) { null }

//退出时关闭
override fun onDestroy() {
    super.onDestroy()
    yTimer.stop()
}

 */
class YTimer {
    //作用域
    var myScope: CoroutineScope? = null
        get() {
            // 检查当前作用域是否有效（非空且未取消）
            if (field != null && field!!.coroutineContext[Job]?.isCancelled == false) {
                return field
            }
            // 无效则创建新作用域（添加默认调度器，如Dispatchers.Default）
            field = CoroutineScope(SupervisorJob() + Dispatchers.Default)
            return field
        }

    /**
     * 循环执行（IO线程），执行完毕后休息指定时间后继续,直到最大执行次数或者超时
     * @param intervalTime 间隔时间
     * @param maxNumber 最多执行多少次
     * @param maxMillisecond 最多执行多少毫秒
     * @param listener 执行回调监听
     */
    @Synchronized
    fun loopIO(intervalTime: Long, maxNumber: Long = Long.MAX_VALUE, maxMillisecond: Long = Long.MAX_VALUE, listener: () -> Unit): Job? {
        //次数统计
        var count = 0L
        //开始时间
        val startTime = System.currentTimeMillis()
        //摧毁之前线程
        return myScope?.launch(Dispatchers.IO) {
            //当统计次数大于最大统计次数，或者执行时间大于最大时间，立即停止循环
            while (this.isActive) {
                try {
                    count++
                    if (count > maxNumber) break
                    if (System.currentTimeMillis() - startTime > maxMillisecond) break
                    listener.invoke()
                    delay(intervalTime)
                } catch (e: Exception) {
                    e.printStackTrace()
                    delay(intervalTime)
                    continue
                }
            }
        }
    }

    /**
     * 循环执行（UI线程），执行完毕后休息指定时间后继续
     * @param intervalTime 间隔时间
     * @param maxNumber 最多执行多少次
     * @param maxMillisecond 最多执行多少毫秒
     * @param listener 执行回调监听
     */
    @Synchronized
    fun loopUI(intervalTime: Long, maxNumber: Long = Long.MAX_VALUE, maxMillisecond: Long = Long.MAX_VALUE, listener: () -> Unit): Job? {
        //次数统计
        var count = 0L
        //开始时间
        val startTime = System.currentTimeMillis()
        //摧毁之前线程
        return myScope?.launch(Dispatchers.Main) {
            //当统计次数大于最大统计次数，或者执行时间大于最大时间，立即停止循环
            while (this.isActive) {
                try {
                    count++
                    if (count > maxNumber) break
                    if (System.currentTimeMillis() - startTime > maxMillisecond) break
                    listener.invoke()
                    delay(intervalTime)
                } catch (e: Exception) {
                    e.printStackTrace()
                    delay(intervalTime)
                    continue
                }
            }
        }
    }

    /**
     * 停止(全部)循环执行
     */
    fun stop() {
        myScope?.cancel()
    }

    companion object {
        /**
         * 同步执行，执行完毕后休息指定时间后继续,直到获取到返回值或最大执行次数或超时
         * @param intervalTime 间隔时间
         * @param maxNumber 最多执行多少次（精确次数，例如 5 表示最多执行 5 次）
         * @param maxMillisecond 最多执行多少毫秒
         * @param listener 执行回调监听，当返回值不为null时，立即停止循环
         * @return 返回值
         */
        fun <T> loopSync(intervalTime: Long, maxNumber: Long = Long.MAX_VALUE, maxMillisecond: Long = Long.MAX_VALUE, listener: () -> T?): T? {
            //次数统计
            var count = 0L
            //开始时间
            val startTime = System.currentTimeMillis()
            //返回的对象
            var obj: T? = null
            runBlocking {
                // count 从 0 递增；条件为 count < maxNumber，保证最多执行 maxNumber 次
                while (count < maxNumber && System.currentTimeMillis() - startTime <= maxMillisecond) {
                    try {
                        count++
                        obj = listener.invoke()
                        if (obj != null) break
                        delay(intervalTime)
                    } catch (e: InterruptedException) {
                        break
                    } catch (e: Exception) {
                        e.printStackTrace()
                        delay(intervalTime)
                        continue
                    }
                }
            }
            return obj
        }
    }
}

