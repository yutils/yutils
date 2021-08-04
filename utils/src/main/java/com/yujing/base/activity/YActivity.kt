package com.yujing.base.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.yujing.base.contract.YLifeEvent
import com.yujing.base.contract.YLifeEventListener
import com.yujing.contract.YReturn2
import com.yujing.contract.YReturn3
import java.lang.Deprecated

/**
 * 监听activity事件
 * @author 余静 2021年8月4日16:31:55
 */
/*
用法：
val base = activity as YActivity
base.setEventListener { event, obj ->
    if (event == YLifeEvent.onDestroy) {
        YLog.d("关闭了")
    } else if (event == YLifeEvent.onActivityResult) {
        val result = obj as YReturn3<Int, Int, Intent>
    }
}
 */
abstract class YActivity : AppCompatActivity() {
    var yEventListeners: MutableList<YLifeEventListener> = ArrayList()

    fun setEventListener(yEventListener: YLifeEventListener) {
        this.yEventListeners.add(yEventListener)
    }

    fun removeEventListener(yEventListener: YLifeEventListener) {
        yEventListeners.remove(yEventListener)
    }

    fun clearEventListener() {
        yEventListeners.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for (item in yEventListeners) item.event(YLifeEvent.onCreate, null)
    }

    @Deprecated
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (item in yEventListeners) item.event(
            YLifeEvent.onActivityResult,
            YReturn3(requestCode, resultCode, data)
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        for (item in yEventListeners) item.event(YLifeEvent.onConfigurationChanged, newConfig)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (item in yEventListeners) item.event(
            YLifeEvent.onRequestPermissionsResult,
            YReturn3(requestCode, permissions, grantResults)
        )
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        for (item in yEventListeners) item.event(YLifeEvent.onNewIntent, null)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        //这儿返回值，不作判断，无效，因为可能多个地方调用onKeyDown，无法判断应返回内容
        for (item in yEventListeners) item.event(YLifeEvent.onKeyDown, YReturn2(keyCode, event))
        return super.onKeyDown(keyCode, event)
    }

    override fun onStart() {
        super.onStart()
        for (item in yEventListeners) item.event(YLifeEvent.onStart, null)
    }

    override fun onResume() {
        super.onResume()
        for (item in yEventListeners) item.event(YLifeEvent.onResume, null)
    }

    override fun onPause() {
        super.onPause()
        for (item in yEventListeners) item.event(YLifeEvent.onPause, null)
    }

    override fun onRestart() {
        super.onRestart()
        for (item in yEventListeners) item.event(YLifeEvent.onRestart, null)
    }

    override fun onStop() {
        super.onStop()
        for (item in yEventListeners) item.event(YLifeEvent.onStop, null)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        for (item in yEventListeners) item.event(YLifeEvent.onBackPressed, null)
    }

    override fun finish() {
        super.finish()
        for (item in yEventListeners) item.event(YLifeEvent.finish, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        for (item in yEventListeners) item.event(YLifeEvent.onDestroy, null)
        clearEventListener()
    }
}