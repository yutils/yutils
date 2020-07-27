package com.yujing.test

import android.content.Intent
import android.util.Log
import com.yujing.contract.YMessage
import com.yujing.crypt.YSha1
import com.yujing.url.YUrlAndroid
import com.yujing.url.contract.YUrlListener
import com.yujing.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : BaseActivity() {

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun init() {
        //var a=findViewById<Button>(R.id.button1)
        button1.text = "YSave写入"
        button1.setOnClickListener { IP = "123456" }
        button2.text = "YSave读取"
        button2.setOnClickListener { show(IP) }
        button3.text = "Date测试"
        button3.setOnClickListener { openDate() }
        button4.text = "对象复制"
        button4.setOnClickListener { copy() }
        button5.text = "安装APK"
        button5.setOnClickListener { install() }
        button6.text = "网络请求测试"
        button6.setOnClickListener { net2() }
        button7.setOnClickListener {
            try {
                YLog.d(
                    YUtils.shell(
                        "start adbd"
                    )
                )
            } catch (e: Exception) {

            }
        }
        button8.setOnClickListener {
            text4.text = """
                |签名SHA1：${YSha1.getSha1(YAppInfoUtils.getSign(applicationContext, packageName)[0].toByteArray())}
                |签名SHA1：${YAppInfoUtils.getSign(applicationContext, packageName, YAppInfoUtils.SHA1)}
                |签名MD5：${YAppInfoUtils.getSign(applicationContext, packageName, YAppInfoUtils.MD5)}
                |“哈哈”= ${YSha1.getSha1("哈哈".toByteArray())}
            """.trimMargin()
            YLog.d(text4.text.toString())
        }
        YPermissions.requestAll(this)
    }

    var yInstallApk: YInstallApk? = null
    private fun install() {
        yInstallApk = YInstallApk(this)
        yInstallApk?.install(YPath.getSDCard() + "/app.apk")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        yInstallApk?.onActivityResult(requestCode, resultCode, data)
    }

    private fun copy() {
        var user1: User = User("111", 123)
        var user2 = YUtils.copyObject(user1)
        Log.e("T", " " + user2.name)
        user2.name = "456789"
        Log.e("T", " " + user1.name)
        Log.e("T", " " + user2.name)
    }

    private fun openDate() {
        YDateDialog.setDefaultFullScreen(true)
        val yDateDialog = YDateDialog(this)
        yDateDialog.setFormat("yyyy年MM月dd日") // 设置日期格式（如："yyyy年MM月dd日HH:mm"）
        yDateDialog.initTime(Date()) //设置初始化日期，必须和设置格式相同（如："2016年07月01日15:19"）
        yDateDialog.isShowDay = true // 设置是否显示日滚轮,默认显示
        yDateDialog.isShowTime = false // 设置是否显示时间滚轮,默认显示
        yDateDialog.isShowMonth = true // 设置是否显示时间滚轮,默认显示
        yDateDialog.show { format: String?, calendar: Calendar?, date: Date?, yyyy: String?, MM: String?, dd: String?, HH: String?, mm: String? -> }
    }

    //ip
    var IP: String
        get() = YSave.get(App.get(), "IP", String::class.java, "127.0.0.1")
        set(ip) {
            //设置还可以这样 YSave.create(this)["IP",String::class.java,""]
            YSave.put(App.get(), "IP", ip)
        }

    private fun net2() {
        var url = "http://192.168.1.120:10007/api/SweepCode/JjdTwoDownload"
//         url = "http://www.baidu.com"
        var p =
            "{\"DeviceNo\":\"868403023178079\",\"BatchNum\":\"54511002\",\"Command\":112,\"MsgID\":1}"

        YUrlAndroid.create().post(url, p, object : YUrlListener {
            override fun success(bytes: ByteArray?, value: String?) {
                YLog.d("第一次测试", value)
            }

            override fun fail(value: String?) {

            }
        })
    }
}
