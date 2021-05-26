package com.yujing.bus
//
//import com.blankj.rxbus.RxBus
//import com.yujing.utils.YThread
//
///**
// * bus 总线通信 工具类
// * 底层RxBus
// * @author 余静 2021年1月12日11:09:26
// */
///*用法
//
////注册该类
//YBusRxBus.init(this)
//
////发送消息
//YBusRxBus.post("tag1","123456789")
//
////接收消息
//@YBus("tag1")
//fun message(message: Any) {
//    YLog.i("收到：$message")
//}
//
////接收全部消息
//@YBus()
//fun message(key: Any,message: Any) {
//    YLog.i("收到：$key:$message")
//    textView1.text = "收到：$key:$message"
//}
//
////解绑该类
//YBusRxBus.onDestroy(this)
//*/
//class YBusRxBus {
//    companion object {
//        /**
//         * 必须调用，注册类
//         */
//        fun init(anyClass: Any) {
////            RxBus.getDefault().subscribeSticky(anyClass, AndroidSchedulers.mainThread(),
////                object : RxBus.Callback<YMessage<Any>>() {
////                    override fun onEvent(yMessage: YMessage<Any>) { } } )
//            //如果打开 AndroidSchedulers.mainThread(),数据量大了会下面异常
//            //E/RxBus: io.reactivex.exceptions.MissingBackpressureException: Could not emit value due to lack of requests
//            //导致上下游流速不均
//            RxBus.getDefault().subscribeSticky(
//                anyClass, object : RxBus.Callback<YMessage<Any>>() {
//                    override fun onEvent(yMessage: YMessage<Any>) {
//                        Utils.findMethod(anyClass, yMessage)
//                    }
//                }
//            )
//        }
//
//        /**
//         * 发送事件
//         */
//        fun post(tag: String, value: Any?) {
//            YThread.runOnUiThread { RxBus.getDefault().post(YMessage(tag, value)) }
//        }
//
//        /**
//         * 发送粘性事件
//         */
//        fun postSticky(tag: String, value: Any?) {
//            removeSticky()
//            YThread.runOnUiThread { RxBus.getDefault().postSticky(YMessage(tag, value)) }
//        }
//
//        /**
//         * 删除粘性事件
//         */
//        fun removeSticky() {
//            RxBus.getDefault().removeSticky(YMessage(null, null))
//        }
//
//        /**
//         * 目标类退出时候必须调用
//         */
//        fun onDestroy(any: Any) {
//            RxBus.getDefault().unregister(any)
//        }
//    }
//}