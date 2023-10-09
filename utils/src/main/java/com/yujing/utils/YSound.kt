package com.yujing.utils

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * SoundPool 快捷使用
 *
 * @author yujing 2021年9月3日17:20:12
 */
/*
用法
//添加资源
YSound.getInstance().put(0,R.raw.di)
//播放资源
YSound.getInstance().play(0)
//释放资源
YSound.getInstance().onDestroy()

//加载播放并释放资源
YSound.play(R.raw.success,1000)
*/

/*
原生用法
val audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
val soundPool = SoundPool.Builder().setMaxStreams(10).setAudioAttributes(audioAttributes).build()
val id = soundPool.load(YApp.get(), R.raw.success, 1)
//加载完成
soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
    if (id == sampleId) { }
}
//播放
var streamID = soundPool.play(id, 1.0f, 1.0f, 0, 0, 1f)
//停止
soundPool.stop(streamID)
*/
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class YSound(maxStreams: Int = 1) {
    companion object {
        @JvmStatic
        private var instance: YSound? = null

        @JvmStatic
        fun getInstance(maxStreams: Int = 1): YSound {
            if (instance == null) {
                synchronized(YSound::class.java) { instance = YSound(maxStreams) }
            }
            return instance!!
        }

        /**
         * 加载 --> 播放 --> timeOut后释放
         */
        @JvmStatic
        fun play(resources: Int, timeOut: Long, loop: Int = 0) {
            val audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
            val soundPool = SoundPool.Builder().setMaxStreams(1).setAudioAttributes(audioAttributes).build()
            val id = soundPool.load(YApp.get(), resources, 1)
            //加载完成
            soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
                if (id == sampleId) {
                    val streamID = soundPool.play(id, 1.0f, 1.0f, 0, loop, 1f)
                    //timeOut时间后释放soundPool
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(timeOut)
                        soundPool.stop(streamID)
                        soundPool.release()
                    }
                }
            }
        }
    }

    var soundPool: SoundPool? private set
    private var map: MutableMap<Int, Int> = HashMap()
    var oldStreamID = -1 //最后一次播放的id

    //音频属性
    var audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()

    init {
        //添加音效
        soundPool = SoundPool.Builder().setMaxStreams(maxStreams).setAudioAttributes(audioAttributes).build()
        map = HashMap()
    }

    /**
     * 添加一条声音
     *
     * @param id        id
     * @param resources 资源
     */
    fun put(id: Int, resources: Int, listener: (() -> Unit)? = null) {
        if (soundPool == null) return
        //priority 优先级
        map[id] = soundPool!!.load(YApp.get(), resources, 1)
        soundPool!!.setOnLoadCompleteListener { soundPool, sampleId, status ->
            if (map[id] == sampleId) listener?.invoke()
        }
    }

    /**
     * 播放其中一条声音，如果只有一条记录可以用这个
     */
    fun play() {
        for (item in map.keys) {
            play(item)
            break
        }
    }

    /**
     * 循环播放一段声音
     *
     * @param id   id
     * @param loop 循环次数
     */
    fun play(id: Int, loop: Int = 0): Int {
        val resources = map[id]
        if (soundPool == null || resources == null)
            return -1
        //每次播放都会产生一个streamID，第一次为1、第二次为2，停止播放调用 stop(streamID)
        val streamID = soundPool!!.play(resources, 1.0f, 1.0f, 0, loop, 1f)
        oldStreamID = streamID
        return streamID
    }

    /**
     * 停止播放一段声音
     *
     * @param streamID
     */
    fun stop(streamID: Int) {
        if (soundPool == null) return
        soundPool!!.stop(streamID)
    }

    /**
     * 停止全部播放的声音
     */
    fun stopAll() {
        if (soundPool == null) return
        for (i in oldStreamID downTo 1) {
            soundPool!!.stop(i)
        }
    }

    /**
     * 释放资源
     */
    fun onDestroy() {
        if (soundPool != null) soundPool!!.release()
        soundPool = null
        map = HashMap()
        instance = null
    }

    fun getMap(): Map<Int, Int> {
        return map
    }
}