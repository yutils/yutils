package com.yujing.contract

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 获取泛类类型
 */
/*
 用法举例
onResponse(call, response, object : YObjectListener<YResponse<Long>>() {
    override fun value(value: YResponse<Long>) {

    }
})

//过滤请求，并把结果转换成对象
fun <T> onResponse(call: Call, response: Response, listener: YObjectListener<T>): Boolean {
    try {
        if (response.code != 200) {
            Log.e("NET", "请求失败：错误码：${response.code}")
            TTS.speak("请求失败：错误码：${response.code}")
            TipDialog.show("请求失败：错误码：${response.code}", WaitDialog.TYPE.ERROR)
            return false
        }
        when {
            "byte[]" == listener.getType().toString() -> listener.value(response.body?.bytes() as T)
            String::class.java == listener.getType() -> listener.value(response.body?.string() as T)
            else -> {
                val json = response.body?.string()
                val data: T = gson.fromJson(json, listener.getType())
                //判断code
                if (data is YResponse<*>) {
                    if (data.errorCode != 0) {
                        Log.e("NET", "${data.msg}")
                        TTS.speak("${data.msg}")
                        TipDialog.show("${data.msg}", WaitDialog.TYPE.ERROR)
                        return false
                    }
                }
                listener.value(data)
            }
        }
    } catch (e: Exception) {
        Log.e("NET", "请求错误：${e.message}", e)
        TipDialog.show("请求错误：${e.message}", WaitDialog.TYPE.ERROR)
        return false
    }
    return true
}
 */
abstract class YObjectListener<T> protected constructor() {
    abstract fun value(value: T)
    private val type: Type = getSuperclassTypeParameter(javaClass)

    /**
     * 取出泛型的具体类型
     *
     * @return Type
     */
    open fun getType(): Type? {
        return type
    }

    companion object {
        //取出class的父类泛类类型
        fun getSuperclassTypeParameter(subclass: Class<*>): Type {
            val superclass = subclass.genericSuperclass
            if (superclass is Class<*>) {
                throw RuntimeException("Missing type parameter.")
            }
            val parameterized = superclass as ParameterizedType
            return parameterized.actualTypeArguments[0]
        }
    }
}