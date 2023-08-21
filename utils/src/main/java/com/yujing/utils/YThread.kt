package com.yujing.utils

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.*

/**
 * 线程操作
 *
 * @author 余静 2022年4月26日16:06:04
 */
/*
用法:
//在主线程中运行
YThread.runOnUiThread { YLog.i("主线程") }

//在主线程中运行
YThread.ui { YLog.i("主线程") }

//在IO线程中运行
YThread.io { YLog.i("子线程") }

//统计当前有多少线程
YThread.countThread()

//获取全部线程
YThread.getAllThread()

//判断是否是在主线程（UI线程）
YThread.isMainThread()

//在主线程中运行,延迟2秒后
YThread.runOnUiThreadDelayed({ YLog.i("主线程") },2000)

//并行，最终耗时以最长耗时线程为准，阻塞
YThread.concurrent({
    Thread.sleep(2000)
    YLog.i("01")
}, {
    Thread.sleep(3000)
    YLog.i("02")
}, {
    Thread.sleep(5000)
    YLog.i("03")
})

//串行，顺序执行，阻塞
YThread.serial({
    Thread.sleep(2000)
    YLog.i("01")
}, {
    Thread.sleep(2000)
    YLog.i("02")
}, {
    Thread.sleep(2000)
    YLog.i("03")
})

//超时线程，执行成功返回true，超时返回false，异常返回null,阻塞
val a = YThread.timeOut(2000) {
    Thread.sleep(1500)
    YLog.i("01")
}
YLog.i("a= $a")
 */
object YThread {
    /**
     * 获取主线程handler
     */
    @JvmStatic
    val mainHandler: Handler = lazy { Handler(Looper.getMainLooper()) }.value

    /**
     * 返回线程是否为主线程。
     */
    @JvmStatic
    fun isMainThread(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) mainHandler.looper.isCurrentThread else Looper.getMainLooper().thread === Thread.currentThread()
    }

    /**
     * 主线程中运行
     */
    @JvmStatic
    fun runOnUiThread(runnable: Runnable) {
        if (isMainThread()) runnable.run() else mainHandler.post(runnable)
    }

    /**
     * 主线程中延时运行
     */
    @JvmStatic
    fun runOnUiThreadDelayed(runnable: Runnable, delayMillis: Long) {
        mainHandler.postDelayed(runnable, delayMillis)
    }

    /**
     * 移除还未运行的线程
     */
    @JvmStatic
    fun remove(runnable: Runnable) {
        mainHandler.removeCallbacks(runnable)
    }

    /**
     * 统计当前有多少线程
     *
     * @return 线程数量
     */
    @JvmStatic
    fun countThread(): Int {
        return Thread.getAllStackTraces().size
    }

    /**
     * 获取当前全部线程
     *
     * @return List<Thread>
    </Thread> */
    @JvmStatic
    val allThread: List<Thread>
        get() {
            val list: MutableList<Thread> = ArrayList()
            val allStackTraces = Thread.getAllStackTraces()
            for ((key) in allStackTraces) list.add(key)
            return list
        }

    /**
     * 打印全部线程信息
     *
     * @return 线程信息
     */
    @JvmStatic
    fun printAllThread(): String {
        val sb = StringBuilder()
        val allStackTraces = Thread.getAllStackTraces()
        for ((thread) in allStackTraces) sb.append("线程：").append(thread.name).append(",id=").append(thread.id).append(",state=").append(thread.state).append("\n")
        val str = sb.toString()
        YLog.i(str)
        return str
    }

    /**
     * 打印全部线程堆栈信息
     *
     * @return 线程信息
     */
    @JvmStatic
    fun printAllThreadStackTraces(): String {
        val sb = StringBuilder()
        val allStackTraces = Thread.getAllStackTraces()
        for ((thread, stack) in allStackTraces) {
            sb.append("线程：").append(thread.name).append(",id=").append(thread.id).append(",state=").append(thread.state).append("\n")
            for (stackTraceElement in stack) sb.append(stackTraceElement.toString()).append("\n")
        }
        val str = sb.toString()
        YLog.i(str)
        return str
    }

    /**
     * 并发，多个线程同时开始执行，直到全部执行完毕。阻塞方法
     */
    /*用法：
        YThread.concurrent({
            Thread.sleep(2000)
            YLog.i("01")
        }, {
            Thread.sleep(5000)
            YLog.i("02")
        })
        //最终耗时以最长的为准
     */
    @JvmStatic
    @Throws(InterruptedException::class)
    fun concurrent(vararg runs: Runnable) {
        if (runs.isEmpty()) return
        val countDownLatch = CountDownLatch(runs.size)
        for (runnable in runs) {
            val thread = Thread {
                try {
                    runnable.run()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                } finally {
                    countDownLatch.countDown()
                }
            }
            thread.start()
        }
        countDownLatch.await()
    }

    /**
     * 串行，多个线程按顺序执行，直到全部执行完毕。阻塞方法
     */
    /*
        用法：
        YThread.serial({
            Thread.sleep(2000)
            YLog.i("01")
        }, {
            Thread.sleep(2000)
            YLog.i("02")
        }, {
            Thread.sleep(2000)
            YLog.i("03")
        })
     */
    @JvmStatic
    @Throws(ExecutionException::class, InterruptedException::class)
    fun serial(vararg runs: Runnable) {
        if (runs.isEmpty()) return
        val executorService = Executors.newSingleThreadExecutor()
        for (runnable in runs) {
            val future = executorService.submit<Int> {
                try {
                    runnable.run()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                0
            }
            future.get() //等待执行完成，如果不写这句，该方法不会阻塞
        }
        executorService.shutdown()
    }


    /**
     * 带超时的同步执行，执行成功返回true，超时返回false，异常返回null,阻塞方法
     */
    /*用法:
        val a = YThread.timeOut(2000) {
            Thread.sleep(1500)
            YLog.i("01")
        }
        YLog.i("a= $a")
     */
    @JvmStatic
    fun timeOut(timeOut: Int, runnable: Runnable): Boolean? {
        val executorService = Executors.newSingleThreadExecutor()
        val future = executorService.submit<Boolean> {
            try {
                runnable.run()
            } catch (e: InterruptedException) {
                Log.i("timeOut", "InterruptedException")
            } catch (e: Exception) {
                e.printStackTrace()
                executorService.shutdownNow()
                return@submit null
            }
            true
        }
        return try {
            future[timeOut.toLong(), TimeUnit.MILLISECONDS]
        } catch (e: TimeoutException) {
            YLog.e("执行超时", 1)
            executorService.shutdownNow()
            false
        } catch (e: Exception) {
            e.printStackTrace()
            executorService.shutdownNow()
            null
        } finally {
            executorService.shutdown()
        }
    }

    /**
     * 主线程中运行,协程
     */
    @JvmStatic
    fun ui(runnable: Runnable) {
        CoroutineScope(Dispatchers.Main).launch { withContext(Dispatchers.Main) { runnable.run() } }
    }

    /**
     * 主线程中运行,协程
     */
    @JvmStatic
    fun ui(block: suspend CoroutineScope.() -> Unit) {
        CoroutineScope(Dispatchers.Main).launch { withContext(Dispatchers.Main, block) }
    }

    /**
     * 子线程中运行,协程
     */
    @JvmStatic
    fun io(runnable: Runnable) {
        CoroutineScope(Dispatchers.IO).launch { withContext(Dispatchers.IO) { runnable.run() } }
    }

    /**
     * 子线程中运行,协程，睡眠不要用Thread.sleep()，应该用delay()
     */
    @JvmStatic
    fun io(block: suspend CoroutineScope.() -> Unit) {
        CoroutineScope(Dispatchers.IO).launch { withContext(Dispatchers.IO, block) }
    }
    
    /**
     * delay延迟 柱塞线程
     */
    @JvmStatic
    fun delay(timeMillis: Long) {
        kotlinx.coroutines.runBlocking {
            kotlinx.coroutines.delay(if (timeMillis < 0) 0 else timeMillis)//延时timeMillis毫秒
        }
    }
}