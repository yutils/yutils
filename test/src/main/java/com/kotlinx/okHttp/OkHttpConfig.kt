package com.kotlinx.okHttp

import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * 创建 OkHttpClient
 */
object OkHttpConfig {
    private var okHttpClient: OkHttpClient? = null
    fun getClient(): OkHttpClient {
        if (okHttpClient == null) {
            okHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .connectionPool(ConnectionPool(100, 100, TimeUnit.SECONDS))
                .retryOnConnectionFailure(true)//错误重试
                .addInterceptor(OkHttpInterceptor()).build()
        }
        return okHttpClient!!
    }
}