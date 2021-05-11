package com.yujing.bus

/**
 * bus 总线通信 工具类
 * 底层原理：循环遍历所有注册类
 * @author yujing 2021年3月31日11:37:59
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
        //类列表
        var listObject: MutableList<Any> = ArrayList()

        //延迟事件
        var anySticky: YMessage<Any>? = null

        /**
         * 必须调用，注册类
         */
        fun init(anyClass: Any) {
            anySticky?.let { Utils.findMethod(anyClass, it) }
            listObject.add(anyClass)
        }

        /**
         * 接收事件，并且调用已经注册类中包含@YBus注解的类
         */
        @Synchronized
        private fun subscribe(yMessage: YMessage<Any>) {
            for (i in listObject.indices) Utils.findMethod(listObject[i], yMessage)
        }

        /**
         * 发送事件
         */
        fun post(tag: String, value: Any?) {
            subscribe(YMessage(tag, value))
        }

        /**
         * 发送事件
         */
        fun post(tag: String) {
            subscribe(YMessage(tag, null))
        }


        /**
         * 发送粘性事件
         */
        fun postSticky(tag: String, value: Any?) {
            anySticky = YMessage(tag, value)
            subscribe(YMessage(tag, value))
        }

        /**
         * 发送粘性事件
         */
        fun postSticky(tag: String) {
            anySticky = YMessage(tag, null)
            subscribe(YMessage(tag, null))
        }

        /**
         * 删除粘性事件
         */
        fun removeSticky() {
            anySticky = null
        }

        /**
         * 移除全部bus
         */
        fun destroyAll() {
            listObject.clear()
        }

        /**
         * 目标类退出时候必须调用
         */
        fun onDestroy(any: Any) {
            listObject.remove(any)
        }
    }
}