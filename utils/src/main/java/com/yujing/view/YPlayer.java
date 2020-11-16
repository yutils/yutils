package com.yujing.view;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

/**
 * 多功能播放器
 *
 * @author yujing 2020年11月13日10:57:38
 * <p>
 * SurfaceView 一但到后台，会立即调用: surfaceDestroyed()
 * SurfaceView 回到前台，会立即调用: surfaceCreated()
 * <p>
 * TextureView前后台，不会释放surface
 */
/*
用法：
YPlayer yPlayer = new YPlayer(this, bind.textureView, url);
yPlayer.setScreenStopTimeLimit(60);//屏幕卡死60秒无响应自动重启（建议播放流的时候设置）
yPlayer.setAutoRestartTimeLimit(60 * 3);//每三分钟自动重启播放（建议播放流的时候设置）

@Override
protected void onResume() {
    super.onResume();
    yPlayer.onResume();
}

@Override
protected void onStop() {
    super.onStop();
    yPlayer.onStop();
}

@Override
protected void onDestroy() {
    super.onDestroy();
    yPlayer.onDestroy();
}
 */
public class YPlayer {
    private final Activity activity;
    private TextureView textureView;//textureView
    private SurfaceView surfaceView;//surfaceView
    private boolean active = true;//页面是否是活跃的
    private String url;//地址
    private MediaPlayer mediaPlayer;//播放器
    private boolean isInit;//是否已经初始化
    private boolean isStart;//是否已经播放
    private YPlayerInitListener yPlayerInitListener;//初始化完成监听
    private YPlayerStartListener yPlayerStartListener;//播放出图像TextureUpdated监听
    private Surface surface;
    private int screenStopTime = 0;//当前已经有多少秒屏幕没有响应
    private int screenStopTimeLimit = -1;//屏幕无响应重启时间,单位：秒,如果<0,则不重启播放
    private int autoRestartTime = 0;//自动重启时间
    private int autoRestartTimeLimit = -1;//自动重启时间,单位：秒,如果<0,则不重启播放

    public YPlayer(Activity activity, SurfaceView surfaceView, String url) {
        this.activity = activity;
        this.surfaceView = surfaceView;
        this.url = url;

        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            //程序到前台自动调用
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                isInit = true;
                YPlayer.this.surface = holder.getSurface();
                play();
                if (yPlayerInitListener != null)
                    yPlayerInitListener.init();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            //程序到后台自动调用
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        thread.start();
    }

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

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!thread.isInterrupted()) {
                try {
                    Thread.sleep(1000);
                    if (activity.isFinishing()) break;
                    if (!active) continue;
                    //如果超过screenStopTimeLimit秒无响应，自动重启视频流
                    if (textureView != null && screenStopTimeLimit > 0) {
                        screenStopTime++;
                        if (screenStopTime >= screenStopTimeLimit) {
                            Log.e("YPlayer", "屏幕无响应时间大于" + screenStopTimeLimit + "秒，自动重启播放");
                            screenStopTime = 0;
                            rePlay();
                        }
                    }
                    //每过autoRestartTimeLimit秒，自动重启视频流
                    if (autoRestartTimeLimit > 0) {
                        autoRestartTime++;
                        if (autoRestartTime >= autoRestartTimeLimit) {
                            Log.i("YPlayer", "稳定运行" + autoRestartTimeLimit + "秒，自动重启播放");
                            autoRestartTime = 0;
                            rePlay();
                        }
                    }
                } catch (InterruptedException e) {
                    thread.interrupt();
                }
            }
        }
    });


    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public Surface getSurface() {
        return surface;
    }

    public void setScreenStopTimeLimit(int screenStopTimeLimit) {
        this.screenStopTimeLimit = screenStopTimeLimit;
    }

    public void setyPlayerInitListener(YPlayerInitListener yPlayerInitListener) {
        this.yPlayerInitListener = yPlayerInitListener;
    }

    public void setyPlayerStartListener(YPlayerStartListener yPlayerStartListener) {
        this.yPlayerStartListener = yPlayerStartListener;
    }

    public int getAutoRestartTimeLimit() {
        return autoRestartTimeLimit;
    }

    public void setAutoRestartTimeLimit(int autoRestartTimeLimit) {
        this.autoRestartTimeLimit = autoRestartTimeLimit;
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }


    /**
     * 播放视频的入口，当SurfaceTexture可得到时被调用
     */
    public synchronized void play() {
        if (!isInit) {
            Log.e("YPlayer", "SurfaceTexture或者surfaceView未完成初始化");
            return;
        }
        this.mediaPlayer = new MediaPlayer();
        try {
            Uri uri = Uri.parse(url);
            mediaPlayer.setDataSource(activity, uri);
            mediaPlayer.setSurface(surface);
            mediaPlayer.setLooping(false);
            mediaPlayer.prepareAsync();
            //准备好监听
            mediaPlayer.setOnPreparedListener(mp -> {
                if (mp != null) mp.start(); //视频开始播放
            });
            //播放完毕监听
            mediaPlayer.setOnCompletionListener(null);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public void rePlay(String url) {
        if (url != null) this.url = url;
        close();
        if (textureView != null) {
            if (surface != null) {
                surface.release();
                surface = null;
            }
            surface = new Surface(textureView.getSurfaceTexture());
        } else if (surfaceView != null) {
            surface = surfaceView.getHolder().getSurface();
        }
        play();
    }

    public void rePlay() {
        rePlay(null);
    }

    public void onResume() {
        active = true;
        screenStopTime = 0;
        autoRestartTime = 0;
        //textureView 回到到页面需要手动重新播放
        if (mediaPlayer == null && isInit && textureView != null) {
            play();
        }
    }

    public void onStop() {
        active = false;
        close();
    }

    public void finish() {
        close();
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
            mediaPlayer.stop();
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
