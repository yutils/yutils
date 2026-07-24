package com.yujing.test.cases

import com.yujing.socket.YSocketSync
import com.yujing.socket.YTcp
import com.yujing.socket.YUdp
import com.yujing.test.suite.AutoTestCase
import com.yujing.test.suite.TestCategory
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.ServerSocket
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

object SocketCases {
    private val portSeq = AtomicInteger(39000)

    fun all(): List<AutoTestCase> = listOf(
        AutoTestCase("net.tcp.loopback", "YTcp 本地回环收发", TestCategory.NETWORK) {
            val port = portSeq.getAndIncrement()
            val expected = "TCP_PING".toByteArray()
            val server = ServerSocket(port)
            val pool = Executors.newSingleThreadExecutor()
            try {
                pool.execute {
                    server.accept().use { client ->
                        val input = BufferedInputStream(client.getInputStream())
                        val buf = ByteArray(256)
                        val n = input.read(buf)
                        val out = BufferedOutputStream(client.getOutputStream())
                        out.write(buf, 0, n.coerceAtLeast(0))
                        out.flush()
                    }
                }
                Thread.sleep(80)
                val resp = YTcp.send("127.0.0.1", port, expected, 3000)
                require(resp != null && resp.contentEquals(expected)) {
                    "TCP 回环失败 resp=${resp?.contentToString()}"
                }
            } finally {
                runCatching { server.close() }
                pool.shutdownNow()
                pool.awaitTermination(1, TimeUnit.SECONDS)
            }
        },
        AutoTestCase("net.udp.loopback", "YUdp 本地回环收发", TestCategory.NETWORK) {
            val port = portSeq.getAndIncrement()
            val expected = "UDP_PING".toByteArray()
            val server = DatagramSocket(port)
            val pool = Executors.newSingleThreadExecutor()
            try {
                pool.execute {
                    val buf = ByteArray(256)
                    val packet = DatagramPacket(buf, buf.size)
                    server.soTimeout = 3000
                    server.receive(packet)
                    val echo = DatagramPacket(
                        packet.data, packet.offset, packet.length,
                        packet.address, packet.port
                    )
                    server.send(echo)
                }
                Thread.sleep(80)
                val resp = YUdp.sendSync("127.0.0.1", port, expected, 256, 3000)
                require(resp.contentEquals(expected)) {
                    "UDP 回环失败 resp=${resp.contentToString()}"
                }
            } finally {
                runCatching { server.close() }
                pool.shutdownNow()
                pool.awaitTermination(1, TimeUnit.SECONDS)
            }
        },
        AutoTestCase("net.socketSync.loopback", "YSocketSync 本地回环", TestCategory.NETWORK) {
            val port = portSeq.getAndIncrement()
            val expected = "SYNC_PING".toByteArray()
            val server = ServerSocket(port)
            val pool = Executors.newSingleThreadExecutor()
            val client = YSocketSync("127.0.0.1", port)
            try {
                pool.execute {
                    try {
                        server.soTimeout = 5000
                        server.accept().use { sock ->
                            sock.soTimeout = 5000
                            val input = BufferedInputStream(sock.getInputStream())
                            val buf = ByteArray(256)
                            val n = input.read(buf)
                            if (n > 0) {
                                val out = BufferedOutputStream(sock.getOutputStream())
                                out.write(buf, 0, n)
                                out.flush()
                            }
                        }
                    } catch (_: Exception) {
                    }
                }
                Thread.sleep(100)
                client.start()
                Thread.sleep(300)
                val resp = client.send(expected, 2000)
                require(resp != null && resp.contentEquals(expected)) {
                    "SocketSync 失败 resp=${resp?.contentToString()}"
                }
            } finally {
                runCatching { client.exit() }
                runCatching { server.close() }
                pool.shutdownNow()
                pool.awaitTermination(1, TimeUnit.SECONDS)
            }
        },
    )
}
