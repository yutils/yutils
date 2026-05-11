package com.kotlinx.apkUpdate

import java.io.Serializable

class UpdateResponse<T> : Serializable {
    val code = 0 // 状态码，0=成功，1=失败
    val msg: String? = null // 消息提示
    val data: T? = null // 具体数据
}