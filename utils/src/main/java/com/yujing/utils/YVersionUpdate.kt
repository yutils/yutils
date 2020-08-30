package com.yujing.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import com.yutils.http.YHttp
import com.yutils.http.contract.YHttpDownloadFileListener
import java.io.File
import kotlin.system.exitProcess

/**
 * 更新APP
 * @author yujing 2020年8月30日13:04:47
 */

/*使用说明,举例

val url = "https://down.qq.com/qqweb/QQ_1/android_apk/AndroidQQ_8.4.5.4745_537065283.apk"
//activity, 服务器版本号, 是否强制更新, apk下载地址
val yVersionUpdate = YVersionUpdate(activity, 20, false, url)
//或者activity, 服务器版本号, 是否强制更新, apk下载地址，服务器版版本名，更新说明
//YVersionUpdate(this, 20, false, url,"1.1.2"，更新说明).checkUpdate()

yVersionUpdate.checkUpdate()

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    yVersionUpdate?.onActivityResult(requestCode, resultCode, data)
}

fun onDestroy() {
    yVersionUpdate?.onDestroy()
}
*/

/* 权限说明
 1.运行下文件，首先创建res/xml/file_paths.xml

    <?xml version="1.0" encoding="UTF-8"?>
    <resources>
        <paths>
            <external-path path="" name="download"/>
        </paths>
    </resources>

 2.允许安装app，在AndroidManifest.xml  中的application加入

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
class YVersionUpdate(
    var activity: Activity,//activity
    var serverCode: Int,//服务器版本
    var isForceUpdate: Boolean = false,//是否强制更新
    var downUrl: String,//下载路径
    var serverName: String = "",//服务器版本名
    var versionDescription: String = ""//版本说明
) {
    //是否使用通知栏下载
    var useNotificationDownload = false
    private var yNoticeDownload: YNoticeDownload? = null

    //安装apk
    private var yInstallApk: YInstallApk? = null

    //初始化
    init {
        yInstallApk = YInstallApk(activity)
    }

    /**
     * 立即检查一次更新
     */
    fun update() {
        if (serverCode > YUtils.getVersionCode(activity)) needUpdate()
        else noNeedUpdate()
    }

    /**
     * 需要更新时候才弹出，否则不弹出
     */
    fun updateNeedUpdateDisplay() {
        if (serverCode > YUtils.getVersionCode(activity)) needUpdate()
    }

    /**
     * 不用更新
     */
    private fun noNeedUpdate() {
        val sb = """
            #本地版本号:${YUtils.getVersionCode(activity)}（版本名:${YUtils.getVersionName(activity)}）
            #最新版本号:$serverCode${if (serverName.isNotEmpty()) "（版本名:$serverName）" else ""}${if (versionDescription.isNotEmpty()) "\n更新说明：$versionDescription" else ""}
            #已是最新版,无需更新!
            """.trimMargin("#")
        val dialog = AlertDialog.Builder(activity).setTitle("软件更新").setMessage(sb) // 设置内容
            .setPositiveButton("确定", null).create() // 创建
        // 显示对话框
        if (activity.isDestroyed || activity.isFinishing)
            return
        dialog.show()
    }

    /**
     * 需要更新
     */
    private fun needUpdate() {
        val sb = """
            #本地版本号:${YUtils.getVersionCode(activity)}　(版本名:${YUtils.getVersionName(activity)})
            #最新版本号:$serverCode${if (serverName.isNotEmpty()) "（版本名:$serverName）" else ""}${if (versionDescription.isNotEmpty()) "\n更新说明：$versionDescription" else ""}
            #发现新版本，是否更新?
            """.trimMargin("#")
        val dialog =
            AlertDialog.Builder(activity).setTitle("软件更新").setCancelable(!isForceUpdate)
                .setMessage(sb)
                .setPositiveButton("更新（${YUtils.getVersionCode(activity)}-->$serverCode）") { _, _ ->
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
        YShow.show(activity, "正在下载")
        YShow.setMessageOther("请稍候...")
        YShow.setCancel(!isForceUpdate)
        val saveApkName = downUrl.substring(downUrl.lastIndexOf("/") + 1)
        val file = File(YPath.getFilePath(activity) + "/download/" + saveApkName)
        //下载
        YHttp.create().downloadFile(downUrl, file, object :
            YHttpDownloadFileListener {
            override fun progress(downloadSize: Int, fileSize: Int) {
                if (!activity.isFinishing) {
                    val progress = (10000.0 * downloadSize / fileSize).toInt() / 100.0 //下载进度，保留2位小数
                    YShow.setMessage("下载进度$progress%")
                    YShow.setMessageOther(
                        if (downloadSize > 1048576) "已下载:" +
                                YNumber.showNumber(downloadSize / 1048576.0, 1) + "MB"
                        else "已下载:" + downloadSize / 1024 + "KB"
                    )
                } else YShow.finish()
            }

            override fun success(file: File) {
                if (!activity.isFinishing) {
                    if (!activity.isFinishing) YShow.finish()
                    YShow.setMessageOther("下载完成")
                    yInstallApk?.install(file)
                }
            }

            override fun fail(value: String) {
                if (!activity.isFinishing) {
                    YShow.setMessage("下载失败")
                    YShow.setMessageOther(null)
                    YShow.setCancel(true)
                }
            }
        })
    }

    /**
     * 通知栏下载APK
     */
    private fun notifyDownApkFile() {
        YShow.show(activity, "正在下载")
        YShow.setMessageOther("请稍候...")
        YShow.setCancel(!isForceUpdate)
        if (yNoticeDownload == null) yNoticeDownload = YNoticeDownload(activity)
        yNoticeDownload?.url = downUrl
        yNoticeDownload?.isAPK = true

        yNoticeDownload?.setDownLoadComplete { uri, file ->
            if (!activity.isFinishing)
                YShow.setMessageOther("下载完成")
            yInstallApk?.install(uri)
        }

        yNoticeDownload?.setDownLoadFail {
            if (!activity.isFinishing) {
                YShow.setMessage("下载失败")
                YShow.setMessageOther(null)
            }
        }
        yNoticeDownload?.setDownLoadProgress { downloadSize, fileSize ->
            if (!activity.isFinishing) {
                val progress = (10000.0 * downloadSize / fileSize).toInt() / 100.0 //下载进度，保留2位小数
                YShow.setMessage("下载进度$progress%")
                YShow.setMessageOther(
                    if (downloadSize > 1048576) "已下载:" +
                            YNumber.showNumber(downloadSize / 1048576.0, 1) + "MB"
                    else "已下载:" + downloadSize / 1024 + "KB"
                )
            } else YShow.finish()
        }

        yNoticeDownload?.start()
    }

    fun onResume() {
        yNoticeDownload?.onResume()
    }

    //需要调用
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        yInstallApk?.onActivityResult(requestCode, resultCode, data)
    }

    //需要调用,注销广播
    fun onDestroy() {
        yNoticeDownload?.onDestroy()
        YShow.finish()
    }
}