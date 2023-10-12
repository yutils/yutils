package com.yujing.socket

import com.yujing.utils.YLog
import com.yujing.utils.YReadInputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress

/**
 * tcp同步收发，发送后及时断开socket
 *
 * 保持连接，异步请求参照：YSocket
 * 保持连接，同步请求参照：YSocketSync
 */
object YTcp {
    var showLog = false //是否显示log

    /**
     * 连接并发送数据
     * 举例：
     * val receive = YTcp.connectAndSend(ip, port, timeOut) { socket ->
     *   socket.outputStream.write(data)
     *   socket.outputStream.flush()
     *   return@connectAndSend YReadInputStream.readOnce(socket.inputStream, timeOut.toLong())
     * }
     */
    @JvmStatic
    @Throws(java.net.SocketTimeoutException::class, Exception::class)
    fun connectAndSend(ip: String, port: Int, timeOut: Int = 5000, handler: (Socket) -> ByteArray?): ByteArray? {
        val socket = Socket()
        try {
            val socAddress: SocketAddress = InetSocketAddress(ip, port) // 连接
            socket.connect(socAddress, timeOut)
            if (showLog) YLog.i("连接成功... (${ip}:${port})")
            return handler.invoke(socket)
        } finally {
            try {
                socket.shutdownInput()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                socket.shutdownOutput()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                socket.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 发送并等待数据，最多等timeOut时间
     * 举例：
     * val receive = YTcp.send(ip, port, yb.bytes, 5000)
     */
    @JvmStatic
    @Throws(java.net.SocketTimeoutException::class, Exception::class)
    fun send(ip: String, port: Int, data: ByteArray, timeOut: Int = 5000): ByteArray? {
        return connectAndSend(ip, port, timeOut) { socket ->
            socket.outputStream.write(data)
            socket.outputStream.flush()
            return@connectAndSend YReadInputStream.readOnce(socket.inputStream, timeOut.toLong())
        }
    }


    /**
     * 发送并等待数据，一直不停组包，每次组包时间maxGroupTime，如果一直有数据总时间不超过timeOut
     */
    @JvmStatic
    @Throws(java.net.SocketTimeoutException::class, Exception::class)
    fun sendReadTime(ip: String, port: Int, data: ByteArray, maxGroupTime: Int = 100, timeOut: Int = 5000): ByteArray? {
        return connectAndSend(ip, port, timeOut) { socket ->
            socket.outputStream.write(data)
            socket.outputStream.flush()
            return@connectAndSend YReadInputStream.readTime(socket.inputStream, maxGroupTime, timeOut).bytes
        }
    }

    /**
     * 发送并等待数据，一直不停组包，但是期间读取长度达到minReadLength，立即返回。总时间不超过timeOut
     */
    @JvmStatic
    @Throws(java.net.SocketTimeoutException::class, Exception::class)
    fun sendReadLength(ip: String, port: Int, data: ByteArray, minLength: Int, timeOut: Int = 5000): ByteArray? {
        return connectAndSend(ip, port, timeOut) { socket ->
            socket.outputStream.write(data)
            socket.outputStream.flush()
            return@connectAndSend YReadInputStream.readLength(socket.inputStream, minLength, timeOut).bytes
        }
    }
}