package com.yujing.bus

import com.yujing.utils.YLog
import com.yujing.utils.YThread
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * 找出anyClass中全部包含YBus注解的方法，并且调用
 */
//internal 本包可以调用
internal class Utils {
    companion object {
        /**
         * 找出anyClass中全部包含YBus注解的方法
         */
        @Synchronized
        fun findMethod(anyClass: Any, yMessage: YMessage<Any>) {
            //一层一层直到找到Object::class.java为止
            var mClass = anyClass.javaClass
            while (mClass != Object::class.java && mClass != Any::class.java) {
                //获取遍历该类所有方法
                val methods = mClass.declaredMethods
                for (method in methods) {
                    method.isAccessible = true //允许调用私有方法
                    // 判断这个methods上是否有这个注解,有就调用
                    if (method.isAnnotationPresent(YBus::class.java)) {
                        if (YThread.isMainThread())
                            runMethod(anyClass, method, yMessage)
                        else
                            YThread.runOnUiThread { runMethod(anyClass, method, yMessage) }
                    }
                }
                mClass = mClass.superclass!!
            }
        }

        /**
         * 调用执行包含YBus注解的methods
         */
        private fun runMethod(anyClass: Any, method: Method, yMessage: YMessage<Any>) {
            try {
                //如果只有一个接收参数，而且 @YBus(tag) tag值不为空，就直接返回data
                val yBus = method.getAnnotation(YBus::class.java)
                //获取参数列表
                val parameters = method.parameterTypes
                //如果是没有tag，满足情况就调用(@YBus() 默认长度1，默认是"")
                if (yBus.value.size == 1 && "" == yBus.value[0]) {
                    when (parameters.size) {
                        //只有1个参数
                        1 ->
                            //接收的是YMessage<Any>，直接调用， yMessage
                            if (parameters[0] == yMessage::class.java)
                                method.invoke(anyClass, yMessage)
                            //接收参数正好是发送的内容，直接调用， data
                            else if (yMessage.data!!::class.java == parameters[0]) {
                                method.invoke(anyClass, yMessage.data)
                            }
                        //有2个参数
                        2 ->
                            //如果data==null，直接调用， null
                            if (yMessage.data == null) {
                                method.invoke(anyClass, yMessage.type, null)
                            } else {
                                //如果data!=null,判断data类型相同后，直接调用， data
                                if (yMessage.data!!::class.java == parameters[1])
                                    method.invoke(anyClass, yMessage.type, yMessage.data)
                            }
                    }
                } else {
                    for (tag in yBus.value) {
                        if (tag != yMessage.type) continue
                        //tag，匹配成功
                        when (parameters.size) {
                            //如果这个方法没有参数，直接调用
                            0 ->
                                YThread.runOnUiThread { method.invoke(anyClass) }
                            //如果这个方法有1个参数，直接返回 data。
                            1 ->
                                method.invoke(anyClass, yMessage.data)
                            //如果这个方法有2个参数，直接返回 tag 和 data
                            2 ->
                                method.invoke(anyClass, yMessage.type, yMessage.data)
                        }
                    }
                }
            } catch (e: ClassNotFoundException) {
                YLog.e("YBus", "类没有找到", e)
            } catch (e: SecurityException) {
                YLog.e("YBus", "安全例外异常，检查权限", e)
            } catch (e: IllegalAccessException) {
                YLog.e("YBus", "调用方法权限不足", e)
            } catch (e: IllegalArgumentException) {
                YLog.e("YBus", "接口接收参数个数不匹配", e)
            } catch (e: InvocationTargetException) {
                // 获取目标异常
                val t = e.targetException
                if (t.message != null && t.message!!.contains("checkNotNullParameter")) {
                    YLog.e(
                        "YBus",
                        "调用的目标方法异常，发送数据有null，然接收参数却不能为null，可以设置接收参数后面加?，tag=${yMessage.type}", t
                    )
                } else {
                    YLog.e("YBus", "调用目标异常，如下", t)
                }
            } catch (e: ArithmeticException) {
                YLog.e("YBus", "算术运算异常", e)
            } catch (e: Throwable) {
                YLog.e("YBus", "未知异常", e)
            }
        }
    }
}