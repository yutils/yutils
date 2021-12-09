package com.yujing.utils

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.*
import com.yujing.contract.YListener1
import java.nio.ByteBuffer

/**
 * USB使用通用方法
 * 包含连接，打开，发送数据，读取数据
 * @author 余静 2021年4月29日11:39:38
 */
/*使用方法
//权限
<uses-permission android:name="android.permission.HARDWARE_TEST" />
//过滤所有你设备不支持的应用
<uses-feature android:name="android.hardware.usb.host" android:required="true"/>

//创建
private val yUsb = YUsb()

//usb监听
usb.setStatusListener{ status ->
    if (status) usb.open()
    else //USB 已断开
}

//初始化
yUsb.initUSB(4611,null)

//打开
if (!yUsb.open()) {
    speak("USB打开失败")
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
    lateinit var mUsbManager: UsbManager

    //注册广播
    private var mPermissionIntent: PendingIntent? = null

    //是否有USB权限
    var hasPermission = false

    //usb设备
    var device: UsbDevice? = null

    //Interface
    var usbInterface: UsbInterface? = null

    //point，写
    var usbEndpointOut: UsbEndpoint? = null

    //point，读
    var usbEndpointIn: UsbEndpoint? = null

    //是否找到USB
    var status = false

    //供应商ID
    var vendorId: Int? = null

    //设备ID
    var productId: Int? = null

    //连接
    var usbConnection: UsbDeviceConnection? = null

    //USB连接监听，如果是自己的USB，连接就返回true，断开就返回false
    private var statusListener: YListener1<Boolean>? = null

    fun setStatusListener(statusListener: YListener1<Boolean>) {
        this.statusListener = statusListener
    }

    //动态光比，提示用户是否授予使用USB设备的权限
    private val mUsbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            //权限
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE) as UsbDevice?
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) hasPermission = true
                    }
                }
            }
            //usb连接监听
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
                val device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE) as UsbDevice?
                device?.let {
                    //万一用户只用vendorId连接
                    if (it.vendorId == vendorId && productId == null) {
                        this@YUsb.device = device
                        status = true
                        statusListener?.value(true)
                    }
                    //万一用户只用productId连接
                    if (vendorId == null && it.productId == productId) {
                        this@YUsb.device = device
                        status = true
                        statusListener?.value(true)
                    }
                    //万一用户同时需要判断vendorId和productId
                    if (it.vendorId == vendorId && it.productId == productId) {
                        this@YUsb.device = device
                        status = true
                        statusListener?.value(true)
                    }
                }
            }
            //usb断开监听
            if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                val device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE) as UsbDevice?
                device?.let {
                    if (this@YUsb.device != null && this@YUsb.device == device) {
                        status = false
                        statusListener?.value(false)
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
        this.vendorId = vendorId
        this.productId = productId
        mUsbManager = YApp.get().getSystemService(Context.USB_SERVICE) as UsbManager
        //初始化广播
        mPermissionIntent =
            PendingIntent.getBroadcast(YApp.get(), 0, Intent(ACTION_USB_PERMISSION), 0)
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        YApp.get().registerReceiver(mUsbReceiver, filter)
        //查找USB设备
        return find(vendorId, productId)
    }

    //查找USB设备
    fun find(vendorId: Int?, productId: Int?): Boolean {
        val deviceList = mUsbManager.deviceList
        if (showLog && deviceList.size == 0) YLog.e("没有USB设备")
        val deviceIterator: Iterator<UsbDevice> = deviceList.values.iterator()
        var deviceTemp: UsbDevice
        while (deviceIterator.hasNext()) {
            deviceTemp = deviceIterator.next()
            if (vendorId == null && productId == null) break
            //万一用户只用vendorId连接
            if (deviceTemp.vendorId == vendorId && productId == null) {
                device = deviceTemp
                break
            }
            //万一用户只用productId连接
            if (vendorId == null && deviceTemp.productId == productId) {
                device = deviceTemp
                break
            }
            //万一用户同时需要判断vendorId和productId
            if ((deviceTemp.vendorId == vendorId) && deviceTemp.productId == productId) {
                device = deviceTemp
                break
            }
        }
        status = false
        statusListener?.value(false)
        if (device == null) return false
        val mPermissionIntent = PendingIntent.getBroadcast(
            YApp.get(), 0,
            Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_ONE_SHOT
        )
        mUsbManager.requestPermission(device, mPermissionIntent)
        status = true
        statusListener?.value(true)
        return true
    }

    /**
     * 打开USB连接
     * @return 成功返回true
     */
    fun open(): Boolean {
        if (!status) return false
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
        if (!status) return
        send(str.toByteArray())
    }

    /**
     * 发送数据，每次发送长度为4096，如果大于4096就拆包
     * @bytes 发送的数据
     */
    fun send(bytes: ByteArray) {
        if (!status) return
        val thread = Thread {
            sendSynchronization(bytes)
        }
        thread.name = "YUsb—send"
        thread.start()
    }

    /**
     * 同步发送数据，每次发送长度为4096，如果大于4096就拆包
     * @bytes 发送的数据
     */
    fun sendSynchronization(bytes: ByteArray): Boolean {
        if (!status) return false
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
        if (!status) return null
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
        if (!status) return null
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
    @Synchronized
    fun startRead(maxLength: Int, timeOut: Int, yListener1: YListener1<ByteArray>) {
        readThread?.interrupt()
        readThread = Thread {
            while (!Thread.interrupted()) {
                if (!status) {
                    try {
                        Thread.sleep(10)
                    } catch (e: Exception) {
                        readThread?.interrupt()
                    }
                    continue
                }
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
        readThread?.name="YUsb-read"
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
        status = false
        usbConnection?.releaseInterface(usbInterface)
        YApp.get().unregisterReceiver(mUsbReceiver)
    }
}