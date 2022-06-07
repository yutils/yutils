package com.yujing.utils

/**
 * 循环执行
 * @author yujing 2022年6月7日10:53:30
 */
/*
用法：
val yTimer = YTimer()

//每秒调用一次
yTimer.loopIO(1000) {  }

//每秒调用一次，最多调用5次，或者10秒,回调UI线程
yTimer.loopUI(1000,5,10000) {  }

//退出时关闭
override fun onDestroy() {
    super.onDestroy()
    yTimer.stop()
}

 */
class YTimer {
    //循环线程
    private var loopThread: Thread? = null

    /**
     * 循环执行（IO线程），执行完毕后休息指定时间后继续,直到最大执行次数或者超时
     * @param intervalTime 间隔时间
     * @param maxNumber 最多执行多少次
     * @param maxMillisecond 最多执行多少毫秒
     * @param listener 执行回调监听
     */
    @Synchronized
    fun loopIO(intervalTime: Long, maxNumber: Long = Long.MAX_VALUE, maxMillisecond: Long = Long.MAX_VALUE, listener: () -> Unit) {
        //次数统计
        var count = 0L
        //开始时间
        val startTime = System.currentTimeMillis()
        //摧毁之前线程
        loopThread?.interrupt()
        loopThread = Thread {
            //当统计次数大于最大统计次数，或者执行时间大于最大时间，立即停止循环
            while (!Thread.interrupted()) {
                try {
                    count++
                    if (count > maxNumber) break
                    if (System.currentTimeMillis() - startTime > maxMillisecond) break
                    listener.invoke()
                    Thread.sleep(intervalTime)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    break
                } catch (e: Exception) {
                    e.printStackTrace()
                    Thread.sleep(intervalTime)
                    continue
                }
            }
        }
        loopThread?.start()
    }

    /**
     * 循环执行（UI线程），执行完毕后休息指定时间后继续
     * @param intervalTime 间隔时间
     * @param maxNumber 最多执行多少次
     * @param maxMillisecond 最多执行多少毫秒
     * @param listener 执行回调监听
     */
    @Synchronized
    fun loopUI(intervalTime: Long, maxNumber: Long = Long.MAX_VALUE, maxMillisecond: Long = Long.MAX_VALUE, listener: () -> Unit) {
        loopIO(intervalTime, maxNumber, maxMillisecond) { YThread.ui { listener.invoke() } }
    }

    /**
     * 停止循环执行
     */
    fun stop() {
        loopThread?.interrupt()
        loopThread = null
    }

    companion object {
        /**
         * 同步执行，执行完毕后休息指定时间后继续,直到获取到返回值或最大执行次数或超时
         * @param intervalTime 间隔时间
         * @param maxNumber 最多执行多少次
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
            //当统计次数大于最大统计次数，或者执行时间大于最大时间，或者监听返回不为null，立即停止循环
            while (count <= maxNumber || System.currentTimeMillis() - startTime <= maxMillisecond) {
                try {
                    count++
                    obj = listener.invoke()
                    if (obj != null) break
                    Thread.sleep(intervalTime)
                } catch (e: InterruptedException) {
                    break
                } catch (e: Exception) {
                    e.printStackTrace()
                    Thread.sleep(intervalTime)
                    continue
                }
            }
            return obj
        }
    }
}

