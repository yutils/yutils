package com.yujing.bus

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * bus 总线通信 工具类
 * 底层eventBus
 * @author yujing 2021年1月12日11:09:26
 */
/*用法

//注册该类
YBusEvent.init(this)

//发送消息
YBusEvent.post("tag1","123456789")

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
YBusEvent.onDestroy(this)
*/
class YBusEvent {
    companion object {
        init {
            EventBus.getDefault().register(this)
        }

        //类列表
        var listObject: MutableList<Any> = ArrayList()

        /**
         * 必须调用，注册类
         */
        fun init(anyClass: Any) {
            listObject.add(anyClass)
        }

        /**
         * 接收事件，并且调用已经注册类中包含@YBus注解的类
         */
        @Subscribe(sticky = true)
        fun subscribe(yMessage: YMessage<Any>) {
            for (any in listObject) Utils.findMethod(any, yMessage)
        }

        /**
         * 发送事件
         */
        fun post(tag: String, value: Any?) {
            EventBus.getDefault().post(YMessage(tag, value))
        }

        /**
         * 发送粘性事件
         */
        fun postSticky(tag: String, value: Any?) {
            removeSticky()
            EventBus.getDefault().postSticky(YMessage(tag, value))
        }

        /**
         * 删除粘性事件
         */
        fun removeSticky() {
            EventBus.getDefault().removeStickyEvent(YMessage(null, null))
        }

        /**
         * 移除全部bus
         */
        fun destroyAll() {
            YBusUtil.listObject.clear()
        }

        /**
         * 目标类退出时候必须调用
         */
        fun onDestroy(any: Any) {
            listObject.remove(any)
        }

        /**
         * 退出APP时候调用
         */
        fun exitApp() {
            EventBus.getDefault().removeStickyEvent(this)
        }
    }
}