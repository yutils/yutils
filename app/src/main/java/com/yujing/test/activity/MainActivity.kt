package com.yujing.test.activity

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import com.yujing.crypt.YSha1
import com.yujing.test.App
import com.yujing.test.R
import com.yujing.test.base.BaseActivity
import com.yujing.test.utils.YVersionUpdate
import com.yujing.url.YUrlAndroid
import com.yujing.url.contract.YUrlListener
import com.yujing.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*


class MainActivity : BaseActivity() {
    private val yPicture: YPicture = YPicture()
    var bitmap: Bitmap? = null
    var uri: Uri? = null
    override val layoutId: Int
        get() = R.layout.activity_main

    override fun init() {
        //var a=findViewById<Button>(R.id.button1)
        button1.text = "拍照"
        button1.setOnClickListener { yPicture.gotoCamera(this) }
        button2.text = "相册"
        button2.setOnClickListener { yPicture.gotoAlbum(this) }
        button3.text = "剪切"
        button3.setOnClickListener { uri?.let { yPicture.gotoCrop(this, uri, 400, 400) } }
        button4.text = "Date测试"
        button4.setOnClickListener { openDate() }
        button5.text = "通知栏下载"
        button5.setOnClickListener { download() }
        button6.text = "App更新"
        button6.setOnClickListener { update() }
        button7.text = "网络请求测试"
        button7.setOnClickListener { net2() }
        button8.text = "签名测试"
        button8.setOnClickListener {
            text4.text = """
                |签名SHA1：${
                YSha1.getSha1(
                    YAppInfoUtils.getSign(
                        applicationContext,
                        packageName
                    )[0].toByteArray()
                )
            }
                |签名SHA1：${
                YAppInfoUtils.getSign(
                    applicationContext,
                    packageName,
                    YAppInfoUtils.SHA1
                )
            }
                |签名MD5：${YAppInfoUtils.getSign(applicationContext, packageName, YAppInfoUtils.MD5)}
                |“哈哈”= ${YSha1.getSha1("哈哈".toByteArray())}
            """.trimMargin()
            YLog.d(text4.text.toString())
        }

        yPicture.setPictureFromCameraListener { uri, file, Flag ->
            val bm = YConvert.uri2Bitmap(this, uri)
            this.bitmap = bitmap
            this.uri = uri
            YImageDialog.show(this, bitmap)
            YToast.show(this, "file:" + file.exists())
        }

        yPicture.setPictureFromCropListener { uri, file, Flag ->
            val bitmap = YConvert.uri2Bitmap(this, uri)
            this.bitmap = bitmap
            this.uri = uri
            YImageDialog.show(this, bitmap)
            YToast.show(this, "file:" + file.exists())
        }

        yPicture.setPictureFromAlbumListener { uri, file, Flag ->
            val bitmap = YConvert.uri2Bitmap(this, uri)
            this.bitmap = bitmap
            this.uri = uri
            YImageDialog.show(this, bitmap)
            YToast.show(this, "file:" + file.exists())
        }

        YPermissions.requestAll(this)

    }


    private var yNoticeDownload: YNoticeDownload? = null
    private fun download() {
        val url = "https://down.qq.com/qqweb/QQ_1/android_apk/AndroidQQ_8.4.5.4745_537065283.apk"
        if (yNoticeDownload == null)
            yNoticeDownload = YNoticeDownload(this, url)
        yNoticeDownload?.setDownLoadFail { show("下载失败") }
        yNoticeDownload?.setDownLoadComplete { uri, file ->
            show("下载完成")
            installApk(file)
        }
        yNoticeDownload?.setDownLoadProgress { downloadSize, fileSize ->
            val progress = (10000.0 * downloadSize / fileSize).toInt() / 100.0 //下载进度，保留2位小数
            text1.text = "$downloadSize/$fileSize"
            text2.text = "进度：$progress%"
        }
        yNoticeDownload?.start()
    }

    private var yVersionUpdate: YVersionUpdate?=null
    private fun update() {
        val url = "https://down.qq.com/qqweb/QQ_1/android_apk/AndroidQQ_8.4.5.4745_537065283.apk"
        yVersionUpdate = YVersionUpdate(
            this,
            20,
            false,
            url,
            "1.9.99",
            "\n修复了bug1引起的问题\n新增功能：aaa"
        )
        yVersionUpdate?.useNotificationDownload=true
        yVersionUpdate?.update()
    }


    private var yInstallApk: YInstallApk? = null
    private fun installApk(file: File) {
        yInstallApk = YInstallApk(this)
        //yInstallApk?.install(YPath.getSDCard() + "/app.apk")
        yInstallApk?.install(file.path)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        yInstallApk?.onActivityResult(requestCode, resultCode, data)
        yPicture.onActivityResult(requestCode, resultCode, data)
        yVersionUpdate?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        yNoticeDownload?.onDestroy()
        yVersionUpdate?.onDestroy()
    }
}
