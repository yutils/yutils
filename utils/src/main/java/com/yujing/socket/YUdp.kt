package com.yujing.socket

import com.yujing.bus.YBusUtil
import com.yujing.contract.YListener1
import com.yujing.utils.YConvert
import com.yujing.utils.YLog
import com.yujing.utils.YThread
import java.net.*

/**
 * Udp通信
 * @author 余静 2021年3月26日14:31:19
 */
/*用法
var yUdp: YUdp? = null
yUdp= YUdp("192.168.6.159",8080)
yUdp?.start()

//发送数据
yUdp?.send(data)

//接收数据
yUdp?.readListener= YListener1<ByteArray> {
    textView1.text=YConvert.bytesToHexString(it)
}

//或者
@YBus(YUdp.UdpReceive)
fun receive(value: ByteArray) {
    textView1.text=YConvert.bytesToHexString(value)
}

override fun onDestroy() {
    yUdp?.onDestroy()
    super.onDestroy()
}
*/
class YUdp(var ip: String, var port: Int) {
    //一次最多读取多长的数据
    var readMaxLength: Int = 1024

    //超时时间
    var soTimeout = 1000 * 5

    //超时是否重新连接
    var reconnect = true

    //Socket
    var datagramSocket: DatagramSocket? = null

    //读取成功监听
    var readListener: YListener1<ByteArray>? = null

    //YBus-Tag
    var tag = defaultTag

    //读取线程
    private var readThread: Thread? = null

    fun start() = send(ByteArray(0))

    fun reStart() = start()

    fun reStart(ip: String, port: Int) {
        this.ip = ip
        this.port = port
        start()
    }

    //同步发送
    fun send(data: ByteArray) {
        Thread {
            try {
                onDestroy()
                datagramSocket = DatagramSocket()
                if (showLog) YLog.i("UDP发送数据", YConvert.bytesToHexString(data))
                datagramSocket?.send(
                    DatagramPacket(data, data.size, InetAddress.getByName(ip), port)
                )
                read()
            } catch (e: SocketException) {
                if ("Socket is closed" == e.message) YLog.i("发送数据时Socket关闭")
            } catch (e: Exception) {
                if (showLog) YLog.e("发送数据时异常", e)
            }
        }.start()
    }

    //同步发送
    private fun read() {
        readThread?.interrupt()
        readThread = Thread {
            while (!Thread.interrupted()) {
                try {
                    // 接收服务器端响应的数据
                    // 1.创建数据报，用于接收服务器端响应的数据
                    val tempRead = ByteArray(readMaxLength)
                    val datagramPacketRead = DatagramPacket(tempRead, tempRead.size)
                    datagramSocket?.soTimeout = soTimeout
                    datagramSocket?.receive(datagramPacketRead)
                    if (Thread.interrupted()) break
                    //2.取出数据
                    val bytes = ByteArray(datagramPacketRead.length)
                    System.arraycopy(tempRead, 0, bytes, 0, datagramPacketRead.length)
                    if (showLog) YLog.i("UDP收到数据", YConvert.bytesToHexString(bytes))
                    YThread.runOnUiThread { readListener?.value(bytes) }
                    YBusUtil.post(tag, bytes)
                } catch (e: SocketException) {
                    if ("Socket closed" == e.message && showLog) YLog.i("读取数据时Socket关闭")
                    Thread.currentThread().interrupt()
                    break
                } catch (e: SocketTimeoutException) {
                    Thread.currentThread().interrupt()
                    if (showLog) YLog.e("读取超时", e.message)
                    if (reconnect) reStart()
                } catch (e: Exception) {
                    Thread.currentThread().interrupt()
                    if (showLog) YLog.e("读取数据时异常", e)
                    break
                }
            }
            if (showLog) YLog.d("退出读取线程")
        }
        readThread?.start()
    }

    fun onDestroy() {
        readThread?.interrupt()
        datagramSocket?.close()
    }

    companion object {
        const val defaultTag = "UdpReceiveDefaultTagTag"
        var showLog = false

        //同步发送
        fun sendSynchronized(
            data: ByteArray, ip: String, port: Int,
            timeout: Int = 2000, readMaxLength: Int = 1204
        ): ByteArray {
            //向服务器端发送数据
            // 1.定义服务器的地址、端口号、数据
            val address = InetAddress.getByName(ip)
            // 2.创建数据报，包含发送的数据信息
            val datagramPacketSend = DatagramPacket(data, data.size, address, port)
            // 3.创建DatagramSocket对象
            val datagramSocket = DatagramSocket()
            // 4.向服务器端发送数据报
            datagramSocket.soTimeout = timeout
            datagramSocket.send(datagramPacketSend)
            // 接收服务器端响应的数据
            // 1.创建数据报，用于接收服务器端响应的数据
            val tempRead = ByteArray(readMaxLength)
            val datagramPacketRead = DatagramPacket(tempRead, tempRead.size)
            // 2.接收服务器响应的数据
            datagramSocket.soTimeout = timeout
            datagramSocket.receive(datagramPacketRead)
            //3.取出数据
            val bytes = ByteArray(datagramPacketRead.length)
            System.arraycopy(tempRead, 0, bytes, 0, datagramPacketRead.length)
            // 4.关闭资源
            datagramSocket.close()
            return bytes
        }
    }
}