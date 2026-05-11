package com.kotlinx.okHttp

import com.kotlinx.apkUpdate.UpdateResponse
import com.kotlinx.extend.logE
import com.kotlinx.extend.speak
import com.kotlinx.extend.toast
import com.kotlinx.utils.ui
import com.yujing.bus.YBusUtil
import com.yujing.utils.YJson.toEntity
import com.yujing.utils.YShow
import okhttp3.Call
import okhttp3.Response
import java.io.FileNotFoundException
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.MalformedURLException
import java.net.SocketTimeoutException

/**
 * 预处理类
 */
object OkHttpPretreatment {
    const val 网络正常 = "网络正常"
    const val 网络异常 = "网络异常"

    //定义正常时code的值
    var successCode = 0

    //网络错误情况
    fun netError(call: Call, e: IOException) {
        YShow.finish()
        @Suppress("USELESS_IS_CHECK")
        val error = when (e) {
            is MalformedURLException -> "URL地址不规范"
            is SocketTimeoutException -> "网络连接超时"
            is UnsupportedEncodingException -> "不支持的编码"
            is FileNotFoundException -> "找不到该地址"
            is IOException -> "连接服务器失败"
            is Exception -> "请求异常"
            else -> "请求失败 " + e.message
        }
        ui { error.logE("NET").speak().toast() }
        YBusUtil.post(网络异常)
    }

    //返回结果正常
    inline fun <reified T : Any> onSuccess(response: Response, listener: (T) -> Unit) {
        YShow.finish()
        YBusUtil.post(网络正常)
        if (response.code == 200) {
            val it = response.bodyToString()
            if (it.isEmpty()) ui { "返回结果为空".speak().toast().logE() }
            it//.also { json -> if (!json.isJson()) "返回不是json".speak().toast().logE() }
                .toEntity<T>()
                .also { bean -> if (bean == null) ui { "数据解析失败".speak().toast().logE() } }
                .also { bean -> if (bean is UpdateResponse<*> && bean.code == successCode) listener.invoke(bean) }
        } else {
            ui { "请求失败，错误码：${response.code}".logE().speak().toast() }
        }
    }

    //返回结果异常
    inline fun <reified T : Any> onFail(response: Response, listener: ((T) -> Unit) = {}) {
        YShow.finish()
        if (response.code == 200) {
            val it = response.bodyToString()
            if (it.isEmpty()) "返回结果为空".logE()
            it.toEntity<T>()
                .also { bean -> if (bean == null) "数据解析失败".logE() }
                .also { bean -> if (bean is UpdateResponse<*> && bean.code != successCode) listener.invoke(bean) }
        }
    }
}