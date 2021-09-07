package com.yujing.utils;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Map;

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
*/
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class YSound {
    //对象的最大并发流数
    static int maxStreams = 5;
    private static YSound instance;

    private SoundPool mSoundPool;
    private Map<Integer, Integer> map;
    //音频属性
    AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();

    public static YSound getInstance() {
        if (instance == null) {
            synchronized (YSound.class) {
                instance = new YSound();
            }
        }
        return instance;
    }

    public YSound() {
        //添加音效
        mSoundPool = new SoundPool.Builder().setMaxStreams(maxStreams).setAudioAttributes(audioAttributes).build();
        map = new HashMap<>();
    }

    /**
     * 添加一条声音
     *
     * @param id        id
     * @param resources 资源
     */
    public void put(int id, int resources) {
        //priority 优先级
        map.put(id, mSoundPool.load(YApp.get(), resources, 1));
    }

    /**
     * 播放最后一次添加的声音
     */
    public void play() {
        for (Integer item : map.keySet()) {
            play(item);
            break;
        }
    }

    /**
     * 播放一条声音
     *
     * @param id id
     */
    public void play(int id) {
        Integer resources = map.get(id);
        if (mSoundPool != null && resources != null) {
            mSoundPool.play(resources, 1.0f, 1.0f, 0, 0, 1f);
        }
    }

    /**
     * 释放资源
     */
    public void onDestroy() {
        if (mSoundPool != null) mSoundPool.release();
        mSoundPool = null;
        instance = null;
    }
}
