package com.yujing.utils

import android.app.AlertDialog
import com.yutils.http.YHttp
import com.yutils.http.contract.YHttpDownloadFileListener
import java.io.File
import kotlin.system.exitProcess

/**
 * 更新APP
 * @author 余静 2021年10月13日17:02:37
 */

/*使用说明,举例
val url = "https://down.qq.com/qqweb/QQ_1/android_apk/AndroidQQ_8.4.5.4745_537065283.apk"

//实例化
var yVersionUpdate = YVersionUpdate()

//服务器版本号, 是否强制更新, apk下载地址
yVersionUpdate?.update(999, true, url)

//通知栏下载需要调用onDestroy()
fun onDestroy() {
    yVersionUpdate?.onDestroy()
}
*/

/* 权限说明
安装apk
如果是安卓8.0以上先请求打开位置来源
权限：<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

1.首先创建res/xml/file_paths.xml
内容：
<?xml version="1.0" encoding="UTF-8"?>
<resources>
    <paths>
        <external-path path="" name="download"/>
    </paths>
</resources>

2.再在AndroidManifest.xml  中的application加入
<!--安装app-->
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.provider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>

 */

@Suppress("MemberVisibilityCanBePrivate", "FunctionName", "unused")
class YVersionUpdate {
    var serverCode: Int = 0//服务器版本
    var isForceUpdate: Boolean = false//是否强制更新
    lateinit var downUrl: String//下载路径
    var serverName: String = ""//服务器版本名
    var versionDescription: String = ""//版本说明

    //是否使用通知栏下载
    var useNotificationDownload = false

    //通知栏下载
    private var yNoticeDownload: YNoticeDownload? = null

    /**
     * 立即检查一次更新
     */
    fun update(
        serverCode: Int,//服务器版本
        isForceUpdate: Boolean = false,//是否强制更新
        downUrl: String,//下载路径
        serverName: String = "",//服务器版本名
        versionDescription: String = ""//版本说明
    ) {
        this.serverCode = serverCode
        this.isForceUpdate = isForceUpdate
        this.downUrl = downUrl
        this.serverName = serverName
        this.versionDescription = versionDescription

        if (serverCode > YUtils.getVersionCode()) needUpdate()
        else noNeedUpdate()
    }

    /**
     * 需要更新时候才弹出，否则不弹出
     */
    fun updateNeedUpdateDisplay(
        serverCode: Int,//服务器版本
        isForceUpdate: Boolean = false,//是否强制更新
        downUrl: String,//下载路径
        serverName: String = "",//服务器版本名
        versionDescription: String = ""//版本说明
    ) {
        this.serverCode = serverCode
        this.isForceUpdate = isForceUpdate
        this.downUrl = downUrl
        this.serverName = serverName
        this.versionDescription = versionDescription

        if (serverCode > YUtils.getVersionCode()) needUpdate()
    }

    /**
     * 不用更新时候弹窗
     */
    private fun noNeedUpdate() {
        val sb = """
            #本地版本号:${YUtils.getVersionCode()}（版本名:${YUtils.getVersionName()}）
            #最新版本号:$serverCode${if (serverName.isNotEmpty()) "（版本名:$serverName）" else ""}${if (versionDescription.isNotEmpty()) "\n更新说明：$versionDescription" else ""}
            #已是最新版,无需更新!
            """.trimMargin("#")
        val activity = YActivityUtil.getCurrentActivity()
        val dialog = AlertDialog.Builder(activity).setTitle("软件更新").setMessage(sb) // 设置内容
            .setPositiveButton("确定", null).create() // 创建
        // 显示对话框
        if (activity.isDestroyed || activity.isFinishing)
            return
        dialog.show()
    }

    /**
     * 需要更新时候弹窗
     */
    private fun needUpdate() {
        val activity = YActivityUtil.getCurrentActivity()
        val sb = """
            #本地版本号:${YUtils.getVersionCode()}　(版本名:${YUtils.getVersionName()})
            #最新版本号:$serverCode${if (serverName.isNotEmpty()) "（版本名:$serverName）" else ""}${if (versionDescription.isNotEmpty()) "\n更新说明：$versionDescription" else ""}
            #发现新版本，是否更新?
            """.trimMargin("#")
        val dialog =
            AlertDialog.Builder(activity).setTitle("软件更新").setCancelable(!isForceUpdate)
                .setMessage(sb)
                .setPositiveButton("更新（${YUtils.getVersionCode()}-->$serverCode）") { _, _ ->
                    if (useNotificationDownload) notifyDownApkFile() else yHttpDownApkFile()
                }.setNegativeButton(
                    if (isForceUpdate) "退出APP" else "暂不更新"
                ) { dialog, _ ->
                    // 判断强制更新
                    if (isForceUpdate) {
                        dialog.dismiss()
                        activity.finish()
                        exitProcess(0)
                    } else dialog.dismiss()
                }.create() // 创建
        // 显示对话框
        if (activity.isDestroyed || activity.isFinishing) return
        dialog.show()
    }

    /**
     * 自己下载器
     */
    private fun yHttpDownApkFile() {

        YShow.show("正在下载")
        YShow.setMessageOther("请稍候...")
        YShow.setCancel(!isForceUpdate)
//        var saveApkName = downUrl.substring(downUrl.lastIndexOf("/") + 1)
//        saveApkName = saveApkName
//            .replace("*", "_").replace("#", "_").replace(":", "_").replace("?", "_")
//            .replace("/", "_").replace("\\", "_").replace("|", "_").replace("<", "_")
//            .replace(">", "_").replace(" ", "_")
        val saveApkName = "update_$serverCode.apk"
        val file = File(YPath.getFilePath(YApp.get()) + "/download/" + saveApkName)
        //下载
        YHttp.create().downloadFile(downUrl, file, object :
            YHttpDownloadFileListener {
            override fun progress(downloadSize: Int, fileSize: Int) {
                val progress = (10000.0 * downloadSize / fileSize).toInt() / 100.0 //下载进度，保留2位小数
                YShow.show("下载进度:${YNumber.fill(progress)}%")
                YShow.setMessageOther(
                    if (downloadSize > 1048576) "已下载:" + YNumber.fill(downloadSize / 1048576.0, 2) + "MB" else "已下载:" + downloadSize / 1024 + "KB"
                )
            }

            override fun success(file: File) {
                YShow.setMessageOther("下载完成")
                YInstallApk().install(file)
            }

            override fun fail(value: String) {
                YShow.show("下载失败")
                YShow.setMessageOther(null)
                YShow.setCancel(true)
            }
        })
    }

    /**
     * 通知栏下载APK
     */
    private fun notifyDownApkFile() {
        YShow.show("正在下载")
        YShow.setMessageOther("请稍候...")
        YShow.setCancel(!isForceUpdate)
        if (yNoticeDownload == null) yNoticeDownload = YNoticeDownload(YActivityUtil.getCurrentActivity())
        yNoticeDownload?.url = downUrl
        yNoticeDownload?.isAPK = true
        yNoticeDownload?.setDownLoadComplete { uri, file ->
            YShow.show("下载完成")
            YInstallApk().install(file)
        }
        yNoticeDownload?.setDownLoadFail {
            YShow.show("下载失败")
            YShow.setMessageOther(null)
        }
        yNoticeDownload?.setDownLoadProgress { downloadSize, fileSize ->
            val progress = (10000.0 * downloadSize / fileSize).toInt() / 100.0 //下载进度，保留2位小数
            YShow.show("下载进度:${YNumber.fill(progress)}%")
            YShow.setMessageOther(
                if (downloadSize > 1048576) "已下载:" +
                        YNumber.fill(downloadSize / 1048576.0, 2) + "MB"
                else "已下载:" + downloadSize / 1024 + "KB"
            )
        }
        yNoticeDownload?.start()
    }

    fun onResume() {
        yNoticeDownload?.onResume()
    }

    //需要调用,注销广播
    fun onDestroy() {
        yNoticeDownload?.onDestroy()
        YShow.finish()
    }
}