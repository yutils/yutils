package com.yujing.socket;

import android.os.Handler;
import android.util.Log;

import java.util.List;

/**
 * YSocket，套接字连接
 * 1.启动时候，如果服务器没有启动或者检测不到服务器已经启动，就每3秒（默认）重新连接一次，连接成功后回调成功，没有连接成功回调失败。
 * 2.如果使用中途与服务器断开（如断网，服务器重启）立即回到等待连接状态，并且回调连接失败。每隔3秒（默认）重新连接一次服务器，如果此时再次连接上服务器立即回调连接成功。
 * 使用用于保持socket连接，和自动重新连接功能。 首先我们有3主要线程。
 * 读取消息线程：用于读取次socket消息，当socket断开后此线程应该及时关闭，当socket重新连接后，此线程应该重新创建以便于读取新的线程中的数据。
 * 心跳线程：当启动后每一定时间发送一条心跳信息，当心跳发送失败时把连接状态（connect）标记为失败，反之成功。
 * 连接线程：连接线程每一定时间根据（connect）检查一连接，如果连接断开就重新连接，更新socket，并通知连接状态。
 *
 * @author YuJing
 * @version 1.2 2020年5月18日13:51:04
 */
/*
    使用方法
      //获取心跳包
    private fun getHearBytes(): ByteArray? {
        val heartMap: MutableMap<String, Any> = HashMap()
        heartMap["Command"] = 0
        heartMap["DeviceNo"] = YUtils.getAndroidId(App.get())
        val heartMessage = Message(0)
        heartMessage.data = Gson().toJson(heartMap)
        Log.i("心跳包内容：", heartMessage.data)
        return heartMessage.getyBytes()
    }
    //把 inputStream 转换成byte[]
    var inputStreamReadListener= InputStreamReadListener { inputStream ->
        //读取协议头
        val count = 10
        val bytes1 = ByteArray(count)
        // 一定要读取count个数据，如果inputStream.read(bytes);可能读不完
        var readCount = 0 // 已经成功读取的字节的个数
        while (readCount < count) {
            readCount += inputStream.read(bytes1, readCount, count - readCount)
        }
        if (bytes1[0] != 0x5A.toByte()) {
            return@InputStreamReadListener null
        }
        //读取正文
        val length = YConvertBytes.bytesToInt(bytes1, 6)
        val bytes2 = ByteArray(length)
        var readContent = 0 // 已经成功读取的字节的个数
        while (readContent < length) {
            readContent += inputStream.read(bytes2, readContent, length - readContent)
        }
        //组装
        val bytes3 = ByteArray(bytes1.size + bytes2.size)
        System.arraycopy(bytes1, 0, bytes3, 0, bytes1.size)
        System.arraycopy(bytes2, 0, bytes3, bytes1.size, bytes2.size)
        bytes3
    }

    //连接监听
    var stateListener = YSocket.StateListener { isSuccess: Boolean ->
        println("连接状态：$isSuccess")
        RxBus.getDefault().post(RxBusMessage(Constants.事件_网络连接状态, isSuccess))
    }

    //消息监听
    var dataListener = YSocket.DataListener { bytes: ByteArray ->
        //获取消息
        val message = Message(bytes)
        YLog.e("消息", message.resultData)
        RxBus.getDefault().post(RxBusMessage(Constants.事件_接收到新的消息, message.resultData))
    }

    //start或者reStart
    fun start() {
            //创建实例
            YSocketAndroid.getInstance(Constants.IP, Constants.PORT.toInt())
            //断开已有的连接
            YSocketAndroid.getInstance().closeConnect()
            //设置心跳内容
            YSocketAndroid.getInstance().setHearBytes(getHearBytes())
            //清空状态连接监听
            YSocketAndroid.getInstance().clearConnectListener()
            //清空返回消息监听
            YSocketAndroid.getInstance().clearDataListener()
            //设置读取方法
            YSocketAndroid.getInstance().setInputStreamReadListener(inputStreamReadListener)
            //添加状态连接监听
            YSocketAndroid.getInstance().addConnectListener(stateListener)
            //添加返回消息监听
            YSocketAndroid.getInstance().addDataListener(dataListener)
            //不显示日志
            YSocketAndroid.getInstance().setShowLog(false)
            //开始运行
            YSocketAndroid.getInstance().start()
            }

            fun send(json:String) {
            val heartMessage = Message(0)
            heartMessage.data = json
            YSocketAndroid.getInstance().send(heartMessage.getyBytes(),null)
            }

            fun onDestroy() {
            //退出APP
            YSocketAndroid.getInstance().exit()
            }
 */
public class YSocketAndroid extends YSocket {
    Handler handler = new Handler();

    private static YSocketAndroid instance;

    /**
     * 单例模式，调用此方法前必须先调用getInstance(String ip, int port)
     */
    public static synchronized YSocketAndroid getInstance() {
        if (instance == null) {
            synchronized (YSocketAndroid.class) {
                if (instance == null) {
                    instance = new YSocketAndroid(null, 0);
                }
            }
        }
        return instance;
    }

    /**
     * 单例模式
     */
    public static YSocketAndroid getInstance(String ip, int port) {
        if (instance == null) {
            synchronized (YSocketAndroid.class) {
                if (instance == null) {
                    instance = new YSocketAndroid(ip, port);
                }
            }
        }
        instance.setIp(ip);
        instance.setPort(port);
        return instance;
    }

    /**
     * 构造函数
     *
     * @param ip   服务器IP地址
     * @param port
     */
    public YSocketAndroid(String ip, int port) {
        super(ip, port);
    }

    @Override
    protected void backData(final List<DataListener> dataListeners, final byte[] bytes) {
        if (dataListeners != null) {

            //回到UI线程
            handler.post(() -> {
                try {
                    for (DataListener dataListener : dataListeners) {
                        if (dataListener != null)
                            dataListener.data(bytes);
                    }
                } catch (Exception e) {
                    printLog("错误：" + e.getMessage());
                    Log.e("DataListener：", "错误", e);
                }
            });
        }
    }

    @Override
    protected void backNotice(final StateListener stateListener, final boolean status) {
        if (stateListener != null) {
            //回到UI线程
            handler.post(() -> {
                try {
                    stateListener.isSuccess(status);
                } catch (Exception e) {
                    printLog("错误：" + e.getMessage());
                    Log.e("StateListener：", "错误", e);
                }
            });
        }
    }

}
