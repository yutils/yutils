package com.kotlinx.apkUpdate

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.kotlinx.extend.logI
import com.kotlinx.extend.readPath
import com.kotlinx.extend.toast
import com.kotlinx.extend.writePath
import com.kotlinx.okHttp.OkHttpConfig
import com.kotlinx.okHttp.OkHttpPretreatment
import com.kotlinx.utils.ui
import com.yujing.utils.YVersionUpdate
import com.yujing.view.YView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * 网络请求处理类
 */
/*
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
用法：AppUpdate().checkAndUpdate(this)
 */
class AppUpdate {
    companion object {
        val DEFAULT_BASE_URL = "http://apk.kotlinx.com:9999"
        var baseUrl: String
            get() = "UPDATE_URL.txt".readPath() ?: DEFAULT_BASE_URL
            set(value) = value.writePath("UPDATE_URL.txt")
    }

    //-----------------------------------------------方法开始-----------------------------------------------
    //检查并更新APP
    var yVersionUpdate: YVersionUpdate? = null

    // 存储当前网络请求Call，用于取消
    private var currentCall: Call? = null

    // 标记是否正在发起请求，避免重复
    private var isRequesting = false

    fun checkAndUpdate(activity: AppCompatActivity) {
        if (yVersionUpdate == null) {
            // 监听生命周期
            activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
                // 标记：是否已执行过销毁逻辑，避免重复执行
                private var isDestroyed = false
                override fun onDestroy(owner: LifecycleOwner) {
                    if (isDestroyed) return
                    isDestroyed = true
                    //取消网络请求
                    currentCall?.cancel()
                    currentCall = null
                    // 释放资源逻辑
                    yVersionUpdate?.onDestroy()
                    yVersionUpdate = null
                    // 移除自身，避免内存泄漏
                    owner.lifecycle.removeObserver(this)
                }
            })
            yVersionUpdate = YVersionUpdate().apply {
                alertDialogListener = {
                    val buttonOk = it.getButton(AlertDialog.BUTTON_POSITIVE)
                    val buttonCancel = it.getButton(AlertDialog.BUTTON_NEGATIVE)
                    YView.setButtonBackgroundTint(buttonOk, "#6045D0A0".toColorInt(), "#FF45D0A0".toColorInt())
                    YView.setButtonBackgroundTint(buttonCancel, "#6045D0A0".toColorInt(), "#FF45D0A0".toColorInt())
                }
            }
        }
        check(activity) { apkInfo ->
            // 判空+Activity状态检查（避免Activity销毁后创建View）
            if (activity.isFinishing || activity.isDestroyed) return@check
            yVersionUpdate?.updateNeedUpdateDisplay(
                apkInfo.versionCode,
                apkInfo.forceUpdate,
                apkInfo.downloadUrl,
                apkInfo.versionName,
                apkInfo.changelog
            )
        }
    }

    //网络请求
    fun check(activity: AppCompatActivity, listener: ((ApkInfo) -> Unit)? = null) {
        // 新增：如果正在请求，直接返回
        if (isRequesting) return
        isRequesting = true
        val url = "${baseUrl}/appserver/apk/latest?packageName=${activity.packageName}"
        val request: Request = Request.Builder().url(url).get().build()
        currentCall = OkHttpConfig.getClient().newCall(request).apply {
            enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    isRequesting = false
                    if (call.isCanceled()) return
                    OkHttpPretreatment.netError(call, e)
                }

                override fun onResponse(call: Call, response: Response) {
                    isRequesting = false
                    OkHttpPretreatment.onSuccess<UpdateResponse<ApkInfo>>(response) { value ->
                        ui { value.data?.let { listener?.invoke(it) } }
                    }
                    OkHttpPretreatment.onFail<UpdateResponse<ApkInfo>>(response) { value ->
                        "APP更新失败：${value.msg}".logI().toast()
                    }
                }
            })
        }
    }
}