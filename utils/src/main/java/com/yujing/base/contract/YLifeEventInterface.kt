package com.yujing.base.contract

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import com.yujing.contract.YReturn2
import com.yujing.contract.YReturn3

/**
 * activity基类
 * 实现监听activity事件
 */
/* 用法
abstract class YActivity : RxAppCompatActivity(), YActivityInterface {
override var yEventListener: YEventListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super<RxAppCompatActivity>.onCreate(savedInstanceState)
        super<YActivityInterface>.onCreate(savedInstanceState)
    }
    override fun onStop() {
        super<RxAppCompatActivity>.onStop()
        super<YActivityInterface>.onStop()
    }
    ...
}

//用的地方
val base = activity as YActivity
base.setEventListener { event, obj ->
    if (event == YLifeEvent.onDestroy) {
        YLog.d("关闭了")
    } else if (event == YLifeEvent.onActivityResult) {
        val result = obj as YReturn3<Int, Int, Intent>
    }
}
 */
interface YLifeEventInterface {
    //-------------------------------------public-------------------------------------
    var yEventListeners: MutableList<YLifeEventListener>

    fun setEventListener(yEventListener: YLifeEventListener?) {
        yEventListener?.let { this.yEventListeners.add(it) }
    }

    fun removeEventListener(yEventListener: YLifeEventListener?) {
        yEventListener?.let { yEventListeners.remove(yEventListener) }
    }

    fun clearEventListener() {
        yEventListeners.clear()
    }

    fun onCreate(savedInstanceState: Bundle?) {
        for (yEventListener in yEventListeners) yEventListener.event(YLifeEvent.onCreate, null)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        for (yEventListener in yEventListeners) yEventListener.event(
            YLifeEvent.onActivityResult,
            YReturn3(requestCode, resultCode, data)
        )
    }

    fun onConfigurationChanged(newConfig: Configuration) {
        for (yEventListener in yEventListeners) yEventListener.event(
            YLifeEvent.onConfigurationChanged,
            newConfig
        )
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        for (yEventListener in yEventListeners) yEventListener.event(
            YLifeEvent.onRequestPermissionsResult,
            YReturn3(requestCode, permissions, grantResults)
        )
    }

    fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        for (yEventListener in yEventListeners) yEventListener.event(
            YLifeEvent.onKeyDown,
            YReturn2(keyCode, event)
        )
        //这儿返回值，不作判断，无效，因为可能多个地方调用onKeyDown，无法判断应返回内容
        return true
    }

    fun onStart() {
        for (yEventListener in yEventListeners) yEventListener.event(YLifeEvent.onStart, null)
    }

    fun onResume() {
        for (yEventListener in yEventListeners) yEventListener.event(YLifeEvent.onResume, null)
    }

    fun onPause() {
        for (yEventListener in yEventListeners) yEventListener.event(YLifeEvent.onPause, null)
    }

    fun onRestart() {
        for (yEventListener in yEventListeners) yEventListener.event(YLifeEvent.onRestart, null)
    }

    fun onStop() {
        for (yEventListener in yEventListeners) yEventListener.event(YLifeEvent.onStop, null)
    }

    fun onDestroy() {
        for (yEventListener in yEventListeners) yEventListener.event(YLifeEvent.onDestroy, null)
    }

    //-------------------------------------activity-------------------------------------

    fun onNewIntent(intent: Intent?) {
        for (yEventListener in yEventListeners) yEventListener.event(YLifeEvent.onNewIntent, null)
    }

    fun onBackPressed() {
        for (yEventListener in yEventListeners) yEventListener.event(YLifeEvent.onBackPressed, null)
    }

    fun finish() {
        for (yEventListener in yEventListeners) yEventListener.event(YLifeEvent.finish, null)
    }
    //-------------------------------------fragment-------------------------------------

    fun onHiddenChanged(hidden: Boolean) {
        for (yEventListener in yEventListeners) yEventListener.event(
            YLifeEvent.onHiddenChanged,
            hidden
        )
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        for (yEventListener in yEventListeners) yEventListener.event(
            YLifeEvent.onViewCreated,
            YReturn2(view, savedInstanceState)
        )
    }

    fun onDestroyView() {
        for (yEventListener in yEventListeners) yEventListener.event(YLifeEvent.onDestroyView, null)
    }

    fun onDetach() {
        for (yEventListener in yEventListeners) yEventListener.event(YLifeEvent.onDetach, null)
    }

}