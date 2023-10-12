package com.yujing.bus

import android.os.Build
import com.yujing.utils.YLog
import java.util.*

/**
 * bus 总线通信 工具类
 * 底层原理：循环遍历所有注册类
 * @author 余静 2021年3月31日11:37:59
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
@YBus
fun message(key: Any,message: Any) {
    YLog.i("收到：$key:$message")
    textView1.text = "收到：$key:$message"
}
//接收全部消息
@YBus
fun message(yMessage: YMessage<Any>) {
    YLog.i("收到：$key:$message")
    textView1.text = "收到：$key:$message"
}

//解绑该类
YBusUtil.onDestroy(this)
*/
class YBusUtil {
    companion object {
        //类列表
        @JvmStatic
        var listObject: Vector<Any> = Vector()

        //延迟事件
        @JvmStatic
        var anySticky: Vector<YMessage<Any>> = Vector()

        /**
         * 必须调用，注册类，同register
         */
        @JvmStatic
        fun init(anyObject: Any) {
            register(anyObject)
        }

        /**
         * 注册类，用完后必须unregister
         */
        @JvmStatic
        fun register(anyObject: Any) {
            if (listObject.contains(anyObject)) return
            anySticky.forEach { Utils.execute(anyObject, it) }
            listObject.add(anyObject)
        }

        /**
         * 接收事件，并且调用已经注册类中包含@YBus注解的类
         */
        @Synchronized
        @JvmStatic
        private fun subscribe(yMessage: YMessage<Any>) {
            try {
                //不能用for (i in list)循环，因为list长度不固定，循环途中增加或删除元素，会导致并发修改异常
                //list.forEach循环，增加元素会导致并发修改异常,删除不会
                //不能用for (i in list.indices)循环，因为list长度不固定，循环途中增删除元素，会导致并发修改异常或数组越界，增加不会
                //不能用for (i in 0..list.size-1)，同上
                //不能用for (i in list.size-1..0)，倒着循环删1个没问题，但是可能会同时移除多个
                //不能用var i=0 while (i <list.size)，因为删除元素会导致错乱。比如，a，b，c，d，执行到b的时候，删除b，会发现，没有执行的是c
                //不能使用迭代器，因为不知道删除的元素位置，不一定是当前迭代的元素

                //所以，复制出临时listTemp  //val temp: MutableList<Any> = Vector()
                val temp: MutableList<Any> = ArrayList()
                var i = 0
                while (i < listObject.size) temp.add(listObject[i++])
                //执行
                var j = 0
                while (j < temp.size) Utils.execute(temp[j++], yMessage)
            } catch (e: Exception) {
                YLog.e("总线发生异常" + e.message, e)
            }
        }

        /**
         * 发送事件
         */
        @JvmStatic
        fun post(tag: String, value: Any?) {
            subscribe(YMessage(tag, value))
        }

        /**
         * 发送事件
         */
        @JvmStatic
        fun post(tag: String) {
            subscribe(YMessage(tag, null))
        }

        /**
         * 发送粘性事件，当前注册的全部对象都能收到事件，未来每个新注册的对象也能收到事件。
         * 如果多次添加tag相同的事件，那么会更新value的值
         */
        @JvmStatic
        fun postSticky(tag: String, value: Any?) {
            var msg: YMessage<Any>? = null
            anySticky.forEach {
                if (it.type == tag) {
                    it.data = value
                    msg = it
                }
            }
            if (msg == null) {
                msg = YMessage(tag, value)
                anySticky.add(msg)
            }
            subscribe(msg!!)
        }

        /**
         * 发送粘性事件，当前注册的全部对象都能收到事件，未来每个新注册的对象也能收到事件
         * 如果多次添加tag相同的事件，那么会更新value的值
         */
        @JvmStatic
        fun postSticky(tag: String) {
            post(tag, null)
        }

        /**
         * 删除粘性事件
         */
        @JvmStatic
        fun removeSticky(tag: String) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                anySticky.removeIf { it.type == tag }
            } else {
                for (i in anySticky.size - 1..0) {
                    if (anySticky[i].type == tag) anySticky.removeAt(i)
                }
            }
        }

        /**
         * 清空粘性事件
         */
        @JvmStatic
        fun clearSticky() {
            anySticky.clear()
        }

        /**
         * 移除全部bus
         */
        @JvmStatic
        fun destroyAll() {
            listObject.clear()
        }

        /**
         * 目标类退出时候必须调用
         */
        @JvmStatic
        fun onDestroy(anyObject: Any) {
            unregister(anyObject)
        }

        /**
         * 移除目标类
         */
        @JvmStatic
        fun unregister(anyObject: Any) {
            listObject.remove(anyObject)
        }
    }
}