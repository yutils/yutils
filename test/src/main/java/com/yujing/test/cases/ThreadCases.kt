package com.yujing.test.cases

import com.yujing.test.suite.AutoTestCase
import com.yujing.test.suite.TestCategory
import com.yujing.utils.YAsync
import com.yujing.utils.YDelay
import com.yujing.utils.YEventCount
import com.yujing.utils.YLoop
import com.yujing.utils.YQueue
import com.yujing.utils.YReadInputStream
import com.yujing.utils.YRunOnceOfTime
import com.yujing.utils.YThread
import com.yujing.utils.YThreadPool
import com.yujing.utils.YTimer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

object ThreadCases {
    fun all(): List<AutoTestCase> = listOf(
        AutoTestCase("thread.isMain", "YThread.isMainThread 标识", TestCategory.THREAD) {
            // 本 block 在 Default 调度器跑，不应是主线程
            require(!YThread.isMainThread()) { "Default 线程被标成主线程" }
        },
        AutoTestCase("thread.runOnUi", "YThread.runOnUiThread 可达", TestCategory.THREAD) {
            val latch = CountDownLatch(1)
            val onMain = AtomicInteger(0)
            YThread.runOnUiThread {
                if (YThread.isMainThread()) onMain.set(1)
                latch.countDown()
            }
            require(latch.await(3, TimeUnit.SECONDS)) { "主线程回调超时" }
            require(onMain.get() == 1) { "未在主线程执行" }
        },
        AutoTestCase("thread.runOnce", "YRunOnceOfTime 节流", TestCategory.THREAD) {
            val tag = "test_run_once_${System.nanoTime()}"
            YRunOnceOfTime.remove(tag)
            val count = AtomicInteger(0)
            val latch = CountDownLatch(1)
            // run 会 post 到主线程，需等待回调后再断言
            YRunOnceOfTime.run(5000, tag) {
                count.incrementAndGet()
                latch.countDown()
            }
            YRunOnceOfTime.run(5000, tag) { count.incrementAndGet() }
            YRunOnceOfTime.run(5000, tag) { count.incrementAndGet() }
            require(latch.await(3, TimeUnit.SECONDS)) { "节流回调超时" }
            require(count.get() == 1) { "节流期内应只执行1次，实际=${count.get()}" }
            YRunOnceOfTime.remove(tag)
        },
        AutoTestCase("thread.delay.offMain", "YThread.delay 禁止主线程", TestCategory.THREAD) {
            // 在后台验证 delay 可调用
            YThread.delay(10)
            // 主线程应抛异常
            val latch = CountDownLatch(1)
            var threw = false
            YThread.runOnUiThread {
                try {
                    YThread.delay(1)
                } catch (_: IllegalStateException) {
                    threw = true
                } finally {
                    latch.countDown()
                }
            }
            require(latch.await(3, TimeUnit.SECONDS))
            require(threw) { "主线程 delay 应抛 IllegalStateException" }
        },
        AutoTestCase("thread.delay.runIO", "YDelay.runIO 延迟执行", TestCategory.THREAD) {
            val latch = CountDownLatch(1)
            val t0 = System.currentTimeMillis()
            YDelay.runIO(80) { latch.countDown() }
            require(latch.await(3, TimeUnit.SECONDS)) { "YDelay 超时" }
            require(System.currentTimeMillis() - t0 >= 60) { "延迟过短" }
        },
        AutoTestCase("thread.timer.sync", "YTimer.loopSync 次数", TestCategory.THREAD) {
            val n = AtomicInteger(0)
            YTimer.loopSync(30, 3) {
                n.incrementAndGet()
                null
            }
            require(n.get() == 3) { "应循环3次，实际=${n.get()}" }
        },
        AutoTestCase("thread.queue", "YQueue 串行执行", TestCategory.THREAD) {
            val q = YQueue()
            val latch = CountDownLatch(2)
            val order = StringBuilder()
            q.run(30) {
                order.append("A")
                latch.countDown()
            }
            q.run(30) {
                order.append("B")
                latch.countDown()
            }
            require(latch.await(5, TimeUnit.SECONDS)) { "YQueue 超时" }
            require(order.toString() == "AB") { "顺序=$order" }
            q.onDestroy()
        },
        AutoTestCase("thread.pool", "YThreadPool 提交任务", TestCategory.THREAD) {
            val latch = CountDownLatch(1)
            YThreadPool.getInstance().add { latch.countDown() }
            require(latch.await(3, TimeUnit.SECONDS)) { "线程池任务超时" }
        },
        AutoTestCase("thread.async", "YAsync submit/finish", TestCategory.THREAD) {
            val tag = "async_${System.nanoTime()}"
            Thread {
                Thread.sleep(80)
                YAsync.getInstance().finish(tag, "OK")
            }.start()
            val r = YAsync.getInstance().submit<String>(tag, 3000)
            require(r == "OK") { "结果=$r" }
        },
        AutoTestCase("thread.eventCount", "YEventCount 连点成功", TestCategory.THREAD) {
            val latch = CountDownLatch(1)
            val ec = YEventCount(2000, 3)
            ec.setEventSuccessListener { latch.countDown() }
            ec.event()
            ec.event()
            ec.event()
            require(latch.await(2, TimeUnit.SECONDS)) { "连点未触发成功" }
        },
        AutoTestCase("thread.runOnce.check", "YRunOnceOfTime.check 节流", TestCategory.THREAD) {
            val tag = "check_${System.nanoTime()}"
            YRunOnceOfTime.remove(tag)
            require(YRunOnceOfTime.check(2000, tag)) { "首次应可用" }
            require(!YRunOnceOfTime.check(2000, tag)) { "节流期内应不可用" }
            YRunOnceOfTime.remove(tag)
            require(YRunOnceOfTime.checkUpdate(50, tag))
            Thread.sleep(80)
            require(YRunOnceOfTime.checkUpdate(50, tag)) { "间隔后应可用" }
            YRunOnceOfTime.remove(tag)
        },
        AutoTestCase("thread.loop", "YLoop 有限次反射调用", TestCategory.THREAD) {
            val holder = object {
                val n = AtomicInteger(0)

                @Suppress("unused")
                fun tick() {
                    n.incrementAndGet()
                }
            }
            YLoop.start(holder, "tick", 40, 3)
            Thread.sleep(600)
            YLoop.stop(holder, "tick")
            require(holder.n.get() >= 3) { "循环次数异常=${holder.n.get()}" }
        },
        AutoTestCase("thread.readStream", "YReadInputStream.readOnce", TestCategory.THREAD) {
            val data = "stream-data".toByteArray()
            val bytes = YReadInputStream.readOnce(java.io.ByteArrayInputStream(data), 500)
            require(bytes.contentEquals(data)) { "读回=${bytes?.contentToString()}" }
        },
        AutoTestCase("thread.readStream.length", "YReadInputStream 长度/时间读", TestCategory.THREAD) {
            val data = ByteArray(32) { it.toByte() }
            val len = YReadInputStream.readLength(java.io.ByteArrayInputStream(data), 16, 1000)
            require(len != null && len.bytes != null && len.bytes.size >= 16) {
                "readLength size=${len?.bytes?.size}"
            }
            val timed = YReadInputStream.readTime(java.io.ByteArrayInputStream(data), 50, 1000)
            require(timed != null && timed.bytes != null && timed.bytes.isNotEmpty())
        },
        AutoTestCase("thread.timer.io", "YTimer.loopIO 有限次", TestCategory.THREAD) {
            val n = AtomicInteger(0)
            val timer = YTimer()
            timer.loopIO(30, 3) { n.incrementAndGet() }
            Thread.sleep(400)
            timer.stop()
            val after = n.get()
            Thread.sleep(120)
            require(n.get() in 3..6) { "loopIO count=${n.get()}" }
            require(n.get() == after) { "stop 后仍在增加" }
        },
    )
}
