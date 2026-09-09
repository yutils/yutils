package com.yujing.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Message
import android.view.WindowManager
import android.widget.Toast
import com.yujing.utils.TTS.speak
import com.yujing.utils.TTS.speakQueue
import com.yujing.utils.YThread.runOnUiThread

/**
 * Toast，当第一个未消失时调用，直接覆盖文本值，并延长两秒显示时间
 * 或者队列显示一条toast 至少显示queueTime时间
 *
 * @author 余静 2019年2月18日11:27:58
 */
@Suppress("unused")
@SuppressLint("ShowToast")
object YToast {
    private var toast: Toast? = null
    private var yQueue: YQueue? = null

    /**
     * 设置队列显示时间 毫秒
     */
    @JvmStatic
    @Volatile
    var queueTime = 1000 //队列显示一条toast至少显示这么长时间

    @JvmStatic
    var showLog = false //是否显示log

    @JvmStatic
    var filter: ((String) -> String?)? = null

    /**
     * 获取历史记录
     *
     * @return
     */
    @JvmStatic
    var history: MutableList<String?> = ArrayList() //历史记录，倒序，最多1000条

    /**
     * 显示一条toast
     *
     * @param text 内容
     */
    @JvmStatic
    fun show(text: String?) {
        show(YApp.get(), text)
    }

    @JvmStatic
    fun show(text: String?, topClass: Int) {
        show(YApp.get(), text, topClass)
    }

    /**
     * 显示toast 并播放语音
     *
     * @param text 语音内容
     */
    @JvmStatic
    fun showSpeak(text: String?) {
        show(YApp.get(), text, 0)
        speak(text)
    }

    @JvmStatic
    fun showSpeak(text: String?, topClass: Int) {
        show(YApp.get(), text, topClass)
        speak(text)
    }

    /**
     * 显示toast 并播放语音
     *
     * @param text 语音内容
     */
    @JvmStatic
    fun showSpeakQueue(text: String?) {
        show(YApp.get(), text, 0)
        speakQueue(text)
    }

    @JvmStatic
    fun showSpeakQueue(text: String?, topClass: Int) {
        show(YApp.get(), text, topClass)
        speakQueue(text)
    }

    /**
     * 多条toast同时过来，只显示最后一条，显示时间为LENGTH_SHORT
     *
     * @param context context
     * @param text    内容
     */
    @JvmOverloads
    @JvmStatic
    fun show(context: Context?, text: String?, topClass: Int = 0) {
        if (context == null || text == null) return
        val value: String? = if (filter != null) filter?.invoke(text) else text
        if (showLog) YLog.i("YToast ", value, YStackTrace.getTopClassLine(1 + topClass))
        runOnUiThread {
            if (toast != null) {
                try {
                    toast!!.cancel()
                } catch (e: Exception) {
                }
                toast = null
            }
            toast = makeText(context, value, Toast.LENGTH_SHORT)
            try {
                toast?.show()
            } catch (e: Exception) {
            }
            history.add(0, value)
            if (history.size > 1000) history.removeAt(history.size - 1)
        }
    }

    /**
     * 显示一条toast
     *
     * @param text 内容
     */
    @JvmStatic
    fun showLong(text: String?) {
        showLong(YApp.get(), text)
    }

    @JvmStatic
    fun showLong(text: String?, topClass: Int) {
        showLong(YApp.get(), text, topClass)
    }

    /**
     * 显示toast 并播放语音
     *
     * @param text 语音内容
     */
    @JvmStatic
    fun showLongSpeak(text: String?) {
        show(YApp.get(), text)
        speak(text)
    }

    @JvmStatic
    fun showLongSpeak(text: String?, topClass: Int) {
        show(YApp.get(), text, topClass)
        speak(text)
    }

    /**
     * 多条toast同时过来，只显示最后一条，显示时间为LENGTH_LONG
     *
     * @param context context
     * @param text    内容
     */
    @JvmOverloads
    @JvmStatic
    fun showLong(context: Context?, text: String?, topClass: Int = 0) {
        if (context == null || text == null) return
        val value: String? = if (filter != null) filter?.invoke(text) else text
        if (showLog) YLog.i("YToast ", value, YStackTrace.getTopClassLine(1 + topClass))
        runOnUiThread {
            if (toast != null) {
                try {
                    toast!!.cancel()
                } catch (e: Exception) {
                }
                toast = null
            }
            toast = makeText(context, value, Toast.LENGTH_LONG)
            try {
                toast?.show()
            } catch (e: Exception) {
            }
            history.add(0, value)
            if (history.size > 1000) history.removeAt(history.size - 1)
        }
    }

    @JvmStatic
    fun showQueue(text: String?) {
        showQueue(YApp.get(), text)
    }

    /**
     * 多条toast同时过来，每一条toast至少显示queueTime时间（毫秒）
     *
     * @param context context
     * @param text    内容
     */
    @JvmStatic
    fun showQueue(context: Context?, text: String?) {
        if (context == null || text == null) return
        runOnUiThread {
            if (yQueue == null) yQueue = YQueue()
            yQueue!!.run(queueTime) { show(context, text) }
        }
    }

    @JvmStatic
    fun showQueueLong(text: String?) {
        showQueueLong(YApp.get(), text)
    }

    /**
     * 多条toast同时过来，每一条toast至少显示queueTime时间（毫秒）
     *
     * @param context context
     * @param text    内容
     */
    @SuppressLint("ShowToast")
    @JvmStatic
    fun showQueueLong(context: Context?, text: String?) {
        if (context == null || text == null) return
        runOnUiThread {
            if (yQueue == null) yQueue = YQueue()
            yQueue!!.run(queueTime) { showLong(context, text) }
        }
    }

    /**
     * toast至少显示queueTime时间 并播放语音
     *
     * @param text 语音内容
     */
    @JvmStatic
    fun showQueueLongSpeak(text: String?) {
        showQueueLong(YApp.get(), text)
        speak(text)
    }

    /**
     * toast至少显示queueTime时间 并播放语音队列
     *
     * @param text 语音内容
     */
    @JvmStatic
    fun showQueueLongSpeakQueue(text: String?) {
        showQueueLong(YApp.get(), text)
        speakQueue(text)
    }

    /**
    解决安卓7.x Toast内部抛出的token无效崩溃
     */
    @JvmStatic
    fun makeText(context: Context?, text: CharSequence?, duration: Int): Toast {
        // 先创建toast
        val toast = Toast.makeText(context, text, duration)
        // 仅7.0/7.1做反射hook
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N || Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            try {
                val tnField = Toast::class.java.getDeclaredField("mTN")
                tnField.isAccessible = true
                val tn = tnField.get(toast)
                val handlerField = tn.javaClass.getDeclaredField("mHandler")
                handlerField.isAccessible = true
                val originHandler = handlerField.get(tn) as Handler
                val safeHandler = object : Handler(originHandler.looper) {
                    override fun handleMessage(msg: Message) {
                        try {
                            originHandler.handleMessage(msg)
                        } catch (_: WindowManager.BadTokenException) {
                            // 吞掉7.x Toast内部抛出的token无效崩溃
                        }
                    }
                }
                handlerField.set(tn, safeHandler)
            } catch (_: Exception) {
                // 反射失败，直接降级使用原生Toast，不抛异常
            }
        }
        return toast
    }
}