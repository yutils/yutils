package com.yujing.utils

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import com.yujing.utils.YWebView

/**
 * YWeb，对webView二次封装，实现播放视频等操作
 * @author yujing 2020年11月24日14:57:04
 */
/*
用法
lateinit var yWeb: YWeb
override fun init() {
    val url = "http://www.nangua5.com/"
    yWeb = YWeb(this, binding.webView, binding.flVideoContainer)
    yWeb.loadUrl(url)
}

override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    yWeb.onConfigurationChanged(newConfig)
}

override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    return yWeb.onKeyDown(keyCode, event)
}

override fun onDestroy() {
    yWeb.onDestroy()
    super.onDestroy()
}
 */
class YWeb(var activity: Activity, var webView: WebView, var frameLayout: FrameLayout) {
    init {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
    }

    /**
     * 加载url地址
     */
    fun loadUrl(url: String) {
        YWebView.init(webView, url)
        //播放可以横屏旋转
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowCustomView(
                view: View?,
                callback: CustomViewCallback?
            ) {
                fullScreen()
                webView.visibility = View.GONE
                frameLayout.visibility = View.VISIBLE
                frameLayout.addView(view)
                super.onShowCustomView(view, callback)
            }

            override fun onHideCustomView() {
                fullScreen()
                webView.visibility = View.VISIBLE
                frameLayout.visibility = View.GONE
                frameLayout.removeAllViews()
                super.onHideCustomView()
            }
        }
    }

    /**
     *屏幕旋转监听
     */
    fun onConfigurationChanged(config: Configuration) {
        when (config.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)//全屏
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)//非全屏
            }
        }
    }

    /**
     * 全屏切换
     */
    private fun fullScreen() {
        activity.requestedOrientation =
            if (activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE//竖屏
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT//横屏
            }
    }

    /**
     * 返回
     */
    fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack() //返回上个页面
            }
            return true
        }
        return activity.onKeyDown(keyCode, event)
    }

    /**
     * 退出
     */
    fun onDestroy() {
        webView.destroy()
    }
}