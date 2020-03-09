package com.yujing.view;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

/**
 * 多功能播放器
 *
 * @author yujing 2019年4月29日13:56:39
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class YPlayer {
    private Activity activity;
    private TextureView textureView;//view
    private String url;//地址
    private MediaPlayer mediaPlayer;//播放器
    private boolean isInit;//是否已经初始化
    private boolean isStart;//是否已经播放
    private YPlayerInitListener yPlayerInitListener;//初始化完成监听
    private YPlayerStartListener yPlayerStartListener;//播放出图像TextureUpdated监听
    private Surface surface;
    private int screenStopTime;//超时时间
    private int screenStopRestartTime = 60;//屏幕无响应重启时间
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!thread.isInterrupted()) {
                try {
                    Thread.sleep(1000);
                    screenStopTime++;
                    //如果超过60秒无响应，自动重启视频流
                    if (screenStopTime >= screenStopRestartTime) {
                        rePlay();
                        screenStopTime = 0;
                    }
                } catch (InterruptedException e) {
                    thread.interrupt();
                }
            }
        }
    });

    public YPlayer(Activity activity, TextureView textureView, String url) {
        this.activity = activity;
        this.textureView = textureView;
        this.url = url;
        this.textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                //textureView初始化完成
                isInit = true;
                YPlayer.this.surface = new Surface(YPlayer.this.textureView.getSurfaceTexture());
                play();
                if (yPlayerInitListener != null)
                    yPlayerInitListener.init();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                //屏幕更新回调
                screenStopTime = 0;
                if (!isStart && yPlayerStartListener != null) {
                    isStart = true;
                    yPlayerStartListener.start();
                }
            }
        });
        thread.start();
    }

    public void rePlay(String url) {
        this.url = url;
        close();
        if (surface != null) {
            surface.release();
            surface = null;
        }
        YPlayer.this.surface = new Surface(YPlayer.this.textureView.getSurfaceTexture());
        play();
    }

    public void rePlay() {
        close();
        if (surface != null) {
            surface.release();
            surface = null;
        }
        YPlayer.this.surface = new Surface(YPlayer.this.textureView.getSurfaceTexture());
        play();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public Surface getSurface() {
        return surface;
    }

    public void setyPlayerInitListener(YPlayerInitListener yPlayerInitListener) {
        this.yPlayerInitListener = yPlayerInitListener;
    }

    public void setyPlayerStartListener(YPlayerStartListener yPlayerStartListener) {
        this.yPlayerStartListener = yPlayerStartListener;
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    /**
     * 播放视频的入口，当SurfaceTexture可得到时被调用
     */
    public synchronized void play() {
        if (!isInit) {
            Log.e("YPlayer", "SurfaceTexture No initialization completed");
            return;
        }
        if (activity == null || textureView == null || url == null) {
            return;
        }
        this.mediaPlayer = new MediaPlayer();
        try {
            Uri uri = Uri.parse(url);
            mediaPlayer.setDataSource(activity, uri);
            mediaPlayer.setSurface(surface);
            mediaPlayer.prepareAsync();
            mediaPlayer.setLooping(false);
            //准备好监听
            mediaPlayer.setOnPreparedListener(mp -> {
                try {
                    if (mp != null) {
                        mp.start(); //视频开始播放
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            });
            //播放完毕监听
            mediaPlayer.setOnCompletionListener(null);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public void onResume() {
        if (mediaPlayer == null) {
            play();
            screenStopTime = 0;
        }
    }

    public void onStop() {
        screenStopTime = Integer.MIN_VALUE;
        close();
    }

    public void finish() {
        onDestroy();
    }

    public void onDestroy() {
        thread.interrupt();
        close();
        if (surface != null) {
            surface.release();
            surface = null;
        }
    }

    public void close() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public interface YPlayerInitListener {
        void init();
    }

    public interface YPlayerStartListener {
        void start();
    }
}
