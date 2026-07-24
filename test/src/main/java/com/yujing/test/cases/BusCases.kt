package com.yujing.test.cases

import com.yujing.bus.YBus
import com.yujing.bus.YBusUtil
import com.yujing.test.suite.AutoTestCase
import com.yujing.test.suite.TestCategory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

object BusCases {
    fun all(): List<AutoTestCase> = listOf(
        AutoTestCase("bus.post.receive", "YBusUtil post/receive", TestCategory.BUS) {
            val tag = "test_bus_${System.nanoTime()}"
            val got = AtomicReference<String?>(null)
            val latch = CountDownLatch(1)
            val receiver = object {
                @YBus
                fun onMsg(key: Any?, message: Any?) {
                    if (key?.toString() == tag) {
                        got.set(message?.toString())
                        latch.countDown()
                    }
                }
            }
            YBusUtil.register(receiver)
            try {
                YBusUtil.post(tag, "hello_bus")
                require(latch.await(3, TimeUnit.SECONDS)) { "未收到 bus 消息" }
                require(got.get() == "hello_bus") { "收到=${got.get()}" }
            } finally {
                YBusUtil.unregister(receiver)
            }
        },
        AutoTestCase("bus.sticky", "YBusUtil sticky 补发", TestCategory.BUS) {
            val tag = "test_sticky_${System.nanoTime()}"
            YBusUtil.postSticky(tag, "sticky_value")
            val got = AtomicReference<String?>(null)
            val latch = CountDownLatch(1)
            val receiver = object {
                @YBus
                fun onMsg(key: Any?, message: Any?) {
                    if (key?.toString() == tag) {
                        got.set(message?.toString())
                        latch.countDown()
                    }
                }
            }
            try {
                YBusUtil.register(receiver)
                require(latch.await(3, TimeUnit.SECONDS)) { "sticky 未补发" }
                require(got.get() == "sticky_value") { "收到=${got.get()}" }
            } finally {
                YBusUtil.unregister(receiver)
                YBusUtil.removeSticky(tag)
            }
        },
        AutoTestCase("bus.removeSticky", "YBusUtil 移除 sticky", TestCategory.BUS) {
            val tag = "test_rm_sticky_${System.nanoTime()}"
            YBusUtil.postSticky(tag, "x")
            YBusUtil.removeSticky(tag)
            val got = AtomicReference(false)
            val receiver = object {
                @YBus
                fun onMsg(key: Any?, message: Any?) {
                    if (key?.toString() == tag) got.set(true)
                }
            }
            try {
                YBusUtil.register(receiver)
                Thread.sleep(200)
                require(!got.get()) { "removeSticky 后不应补发" }
            } finally {
                YBusUtil.unregister(receiver)
            }
        },
        AutoTestCase("bus.clearSticky", "YBusUtil 清空 sticky", TestCategory.BUS) {
            val tag = "test_clear_sticky_${System.nanoTime()}"
            YBusUtil.postSticky(tag, "will-clear")
            YBusUtil.clearSticky()
            val got = AtomicReference(false)
            val receiver = object {
                @YBus
                fun onMsg(key: Any?, message: Any?) {
                    if (key?.toString() == tag) got.set(true)
                }
            }
            try {
                YBusUtil.register(receiver)
                Thread.sleep(200)
                require(!got.get()) { "clearSticky 后不应补发" }
            } finally {
                YBusUtil.unregister(receiver)
            }
        },
        AutoTestCase("bus.postSerial", "YBusUtil 串行投递顺序", TestCategory.BUS) {
            val tag = "test_serial_${System.nanoTime()}"
            val order = java.util.concurrent.CopyOnWriteArrayList<Int>()
            val latch = CountDownLatch(3)
            val receiver = object {
                @YBus
                fun onMsg(key: Any?, message: Any?) {
                    if (key?.toString() == tag) {
                        order.add((message as? Number)?.toInt() ?: -1)
                        latch.countDown()
                    }
                }
            }
            YBusUtil.register(receiver)
            try {
                YBusUtil.postSerial(tag, 1)
                YBusUtil.postSerial(tag, 2)
                YBusUtil.postSerial(tag, 3)
                require(latch.await(3, TimeUnit.SECONDS)) { "串行消息未收齐" }
                require(order.toList() == listOf(1, 2, 3)) { "order=$order" }
            } finally {
                YBusUtil.unregister(receiver)
            }
        },
    )
}
