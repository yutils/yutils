package com.yujing.utils

import android.graphics.Color
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import androidx.appcompat.app.AlertDialog
import com.yujing.view.YAlertDialogUtils
import com.yutils.http.YHttp
import com.yutils.http.contract.YHttpDownloadFileListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.Objects
import kotlin.system.exitProcess

/**
 * 更新APP
 * @author 余静 2021年10月13日17:02:37
 */

/*使用说明,举例
val url = "https://down.qq.com/qqweb/QQ_1/android_apk/AndroidQQ_8.4.5.4745_537065283.apk"
var yVersionUpdate = YVersionUpdate()
//设置按钮背景颜色
yVersionUpdate.alertDialogListener = {
    val buttonOk = it.getButton(AlertDialog.BUTTON_POSITIVE)
    val buttonCancel = it.getButton(AlertDialog.BUTTON_NEGATIVE)
    YView.setButtonBackgroundTint(buttonOk, Color.parseColor("#6045D0A0"), Color.parseColor("#FF45D0A0"))
    YView.setButtonBackgroundTint(buttonCancel, Color.parseColor("#6045D0A0"), Color.parseColor("#FF45D0A0"))
}
//服务器版本号, 是否强制更新, apk下载地址
yVersionUpdate?.update(999, true, url)
yVersionUpdate?.update(999, true, "1.0.1","这是更新说明")

//通知栏下载需要调用onDestroy()
fun onDestroy() {
    yVersionUpdate?.onDestroy()
}
*/

/*
java:
YVersionUpdate yVersionUpdate = new YVersionUpdate();
 //设置按钮背景颜色
yVersionUpdate.setAlertDialogListener(it -> {
    Button buttonOk = it.getButton(AlertDialog.BUTTON_POSITIVE);
    Button buttonCancel = it.getButton(AlertDialog.BUTTON_NEGATIVE);
    YView.INSTANCE.setButtonBackgroundTint(buttonOk, Color.parseColor("#6045D0A0"), Color.parseColor("#FF45D0A0"));
    YView.INSTANCE.setButtonBackgroundTint(buttonCancel, Color.parseColor("#6045D0A0"), Color.parseColor("#FF45D0A0"));
    return null;
});
yVersionUpdate.getDialog().setFullScreen(false);
//服务器版本号, 是否强制更新, apk下载地址
yVersionUpdate.update(32, true, url, "1.1.1", "这是详细说明1\n这是详细说明2");

 */


/* 权限说明
安装apk
如果是安卓8.0以上先请求打开位置来源
1.AndroidManifest.xml添加权限
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

2.创建文件：res/xml/provider_paths.xml
内容：
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- /storage/emulated/0/Download/${applicationId}/.beta/apk-->
    <external-path name="beta_external_path" path="Download/"/>
    <!--/storage/emulated/0/Android/data/${applicationId}/files/apk/-->
    <external-path name="beta_external_files_path" path="Android/data/"/>
</paths>

3.在AndroidManifest.xml中的application加入
<!--安装app-->
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileProvider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/provider_paths"/>
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

    //是否使用OkHttp
    var useOkHttp = true

    //是否显示失败原因
    var showFailDialog = true

    //通知栏下载
    private var yNoticeDownload: YNoticeDownload? = null

    //弹窗
    //yVersionUpdate.dialog.okButtonBackgroundColor= Color.parseColor("#21A9FA")
    //yVersionUpdate.dialog.okButtonTextColor= Color.parseColor("#FFFFFF")
    //yVersionUpdate.dialog.cancelButtonBackgroundColor= Color.parseColor("#21A9FA")
    //yVersionUpdate.dialog.cancelButtonTextColor= Color.parseColor("#FFFFFF")
    var dialog = YAlertDialogUtils()

    //当前显示的AlertDialog
    var alertDialogListener: ((AlertDialog) -> Unit)? = null

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
            #本地版本:${YUtils.getVersionCode()}　(${YUtils.getVersionName()})
            #最新版本:$serverCode${if (serverName.isNotEmpty()) "（$serverName）" else ""}${if (versionDescription.isNotEmpty()) "\n更新说明：\n$versionDescription" else "\n"}
            #已是最新版,无需更新!
            """.trimMargin("#")

//        val dialog = AlertDialog.Builder(activity).setTitle("软件更新").setMessage(sb) // 设置内容
//            .setPositiveButton("确定", null).create() // 创建
//        dialog.show()
        dialog.contentTextViewGravity = Gravity.START
        dialog.okButtonString = "确定"
        val alertDialog = dialog.showMessage("软件更新", sb) {}
        alertDialogListener?.invoke(alertDialog)
    }

    /**
     * 需要更新时候弹窗
     */
    private fun needUpdate() {
        val sb = """
            #本地版本:${YUtils.getVersionCode()}  (${YUtils.getVersionName()})
            #最新版本:$serverCode${if (serverName.isNotEmpty()) "($serverName)" else ""}${if (versionDescription.isNotEmpty()) "\n更新说明：\n$versionDescription" else "\n"}
            #发现新版本，是否更新?
            """.trimMargin("#")
        dialog.contentTextViewGravity = Gravity.START
        dialog.okButtonString = "更新(${YUtils.getVersionCode()}→$serverCode)"
        dialog.cancelButtonString = if (isForceUpdate) "退出APP" else "暂不更新"
        dialog.cancelable = !isForceUpdate
        val alertDialog = dialog.showMessageCancel("软件更新", sb, {
            when {
                useNotificationDownload -> notifyDownApkFile()
                useOkHttp -> okHttpDownApkFile()
                else -> yHttpDownApkFile()
            }
        }, {
            // 判断强制更新
            if (isForceUpdate) {
                YActivityUtil.getCurrentActivity().finish()
                exitProcess(0)
            }
        })
        alertDialogListener?.invoke(alertDialog)
    }

    /**
     * 自己下载器
     */
    private fun yHttpDownApkFile() {
        YShow.show("正在下载", "请稍候...")
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
                if (YRunOnceOfTime.check(100, "下载中") || downloadSize == fileSize) {
                    val message1 = "下载进度:${YNumber.fill((10000.0 * downloadSize / fileSize).toInt() / 100.0)}%"//下载进度，保留2位小数
                    val message2 = if (downloadSize > 1048576) "已下载:" + YNumber.fill(downloadSize / 1048576.0, 2) + "MB" else "已下载:" + downloadSize / 1024 + "KB"
                    YShow.show(message1, message2)
                    YLog.i("下载中", "$message1 $message2")
                }
            }

            override fun success(file: File) {
                YLog.i("下载完成", "存放路径${file.path}")
                YShow.show("下载完成")
                try {
                    YInstallApk().install(file)
                } catch (e: Exception) {
                    exceptionDialog("安装失败", "原因:${e.message}")
                }
            }

            override fun fail(value: String) {
                exceptionDialog("下载失败", "原因:${value}")
            }
        })
    }

    /**
     * okHttp下载
     */
    private fun okHttpDownApkFile() {
        YShow.show("正在下载", "请稍候...")
        YShow.setCancel(!isForceUpdate)
//        var saveApkName = downUrl.substring(downUrl.lastIndexOf("/") + 1)
//        saveApkName = saveApkName
//            .replace("*", "_").replace("#", "_").replace(":", "_").replace("?", "_")
//            .replace("/", "_").replace("\\", "_").replace("|", "_").replace("<", "_")
//            .replace(">", "_").replace(" ", "_")
        val saveApkName = "update_$serverCode.apk"
        val file = File(YPath.getFilePath(YApp.get()) + "/download/" + saveApkName)
        val parent = file.parentFile
        if (!Objects.requireNonNull(parent).exists()) parent.mkdirs()
        if (file.exists()) file.delete() // 删除存在文件

        val request: Request = Request.Builder().url(downUrl).get().build()
        //协程
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val startTime = System.currentTimeMillis()
                //文件下载别用过滤器
                val response = OkHttpClient().newCall(request).execute()
                if (response.code != 200) {
                    exceptionDialog("下载失败", "code:${response.code}")
                    return@launch
                }
                val inputStream = response.body?.byteStream()
                inputStream?.let {
                    val output = FileOutputStream(file)
                    var downloadSize = 0L   //当前长度
                    val fileSize = response.body!!.contentLength()  //总长度
                    val b = ByteArray(4096)
                    var len: Int
                    while (inputStream.read(b).also { len = it } != -1) {
                        output.write(b, 0, len)
                        downloadSize += len
                        if (fileSize > 0) {
                            if (YRunOnceOfTime.check(100, "下载中") || downloadSize == fileSize) {
                                val message1 = "下载进度:${YNumber.fill((10000.0 * downloadSize / fileSize).toInt() / 100.0)}%"//下载进度，保留2位小数
                                val message2 = if (downloadSize > 1048576) "已下载:" + YNumber.fill(downloadSize / 1048576.0, 2) + "MB" else "已下载:" + downloadSize / 1024 + "KB"
                                YShow.show(message1, message2)
                                YLog.i("下载中", "$message1 $message2")
                            }
                        } else {
                            if (YRunOnceOfTime.check(100, "下载中") || len < b.size) {
                                val message1 = "下载中..."
                                val message2 = if (downloadSize > 1048576) "已下载:" + YNumber.fill(downloadSize / 1048576.0, 2) + "MB" else "已下载:" + downloadSize / 1024 + "KB"
                                YShow.show(message1, message2)
                                YLog.i("下载中", "$message1 $message2")
                            }
                        }
                    }
                    output.flush()
                    output.close()
                    inputStream.close()
                    YLog.i("下载完成", "下载完成，总长度：${downloadSize},存放路径${file.path},耗时：${System.currentTimeMillis() - startTime}毫秒")
                    YShow.show("下载完成")
                    try {
                        YInstallApk().install(file)
                    } catch (e: Exception) {
                        exceptionDialog("安装失败", "原因:${e.message}")
                    }
                }
            } catch (e: Exception) {
                exceptionDialog("下载失败", "原因:${e.message}")
            }
        }
    }

    //异常时弹窗
    private fun exceptionDialog(title: CharSequence, cause: CharSequence) {
        if (!showFailDialog) {
            val ys = YShow.show(title, cause)
            ys.setOnClickListener {
                if (isForceUpdate) {
                    YActivityUtil.getCurrentActivity().finish()
                    exitProcess(0)
                } else {
                    YShow.finish()
                }
            }
            return
        }
        //失败原因弹窗
        YShow.finish()
        val content = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SpannableStringBuilder().apply {
                append("${cause}\n", ForegroundColorSpan(Color.parseColor("#FFFF0000")), Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                append("下载地址：${downUrl}\n", ForegroundColorSpan(Color.parseColor("#FF51A691")), Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                append("请尝试浏览器自行下载安装", ForegroundColorSpan(Color.parseColor("#FFFF8888")), Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            }
        } else {
            "${cause}\n下载地址：${downUrl}\n请尝试浏览器自行下载安装"
        }

        YThread.ui {
            //提示,有确定按钮
            YAlertDialogUtils().apply {
                cancelable = !isForceUpdate //如果是强制更新
                okButtonString = if (isForceUpdate) "退出" else "确定"
                showMessage(title, content) {
                    // 判断强制更新 ,直接退出
                    if (isForceUpdate) {
                        YActivityUtil.getCurrentActivity().finish()
                        exitProcess(0)
                    }
                }
            }
        }
    }

    /**
     * 通知栏下载APK
     */
    private fun notifyDownApkFile() {
        YShow.show("正在下载", "请稍候...")
        YShow.setCancel(!isForceUpdate)
        if (yNoticeDownload == null) yNoticeDownload = YNoticeDownload(YActivityUtil.getCurrentActivity())
        yNoticeDownload?.url = downUrl
        yNoticeDownload?.isAPK = true
        yNoticeDownload?.setDownLoadComplete { uri, file ->
            YShow.show("下载完成")
            YLog.i("下载完成")
            try {
                YInstallApk().install(file)
            } catch (e: Exception) {
                YShow.show("下载完成", "安装异常：" + e.message)
            }
        }
        yNoticeDownload?.setDownLoadFail {
            YShow.show("下载失败")
            YLog.e("下载失败")
        }
        yNoticeDownload?.setDownLoadProgress { downloadSize, fileSize ->
            val message1 = "下载进度:${YNumber.fill((10000.0 * downloadSize / fileSize).toInt() / 100.0)}%"//下载进度，保留2位小数
            val message2 = if (downloadSize > 1048576) "已下载:" + YNumber.fill(downloadSize / 1048576.0, 2) + "MB" else "已下载:" + downloadSize / 1024 + "KB"
            YShow.show(message1, message2)
            YLog.i("下载中", "$message1 $message2")
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