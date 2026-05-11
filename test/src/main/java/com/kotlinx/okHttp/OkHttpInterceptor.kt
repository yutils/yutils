package com.kotlinx.okHttp

import com.kotlinx.extend.logD
import com.kotlinx.extend.logI
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import java.io.IOException
import java.nio.charset.StandardCharsets


/**
 * OkHttp 拦截器  请求日志打印
 * @author yujing 2023年8月14日11:12:47
 */
class OkHttpInterceptor : Interceptor {
    var session: String? = null
    override fun intercept(chain: Chain): Response {
        //拦截器，给每条请求添加头
        val request = chain.request().newBuilder().run {
            addHeader("Accept", "application/json")
            addHeader("Accept-Encoding", "identity")
            addHeader("Connection", "keep-alive")
            session?.let { addHeader("cookie", it) }
            //url(chain.request().url.toString())
            build()
        }

        val startTime = System.currentTimeMillis()//记录请求开始时间
        val response: Response = chain.proceed(request)//请求
        val time = System.currentTimeMillis() - startTime //计算请求时间
        val rqBody = request.bodyToString()
        val bodyString = response.bodyToString()
        """
                #类型：${request.method}
                #地址：${request.url}${if (rqBody.isNotEmpty()) "\n发送：${rqBody}" else ""}${if (bodyString.isNotEmpty()) "\n结果：${bodyString}" else ""}
                #耗时：${time}ms${"  code：${response.code}"}${if (session != null) "  session:${session}" else ""}
                """.trimMargin("#").logI("请求信息")
        //获取session
        val cookies = response.headers.values("Set-Cookie")
        if (cookies.isNotEmpty()) {
            session = cookies[0].substring(0, cookies[0].indexOf(";")).logD("session")
        }
        //val bytes = response.body?.bytes()//获取请求结果（response.body只能读取一次）
        //return response.newBuilder().apply { bytes?.let { body(it.toResponseBody()) } }.build() //因为response只能读一次，所以还得写回去
        return response
    }
}

/**
 * 复制并读取body中数据
 */
fun Response.bodyToString(): String {
    val responseBody: ResponseBody = this.body ?: return ""
    val source = responseBody.source()
    source.request(Long.MAX_VALUE)
    var charset = StandardCharsets.UTF_8
    val contentType = responseBody.contentType()
    if (contentType?.charset() != null) {
        charset = contentType.charset()
    }
    return source.buffer.clone().readString(charset!!)
}

/**
 * 复制并读取body中数据
 */
fun Request.bodyToString(): String {
    return try {
        val buffer = Buffer()
        if ("GET" == this.method) return ""
        if (this.newBuilder().build().body == null) return ""
        this.newBuilder().build().body?.writeTo(buffer)
        buffer.readUtf8()
    } catch (e: IOException) {
        e.printStackTrace()
        "请求异常：${e.message}"
    }
}