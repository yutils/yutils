package com.yujing.bus

import com.blankj.rxbus.RxBus
import io.reactivex.android.schedulers.AndroidSchedulers
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * bus 总线通信 工具类
 * @author yujing 2021年1月12日11:09:26
 */
/*用法

//注册该类
YBusUtil.init(this)

//发送消息
YBusUtil.post("tag1","123456789")

//接收消息
@YBus("tag1")
fun message(message: Any) {
    YLog.i("收到：$message")
}

//接收全部消息
@YBus()
fun message(key: Any,message: Any) {
    YLog.i("收到：$key:$message")
    textView1.text = "收到：$key:$message"
}

//解绑该类
YBusUtil.onDestroy(this)
*/
class YBusUtil {
    companion object {
        /**
         * 必须调用
         */
        fun init(anyClass: Any) {
            RxBus.getDefault().subscribeSticky(anyClass, AndroidSchedulers.mainThread(),
                object : RxBus.Callback<YMessage<Any>>() {
                    override fun onEvent(yMessage: YMessage<Any>) {
                        try {
                            val methods = anyClass.javaClass.declaredMethods
                            //遍历所有方法
                            for (method in methods) {
                                method.isAccessible = true //允许调用私有方法
                                doMessage(anyClass, method, yMessage.type, yMessage.data) // 设置监听
                            }
                        } catch (e: IllegalAccessException) {
                            println("调用方法权限不足")
                            e.printStackTrace()
                        } catch (e: IllegalArgumentException) {
                            println("接口接收参数个数不匹配")
                            e.printStackTrace()
                        } catch (e: InvocationTargetException) {
                            println("调用目标异常")
                            e.printStackTrace()
                        }
                    }
                }
            )
        }

        /**
         * 消息分发到对应方法
         */
        private fun doMessage(
            anyClass: Any,
            method: Method,
            messageType: String?,
            messageData: Any?
        ) {

            // 判断这个methods上是否有这个注解
            if (method.isAnnotationPresent(YBus::class.java)) {
                //获取参数个数
                val parameters = method.parameterTypes
                //如果这个方法有两个参数，直接返回messageType，messageData
                when (parameters.size) {
                    2 -> method.invoke(anyClass, messageType, messageData)
                }

                val yBus = method.getAnnotation(YBus::class.java)
                if (yBus != null) {
                    for (tag in yBus.tag) {
                        if (tag == messageType) {
                            //如果这个方法有一个参数，直接返回messageData。
                            // 如果这个方法没有参数，直接调用
                            when (parameters.size) {
                                1 -> method.invoke(anyClass, messageData)
                                else -> method.invoke(anyClass)
                            }
                        }
                    }
                }
            }
        }

        fun post(tag: String, value: Any?) {
            RxBus.getDefault().post(YMessage(tag, value))
        }

        fun postSticky(tag: String, value: Any?) {
            RxBus.getDefault().postSticky(YMessage(tag, value))
        }

        /**
         * 必须调用
         */
        fun onDestroy(any: Any) {
            RxBus.getDefault().unregister(any)
        }
    }
}