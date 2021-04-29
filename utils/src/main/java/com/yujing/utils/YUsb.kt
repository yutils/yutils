package com.yujing.utils

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.*
import android.os.Parcelable
import com.yujing.contract.YListener1
import java.nio.ByteBuffer

/**
 * USB使用通用方法
 * 包含连接，打开，发送数据，读取数据
 * @author yujing 2021年4月29日11:39:38
 */
/*使用方法
//权限
<uses-permission android:name="android.permission.HARDWARE_TEST" />
//过滤所有你设备不支持的应用
<uses-feature android:name="android.hardware.usb.host" android:required="true"/>

//创建
private val yUsb = YUsb()

//初始化
yUsb.initUSB(4611,null)

//打开
if (!yUsb.open()) {
    speak("USB打开失败")
    return
}

//发送
yUsb.send(value.toByteArray(charset("GB18030")))
//读取数据
val result = read(maxLength, timeOut)
//读取数据,直到有数据为止
val result =yUsb.readWait()

//持续读取监听，回调
yUsb.startRead {  result->  }
//停止持续读取监听
yUsb.stopRead()

//关闭
yUsb.close()

//退出释放
override fun onDestroy() {
    super.onDestroy()
    yUsb.onDestroy()
}
 */
class YUsb {
    companion object {
        var showLog = false

        //USB权限
        const val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
    }

    //USB管理
    private lateinit var mUsbManager: UsbManager

    //注册广播
    private var mPermissionIntent: PendingIntent? = null

    //是否有USB权限
    private var hasPermission = false

    //usb设备
    private var device: UsbDevice? = null

    //Interface
    var usbInterface: UsbInterface? = null

    //point，读或者写
    var usbEndpointOut: UsbEndpoint? = null
    var usbEndpointIn: UsbEndpoint? = null

    //连接
    var usbConnection: UsbDeviceConnection? = null

    //动态光比，提示用户是否授予使用USB设备的权限
    private val mUsbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val device =
                        intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            hasPermission = true
                        }
                    }
                }
            }
        }
    }

    /**
     * 初始化USB，可以只传一个值
     * @vendorId 供应商ID
     * @productId 设备ID
     */
    fun initUSB(vendorId: Int?, productId: Int?): Boolean {
        mUsbManager = YApp.get().getSystemService(Context.USB_SERVICE) as UsbManager
        mPermissionIntent =
            PendingIntent.getBroadcast(YApp.get(), 0, Intent(ACTION_USB_PERMISSION), 0)
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        YApp.get().registerReceiver(mUsbReceiver, filter)
        val deviceList = mUsbManager.deviceList
        if (showLog && deviceList.size == 0) YLog.e("没有USB设备")
        val deviceIterator: Iterator<UsbDevice> = deviceList.values.iterator()
        var deviceTemp: UsbDevice
        while (deviceIterator.hasNext()) {
            deviceTemp = deviceIterator.next()
            if (vendorId == null && productId == null) break
            if (deviceTemp.vendorId == vendorId && productId == null) {
                device = deviceTemp
                break
            }
            if (vendorId == null && deviceTemp.productId == productId) {
                device = deviceTemp
                break
            }
            if ((deviceTemp.vendorId == vendorId) && deviceTemp.productId == productId) {
                device = deviceTemp
                break
            }
        }
        if (device == null) return false
        val mPermissionIntent = PendingIntent.getBroadcast(
            YApp.get(), 0,
            Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_ONE_SHOT
        )
        mUsbManager.requestPermission(device, mPermissionIntent)
        return true
    }

    /**
     * 打开USB连接
     * @return 成功返回true
     */
    fun open(): Boolean {
        return open(mUsbManager, device)
    }

    private fun open(manager: UsbManager, device: UsbDevice?): Boolean {
        try {
            if (device == null) return false
            usbInterface = device.getInterface(0)
            if (usbInterface == null) return false
            usbEndpointOut = usbInterface?.getEndpoint(0)//写数据节点
            if (usbEndpointOut == null) return false
            usbEndpointIn = usbInterface?.getEndpoint(1)//读数据节点
            if (usbEndpointIn == null) return false
            usbConnection = manager.openDevice(device)
            if (usbConnection == null) return false
            return usbConnection!!.claimInterface(usbInterface, true)
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * 延迟关闭USB连接，休息sleep毫秒后关闭连接
     * @sleep 休息毫秒
     */
    fun close() {
        usbConnection?.close()
    }

    /**
     * 延迟关闭USB连接，休息sleep毫秒后关闭连接
     * @sleep 休息毫秒
     */
    fun close(sleep: Long) {
        Thread.sleep(sleep)
        usbConnection?.close()
    }

    /**
     * 发送字符串
     * @str 字符串
     */
    fun send(str: String) {
        send(str.toByteArray())
    }

    /**
     * 发送数据，每次发送长度为4096，如果大于4096就拆包
     * @bytes 发送的数据
     */
    fun send(bytes: ByteArray) {
        Thread {
            sendSynchronization(bytes)
        }.start()
    }

    /**
     * 同步发送数据，每次发送长度为4096，如果大于4096就拆包
     * @bytes 发送的数据
     */
    fun sendSynchronization(bytes: ByteArray): Boolean {
        try {
            val sendLength = 4096 //每次写入长度
            var count = 0 //统计已经发送长度
            var errorCount = 0 //统计已经发送长度
            while (true) {
                //剩余长度
                val sy = bytes.size - count
                //如果剩余长度小于等于0，说明发送完成
                if (sy <= 0) break
                //如果剩余长度大于每次写入长度，就写入对应长度，如果不大于就写入剩余长度
                val current = ByteArray(sy.coerceAtMost(sendLength))
                //数组copy
                System.arraycopy(bytes, count, current, 0, current.size)
                //写入
                val re =
                    usbConnection?.bulkTransfer(usbEndpointOut, current, current.size, 500)
                if (re == null || re < 0) {
                    errorCount++
                    if (showLog) YLog.e("错误", "错误码：$re")
                    if (errorCount >= 3) break
                } else {
                    count += re
                }
                //YLog.i("发送成功：$re  总共发送：$count")
            }
            return true
        } catch (e: Exception) {
            if (showLog) YLog.e("发送失败", e)
            return false
        }
    }

    /**
     * 读取一次数据
     * @maxLength  一次读取最大长度
     * @timeOut 超时时间
     * @return 最终读取数据的真实长度
     */
    fun read(maxLength: Int, timeOut: Int): ByteArray? {
        val current = ByteArray(maxLength)
        val re = usbConnection?.bulkTransfer(usbEndpointIn, current, current.size, timeOut)
        if (re == null || re < 0) return null
        val outByteArray = ByteArray(re)
        //数组copy
        System.arraycopy(current, 0, outByteArray, 0, re)
        return outByteArray
    }

    /**
     * 读取一次数据
     * @return 最终读取数据的真实长度
     */
    fun readWait(): ByteArray? {
        val inMax = usbEndpointIn?.maxPacketSize
        val byteBuffer = ByteBuffer.allocate(inMax!!)
        val usbRequest = UsbRequest()
        usbRequest.initialize(usbConnection, usbEndpointIn);
        usbRequest.queue(byteBuffer, inMax)
        if (usbConnection?.requestWait() == usbRequest) {
            return byteBuffer.array()
        }
        return null
    }

    /**
     * 启动读取线程。默认每次最长读取1024，没次读取3秒超时
     * @yListener1 每次回调长度是读取数据的真实长度
     */
    private var readThread: Thread? = null
    fun startRead(yListener1: YListener1<ByteArray>) {
        startRead(1024, 3000, yListener1)
    }

    /**
     * 启动读取线程。
     * @maxLength  一次读取最大长度
     * @timeOut 超时时间
     * @yListener1 每次回调长度是读取数据的真实长度
     */
    fun startRead(maxLength: Int, timeOut: Int, yListener1: YListener1<ByteArray>) {
        readThread?.interrupt()
        readThread = Thread {
            while (!Thread.interrupted()) {
                try {
                    // 接收服务器端响应的数据
                    val result = read(maxLength, timeOut) ?: continue
                    YThread.runOnUiThread { yListener1.value(result) }
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

    /**
     * 停止读取线程
     */
    fun stopRead() {
        readThread?.interrupt()
    }

    fun onDestroy() {
        stopRead()
        close()
        usbConnection?.releaseInterface(usbInterface)
        YApp.get().unregisterReceiver(mUsbReceiver)
    }
}