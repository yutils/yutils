package com.yujing.bus

import java.io.Serializable

/**
 * 消息类，发送或者接收消息
 * @author 余静 2020年7月27日19:51:20
 */
/*
RxBus用法 RxBus.getDefault().post(YMessage<Any?>(key,value))
*/
open class YMessage<T>(var type: String?, var data: T?) : Serializable {
    /**
     *重写string方法，打印type和data的值
     */
    override fun toString(): String {
        //如果引用的有Gson包就用Gson序列化
        try {
            val cls = Class.forName("com.google.gson.Gson")
            val obj = cls.newInstance()
            val method = cls.getMethod("toJson", Any::class.java)
            val dataJson = method.invoke(obj, data) as String
            return "{" +
                    "type='" + type + '\'' +
                    ", data=" + dataJson +
                    '}'
        } catch (e: Exception) {
            //如果失败就用Yjson转json
            return "{" +
                    "type='" + type + '\'' +
                    ", data=" + data +
                    '}'
        }
    }
}