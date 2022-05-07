package com.yujing.utils

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

/**
 * TTS 语音
 * @author yujing 2022年4月24日17:31:12
 */
/*
用法：

//设置过滤器
TTS.filter={ it.replace("皮重", "皮众") }
//播放语音
TTS.speak("皮重不是在范围内1")

//关闭，释放资源
TTS.destroy()
 */
object TTS {
    private const val TAG = "TTS"

    @Volatile
    var initState: Int = -1 //初始化状态,-1未初始化，0完成，1语言包丢失，2语音不支持
        private set
    var textToSpeech: TextToSpeech? = null
        private set
    var speechRate = 1.0f //速度
    var pitch = 1.0f //音调
    var filter: ((String) -> String?)? = null
    var SHOW_LOG = true //是否显示log
    var history = mutableListOf<String>()//历史记录，倒序，最多1000条

    //是否是第一次播放语音
    private var isFirst = true

    /**
     * 初始化
     * @param context 上下文
     * @param initListener 初始化监听
     */
    @Synchronized
    @JvmStatic
    fun init(context: Context?, initListener: ((Boolean) -> Unit?)? = null) {
        if (context == null || initState == 0 || textToSpeech != null) return
        textToSpeech = TextToSpeech(context) { status: Int ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech!!.setLanguage(Locale.CHINA)
                initState = when (result) {
                    TextToSpeech.LANG_MISSING_DATA -> {
                        Log.e(TAG, "TTS初始化失败，语言包丢失")
                        1
                    }
                    TextToSpeech.LANG_NOT_SUPPORTED -> {
                        Log.e(TAG, "TTS初始化失败，语音不支持")
                        2
                    }
                    else -> {
                        Log.i(TAG, "TTS初始化成功")
                        0
                    }
                }
            } else {
                Log.e(TAG, "TTS初始化失败:$status")
                initState = 3
            }
            initListener?.invoke(initState == 0)
        }
    }

    /**
     * 播放语音并显示Toast
     */
    @JvmStatic
    fun speakToast(str: String?) {
        speak(str)
        YToast.show(str, 1)
    }

    /**
     * 语音播放
     *
     * @param str 语音播放文字内容
     */
    @JvmStatic
    fun speak(str: String?) {
        if (initState == -1) return init(YApp.get()) { if (it) speak(str) }
        if (initState != 0 || str == null || textToSpeech == null) return
        val speak = filter?.invoke(str) ?: str
        textToSpeech?.setSpeechRate(speechRate) //速度
        textToSpeech?.setPitch(pitch) // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        if (Build.VERSION.SDK_INT >= 21) {
            textToSpeech?.speak(speak, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            textToSpeech?.speak(speak, TextToSpeech.QUEUE_FLUSH, null)
        }
        if (SHOW_LOG) YLog.i(TAG, "TTS: $speak", if (isFirst) 0 else YStackTrace.getTopClassLine(1))
        isFirst = false //是否是第一次播放语音，因为第一次播放语音时，日志获取不到调用类，因为上面有个递归，递归又在初始化回调里面，所以第一次播放不偏移行
        history.add(0, speak)
        if (history.size > 1000) history.removeAt(history.size - 1)
    }

    /**
     * 播放语音并显示Toast
     */
    @JvmStatic
    fun speakQueueToast(str: String?) {
        speakQueue(str)
        YToast.show(str, 1)
    }

    /**
     * 语音队列播放
     *
     * @param speak 语音播放文字内容
     */
    @JvmStatic
    fun speakQueue(str: String?) {
        if (initState == -1) return init(YApp.get()) { if (it) speakQueue(str) }
        if (initState != 0 || str == null || textToSpeech == null) return
        val speak = filter?.invoke(str) ?: str
        textToSpeech?.setSpeechRate(speechRate) //速度
        textToSpeech?.setPitch(pitch) // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        if (Build.VERSION.SDK_INT >= 21) {
            textToSpeech?.speak(speak, TextToSpeech.QUEUE_ADD, null, null)
        } else {
            textToSpeech?.speak(speak, TextToSpeech.QUEUE_ADD, null)
        }
        if (SHOW_LOG) YLog.i(TAG, "TTS: $speak", if (isFirst) 0 else YStackTrace.getTopClassLine(1))
        isFirst = false //是否是第一次播放语音，因为第一次播放语音时，日志获取不到调用类，因为上面有个递归，递归又在初始化回调里面，所以第一次播放不偏移行
        history.add(0, speak)
        if (history.size > 1000) history.removeAt(history.size - 1)
    }

    /**
     * 停止,TTS都被打断，包含队列
     */
    @JvmStatic
    fun onStop() {
        textToSpeech?.let { if (it.isSpeaking) it.stop() }
    }

    /**
     * 关闭，释放资源
     */
    @JvmStatic
    fun destroy() {
        textToSpeech?.shutdown() // 关闭，释放资源
        textToSpeech = null
        initState = -1
    }
}