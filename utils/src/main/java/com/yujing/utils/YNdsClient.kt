package com.yujing.utils

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import androidx.appcompat.app.AppCompatActivity
import com.yujing.contract.YListener1
import com.yujing.contract.YListener2

/**
 * 网络服务发现 客户端
 * @author yujing 2022年4月13日14:18:19
 */
/*
用法：

//自动发现并连接网络服务,可以设置超时时间
NSDClient.auto(timeOut = 5000) { success, nsdServiceInfo ->
    if (success) {
        val name = nsdServiceInfo!!.attributes["name"]
        val s = """
            ip=${nsdServiceInfo.host?.hostAddress}
            port=${nsdServiceInfo.port}
            name:${if (name == null) "null" else String(name)}
        """.trimIndent()
        YToast.show("连接成功"+s)
    } else {
        tv.text = "连接失败"
        YToast.show("连接失败")
    }
}


//发现网络服务
private var nsdServiceInfo: NsdServiceInfo? = null
NSDClient.discoverService("_ipfs-discovery._udp") { success, value ->
    if (success) {  nsdServiceInfo = value  }
}

//连接网络服务
if (nsdServiceInfo == null) return@button YToast.show("请先发现网络服务")
NSDClient.resolveService(nsdServiceInfo!!) { success, nsdServiceInfo ->
    if (success) {
        val name = nsdServiceInfo!!.attributes["name"]
        val s = """
            ip=${nsdServiceInfo.host?.hostAddress}
            port=${nsdServiceInfo.port}
            name:${if (name == null) "null" else String(name)}
        """.trimIndent()
        YToast.show("连接成功"+s)
    } else {
        tv.text = "连接失败"
        YToast.show("连接失败")
    }
}

//关闭网络发现
NSDClient.stopServiceDiscovery()

 */
object YNdsClient {
    /**
     * 发现网络服务的监听器
     */
    private var nsDicListener: NsdManager.DiscoveryListener? = null

    /**
     * 发现网络服务
     */
    @JvmStatic
    fun discoverService(serviceType: String = "_nsdchat._tcp", listener: YListener2<Boolean, NsdServiceInfo?>? = null) {
        if (nsDicListener != null) return YLog.e("正在发现服务")
        nsDicListener = object : NsdManager.DiscoveryListener {
            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                YLog.e("停止（网络服务发现）失败")
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                YLog.e("启动（网络服务发现）失败")
                YThread.runOnUiThread { listener?.value(false, null) }
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                YLog.i("服务丢失：" + serviceInfo.serviceName)
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                // 发现网络服务时就会触发该事件
                YLog.i("找到（网络服务）  " + serviceInfo.serviceName + " " + serviceInfo.serviceType)
                YThread.runOnUiThread { listener?.value(true, serviceInfo) }
            }

            override fun onDiscoveryStopped(serviceType: String) {
                YLog.i("（网络服务发现）停止中")
            }

            override fun onDiscoveryStarted(serviceType: String) {
                YLog.i("（网络服务发现）启动中")
            }
        }
        val nsdManager = YApp.get().getSystemService(Context.NSD_SERVICE) as NsdManager
        nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, nsDicListener)
    }

    /**
     * 发现网络服务,并给定超时时间，最后返回改时间内的所有结果
     */
    @JvmStatic
    fun discoverService(serviceType: String = "_nsdchat._tcp", timeOut: Int? = null, listenerList: YListener1<MutableList<NsdServiceInfo>>? = null) {
        if (nsDicListener != null) return YLog.e("正在发现服务")

        val list = mutableListOf<NsdServiceInfo>()
        val runnable = Runnable {
            listenerList?.value(list)
            stopServiceDiscovery()
        }
        //timeOut时间后返回结果
        timeOut?.let { YDelay.run(timeOut, runnable) }

        nsDicListener = object : NsdManager.DiscoveryListener {
            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                YLog.e("停止（网络服务发现）失败")
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                YLog.e("启动（网络服务发现）失败")
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                YLog.i("服务丢失：" + serviceInfo.serviceName)
                list.remove(serviceInfo)
                //list根据serviceName去重
                for (i in list.size - 1 downTo 0) {
                    if (list[i].serviceName == serviceInfo.serviceName && list[i].serviceType == serviceInfo.serviceType && list[i].port == serviceInfo.port) {
                        list.removeAt(i)
                    }
                }
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                // 发现网络服务时就会触发该事件
                YLog.i("找到（网络服务）  " + serviceInfo.serviceName + " " + serviceInfo.serviceType)
                var isExist = false
                for (i in list.size - 1 downTo 0) {
                    if (list[i].serviceName == serviceInfo.serviceName && list[i].serviceType == serviceInfo.serviceType && list[i].port == serviceInfo.port) {
                        isExist = true
                        break
                    }
                }
                if (!isExist) {
                    list.add(serviceInfo)
                }
            }

            override fun onDiscoveryStopped(serviceType: String) {
                YLog.i("（网络服务发现）停止中")
            }

            override fun onDiscoveryStarted(serviceType: String) {
                YLog.i("（网络服务发现）启动中")
            }
        }
        val nsdManager = YApp.get().getSystemService(Context.NSD_SERVICE) as NsdManager
        nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, nsDicListener)
    }

    /**
     * 连接网络服务
     */
    @JvmStatic
    fun resolveService(serviceInfo: NsdServiceInfo, listener: YListener2<Boolean, NsdServiceInfo?>? = null) {
        val nsResolveListener = object : NsdManager.ResolveListener {
            override fun onServiceResolved(nsdServiceInfo: NsdServiceInfo) {
                // 可以再这里获取相应网络服务的地址及端口信息，然后决定是否要与之建立连接。
                val s = """
                        ip=${nsdServiceInfo.host?.hostAddress}
                        port=${nsdServiceInfo.port}
                    """.trimIndent()
                YLog.i("服务连接成功：\n$s")
                YThread.runOnUiThread { listener?.value(true, nsdServiceInfo) }
            }

            override fun onResolveFailed(nsdServiceInfo: NsdServiceInfo, arg1: Int) {
                YLog.e("服务连接失败")
                YThread.runOnUiThread { listener?.value(false, nsdServiceInfo) }
            }
        }

        val nsdManager = YApp.get().getSystemService(Context.NSD_SERVICE) as NsdManager
        nsdManager.resolveService(serviceInfo, nsResolveListener)
    }

    /**
     * 关闭网络发现
     */
    @JvmStatic
    fun stopServiceDiscovery() {
        if (nsDicListener == null) return YLog.i("请先发现网络服务")
        try {
            val nsdManager = YApp.get().getSystemService(AppCompatActivity.NSD_SERVICE) as NsdManager
            nsdManager.stopServiceDiscovery(nsDicListener) // 关闭网络发现
            YLog.i("关闭网络发现成功")
        } catch (e: Exception) {
            YLog.e("关闭网络发现失败", e.message)
        }
        nsDicListener = null
    }

    /**
     * 自动发现并连接网络服务，可以设置超时时间
     */
    @JvmStatic
    fun auto(serviceType: String = "_ipfs-discovery._udp", timeOut: Int? = null, listener: (Boolean, NsdServiceInfo?) -> Unit) {
        var isConnect = false
        val startTime = System.currentTimeMillis()
        val runnable = Runnable {
            listener(false, null)
            stopServiceDiscovery()
        }
        //timeOut时间内没连接上就返回失败
        timeOut?.let { YDelay.run(timeOut, runnable) }

        //发现网络服务
        discoverService(serviceType) { success, value ->
            //关闭网络发现
            stopServiceDiscovery()
            //超时后，发现不再处理
            if (timeOut != null && System.currentTimeMillis() - startTime > timeOut) {
                YLog.e("自动连接网络服务超时")
                return@discoverService
            }
            if (success) {
                if (!isConnect) {
                    isConnect = true
                    //连接网络服务
                    resolveService(value!!) { success, value ->
                        //超时后，连接成功不再处理
                        if (timeOut != null && System.currentTimeMillis() - startTime > timeOut) {
                            YLog.e("自动连接网络服务超时")
                            return@resolveService
                        }
                        listener(success, value)
                        //移除计时
                        YDelay.remove(runnable)
                    }
                }
            } else {
                listener(success, value)
                //移除计时
                YDelay.remove(runnable)
            }
        }
    }
}