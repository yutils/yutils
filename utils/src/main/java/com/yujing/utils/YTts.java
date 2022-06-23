package com.yujing.utils;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.yujing.contract.YListener1;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * TTS语音合成
 *
 * @author 余静 2019年12月5日09:21:06
 * <p>
 * 过时类  用TTS类代替
 */
/*
用法
    YTts.play("你好")
    YTts.playQueue("你好啊")
    //设置音调
    YTts.getInstance().pitch=1.0f
    //设置播放速度
    YTts.getInstance().speechRate=1.0f
 */
@Deprecated
public class YTts {
    private static String TAG = "YTts";
    public static boolean SHOW_LOG = true;//是否显示log
    private TextToSpeech textToSpeech; // TTS对象
    private float speechRate = 1.0f;//速度
    private float pitch = 1.0f;//音调
    private int initState = -1;//初始化状态,-1未初始化，0完成，1语言包丢失，2语音不支持
    private FilterListener filter;//过滤器
    private List<String> history = new ArrayList<>();//历史记录，倒序，最多1000条
    //----------------------------------------------静态----------------------------------------------
    private static volatile YTts yTts;//单例

    public static YTts getInstance() {
        if (yTts == null || yTts.textToSpeech == null) {
            synchronized (YTts.class) {
                if (yTts == null || yTts.textToSpeech == null) yTts = new YTts(YApp.get());
            }
        }
        return yTts;
    }

    public static YTts play(String speak) {
        return getInstance().speak(speak);
    }

    public static YTts playQueue(String speak) {
        return getInstance().speakQueue(speak);
    }

    /**
     * 关闭，释放资源
     */
    public static void destroy() {
        if (yTts != null) yTts.onDestroy();
    }
    //----------------------------------------------静态----------------------------------------------

    Context context;

    public YTts(final Context context) {
        this.context = context;
    }

    private synchronized void init(YListener1<Boolean> initListener) {
        if (initState == 0 || context == null || textToSpeech != null) return;
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.CHINA);
                if (result == TextToSpeech.LANG_MISSING_DATA) {
                    Log.e(TAG, "TTS初始化失败，语言包丢失");
                    initState = 1;
                } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "TTS初始化失败，语音不支持");
                    initState = 2;
                } else {
                    Log.i(TAG, "TTS初始化成功");
                    initState = 0;
                }
            } else {
                Log.e(TAG, "TTS初始化失败:" + status);
                initState = 3;
            }
            if (initListener != null) initListener.value(initState == 0);
        });
    }

    /**
     * 获取初始化状态
     *
     * @return 是否初始化成功
     */
    public boolean getInitState() {
        return initState == 0;
    }

    /**
     * 获取textToSpeech对象
     *
     * @return textToSpeech
     */
    public TextToSpeech getTextToSpeech() {
        return textToSpeech;
    }

    /**
     * 获取播放速度
     *
     * @return 速度
     */
    public float getSpeechRate() {
        return speechRate;
    }

    /**
     * 设置播放速度
     *
     * @param speechRate 速度
     */
    public YTts setSpeechRate(float speechRate) {
        this.speechRate = speechRate;
        return this;
    }

    /**
     * 获取音调
     *
     * @return 音调
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * 设置音调
     *
     * @param pitch 音调
     */
    public YTts setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }


    /**
     * 设置速度和音调
     *
     * @param speechRate 速度
     * @param pitch      音调
     */
    public void setSpeechRatePitch(float speechRate, float pitch) {
        setSpeechRate(speechRate);
        setPitch(pitch);
    }

    /**
     * 语音播放
     *
     * @param speak      语音播放文字内容
     * @param speechRate 速度
     * @param pitch      音调
     */
    public YTts speak(String speak, float speechRate, float pitch) {
        setSpeechRate(speechRate);
        setPitch(pitch);
        speak(speak);
        return this;
    }

    /**
     * 语音播放 并显示Toast
     *
     * @param speak 语音播放文字内容
     * @return YTts
     */
    public YTts speakToast(String speak) {
        speak(speak);
        YToast.show(speak, 1);
        return this;
    }

    /**
     * 语音播放
     *
     * @param speak 语音播放文字内容
     */
    public YTts speak(String speak) {
        if (initState == -1) {
            String finalSpeak = speak;
            if (context == null) context = YApp.get();
            init(aBoolean -> {
                if (aBoolean) speak(finalSpeak);
            });
            return this;
        }
        if (initState != 0 || speak == null || speak.isEmpty() || textToSpeech == null) return this;
        if (filter != null) speak = filter.filter(speak);
        if (speak == null || speak.isEmpty()) return this;
                textToSpeech.setSpeechRate(speechRate);//速度
        textToSpeech.setPitch(pitch);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        if (Build.VERSION.SDK_INT >= 21) {
            textToSpeech.speak(speak, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(speak, TextToSpeech.QUEUE_FLUSH, null);
        }
        if (SHOW_LOG) YLog.i(TAG, " \nTTS: " + speak, YStackTrace.getTopClassLine(1));
        history.add(0, speak);
        if (history.size() > 1000) history.remove(history.size() - 1);
        return this;
    }

    /**
     * 语音播放 并显示Toast
     *
     * @param speak 语音播放文字内容
     * @return YTts
     */
    public YTts speakQueueToast(String speak) {
        speakQueue(speak);
        YToast.show(speak, 1);
        return this;
    }

    /**
     * 语音队列播放
     *
     * @param speak      语音播放文字内容
     * @param speechRate 速度
     * @param pitch      音调
     */
    public YTts speakQueue(String speak, float speechRate, float pitch) {
        setSpeechRate(speechRate);
        setPitch(pitch);
        speakQueue(speak);
        return this;
    }

    /**
     * 语音队列播放
     *
     * @param speak 语音播放文字内容
     */
    public YTts speakQueue(String speak) {
        if (initState == -1) {
            String finalSpeak = speak;
            if (context == null) context = YApp.get();
            init(aBoolean -> {
                if (aBoolean) speakQueue(finalSpeak);
            });
            return this;
        }
        if (initState != 0 || speak == null || speak.isEmpty()|| textToSpeech == null) return this;
        if (filter != null) speak = filter.filter(speak);
        if (speak == null || speak.isEmpty()) return this;
        textToSpeech.setSpeechRate(speechRate);//速度
        textToSpeech.setPitch(pitch);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        if (Build.VERSION.SDK_INT >= 21) {
            textToSpeech.speak(speak, TextToSpeech.QUEUE_ADD, null, null);
        } else {
            textToSpeech.speak(speak, TextToSpeech.QUEUE_ADD, null);
        }
        if (SHOW_LOG) Log.i(TAG, " \nTTS: " + speak);
        history.add(0, speak);
        if (history.size() > 1000) history.remove(history.size() - 1);
        return this;
    }

    /**
     * 停止,TTS都被打断，包含队列
     */
    public void onStop() {
        if (textToSpeech != null && textToSpeech.isSpeaking()) textToSpeech.stop();
    }

    /**
     * 关闭，释放资源
     */
    public void onDestroy() {
        if (textToSpeech != null) textToSpeech.shutdown(); // 关闭，释放资源
        textToSpeech = null;
        initState = -1;
    }

    public FilterListener getFilter() {
        return filter;
    }

    /**
     * 语音过滤
     *
     * @param filter 过滤监听
     */
    public YTts setFilter(FilterListener filter) {
        this.filter = filter;
        return this;
    }

    /**
     * 获取语音播放历史
     */
    public List<String> getHistory() {
        return history;
    }

    /**
     * 语音播放过滤
     */
    public interface FilterListener {
        /**
         * 过滤
         *
         * @param content 内容
         * @return 过滤后的内容
         */
        String filter(String content);
    }
}
