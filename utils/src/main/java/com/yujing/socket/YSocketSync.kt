package com.yujing.socket

import com.yujing.utils.YClass
import com.yujing.utils.YLog
import com.yujing.utils.YThread
import java.io.IOException
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.util.*
import java.util.concurrent.TimeoutException

/**
 * YSocketSync，套接字连接,同步请求
 * 1.启动时候，如果服务器没有启动或者检测不到服务器已经启动，就每3秒（默认）重新连接一次，连接成功后回调成功，没有连接成功回调失败。
 * 2.如果使用中途与服务器断开（如断网，服务器重启）立即回到等待连接状态，并且回调连接失败。每隔3秒（默认）重新连接一次服务器，如果此时再次连接上服务器立即回调连接成功。
 * 使用用于保持socket连接，和自动重新连接功能。 首先我们有2主要线程。
 * 心跳线程：当启动后每一定时间发送一条心跳信息，当心跳发送失败时把连接状态（connect）标记为失败，反之成功。
 * 连接线程：连接线程每一定时间根据（connect）检查一连接，如果连接断开就重新连接，更新socket，并通知连接状态。
 *
 * 发送消息采用同步模式，一问一答模式。
 *
 * @author 余静
 * @version 2022年5月31日14:12:56
 */
/*
使用方法：
Create.space(binding.wll)
var ySocketSync: YSocketSync? = null

//连接
ySocketSync?.exit()
ySocketSync = YSocketSync("192.168.1.21", 8888)
ySocketSync?.hearBytes = byteArrayOf(0)//心跳
ySocketSync?.readTimeOut = 1000 * 5L//读取超时
ySocketSync?.showLog = true
ySocketSync?.showSendLog = true
ySocketSync?.showReceiveLog = true
ySocketSync?.clearInputStream = true //发送前清空输入流
ySocketSync?.connectListeners?.add {
    YLog.i("连接状态", "连接${if (it) "成功" else "失败"}")
}
ySocketSync?.start()


//发送接收
Thread {
    val data = ySocketSync?.send(YConvert.hexStringToByte("02 52 44 53 01 ea 0d"))
    YThread.ui { textView1.text = "收到结果：" + YConvert.bytesToHexString(data) }
}.start()

//退出
ySocketSync?.exit()

*/
class YSocketSync(var ip: String?, var port: Int) {
    var socket: Socket? = null //当前socket
    var connectThread: ConnectThread? = null // 连接线程
    var heartbeat: HeartbeatThread? = null // 心跳线程
    var hearBytes = ByteArray(0) // 心跳包内容，如果设置了heartbeatContent，则使用heartbeatContent，否则使用默认的心跳包内容
    var urgentData = 0xFF // 紧急数据,发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
    var isNoHeartbeatSendUrgentData = true  // 没有设置心跳包时,发送紧急数据
    var connectListeners: MutableList<(Boolean) -> Unit> = ArrayList() // 连接监听
    var isConnect = false // 当前连接状态
    var heartTime = 1000 * 3L // 心跳间隔时间
    var checkConnectTime = 1000 * 3L // 检查连接时间
    var readTimeOut = 1000 * 30L // 每次读取最长时间，防止inputStream.available()卡死
    var showLog = true // 显示日志
    var showReceiveLog = false // 显示接收日志
    var showSendLog = false // 显示发送日志
    var clearInputStream = false // 发送前是否清除inputStream缓存
    var inputStreamReadListener: ((InputStream?) -> ByteArray)? = null // 读取InputStream接口，此接口一旦实现，则采用该接口返回数据
    var createSocketInterceptor: (() -> Socket)? = null // 创建Socket，此接口一旦实现，不会实例化Socket,采用该接口返回数据
    var heartbeatContent: (() -> ByteArray)? = null  // 心跳包发送内容监听

    /**
     * 开始，此方法只能调一次，用于启动心跳发送线程和连接线程，当连接线程连接成功后启动读取数据线程，当收到连接断开消息后，关闭读取消息线程。
     */
    fun start() {
        heartbeat = HeartbeatThread(this)
        heartbeat?.name = "YSocket-心跳线程"
        heartbeat?.start()
        connectThread = ConnectThread(this)
        connectThread?.connectListener = { success ->
            for (i in connectListeners.indices) backNotice(connectListeners[i], success)
        }
        connectThread?.name = "YSocket-连接线程"
        connectThread?.start()
    }

    /**
     * 心跳类，用于发送心跳包
     */
    class HeartbeatThread(var ySocketSync: YSocketSync) : Thread() {
        override fun run() {
            while (!isInterrupted) {
                try {
                    sleep(ySocketSync.heartTime)
                } catch (e: InterruptedException) {
                    interrupt()
                }
                if (ySocketSync.heartbeatContent == null) {
                    send(ySocketSync.hearBytes)
                } else {
                    val bytes = ySocketSync.heartbeatContent?.invoke()
                    send(bytes ?: ySocketSync.hearBytes)
                }
            }
            ySocketSync.printLog("退出心跳线程")
        }

        fun send(bytes: ByteArray?) {
            if (ySocketSync.socket == null) return
            try {
                if (bytes == null || bytes.isEmpty()) {
                    //如果开启了,没有设置心跳包时发送紧急数据
                    if (ySocketSync.isNoHeartbeatSendUrgentData) ySocketSync.socket?.sendUrgentData(ySocketSync.urgentData)
                    ySocketSync.isConnect = true
                    return
                }
                val os = ySocketSync.socket?.getOutputStream() // 获得输出流
                os?.write(bytes)
                os?.flush()
                ySocketSync.isConnect = true
            } catch (e: Exception) {
                ySocketSync.isConnect = false
            }
        }
    }

    /**
     * 连接类，保存连接，每3秒检查一次连接状态，如果断开
     */
    class ConnectThread(var ySocketSync: YSocketSync) : Thread() {
        // 连接监听
        var connectListener: ((Boolean) -> Unit)? = null

        override fun run() {
            // 保持连接
            while (!isInterrupted) {
                if (ySocketSync.socket == null || !ySocketSync.isConnect) {
                    try {
                        ySocketSync.socket = ySocketSync.createSocketInterceptor?.invoke() ?: Socket()
                        val socAddress: SocketAddress = InetSocketAddress(ySocketSync.ip, ySocketSync.port) // 连接
                        ySocketSync.socket?.connect(socAddress, 1000 * 5)
                        ySocketSync.socket?.keepAlive = true
                        ySocketSync.isConnect = true
                        ySocketSync.printLog("连接成功...")
                        ySocketSync.backNotice(connectListener, true)
                    } catch (e: Exception) {
                        ySocketSync.backNotice(connectListener, false)
                        ySocketSync.closeSocket()
                        ySocketSync.printLog("正在重新连接...")
                    }
                }
                try {
                    sleep(ySocketSync.checkConnectTime)
                } catch (es: Exception) {
                    interrupt()
                }
            }
            ySocketSync.printLog("退出保持连接线程")
        }
    }

    /**
     * 发送消息，死等结果
     */
    fun send(bytes: ByteArray?, readTimeOut: Long? = null): ByteArray? {
        //发送数据
        if (sendSync(bytes)) {
            //是否清除缓存
            if (clearInputStream) socket?.getInputStream()?.let { it.skip(it.available().toLong()) }
            //读取结果
            return readSync(readTimeOut ?: this.readTimeOut)
        }
        return null
    }

    /**
     * 发送消息byte[],同步
     *
     * @param bytes 消息byte[]
     * @return 是否发送成功
     */
    fun sendSync(bytes: ByteArray?): Boolean {
        // socket==null直接返回失败
        if (socket == null) return false
        // 判断消息为空直接丢弃
        return if (bytes == null || bytes.isEmpty()) false else try {
            val os = socket?.getOutputStream() // 获得输出流
            os?.write(bytes)
            os?.flush()
            if (showSendLog) printLog("发送:" + Arrays.toString(bytes))
            isConnect = true
            true
        } catch (e: Exception) {
            isConnect = false
            YLog.e("发送消息", e)
            false
        }
    }

    /**
     * 读取一次消息，同步
     */
    @Synchronized
    fun readSync(readTimeOut: Long): ByteArray? {
        try {
            val inputStream = socket?.getInputStream()
            val resultBytes = inputStream?.let { inputStreamToBytes(it, readTimeOut) }
            if (resultBytes == null || resultBytes.isEmpty()) {
                if (showReceiveLog) printLog("resultBytes.length==0")
                return resultBytes
            }
            if (showReceiveLog) printLog("收到:" + resultBytes.contentToString())
            isConnect = true
            return resultBytes
        } catch (e: TimeoutException) {
            YLog.e("读取消息", e)
        } catch (e: Exception) {
            YLog.e("读取消息", e)
            isConnect = false
        }
        return null
    }

    /**
     * 回调状态通知，
     * 回调数据，考虑到状态可能在短时间内多次变化回调，本地消息处理有一定时间，为了不卡住读取连接线程，因此这里单独开线程。又因为回调数据处理时可能引发异常，为了引起读取线程崩溃，因此进行异常捕获。
     */
    fun backNotice(stateListener: ((Boolean) -> Unit)?, status: Boolean) {
        if (stateListener == null) return
        Thread {
            if (YClass.isAndroid()) {
                YThread.ui { stateListener.invoke(status) }
            } else {
                stateListener.invoke(status)
            }
        }.start()
    }

    /**
     * 读取inputStream到byte数组中，在网络数据读取中inputStream.available()可能读取不到真是大小，因此采用如下循环方式读取inputStream数据长度。
     * inputStream.read(bytes);可能读不完inputStream中全部数据，所以采用循环方式读取数据。
     */
    @Throws(Exception::class)
    fun inputStreamToBytes(inputStream: InputStream, readTimeOut: Long): ByteArray {
        inputStreamReadListener?.let { return it.invoke(inputStream) }
        val startTime = System.currentTimeMillis()
        var count = 0
        while (count == 0 && System.currentTimeMillis() - startTime < readTimeOut) count = inputStream.available() //获取真正长度
        if (System.currentTimeMillis() - startTime >= readTimeOut) throw TimeoutException("读取超时")
        val bytes = ByteArray(count)
        // 一定要读取count个数据，如果inputStream.read(bytes);可能读不完
        var readCount = 0 // 已经成功读取的字节的个数
        while (readCount < count) readCount += inputStream.read(bytes, readCount, count - readCount)
        return bytes
    }

    /**
     * 打印日志
     */
    fun printLog(str: String?) {
        if (showLog) YLog.d(str, 1)
    }

    /**
     * 关闭Socket
     */
    fun closeSocket() {
        try {
            socket?.shutdownInput()
            socket?.shutdownOutput()
            socket?.close()
        } catch (e: IOException) {
            printLog("closeSocket:" + e.message)
        }
        socket = null
    }

    /**
     * 退出
     */
    fun exit() {
        heartbeat?.interrupt()
        connectThread?.interrupt()
        for (i in connectListeners.indices) backNotice(connectListeners[i], false)
        connectListeners.clear()
        closeSocket()
    }
}