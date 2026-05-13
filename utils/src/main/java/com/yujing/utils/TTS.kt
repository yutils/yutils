@file:Suppress("DEPRECATION")
package com.yujing.utils

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

private const val SG_TTS_PACKAGE = "org.nobody.sgtts"

/**
 * 默认 TTS 引擎包名尝试顺序（[init] 未传入非空的 `enginePackages` 且 [TTS.enginePackageOrder] 为 null 时使用）。
 * 靠前的优先；某一引擎初始化并设置语言成功后即停止。
 * 若列表中的引擎均不可用且 [TTS.fallbackToSystemDefaultEngine] 为 true，会再选用系统设置的「默认朗读引擎」。
 * （API 21+ 使用 [TextToSpeech] 第三参：`null`；API 21 以下无法按包名选择，仅能使用旧版构造函数即系统默认。）
 */
val DEFAULT_TTS_ENGINE_PACKAGES: List<String> = listOf()

/**
 * TTS 语音
 * @author yujing 2022年4月24日17:31:12
 */
/*
用法：

//播放语音
TTS.speak("你是张三吗？")
//语音队列
TTS.speakQueue("是的，你是谁？")


//速度
TTS.speechRate=1.1F
//音调
TTS.pitch=1.1F
//任意位置可以设置过滤器
TTS.filter={ it.replace("张三", "李四") }

//退出时关闭，释放资源
TTS.destroy()

//自定义引擎顺序（可选；init 传入 enginePackages 时优先生效；需 API 21+）
TTS.enginePackageOrder = listOf(
    "com.iflytek.speechsuite",           // 讯飞 64 位
    "com.iflytek.speechcloud",          // 讯飞 32 位
    "com.xiaomi.mibrain.speech",        // 小米 TTS
    "org.nobody.sgtts",                 // 搜狗 TTS（成功时自动将 speechRate 设为 2.5）
    "com.hikvision.hikttsservice",      // 海康威视
    "com.baidu.duersdk.opensdk",        // 度秘语音
    "com.vivo.aiservice",               // vivo
)
TTS.init(context)

//或单次指定顺序
TTS.init(context, listOf(
    "com.iflytek.speechsuite",           // 讯飞 64 位
    "com.iflytek.speechcloud",          // 讯飞 32 位
    "com.xiaomi.mibrain.speech",        // 小米 TTS
    "org.nobody.sgtts",                 // 搜狗 TTS（成功时自动将 speechRate 设为 2.5）
    "com.hikvision.hikttsservice",      // 海康威视
    "com.baidu.duersdk.opensdk",        // 度秘语音
    "com.vivo.aiservice",               // vivo
)) { ok -> }

//列表里没有该机型引擎：写明包名或依赖「系统默认」（列表全失败后自动尝试，API 21+）
TTS.fallbackToSystemDefaultEngine = true
TTS.init(context)
//只听系统默认（API 21+）：不先试列表
TTS.fallbackToSystemDefaultEngine = true
TTS.enginePackageOrder = emptyList()
TTS.init(context)


必须 AndroidManifest.xml添加
<queries>
    <!-- 允许查询所有 TTS 服务 -->
    <!-- 讯飞64位 -->
    <package android:name="com.iflytek.speechsuite" />
    <!-- 讯飞32位 -->
    <package android:name="com.iflytek.speechcloud" />
    <!-- 小米TTS -->
    <package android:name="com.xiaomi.mibrain.speech" />
    <!-- 搜狗TTS -->
    <package android:name="org.nobody.sgtts" />
    <!-- 海康威视 -->
    <package android:name="com.hikvision.hikttsservice" />
    <!-- 度秘语音 -->
    <package android:name="com.baidu.duersdk.opensdk" />
    <!-- 谷歌TTS -->
    <package android:name="com.google.android.tts" />
    <!-- vivo -->
    <package android:name="com.vivo.aiservice" />
</queries>
 */
object TTS {
    private const val TAG = "TTS"

    @Volatile
    var initState: Int = -1 //初始化状态,-1未初始化，0完成，1语言包丢失，2语音不支持
        private set
    var textToSpeech: TextToSpeech? = null
        private set
    @JvmStatic
    var speechRate = 1.0f //速度
    @JvmStatic
    var pitch = 1.0f //音调

    @JvmStatic
    var filter: ((String) -> String?)? = null
    @JvmStatic
    var showLog = false //是否显示log
    @JvmStatic
    var history = mutableListOf<String>()//历史记录，倒序，最多1000条

    //是否是第一次播放语音
    private var isFirst = true

    /**
     * 自定义引擎包名顺序。
     * [init] 的 `enginePackages` 非 null 时仅用传入列表；否则用本属性；本属性仍为 null 时用 [DEFAULT_TTS_ENGINE_PACKAGES]。
     * （API 21 以下仅能使用系统默认引擎，不按包名字段尝试多引擎。）
     */
    @Volatile
    var enginePackageOrder: List<String>? = null

    /**
     * 白名单中引擎全部失败（或未安装）时，是否最后用系统设置的默认 TTS。（API 21+ 传入 `engine = null`）；API 21 以下始终为单次系统默认构造函数。
     */
    @Volatile
    var fallbackToSystemDefaultEngine: Boolean = true

    /**
     * 初始化：按引擎列表依次尝试，直到某一引擎成功且中文可用为止。（指定引擎序列需 API 21+）
     *
     * @param context 上下文
     * @param enginePackages 引擎包名顺序；null 则使用 [enginePackageOrder]，再默认为 [DEFAULT_TTS_ENGINE_PACKAGES]
     * @param initListener 初始化监听
     */
    @JvmOverloads
    @Synchronized
    @JvmStatic
    fun init(
        context: Context?,
        enginePackages: List<String>? = null,
        initListener: ((Boolean) -> Unit)? = null,
    ) {
        if (context == null || initState == 0 || textToSpeech != null) return
        val appContext = context.applicationContext
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            initLegacySystemDefault(appContext, initListener)
            return
        }
        val packages = (
            enginePackages
                ?: enginePackageOrder
                ?: DEFAULT_TTS_ENGINE_PACKAGES
        ).map { it.trim() }.filter { it.isNotEmpty() }
        tryInitEngine(appContext, packages, 0, initListener)
    }

    /** @see init */
    @JvmStatic
    fun init(context: Context?, initListener: ((Boolean) -> Unit)?) {
        init(context, enginePackages = null, initListener = initListener)
    }

    /** API 21 以下：无法用包名绑定引擎，仅单次系统默认 [TextToSpeech]。 */
    private fun initLegacySystemDefault(context: Context, initListener: ((Boolean) -> Unit)?) {
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

    private fun initAllEnginesFailed(initListener: ((Boolean) -> Unit)?) {
        Log.e(TAG, "TTS初始化失败，已尝试全部候选引擎")
        initState = 3
        initListener?.invoke(false)
    }

    /** @param enginePkg 若为 null，使用系统设置的默认朗读引擎。 */
    private fun bindOneEngine(
        context: Context,
        enginePkg: String?,
        initListener: ((Boolean) -> Unit)?,
        onFailTryNext: () -> Unit,
    ) {
        val logTag = enginePkg ?: "(系统默认引擎)"
        textToSpeech = TextToSpeech(context, { status: Int ->
            val tts = textToSpeech
            if (tts == null) {
                initState = -1
                initListener?.invoke(false)
                return@TextToSpeech
            }
            if (status == TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(Locale.CHINA)
                initState = when (result) {
                    TextToSpeech.LANG_MISSING_DATA -> {
                        Log.e(TAG, "TTS初始化失败[$logTag]，语言包丢失")
                        1
                    }
                    TextToSpeech.LANG_NOT_SUPPORTED -> {
                        Log.e(TAG, "TTS初始化失败[$logTag]，语音不支持")
                        2
                    }
                    else -> {
                        Log.i(TAG, "TTS初始化成功: $logTag")
                        if (enginePkg == SG_TTS_PACKAGE) {
                            speechRate = 2.5f
                        }
                        0
                    }
                }
                if (initState == 0) {
                    initListener?.invoke(true)
                } else {
                    tts.shutdown()
                    textToSpeech = null
                    initState = -1
                    onFailTryNext()
                }
            } else {
                Log.e(TAG, "TTS初始化失败[$logTag]:$status")
                tts.shutdown()
                textToSpeech = null
                initState = -1
                onFailTryNext()
            }
        }, enginePkg)
    }

    private fun tryInitEngine(
        context: Context,
        packages: List<String>,
        index: Int,
        initListener: ((Boolean) -> Unit)?,
    ) {
        if (index < packages.size) {
            bindOneEngine(context, packages[index], initListener) {
                tryInitEngine(context, packages, index + 1, initListener)
            }
            return
        }
        if (fallbackToSystemDefaultEngine) {
            bindOneEngine(context, null, initListener) {
                initAllEnginesFailed(initListener)
            }
        } else {
            initAllEnginesFailed(initListener)
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
    @Synchronized
    fun speak(str: String?) {
        if (initState == -1) return init(YApp.get()) { if (it) speak(str) }
        if (initState != 0 || str.isNullOrEmpty() || textToSpeech == null) return
        val speak: String? = if (filter != null) filter?.invoke(str) else str
        if (speak.isNullOrEmpty()) return
        textToSpeech?.setSpeechRate(speechRate) //速度
        textToSpeech?.setPitch(pitch) // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        if (Build.VERSION.SDK_INT >= 21) {
            textToSpeech?.speak(speak, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            textToSpeech?.speak(speak, TextToSpeech.QUEUE_FLUSH, null)
        }
        if (showLog) YLog.i(TAG, "$speak", if (isFirst) 0 else YStackTrace.getTopClassLine(1))
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


    //循环线程
    private var loopThread: Thread? = null

    /**
     * 循环播放语音，直到下一条，或者loopClose()
     */
    @Synchronized
    @JvmStatic
    fun loopSpeak(str: String?, intervalTime: Long) {
        loop(intervalTime) { speak(str) }
    }

    /**
     * 循环播放语音，直到下一条，或者loopClose()
     */
    @Synchronized
    @JvmStatic
    fun loopSpeakQueue(str: String?, intervalTime: Long) {
        loop(intervalTime) { speakQueue(str) }
    }

    @Synchronized
    @JvmStatic
    fun loop(intervalTime: Long, listener: () -> Unit) {
        loopThread?.interrupt()
        loopThread = Thread {
            while (!Thread.interrupted()) {
                try {
                    listener.invoke()
                    Thread.sleep(intervalTime)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    break
                } catch (e: Exception) {
                    e.printStackTrace()
                    continue
                }
            }
        }
        loopThread?.start()
    }

    @JvmStatic
    fun loopClose() {
        loopThread?.interrupt()
    }

    /**
     * 语音队列播放
     *
     * @param speak 语音播放文字内容
     */
    @JvmStatic
    @Synchronized
    fun speakQueue(str: String?) {
        if (initState == -1) return init(YApp.get()) { if (it) speakQueue(str) }
        if (initState != 0 || str.isNullOrEmpty() || textToSpeech == null) return
        val speak: String? = if (filter != null) filter?.invoke(str) else str
        if (speak.isNullOrEmpty()) return
        textToSpeech?.setSpeechRate(speechRate) //速度
        textToSpeech?.setPitch(pitch) // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        if (Build.VERSION.SDK_INT >= 21) {
            textToSpeech?.speak(speak, TextToSpeech.QUEUE_ADD, null, null)
        } else {
            textToSpeech?.speak(speak, TextToSpeech.QUEUE_ADD, null)
        }
        if (showLog) YLog.i(TAG, "$speak", if (isFirst) 0 else YStackTrace.getTopClassLine(1))
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