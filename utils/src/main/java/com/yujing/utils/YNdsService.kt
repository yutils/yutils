package com.yujing.utils

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import androidx.appcompat.app.AppCompatActivity
import com.yujing.contract.YListener1
import java.net.ServerSocket

/**
 * 网络服务发现 服务端
 * @author yujing 2022年4月13日14:18:41
 */
/*
用法：
//注册网络服务
NSDService.registerService() {
    //if (it) "注册成功" else "注册失败"
}

//注销网络服务
NSDService.unregisterService()
 */
object YNdsService {
    /**
     * 注册网络服务的监听器
     */
    private var nsRegListener: NsdManager.RegistrationListener? = null

    /**
     * 注册网络服务
     */
    fun registerService(serviceType: String = "_ipfs-discovery._udp", listener: YListener1<Boolean>? = null) {
        if (nsRegListener != null) return YLog.e("已经注册过了")
        var port = 9999
        try {
            val sock = ServerSocket(0)
            port = sock.localPort
            sock.close()
        } catch (e: Exception) {
            YLog.e("不能设置端口：${e.message}")
        }
        YLog.i("端口：$port")

        // 注册网络服务的名称、类型、端口
        val nsdServiceInfo = NsdServiceInfo()
        nsdServiceInfo.serviceName = "余静的服务"
        nsdServiceInfo.serviceType = serviceType
        nsdServiceInfo.port = port

        nsdServiceInfo.setAttribute("name", "我是服务器")
        nsdServiceInfo.setAttribute("dpcode", "123456789")

        // 实现一个网络服务的注册事件监听器，监听器的对象应该保存起来以便之后进行注销
        nsRegListener = object : NsdManager.RegistrationListener {
            override fun onUnregistrationFailed(arg0: NsdServiceInfo, arg1: Int) {
                YLog.e("注销（网络服务）失败")
            }

            override fun onServiceUnregistered(arg0: NsdServiceInfo) {
                YLog.i("注销（网络服务）完成")
            }

            override fun onServiceRegistered(arg0: NsdServiceInfo) {
                YLog.i("（网络服务）注册完成")
                YThread.runOnUiThread { listener?.value(true) }
            }

            override fun onRegistrationFailed(arg0: NsdServiceInfo, arg1: Int) {
                YLog.e("（网络服务）注册失败")
                YThread.runOnUiThread { listener?.value(false) }
            }
        }

        // 获取系统网络服务管理器，准备之后进行注册
        val nsdManager = YApp.get().getSystemService(AppCompatActivity.NSD_SERVICE) as NsdManager
        nsdManager.registerService(nsdServiceInfo, NsdManager.PROTOCOL_DNS_SD, nsRegListener)
    }

    /**
     * 注销网络服务
     */
    fun unregisterService() {
        if (nsRegListener == null) return YLog.i("请先注册网络服务")
        try {
            val nsdManager = YApp.get().getSystemService(AppCompatActivity.NSD_SERVICE) as NsdManager
            nsdManager.unregisterService(nsRegListener) // 注销网络服务
            YLog.i("注销网络服务成功")
        } catch (e: Exception) {
            YLog.e("注销网络服务失败", e.message)
        }
        nsRegListener = null
    }
}